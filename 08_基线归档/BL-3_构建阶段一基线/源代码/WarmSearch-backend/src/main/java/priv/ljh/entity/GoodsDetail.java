package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品详情图
 */
@Data
@TableName("goods_detail")
public class GoodsDetail {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private String dimgUrl;
    private Integer sort;
    private LocalDateTime createTime;
}
