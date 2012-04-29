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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * A captured multi part content file item to be used for later analysis and verification. We can't keep hold of the
 * file items themselves as the web server may remove access to any underlying resourcess
 * request has completed
 *
 * @author Bert van Brakel
 */
public class CapturedFileItem {

    /**
     * Name as set by the form field
     */
    public final String fieldName;
    /**
     * Name of the file uploaded. Some browsers send this
     */
    public final String fileName;

    public final long size;
    public final String contentType;
    public final byte[] payloadBytes;

    public CapturedFileItem(final FileItemBuilder item) {
        fieldName = item.getFieldName();
        fileName = item.getFileName();
        size = item.getSize();
        contentType = item.getContentType();
        payloadBytes = item.getPayloadBytes();
    }

    public CapturedFileItem(final FileItem item) {
        fieldName = item.getFieldName();
        fileName = item.getName();
        size = item.getSize();
        contentType = item.getContentType();
        payloadBytes = item.get();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(23, 9, this);
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(other, this);
    }
}
