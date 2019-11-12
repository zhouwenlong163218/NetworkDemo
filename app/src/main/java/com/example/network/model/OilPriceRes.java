package com.example.network.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class OilPriceRes implements Serializable {
    @JSONField(name = "showapi_res_code")
    private int resCode;
    @JSONField(name = "showapi_res_body")
    private OilPriceBody resBody;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public OilPriceBody getResBody() {
        return resBody;
    }

    public void setResBody(OilPriceBody resBody) {
        this.resBody = resBody;
    }
}