package com.gigaspaces.dev.training;

import java.io.Serializable;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;

/**
 * Plain Java object
 */
public class MessagePOJO implements Serializable {
    private static final long serialVersionUID = -4432735134652140788L;

    private long counter = -1;
    private byte[] content;

    public MessagePOJO() {
    }

    @SpaceId
    @SpaceProperty(nullValue = "-1")
    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return getClass() + "_" + counter + "_" + content;
    }
}
