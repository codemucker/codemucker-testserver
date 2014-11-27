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
package org.codemucker.testserver.capturing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A captured http request for later analysis and verification. We can't keep
 * hold of the requests themselves as the web server may perform cleanup on them
 * once a request has completed
 *
 * @author Bert van Brakel
 */
public class CapturedRequest {

    public String scheme;
    public String host;
    public int port;
    public String contextPath;
    public String servletPath;
    public String pathInfo;

    public String characterEncoding;
    public String method;

    /**
     * The request parameters we got coming in
     */
    final Map<String, Collection<String>> parameters = new HashMap<String, Collection<String>>();
    /**
     * The cookies which we got coming in
     */
    final Collection<CapturedCookie> cookies = new ArrayList<CapturedCookie>();
    /**
     * The headers we received
     */
    final Collection<CapturedHeader> headers = new ArrayList<CapturedHeader>();

    /**
     * The multi part content file items we received
     */
    final Collection<CapturedFileItem> fileItems = new ArrayList<CapturedFileItem>();

    public CapturedRequest() {
    }

    public CapturedRequest(final HttpServletRequest req) {
        scheme = req.getScheme();
        host = req.getServerName();
        port = req.getServerPort();
        contextPath = req.getContextPath();
        servletPath = req.getServletPath();
        pathInfo = req.getPathInfo();
        characterEncoding = req.getCharacterEncoding();
        method = req.getMethod();
        final Cookie[] cookies = req.getCookies();

        // cookies
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                this.cookies.add(new CapturedCookie(cookie));
            }
        }
        // headers
        for (@SuppressWarnings("unchecked")
        final Enumeration<String> names = req.getHeaderNames(); names
                .hasMoreElements();) {
            final String name = names.nextElement();
            @SuppressWarnings("unchecked")
            final Enumeration<String> values = req.getHeaders(name);
            if (values != null) {
                for (; values.hasMoreElements();) {
                    this.addHeader(new CapturedHeader(name, values
                            .nextElement()));
                }
            }
        }
        // if we use the normal 'toString' on maps, and arrays, we get pretty
        // poor results
        // Use ArrayLists instead to get a nice output
        @SuppressWarnings("unchecked")
        final Map<String, String[]> paramMap = req.getParameterMap();
        if (paramMap != null) {
            for (final String key : paramMap.keySet()) {
                final String[] vals = paramMap.get(key);
                this.parameters.put(key,
                        new ArrayList<String>(Arrays.asList(vals)));
            }
        }
        // handle multipart posts
        if (ServletFileUpload.isMultipartContent(req)) {
            // Create a factory for disk-based file items
            final FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            final ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                @SuppressWarnings("unchecked")
                final List<FileItem> items = upload.parseRequest(req);
                for (final FileItem item : items) {
                    fileItems.add(new CapturedFileItem(item));
                }
            } catch (final FileUploadException e) {
                throw new RuntimeException("Error handling multipart content",
                        e);
            }
        }

    }

    public void addCookie(final Cookie cookie) {
        cookies.add(new CapturedCookie(cookie));
    }

    public void addCookie(final org.apache.http.cookie.Cookie cookie) {
        cookies.add(new CapturedCookie(cookie));
    }

    public void addHeader(final CapturedHeader header) {
        headers.add(header);
    }

    public void addHeader(final String name, final String value) {
        headers.add(new CapturedHeader(name, value));
    }

    public void addFileItem(final FileItem item) {
        fileItems.add(new CapturedFileItem(item));
    }

    public void addFileItem(final FileItemBuilder item) {
        fileItems.add(new CapturedFileItem(item));
    }

    public void setParamValues(final String name, final String... paramValues) {
        parameters.put(name, new ArrayList<String>(Arrays.asList(paramValues)));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(21, 7, this);
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(other, this);
    }

    public Collection<CapturedCookie> getCookies() {
        return cookies;
    }

    public Collection<CapturedHeader> getHeaders() {
        return headers;
    }

    public Collection<CapturedFileItem> getMultiPartFileItems() {
        return fileItems;
    }

    public Map<String, Collection<String>> getParameters() {
        return parameters;
    }
}