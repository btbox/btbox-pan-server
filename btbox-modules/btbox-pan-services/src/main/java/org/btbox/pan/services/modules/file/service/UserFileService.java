package org.btbox.pan.services.modules.file.service;

import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.btbox.pan.services.modules.file.domain.vo.FileChunkUploadVO;
import org.btbox.pan.services.modules.file.domain.vo.UploadedChunksVO;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:15
 * @version: 1.0
 */
public interface UserFileService extends IService<UserFile> {

    /**
     * 创建文件夹信息
     *
     * @param createFolderContext
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/14 9:25
     * @return: java.lang.Long
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 获取用户的根文件夹信息
     *
     * @param userId
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/25 17:58
     * @return: org.btbox.pan.services.modules.file.domain.entity.UserFile
     */
    UserFile getUserRootFile(Long userId);

    /**
     * 查询用户的文件列表
     * @author: BT-BOX(HJH)
     * @param
     * @version: 1.0
     * @createDate: 2023/12/26 10:40
     * @return: java.util.List<org.btbox.pan.services.modules.file.domain.vo.UserFileVO>
     */
    List<UserFileVO> getFileList(QueryFileListContext context);

    /**
     * 更新文件名称
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/26 17:22
     * @return: void
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除用户文件
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/27 9:12
     * @return: void
     */
    void deleteFile(DeleteFileContext context);

    /**
     * 文件秒传
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/27 10:27
     * @return: boolean
     */
    boolean secUpload(SecUploadFileContext context);

    /**
     * 单文件上传
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/27 14:48
     * @return: void
     */
    void upload(FileUploadContext context);

    /**
     * 文件分片上传
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/28 15:07
     * @return: org.btbox.pan.services.modules.file.domain.vo.FileChunkUploadVO
     */
    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    /**
     * 查询用户已上传的分片列表
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/28 16:30
     * @return: org.btbox.pan.services.modules.file.domain.vo.UploadedChunksVO
     */
    UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context);

    /**
     * 文件分片合并
     * @param context
     */
    void mergeFile(FileChunkMergeContext context);

    /**
     * 文件下载
     * @param context
     */
    void download(FileDownloadContext context);
}
