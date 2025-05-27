package com.prj.m8eat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.prj.m8eat.model.dao")
public class M8eatApplication {

	public static void main(String[] args) {
		SpringApplication.run(M8eatApplication.class, args);
	}

}
