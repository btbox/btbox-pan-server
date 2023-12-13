package org.btbox.common.job.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.btbox.common.core.utils.SpringUtils;
import org.btbox.common.job.domain.entity.PjAppInfo;
import org.btbox.common.job.mapper.PowerJobMapper;
import org.btbox.common.job.domain.vo.PjAppInfoVO;
import tech.powerjob.client.IPowerJobClient;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.request.http.SaveWorkflowNodeRequest;
import tech.powerjob.common.request.http.SaveWorkflowRequest;
import tech.powerjob.common.request.query.JobInfoQuery;
import tech.powerjob.common.response.*;
import tech.powerjob.worker.autoconfigure.PowerJobProperties;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/10/26 16:22
 * @version: 1.0
 */
public class PowerJobUtils {

    private static final PowerJobProperties POWER_JOB_PROPERTIES = SpringUtils.getBean(PowerJobProperties.class);

    private static final PowerJobMapper POWER_JOB_MAPPER = SpringUtils.getBean(PowerJobMapper.class);

    // 使用直接 PowerJobUtils.getClient() 获取实例后根据官网文档使用api
    // 官方API文档 https://www.yuque.com/powerjob/guidence/olgyf0#0xRrA
    @Getter
    private static final PowerJobClient client;

    static {
        PowerJobProperties.Worker worker = POWER_JOB_PROPERTIES.getWorker();
        PjAppInfoVO pjAppInfoVO = POWER_JOB_MAPPER.selectVoOne(new LambdaQueryWrapper<PjAppInfo>().eq(PjAppInfo::getAppName,"btbox-worker"));
        client = new PowerJobClient(
                worker.getServerAddress(),
                worker.getAppName(),
                pjAppInfoVO.getPassword()
        );
    }

}