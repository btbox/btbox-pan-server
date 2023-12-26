package org.btbox.pan.services.modules.file.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 11:47
 * @version: 1.0
 */
@Schema(title = "创建文件夹参数实体")
@Data
public class CreateFolderBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3207603157749018957L;

    @Schema(title = "父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @Schema(title = "文件夹名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}