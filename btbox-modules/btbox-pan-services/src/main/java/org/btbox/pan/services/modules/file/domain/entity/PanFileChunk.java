package org.btbox.pan.services.modules.file.domain.entity;

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
 * @createDate: 2023/12/28 15:09
 * @version: 1.0
*/
/**
    * 文件分片信息表
    */
@Schema(description="文件分片信息表")
@Data
@TableName(value = "btbox_pan_file_chunk")
public class PanFileChunk {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    @NotNull(message = "主键不能为null")
    private Long id;

    /**
     * 文件唯一标识
     */
    @TableField(value = "identifier")
    @Schema(description="文件唯一标识")
    @Size(max = 255,message = "文件唯一标识最大长度要小于 255")
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    /**
     * 分片真实的存储路径
     */
    @TableField(value = "real_path")
    @Schema(description="分片真实的存储路径")
    @Size(max = 700,message = "分片真实的存储路径最大长度要小于 700")
    @NotBlank(message = "分片真实的存储路径不能为空")
    private String realPath;

    /**
     * 分片编号
     */
    @TableField(value = "chunk_number")
    @Schema(description="分片编号")
    @NotNull(message = "分片编号不能为null")
    private Integer chunkNumber;

    /**
     * 过期时间
     */
    @TableField(value = "expiration_time")
    @Schema(description="过期时间")
    @NotNull(message = "过期时间不能为null")
    private Date expirationTime;

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
}