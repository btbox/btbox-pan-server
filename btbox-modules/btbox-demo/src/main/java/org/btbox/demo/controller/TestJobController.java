package org.btbox.demo.controller;

import org.btbox.common.core.domain.R;
import org.btbox.common.job.utils.PowerJobUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/10/26 17:42
 * @version: 1.0
 */
@RestController
@RequestMapping("/demo/job")
public class TestJobController {

    @GetMapping("fetch-all-job")
    public R<ResultDTO<List<JobInfoDTO>>> fetchAllJob() {
        return R.ok(PowerJobUtils.getClient().fetchAllJob());
    }

    @PostMapping("save-job")
    public R<ResultDTO<Long>> saveJob(SaveJobInfoRequest request) {
        return R.ok(PowerJobUtils.getClient().saveJob(request));
    }

    @PostMapping("delete-job")
    public R<ResultDTO<Void>> deleteJob(Long id) {
        return R.ok(PowerJobUtils.getClient().deleteJob(id));
    }

    @PostMapping("fetch-job")
    public R<ResultDTO<JobInfoDTO>> fetchJob(Long id) {
        return R.ok(PowerJobUtils.getClient().fetchJob(id));
    }


}