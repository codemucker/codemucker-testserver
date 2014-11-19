package org.codemucker.testserver;

import org.apache.http.StatusLine;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AnInt;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

public class AStatusLine extends PropertyMatcher<StatusLine> {

    public static AStatusLine with() {
        return new AStatusLine();
    }

    public AStatusLine() {
        super(StatusLine.class);
    }

    public AStatusLine code(int val) {
        code(AnInt.equalTo(val));
        return this;
    }

    public AStatusLine code(Matcher<Integer> matcher) {
        matchProperty("statusCode", int.class, matcher);
        return this;
    }

    public AStatusLine reason(String val) {
        reason(AString.equalTo(val));
        return this;
    }

    public AStatusLine reason(Matcher<String> matcher) {
        matchProperty("reasonPhrase", String.class, matcher);
        return this;
    }

}
