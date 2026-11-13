package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 信用变动流水
 */
@Data
@TableName("credit_log")
public class CreditLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer changeValue;
    /** RETURN / FALSE_CLAIM / PUNISH / MANUAL */
    private String type;
    private String reason;
    private Long bizId;
    private Long operatorId;
    private LocalDateTime createTime;
}
