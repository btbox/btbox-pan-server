package org.btbox.pan.services.modules.file.domain.vo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;
import org.btbox.common.json.utils.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 文件夹树节点实体
 * @author: BT-BOX
 * @createDate: 2023/12/29 15:31
 * @version: 1.0
 */
@Schema(title = "文件夹树节点实体")
@Data
public class FolderTreeNodeVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2107545040180535380L;

    @Schema(title = "文件夹名称")
    private String label;

    @Schema(title = "文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @Schema(title = "父文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @Schema(title = "子节点集合")
    private List<FolderTreeNodeVO> children;

    public void print() {

        String jsonString = JSONUtil.toJsonStr(this);
        System.out.println("jsonString = " + jsonString);
    }
}