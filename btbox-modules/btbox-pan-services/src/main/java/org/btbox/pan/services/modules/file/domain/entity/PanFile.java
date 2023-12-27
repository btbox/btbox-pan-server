package org.btbox.pan.services.modules.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:31
 * @version: 1.0
*/
/**
    * 物理文件信息表
    */
@Schema(description="物理文件信息表")
@Data
@TableName(value = "btbox_pan_file")
public class PanFile {
    /**
     * 文件id
     */
    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    @Schema(description="文件id")
    @NotNull(message = "文件id不能为null")
    private Long fileId;

    /**
     * 文件名称
     */
    @TableField(value = "filename")
    @Schema(description="文件名称")
    @Size(max = 255,message = "文件名称最大长度要小于 255")
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    /**
     * 文件物理路径
     */
    @TableField(value = "real_path")
    @Schema(description="文件物理路径")
    @Size(max = 700,message = "文件物理路径最大长度要小于 700")
    @NotBlank(message = "文件物理路径不能为空")
    private String realPath;

    /**
     * 文件实际大小
     */
    @TableField(value = "file_size")
    @Schema(description="文件实际大小")
    @Size(max = 255,message = "文件实际大小最大长度要小于 255")
    @NotBlank(message = "文件实际大小不能为空")
    private String fileSize;

    /**
     * 文件大小展示字符
     */
    @TableField(value = "file_size_desc")
    @Schema(description="文件大小展示字符")
    @Size(max = 255,message = "文件大小展示字符最大长度要小于 255")
    @NotBlank(message = "文件大小展示字符不能为空")
    private String fileSizeDesc;

    /**
     * 文件后缀
     */
    @TableField(value = "file_suffix")
    @Schema(description="文件后缀")
    @Size(max = 255,message = "文件后缀最大长度要小于 255")
    @NotBlank(message = "文件后缀不能为空")
    private String fileSuffix;

    /**
     * 文件预览的响应头Content-Type的值
     */
    @TableField(value = "file_preview_content_type")
    @Schema(description="文件预览的响应头Content-Type的值")
    @Size(max = 255,message = "文件预览的响应头Content-Type的值最大长度要小于 255")
    @NotBlank(message = "文件预览的响应头Content-Type的值不能为空")
    private String filePreviewContentType;

    /**
     * 文件唯一标识
     */
    @TableField(value = "identifier")
    @Schema(description="文件唯一标识")
    @Size(max = 255,message = "文件唯一标识最大长度要小于 255")
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    @Schema(description="创建人")
    @NotNull(message = "创建人不能为null")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @Schema(description="创建时间")
    @NotNull(message = "创建时间不能为null")
    private Date createTime;
}