package com.gigaspaces.dev.training;

import com.gigaspaces.annotation.pojo.*;
import com.gigaspaces.metadata.ClassBinaryStorageLayout;
import com.gigaspaces.metadata.StorageType;
import com.gigaspaces.metadata.index.SpaceIndexType;

@SpaceClassBinaryStorage(layout = ClassBinaryStorageLayout.DIRECT)
public class MyBigObjectDirect {
    Integer id;
    String key1;
    String key2;
    String val1;
    String val2;
    String val3;
    String val4;
    String val5;
    String val6;
    String val7;
    String val8;
    String val9;
    String val10;
    String[] payload;

    public MyBigObjectDirect() {
    }

    public void setVals(String val){
        setVal1(val);
        setVal2(val+ "_2");
        setVal3(val+ "_3");
        setVal4(val+ "_4");
        setVal5(val+ "_5");
        setVal6(val+ "_6");
        setVal7(val+ "_7");
        setVal8(val+ "_8");
        setVal9(val+ "_9");
        setVal10(val+ "_10");
    }

    public MyBigObjectDirect(Integer id, String key1, String key2) {
        this.id = id;
        this.key1 = key1;
        this.key2 = key2;
    }

    @SpaceId
    @SpaceRouting
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SpaceIndex(type = SpaceIndexType.ORDERED)
    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    @SpaceIndex(type = SpaceIndexType.EQUAL_AND_ORDERED)
    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal1() {
        return val1;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal2() {
        return val2;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal3() {
        return val3;
    }

    public void setVal3(String val3) {
        this.val3 = val3;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal4() {
        return val4;
    }

    public void setVal4(String val4) {
        this.val4 = val4;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal5() {
        return val5;
    }

    public void setVal5(String val5) {
        this.val5 = val5;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal6() {
        return val6;
    }

    public void setVal6(String val6) {
        this.val6 = val6;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal7() {
        return val7;
    }

    public void setVal7(String val7) {
        this.val7 = val7;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal8() {
        return val8;
    }

    public void setVal8(String val8) {
        this.val8 = val8;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal9() {
        return val9;
    }

    public void setVal9(String val9) {
        this.val9 = val9;
    }

    @SpacePropertyStorage(StorageType.BINARY)
    public String getVal10() {
        return val10;
    }

    public void setVal10(String val10) {
        this.val10 = val10;
    }

    @SpacePropertyStorage(StorageType.COMPRESSED)
    public String[] getPayload() {
        return payload;
    }

    public void setPayload(String[] payload) {
        this.payload = payload;
    }
}
