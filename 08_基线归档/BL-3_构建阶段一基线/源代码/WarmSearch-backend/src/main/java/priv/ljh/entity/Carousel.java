package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图
 */
@Data
@TableName("carousel")
public class Carousel {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String imgUrl;
    /** MINI / PC */
    private String type;
    private String link;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
