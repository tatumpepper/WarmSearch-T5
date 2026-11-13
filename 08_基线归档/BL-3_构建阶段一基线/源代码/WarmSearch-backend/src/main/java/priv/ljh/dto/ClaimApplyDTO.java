package priv.ljh.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 学生提交认领申请
 */
@Data
public class ClaimApplyDTO implements Serializable {

    @NotNull(message = "物品ID不能为空")
    private Long goodsId;

    private String contact;
    /** IDCARD / PURCHASE / OTHER */
    private String credentialType;
    private String credentialImg;
    private String description;
}
