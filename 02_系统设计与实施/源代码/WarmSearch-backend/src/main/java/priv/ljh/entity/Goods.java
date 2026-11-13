package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招领物品（拾到物品，寻失主）
 */
@Data
@TableName("goods")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String imgUrl;
    private String imgName;
    private String imgDesc;
    private Long gid;
    /** WAITING / CLAIMING / CLAIMED / OFFLINE */
    private String status;
    private String contactName;
    private String contact;
    private String foundLocation;
    private LocalDateTime foundTime;
    private Integer isValuable;
    private Integer viewCount;
    private Long publisherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
