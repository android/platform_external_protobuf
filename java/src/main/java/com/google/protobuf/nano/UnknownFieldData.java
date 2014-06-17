// Protocol Buffers - Google's data interchange format
// Copyright 2013 Google Inc.  All rights reserved.
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.google.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;

/**
 * Stores unknown fields. These might be extensions or fields that the generated API doesn't
 * know about yet.
 *
 * @author bduff@google.com (Brian Duff)
 */
final class UnknownFieldData {

    final int tag;
    private byte[] bytes;
    private Extension<?, ?> extension;
    private Object value;

    UnknownFieldData(int tag, byte[] bytes) {
        this.tag = tag;
        this.bytes = bytes;
        this.extension = null;
        this.value = null;
    }

    UnknownFieldData(Extension<?, ?> extension, Object value) {
        this.tag = extension.tag;
        this.bytes = null;
        this.extension = extension;
        this.value = value;
    }

    void setValue(Extension<?, ?> extension, Object value) {
        bytes = null;
        this.extension = extension;
        this.value = value;
    }

    int computeSerializedSize() {
        int size = 0;
        if (getBytes() != null) {
            size += CodedOutputByteBufferNano.computeRawVarint32Size(tag);
            size += getBytes().length;
        } else {
            size = extension.computeSerializedSize(value);
        }
        return size;
    }

    void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (getBytes() != null) {
            output.writeRawVarint32(tag);
            output.writeRawBytes(getBytes());
        } else {
            extension.writeTo(value, output);
        }
    }

    byte[] getBytes() {
        return bytes;
    }

    boolean hasValue(Extension<?, ?> extension) {
        if (value != null){
            if (this.extension != extension) {  // Extension objects are singletons.
                throw new IllegalStateException(
                        "Tried to getExtension with a differernt Extension.");
            }
            return true;
        }
        return false;
    }

    Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UnknownFieldData)) {
            return false;
        }

        UnknownFieldData other = (UnknownFieldData) o;
        return tag == other.tag && Arrays.equals(getBytes(), other.getBytes());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + tag;
        result = 31 * result + Arrays.hashCode(getBytes());
        return result;
    }
}
