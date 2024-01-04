package org.btbox.pan.storage.engine.local.initalizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.btbox.pan.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @description: 初始化上传文件根目录和文件分片存储根目录的初始化器
 * @author: BT-BOX
 * @createDate: 2024/1/4 15:09
 * @version: 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UploadFolderAndChunksFolderInitalizer implements CommandLineRunner {

    private final LocalStorageEngineConfig config;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(config.getRootFilePath()));
        log.info("默认文件的存储路径创建完成");
        FileUtils.forceMkdir(new File(config.getRootFileChunkPath()));
        log.info("默认的文件分片的存储路径创建完成");
    }
}