package com.aotobang.local.bean;

import com.aotobang.local.db.DBHelper;
import com.aotobang.local.db.annotation.Column;
import com.aotobang.local.db.annotation.TableName;

/**
 * Created by Admin on 2015/7/21.
 */
@TableName(DBHelper.TABLE_PHOTO)
public class Photo {
    @Column(DBHelper.TABLE_PHOTO_REMOTE_URL)
    private String remote_url;
    @Column(DBHelper.TABLE_PHOTO_LOCAL_URL)
    private String local_url;
    @Column(DBHelper.TABLE_PHOTO_INDEX)
    private int photo_index;
    public int getPhoto_index() {
        return photo_index;
    }

    public void setPhoto_index(int photo_index) {
        this.photo_index = photo_index;
    }



    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        this.local_url = local_url;
    }



    public String getRemote_url() {
        return remote_url;
    }

    public void setRemote_url(String remote_url) {
        this.remote_url = remote_url;
    }


}
