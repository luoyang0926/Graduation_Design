package com.xy.blog.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.service.UploadService;
import com.xy.blog.utils.PathUtils;
import com.xy.blog.utils.ResponseResult;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class UploadServiceImpl implements UploadService {


    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //判断文件类型
        //获取文件原始名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if (!originalFilename.endsWith(".png")&&!originalFilename.endsWith(".jpg")) {
            throw new SystemException(AppHttpCodeEnum.FiLE_TYPE_ERRPR);
        }
        String filePath = PathUtils.generateFilePath(originalFilename);
        System.out.println("===================>"+filePath);
        String url = uploadOss(img, filePath);
        return ResponseResult.okResult(url);

    }

    private String accessKey;
    private String secretKey;
    private String bucket;


    public String uploadOss(MultipartFile imgFile,String filePath) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
//        String accessKey = "your access key";
//        String secretKey = "your secret key";
//        String bucket = "sg-blog";

        //默认不指定key的情况下，以文件内容的hash值作为文件名
         String key =filePath;

        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);


            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return "http://rqiz2rnut.hb-bkt.clouddn.com/"+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return "www";
    }
}
