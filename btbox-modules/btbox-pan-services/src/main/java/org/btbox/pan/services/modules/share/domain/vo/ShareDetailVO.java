package org.btbox.pan.services.modules.share.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/17 9:29
 * @version: 1.0
 */
@Schema(title = "分享详情的返回实体对象")
@Data
public class ShareDetailVO {

    @Schema(title = "分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @Schema(title = "分享的名称")
    private String shareName;

    @Schema(title = "分享的创建时间")
    private Date createTime;

    @Schema(title = "分享的过期类型")
    private Integer shareDay;

    @Schema(title = "分享的截止时间")
    private Date shareEndTime;

    @Schema(title = "分享的文件列表")
    private List<UserFileVO> userFileVOList;

    @Schema(title = "分享者的信息")
    private ShareUserInfoVO shareUserInfoVO;
}