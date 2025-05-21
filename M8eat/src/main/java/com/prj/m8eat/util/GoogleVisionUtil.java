package com.prj.m8eat.util;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import com.prj.m8eat.model.dto.CropBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;

public class GoogleVisionUtil {

    // 📌 객체 인식 (Object Detection)
    public static List<LocalizedObjectAnnotation> detectObjects(ImageAnnotatorClient client, ByteString imageBytes) throws IOException {
        Image image = Image.newBuilder().setContent(imageBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
        return response.getResponses(0).getLocalizedObjectAnnotationsList();
    }

    // 📌 라벨 인식 (Label Detection)
    public static List<EntityAnnotation> detectLabels(ImageAnnotatorClient client, ByteString imageBytes) throws IOException {
        Image image = Image.newBuilder().setContent(imageBytes).build();
        Feature labelFeature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(labelFeature)
                .setImage(image)
                .build();

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
        return response.getResponses(0).getLabelAnnotationsList();
    }

    // 📌 객체 박스 추출
    public static CropBox extractBox(LocalizedObjectAnnotation obj, int imgWidth, int imgHeight) {
        List<NormalizedVertex> verts = obj.getBoundingPoly().getNormalizedVerticesList();

        float xMin = verts.stream().map(NormalizedVertex::getX).min(Float::compare).orElse(0f);
        float yMin = verts.stream().map(NormalizedVertex::getY).min(Float::compare).orElse(0f);
        float xMax = verts.stream().map(NormalizedVertex::getX).max(Float::compare).orElse(1f);
        float yMax = verts.stream().map(NormalizedVertex::getY).max(Float::compare).orElse(1f);

        int x = (int) (xMin * imgWidth);
        int y = (int) (yMin * imgHeight);
        int w = (int) ((xMax - xMin) * imgWidth);
        int h = (int) ((yMax - yMin) * imgHeight);

        if (w <= 0 || h <= 0 || x < 0 || y < 0 || x + w > imgWidth || y + h > imgHeight) {
            return null;
        }

        return new CropBox(x, y, w, h);
    }

    // 📌 이미지 잘라서 ByteString으로 변환
    public static ByteString toByteString(BufferedImage image) throws IOException {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rgbImage, "jpg", baos);
        return ByteString.copyFrom(baos.toByteArray());
    }
    
    // Vision API 클라이언트 생성
    public static ImageAnnotatorClient createClient() throws Exception {
        InputStream keyStream = GoogleVisionUtil.class.getResourceAsStream("/braided-gravity-460410-r6-1e7c33f2eb8a.json"); // 키 경로
        if (keyStream == null) {
            throw new RuntimeException("📁 Vision API 키 파일을 찾을 수 없습니다.");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return ImageAnnotatorClient.create(settings);
    }
}
