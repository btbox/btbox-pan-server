package org.btbox.pan.services.modules.user.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 11:22
 * @version: 1.0
 */
@Data
public class CheckUsernameContext implements Serializable {


    @Serial
    private static final long serialVersionUID = -2719629853012148080L;

    /**
     * 用户名
     */
    private String username;

}