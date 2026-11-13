package priv.ljh.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前登录用户（由 JWT 解析得到）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    /** 用户ID（学生为 suser.id，后台为 teacher.id） */
    private Long id;
    /** 登录账号 */
    private String username;
    /** 姓名 */
    private String name;
    /** 用户类型：STUDENT / TEACHER */
    private String userType;
    /** 角色：学生为 STUDENT，后台为 ADMIN / SECURITY */
    private String role;
}
