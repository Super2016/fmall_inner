package cn.fmall.service.impl;

import cn.fmall.service.IFileService;
import cn.fmall.utils.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 文件上传服务
     * @param file
     * @param path
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file,String path){
        //需要上传的完整原始文件名
        String fileName = file.getOriginalFilename();
        //原始文件的扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //新文件名,用UUID使文件唯一化
        String uploadFileName = UUID.randomUUID().toString()+fileExtensionName;

        logger.info("上传文件>>{}上传的文件名为{},上传的路径是{},新文件名是{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //目标文件,路径+文件名
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //将target文件上传到ftp服务器
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传完成后删除upload文件夹下的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("文件上传异常"+e);
        }
        return targetFile.getName();
    }
}
