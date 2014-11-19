package org.codemucker.testserver;

import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

public class AHttpResponse extends PropertyMatcher<HttpResponse>{

    public static AHttpResponse with(){
        return new AHttpResponse();
    }
    
    public AHttpResponse() {
        super(HttpResponse.class);
    }

    public AHttpResponse statusCode(int status){
        statusLine(AStatusLine.with().code(status));
        return this;
    }
    
    public AHttpResponse statusReason(Matcher<String> matcher){
        statusLine(AStatusLine.with().reason(matcher));
        return this;
    }
    
    public AHttpResponse statusLine(Matcher<StatusLine> matcher){
        matchProperty("statusLine", StatusLine.class, matcher);
        return this;
    }
    
    public AHttpResponse locale(Matcher<Locale> matcher){
        matchProperty("locale", Locale.class, matcher);
        return this;
    }
    
    public AHttpResponse entity(Matcher<HttpEntity> matcher){
        matchProperty("entity", HttpEntity.class, matcher);
        return this;
    }
    
}
