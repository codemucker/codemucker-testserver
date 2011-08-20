package com.bertvanbrakel.testserver.capturing;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A captured cookie later analysis and verification. We can't keep hold 
 * of the cookies themselves as the web server may perform cleanup on them once
 * a request has completed
 * 
 * @author Bert van Brakel
 */
public class CapturedCookie {
	final String domain;
	final String path;
	final String name;
	final String value;
	final int maxAge;
	final int version;
	final boolean secure;

	public CapturedCookie(Cookie c) {
		domain = c.getDomain();
		path = c.getPath();
		name = c.getName();
		value = c.getValue();
		secure = c.getSecure();
		maxAge = c.getMaxAge();
		version = c.getVersion();
	}

	public CapturedCookie(org.apache.http.cookie.Cookie c) {
		domain = c.getDomain();
		path = c.getPath();
		name = c.getName();
		value = c.getValue();
		secure = c.isSecure();
		maxAge = c.getExpiryDate() == null ? -1 : 0;
		version = c.getVersion();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(13, 5, this);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(other, this);
	}
}
