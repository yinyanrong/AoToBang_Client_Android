package com.aotobang.local.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.local.db.annotation.Column;
import com.aotobang.local.db.annotation.ID;
import com.aotobang.local.db.annotation.TableName;


public abstract class DAOSupport<M> implements DAO<M> {

	// 问题一：表名的获取
	// 问题二：实体中字段数据如何设置给数据库表中列
	// 问题三：数据库表中列的数据设置给对应的实体中的字段
	// 问题四：实体中主键数据获取
	// 问题五：获取到实体对应的对象

	private static final String TAG = "DAOSupport";
	protected DBHelper helper;
	protected Context context;
	protected SQLiteDatabase database;

	public DAOSupport() {
		super();
		context=AotoBangApp.mPreLoad.appContext;
		helper = new DBHelper(context);
		database = helper.getWritableDatabase();
	}

	@Override
	public long insert(M m) {
		ContentValues values = new ContentValues();
		fillContentValues(m, values);// 将m上的数据添加到valuse，数据来源：第一参数；设置目标：第二个参数
		return database.insert(getTableName(), null, values);
	}

	@Override
	public int update(M m) {
		ContentValues values = new ContentValues();
		fillContentValues(m, values);

		return database.update(getTableName(), values, DBHelper.TABLE_ID + "=?", new String[] { getId(m) });
	}

	@Override
	public int delete(Serializable id) {
		return database.delete(getTableName(), DBHelper.TABLE_ID + "=?", new String[] { id + "" });
	}

	@Override
	public List<M> findAll() {
		List<M> result = null;// List<M>

		Cursor cursor = database.query(getTableName(), null, null, null, null, null, null);
		if (cursor != null) {
			result = new ArrayList<M>();
			while (cursor.moveToNext()) {
				M m = getInstance();

				fillFields(cursor, m);

				result.add(m);

			}

			cursor.close();
			return result;
		}

		return null;
	}

	public List<M> findByCondition(String selection, String[] selectionArgs, String orderBy) {
		return findByCondition(null, selection, selectionArgs, null, null, orderBy);
	}

	public List<M> findByCondition(String[] columns, String selection, String[] selectionArgs, String orderBy) {
		return findByCondition(columns, selection, selectionArgs, null, null, orderBy);
	}

	public List<M> findByCondition(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {

		List<M> result = null;// List<M>

		Cursor cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor != null) {
			result = new ArrayList<M>();
			while (cursor.moveToNext()) {
				M m = getInstance();

				fillFields(cursor, m);

				result.add(m);

			}

			cursor.close();
			return result;
		}

		return null;

	}

	/**
	 * 问题一：表名的获取
	 */
	public String getTableName() {
		M m = getInstance();
		TableName tableName = m.getClass().getAnnotation(TableName.class);
		if (tableName != null) {
			return tableName.value();
		}
		return "";
	}

	/**
	 * 问题二：实体中字段数据如何设置给数据库表中列
	 */
	public void fillContentValues(M m, ContentValues values) {
		// values.put(DBHelper.TABLE_NEWS_TITLE, news.getTitle());
		// 此处省略n行代码

		// 实体和表对应关系：TableName注解
		// 解决实体中字段与表中列对应关系

		// 循环M身上所有Field信息，如果在Field上有Column信息，需要将该数据添加到数据库中

		// m.getClass().getFields();//public fields
		Field[] fields = m.getClass().getDeclaredFields();// all fields

		for (Field item : fields) {
			item.setAccessible(true);// 暴力反射

			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				String key = column.value();// object.fieldName --object.title
				try {
					// Sqlite数据库中数据存储：弱数据类型
					// TODO 主键+自增
					Object obj=item.get(m);
					if(obj!=null){
					String value =obj.toString();
					ID id = item.getAnnotation(ID.class);
					if (id != null) {
						if (id.autoincrement()) {
							// 主键+自增
						} else {
							values.put(key, value);
						}
					} else {
						values.put(key, value);
					}

					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 问题三：数据库表中列的数据设置给对应的实体中的字段
	 * 
	 * @param cursor
	 * @param m
	 */
	protected void fillFields(Cursor cursor, M m) {
		// int columnIndex = cursor.getColumnIndex(DBHelper.TABLE_NEWS_TITLE);
		// String title = cursor.getString(columnIndex);
		// item.setTitle(title);
		// 此处省略n行代码

		Field[] fields = m.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				int columnIndex = cursor.getColumnIndex(column.value());

				// 主键+自增 int,long
				Object value = null;
				if (field.getType() == int.class) {
					value = cursor.getInt(columnIndex);
				} else if (field.getType() == long.class) {
					value = cursor.getLong(columnIndex);
				} else {
					value = cursor.getString(columnIndex);
				}

				try {
					field.set(m, value);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * 问题四：实体中主键数据获取
	 * 
	 * @param m
	 * @return
	 */
	private String getId(M m) {
		// 找field有ID注解
		Field[] fields = m.getClass().getDeclaredFields();

		for (Field item : fields) {
			item.setAccessible(true);
			ID id = item.getAnnotation(ID.class);
			if (id != null) {
				try {
					return item.get(m).toString();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return "";
	}

	/**
	 * 问题五：获取到实体对应的对象
	 * 
	 * @return
	 */
	private M getInstance() {
		// 何时确定M
		// 子类中通过extends DAOSupport<M>
		// ①获取子类:在运行时候实际在跑的那个子类
		Class clazz = super.getClass();
		Log.i(TAG, clazz.getName());
		// ②获取该类的父类（普通），获取到支持泛型的父类
		// Class superclass = clazz.getSuperclass();//class cn.ithm.dbhm25.dao.base.DAOSupport
		Type type = clazz.getGenericSuperclass();// cn.ithm.dbhm25.dao.base.DAOSupport<cn.ithm.dbhm25.dao.domain.News>
		// JDK会让泛型实现一个接口：参数化类型
		if (type instanceof ParameterizedType) {
			Class target = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];// [class cn.ithm.dbhm25.dao.domain.News]
			try {
				return (M) target.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// ③获取到支持泛型的父类中泛型的参数信息

		return null;
	}
}
