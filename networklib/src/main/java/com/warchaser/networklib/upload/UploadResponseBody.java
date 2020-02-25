package com.warchaser.networklib.upload;

public class UploadResponseBody {


    /**
     * id : 20
     * user_id : 1000000074
     * uuid : 2a21394320364b3faeb128e3a371c495.db
     * app_id : c3cf2837665d010ba44e0d105b031cca
     * db_url : https://res.carautocloud.com/analysis/userbehavior/c3cf2837665d010ba44e0d105b031cca/2020-02-25/1582594834721-0edb5e73-da40-4c3c.db
     */

    private int id;
    private int user_id;
    private String uuid;
    private String app_id;
    private String db_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }
}
