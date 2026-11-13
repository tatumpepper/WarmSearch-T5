package priv.ljh.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.entity.Suser;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.SuserService;

import javax.annotation.Resource;

@Api(tags = "学生用户管理")
@RestController
@RequestMapping("/suser")
public class SuserController {

    @Resource
    private SuserService suserService;

    @ApiOperation("分页查询学生（管理员）")
    @RequireRole("ADMIN")
    @GetMapping("/page")
    public Result<PageResult<Suser>> page(PageQuery pq,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Integer status) {
        Page<Suser> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<Suser> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("username", keyword).or().like("name", keyword));
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("id");
        return Result.success(PageResult.of(suserService.page(page, wrapper)));
    }

    @ApiOperation("学生详情")
    @GetMapping("/{id}")
    public Result<Suser> get(@PathVariable Long id) {
        return Result.success(suserService.getById(id));
    }

    @ApiOperation("学生修改个人资料")
    @RequireRole("STUDENT")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody Suser profile) {
        profile.setId(UserContext.getCurrentId());
        profile.setUsername(null);
        profile.setPassword(null);
        profile.setStatus(null);
        suserService.updateById(profile);
        return Result.success();
    }

    @ApiOperation("管理员启用/封禁学生")
    @RequireRole("ADMIN")
    @PostMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Suser user = new Suser();
        user.setId(id);
        user.setStatus(status);
        suserService.updateById(user);
        return Result.success();
    }

    @ApiOperation("管理员删除学生")
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        suserService.removeById(id);
        return Result.success();
    }
}
