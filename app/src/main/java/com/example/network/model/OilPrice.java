package com.example.network.model;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OilPrice implements Serializable {
    private String prov;
    private String p0;
    private String p95;
    private String p98;
    private String p92;

    @JSONField(name = "ct", format = "yyyy-MM-dd hh:mm:ss.SSS")
    private Date date;

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getP0() {
        return p0;
    }

    public void setP0(String p0) {
        this.p0 = p0;
    }

    public String getP95() {
        return p95;
    }

    public void setP95(String p95) {
        this.p95 = p95;
    }

    public String getP98() {
        return p98;
    }

    public void setP98(String p98) {
        this.p98 = p98;
    }

    public String getP92() {
        return p92;
    }

    public void setP92(String p92) {
        this.p92 = p92;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return prov + format.format(date) + "油价如下：" +
                "\n\t 92号：" + p92 +
                "\n\t 95号：" + p95 +
                "\n\t 98号：" + p98;
    }
}
