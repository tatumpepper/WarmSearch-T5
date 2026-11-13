package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 贵重物品流转记录（处理台账）
 */
@Data
@TableName("valuables_flow")
public class ValuablesFlow {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long valuablesId;
    private Long goodsId;
    /** REPORT / AUDIT_PASS / CUSTODY / CLAIM_APPLY / VERIFY_OFFLINE / CLAIM_COMPLETE / REJECT / TRANSFER */
    private String action;
    private String fromStatus;
    private String toStatus;
    private String claimerName;
    private String claimerContact;
    private String claimerNumber;
    private String purchaseProofImg;
    private String idProofImg;
    private String verifyRemark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operateTime;
    private LocalDateTime createTime;
}
