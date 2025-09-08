package com.gs.admin.feeder;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

public class Data implements java.io.Serializable {
    Integer id;
    String txt1;
    String txt2;
    String txt3;


    public Data() {
    }

    @SpaceId
    @SpaceRouting
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTxt1() {
        return txt1;
    }

    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    public String getTxt2() {
        return txt2;
    }

    public void setTxt2(String txt2) {
        this.txt2 = txt2;
    }

    public String getTxt3() {
        return txt3;
    }

    public void setTxt3(String txt3) {
        this.txt3 = txt3;
    }
}
