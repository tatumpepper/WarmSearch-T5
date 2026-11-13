package priv.ljh.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "资源不存在"),

    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    ACCOUNT_DISABLED(1002, "账号已被停用或封禁"),
    USERNAME_EXISTS(1003, "账号已存在"),
    CREDIT_TOO_LOW(1004, "信用分过低，暂不可申请认领"),
    REPEAT_APPLY(1005, "您已对该物品提交过认领申请"),
    GOODS_NOT_WAITING(1006, "该物品当前不可被认领");

    private final Integer code;
    private final String msg;
}
