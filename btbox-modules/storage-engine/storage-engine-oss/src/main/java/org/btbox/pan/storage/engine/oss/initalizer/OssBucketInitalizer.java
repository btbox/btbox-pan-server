package org.btbox.pan.storage.engine.oss.initalizer;

import com.aliyun.oss.OSSClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.btbox.pan.storage.engine.oss.config.OssStorageEngineConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;

/**
 * @description: 初始化上传文件根目录和文件分片存储根目录的初始化器
 * @author: BT-BOX
 * @createDate: 2024/1/4 15:09
 * @version: 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OssBucketInitalizer implements CommandLineRunner {

    private final OssStorageEngineConfig config;

    private final OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        String bucketName = config.getBucketName();

        boolean bucketExist = client.doesBucketExist(bucketName);
        // 桶不存在，并且要求自动创建
        if (!bucketExist && config.getAutoCreateBucket()) {
            client.createBucket(bucketName);
        }

        if (!bucketExist && !config.getAutoCreateBucket()) {
            throw new ServerException("这个桶 " + bucketName + " 需要您手动创建");
        }

        log.info("桶名称: " + bucketName + " 已经创建完成");

    }
}