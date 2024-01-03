package org.btbox.pan.services.modules.user.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:40
 * @version: 1.0
*/
/**
    * 用户信息表
    */
@Schema(description="用户信息表")
@Data
@TableName(value = "btbox_pan_user")
public class BtboxPanUser implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    @Schema(description="用户id")
    @NotNull(message = "用户id不能为null")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    @Schema(description="用户名")
    @Size(max = 255,message = "用户名最大长度要小于 255")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @Schema(description="密码")
    @Size(max = 255,message = "密码最大长度要小于 255")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 随机盐值
     */
    @TableField(value = "salt")
    @Schema(description="随机盐值")
    @Size(max = 255,message = "随机盐值最大长度要小于 255")
    @NotBlank(message = "随机盐值不能为空")
    private String salt;

    /**
     * 密保问题
     */
    @TableField(value = "question")
    @Schema(description="密保问题")
    @Size(max = 255,message = "密保问题最大长度要小于 255")
    @NotBlank(message = "密保问题不能为空")
    private String question;

    /**
     * 密保答案
     */
    @TableField(value = "answer")
    @Schema(description="密保答案")
    @Size(max = 255,message = "密保答案最大长度要小于 255")
    @NotBlank(message = "密保答案不能为空")
    private String answer;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    @NotNull(message = "创建时间不能为null")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    @NotNull(message = "更新时间不能为null")
    private Date updateTime;
}