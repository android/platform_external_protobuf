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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an extension.
 *
 * @author bduff@google.com (Brian Duff)
 * @author maxtroy@google.com (Max Cai)
 * @param <M> the type of the extendable message this extension is for.
 * @param <T> the Java type of the extension; see {@link #clazz}.
 */
public class Extension<M extends ExtendableMessageNano<M>, T> {

    public static final int TYPE_DOUBLE   = 1;
    public static final int TYPE_FLOAT    = 2;
    public static final int TYPE_INT64    = 3;
    public static final int TYPE_UINT64   = 4;
    public static final int TYPE_INT32    = 5;
    public static final int TYPE_FIXED64  = 6;
    public static final int TYPE_FIXED32  = 7;
    public static final int TYPE_BOOL     = 8;
    public static final int TYPE_STRING   = 9;
    public static final int TYPE_GROUP    = 10;
    public static final int TYPE_MESSAGE  = 11;
    public static final int TYPE_BYTES    = 12;
    public static final int TYPE_UINT32   = 13;
    public static final int TYPE_ENUM     = 14;
    public static final int TYPE_SFIXED32 = 15;
    public static final int TYPE_SFIXED64 = 16;
    public static final int TYPE_SINT32   = 17;
    public static final int TYPE_SINT64   = 18;

    /**
     * Creates an {@code Extension} of the given type and tag number.
     * Should be used by the generated code only.
     */
    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> create(
            int type, Class<T> clazz, int tag) {
        return new Extension<M, T>(type, clazz, tag);
    }

    /**
     * Creates a repeated {@code Extension} of the given type and tag number.
     * Should be used by the generated code only.
     */
    public static <M extends ExtendableMessageNano<M>, T> Extension<M, T> createRepeated(
            int type, Class<T> clazz, int tag, int nonPackedTag, int packedTag) {
        return new RepeatedExtension<M, T>(type, clazz, tag, nonPackedTag, packedTag);
    }

    /**
     * Protocol Buffer type of this extension; one of the {@code TYPE_} constants.
     */
    protected final int type;

    /**
     * Java type of this extension. For a singular extension, this is the boxed Java type for the
     * Protocol Buffer {@link #type}; for a repeated extension, this is an array type whose
     * component type is the unboxed Java type for {@link #type}. For example, for a singular
     * {@code int32}/{@link #TYPE_INT32} extension, this equals {@code Integer.class}; for a
     * repeated {@code int32} extension, this equals {@code int[].class}.
     */
    protected final Class<T> clazz;

    /**
     * Tag number of this extension.
     */
    protected final int tag;

    private Extension(int type, Class<T> clazz, int tag) {
        this.type = type;
        this.clazz = clazz;
        this.tag = tag;
    }

    /**
     * Returns the value of this extension stored in the given list of unknown fields, or
     * {@code null} if no unknown fields matches this extension.
     */
    T getValueFrom(List<UnknownFieldData> unknownFields) {
        // This implementation is for singular extensions. Class RepeatedExtension overrides this
        // method to provide the implementation for repeated extensions.
        if (unknownFields == null) {
            return null;
        }

        // For singular extensions, get the last piece of data stored under this extension.
        UnknownFieldData data = null;
        for (int i = unknownFields.size() - 1; data == null && i >= 0; i--) {
            UnknownFieldData thisData = unknownFields.get(i);
            if (thisData.tag == tag && thisData.bytes.length != 0) {
                data = thisData;
            }
        }
        if (data == null) {
            return null;
        }
        try {
            return clazz.cast(readData(CodedInputByteBufferNano.newInstance(data.bytes), clazz));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading extension field", e);
        }
    }

    protected Object readData(CodedInputByteBufferNano input, Class<?> elementClass)
            throws IOException {
        switch (type) {
            case TYPE_DOUBLE:
                return input.readDouble();
            case TYPE_FLOAT:
                return input.readFloat();
            case TYPE_INT64:
                return input.readInt64();
            case TYPE_UINT64:
                return input.readUInt64();
            case TYPE_INT32:
                return input.readInt32();
            case TYPE_FIXED64:
                return input.readFixed64();
            case TYPE_FIXED32:
                return input.readFixed32();
            case TYPE_BOOL:
                return input.readBool();
            case TYPE_STRING:
                return input.readString();
            case TYPE_GROUP:
                try {
                    MessageNano group = (MessageNano) elementClass.newInstance();
                    input.readGroup(group, WireFormatNano.getTagFieldNumber(tag));
                    return group;
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException(
                            "Error creating instance of class " + elementClass, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            "Error creating instance of class " + elementClass, e);
                }
            case TYPE_MESSAGE:
                try {
                    MessageNano message = (MessageNano) elementClass.newInstance();
                    input.readMessage(message);
                    return message;
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException(
                            "Error creating instance of class " + elementClass, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            "Error creating instance of class " + elementClass, e);
                }
            case TYPE_BYTES:
                return input.readBytes();
            case TYPE_UINT32:
                return input.readUInt32();
            case TYPE_ENUM:
                return input.readEnum();
            case TYPE_SFIXED32:
                return input.readSFixed32();
            case TYPE_SFIXED64:
                return input.readSFixed64();
            case TYPE_SINT32:
                return input.readSInt32();
            case TYPE_SINT64:
                return input.readSInt64();
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    /**
     * Sets the value of this extension to the given list of unknown fields. This removes any
     * previously stored data matching this extension.
     *
     * @param value The value of this extension, or {@code null} to clear this extension from the
     *     unknown fields.
     * @return The same {@code unknownFields} list, or a new list storing the extension value if
     *     the argument was null.
     */
    List<UnknownFieldData> setValueTo(T value, List<UnknownFieldData> unknownFields) {
        // This implementation is for singular extensions. Class RepeatedExtension overrides this
        // method to provide the implementation for repeated extensions.
        if (unknownFields != null) {
            // Delete all data matching this extension
            for (int i = unknownFields.size() - 1; i >= 0; i--) {
                if (unknownFields.get(i).tag == tag) {
                    unknownFields.remove(i);
                }
            }
        }

        if (value == null) {
            return unknownFields;
        }

        if (unknownFields == null) {
            unknownFields = new ArrayList<UnknownFieldData>();
        }

        unknownFields.add(writeData(value));
        return unknownFields;
    }

    protected UnknownFieldData writeData(Object value) {
        byte[] data;
        try {
            switch (type) {
                case TYPE_DOUBLE:
                    Double doubleValue = (Double) value;
                    data = new byte[CodedOutputByteBufferNano.computeDoubleSizeNoTag(doubleValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeDoubleNoTag(doubleValue);
                    break;
                case TYPE_FLOAT:
                    Float floatValue = (Float) value;
                    data = new byte[CodedOutputByteBufferNano.computeFloatSizeNoTag(floatValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeFloatNoTag(floatValue);
                    break;
                case TYPE_INT64:
                    Long int64Value = (Long) value;
                    data = new byte[CodedOutputByteBufferNano.computeInt64SizeNoTag(int64Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeInt64NoTag(int64Value);
                    break;
                case TYPE_UINT64:
                    Long uint64Value = (Long) value;
                    data = new byte[CodedOutputByteBufferNano.computeUInt64SizeNoTag(uint64Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeUInt64NoTag(uint64Value);
                    break;
                case TYPE_INT32:
                    Integer int32Value = (Integer) value;
                    data = new byte[CodedOutputByteBufferNano.computeInt32SizeNoTag(int32Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeInt32NoTag(int32Value);
                    break;
                case TYPE_FIXED64:
                    Long fixed64Value = (Long) value;
                    data = new byte[
                            CodedOutputByteBufferNano.computeFixed64SizeNoTag(fixed64Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeFixed64NoTag(fixed64Value);
                    break;
                case TYPE_FIXED32:
                    Integer fixed32Value = (Integer) value;
                    data = new byte[
                            CodedOutputByteBufferNano.computeFixed32SizeNoTag(fixed32Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeFixed32NoTag(fixed32Value);
                    break;
                case TYPE_BOOL:
                    Boolean boolValue = (Boolean) value;
                    data = new byte[CodedOutputByteBufferNano.computeBoolSizeNoTag(boolValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeBoolNoTag(boolValue);
                    break;
                case TYPE_STRING:
                    String stringValue = (String) value;
                    data = new byte[CodedOutputByteBufferNano.computeStringSizeNoTag(stringValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeStringNoTag(stringValue);
                    break;
                case TYPE_GROUP:
                    MessageNano groupValue = (MessageNano) value;
                    int fieldNumber = WireFormatNano.getTagFieldNumber(tag);
                    data = new byte[CodedOutputByteBufferNano.computeGroupSizeNoTag(groupValue)
                            + CodedOutputByteBufferNano.computeTagSize(fieldNumber)];
                    CodedOutputByteBufferNano out = CodedOutputByteBufferNano.newInstance(data);
                    out.writeGroupNoTag(groupValue);
                    // The endgroup tag must be included in the data payload.
                    out.writeTag(fieldNumber, WireFormatNano.WIRETYPE_END_GROUP);
                    break;
                case TYPE_MESSAGE:
                    MessageNano messageValue = (MessageNano) value;
                    data = new byte[
                            CodedOutputByteBufferNano.computeMessageSizeNoTag(messageValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeMessageNoTag(messageValue);
                    break;
                case TYPE_BYTES:
                    byte[] bytesValue = (byte[]) value;
                    data = new byte[CodedOutputByteBufferNano.computeBytesSizeNoTag(bytesValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeBytesNoTag(bytesValue);
                    break;
                case TYPE_UINT32:
                    Integer uint32Value = (Integer) value;
                    data = new byte[CodedOutputByteBufferNano.computeUInt32SizeNoTag(uint32Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeUInt32NoTag(uint32Value);
                    break;
                case TYPE_ENUM:
                    Integer enumValue = (Integer) value;
                    data = new byte[CodedOutputByteBufferNano.computeEnumSizeNoTag(enumValue)];
                    CodedOutputByteBufferNano.newInstance(data).writeEnumNoTag(enumValue);
                    break;
                case TYPE_SFIXED32:
                    Integer sfixed32Value = (Integer) value;
                    data = new byte[
                            CodedOutputByteBufferNano.computeSFixed32SizeNoTag(sfixed32Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeSFixed32NoTag(sfixed32Value);
                    break;
                case TYPE_SFIXED64:
                    Long sfixed64Value = (Long) value;
                    data = new byte[
                            CodedOutputByteBufferNano.computeSFixed64SizeNoTag(sfixed64Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeSFixed64NoTag(sfixed64Value);
                    break;
                case TYPE_SINT32:
                    Integer sint32Value = (Integer) value;
                    data = new byte[CodedOutputByteBufferNano.computeSInt32SizeNoTag(sint32Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeSInt32NoTag(sint32Value);
                    break;
                case TYPE_SINT64:
                    Long sint64Value = (Long) value;
                    data = new byte[CodedOutputByteBufferNano.computeSInt64SizeNoTag(sint64Value)];
                    CodedOutputByteBufferNano.newInstance(data).writeSInt64NoTag(sint64Value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type " + type);
            }
        } catch (IOException e) {
            // Should not happen
            throw new IllegalStateException(e);
        }
        return new UnknownFieldData(tag, data);
    }

    /**
     * Represents a repeated extension. The {@link #tag} will equal either {@link #nonPackedTag} or
     * {@link #packedTag}, depending on whether the extension is specified as packed.
     *
     * <p>If there is no repeated extensions, this subclass will be removable by ProGuard.
     */
    private static class RepeatedExtension<M extends ExtendableMessageNano<M>, T>
            extends Extension<M, T> {

        /**
         * Tag of a piece of non-packed data from the wire compatible with this extension.
         */
        private final int nonPackedTag;

        /**
         * Tag of a piece of packed data from the wire compatible with this extension.
         * 0 if the type of this extension is not packable.
         */
        private final int packedTag;

        public RepeatedExtension(
                int type, Class<T> clazz, int tag, int nonPackedTag, int packedTag) {
            super(type, clazz, tag);
            this.nonPackedTag = nonPackedTag;
            this.packedTag = packedTag;
        }

        @Override
        T getValueFrom(List<UnknownFieldData> unknownFields) {
            if (unknownFields == null) {
                return null;
            }

            // For repeated extensions, read all matching unknown fields in their original order.
            Class<?> elementType = clazz.getComponentType();
            List<Object> resultList = new ArrayList<Object>();
            try {
                for (int i = 0; i < unknownFields.size(); i++) {
                    UnknownFieldData data = unknownFields.get(i);
                    if (data.bytes.length == 0) {
                        continue;
                    }
                    if (data.tag == nonPackedTag) {
                        // Data is not packed. Read as singular data.
                        resultList.add(readData(
                                CodedInputByteBufferNano.newInstance(data.bytes), elementType));
                    } else if (data.tag == packedTag) {
                        // Data is packed.
                        CodedInputByteBufferNano buffer =
                                CodedInputByteBufferNano.newInstance(data.bytes);
                        buffer.pushLimit(buffer.readRawVarint32()); // length limit
                        while (!buffer.isAtEnd()) {
                            resultList.add(readData(buffer, elementType));
                        }
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading extension field", e);
            }

            // Construct the resulting array.
            int resultSize = resultList.size();
            if (resultSize == 0) {
                return null;
            }
            T result = clazz.cast(Array.newInstance(elementType, resultSize));
            for (int i = 0; i < resultSize; i++) {
                Array.set(result, i, resultList.get(i));
            }
            return result;
        }

        @Override
        List<UnknownFieldData> setValueTo(T value, List<UnknownFieldData> unknownFields) {
            if (unknownFields != null) {
                // Delete all data matching this extension
                for (int i = unknownFields.size() - 1; i >= 0; i--) {
                    int fieldTag = unknownFields.get(i).tag;
                    if (fieldTag == nonPackedTag || fieldTag == packedTag) {
                        unknownFields.remove(i);
                    }
                }
            }

            int arrayLength = value == null ? 0 : Array.getLength(value);
            if (arrayLength == 0) {
                return unknownFields;
            }

            if (unknownFields == null) {
                unknownFields = new ArrayList<UnknownFieldData>();
            }

            if (tag == nonPackedTag) {
                // Non-packed. Use individual unknown fields for each non-null array element.
                for (int i = 0; i < arrayLength; i++) {
                    Object element = Array.get(value, i);
                    if (element != null) {
                        unknownFields.add(writeData(element));
                    }
                }
            } else if (tag == packedTag) {
                // Packed. Note that the array element type is guaranteed to be primitive, so there
                // won't be any null elements. First get data size.
                int dataSize = 0;
                switch (type) {
                    case TYPE_BOOL:
                        // Bools are stored as int32 but just as 0 or 1, so 1 byte each.
                        dataSize = arrayLength;
                        break;
                    case TYPE_FIXED32:
                    case TYPE_SFIXED32:
                    case TYPE_FLOAT:
                        dataSize = arrayLength * CodedOutputByteBufferNano.LITTLE_ENDIAN_32_SIZE;
                        break;
                    case TYPE_FIXED64:
                    case TYPE_SFIXED64:
                    case TYPE_DOUBLE:
                        dataSize = arrayLength * CodedOutputByteBufferNano.LITTLE_ENDIAN_64_SIZE;
                        break;
                    case TYPE_INT32:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(
                                    Array.getInt(value, i));
                        }
                        break;
                    case TYPE_SINT32:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeSInt32SizeNoTag(
                                    Array.getInt(value, i));
                        }
                        break;
                    case TYPE_UINT32:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeUInt32SizeNoTag(
                                    Array.getInt(value, i));
                        }
                        break;
                    case TYPE_INT64:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeInt64SizeNoTag(
                                    Array.getLong(value, i));
                        }
                        break;
                    case TYPE_SINT64:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeSInt64SizeNoTag(
                                    Array.getLong(value, i));
                        }
                        break;
                    case TYPE_UINT64:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeUInt64SizeNoTag(
                                    Array.getLong(value, i));
                        }
                        break;
                    case TYPE_ENUM:
                        for (int i = 0; i < arrayLength; i++) {
                            dataSize += CodedOutputByteBufferNano.computeEnumSizeNoTag(
                                    Array.getInt(value, i));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected non-packable type " + type);
                }

                // Now construct payload.
                int payloadSize =
                        dataSize + CodedOutputByteBufferNano.computeRawVarint32Size(dataSize);
                byte[] data = new byte[payloadSize];
                CodedOutputByteBufferNano output = CodedOutputByteBufferNano.newInstance(data);
                try {
                    output.writeRawVarint32(dataSize);
                    switch (type) {
                        case TYPE_BOOL:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeBoolNoTag(Array.getBoolean(value, i));
                            }
                            break;
                        case TYPE_FIXED32:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeFixed32NoTag(Array.getInt(value, i));
                            }
                            break;
                        case TYPE_SFIXED32:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeSFixed32NoTag(Array.getInt(value, i));
                            }
                            break;
                        case TYPE_FLOAT:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeFloatNoTag(Array.getFloat(value, i));
                            }
                            break;
                        case TYPE_FIXED64:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeFixed64NoTag(Array.getLong(value, i));
                            }
                            break;
                        case TYPE_SFIXED64:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeSFixed64NoTag(Array.getLong(value, i));
                            }
                            break;
                        case TYPE_DOUBLE:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeDoubleNoTag(Array.getDouble(value, i));
                            }
                            break;
                        case TYPE_INT32:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeInt32NoTag(Array.getInt(value, i));
                            }
                            break;
                        case TYPE_SINT32:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeSInt32NoTag(Array.getInt(value, i));
                            }
                            break;
                        case TYPE_UINT32:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeUInt32NoTag(Array.getInt(value, i));
                            }
                            break;
                        case TYPE_INT64:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeInt64NoTag(Array.getLong(value, i));
                            }
                            break;
                        case TYPE_SINT64:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeSInt64NoTag(Array.getLong(value, i));
                            }
                            break;
                        case TYPE_UINT64:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeUInt64NoTag(Array.getLong(value, i));
                            }
                            break;
                        case TYPE_ENUM:
                            for (int i = 0; i < arrayLength; i++) {
                                output.writeEnumNoTag(Array.getInt(value, i));
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unpackable type " + type);
                    }
                } catch (IOException e) {
                    // Should not happen.
                    throw new IllegalStateException(e);
                }
                unknownFields.add(new UnknownFieldData(tag, data));
            } else {
                throw new IllegalArgumentException("Unexpected repeated extension tag " + tag
                        + ", unequal to both non-packed variant " + nonPackedTag
                        + " and packed variant " + packedTag);
            }
            return unknownFields;
        }
    }
}
