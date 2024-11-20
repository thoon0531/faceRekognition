package com.rekog.faceRekognition.controller;


import com.rekog.faceRekognition.service.FaceService;
import com.rekog.faceRekognition.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FaceController {

    @Autowired
    FaceService faceService;
    @Autowired
    ImageService imageService;

    @PostMapping
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> faceProcess(
            @RequestPart(required = false) MultipartFile image) throws Exception{
        Map<String, Object> response;

        String imagepath = imageService.save(image);
        response = faceService.faceProcess(imagepath);
        imageService.delete(imagepath);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }


}
