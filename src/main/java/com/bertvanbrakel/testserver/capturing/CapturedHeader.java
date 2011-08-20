package com.bertvanbrakel.testserver.capturing;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CapturedHeader {
	private final String name;
	private final String value;

	public CapturedHeader(final String name, final String value) {
		super();
		this.name = name;
		this.value = value;
	}


	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(7, 11, this);
	}

	@Override
	public boolean equals(final Object other) {
		return EqualsBuilder.reflectionEquals(other, this);
	}

}
