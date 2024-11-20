package com.rekog.faceRekognition.service;

import com.rekog.faceRekognition.exceptionhandler.exception.EmptyImageException;
import com.rekog.faceRekognition.exceptionhandler.exception.ImageFormatNotSupportedException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class ImageService {

    public String save(MultipartFile image) throws IOException {

        if(image.isEmpty()){
            throw new EmptyImageException();
        }
        //Use current date as image file directory
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        String absolutePath = System.getProperty("user.dir") + "\\";
        String path = "images\\" + date;
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        String contentType = image.getContentType();
        String format;
        if(ObjectUtils.isEmpty(contentType)){
            throw new ImageFormatNotSupportedException();
        }
        else if(contentType.equals("image/jpeg")){
            format = ".jpg";
        }
        else if(contentType.equals("image/png")){
            format = ".png";
        }
        else{
            throw new ImageFormatNotSupportedException();
        }

        String newFileName = Long.toString(System.currentTimeMillis()) + format;
        file = new File(absolutePath + path + "/" + newFileName);
        image.transferTo(file);

        return absolutePath + path + "/" + newFileName;
    }

    public void delete(String absolutePath){
        File file = new File(absolutePath);
        if(file.exists()){
            if(file.delete()){
                System.out.println("Deleted image " + absolutePath);
            } else{
                System.out.println("Failed to delete image " + absolutePath);
            }
        }
    }
}

