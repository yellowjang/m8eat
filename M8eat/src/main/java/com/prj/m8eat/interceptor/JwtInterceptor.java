package com.prj.m8eat.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.prj.m8eat.jwt.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
//		System.out.println("🟡 인터셉터 진입: " + request.getRequestURI());

		if (request.getMethod().equals("OPTIONS")) { 
			return true;
		}
		
		String token = getTokenFromCookie(request);
//		System.out.println("prehandleeeeeeeeeeee " + token);
		
		if (token != null && jwtUtil.validate(token)) {
			return true;
		}
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false;
	}

	private String getTokenFromCookie(HttpServletRequest request) {
		
		if (request.getCookies() == null) return null;
		
		for (Cookie cookie : request.getCookies()) {
			if ("access-token".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		
		return null;
	}
}
