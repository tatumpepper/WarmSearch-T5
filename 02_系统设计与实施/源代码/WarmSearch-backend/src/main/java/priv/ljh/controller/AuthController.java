package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import priv.ljh.common.Result;
import priv.ljh.dto.LoginDTO;
import priv.ljh.dto.RegisterDTO;
import priv.ljh.security.LoginUser;
import priv.ljh.security.RequireLogin;
import priv.ljh.security.UserContext;
import priv.ljh.service.AuthService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证管理")
@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @ApiOperation("学生注册")
    @PostMapping("/student/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterDTO dto) {
        String token = authService.register(dto);
        return Result.success(Collections.singletonMap("token", token));
    }

    @ApiOperation("学生登录")
    @PostMapping("/student/login")
    public Result<Map<String, String>> studentLogin(@Valid @RequestBody LoginDTO dto) {
        String token = authService.studentLogin(dto);
        return Result.success(Collections.singletonMap("token", token));
    }

    @ApiOperation("后台用户登录（管理员/保卫处）")
    @PostMapping("/user/login")
    public Result<Map<String, String>> teacherLogin(@Valid @RequestBody LoginDTO dto) {
        String token = authService.teacherLogin(dto);
        return Result.success(Collections.singletonMap("token", token));
    }

    @ApiOperation("获取当前登录用户信息")
    @RequireLogin
    @GetMapping("/user/info")
    public Result<Map<String, Object>> info() {
        LoginUser user = UserContext.get();
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("name", user.getName());
        data.put("userType", user.getUserType());
        data.put("roles", Collections.singletonList(user.getRole()));
        data.put("avatar", "");
        return Result.success(data);
    }

    @ApiOperation("退出登录")
    @RequireLogin
    @PostMapping("/user/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
