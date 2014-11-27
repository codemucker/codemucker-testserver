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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

public class ACapturedCookie extends AbstractMatcher<CapturedCookie> {
    private final CapturedCookie expect;

    public ACapturedCookie(final CapturedCookie expect) {
        this.expect = expect;
    }

    @Override
    public boolean matchesSafely(final CapturedCookie actual, MatchDiagnostics diag) {
        return EqualsBuilder.reflectionEquals(expect, actual);
    }

    @Override
    public void describeTo(final Description desc) {
        desc.value("expect",ToStringBuilder.reflectionToString(expect,ToStringStyle.SHORT_PREFIX_STYLE));
    }

    /**
     * Convenience fluent API to create this matcher
     *
     *
     * @param cookie
     * @return
     */
    public static Matcher<? super CapturedCookie> equalTo(
            final CapturedCookie expect) {
        return new ACapturedCookie(expect);
    }
}