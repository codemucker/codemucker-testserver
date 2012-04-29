/*
 * Copyright 2011 Bert van Brakel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bertvanbrakel.testserver.capturing;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.fileupload.FileItem;

import com.bertvanbrakel.testserver.Server;

public class CapturedRequestBuilder {

    private final CapturedRequest request = new CapturedRequest();

    public CapturedRequestBuilder setScheme(final String scheme) {
        request.scheme = scheme;
        return this;
    }

    public CapturedRequestBuilder setServer(final Server server) {
        request.host = server.getHost();
        request.port = server.getHttpPort();
        return this;
    }

    public CapturedRequestBuilder setHost(final String host) {
        request.host = host;
        return this;
    }

    public CapturedRequestBuilder setPort(final int port) {
        request.port = port;
        return this;
    }

    public CapturedRequestBuilder setContextPath(final String contextPath) {
        request.contextPath = contextPath;
        return this;
    }

    public CapturedRequestBuilder setServletPath(final String servletPath) {
        request.servletPath = servletPath;
        return this;
    }

    public CapturedRequestBuilder setPathInfo(final String pathInfo) {
        request.pathInfo = pathInfo;
        return this;
    }

    public CapturedRequestBuilder setCharacterEncoding(
            final String characterEncoding) {
        request.characterEncoding = characterEncoding;
        return this;
    }

    public CapturedRequestBuilder setMethodGET() {
        return setMethod("GET");
    }

    public CapturedRequestBuilder setMethodPOST() {
        return setMethod("POST");
    }

    public CapturedRequestBuilder setMethod(final String method) {
        request.method = method;

        return this;
    }

    public CapturedRequestBuilder addCookie(final Cookie cookie) {
        request.addCookie(cookie);
        return this;
    }

    public CapturedRequestBuilder addCookie(
            final org.apache.http.cookie.Cookie cookie) {
        request.addCookie(cookie);
        return this;
    }

    public CapturedRequestBuilder addHeader(final CapturedHeader header) {
        request.addHeader(header);
        return this;
    }

    public CapturedRequestBuilder addHeader(final String name,
            final String value) {
        request.addHeader(name, value);
        return this;
    }

    public CapturedRequestBuilder addHeaders(final Map<String,String> headers) {
        for( final String name:headers.keySet()){
            request.addHeader(name, headers.get(name));
        }
        return this;
    }

    public CapturedRequestBuilder addFileItem(final FileItem item) {
        request.addFileItem(item);
        return this;
    }

    public CapturedRequestBuilder addFileItem(final FileItemBuilder item) {
        request.addFileItem(item);
        return this;
    }

    public CapturedRequestBuilder setParamValues(final String name,
            final String... paramValues) {
        request.setParamValues(name, paramValues);
        return this;
    }

    public CapturedRequest getRequest() {
        return request;
    }

}
