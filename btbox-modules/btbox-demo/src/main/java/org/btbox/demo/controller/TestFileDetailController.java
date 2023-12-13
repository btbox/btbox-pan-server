package org.btbox.demo.controller;


import lombok.RequiredArgsConstructor;
import org.btbox.common.core.domain.R;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.ProgressListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/demo/file")
public class TestFileDetailController {


    // 注入实列
    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     * @param file
     */
    @PostMapping("/upload")
    public R<FileInfo> upload(MultipartFile file) {
        return R.ok(fileStorageService.of(file)
                .upload());
    }
    
    /**
     * 上传文件,自定义参数
     */
    @PostMapping("/upload2")
    public R<FileInfo> upload2(MultipartFile file) {
        return R.ok(fileStorageService.of(file)
                .setPath("upload/") // 保存到相对路径下，为了方便管理，不需要可以不写
                .setObjectId("0")   // 关联对象id，为了方便管理，不需要可以不写
                .setObjectType("0") // 关联对象类型，为了方便管理，不需要可以不写
                .putAttr("role","admin") // 保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                .upload());  // 将文件上传到对应地方
    }

    /**
     * 上传图片，成功返回文件信息
     * 图片处理使用的是 https://github.com/coobird/thumbnailator
     */
    @PostMapping("/upload-image")
    public R<FileInfo> uploadImage(MultipartFile file) {
        return R.ok(fileStorageService.of(file)
                .image(img -> img.size(1000,1000))  // 将图片大小调整到 1000*1000
                .thumbnail(th -> th.size(200,200))  // 再生成一张 200*200 的缩略图
                .upload());
    }

    /**
     * 上传文件到指定存储平台，成功返回文件信息
     */
    @PostMapping("/upload-platform")
    public R<FileInfo> uploadPlatform(MultipartFile file) {
        return R.ok(fileStorageService.of(file)
                .setPlatform("aliyun-oss-1")    // 使用指定的存储平台
                .upload());
    }

    @PostMapping("/upload-listener")
    public R<FileInfo> uploadListener(MultipartFile file) {
        return R.ok(fileStorageService.of(file).setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                System.out.println("上传开始");
            }

            @Override
            public void progress(long progressSize,long allSize) {
                System.out.println("已上传 " + progressSize + " 总大小" + allSize);
            }

            @Override
            public void finish() {
                System.out.println("上传结束");
            }
        }).upload());
    }


    /**
     * 下载文件
     * @param url
     */
    @PostMapping("/download")
    public void download(String url) {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        fileStorageService.download(fileInfo).file("D:\\Temp\\" + fileInfo.getOriginalFilename());
    }

    /**
     * 监听下载
     * @param url  文件存储的路径 local-plus/1.png
     */
    @PostMapping("/download-listener")
    public void downloadListener(String url) {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        fileStorageService.download(fileInfo).setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                System.out.println("下载开始");
            }

            @Override
            public void progress(long progressSize,long allSize) {
                System.out.println("已下载 " + progressSize + " 总大小" + allSize);
            }

            @Override
            public void finish() {
                System.out.println("下载结束");
            }
        }).file("D:\\Temp\\" + fileInfo.getOriginalFilename());
    }


    /**
     * 刪除文件
     * @param url
     * @return
     */
    @DeleteMapping("delete")
    public R<Void> delete(String url) {
        //获取文件信息
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);

        //直接删除
        boolean delete = fileStorageService.delete(fileInfo);
        //直接通过文件信息中的 url 判断文件是否存在，省去手动查询文件信息记录的过程
        // boolean delete2 = fileStorageService.delete("test/a.jpg");
        if (!delete) {
           return R.fail();
        }
        return R.ok();
    }

    /**
     * 判斷文件是否存在
     * @param url
     * @return
     */
    @GetMapping("exists")
    public R<Boolean> exists(String url) {
        //获取文件信息
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);

        //直接删除
        boolean exists = fileStorageService.exists(fileInfo);
        //直接通过文件信息中的 url 判断文件是否存在，省去手动查询文件信息记录的过程
        // boolean exists2 = fileStorageService.exists("test/a.jpg");
        return R.ok(exists);
    }

}
