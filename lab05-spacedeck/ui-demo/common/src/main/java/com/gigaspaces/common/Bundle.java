package com.gigaspaces.common;

import com.gigaspaces.annotation.pojo.SpaceId;


public class Bundle {

    private String id;
    private String name;
    private byte[] bytes;


    public Bundle() {
    }

    public static Bundle createBundle() {
        Bundle template = new Bundle();
        template.setBytes(new byte[1000]);
        return template;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}


