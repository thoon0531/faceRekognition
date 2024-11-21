package com.rekog.faceRekognition.exceptionhandler;

import com.rekog.faceRekognition.exceptionhandler.exception.EmptyImageException;
import com.rekog.faceRekognition.exceptionhandler.exception.ImageFormatNotSupportedException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmptyImageException.class)
    protected ResponseEntity<?> handleEmptyImageException(EmptyImageException e) {
        Map<String, Object> errorResponse = new HashMap<String, Object>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ImageFormatNotSupportedException.class)
    protected ResponseEntity<?> handleImageFormatNotSupportedException(ImageFormatNotSupportedException e) {
        Map<String, Object> errorResponse = new HashMap<String, Object>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(FileUploadException.class)
    protected ResponseEntity<?> handleFileUploadException(FileUploadException e) {
        Map<String, Object> errorResponse = new HashMap<String, Object>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> errorResponse = new HashMap<String, Object>();
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }


}
