# API test results

| ID | Case | Expected | Actual | Status |
|---|---|---|---|---|
| AUTH-01 | student login 2023001 | code=200 and token | HTTP=200 code=200 msg=操作成功 | PASS |
| AUTH-02 | admin login | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| AUTH-03 | security login | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| AUTH-04 | wrong password | code=1001 | HTTP=200 code=1001 msg=用户名或密码错误 | PASS |
| AUTH-05 | info without token | code=401 | HTTP=200 code=401 msg=未登录或登录已过期 | PASS |
| AUTH-06 | student self info | code=200 roles=STUDENT | HTTP=200 code=200 msg=操作成功 | PASS |
| REG-01 | register new student | code=200 token | HTTP=200 code=200 msg=操作成功 | PASS |
| REG-02 | duplicate register | code=1003 | HTTP=200 code=1003 msg=账号已存在 | PASS |
| CAT-01 | category list | code=200 count>0 | HTTP=200 code=200 msg=操作成功 | PASS |
| CAT-02 | admin add category | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CAT-03 | student add category forbidden | code=403 | HTTP=200 code=403 msg=没有访问权限 | PASS |
| GOODS-01 | publish normal goods | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| GOODS-02 | publish valuable goods | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| GOODS-03 | goods list contains published item | code=200 goodsId>0 total>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| GOODS-06 | goods keyword search | code=200 items>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| GOODS-04 | goods detail view count increment | viewCount +1 and details array | HTTP=200 code=200 msg=操作成功 | PASS |
| GOODS-05 | goods filter valuable | code=200 items>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| FIND-01 | publish lost notice | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| FIND-02 | findinfo page | code=200 total>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| CREDIT-01 | my credit initial | code=200 score=120 level=GOOD | HTTP=200 code=200 msg=操作成功 | PASS |
| CREDIT-02 | credit leaderboard | code=200 list>0 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-01 | submit claim application | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-02 | duplicate claim rejected | code=1005 | HTTP=200 code=1005 msg=您已对该物品提交过认领申请 | PASS |
| CLAIM-03 | my claims | code=200 total>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-04 | claim audit page sorted by priority | code=200 items>=1 priority=120 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-05 | audit pass | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-06 | confirm claim | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-07 | attestation ledger | code=200 total>=1 | HTTP=200 code=200 msg=操作成功 | PASS |
| CREDIT-03 | finder credit +10 after return | code=200 score=130 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-01 | valuables auto record on publish | code=200 items>=1 status=PENDING_AUDIT | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-02 | security audit valuable | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-03 | status IN_CUSTODY after audit | code=200 status=IN_CUSTODY | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-04 | register custody location | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-05 | student claim valuable with proofs | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-06 | status IN_VERIFICATION after claim | status=IN_VERIFICATION | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-07 | security verify pass | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-08 | valuables flow ledger | code=200 flows>=4 | HTTP=200 code=200 msg=操作成功 | PASS |
| SEC-09 | admin cannot audit valuables (403) | code=403 | HTTP=200 code=403 msg=没有访问权限 | PASS |
| CREDIT-04 | find user 2023002 | code=200 id found | HTTP=200 code=200 msg=操作成功 | PASS |
| CREDIT-05 | admin adjust credit -30 | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CREDIT-06 | credit becomes 50 level POOR | score=50 level=POOR | HTTP=200 code=200 msg=操作成功 | PASS |
| AUTH-07 | student 2023002 login | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| CLAIM-08 | low credit claim forbidden | code=1004 | HTTP=200 code=1004 msg=信用分过低，暂不可申请认领 | PASS |
| CAR-01 | carousel list | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| USER-01 | admin student page | code=200 | HTTP=200 code=200 msg=操作成功 | PASS |
| USER-02 | student cannot list users (403) | code=403 | HTTP=200 code=403 msg=没有访问权限 | PASS |

TOTAL=46 PASS=46 FAIL=0
