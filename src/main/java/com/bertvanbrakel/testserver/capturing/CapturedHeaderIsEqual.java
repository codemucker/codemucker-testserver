package com.bertvanbrakel.testserver.capturing;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CapturedHeaderIsEqual extends
        TypeSafeMatcher<CapturedHeader> {
    private final CapturedHeader expect;

    public CapturedHeaderIsEqual(final CapturedHeader expect) {
        this.expect = expect;
    }

    @Override
    public boolean matchesSafely(final CapturedHeader actual) {
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
     * @param header
     * @return
     */
    public static Matcher<? super CapturedHeader> equalTo(
            final CapturedHeader expect) {
        return new CapturedHeaderIsEqual(expect);
    }
}