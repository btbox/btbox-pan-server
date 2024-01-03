package org.btbox.pan.storage.engine.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import lombok.*;
import org.assertj.core.util.Lists;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.CacheNames;
import org.btbox.common.core.utils.DateUtils;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.common.redis.utils.CacheUtils;
import org.btbox.common.redis.utils.RedisUtils;
import org.btbox.pan.storage.engine.core.AbstractStorageEngine;
import org.btbox.pan.storage.engine.core.context.*;
import org.btbox.pan.storage.engine.oss.config.OssStorageEngineConfig;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 基于OSS的文件存储引擎实现
 * @author: BT-BOX
 * @createDate: 2023/12/27 11:46
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class OSSStorageEngine extends AbstractStorageEngine {

    private static final Integer TEN_THOUSAND_INT = 10000;

    private static final String CACHE_KEY_TEMPLATE = "oss_cache_upload_id_%s_%s";

    private static final String IDENTIFIER_KEY = "identifier";

    private static final String UPLOAD_ID_KEY = "uploadId";

    private static final String USER_ID_KEY = "userId";

    private static final String PART_NUMBER_KEY = "partNumber";

    private static final String E_TAG_KEY = "eTag";

    private static final String PART_SIZE_KEY = "partSize";

    private static final String PART_CRC_KEY = "partCRC";

    private final OssStorageEngineConfig config;

    private final OSSClient client;

    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     * <p>
     * OSS文件分片上传的步骤：
     * 1、初始化文件分片上传，获取一个全局唯一的uploadId
     * 2、并发上传文件分片，每一个文件分片都需要带有初始化返回的uploadId
     * 3、所有分片上传完成，触发文件分片合并的操作
     * <p>
     * 难点：
     * 1、我们的分片上传是在一个多线程并发环境下运行的，我们的程序需要保证我们的初始化分片上传的操作只有一个线程可以做
     * 2、我们所有的文件分片都需要带有一个全局唯一的uploadId,该uploadId就需要放到一个线程的共享空间中
     * 3、我们需要保证每一个文件分片都能够单独的去调用文件分片上传，而不是依赖于全局的uploadId
     * <p>
     * 解决方案：
     * 1、加锁，我们目前首先按照单体架构去考虑，使用JVM的锁去保证一个线程初始化文件分片上传，如果后续扩展成分布式的架构，需更换分布式锁
     * 2、使用缓存，缓存分为本地缓存以及分布式缓存（比如Redis），我们由于当前是一个单体架构，可以考虑使用本地缓存，但是，后期的项目额度分布式架构
     * 升级之后，同样要升级我们的缓存为分布式缓存，与其后期升级，我们还是第一版本就支持分布式缓存比较好
     * 3、我们要想把每一个文件的Key都能够通过文件的url来获取，就需要定义一种数据格式，支持我们添加附件数据，并且可以很方便的解析出来，我们的实现方案，可以参考
     * 网络请求的URL格式：fileRealPath?paramKey=paramValue
     * <p>
     * 具体的实现逻辑：
     * 1、校验文件分片数不得大于10000
     * 2、获取缓存key
     * 3、通过缓存key获取初始化后的实体对象，获取全局的uploadId和ObjectName
     * 4、如果获取为空，直接初始化
     * 5、执行文件分片上传的操作
     * 6、上传完成后，将全局的参数封装成一个可识别的url，保存在上下文里面，用于业务的落库操作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected synchronized void doStoreChunk(StoreFileChunkContext context) throws IOException {

        if (context.getTotalChunks() > TEN_THOUSAND_INT) {
            throw new ServerException("分片数超过了限制，分片数不得大于: " + TEN_THOUSAND_INT);
        }

        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());

        ChunkUploadEntity entity = RedisUtils.getCacheObject(cacheKey);

        if (ObjectUtil.isNull(entity)) {
            entity = initChunkUpload(context.getFilename(), cacheKey);
        }

        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(config.getBucketName());
        request.setKey(entity.getObjectKey());
        request.setUploadId(entity.getUploadId());
        request.setInputStream(context.getInputStream());
        request.setPartSize(context.getCurrentChunkSize());
        request.setPartNumber(context.getChunkNumber());

        UploadPartResult result = client.uploadPart(request);

        if (ObjectUtil.isNull(request)) {
            throw new ServerException("文件分片上传失败");
        }
        PartETag partETag = result.getPartETag();

        // 拼装文件分片的url
        JSONObject params = new JSONObject();
        params.set(IDENTIFIER_KEY, context.getIdentifier());
        params.set(UPLOAD_ID_KEY, entity.getUploadId());
        params.set(USER_ID_KEY, context.getUserId());
        params.set(PART_NUMBER_KEY, context.getChunkNumber());
        params.set(E_TAG_KEY, partETag.getETag());
        params.set(PART_SIZE_KEY, partETag.getPartSize());
        params.set(PART_CRC_KEY, partETag.getPartCRC());


        // 拼装文件分片的url
        String realPath = assembleUrl(entity.getObjectKey(), params);

        context.setRealPath(realPath);
    }



    /**
     * 执行删除物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtils.getFileSuffix(context.getFilename()));
        client.putObject(config.getBucketName(), realPath, context.getInputStream());
        context.setRealPath(realPath);
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {

    }

    /**
     * 执行文件分片的动作
     * 下沉到底层去实现
     *
     * 1. 获取缓存信息，拿到全局的uploadId
     * 2. 从上下文信息里面获取所有的分片的URL，解析出需要执行文件合并请求的参数
     * 3. 执行文件合并的请求
     * 4. 清除缓存
     * 5. 设置返回结果
     * @param context
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());
        ChunkUploadEntity entity = RedisUtils.getCacheObject(cacheKey);

        if (ObjectUtil.isNull(entity)) {
            throw new ServerException("文件分片合并失败，文件的唯一标识为：" + context.getIdentifier());
        }
        List<String> chunkPaths = context.getRealPathList();
        List<PartETag> partETags = CollUtil.newArrayList();

        if (CollUtil.isNotEmpty(chunkPaths)) {
            partETags = chunkPaths.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(this::analysisUrlParams)
                    .filter(ObjectUtil::isNotNull)
                    .filter(jsonObject -> !jsonObject.isEmpty())
                    .map(jsonObject -> new PartETag(jsonObject.getInt(PART_NUMBER_KEY),
                            jsonObject.getStr(E_TAG_KEY),
                            jsonObject.getLong(PART_SIZE_KEY),
                            jsonObject.getLong(PART_CRC_KEY)
                    )).collect(Collectors.toList());
        }

        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(config.getBucketName(), entity.getObjectKey(), entity.getUploadId(), partETags);
        CompleteMultipartUploadResult result = client.completeMultipartUpload(request);

        if (ObjectUtil.isNull(result)) {
            throw new ServerException("文件分片合并失败，文件的唯一标识为：" + context.getIdentifier());
        }
        RedisUtils.deleteObject(cacheKey);
        context.setRealPath(entity.getObjectKey());
    }

    /*************************************** private ****************************************/

    /**
     * 分析URL参数
     *
     * @param url
     * @return
     */
    private JSONObject analysisUrlParams(String url) {
        JSONObject result = new JSONObject();
        if (!checkHaveParams(url)) {
            return result;
        }
        String paramsPart = url.split(getSplitMark(BtboxConstants.QUESTION_MARK_STR))[1];
        if (StringUtils.isNotBlank(paramsPart)) {
            List<String> paramPairList = StringUtils.splitList(paramsPart, BtboxConstants.AND_MARK_STR);
            paramPairList.stream().forEach(paramPair -> {
                String[] paramArr = paramPair.split(getSplitMark(BtboxConstants.EQUALS_MARK_STR));
                if (paramArr != null && paramArr.length == BtboxConstants.TWO_INT) {
                    result.put(paramArr[0], paramArr[1]);
                }
            });
        }
        return result;
    }

    /**
     * 拼装URL
     *
     * @param baseUrl
     * @param params
     * @return baseUrl?paramKey1=paramValue1&paramKey2=paramValue2
     */
    private String assembleUrl(String baseUrl, JSONObject params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return baseUrl;
        }
        StringBuffer urlStringBuffer = new StringBuffer(baseUrl);
        urlStringBuffer.append(BtboxConstants.QUESTION_MARK_STR);
        List<String> paramsList = Lists.newArrayList();
        StringBuffer urlParamsStringBuffer = new StringBuffer();
        params.forEach((key, value) -> {
            urlParamsStringBuffer.setLength(BtboxConstants.ZERO_INT);
            urlParamsStringBuffer.append(key);
            urlParamsStringBuffer.append(BtboxConstants.EQUALS_MARK_STR);
            urlParamsStringBuffer.append(value);
            paramsList.add(urlParamsStringBuffer.toString());
        });
        return urlStringBuffer.append(StringUtils.join(paramsList, BtboxConstants.AND_MARK_STR)).toString();
    }

    /**
     * 获取基础URL
     *
     * @param url
     * @return
     */
    private String getBaseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return BtboxConstants.EMPTY_STR;
        }
        if (checkHaveParams(url)) {
            return url.split(getSplitMark(BtboxConstants.QUESTION_MARK_STR))[0];
        }
        return url;
    }

    /**
     * 检查是否是含有参数的URL
     *
     * @param url
     * @return
     */
    private boolean checkHaveParams(String url) {
        return StringUtils.isNotBlank(url) && url.indexOf(BtboxConstants.QUESTION_MARK_STR) != RPanConstants.MINUS_ONE_INT;
    }

    /**
     * 获取截取字符串的关键标识
     * 由于java的字符串分割会按照正则去截取
     * 我们的URL会影响标识的识别，故添加左右中括号去分组
     *
     * @param mark
     * @return
     */
    private String getSplitMark(String mark) {
        return new StringBuffer(BtboxConstants.LEFT_BRACKET_STR)
                .append(mark)
                .append(BtboxConstants.RIGHT_BRACKET_STR)
                .toString();
    }

    /**
     * 初始化文件分片上传
     * 1. 执行初始化请求
     * 2. 保存时初始化结果到缓存中
     * @param filename
     * @param cacheKey
     * @return
     */
    private ChunkUploadEntity initChunkUpload(String filename, String cacheKey) throws ServerException {
        String filePath = getFilePath(filename);

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), filePath);
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);

        if (ObjectUtil.isNull(result)) {
            throw new ServerException("文件分片上传初始化失败");
        }

        ChunkUploadEntity entity = new ChunkUploadEntity();
        entity.setObjectKey(filePath);
        entity.setUploadId(result.getUploadId());

        RedisUtils.setCacheObject(cacheKey, entity);
        return entity;
    }

    /**
     * 该实体为文件分片上传树池化之后的全局信息载体
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ChunkUploadEntity implements Serializable {

        @Serial
        private static final long serialVersionUID = -4924175915432667680L;

        /**
         * 分片上传全局唯一的uploadId
         */
        private String uploadId;

        /**
         * 文件分片上传的实体名称
         */
        private String objectKey;

    }

    /**
     * 获取分片上传的缓存key
     * @param identifier
     * @param userId
     * @return
     */
    private String getCacheKey(String identifier, Long userId) {
        return String.format(CACHE_KEY_TEMPLATE, identifier, userId);
    }

    /**
     * 获取对象的完整名称
     * 年/月/日/UUID.fileSuffix
     * @param fileSuffix
     * @return
     */
    private String getFilePath(String fileSuffix) {
        return DateUtil.thisYear() +
                BtboxConstants.SLASH_STR +
                (DateUtil.thisMonth() + 1) +
                BtboxConstants.SLASH_STR +
                DateUtil.thisDayOfMonth() +
                BtboxConstants.SLASH_STR +
                IdUtil.fastSimpleUUID() +
                fileSuffix;
    }
}