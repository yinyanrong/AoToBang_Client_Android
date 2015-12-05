package com.aotobang.local.dao;

import android.content.ContentValues;

import com.aotobang.local.bean.Photo;
import com.aotobang.local.db.DAOSupport;
import com.aotobang.local.db.DBHelper;

/**
 * Created by Admin on 2015/7/21.
 */
public class PhotoDao extends DAOSupport<Photo> {
    @Override
    public int  update(Photo p){
        ContentValues values=new ContentValues();
        values.put(DBHelper.TABLE_PHOTO_LOCAL_URL,p.getLocal_url());
        values.put(DBHelper.TABLE_PHOTO_REMOTE_URL,p.getRemote_url());
       return database.update(getTableName(), values, DBHelper.TABLE_PHOTO_INDEX + "=?", new String[]{p.getPhoto_index() + ""});
    }



}
