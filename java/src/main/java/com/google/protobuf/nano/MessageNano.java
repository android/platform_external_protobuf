// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
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
 * Abstract interface implemented by Protocol Message objects.
 *
 * @author wink@google.com Wink Saville
 */
public abstract class MessageNano {
    /**
     * Get the number of bytes required to encode this message.
     * Returns the cached size or calls getSerializedSize which
     * sets the cached size. This is used internally when serializing
     * so the size is only computed once. If a member is modified
     * then this could be stale call getSerializedSize if in doubt.
     */
    abstract public int getCachedSize();

    /**
     * Computes the number of bytes required to encode this message.
     * The size is cached and the cached result can be retrieved
     * using getCachedSize().
     */
    abstract public int getSerializedSize();

    /**
     * Serializes the message and writes it to {@code output}.  This does not
     * flush or close the stream.
     */
    abstract public void writeTo(CodedOutputStreamNano output) throws java.io.IOException;

    /**
     * Parse {@code input} as a message of this type and merge it with the
     * message being built.
     */
    abstract public MessageNano mergeFrom(final CodedInputStreamNano input) throws IOException;

    /**
     * Serialize to a byte array.
     * @return byte array with the serialized data.
     */
    public byte[] toByteArray() {
        final byte[] result = new byte[getSerializedSize()];
        toByteArray(result, 0, result.length);
        return result;
    }

    /**
     * Serialize to a byte array starting at offset through length. The
     * method getSerializedSize must have been called prior to calling
     * this method so the proper length is know.  If an attempt to
     * write more than length bytes OutOfSpaceException will be thrown
     * and if length bytes are not written then IllegalStateException
     * is thrown.
     * @return byte array with the serialized data.
     */
    public void toByteArray(byte [] data, int offset, int length) {
        try {
            final CodedOutputStreamNano output = CodedOutputStreamNano.newInstance(data, offset, length);
            writeTo(output);
            output.checkNoSpaceLeft();
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException "
                    + "(should never happen).");
        }
    }

    /**
     * Parse {@code data} as a message of this type and merge it with the
     * message being built.
     */
    public MessageNano mergeFrom(final byte[] data) throws InvalidProtocolBufferNanoException {
        return mergeFrom(data, 0, data.length);
    }

    /**
     * Parse {@code data} as a message of this type and merge it with the
     * message being built.
     */
    public MessageNano mergeFrom(final byte[] data, final int off, final int len)
            throws InvalidProtocolBufferNanoException {
        try {
            final CodedInputStreamNano input = CodedInputStreamNano.newInstance(data, off, len);
            mergeFrom(input);
            input.checkLastTagWas(0);
            return this;
        } catch (InvalidProtocolBufferNanoException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should "
                    + "never happen).");
        }
    }

    protected static final int EMPTY_INT_ARRAY[] = {};
    protected static final long EMPTY_LONG_ARRAY[] = {};
    protected static final float EMPTY_FLOAT_ARRAY[] = {};
    protected static final double EMPTY_DOUBLE_ARRAY[] = {};
    protected static final boolean EMPTY_BOOLEAN_ARRAY[] = {};
    protected static final String EMPTY_STRING_ARRAY[] = {};
    protected static final ByteStringNano EMPTY_BYTE_STRING_NANO_ARRAY[] = {};

    /**
     * Called by subclasses to parse an unknown field.
     * @return {@code true} unless the tag is an end-group tag.
     */
    protected boolean parseUnknownField(
        final CodedInputStreamNano input,
        final int tag) throws IOException {
      return input.skipField(tag);
    }

    /**
     * Computes the array length of a repeated field. We assume that in the common case repeated
     * fields are contiguously serialized but we still correctly handle interspersed values of a
     * repeated field (but with extra allocations).
     *
     * Rewinds to current input position before returning.
     *
     * @param input stream input, pointing to the byte after the first tag
     * @param tag repeated field tag just read
     * @return length of array
     * @throws IOException
     */
    protected static final int getRepeatedFieldArrayLength(
        final CodedInputStreamNano input,
        final int tag) throws IOException {
      int arrayLength = 1;
      int startPos = input.getPosition();
      input.skipField(tag);
      while (input.getBytesUntilLimit() > 0) {
        int thisTag = input.readTag();
        if (thisTag != tag) {
          break;
        }
        input.skipField(tag);
        arrayLength++;
      }
      input.rewindToPosition(startPos);
      return arrayLength;
    }

    /**
     * Computes the array length of a packed repeated field.
     *
     * Rewinds to current input position before returning.
     *
     * @param input stream input, pointing to the byte after the tag
     * @param tag packed repeated field tag just read
     * @return length of array
     * @throws IOException
     */
    protected static final int getPackedRepeatedFieldArrayLength(
            final CodedInputStreamNano input,
            final int tag) throws IOException {
      int arrayLength = 0;
      int startPos = input.getPosition();
      while (input.getBytesUntilLimit() > 0) {
        input.skipField(tag);
        arrayLength++;
      }
      input.rewindToPosition(startPos);
      return arrayLength;
    }
}
