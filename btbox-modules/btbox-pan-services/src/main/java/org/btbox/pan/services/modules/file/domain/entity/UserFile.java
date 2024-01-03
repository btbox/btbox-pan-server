package org.btbox.pan.services.modules.file.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.github.linpeilie.annotations.AutoMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;
import org.btbox.pan.services.modules.file.domain.vo.FileSearchResultVO;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:15
 * @version: 1.0
*/
/**
    * 用户文件信息表
    */
@Schema(description="用户文件信息表")
@Data
@TableName(value = "btbox_pan_user_file")
@AutoMappers({
        @AutoMapper(target = UserFileVO.class),
        @AutoMapper(target = FileSearchResultVO.class)
})

public class UserFile {
    /**
     * 文件记录ID
     */
    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    @Schema(description="文件记录ID")
    @NotNull(message = "文件记录ID不能为null")
    private Long fileId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description="用户ID")
    @NotNull(message = "用户ID不能为null")
    private Long userId;

    /**
     * 上级文件夹ID,顶级文件夹为0
     */
    @TableField(value = "parent_id")
    @Schema(description="上级文件夹ID,顶级文件夹为0")
    @NotNull(message = "上级文件夹ID,顶级文件夹为0不能为null")
    private Long parentId;

    /**
     * 真实文件id
     */
    @TableField(value = "real_file_id")
    @Schema(description="真实文件id")
    @NotNull(message = "真实文件id不能为null")
    private Long realFileId;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    @Schema(description="文件名")
    @Size(max = 255,message = "文件名最大长度要小于 255")
    @NotBlank(message = "文件名不能为空")
    private String filename;

    /**
     * 是否是文件夹 （0 否 1 是）
     */
    @TableField(value = "folder_flag")
    @Schema(description="是否是文件夹 （0 否 1 是）")
    @NotNull(message = "是否是文件夹 （0 否 1 是）不能为null")
    private Integer folderFlag;

    /**
     * 文件大小展示字符
     */
    @TableField(value = "file_size_desc")
    @Schema(description="文件大小展示字符")
    @Size(max = 255,message = "文件大小展示字符最大长度要小于 255")
    @NotBlank(message = "文件大小展示字符不能为空")
    private String fileSizeDesc;

    /**
     * 文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     */
    @TableField(value = "file_type")
    @Schema(description="文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）")
    @NotNull(message = "文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）不能为null")
    private Integer fileType;

    /**
     * 删除标识（0 否 1 是）
     */
    @TableField(value = "del_flag")
    @TableLogic
    @Schema(description="删除标识（0 否 1 是）")
    @NotNull(message = "删除标识（0 否 1 是）不能为null")
    private Integer delFlag;

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