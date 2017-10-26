package cn.summerwaves.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QiNiuUtil {

    protected static Logger logger = Logger.getLogger(QiNiuUtil.class);
    private static String ACCESS_KEY;
    private static String SECRET_KEY;
    private static String bucketName;


    public static void upLoad(InputStream inputStream, String fileName) {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String token = auth.uploadToken(bucketName);
        boolean isException = false;
        Zone z = Zone.autoZone();
        Configuration c = new Configuration(z);
        UploadManager uploadManager = new UploadManager(c);
        try {
/*            InputStream inputStream = new FileInputStream(filePath);*/
            Response res = uploadManager.put(inputStream, fileName, token, null, null);
        } catch (QiniuException e) {
            Response r = e.response;
            isException = true;
            logger.error(r.toString());
            try {
                logger.error(r.bodyString());
            } catch (QiniuException el) {
            }
        } finally {
            if (!isException) {
                logger.info("Uploaded successfully");
            }
        }
    }

    public static InputStream downLoad(String fileName) {
        try {
            /*String fileName = "七牛/云存储/qiniu.jpg";*/
            String domainOfBucket = "http://img.summerwaves.cn";
            String encodedFileName = URLEncoder.encode(fileName, "utf-8");
            String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
            System.out.println(finalUrl);
            return new URL(finalUrl).openStream();
        } catch (MalformedURLException urle) {
            logger.error("QiNiuYun throw a MalformedURLException");
        } catch (IOException IOe) {
            logger.error("QiNiuYun throw a IOException");
        }
        return null;
    }

    public static List<String> getAllFileName() {
        List<String> keys = new ArrayList<>();
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        Configuration cfg = new Configuration(Zone.zone0());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucketName, prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                keys.add(item.key);
//                System.out.println(item.key);
//                System.out.println(item.hash);
//                System.out.println(item.fsize);
//                System.out.println(item.mimeType);
//                System.out.println(item.putTime);
//                System.out.println(item.endUser);
            }
        }
        return keys;
    }


    public void setAccessKey(String accessKey) {
        ACCESS_KEY = accessKey;
    }

    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public void setBucketName(String bucketName) {
        QiNiuUtil.bucketName = bucketName;
    }

    public static String getFileUrl(String fileName) {
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format("%s/%s", "http://img.summerwaves.cn", encodedFileName);
    }
}
