package com.prj.m8eat.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.api.gax.core.FixedCredentialsProvider;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import com.prj.m8eat.model.dto.CropBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class GoogleVisionUtil {

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    @PostConstruct
    public void init() {
        System.out.println("✅ GOOGLE_APPLICATION_CREDENTIALS set: " + credentialsPath);
    }

    // 1. 클라이언트 생성
    public ImageAnnotatorClient createClient() throws IOException {
    	Resource resource = new ClassPathResource("braided-gravity-460410-r6-1e7c33f2eb8a.json");
        GoogleCredentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream(credentialsPath))
                .fromStream(resource.getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return ImageAnnotatorClient.create(settings);
    }

    // 2. 객체 감지
    public List<LocalizedObjectAnnotation> detectObjects(ImageAnnotatorClient client, ByteString imageBytes) throws IOException {
        Image img = Image.newBuilder().setContent(imageBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(img)
                .build();

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
        return response.getResponses(0).getLocalizedObjectAnnotationsList();
    }

    // 3. 라벨 감지
    public List<EntityAnnotation> detectLabels(ImageAnnotatorClient client, ByteString imageBytes) throws IOException {
        Image img = Image.newBuilder().setContent(imageBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(img)
                .build();

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
        return response.getResponses(0).getLabelAnnotationsList();
    }

    // 4. 박스 추출
    public static CropBox extractBox(LocalizedObjectAnnotation obj, int width, int height) {
        if (obj.getBoundingPoly().getNormalizedVerticesCount() < 2) return null;

        float x1 = obj.getBoundingPoly().getNormalizedVertices(0).getX();
        float y1 = obj.getBoundingPoly().getNormalizedVertices(0).getY();
        float x2 = obj.getBoundingPoly().getNormalizedVertices(2).getX();
        float y2 = obj.getBoundingPoly().getNormalizedVertices(2).getY();

        int x = (int) (x1 * width);
        int y = (int) (y1 * height);
        int w = (int) ((x2 - x1) * width);
        int h = (int) ((y2 - y1) * height);

        return new CropBox(x, y, w, h);
    }

    // 5. BufferedImage → ByteString 변환
//    public static ByteString toByteString(BufferedImage image) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", baos);
//        return ByteString.copyFrom(baos.toByteArray());
//    }
    public static ByteString toByteString(BufferedImage image) throws IOException {
        if (image == null) return ByteString.EMPTY;

        // 💡 TYPE_INT_RGB로 변환
        BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        converted.getGraphics().drawImage(image, 0, 0, null);
        
//        ImageIO.write(converted, "jpg", new File("C:/SSAFY/m8eat/test_crop_" + System.currentTimeMillis() + ".jpg"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean success = ImageIO.write(converted, "jpg", baos);

        if (!success || baos.size() == 0) {
            System.out.println("❌ ImageIO.write 실패 또는 결과 비어있음");
            return ByteString.EMPTY;
        }

        return ByteString.copyFrom(baos.toByteArray());
    }


}
