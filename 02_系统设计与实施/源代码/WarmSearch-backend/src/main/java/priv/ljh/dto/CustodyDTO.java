package priv.ljh.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 贵重物品入库保管登记
 */
@Data
public class CustodyDTO implements Serializable {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String custodyLocation;
}
