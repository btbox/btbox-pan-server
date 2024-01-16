package org.btbox.pan.services.modules.share.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.github.linpeilie.annotations.AutoMappings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;

/**
 * @description: 用户分享表
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
@AutoMappers(
        @AutoMapper(target = PanShareUrlListVO.class)
)
@Schema(description="用户分享表")
@Data
@TableName(value = "btbox_pan_share")
public class PanShare {
    /**
     * 分享id
     */
    @TableId(value = "share_id", type = IdType.ASSIGN_ID)
    @Schema(description="分享id")
    @NotNull(message = "分享id不能为null")
    private Long shareId;

    /**
     * 分享名称
     */
    @TableField(value = "share_name")
    @Schema(description="分享名称")
    @Size(max = 255,message = "分享名称最大长度要小于 255")
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    /**
     * 分享类型（0 有提取码）
     */
    @TableField(value = "share_type")
    @Schema(description="分享类型（0 有提取码）")
    @NotNull(message = "分享类型（0 有提取码）不能为null")
    private Integer shareType;

    /**
     * 分享类型（0 永久有效；1 7天有效；2 30天有效）
     */
    @TableField(value = "share_day_type")
    @Schema(description="分享类型（0 永久有效；1 7天有效；2 30天有效）")
    @NotNull(message = "分享类型（0 永久有效；1 7天有效；2 30天有效）不能为null")
    private Integer shareDayType;

    /**
     * 分享有效天数（永久有效为0）
     */
    @TableField(value = "share_day")
    @Schema(description="分享有效天数（永久有效为0）")
    @NotNull(message = "分享有效天数（永久有效为0）不能为null")
    private Integer shareDay;

    /**
     * 分享结束时间
     */
    @TableField(value = "share_end_time")
    @Schema(description="分享结束时间")
    @NotNull(message = "分享结束时间不能为null")
    private Date shareEndTime;

    /**
     * 分享链接地址
     */
    @TableField(value = "share_url")
    @Schema(description="分享链接地址")
    @Size(max = 255,message = "分享链接地址最大长度要小于 255")
    @NotBlank(message = "分享链接地址不能为空")
    private String shareUrl;

    /**
     * 分享提取码
     */
    @TableField(value = "share_code")
    @Schema(description="分享提取码")
    @Size(max = 255,message = "分享提取码最大长度要小于 255")
    @NotBlank(message = "分享提取码不能为空")
    private String shareCode;

    /**
     * 分享状态（0 正常；1 有文件被删除）
     */
    @TableField(value = "share_status")
    @Schema(description="分享状态（0 正常；1 有文件被删除）")
    @NotNull(message = "分享状态（0 正常；1 有文件被删除）不能为null")
    private Integer shareStatus;

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