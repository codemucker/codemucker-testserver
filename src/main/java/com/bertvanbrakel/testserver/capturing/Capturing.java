package com.bertvanbrakel.testserver.capturing;

import java.util.List;

import org.hamcrest.Matcher;

/**
 * Convenience class to import all the matchers at once
 *
 * @author Bert van Brakel
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
