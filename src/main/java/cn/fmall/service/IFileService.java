package cn.fmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    //上传文件服务
    public String uploadFile(MultipartFile file, String path);
}
