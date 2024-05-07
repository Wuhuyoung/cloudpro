package com.cloud.pro.core.utils;

import cn.hutool.core.date.DateUtil;
import com.cloud.pro.core.constants.CommonConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * 文件相关工具类
 */
public class FileUtil {
    /**
     * 获取文件后缀
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) ||
                filename.indexOf(CommonConstants.POINT_STR) == CommonConstants.MINUS_ONE_INT) {
            return StringUtils.EMPTY;
        }
        return filename.substring(filename.lastIndexOf(CommonConstants.POINT_STR)).toLowerCase();
    }

    /**
     * 获取文件类型
     *
     * @param filename
     * @return
     */
    public static String getFileExtName(String filename) {
        if (StringUtils.isBlank(filename) ||
                filename.indexOf(CommonConstants.POINT_STR) == CommonConstants.MINUS_ONE_INT) {
            return StringUtils.EMPTY;
        }
        return filename.substring(filename.lastIndexOf(CommonConstants.POINT_STR) + CommonConstants.ONE_INT).toLowerCase();
    }

    /**
     * 将文件大小转化为文件大小的展示名称
     *
     * @param totalSize
     * @return
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return StringUtils.EMPTY;
        }
        return FileUtils.byteCountToDisplaySize(totalSize);
    }

    /**
     * 生成文件的存储路径
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     *
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename))
                .toString();
    }

    /**
     * 将文件的输入流写入到文件中
     *
     * @param inputStream
     * @param targetFile
     * @param totalSize
     * @throws IOException
     */
    public static void writeStream2File(InputStream inputStream, File targetFile, Long totalSize) throws IOException {
        createFile(targetFile);
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
                FileChannel outputChannel = randomAccessFile.getChannel();
                ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
            // 底层会调用零拷贝技术（如果当前操作系统有提供该系统调用）
            outputChannel.transferFrom(inputChannel, 0L, totalSize);
        }
        inputStream.close();
    }

    /**
     * 创建文件，如果父文件夹不存在会一起创建
     *
     * @param targetFile
     * @throws IOException
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }

    /**
     * 批量删除物理文件
     * @param realFilePathList
     * @throws IOException
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if (CollectionUtils.isEmpty(realFilePathList)) {
            return;
        }
        for (String realFilePath : realFilePathList) {
            FileUtils.forceDelete(new File(realFilePath));
        }
    }

    /**
     * 生成默认的文件存储路径前缀
     * 生成规则：当前登录用户的文件目录 + cloudpro
     * @return
     */
    public static String generateDefaultStoreFileRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("cloudpro")
                .toString();
    }

    /**
     * 生成默认的文件分片存储路径前缀
     * @return
     */
    public static String generateDefaultStoreFileChunkRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("cloudpro")
                .append(File.separator)
                .append("chunks")
                .toString();
    }

    /**
     * 生成文件的存储路径
     * 生成规则：基础路径 + 年 + 月 + 日 + 唯一标识 + 随机的文件名称 + __,__ + 文件分片的下标
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(CommonConstants.COMMON_SEPARATOR)
                .append(chunkNumber)
                .toString();
    }

    /**
     * 追加写文件
     * @param target
     * @param source
     * @throws IOException
     */
    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    /**
     * 利用零拷贝技术读取文件内容写入到输出流中
     * @param fileInputStream
     * @param outputStream
     * @param length
     * @throws IOException
     */
    public static void writeFile2OutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) throws IOException {
        try (
                FileChannel inputChannel = fileInputStream.getChannel();
                WritableByteChannel writableByteChannel = Channels.newChannel(outputStream)) {
            // 底层会调用零拷贝技术（如果当前操作系统有提供该系统调用）
            inputChannel.transferTo(0L, length, writableByteChannel);
            outputStream.flush();
        }
        fileInputStream.close();
        outputStream.close();
    }

    /**
     * 获取文件的 content-type
     * @param filePath
     * @return
     */
    public static String getContentType(String filePath) {
        //利用nio提供的类判断文件ContentType
        File file = new File(filePath);
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //若失败则调用另一个方法进行判断
        if (StringUtils.isBlank(contentType)) {
            contentType = new MimetypesFileTypeMap().getContentType(file);
        }
        return contentType;
    }

    /**
     * 普通的流对流数据传输
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void writeStream2Stream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != CommonConstants.MINUS_ONE_INT) {
            outputStream.write(buffer, CommonConstants.ZERO_INT, len);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }
}
