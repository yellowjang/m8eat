package com.prj.m8eat.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;

public class GoogleTranslateUtil {

    private static Translate translate;

    static {
        try {
            // JSON 키 파일 로드 (클래스패스 기준 경로로 수정 가능)
            InputStream credentialsStream = GoogleTranslateUtil.class.getResourceAsStream("/braided-gravity-460410-r6-1e7c33f2eb8a.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-translation");

            translate = TranslateOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String translateToKorean(String text) {
        if (translate == null) return text; // fallback
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage("ko"),
                Translate.TranslateOption.model("nmt") // 신경망 번역 모델
        );
        return translation.getTranslatedText();
    }

    // 예시: String translated = GoogleTranslateUtil.translateToKorean("Garlic bread");
}
