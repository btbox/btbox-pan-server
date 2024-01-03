package org.btbox.pan.services.modules.file.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 面包屑列表展示实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 15:07
 * @version: 1.0
 */
@Schema(title = "面包屑列表展示实体")
@Data
public class BreadcrumbVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4624143829784983435L;

    @Schema(title = "文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @Schema(title = "父文件夹ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @Schema(title = "文件夹名称")
    private String name;

    public static BreadcrumbVO transfer(UserFile record) {
        BreadcrumbVO vo = new BreadcrumbVO();
        if (ObjectUtil.isNotNull(record)) {
            vo.setId(record.getFileId());
            vo.setParentId(record.getParentId());
            vo.setName(record.getFilename());
        }
        return vo;
    }
}