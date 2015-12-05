package com.aotobang.local.db;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库操作通用接口
 * 
 * @author Administrator
 * 
 */
public interface DAO<M> {
	/**
	 * 添加
	 * 
	 * @param m
	 *            ：通用实体
	 * @return
	 */
	long insert(M m);

	/**
	 * 更新
	 * 
	 * @param m
	 * @return
	 */
	int update(M m);

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	int delete(Serializable id);// int long String

	/**
	 * 获取所有
	 * 
	 * @return
	 */
	List<M> findAll();

	/**
	 * 依据条件查询
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	List<M> findByCondition(String selection, String[] selectionArgs, String orderBy);
}
