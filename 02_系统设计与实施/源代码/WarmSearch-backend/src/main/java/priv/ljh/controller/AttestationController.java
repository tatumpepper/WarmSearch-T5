package priv.ljh.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.entity.Attestation;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.AttestationService;

import javax.annotation.Resource;

@Api(tags = "认领记录台账")
@RestController
@RequestMapping("/attestation")
public class AttestationController {

    @Resource
    private AttestationService attestationService;

    @ApiOperation("分页查询全部认领记录（管理员/保卫处）")
    @RequireRole({"ADMIN", "SECURITY"})
    @GetMapping("/page")
    public Result<PageResult<Attestation>> page(PageQuery pq) {
        Page<Attestation> page = new Page<>(pq.getCurrent(), pq.getSize());
        return Result.success(PageResult.of(
                attestationService.page(page, new QueryWrapper<Attestation>().orderByDesc("id"))));
    }

    @ApiOperation("查询我的认领记录（学生）")
    @RequireRole("STUDENT")
    @GetMapping("/my")
    public Result<PageResult<Attestation>> my(PageQuery pq) {
        Page<Attestation> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<Attestation> wrapper = new QueryWrapper<Attestation>()
                .eq("user_id", UserContext.getCurrentId())
                .orderByDesc("id");
        return Result.success(PageResult.of(attestationService.page(page, wrapper)));
    }

    @ApiOperation("认领记录详情")
    @GetMapping("/{id}")
    public Result<Attestation> get(@PathVariable Long id) {
        return Result.success(attestationService.getById(id));
    }
}
