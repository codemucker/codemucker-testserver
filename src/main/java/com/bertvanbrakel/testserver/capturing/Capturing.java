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
 */package com.bertvanbrakel.testserver.capturing;

import java.util.List;

import org.hamcrest.Matcher;

/**
 * Convenience class to import all the matchers at once
 *
 * @author Be
 * rt van Brakel
 *
 */
public final class Capturing {

    public static Matcher<CapturedRequest> equalTo(final CapturedRequest request) {
        return CapturedRequestIsEqual.equalTo(request);
    }

    public static Matcher<CapturedRequest> equalTo(
            final CapturedRequestBuilder builder) {
        return CapturedRequestIsEqual.equalTo(builder);
    }

    public static List<Matcher<CapturedRequest>> equalTo(
            final CapturedRequest... requests) {
        return CapturedRequestIsEqual.equalTo(requests);
    }

    public static Matcher<? super CapturedHeader> equalTo(
            final CapturedHeader expect) {
        return CapturedHeaderIsEqual.equalTo(expect);
    }

    public static Matcher<? super CapturedFileItem> equalTo(
            final CapturedFileItem expect) {
        return CapturedFileItemIsEqual.equalTo(expect);
    }

    public static Matcher<? super CapturedCookie> equalTo(
            final CapturedCookie expect) {
        return CapturedCookieIsEqual.equalTo(expect);
    }

    public static FileItemBuilder fileItemBuilder() {
        return new FileItemBuilder();
    }

    public static CapturedRequestBuilder requestBuilder() {
        return new CapturedRequestBuilder();
    }
}
