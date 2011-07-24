package com.bertvanbrakel.testserver.capturing;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.bertvanbrakel.lang.matcher.IsCollectionOf;
import com.bertvanbrakel.lang.matcher.IsCollectionOf.CONTAINS;
import com.bertvanbrakel.lang.matcher.IsCollectionOf.ORDER;

public class CapturedRequestIsEqual extends
        TypeSafeMatcher<CapturedRequest> {
    private final CapturedRequest expect;
    private final IsCollectionOf<CapturedCookie> cookieMatchers;
    private final IsCollectionOf<CapturedHeader> headerMatchers;
    private final IsCollectionOf<CapturedFileItem> fileItemMatchers;

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

    public CapturedRequestIsEqual(final CapturedRequest expect) {
        this(expect,MATCH.LOOSE);
    }

    public CapturedRequestIsEqual(final CapturedRequest expect, final MATCH match) {
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
        cookieMatchers = new IsCollectionOf<CapturedCookie>(isStrictMatch?CONTAINS.ONLY:CONTAINS.ALL,ORDER.ANY);
        headerMatchers = new IsCollectionOf<CapturedHeader>(isStrictMatch?CONTAINS.ONLY:CONTAINS.ALL,ORDER.ANY);
        //todo:do we want to be able to set this to order exact? We may be expecting certain fields first so
        //we can handle other fields (streaming file for example)
        fileItemMatchers = new IsCollectionOf<CapturedFileItem>(CONTAINS.ONLY,ORDER.ANY);

        for (final CapturedCookie c : expect.getCookies()) {
            cookieMatchers.add(new CapturedCookieIsEqual(c));
        }
        for (final CapturedHeader h : expect.getHeaders()) {
            headerMatchers.add(new CapturedHeaderIsEqual(h));
        }
        for (final CapturedFileItem item : expect.getMultiPartFileItems()) {
            fileItemMatchers.add(new CapturedFileItemIsEqual(item));
        }
    }

    @Override
    public boolean matchesSafely(final CapturedRequest actual) {
        if (!EqualsBuilder.reflectionEquals(expect, actual, new String[] {
                "cookies", "headers" })) {
            return false;
        }
        assertThat(actual.getCookies(), cookieMatchers);
        assertThat(actual.getHeaders(), headerMatchers);
        assertThat(actual.getMultiPartFileItems(), fileItemMatchers);
        return true;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(ToStringBuilder.reflectionToString(expect,ToStringStyle.MULTI_LINE_STYLE));
        desc.appendText(" with cookies ");
        desc.appendDescriptionOf(cookieMatchers);
        desc.appendText(" and with headers ");
        desc.appendDescriptionOf(headerMatchers);
        desc.appendText(" and with multiPartContent fileItems ");
        desc.appendDescriptionOf(fileItemMatchers);
    }

    /**
     * Convenience fluent API to create this matcher
     *
     * @param request
     * @return
     */
    public static Matcher<CapturedRequest> equalTo(
            final CapturedRequest request) {
        return new CapturedRequestIsEqual(request);
    }

    public static Matcher<CapturedRequest> equalTo(
            final CapturedRequestBuilder builder) {
        return new CapturedRequestIsEqual(builder.getRequest());
    }

    public static List<Matcher<CapturedRequest>> equalTo( final CapturedRequest... requests) {
        final List<Matcher<CapturedRequest>> matchers = new ArrayList<Matcher<CapturedRequest>>();
        for( final CapturedRequest r:requests){
            matchers.add(new CapturedRequestIsEqual(r));
        }
        return matchers;
    }

}