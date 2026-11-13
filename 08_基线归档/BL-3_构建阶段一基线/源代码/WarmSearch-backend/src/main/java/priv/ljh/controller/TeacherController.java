package priv.ljh.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.BusinessException;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.entity.Teacher;
import priv.ljh.security.RequireRole;
import priv.ljh.service.TeacherService;

import javax.annotation.Resource;

@Api(tags = "后台用户管理（管理员/保卫处账号）")
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    @ApiOperation("分页查询后台用户（管理员）")
    @RequireRole("ADMIN")
    @GetMapping("/page")
    public Result<PageResult<Teacher>> page(PageQuery pq,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String role) {
        Page<Teacher> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("username", keyword).or().like("name", keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq("role", role);
        }
        wrapper.orderByDesc("id");
        return Result.success(PageResult.of(teacherService.page(page, wrapper)));
    }

    @ApiOperation("新增后台用户（管理员），密码按明文传入，后端MD5")
    @RequireRole("ADMIN")
    @PostMapping
    public Result<Void> add(@RequestBody Teacher teacher) {
        if (teacherService.findByUsername(teacher.getUsername()) != null) {
            throw new BusinessException("登录账号已存在");
        }
        teacher.setPassword(SecureUtil.md5(teacher.getPassword()));
        if (teacher.getRole() == null) {
            teacher.setRole("ADMIN");
        }
        teacher.setStatus(1);
        teacherService.save(teacher);
        return Result.success();
    }

    @ApiOperation("修改后台用户（管理员）")
    @RequireRole("ADMIN")
    @PutMapping
    public Result<Void> update(@RequestBody Teacher teacher) {
        // 若传入了非空且非MD5的新密码则加密，否则不改动密码
        if (teacher.getPassword() != null && teacher.getPassword().length() != 32) {
            teacher.setPassword(SecureUtil.md5(teacher.getPassword()));
        } else {
            teacher.setPassword(null);
        }
        teacherService.updateById(teacher);
        return Result.success();
    }

    @ApiOperation("删除后台用户（管理员）")
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.removeById(id);
        return Result.success();
    }
}
