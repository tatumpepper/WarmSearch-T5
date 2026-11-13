package priv.ljh.service;

import cn.hutool.crypto.SecureUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.ljh.common.BusinessException;
import priv.ljh.common.ResultCode;
import priv.ljh.constant.RoleConstant;
import priv.ljh.dto.LoginDTO;
import priv.ljh.dto.RegisterDTO;
import priv.ljh.entity.Suser;
import priv.ljh.entity.Teacher;
import priv.ljh.security.JwtUtils;
import priv.ljh.security.LoginUser;

import javax.annotation.Resource;

/**
 * 认证服务：学生注册/登录、后台用户登录
 */
@Service
public class AuthService {

    @Resource
    private SuserService suserService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private CreditService creditService;

    /**
     * 学生注册，注册成功即返回令牌，并初始化信用档案
     */
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterDTO dto) {
        if (suserService.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        Suser user = new Suser();
        user.setUsername(dto.getUsername());
        user.setPassword(SecureUtil.md5(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhoneNum(dto.getPhoneNum());
        user.setProfession(dto.getProfession());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        suserService.save(user);

        creditService.getOrCreate(user.getId());

        LoginUser loginUser = LoginUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .userType(RoleConstant.STUDENT)
                .role(RoleConstant.STUDENT)
                .build();
        return jwtUtils.createToken(loginUser);
    }

    /**
     * 学生登录
     */
    public String studentLogin(LoginDTO dto) {
        Suser user = suserService.findByUsername(dto.getUsername());
        if (user == null || !user.getPassword().equals(SecureUtil.md5(dto.getPassword()))) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        LoginUser loginUser = LoginUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .userType(RoleConstant.STUDENT)
                .role(RoleConstant.STUDENT)
                .build();
        return jwtUtils.createToken(loginUser);
    }

    /**
     * 后台用户（管理员 / 保卫处）登录
     */
    public String teacherLogin(LoginDTO dto) {
        Teacher teacher = teacherService.findByUsername(dto.getUsername());
        if (teacher == null || !teacher.getPassword().equals(SecureUtil.md5(dto.getPassword()))) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (teacher.getStatus() == null || teacher.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        LoginUser loginUser = LoginUser.builder()
                .id(teacher.getId())
                .username(teacher.getUsername())
                .name(teacher.getName())
                .userType(RoleConstant.TEACHER)
                .role(teacher.getRole())
                .build();
        return jwtUtils.createToken(loginUser);
    }
}
