package org.btbox.pan.services.modules.share.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
/**
    * 用户分享文件表
    */
@Schema(description="用户分享文件表")
@Data
@TableName(value = "btbox_pan_share_file")
public class PanShareFile {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    @NotNull(message = "主键不能为null")
    private Long id;

    /**
     * 分享id
     */
    @TableField(value = "share_id")
    @Schema(description="分享id")
    @NotNull(message = "分享id不能为null")
    private Long shareId;

    /**
     * 文件记录ID
     */
    @TableField(value = "file_id")
    @Schema(description="文件记录ID")
    @NotNull(message = "文件记录ID不能为null")
    private Long fileId;

    /**
     * 分享创建人
     */
    @TableField(value = "create_user")
    @Schema(description="分享创建人")
    @NotNull(message = "分享创建人不能为null")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @Schema(description="创建时间")
    @NotNull(message = "创建时间不能为null")
    private Date createTime;
}