package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认领申请
 */
@Data
@TableName("claim_application")
public class ClaimApplication {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long applicantId;
    private String applicantName;
    private String contact;
    private String credentialType;
    private String credentialImg;
    private String description;
    private Integer priority;
    /** PENDING / PASSED / REJECTED / CONFIRMED / FALSE / CANCELED */
    private String status;
    private Long auditTeacherId;
    private String auditRemark;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
