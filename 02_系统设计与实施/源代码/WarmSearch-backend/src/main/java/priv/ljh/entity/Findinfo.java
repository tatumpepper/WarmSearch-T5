package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 寻物启事（丢失物品，寻物）
 */
@Data
@TableName("findinfo")
public class Findinfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String flImgUrl;
    private String flImgDesc;
    private String flName;
    private String flContact;
    /** OPEN / FOUND / CLOSED */
    private String flStatus;
    private Long gid;
    private String lostLocation;
    private LocalDateTime lostTime;
    private Long publisherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
