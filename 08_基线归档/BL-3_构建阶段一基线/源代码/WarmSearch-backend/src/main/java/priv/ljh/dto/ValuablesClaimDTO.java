package priv.ljh.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 贵重物品认领申请（含购买凭证 / 身份证明）
 */
@Data
public class ValuablesClaimDTO implements Serializable {

    @NotNull(message = "贵重物品ID不能为空")
    private Long valuablesId;

    private String claimerName;
    private String claimerContact;
    private String claimerNumber;
    private String purchaseProofImg;
    private String idProofImg;
    private String remark;
}
