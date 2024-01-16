package org.btbox.pan.services.modules.share.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;

import java.util.List;

/**
 * @description: 创建分享链接上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/16 11:15
 * @version: 1.0
 */
@Data
public class CreateShareUrlContext {

    /**
     * 分享的名称
     */
    private String shareName;

    /**
     * 分享的类型
     */
    private Integer shareType;

    /**
     * 分享的日期类型
     */
    private Integer shareDayType;

    /**
     * 分享的对应的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 已经保存的分享实体信息
     */
    private PanShare record;

}