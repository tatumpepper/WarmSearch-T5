$ErrorActionPreference = 'Continue'
$base = 'http://127.0.0.1:8091'
$outdir = 'D:\编译原理\失物招领\_tests\out'
$bodydir = 'D:\编译原理\失物招领\_tests\body'
$results = @()
$script:tokens = @{}

function Api($method, $path, $bodyfile, $token) {
    $cmdArgs = @('-s', '-X', $method, "$base$path", '-H', 'Content-Type: application/json; charset=utf-8', '-w', '__HTTP__%{http_code}')
    if ($token) { $cmdArgs += @('-H', "Authorization: Bearer $token") }
    if ($bodyfile) { $cmdArgs += @('--data-binary', "@$bodyfile") }
    $raw = (& curl.exe @cmdArgs 2>$null) -join "`n"
    $idx = $raw.LastIndexOf('__HTTP__')
    $http = ''
    $json = $raw
    $obj = $null
    if ($idx -ge 0) {
        $http = $raw.Substring($idx + 8).Trim()
        $json = $raw.Substring(0, $idx)
        try { $obj = $json | ConvertFrom-Json } catch { $obj = $null }
    }
    return @{ http = $http; json = $json; obj = $obj }
}

function Code($res) {
    if ($res.obj -and $res.obj.code) { return [int]$res.obj.code }
    return -1
}

function Rec($id, $desc, $expect, $res, $ok) {
    $actual = "HTTP=$($res.http) code=$(Code $res) msg=$($res.obj.msg)"
    $status = if ($ok) { 'PASS' } else { 'FAIL' }
    $script:results += [pscustomobject]@{
        ID = $id; Case = $desc; Expected = $expect; Actual = $actual; Status = $status; Raw = $res.json
    }
    Write-Host "$id  $status  $desc  -> $actual"
}

function WriteBody($name, $obj) {
    $p = Join-Path $bodydir $name
    [System.IO.File]::WriteAllText($p, ($obj | ConvertTo-Json -Compress), (New-Object System.Text.UTF8Encoding($false)))
    return $p
}

# ---------- 认证 ----------
$r = Api 'POST' '/student/login' (Join-Path $bodydir 'login_student.json') $null
Rec 'AUTH-01' 'student login 2023001' 'code=200 and token' $r ((Code $r) -eq 200 -and $r.obj.data.token)
$stu = $r.obj.data.token
$script:tokens.stu = $stu

$r = Api 'POST' '/user/login' (Join-Path $bodydir 'login_admin.json') $null
Rec 'AUTH-02' 'admin login' 'code=200' $r ((Code $r) -eq 200)
$admin = $r.obj.data.token
$script:tokens.admin = $admin

$r = Api 'POST' '/user/login' (Join-Path $bodydir 'login_security.json') $null
Rec 'AUTH-03' 'security login' 'code=200' $r ((Code $r) -eq 200)
$sec = $r.obj.data.token
$script:tokens.sec = $sec

$r = Api 'POST' '/student/login' (Join-Path $bodydir 'login_bad.json') $null
Rec 'AUTH-04' 'wrong password' 'code=1001' $r ((Code $r) -eq 1001)

$r = Api 'GET' '/user/info' $null $null
Rec 'AUTH-05' 'info without token' 'code=401' $r ((Code $r) -eq 401)

$r = Api 'GET' '/user/info' $null $stu
$roles = ''
if ($r.obj.data.roles) { $roles = ($r.obj.data.roles -join ',') }
Rec 'AUTH-06' 'student self info' 'code=200 roles=STUDENT' $r ((Code $r) -eq 200 -and $roles -match 'STUDENT')

# ---------- 注册 ----------
$r = Api 'POST' '/student/register' (Join-Path $bodydir 'register.json') $null
Rec 'REG-01' 'register new student' 'code=200 token' $r ((Code $r) -eq 200)
$r2 = Api 'POST' '/student/register' (Join-Path $bodydir 'register.json') $null
Rec 'REG-02' 'duplicate register' 'code=1003' $r2 ((Code $r2) -eq 1003)

# ---------- 分类 ----------
$r = Api 'GET' '/category' $null $null
$cnt = 0
if ($r.obj.data) { $cnt = @($r.obj.data).Count }
Rec 'CAT-01' 'category list' 'code=200 count>0' $r ((Code $r) -eq 200 -and $cnt -gt 0)

$r = Api 'POST' '/category' (Join-Path $bodydir 'category.json') $admin
Rec 'CAT-02' 'admin add category' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'POST' '/category' (Join-Path $bodydir 'category.json') $stu
Rec 'CAT-03' 'student add category forbidden' 'code=403' $r ((Code $r) -eq 403)

# ---------- 物品发布 ----------
$r = Api 'POST' '/goods' (Join-Path $bodydir 'goods_normal.json') $stu
Rec 'GOODS-01' 'publish normal goods' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'POST' '/goods' (Join-Path $bodydir 'goods_valuable.json') $stu
Rec 'GOODS-02' 'publish valuable goods' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/goods?limit=100' $null $null
$itemCnt = 0; $goodsId = 0; $publisherId = 0; $total = 0
if ($r.obj.data) {
    $total = $r.obj.data.total
    foreach ($it in @($r.obj.data.items)) {
        if ($it.imgUrl -eq '/upload/test1.jpg') { $goodsId = $it.id; $publisherId = $it.publisherId }
    }
    $itemCnt = @($r.obj.data.items).Count
}
Rec 'GOODS-03' 'goods list contains published item' 'code=200 goodsId>0 total>=1' $r ((Code $r) -eq 200 -and $goodsId -gt 0 -and $total -ge 1)

$kw = [uri]::EscapeDataString('黑色雨伞')
$r = Api 'GET' "/goods?keyword=$kw" $null $null
$kwCnt = 0
if ($r.obj.data) { $kwCnt = @($r.obj.data.items).Count }
Rec 'GOODS-06' 'goods keyword search' 'code=200 items>=1' $r ((Code $r) -eq 200 -and $kwCnt -ge 1)

$r = Api 'GET' "/goods/$goodsId" $null $null
$vc1 = 0
if ($r.obj.data.goods) { $vc1 = $r.obj.data.goods.viewCount }
$r2 = Api 'GET' "/goods/$goodsId" $null $null
$vc2 = 0; $dcount = 0
if ($r2.obj.data.goods) { $vc2 = $r2.obj.data.goods.viewCount; $dcount = @($r2.obj.data.details).Count }
Rec 'GOODS-04' 'goods detail view count increment' 'viewCount +1 and details array' $r2 ((Code $r2) -eq 200 -and $vc2 -eq ($vc1 + 1))

$r = Api 'GET' '/goods?isValuable=1' $null $null
$vCnt = 0
if ($r.obj.data) { $vCnt = @($r.obj.data.items).Count }
Rec 'GOODS-05' 'goods filter valuable' 'code=200 items>=1' $r ((Code $r) -eq 200 -and $vCnt -ge 1)

# ---------- 寻物 ----------
$r = Api 'POST' '/findinfo' (Join-Path $bodydir 'findinfo.json') $stu
Rec 'FIND-01' 'publish lost notice' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/findinfo' $null $null
$fTotal = 0
if ($r.obj.data) { $fTotal = $r.obj.data.total }
Rec 'FIND-02' 'findinfo page' 'code=200 total>=1' $r ((Code $r) -eq 200 -and $fTotal -ge 1)

# ---------- 信用 ----------
$r = Api 'GET' '/credit/my' $null $stu
$score0 = 0; $level0 = ''
if ($r.obj.data) { $score0 = $r.obj.data.score; $level0 = $r.obj.data.level }
Rec 'CREDIT-01' 'my credit initial' 'code=200 score=120 level=GOOD' $r ((Code $r) -eq 200 -and $score0 -eq 120 -and $level0 -eq 'GOOD')

$r = Api 'GET' '/credit/leaderboard?limit=10' $null $null
$lb = 0
if ($r.obj.data) { $lb = @($r.obj.data).Count }
Rec 'CREDIT-02' 'credit leaderboard' 'code=200 list>0' $r ((Code $r) -eq 200 -and $lb -gt 0)

# ---------- 认领 ----------
$claimBody = WriteBody 'claim.json' @{ goodsId = $goodsId; contact = '13800000001'; credentialType = 'STUDENT_CARD'; credentialImg = '/upload/card.jpg'; description = '伞柄刻有姓名缩写' }
$r = Api 'POST' '/claim' $claimBody $stu
Rec 'CLAIM-01' 'submit claim application' 'code=200' $r ((Code $r) -eq 200)

$r2 = Api 'POST' '/claim' $claimBody $stu
Rec 'CLAIM-02' 'duplicate claim rejected' 'code=1005' $r2 ((Code $r2) -eq 1005)

$r = Api 'GET' '/claim/my' $null $stu
$cTotal = 0; $claimId = 0
if ($r.obj.data) { $cTotal = $r.obj.data.total; if ($cTotal -gt 0) { $claimId = $r.obj.data.items[0].id } }
Rec 'CLAIM-03' 'my claims' 'code=200 total>=1' $r ((Code $r) -eq 200 -and $cTotal -ge 1)

$r = Api 'GET' '/claim/page?status=PENDING' $null $admin
$p1 = 0; $prio = 0
if ($r.obj.data) { $p1 = @($r.obj.data.items).Count; if ($p1 -gt 0) { $prio = $r.obj.data.items[0].priority } }
Rec 'CLAIM-04' 'claim audit page sorted by priority' 'code=200 items>=1 priority=120' $r ((Code $r) -eq 200 -and $p1 -ge 1 -and $prio -eq 120)

$auditBody = WriteBody 'audit_pass.json' @{ id = $claimId; pass = $true; remark = 'student card verified' }
$r = Api 'POST' "/claim/$claimId/audit" $auditBody $admin
Rec 'CLAIM-05' 'audit pass' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'POST' "/claim/$claimId/confirm" $null $admin
Rec 'CLAIM-06' 'confirm claim' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/attestation/page' $null $admin
$aTotal = 0
if ($r.obj.data) { $aTotal = $r.obj.data.total }
Rec 'CLAIM-07' 'attestation ledger' 'code=200 total>=1' $r ((Code $r) -eq 200 -and $aTotal -ge 1)

$r = Api 'GET' "/credit/$publisherId" $null $null
$scorePub = 0
if ($r.obj.data) { $scorePub = $r.obj.data.score }
Rec 'CREDIT-03' 'finder credit +10 after return' 'code=200 score=130' $r ((Code $r) -eq 200 -and $scorePub -eq 130)

# ---------- 贵重物品 ----------
$r = Api 'GET' '/valuables/page' $null $sec
$vItems = 0; $valId = 0; $vStatus = ''
if ($r.obj.data) {
    $vItems = @($r.obj.data.items).Count
    if ($vItems -gt 0) { $valId = $r.obj.data.items[0].id; $vStatus = $r.obj.data.items[0].status }
}
Rec 'SEC-01' 'valuables auto record on publish' 'code=200 items>=1 status=PENDING_AUDIT' $r ((Code $r) -eq 200 -and $vItems -ge 1 -and $vStatus -eq 'PENDING_AUDIT')

$r = Api 'POST' "/valuables/$valId/audit" $auditBody $sec
Rec 'SEC-02' 'security audit valuable' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' "/valuables/$valId" $null $sec
$st2 = ''
if ($r.obj.data) { $st2 = $r.obj.data.status }
Rec 'SEC-03' 'status IN_CUSTODY after audit' 'code=200 status=IN_CUSTODY' $r ((Code $r) -eq 200 -and $st2 -eq 'IN_CUSTODY')

$custBody = WriteBody 'custody.json' @{ id = $valId; custodyLocation = 'security office cabinet 1' }
$r = Api 'POST' "/valuables/$valId/custody" $custBody $sec
Rec 'SEC-04' 'register custody location' 'code=200' $r ((Code $r) -eq 200)

$vclaimBody = WriteBody 'vclaim.json' @{ valuablesId = $valId; claimerName = 'Wang Wu'; claimerContact = '13800000003'; claimerNumber = '2023002'; purchaseProofImg = '/upload/invoice.jpg'; idProofImg = '/upload/id.jpg'; remark = 'bought in 2025' }
$r = Api 'POST' '/valuables/claim' $vclaimBody $stu
Rec 'SEC-05' 'student claim valuable with proofs' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' "/valuables/$valId" $null $sec
$st3 = ''
if ($r.obj.data) { $st3 = $r.obj.data.status }
Rec 'SEC-06' 'status IN_VERIFICATION after claim' 'status=IN_VERIFICATION' $r ((Code $r) -eq 200 -and $st3 -eq 'IN_VERIFICATION')

$r = Api 'POST' "/valuables/$valId/verify" $auditBody $sec
Rec 'SEC-07' 'security verify pass' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' "/valuables/$valId/flows" $null $sec
$fCnt = 0
if ($r.obj.data) { $fCnt = @($r.obj.data).Count }
Rec 'SEC-08' 'valuables flow ledger' 'code=200 flows>=4' $r ((Code $r) -eq 200 -and $fCnt -ge 4)

$r = Api 'POST' "/valuables/$valId/audit" $auditBody $admin
Rec 'SEC-09' 'admin cannot audit valuables (403)' 'code=403' $r ((Code $r) -eq 403)

# ---------- 低信用禁止认领 ----------
$r = Api 'GET' '/suser/page?keyword=2023002' $null $admin
$uid3 = 0
if ($r.obj.data -and @($r.obj.data.items).Count -gt 0) { $uid3 = $r.obj.data.items[0].id }
Rec 'CREDIT-04' 'find user 2023002' 'code=200 id found' $r ((Code $r) -eq 200 -and $uid3 -gt 0)

$r = Api 'POST' "/credit/adjust?userId=$uid3&change=-30&reason=test-low-credit" $null $admin
Rec 'CREDIT-05' 'admin adjust credit -30' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' "/credit/$uid3" $null $null
$s3 = 0; $lv3 = ''
if ($r.obj.data) { $s3 = $r.obj.data.score; $lv3 = $r.obj.data.level }
Rec 'CREDIT-06' 'credit becomes 50 level POOR' 'score=50 level=POOR' $r ((Code $r) -eq 200 -and $s3 -eq 50 -and $lv3 -eq 'POOR')

$r = Api 'POST' '/student/login' (WriteBody 'login3.json' @{ username = '2023002'; password = '123456' }) $null
$t3 = $r.obj.data.token
Rec 'AUTH-07' 'student 2023002 login' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/goods?status=WAITING&limit=1' $null $null
$waitId = 0
if ($r.obj.data -and @($r.obj.data.items).Count -gt 0) { $waitId = $r.obj.data.items[0].id }
$fcBody = WriteBody 'claim_low.json' @{ goodsId = $waitId; contact = '13800000004'; description = 'low credit test' }
$r = Api 'POST' '/claim' $fcBody $t3
Rec 'CLAIM-08' 'low credit claim forbidden' 'code=1004' $r ((Code $r) -eq 1004)

# ---------- 其他 ----------
$r = Api 'GET' '/carousel?type=MINI' $null $null
Rec 'CAR-01' 'carousel list' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/suser/page' $null $admin
Rec 'USER-01' 'admin student page' 'code=200' $r ((Code $r) -eq 200)

$r = Api 'GET' '/suser/page' $null $stu
Rec 'USER-02' 'student cannot list users (403)' 'code=403' $r ((Code $r) -eq 403)

# ---------- 输出 ----------
$csv = Join-Path $outdir 'api-test-results.csv'
$script:results | Select-Object ID, Case, Expected, Actual, Status | Export-Csv -Path $csv -Encoding UTF8 -NoTypeInformation
$md = Join-Path $outdir 'api-test-results.md'
$lines = @('# API test results', '', '| ID | Case | Expected | Actual | Status |', '|---|---|---|---|---|')
foreach ($x in $script:results) { $lines += "| $($x.ID) | $($x.Case) | $($x.Expected) | $($x.Actual) | $($x.Status) |" }
$total = @($script:results).Count
$pass = @($script:results | Where-Object { $_.Status -eq 'PASS' }).Count
$lines += '', "TOTAL=$total PASS=$pass FAIL=$($total-$pass)"
[System.IO.File]::WriteAllLines($md, $lines, (New-Object System.Text.UTF8Encoding($false)))
Write-Host "TOTAL=$total PASS=$pass FAIL=$($total-$pass)"
