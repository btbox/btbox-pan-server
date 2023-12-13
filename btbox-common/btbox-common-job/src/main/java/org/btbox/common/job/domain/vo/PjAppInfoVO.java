package org.btbox.common.job.domain.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.btbox.common.job.domain.entity.PjAppInfo;

import java.util.Date;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/10/26 16:40
 * @version: 1.0
 */
@TableName("pj_app_info")
@Data
@AutoMapper(target = PjAppInfo.class)
public class PjAppInfoVO {

    private Long id;

    private String appName;

    private String currentServer;

    private Date gmtCreate;

    private Date gmtModified;

    private String password;

}