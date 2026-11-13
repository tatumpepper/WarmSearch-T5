package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信用分
 */
@Data
@TableName("credit_score")
public class CreditScore {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer score;
    /** EXCELLENT / GOOD / NORMAL / POOR */
    private String level;
    private Integer returnCount;
    private Integer falseClaimCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
