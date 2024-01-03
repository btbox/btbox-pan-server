package org.btbox.pan.services.modules.log.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 17:19
 * @version: 1.0
*/
/**
    * 错误日志表
    */
@Schema(description="错误日志表")
@Data
@TableName(value = "btbox_pan_error_log")
public class PanErrorLog {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    @NotNull(message = "主键不能为null")
    private Long id;

    /**
     * 日志内容
     */
    @TableField(value = "log_content")
    @Schema(description="日志内容")
    @Size(max = 900,message = "日志内容最大长度要小于 900")
    @NotBlank(message = "日志内容不能为空")
    private String logContent;

    /**
     * 日志状态：0 未处理 1 已处理
     */
    @TableField(value = "log_status")
    @Schema(description="日志状态：0 未处理 1 已处理")
    private Integer logStatus;

    /**
     * 创建人
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    @Schema(description="创建人")
    @NotNull(message = "创建人不能为null")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    @NotNull(message = "创建时间不能为null")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新人")
    @NotNull(message = "更新人不能为null")
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    @NotNull(message = "更新时间不能为null")
    private Date updateTime;
}