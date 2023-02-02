package com.asahi.QQC;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;//发送者
    private String getter;//接收者
    private String content;//信息内容
    private Date sendTime;//发送时间
    private String mesType;//信息类型
    private byte[] file;
    private int filelenth = 0;
    private String dest;
    private String src;

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public int getFilelenth() {
        return filelenth;
    }

    public void setFilelenth(int filelenth) {
        this.filelenth = filelenth;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getMasType() {
        return mesType;
    }

    public void setMesType(String masType) {
        this.mesType = masType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
