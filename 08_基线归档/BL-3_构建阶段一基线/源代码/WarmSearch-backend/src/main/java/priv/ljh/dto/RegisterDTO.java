package priv.ljh.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RegisterDTO implements Serializable {

    @NotBlank(message = "学号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String name;
    private String phoneNum;
    private String profession;
    private String email;
}
