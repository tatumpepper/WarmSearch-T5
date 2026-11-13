package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.dto.AuditDTO;
import priv.ljh.dto.CustodyDTO;
import priv.ljh.dto.ValuablesClaimDTO;
import priv.ljh.entity.ValuablesFlow;
import priv.ljh.entity.ValuablesItem;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.ValuablesFlowService;
import priv.ljh.service.ValuablesService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "保卫处贵重物品处理")
@RestController
@RequestMapping("/valuables")
public class ValuablesController {

    @Resource
    private ValuablesService valuablesService;
    @Resource
    private ValuablesFlowService flowService;

    @ApiOperation("贵重物品分页列表（管理员/保卫处）")
    @RequireRole({"ADMIN", "SECURITY"})
    @GetMapping("/page")
    public Result<PageResult<ValuablesItem>> page(PageQuery pq,
                                                  @RequestParam(required = false) String status) {
        return Result.success(valuablesService.page(pq, status));
    }

    @ApiOperation("贵重物品详情")
    @RequireRole({"ADMIN", "SECURITY"})
    @GetMapping("/{id}")
    public Result<ValuablesItem> get(@PathVariable Long id) {
        return Result.success(valuablesService.getById(id));
    }

    @ApiOperation("贵重物品处理台账（流转记录）")
    @RequireRole({"ADMIN", "SECURITY"})
    @GetMapping("/{id}/flows")
    public Result<List<ValuablesFlow>> flows(@PathVariable Long id) {
        return Result.success(flowService.listByValuablesId(id));
    }

    @ApiOperation("保卫处审核贵重物品（通过即入库）")
    @RequireRole("SECURITY")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        valuablesService.audit(id, dto.getPass(), dto.getRemark(), UserContext.get());
        return Result.success();
    }

    @ApiOperation("登记入库保管位置")
    @RequireRole("SECURITY")
    @PostMapping("/{id}/custody")
    public Result<Void> custody(@PathVariable Long id, @Valid @RequestBody CustodyDTO dto) {
        valuablesService.custody(id, dto.getCustodyLocation(), UserContext.get());
        return Result.success();
    }

    @ApiOperation("认领人提交贵重物品认领（上传购买凭证/身份证明）")
    @RequireRole("STUDENT")
    @PostMapping("/claim")
    public Result<Void> claim(@Valid @RequestBody ValuablesClaimDTO dto) {
        valuablesService.submitClaim(dto, UserContext.get());
        return Result.success();
    }

    @ApiOperation("保卫处线下核验（通过则认领完成，不通过退回保管）")
    @RequireRole("SECURITY")
    @PostMapping("/{id}/verify")
    public Result<Void> verify(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        valuablesService.verify(id, dto.getPass(), dto.getRemark(), UserContext.get());
        return Result.success();
    }
}
