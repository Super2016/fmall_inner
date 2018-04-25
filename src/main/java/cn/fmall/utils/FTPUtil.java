package cn.fmall.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FTPUtil {
    private static String ftpServerIp = PropertiesUtil.getValueByKey("ftp.server.ip");
    private static String ftpUsername = PropertiesUtil.getValueByKey("ftp.user");
    private static String ftpPassword = PropertiesUtil.getValueByKey("ftp.password");

    private String  ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    public FTPUtil(String ip,int port,String user,String password){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    //上传文件具体逻辑
    private boolean uploadingFile(String remotePath,List<File> fileList){
        boolean isUpdaloaded = true;
        FileInputStream fileInputStream = null;

        //连接FTPServer
        if (connectFTPServerIsOk(this.ip,this.port,this.user,this.password)) {
            //上传文件
            try {
                //是否需要切换文件夹,如remotePath为空则默认不切换
                ftpClient.changeWorkingDirectory(remotePath);
                //缓冲大小
                ftpClient.setBufferSize(1024);
                //编码
                ftpClient.setControlEncoding("UTF-8");
                //文件类型,设置为二进制文件防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开本地被动模式
                ftpClient.enterLocalPassiveMode();
                //文件上传开始
                for (File fileItem : fileList) {
                    //
                    fileInputStream = new FileInputStream(fileItem);
                    //存储文件
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);

                }
            } catch (IOException e) {
                logger.error("上传文件失败");
                isUpdaloaded = false;
                e.printStackTrace();
            } finally {
                //释放文件流
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //关闭连接
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isUpdaloaded;
    }

    //连接FTPServer操作
    private boolean connectFTPServerIsOk(String ip,int port,String user,String password){
        FTPClient ftpClient = new FTPClient();
        boolean isSuccess = false;
        //连接FTP服务器
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,password);

        } catch (IOException e) {
            logger.error("连接FTP服务器异常:"+e);
        }
        return isSuccess;
    }

    //判断文件上传成功与否
    public static boolean uploadFile(List<File> fileList){
        FTPUtil ftpUtil = new FTPUtil(ftpServerIp,21,ftpUsername,ftpPassword);
        logger.info("开始连接ftp服务器");
        boolean fileIsUploaded = ftpUtil.uploadingFile("img",fileList);
        logger.info("结束上传,上传结果:{}");
        return fileIsUploaded;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
