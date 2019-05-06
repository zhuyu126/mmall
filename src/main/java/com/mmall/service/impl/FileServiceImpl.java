package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger=LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName=file.getOriginalFilename();
        String extensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+extensionName;
        logger.info("开始上传文件，上传文件的文件名为：{},上传路径为：{}，新文件名为：{}",fileName,path,uploadFileName);


        File file1=new File(path);
        if(!file1.exists()){
            file1.setWritable(true);
            file1.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);
        try {
            //将图片上传到本地指定路径，上传成功(写到本地磁盘比较快)
            file.transferTo(targetFile);
            //todo 将文件上传到ftp服务器(比较慢)
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //todo 删除本地tomcat上图片文件
            targetFile.delete();
        } catch (IOException e) {
           logger.error("文件上传异常",e);
           return null;
        }
        return targetFile.getName();
    }
}
