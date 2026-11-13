package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.dto.AuditDTO;
import priv.ljh.dto.ClaimApplyDTO;
import priv.ljh.entity.ClaimApplication;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.ClaimService;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "信用认领-认领申请")
@RestController
@RequestMapping("/claim")
public class ClaimController {

    @Resource
    private ClaimService claimService;

    @ApiOperation("学生提交认领申请")
    @RequireRole("STUDENT")
    @PostMapping
    public Result<Void> apply(@Valid @RequestBody ClaimApplyDTO dto) {
        claimService.apply(dto, UserContext.get());
        return Result.success();
    }

    @ApiOperation("查询我的认领申请（学生）")
    @RequireRole("STUDENT")
    @GetMapping("/my")
    public Result<PageResult<ClaimApplication>> my(PageQuery pq) {
        return Result.success(claimService.myPage(pq, UserContext.getCurrentId()));
    }

    @ApiOperation("认领申请审核列表（后台，按信用优先级排序）")
    @RequireRole({"ADMIN", "SECURITY"})
    @GetMapping("/page")
    public Result<PageResult<ClaimApplication>> page(PageQuery pq,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) Long goodsId) {
        return Result.success(claimService.auditPage(pq, status, goodsId));
    }

    @ApiOperation("审核认领申请（通过/驳回）")
    @RequireRole({"ADMIN", "SECURITY"})
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        claimService.audit(id, dto.getPass(), dto.getRemark(), UserContext.get());
        return Result.success();
    }

    @ApiOperation("最终确认认领")
    @RequireRole({"ADMIN", "SECURITY"})
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        claimService.confirm(id, UserContext.get());
        return Result.success();
    }

    @ApiOperation("判定为虚假认领并扣分")
    @RequireRole({"ADMIN", "SECURITY"})
    @PostMapping("/{id}/false")
    public Result<Void> markFalse(@PathVariable Long id) {
        claimService.markFalse(id, UserContext.get());
        return Result.success();
    }
}
