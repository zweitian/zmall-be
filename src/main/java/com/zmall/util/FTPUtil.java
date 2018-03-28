package com.zmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  18:44
 */
public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器进行文件上传");
        boolean uploadResult = ftpUtil.uploadFileToFtp("img",fileList);
        logger.info("上传结束,上传结果为:{}",uploadResult);
        return uploadResult;
    }
    public static boolean uploadFile(List<File> fileList,String remotePath) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器进行文件上传");
        boolean uploadResult = ftpUtil.uploadFileToFtp(remotePath,fileList);
        logger.info("上传结束,上传结果为:{}",uploadResult);
        return uploadResult;
    }

    private boolean uploadFileToFtp(String remotePath,List<File> fileList) throws IOException {
        boolean uploadResult = false;
        FileInputStream fis = null;
        // 连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                // 切换上传文件目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                //开始进行文件上传
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    //文件上传到FTP服务器
                    //参数一:文件上传到ftp服务器后的文件名
                    //参数二:文件的输入流
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                uploadResult=true;
            } catch (IOException e) {
                // 日志保留现场信息与异常堆栈信息
                logger.error("上传文件过程发生异常_"+e.getMessage(),e);
                throw e;
            } finally {
                if(fis!=null){
                    fis.close();
                }
                if(ftpClient!=null){
                    ftpClient.disconnect();
                }
            }
        }
        return uploadResult;
    }

    private boolean connectServer(String ip,int port,String user,String pwd) throws IOException{
        boolean connectSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            connectSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常,请检查FTP服务器状态",e);
            throw e;
        }
        return connectSuccess;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
