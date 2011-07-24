package com.bertvanbrakel.testserver.capturing;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CapturedFileItemIsEqual extends
        TypeSafeMatcher<CapturedFileItem> {
    private final CapturedFileItem expect;

    public CapturedFileItemIsEqual(final CapturedFileItem expect) {
        this.expect = expect;
    }

    @Override
    public boolean matchesSafely(final CapturedFileItem actual) {
        return EqualsBuilder.reflectionEquals(expect, actual);
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(ToStringBuilder.reflectionToString(expect,
                ToStringStyle.SHORT_PREFIX_STYLE));
    }

    /**
     * Convenience fluent API to create this matcher
     *
     *
     * @param cookie
     * @return
     */
    public static Matcher<? super CapturedFileItem> equalTo(
            final CapturedFileItem expect) {
        return new CapturedFileItemIsEqual(expect);
    }
}