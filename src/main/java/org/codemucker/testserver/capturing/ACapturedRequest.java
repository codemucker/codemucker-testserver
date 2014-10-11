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

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codemucker.jmatch.AList;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.testserver.Server;

//TODO:the bulder methods currently modify the captured request. Use the Property matcher!

public class ACapturedRequest extends AbstractNotNullMatcher<CapturedRequest> {
    private final CapturedRequest expect;
    private final AList.IAcceptMoreMatchers<CapturedCookie> cookieMatchers;
    private final AList.IAcceptMoreMatchers<CapturedHeader> headerMatchers;
    private final AList.IAcceptMoreMatchers<CapturedFileItem> fileItemMatchers;

    public static enum MATCH {
        /**
         * We want request objects to fully match
         */
        STRICT,
        /**
         * We allow additional cookies and headers which do not match
         */
        LOOSE;
    }

    public static ACapturedRequest with(){
    	return new ACapturedRequest(new CapturedRequest());
    }
    
    public ACapturedRequest(final CapturedRequest expect) {
        this(expect,MATCH.LOOSE);
    }

    public ACapturedRequest(final CapturedRequest expect, final MATCH match) {
        if( expect == null ){
            throw new IllegalArgumentException( "Must provide a request to compare to" );
        }
        if( match == null ){
            throw new IllegalArgumentException( "Must provide a match type. One of " + Arrays.toString(MATCH.values()) );
        }

        //if set to strict we don't expect any more cookies or headers than specified
        //if not strict we only look for a set of cookies and headers and ignore the rest
        final boolean isStrictMatch = MATCH.STRICT==match;

        this.expect = expect;
        //determine whether we do strict or loose matching
        if( isStrictMatch){
            cookieMatchers = AList.of(CapturedCookie.class).inAnyOrder().withOnly();
            headerMatchers = AList.of(CapturedHeader.class).inAnyOrder().withOnly();
        } else {
            cookieMatchers = AList.of(CapturedCookie.class).inAnyOrder().withAtLeast();
            headerMatchers = AList.of(CapturedHeader.class).inAnyOrder().withAtLeast();
        }
        //todo:do we want to be able to set this to order exact? We may be expecting certain fields first so
        //we can handle other fields (streaming file for example)
        fileItemMatchers =  AList.of(CapturedFileItem.class).inAnyOrder().withOnly();

        for (final CapturedCookie c : expect.getCookies()) {
            cookieMatchers.item(new ACapturedCookie(c));
        }
        for (final CapturedHeader h : expect.getHeaders()) {
            headerMatchers.item(new ACapturedHeader(h));
        }
        for (final CapturedFileItem item : expect.getMultiPartFileItems()) {
            fileItemMatchers.item(new ACapturedFileItem(item));
        }
    }

    @Override
    public boolean matchesSafely(final CapturedRequest actual, MatchDiagnostics diag) {
        if (!EqualsBuilder.reflectionEquals(expect, actual, new String[] {
                "cookies", "headers" })) {
            return false;
        }
        if(!diag.tryMatch(this,actual.getCookies(), cookieMatchers)){
        	return false;
        }
        if(!diag.tryMatch(this,actual.getHeaders(), headerMatchers)){
        	return false;
        }
        if(!diag.tryMatch(this,actual.getMultiPartFileItems(), fileItemMatchers)){
        	return false;
        }
        return true;
    }

    @Override
    public void describeTo(final Description desc) {
    	super.describeTo(desc);
    	desc.text(ToStringBuilder.reflectionToString(expect,ToStringStyle.MULTI_LINE_STYLE));
        desc.value("cookies",cookieMatchers);
        desc.value("headers",headerMatchers);
        desc.value("multiPartContent fileItems",fileItemMatchers);
    }
    
    public ACapturedRequest scheme(final String scheme) {
        expect.scheme = scheme;
        return this;
    }

    public ACapturedRequest server(final Server server) {
        expect.host = server.getHost();
        expect.port = server.getHttpPort();
        return this;
    }

    public ACapturedRequest host(final String host) {
        expect.host = host;
        return this;
    }

    public ACapturedRequest port(final int port) {
        expect.port = port;
        return this;
    }

    public ACapturedRequest contextPath(final String contextPath) {
        expect.contextPath = contextPath;
        return this;
    }

    public ACapturedRequest servletPath(final String servletPath) {
        expect.servletPath = servletPath;
        return this;
    }

    public ACapturedRequest pathInfo(final String pathInfo) {
        expect.pathInfo = pathInfo;
        return this;
    }

    public ACapturedRequest characterEncoding(
            final String characterEncoding) {
        expect.characterEncoding = characterEncoding;
        return this;
    }

    public ACapturedRequest methodGET() {
        return method("GET");
    }

    public ACapturedRequest methodPOST() {
        return method("POST");
    }

    public ACapturedRequest method(final String method) {
        expect.method = method;

        return this;
    }

    public ACapturedRequest cookie(final Cookie cookie) {
        expect.addCookie(cookie);
        return this;
    }

    public ACapturedRequest cookie(final org.apache.http.cookie.Cookie cookie) {
        expect.addCookie(cookie);
        return this;
    }

    public ACapturedRequest header(final CapturedHeader header) {
        expect.addHeader(header);
        return this;
    }

    public ACapturedRequest header(final String name,final String value) {
        expect.addHeader(name, value);
        return this;
    }

    public ACapturedRequest headers(final Map<String,String> headers) {
        for( final String name:headers.keySet()){
            expect.addHeader(name, headers.get(name));
        }
        return this;
    }

    public ACapturedRequest fileItem(final FileItem item) {
        expect.addFileItem(item);
        return this;
    }

    public ACapturedRequest fileItem(final FileItemBuilder item) {
        expect.addFileItem(item);
        return this;
    }

    public ACapturedRequest paramValues(final String name,final String... paramValues) {
        expect.setParamValues(name, paramValues);
        return this;
    }

    /**
     * Convenience fluent API to create this matcher
     *
     * @param request
     * @return
     */
    public static Matcher<CapturedRequest> equalTo(
            final CapturedRequest request) {
        return new ACapturedRequest(request);
    }


}