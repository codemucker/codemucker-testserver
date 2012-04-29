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