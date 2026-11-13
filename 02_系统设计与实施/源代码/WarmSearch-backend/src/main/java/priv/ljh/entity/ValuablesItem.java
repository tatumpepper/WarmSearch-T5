package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 贵重物品（保卫处）
 */
@Data
@TableName("valuables_item")
public class ValuablesItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private String itemName;
    private String itemCategory;
    private BigDecimal estimatedValue;
    private String imgUrl;
    private String description;
    private Long reportUserId;
    /** AUTO / MANUAL */
    private String reportSource;
    /** PENDING_AUDIT / IN_CUSTODY / IN_VERIFICATION / CLAIMED / REJECTED / UNCLAIMED */
    private String status;
    private String custodyLocation;
    private Long securityId;
    private LocalDateTime reportTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
