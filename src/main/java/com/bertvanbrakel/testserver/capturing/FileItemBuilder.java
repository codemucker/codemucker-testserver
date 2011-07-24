package com.bertvanbrakel.testserver.capturing;

import java.io.UnsupportedEncodingException;

public class FileItemBuilder {
    private String fileName;
    private long size;
    private String contentType;
    private byte[] payloadBytes;
    private String fieldName;

    public FileItemBuilder(){
    }

    public FileItemBuilder(final String fieldName){
        this.fieldName = fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public FileItemBuilder setFileName(final String name) {
        this.fileName = name;
        return this;
    }

    public long getSize() {
        return size;
    }

    public FileItemBuilder setSize(final long size) {
        this.size = size;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public FileItemBuilder setContentTypeUTF8() {
        return setContentType("text/plain; charset=UTF-8");
    }

    public FileItemBuilder setContentTypeBinaryOctet() {
        return setContentType("application/octet-stream");
    }

    public FileItemBuilder setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getPayloadBytes() {
        return payloadBytes;
    }

    public FileItemBuilder setPayload(final String value, final String charSet) {
        try {
            return setPayloadBytes(value==null?null:value.getBytes(charSet));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported charset " + charSet, e);
        }
    }

    public FileItemBuilder setPayloadBytes(final byte[] payloadBytes) {
        this.payloadBytes = payloadBytes;
        this.size = payloadBytes==null?0:payloadBytes.length;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FileItemBuilder setFieldName(final String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
}