package com.example.network.model;

import java.io.Serializable;

public class Ip implements Serializable {
    private int code;
    private IpData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public IpData getData() {
        return data;
    }

    public void setData(IpData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Ip{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
