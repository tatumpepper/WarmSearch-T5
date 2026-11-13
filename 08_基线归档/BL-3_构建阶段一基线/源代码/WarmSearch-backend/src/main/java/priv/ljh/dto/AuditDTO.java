package priv.ljh.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通用审核请求（通过/驳回 + 备注）
 */
@Data
public class AuditDTO implements Serializable {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "审核结果不能为空")
    private Boolean pass;

    private String remark;
}
