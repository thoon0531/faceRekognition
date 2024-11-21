package com.rekog.faceRekognition.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FaceService {

    public Map<String, Object> faceProcess(String imagePath) throws Exception {
        Map<String, Object> faceResult = new HashMap<>();
        String collectionId = "mycollection";

        Region region = Region.AP_NORTHEAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(SystemPropertyCredentialsProvider.create())
                .build();


        System.out.println("=========== Creating collection: " + collectionId);
        try{
            createMyCollection(rekClient, collectionId);
        }
        catch (ResourceAlreadyExistsException e){
            System.out.println("Collection already exists: " + collectionId);
        }

        System.out.println("=========== Searching for a face in a collections");
        List<FaceMatch> faceImageMatches = searchFaceInCollection(rekClient, collectionId, imagePath);

        if(faceImageMatches == null){
            System.out.println("=========== Add face to a collection");
            List<FaceRecord> faceRecords = addToCollection(rekClient, collectionId, imagePath);
            faceResult.put("faceid", faceRecords.get(0).face().faceId());
            faceResult.put("ageHigh", faceRecords.get(0).faceDetail().ageRange().high());
            faceResult.put("ageLow", faceRecords.get(0).faceDetail().ageRange().low());
            faceResult.put("gender", faceRecords.get(0).faceDetail().gender().value().toString());
        }
        else{ //얼굴 여러개 나오면? 일단은 첫번쨰 얼굴만 주는걸로 구현
            faceResult.put("faceid", faceImageMatches.get(0).face().faceId());
        }

        rekClient.close();

        return faceResult;
    }

    public static void createMyCollection(RekognitionClient rekClient, String collectionId) throws RekognitionException {

        CreateCollectionRequest collectionRequest = CreateCollectionRequest.builder()
                .collectionId(collectionId)
                .build();

        CreateCollectionResponse collectionResponse = rekClient.createCollection(collectionRequest);
        System.out.println("CollectionArn: " + collectionResponse.collectionArn());
        System.out.println("Status code: " + collectionResponse.statusCode().toString());
    }

    public static List<FaceRecord> addToCollection(RekognitionClient rekClient, String collectionId, String sourceImage) throws RekognitionException, FileNotFoundException, IOException {
        InputStream sourceStream = new FileInputStream(sourceImage);
        SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);
        Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

        IndexFacesRequest facesRequest = IndexFacesRequest.builder()
                .collectionId(collectionId)
                .image(souImage)
                .maxFaces(1)
                .qualityFilter(QualityFilter.AUTO)
                .detectionAttributes(Attribute.DEFAULT, Attribute.AGE_RANGE, Attribute.GENDER)
                .build();

        IndexFacesResponse facesResponse = rekClient.indexFaces(facesRequest);
        System.out.println("Results for the image");
        System.out.println("\n Faces indexed:");
        List<FaceRecord> faceRecords = facesResponse.faceRecords();
        for (FaceRecord faceRecord : faceRecords) {
            System.out.println("  Face ID: " + faceRecord.face().faceId());
            System.out.print("  Age: " + faceRecord.faceDetail().ageRange().low());
            System.out.print(" ~ " + faceRecord.faceDetail().ageRange().high());
            System.out.println();
            System.out.println("  Gender: " + faceRecord.faceDetail().gender());
            System.out.println("  Location:" + faceRecord.faceDetail().boundingBox().toString());
        }

        List<UnindexedFace> unindexedFaces = facesResponse.unindexedFaces();
        System.out.println("Faces not indexed:");
        for (UnindexedFace unindexedFace : unindexedFaces) {
            System.out.println("  Location:" + unindexedFace.faceDetail().boundingBox().toString());
            System.out.println("  Reasons:");
            for (Reason reason : unindexedFace.reasons()) {
                System.out.println("Reason:  " + reason);
            }
        }
        sourceStream.close();
        return faceRecords;
    }

    public static List<FaceMatch> searchFaceInCollection(RekognitionClient rekClient,String collectionId, String sourceImage) throws RekognitionException, FileNotFoundException, IOException {
        List<FaceMatch> faceImageMatches = null;
        InputStream sourceStream = new FileInputStream(new File(sourceImage));
        SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);
        Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

        SearchFacesByImageRequest facesByImageRequest = SearchFacesByImageRequest.builder()
                .image(souImage)
                .maxFaces(10)
                .faceMatchThreshold(80F)
                .collectionId(collectionId)
                .build();

        SearchFacesByImageResponse imageResponse = rekClient.searchFacesByImage(facesByImageRequest) ;
        faceImageMatches = imageResponse.faceMatches();
        if(faceImageMatches.isEmpty()){
            System.out.println("No faces matching in the collection");
            return null;
        }

        System.out.println("Faces matching in the collection");
        for (FaceMatch face: faceImageMatches) {
            System.out.println("The similarity level is  "+face.similarity());
            System.out.println("Face ID is  "+face.face().faceId());
            System.out.println();
        }
        sourceStream.close();
        return faceImageMatches;
    }
}
