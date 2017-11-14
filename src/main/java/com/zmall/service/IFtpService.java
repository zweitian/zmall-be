package com.zmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  18:29
 */
public interface IFtpService {
    /**
     *
     * @param file      上传文件缓存流
     * @param localPath  上传文件到tomcat路径
     * @return
     */
    String upload(MultipartFile file, String localPath,String ftpPath);
}
