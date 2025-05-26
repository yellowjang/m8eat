package com.prj.m8eat.config;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.prj.m8eat.interceptor.JwtInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private JwtInterceptor interceptor;
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 전체 경로에 대해
                .allowedOrigins("http://localhost:5173") // Vue dev 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // withCredentials 사용 시 true
    }
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor).addPathPatterns("/**")
											.excludePathPatterns("/", "/auth/**", "/oauth/**",
																"/css/**", "/js/**",
																"/swagger-ui/**", "/v3/api-docs/**");
	}
	
    @Value("${file.upload.dir}")
    private String uploadPath; // 예: C:/Users/kmj/Desktop/SSAFY/imgs

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /upload/** 로 들어온 요청을 실제 디렉토리로 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///" + uploadPath + "/");
    }
=======
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/upload/**")
	    .addResourceLocations("file:/Users/jang-ayoung/Desktop/m8eat/M8eat/upload/");
	}
>>>>>>> d0a9c0514aec37fe596da79675549d3ec0c2ddfe
}
