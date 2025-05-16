package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.Diet;

@Mapper
public interface DietDao {

	public List<Diet> selectAllDiets();

}
