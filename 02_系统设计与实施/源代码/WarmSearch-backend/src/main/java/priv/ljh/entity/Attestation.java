package priv.ljh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 普通认领记录
 */
@Data
@TableName("attestation")
public class Attestation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private String goodsName;
    private Long userId;
    private String userName;
    private String contact;
    private String number;
    private String address;
    private String imgUrl;
    private LocalDateTime claimTime;
    private LocalDateTime createTime;
}
