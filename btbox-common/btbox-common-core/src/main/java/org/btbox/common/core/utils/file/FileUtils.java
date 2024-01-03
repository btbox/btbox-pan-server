package org.btbox.common.core.utils.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.utils.DateUtils;
import org.btbox.common.core.utils.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 文件处理工具类
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils extends FileUtil {

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) {
        String percentEncodedFileName = percentEncode(realFileName);
        String contentDispositionValue = "attachment; filename=%s;filename*=utf-8''%s".formatted(percentEncodedFileName, percentEncodedFileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue);
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 通过文件大小转换文件大小的展示名称
     * @param totalSize
     * @return
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (ObjectUtil.isNull(totalSize)) {
            return BtboxConstants.EMPTY_STR;
        }
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
    }

    /**
     * 批量删除物理文件
     * @param realFilePathList
     */
    public static void deleteFiles(List<String> realFilePathList) {
        if (CollUtil.isEmpty(realFilePathList)) {
            return;
        }
        for (String realFilPath : realFilePathList) {
            del(realFilPath);
        }
    }

    /**
     * 获取文件的后缀
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(BtboxConstants.POINT_STR) == BtboxConstants.MINUS_ONE_INT) {
            return BtboxConstants.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(BtboxConstants.POINT_STR)).toLowerCase();
    }

    /**
     * 生成文件的存储路径
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return basePath +
                File.separator +
                DateUtil.thisYear() +
                File.separator +
                (DateUtil.thisMonth() + 1) +
                File.separator +
                DateUtil.thisDayOfMonth() +
                File.separator +
                IdUtil.fastSimpleUUID() +
                getFileSuffix(filename);
    }

    /**
     * 将文件的输入流写入到文件中
     * 使用底层的sendfile零拷贝来提高传输效率
     * @param inputStream
     * @param targetFile
     * @param totalSize
     */
    public static void writeStream2File(InputStream inputStream, File targetFile, Long totalSize) throws IOException {
        createFile(targetFile);
        RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
        FileChannel outputChannel = randomAccessFile.getChannel();
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        outputChannel.transferFrom(inputChannel, BtboxConstants.ZERO_LONG, totalSize);
        inputChannel.close();
        outputChannel.close();
        randomAccessFile.close();
        inputStream.close();
    }

    /**
     * 创建文件
     * 包含父文件夹一起视情况去创建
     * @param targetFile
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }

    /**
     * 生成默认文件的存储路径
     * 生成规则：当前登录用户的文件目录 + btbox-pan
     * @return
     */
    public static String generateDefaultStoreFileRealPath() {
        return System.getProperty("user.home") +
                File.separator +
                "btbox-pan";
    }

    /**
     * 生成默认的文件分片的存储路径前缀
     * @return
     */
    public static String generateDefaultStoreFileChunkRealPath() {
        return System.getProperty("user.home") +
                File.separator +
                "btbox-pan" +
                File.separator +
                "chunks";
    }

    /**
     * 生成文件分片的存储路径
     * 生成规则：基础路径 + 年 + 月 + 唯一标识 + 随机文件名称 + __,__ + 文件分片的下标
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return basePath +
                File.separator +
                DateUtil.thisYear() +
                File.separator +
                (DateUtil.thisMonth() + 1) +
                File.separator +
                DateUtil.thisDayOfMonth() +
                File.separator +
                identifier +
                File.separator +
                IdUtil.fastSimpleUUID() +
                BtboxConstants.COMMON_SEPARATOR +
                chunkNumber;
    }

    /**
     * 追加写文件
     * @param target
     * @param source
     */
    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    /**
     * 利用零拷贝技术读取文件内容并写入到文件的输出流中
     * @param fileInputStream
     * @param outputStream
     * @param length
     */
    public static void writeFile2OutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) throws IOException {
        FileChannel fileChannel = fileInputStream.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        fileChannel.transferTo(BtboxConstants.ZERO_LONG, length, writableByteChannel);
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
        fileChannel.close();
        writableByteChannel.close();
    }

    /**
     * 普通的流对流数据传输
     * @param inputStream
     * @param outputStream
     */
    public static void writeStream2StreamNormal(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != BtboxConstants.MINUS_ONE_INT) {
            outputStream.write(buffer, BtboxConstants.ZERO_INT, len);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }
}
