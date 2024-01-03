package org.btbox.pan.services.modules.user.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;
import org.btbox.common.mybatis.core.domain.BaseEntity;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/3 11:25
 * @version: 1.0
*/
/**
    * 用户搜索历史表
    */
@Schema(description="用户搜索历史表")
@Data
@TableName(value = "btbox_pan_user_search_history")
public class PanUserSearchHistory {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    @NotNull(message = "主键不能为null")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @Schema(description="用户id")
    @NotNull(message = "用户id不能为null")
    private Long userId;

    /**
     * 搜索文案
     */
    @TableField(value = "search_content")
    @Schema(description="搜索文案")
    @Size(max = 255,message = "搜索文案最大长度要小于 255")
    @NotBlank(message = "搜索文案不能为空")
    private String searchContent;

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