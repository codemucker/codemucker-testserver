package org.codemucker.testserver;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.AnInputStream;
import org.codemucker.jmatch.AnInt;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.JMatchException;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

public class AHttpEntity extends PropertyMatcher<HttpEntity> {

    public static AHttpEntity with() {
        return new AHttpEntity();
    }

    public AHttpEntity() {
        super(HttpEntity.class);
    }

    public AHttpEntity contentType(final String val) {
        contentType(AString.equalTo(val));
        return this;
    }

    public AHttpEntity contentType(final Matcher<String> matcher) {
        addMatcher(new AbstractNotNullMatcher<HttpEntity>() {
            @Override
            protected boolean matchesSafely(HttpEntity actual, MatchDiagnostics diag) {
                Header header = actual.getContentType();
                String val = header == null ? null : header.getValue();
                return diag.tryMatch(this, val, matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("contentType is", matcher);
            }
        });
        return this;
    }

    public AHttpEntity contentEncoding(final String val) {
        contentEncoding(AString.equalTo(val));
        return this;
    }

    public AHttpEntity contentEncoding(final Matcher<String> matcher) {
        addMatcher(new AbstractNotNullMatcher<HttpEntity>() {
            @Override
            protected boolean matchesSafely(HttpEntity actual, MatchDiagnostics diag) {
                Header header = actual.getContentEncoding();
                String val = header == null ? null : header.getValue();
                return diag.tryMatch(this, val, matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("contentEncoding is", matcher);
            }
        });
        return this;
    }

    public AHttpEntity contentLength(int val) {
        contentLength(AnInt.equalTo(val));
        return this;
    }

    public AHttpEntity contentLength(Matcher<Integer> matcher) {
        matchProperty("contentLength", int.class, matcher);
        return this;
    }

    public AHttpEntity contentAsString(String val) {
        contentAsString(AString.equalTo(val));
        return this;
    }

    public AHttpEntity contentAsString(final Matcher<String> matcher) {
        content(AnInputStream.with().stringContent("UTF-8", matcher));
        return this;
    }

    public AHttpEntity content(final Matcher<InputStream> matcher) {
        addMatcher(new AbstractNotNullMatcher<HttpEntity>() {
            @Override
            protected boolean matchesSafely(HttpEntity actual, MatchDiagnostics diag) {
                InputStream is;
                try {
                    is = actual.getContent();
                } catch (IllegalStateException | IOException e) {
                    throw new JMatchException("Error getting entity content stream", e);
                }
                return diag.tryMatch(this, is, matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("content", matcher);
            }
        });
        return this;
    }
}
