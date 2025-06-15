package com.prj.m8eat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

	    String token = extractTokenFromCookies(request);

	    if (token != null && jwtTokenProvider.validate(token)) {
	        Authentication authentication = jwtTokenProvider.getAuthentication(token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	    } else {
	    }

	    filterChain.doFilter(request, response);
	}


	// ✅ 쿠키에서 access-token 꺼내는 메서드
	private String extractTokenFromCookies(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if ("access-token".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
