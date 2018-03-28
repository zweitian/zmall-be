package com.zmall.service.impl;

import com.zmall.service.IFtpService;
import com.zmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  18:29
 */
@Service("iFtpService")
@Slf4j
public class FtpServiceImpl implements IFtpService{

    /**
     * 上传文件到Ftp服务器上,成功返回上传文件的文件名,
     * 失败返回null  上传文件缓存流-->tomcat服务器-->FTP服务器
     * @param file      上传文件缓存流
     * @param localPath  上传文件到tomcat路径
     * @param ftpPath   ftp文件路径
     * @return
     */
    @Override
    public String upload(MultipartFile file, String localPath, String ftpPath){
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        //tomcat上传文件目录判断
        File fileDir = new File(localPath);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(localPath,uploadFileName);
        //使用FTP工具类把tomcat服务器下的文件上传到ftp服务器上
        try {
            file.transferTo(targetFile);
            //使用FTP工具类把tomcat服务器下的文件上传到ftp服务器上
            FTPUtil.uploadFile(Arrays.asList(targetFile),ftpPath);
        } catch (IOException e) {
            log.error("上传文件失败,请查看日志了解详细信息");
            return null;
        }finally{
            //tomcat文件上传到ftp服务器上删除tomcat下的文件
            targetFile.delete();
        }
        return targetFile.getName();
    }
}
