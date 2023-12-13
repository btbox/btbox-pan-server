package org.btbox.common.job.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/10/26 16:40
 * @version: 1.0
 */
@TableName(value = "pj_app_info")
@Data
public class PjAppInfo {

    private Long id;

    private String appName;

    private String currentServer;

    private Date gmtCreate;

    private Date gmtModified;

    private String password;

}