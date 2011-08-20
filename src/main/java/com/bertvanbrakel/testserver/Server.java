package com.bertvanbrakel.testserver;

public interface Server {
    public int getHttpPort();

    public String getHost();

    public void start() throws Exception;

    public void stop() throws Exception;
}
