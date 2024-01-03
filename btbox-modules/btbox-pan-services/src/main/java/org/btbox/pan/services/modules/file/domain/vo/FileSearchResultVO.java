package org.btbox.pan.services.modules.file.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 用户搜索文件列表实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 10:40
 * @version: 1.0
 */
@Schema(title = "用户搜索文件列表实体")
@Data
public class FileSearchResultVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7709122828515545135L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(title = "文件ID")
    private Long fileId;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(title = "父文件夹ID")
    private Long parentId;

    @Schema(title = "父文件夹名称")
    private String parentFilename;

    @Schema(title = "文件名称")
    private String filename;

    @Schema(title = "文件大小描述")
    private String fileSizeDesc;

    @Schema(title = "文件夹标识 0 否 1 是")
    private Integer folderFlag;

    @Schema(description="文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）")
    private Integer fileType;

    @Schema(title = "文件更新时间")
    private Date updateTime;
}