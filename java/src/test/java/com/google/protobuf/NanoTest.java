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

package com.google.protobuf;

import com.google.protobuf.nano.NanoOuterClass;
import com.google.protobuf.nano.NanoOuterClass.TestAllTypesNano;
import com.google.protobuf.nano.RecursiveMessageNano;
import com.google.protobuf.nano.SimpleMessageNano;
import com.google.protobuf.nano.StringUtf8;
import com.google.protobuf.nano.UnittestImportNano;
import com.google.protobuf.nano.ByteStringNano;
import com.google.protobuf.nano.CodedInputStreamNano;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Test nano runtime.
 *
 * @author wink@google.com Wink Saville
 */
public class NanoTest extends TestCase {
  public void setUp() throws Exception {
  }

  public void testSimpleMessageNano() throws Exception {
    SimpleMessageNano msg = new SimpleMessageNano();
    assertEquals(123, msg.d);
    assertEquals(null, msg.nestedMsg);
    assertEquals(SimpleMessageNano.BAZ, msg.defaultNestedEnum);

    msg.d = 456;
    assertEquals(456, msg.d);

    SimpleMessageNano.NestedMessage nestedMsg = new SimpleMessageNano.NestedMessage();
    nestedMsg.bb = 2;
    assertEquals(2, nestedMsg.bb);
    msg.nestedMsg = nestedMsg;
    assertEquals(2, msg.nestedMsg.bb);

    msg.defaultNestedEnum = SimpleMessageNano.BAR;
    assertEquals(SimpleMessageNano.BAR, msg.defaultNestedEnum);

    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    SimpleMessageNano newMsg = SimpleMessageNano.parseFrom(result);
    assertEquals(456, newMsg.d);
    assertEquals(2, msg.nestedMsg.bb);
    assertEquals(SimpleMessageNano.BAR, msg.defaultNestedEnum);
  }

  public void testRecursiveMessageNano() throws Exception {
    RecursiveMessageNano msg = new RecursiveMessageNano();
    assertTrue(msg.repeatedRecursiveMessageNano.length == 0);

    RecursiveMessageNano msg1 = new RecursiveMessageNano();
    msg1.id = 1;
    assertEquals(1, msg1.id);
    RecursiveMessageNano msg2 = new RecursiveMessageNano();
    msg2.id = 2;
    RecursiveMessageNano msg3 = new RecursiveMessageNano();
    msg3.id = 3;

    RecursiveMessageNano.NestedMessage nestedMsg = new RecursiveMessageNano.NestedMessage();
    nestedMsg.a = msg1;
    assertEquals(1, nestedMsg.a.id);

    msg.id = 0;
    msg.nestedMessage = nestedMsg;
    msg.optionalRecursiveMessageNano = msg2;
    msg.repeatedRecursiveMessageNano = new RecursiveMessageNano[] { msg3 };

    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 14);
    assertEquals(result.length, msgSerializedSize);

    RecursiveMessageNano newMsg = RecursiveMessageNano.parseFrom(result);
    assertEquals(1, newMsg.repeatedRecursiveMessageNano.length);

    assertEquals(0, newMsg.id);
    assertEquals(1, newMsg.nestedMessage.a.id);
    assertEquals(2, newMsg.optionalRecursiveMessageNano.id);
    assertEquals(3, newMsg.repeatedRecursiveMessageNano[0].id);
  }

  public void testNanoRequiredInt32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.id = 123;
    assertEquals(123, msg.id);
    msg.clear().id = 456;
    msg.clear();

    msg.id = 123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(123, newMsg.id);
  }

  public void testNanoOptionalInt32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalInt32 = 123;
    assertEquals(123, msg.optionalInt32);
    msg.clear()
       .optionalInt32 = 456;
    assertEquals(456, msg.optionalInt32);
    msg.clear();

    msg.optionalInt32 = 123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 2);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(123, newMsg.optionalInt32);
  }

  public void testNanoOptionalInt64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalInt64 = 123;
    assertEquals(123, msg.optionalInt64);
    msg.clear()
       .optionalInt64 = 456;
    msg.clear();
    assertEquals(0, msg.optionalInt64);

    msg.optionalInt64 = 123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 2);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(123, newMsg.optionalInt64);
  }

  public void testNanoOptionalUint32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalUint32 = 123;
    assertEquals(123, msg.optionalUint32);
    msg.clear()
       .optionalUint32 = 456;
    msg.clear();
    assertEquals(0, msg.optionalUint32);

    msg.optionalUint32 = 123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 2);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(123, newMsg.optionalUint32);
  }

  public void testNanoOptionalUint64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalUint64 = 123;
    assertEquals(123, msg.optionalUint64);
    msg.clear()
       .optionalUint64 = 456;
    msg.clear();
    assertEquals(0, msg.optionalUint64);

    msg.optionalUint64 = 123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 2);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(123, newMsg.optionalUint64);
  }

  public void testNanoOptionalSint32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalSint32 = 123;
    assertEquals(123, msg.optionalSint32);
    msg.clear()
       .optionalSint32 = 456;
    msg.clear();
    assertEquals(0, msg.optionalSint32);

    msg.optionalSint32 = -123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(-123, newMsg.optionalSint32);
  }

  public void testNanoOptionalSint64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalSint64 = 123;
    assertEquals(123, msg.optionalSint64);
    msg.clear()
       .optionalSint64 = 456;
    msg.clear();
    assertEquals(0, msg.optionalSint64);

    msg.optionalSint64 = -123;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(-123, newMsg.optionalSint64);
  }

  /*
  public void testNanoOptionalFixed32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalFixed32());
    msg.setOptionalFixed32(123);
    assertTrue(msg.hasOptionalFixed32());
    assertEquals(123, msg.getOptionalFixed32());
    msg.clearOptionalFixed32();
    assertFalse(msg.hasOptionalFixed32());
    msg.clearOptionalFixed32()
       .setOptionalFixed32(456);
    assertTrue(msg.hasOptionalFixed32());
    msg.clear();
    assertFalse(msg.hasOptionalFixed32());

    msg.setOptionalFixed32(123);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalFixed32());
    assertEquals(123, newMsg.getOptionalFixed32());
  }

  public void testNanoOptionalFixed64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalFixed64());
    msg.setOptionalFixed64(123);
    assertTrue(msg.hasOptionalFixed64());
    assertEquals(123, msg.getOptionalFixed64());
    msg.clearOptionalFixed64();
    assertFalse(msg.hasOptionalFixed64());
    msg.clearOptionalFixed64()
       .setOptionalFixed64(456);
    assertTrue(msg.hasOptionalFixed64());
    msg.clear();
    assertFalse(msg.hasOptionalFixed64());

    msg.setOptionalFixed64(123);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalFixed64());
    assertEquals(123, newMsg.getOptionalFixed64());
  }
  public void testNanoOptionalSfixed32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalSfixed32());
    msg.setOptionalSfixed32(123);
    assertTrue(msg.hasOptionalSfixed32());
    assertEquals(123, msg.getOptionalSfixed32());
    msg.clearOptionalSfixed32();
    assertFalse(msg.hasOptionalSfixed32());
    msg.clearOptionalSfixed32()
       .setOptionalSfixed32(456);
    assertTrue(msg.hasOptionalSfixed32());
    msg.clear();
    assertFalse(msg.hasOptionalSfixed32());

    msg.setOptionalSfixed32(123);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalSfixed32());
    assertEquals(123, newMsg.getOptionalSfixed32());
  }

  public void testNanoOptionalSfixed64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalSfixed64());
    msg.setOptionalSfixed64(123);
    assertTrue(msg.hasOptionalSfixed64());
    assertEquals(123, msg.getOptionalSfixed64());
    msg.clearOptionalSfixed64();
    assertFalse(msg.hasOptionalSfixed64());
    msg.clearOptionalSfixed64()
       .setOptionalSfixed64(456);
    assertTrue(msg.hasOptionalSfixed64());
    msg.clear();
    assertFalse(msg.hasOptionalSfixed64());

    msg.setOptionalSfixed64(-123);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalSfixed64());
    assertEquals(-123, newMsg.getOptionalSfixed64());
  }

  public void testNanoOptionalFloat() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalFloat());
    msg.setOptionalFloat(123f);
    assertTrue(msg.hasOptionalFloat());
    assertTrue(123.0f == msg.getOptionalFloat());
    msg.clearOptionalFloat();
    assertFalse(msg.hasOptionalFloat());
    msg.clearOptionalFloat()
       .setOptionalFloat(456.0f);
    assertTrue(msg.hasOptionalFloat());
    msg.clear();
    assertFalse(msg.hasOptionalFloat());

    msg.setOptionalFloat(-123.456f);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalFloat());
    assertTrue(-123.456f == newMsg.getOptionalFloat());
  }

  public void testNanoOptionalDouble() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalDouble());
    msg.setOptionalDouble(123);
    assertTrue(msg.hasOptionalDouble());
    assertTrue(123.0 == msg.getOptionalDouble());
    msg.clearOptionalDouble();
    assertFalse(msg.hasOptionalDouble());
    msg.clearOptionalDouble()
       .setOptionalDouble(456.0);
    assertTrue(msg.hasOptionalDouble());
    msg.clear();
    assertFalse(msg.hasOptionalDouble());

    msg.setOptionalDouble(-123.456);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalDouble());
    assertTrue(-123.456 == newMsg.getOptionalDouble());
  }

  public void testNanoOptionalBool() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalBool());
    msg.setOptionalBool(true);
    assertTrue(msg.hasOptionalBool());
    assertEquals(true, msg.getOptionalBool());
    msg.clearOptionalBool();
    assertFalse(msg.hasOptionalBool());
    msg.clearOptionalBool()
       .setOptionalBool(true);
    assertTrue(msg.hasOptionalBool());
    msg.clear();
    assertFalse(msg.hasOptionalBool());

    msg.setOptionalBool(false);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 2);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalBool());
    assertEquals(false, newMsg.getOptionalBool());
  }
  */

  public void testNanoOptionalString() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.optionalString = "hello";
    assertEquals("hello", msg.optionalString);
    msg.clear();
    assertEquals("", msg.optionalString);
    msg.clear()
       .optionalString = "hello";
    assertFalse("".equals(msg.optionalString));
    msg.clear();
    assertEquals("", msg.optionalString);

    msg.optionalString = "bye";
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.optionalString != null);
    assertEquals("bye", newMsg.optionalString);
  }

  public void testNanoOptionalBytes() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(!msg.optionalBytes.isEmpty());
    msg.optionalBytes = ByteStringNano.copyFromUtf8("hello");
    assertTrue(!msg.optionalBytes.isEmpty());
    assertEquals("hello", msg.optionalBytes.toStringUtf8());
    msg.clear();
    assertFalse(!msg.optionalBytes.isEmpty());
    msg.clear()
       .optionalBytes = ByteStringNano.copyFromUtf8("hello");
    assertTrue(!msg.optionalBytes.isEmpty());
    msg.clear();
    assertFalse(!msg.optionalBytes.isEmpty());

    msg.optionalBytes = ByteStringNano.copyFromUtf8("bye");
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(!newMsg.optionalBytes.isEmpty());
    assertEquals("bye", newMsg.optionalBytes.toStringUtf8());
  }

  public void testNanoOptionalGroup() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    TestAllTypesNano.OptionalGroup grp = new TestAllTypesNano.OptionalGroup();
    grp.a = 1;
    assertFalse(msg.optionalGroup != null);
    msg.optionalGroup = grp;
    assertTrue(msg.optionalGroup != null);
    assertEquals(1, msg.optionalGroup.a);
    msg.clear();
    assertFalse(msg.optionalGroup != null);
    msg.clear()
       .optionalGroup = new TestAllTypesNano.OptionalGroup();
    msg.optionalGroup.a = 2;
    assertTrue(msg.optionalGroup != null);
    msg.clear();
    assertFalse(msg.optionalGroup != null);

    msg.optionalGroup = grp;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.optionalGroup != null);
    assertEquals(1, newMsg.optionalGroup.a);
  }

  public void testNanoOptionalNestedMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    TestAllTypesNano.NestedMessage nestedMsg = new TestAllTypesNano.NestedMessage();
    nestedMsg.bb = 1;
    assertFalse(msg.optionalNestedMessage != null);
    msg.optionalNestedMessage = nestedMsg;
    assertTrue(msg.optionalNestedMessage != null);
    assertEquals(1, msg.optionalNestedMessage.bb);
    msg.clear();
    assertFalse(msg.optionalNestedMessage != null);
    msg.clear()
       .optionalNestedMessage = new TestAllTypesNano.NestedMessage();
    msg.optionalNestedMessage.bb = 2;
    assertTrue(msg.optionalNestedMessage != null);
    msg.clear();
    assertFalse(msg.optionalNestedMessage != null);

    msg.optionalNestedMessage = nestedMsg;
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.optionalNestedMessage != null);
    assertEquals(1, newMsg.optionalNestedMessage.bb);
  }

  /*
  public void testNanoOptionalForeignMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    NanoOuterClass.ForeignMessageNano foreignMsg =
        new NanoOuterClass.ForeignMessageNano();
    assertFalse(foreignMsg.hasC());
    foreignMsg.setC(1);
    assertTrue(foreignMsg.hasC());
    assertFalse(msg.hasOptionalForeignMessage());
    msg.setOptionalForeignMessage(foreignMsg);
    assertTrue(msg.hasOptionalForeignMessage());
    assertEquals(1, msg.getOptionalForeignMessage().getC());
    msg.clearOptionalForeignMessage();
    assertFalse(msg.hasOptionalForeignMessage());
    msg.clearOptionalForeignMessage()
       .setOptionalForeignMessage(new NanoOuterClass.ForeignMessageNano().setC(2));
    assertTrue(msg.hasOptionalForeignMessage());
    msg.clear();
    assertFalse(msg.hasOptionalForeignMessage());

    msg.setOptionalForeignMessage(foreignMsg);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalForeignMessage());
    assertEquals(1, newMsg.getOptionalForeignMessage().getC());
  }

  public void testNanoOptionalImportMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    UnittestImportNano.ImportMessageNano importMsg =
        new UnittestImportNano.ImportMessageNano();
    assertFalse(importMsg.hasD());
    importMsg.setD(1);
    assertTrue(importMsg.hasD());
    assertFalse(msg.hasOptionalImportMessage());
    msg.setOptionalImportMessage(importMsg);
    assertTrue(msg.hasOptionalImportMessage());
    assertEquals(1, msg.getOptionalImportMessage().getD());
    msg.clearOptionalImportMessage();
    assertFalse(msg.hasOptionalImportMessage());
    msg.clearOptionalImportMessage()
       .setOptionalImportMessage(new UnittestImportNano.ImportMessageNano().setD(2));
    assertTrue(msg.hasOptionalImportMessage());
    msg.clear();
    assertFalse(msg.hasOptionalImportMessage());

    msg.setOptionalImportMessage(importMsg);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalImportMessage());
    assertEquals(1, newMsg.getOptionalImportMessage().getD());
  }

  public void testNanoOptionalNestedEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.setOptionalNestedEnum(TestAllTypesNano.BAR);
    assertTrue(msg.hasOptionalNestedEnum());
    assertEquals(TestAllTypesNano.BAR, msg.getOptionalNestedEnum());
    msg.clearOptionalNestedEnum();
    assertFalse(msg.hasOptionalNestedEnum());
    msg.clearOptionalNestedEnum()
       .setOptionalNestedEnum(TestAllTypesNano.BAZ);
    assertTrue(msg.hasOptionalNestedEnum());
    msg.clear();
    assertFalse(msg.hasOptionalNestedEnum());

    msg.setOptionalNestedEnum(TestAllTypesNano.BAR);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalNestedEnum());
    assertEquals(TestAllTypesNano.BAR, newMsg.getOptionalNestedEnum());
  }

  public void testNanoOptionalForeignEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.setOptionalForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAR);
    assertTrue(msg.hasOptionalForeignEnum());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR,
        msg.getOptionalForeignEnum());
    msg.clearOptionalForeignEnum();
    assertFalse(msg.hasOptionalForeignEnum());
    msg.clearOptionalForeignEnum()
       .setOptionalForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAZ);
    assertTrue(msg.hasOptionalForeignEnum());
    msg.clear();
    assertFalse(msg.hasOptionalForeignEnum());

    msg.setOptionalForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAR);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalForeignEnum());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR,
        newMsg.getOptionalForeignEnum());
  }

  public void testNanoOptionalImportEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.setOptionalImportEnum(UnittestImportNano.IMPORT_MICRO_BAR);
    assertTrue(msg.hasOptionalImportEnum());
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR,
        msg.getOptionalImportEnum());
    msg.clearOptionalImportEnum();
    assertFalse(msg.hasOptionalImportEnum());
    msg.clearOptionalImportEnum()
       .setOptionalImportEnum(UnittestImportNano.IMPORT_MICRO_BAZ);
    assertTrue(msg.hasOptionalImportEnum());
    msg.clear();
    assertFalse(msg.hasOptionalImportEnum());

    msg.setOptionalImportEnum(UnittestImportNano.IMPORT_MICRO_BAR);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalImportEnum());
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR,
        newMsg.getOptionalImportEnum());
  }

  public void testNanoOptionalStringPiece() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalStringPiece());
    msg.setOptionalStringPiece("hello");
    assertTrue(msg.hasOptionalStringPiece());
    assertEquals("hello", msg.getOptionalStringPiece());
    msg.clearOptionalStringPiece();
    assertFalse(msg.hasOptionalStringPiece());
    msg.clearOptionalStringPiece()
       .setOptionalStringPiece("hello");
    assertTrue(msg.hasOptionalStringPiece());
    msg.clear();
    assertFalse(msg.hasOptionalStringPiece());

    msg.setOptionalStringPiece("bye");
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalStringPiece());
    assertEquals("bye", newMsg.getOptionalStringPiece());
  }

  public void testNanoOptionalCord() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasOptionalCord());
    msg.setOptionalCord("hello");
    assertTrue(msg.hasOptionalCord());
    assertEquals("hello", msg.getOptionalCord());
    msg.clearOptionalCord();
    assertFalse(msg.hasOptionalCord());
    msg.clearOptionalCord()
      .setOptionalCord("hello");
    assertTrue(msg.hasOptionalCord());
    msg.clear();
    assertFalse(msg.hasOptionalCord());

    msg.setOptionalCord("bye");
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertTrue(newMsg.hasOptionalCord());
    assertEquals("bye", newMsg.getOptionalCord());
  }

  public void testNanoRepeatedInt32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedInt32Count());
    msg.addRepeatedInt32(123);
    assertEquals(1, msg.getRepeatedInt32Count());
    assertEquals(123, msg.getRepeatedInt32(0));
    msg.addRepeatedInt32(456);
    assertEquals(2, msg.getRepeatedInt32Count());
    assertEquals(123, msg.getRepeatedInt32(0));
    assertEquals(456, msg.getRepeatedInt32(1));
    msg.setRepeatedInt32(0, 789);
    assertEquals(2, msg.getRepeatedInt32Count());
    assertEquals(789, msg.getRepeatedInt32(0));
    assertEquals(456, msg.getRepeatedInt32(1));
    msg.clearRepeatedInt32();
    assertEquals(0, msg.getRepeatedInt32Count());
    msg.clearRepeatedInt32()
       .addRepeatedInt32(456);
    assertEquals(1, msg.getRepeatedInt32Count());
    assertEquals(456, msg.getRepeatedInt32(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedInt32Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedInt32(123);
    assertEquals(1, msg.getRepeatedInt32Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedInt32Count());
    assertEquals(123, newMsg.getRepeatedInt32(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedInt32(123)
       .addRepeatedInt32(456);
    assertEquals(2, msg.getRepeatedInt32Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedInt32Count());
    assertEquals(123, newMsg.getRepeatedInt32(0));
    assertEquals(456, newMsg.getRepeatedInt32(1));
  }

  public void testNanoRepeatedInt64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedInt64Count());
    msg.addRepeatedInt64(123);
    assertEquals(1, msg.getRepeatedInt64Count());
    assertEquals(123, msg.getRepeatedInt64(0));
    msg.addRepeatedInt64(456);
    assertEquals(2, msg.getRepeatedInt64Count());
    assertEquals(123, msg.getRepeatedInt64(0));
    assertEquals(456, msg.getRepeatedInt64(1));
    msg.setRepeatedInt64(0, 789);
    assertEquals(2, msg.getRepeatedInt64Count());
    assertEquals(789, msg.getRepeatedInt64(0));
    assertEquals(456, msg.getRepeatedInt64(1));
    msg.clearRepeatedInt64();
    assertEquals(0, msg.getRepeatedInt64Count());
    msg.clearRepeatedInt64()
       .addRepeatedInt64(456);
    assertEquals(1, msg.getRepeatedInt64Count());
    assertEquals(456, msg.getRepeatedInt64(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedInt64Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedInt64(123);
    assertEquals(1, msg.getRepeatedInt64Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedInt64Count());
    assertEquals(123, newMsg.getRepeatedInt64(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedInt64(123)
       .addRepeatedInt64(456);
    assertEquals(2, msg.getRepeatedInt64Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedInt64Count());
    assertEquals(123, newMsg.getRepeatedInt64(0));
    assertEquals(456, newMsg.getRepeatedInt64(1));
  }

  public void testNanoRepeatedUint32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedUint32Count());
    msg.addRepeatedUint32(123);
    assertEquals(1, msg.getRepeatedUint32Count());
    assertEquals(123, msg.getRepeatedUint32(0));
    msg.addRepeatedUint32(456);
    assertEquals(2, msg.getRepeatedUint32Count());
    assertEquals(123, msg.getRepeatedUint32(0));
    assertEquals(456, msg.getRepeatedUint32(1));
    msg.setRepeatedUint32(0, 789);
    assertEquals(2, msg.getRepeatedUint32Count());
    assertEquals(789, msg.getRepeatedUint32(0));
    assertEquals(456, msg.getRepeatedUint32(1));
    msg.clearRepeatedUint32();
    assertEquals(0, msg.getRepeatedUint32Count());
    msg.clearRepeatedUint32()
       .addRepeatedUint32(456);
    assertEquals(1, msg.getRepeatedUint32Count());
    assertEquals(456, msg.getRepeatedUint32(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedUint32Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedUint32(123);
    assertEquals(1, msg.getRepeatedUint32Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedUint32Count());
    assertEquals(123, newMsg.getRepeatedUint32(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedUint32(123)
       .addRepeatedUint32(456);
    assertEquals(2, msg.getRepeatedUint32Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedUint32Count());
    assertEquals(123, newMsg.getRepeatedUint32(0));
    assertEquals(456, newMsg.getRepeatedUint32(1));
  }

  public void testNanoRepeatedUint64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedUint64Count());
    msg.addRepeatedUint64(123);
    assertEquals(1, msg.getRepeatedUint64Count());
    assertEquals(123, msg.getRepeatedUint64(0));
    msg.addRepeatedUint64(456);
    assertEquals(2, msg.getRepeatedUint64Count());
    assertEquals(123, msg.getRepeatedUint64(0));
    assertEquals(456, msg.getRepeatedUint64(1));
    msg.setRepeatedUint64(0, 789);
    assertEquals(2, msg.getRepeatedUint64Count());
    assertEquals(789, msg.getRepeatedUint64(0));
    assertEquals(456, msg.getRepeatedUint64(1));
    msg.clearRepeatedUint64();
    assertEquals(0, msg.getRepeatedUint64Count());
    msg.clearRepeatedUint64()
       .addRepeatedUint64(456);
    assertEquals(1, msg.getRepeatedUint64Count());
    assertEquals(456, msg.getRepeatedUint64(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedUint64Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedUint64(123);
    assertEquals(1, msg.getRepeatedUint64Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedUint64Count());
    assertEquals(123, newMsg.getRepeatedUint64(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedUint64(123)
       .addRepeatedUint64(456);
    assertEquals(2, msg.getRepeatedUint64Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedUint64Count());
    assertEquals(123, newMsg.getRepeatedUint64(0));
    assertEquals(456, newMsg.getRepeatedUint64(1));
  }

  public void testNanoRepeatedSint32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedSint32Count());
    msg.addRepeatedSint32(123);
    assertEquals(1, msg.getRepeatedSint32Count());
    assertEquals(123, msg.getRepeatedSint32(0));
    msg.addRepeatedSint32(456);
    assertEquals(2, msg.getRepeatedSint32Count());
    assertEquals(123, msg.getRepeatedSint32(0));
    assertEquals(456, msg.getRepeatedSint32(1));
    msg.setRepeatedSint32(0, 789);
    assertEquals(2, msg.getRepeatedSint32Count());
    assertEquals(789, msg.getRepeatedSint32(0));
    assertEquals(456, msg.getRepeatedSint32(1));
    msg.clearRepeatedSint32();
    assertEquals(0, msg.getRepeatedSint32Count());
    msg.clearRepeatedSint32()
       .addRepeatedSint32(456);
    assertEquals(1, msg.getRepeatedSint32Count());
    assertEquals(456, msg.getRepeatedSint32(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedSint32Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedSint32(123);
    assertEquals(1, msg.getRepeatedSint32Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 4);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedSint32Count());
    assertEquals(123, newMsg.getRepeatedSint32(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedSint32(123)
       .addRepeatedSint32(456);
    assertEquals(2, msg.getRepeatedSint32Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedSint32Count());
    assertEquals(123, newMsg.getRepeatedSint32(0));
    assertEquals(456, newMsg.getRepeatedSint32(1));
  }

  public void testNanoRepeatedSint64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedSint64Count());
    msg.addRepeatedSint64(123);
    assertEquals(1, msg.getRepeatedSint64Count());
    assertEquals(123, msg.getRepeatedSint64(0));
    msg.addRepeatedSint64(456);
    assertEquals(2, msg.getRepeatedSint64Count());
    assertEquals(123, msg.getRepeatedSint64(0));
    assertEquals(456, msg.getRepeatedSint64(1));
    msg.setRepeatedSint64(0, 789);
    assertEquals(2, msg.getRepeatedSint64Count());
    assertEquals(789, msg.getRepeatedSint64(0));
    assertEquals(456, msg.getRepeatedSint64(1));
    msg.clearRepeatedSint64();
    assertEquals(0, msg.getRepeatedSint64Count());
    msg.clearRepeatedSint64()
       .addRepeatedSint64(456);
    assertEquals(1, msg.getRepeatedSint64Count());
    assertEquals(456, msg.getRepeatedSint64(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedSint64Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedSint64(123);
    assertEquals(1, msg.getRepeatedSint64Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 4);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedSint64Count());
    assertEquals(123, newMsg.getRepeatedSint64(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedSint64(123)
       .addRepeatedSint64(456);
    assertEquals(2, msg.getRepeatedSint64Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedSint64Count());
    assertEquals(123, newMsg.getRepeatedSint64(0));
    assertEquals(456, newMsg.getRepeatedSint64(1));
  }

  public void testNanoRepeatedFixed32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedFixed32Count());
    msg.addRepeatedFixed32(123);
    assertEquals(1, msg.getRepeatedFixed32Count());
    assertEquals(123, msg.getRepeatedFixed32(0));
    msg.addRepeatedFixed32(456);
    assertEquals(2, msg.getRepeatedFixed32Count());
    assertEquals(123, msg.getRepeatedFixed32(0));
    assertEquals(456, msg.getRepeatedFixed32(1));
    msg.setRepeatedFixed32(0, 789);
    assertEquals(2, msg.getRepeatedFixed32Count());
    assertEquals(789, msg.getRepeatedFixed32(0));
    assertEquals(456, msg.getRepeatedFixed32(1));
    msg.clearRepeatedFixed32();
    assertEquals(0, msg.getRepeatedFixed32Count());
    msg.clearRepeatedFixed32()
       .addRepeatedFixed32(456);
    assertEquals(1, msg.getRepeatedFixed32Count());
    assertEquals(456, msg.getRepeatedFixed32(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedFixed32Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedFixed32(123);
    assertEquals(1, msg.getRepeatedFixed32Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedFixed32Count());
    assertEquals(123, newMsg.getRepeatedFixed32(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedFixed32(123)
       .addRepeatedFixed32(456);
    assertEquals(2, msg.getRepeatedFixed32Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedFixed32Count());
    assertEquals(123, newMsg.getRepeatedFixed32(0));
    assertEquals(456, newMsg.getRepeatedFixed32(1));
  }

  public void testNanoRepeatedFixed64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedFixed64Count());
    msg.addRepeatedFixed64(123);
    assertEquals(1, msg.getRepeatedFixed64Count());
    assertEquals(123, msg.getRepeatedFixed64(0));
    msg.addRepeatedFixed64(456);
    assertEquals(2, msg.getRepeatedFixed64Count());
    assertEquals(123, msg.getRepeatedFixed64(0));
    assertEquals(456, msg.getRepeatedFixed64(1));
    msg.setRepeatedFixed64(0, 789);
    assertEquals(2, msg.getRepeatedFixed64Count());
    assertEquals(789, msg.getRepeatedFixed64(0));
    assertEquals(456, msg.getRepeatedFixed64(1));
    msg.clearRepeatedFixed64();
    assertEquals(0, msg.getRepeatedFixed64Count());
    msg.clearRepeatedFixed64()
       .addRepeatedFixed64(456);
    assertEquals(1, msg.getRepeatedFixed64Count());
    assertEquals(456, msg.getRepeatedFixed64(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedFixed64Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedFixed64(123);
    assertEquals(1, msg.getRepeatedFixed64Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedFixed64Count());
    assertEquals(123, newMsg.getRepeatedFixed64(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedFixed64(123)
       .addRepeatedFixed64(456);
    assertEquals(2, msg.getRepeatedFixed64Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 20);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedFixed64Count());
    assertEquals(123, newMsg.getRepeatedFixed64(0));
    assertEquals(456, newMsg.getRepeatedFixed64(1));
  }

  public void testNanoRepeatedSfixed32() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedSfixed32Count());
    msg.addRepeatedSfixed32(123);
    assertEquals(1, msg.getRepeatedSfixed32Count());
    assertEquals(123, msg.getRepeatedSfixed32(0));
    msg.addRepeatedSfixed32(456);
    assertEquals(2, msg.getRepeatedSfixed32Count());
    assertEquals(123, msg.getRepeatedSfixed32(0));
    assertEquals(456, msg.getRepeatedSfixed32(1));
    msg.setRepeatedSfixed32(0, 789);
    assertEquals(2, msg.getRepeatedSfixed32Count());
    assertEquals(789, msg.getRepeatedSfixed32(0));
    assertEquals(456, msg.getRepeatedSfixed32(1));
    msg.clearRepeatedSfixed32();
    assertEquals(0, msg.getRepeatedSfixed32Count());
    msg.clearRepeatedSfixed32()
       .addRepeatedSfixed32(456);
    assertEquals(1, msg.getRepeatedSfixed32Count());
    assertEquals(456, msg.getRepeatedSfixed32(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedSfixed32Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedSfixed32(123);
    assertEquals(1, msg.getRepeatedSfixed32Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedSfixed32Count());
    assertEquals(123, newMsg.getRepeatedSfixed32(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedSfixed32(123)
       .addRepeatedSfixed32(456);
    assertEquals(2, msg.getRepeatedSfixed32Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedSfixed32Count());
    assertEquals(123, newMsg.getRepeatedSfixed32(0));
    assertEquals(456, newMsg.getRepeatedSfixed32(1));
  }

  public void testNanoRepeatedSfixed64() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedSfixed64Count());
    msg.addRepeatedSfixed64(123);
    assertEquals(1, msg.getRepeatedSfixed64Count());
    assertEquals(123, msg.getRepeatedSfixed64(0));
    msg.addRepeatedSfixed64(456);
    assertEquals(2, msg.getRepeatedSfixed64Count());
    assertEquals(123, msg.getRepeatedSfixed64(0));
    assertEquals(456, msg.getRepeatedSfixed64(1));
    msg.setRepeatedSfixed64(0, 789);
    assertEquals(2, msg.getRepeatedSfixed64Count());
    assertEquals(789, msg.getRepeatedSfixed64(0));
    assertEquals(456, msg.getRepeatedSfixed64(1));
    msg.clearRepeatedSfixed64();
    assertEquals(0, msg.getRepeatedSfixed64Count());
    msg.clearRepeatedSfixed64()
       .addRepeatedSfixed64(456);
    assertEquals(1, msg.getRepeatedSfixed64Count());
    assertEquals(456, msg.getRepeatedSfixed64(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedSfixed64Count());

    // Test 1 entry
    msg.clear()
       .addRepeatedSfixed64(123);
    assertEquals(1, msg.getRepeatedSfixed64Count());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedSfixed64Count());
    assertEquals(123, newMsg.getRepeatedSfixed64(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedSfixed64(123)
       .addRepeatedSfixed64(456);
    assertEquals(2, msg.getRepeatedSfixed64Count());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 20);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedSfixed64Count());
    assertEquals(123, newMsg.getRepeatedSfixed64(0));
    assertEquals(456, newMsg.getRepeatedSfixed64(1));
  }

  public void testNanoRepeatedFloat() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedFloatCount());
    msg.addRepeatedFloat(123f);
    assertEquals(1, msg.getRepeatedFloatCount());
    assertTrue(123f == msg.getRepeatedFloat(0));
    msg.addRepeatedFloat(456f);
    assertEquals(2, msg.getRepeatedFloatCount());
    assertTrue(123f == msg.getRepeatedFloat(0));
    assertTrue(456f == msg.getRepeatedFloat(1));
    msg.setRepeatedFloat(0, 789f);
    assertEquals(2, msg.getRepeatedFloatCount());
    assertTrue(789f == msg.getRepeatedFloat(0));
    assertTrue(456f == msg.getRepeatedFloat(1));
    msg.clearRepeatedFloat();
    assertEquals(0, msg.getRepeatedFloatCount());
    msg.clearRepeatedFloat()
       .addRepeatedFloat(456f);
    assertEquals(1, msg.getRepeatedFloatCount());
    assertTrue(456f == msg.getRepeatedFloat(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedFloatCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedFloat(123f);
    assertEquals(1, msg.getRepeatedFloatCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedFloatCount());
    assertTrue(123f == newMsg.getRepeatedFloat(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedFloat(123f)
       .addRepeatedFloat(456f);
    assertEquals(2, msg.getRepeatedFloatCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedFloatCount());
    assertTrue(123f == newMsg.getRepeatedFloat(0));
    assertTrue(456f == newMsg.getRepeatedFloat(1));
  }

  public void testNanoRepeatedDouble() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedDoubleCount());
    msg.addRepeatedDouble(123.0);
    assertEquals(1, msg.getRepeatedDoubleCount());
    assertTrue(123.0 == msg.getRepeatedDouble(0));
    msg.addRepeatedDouble(456.0);
    assertEquals(2, msg.getRepeatedDoubleCount());
    assertTrue(123.0 == msg.getRepeatedDouble(0));
    assertTrue(456.0 == msg.getRepeatedDouble(1));
    msg.setRepeatedDouble(0, 789.0);
    assertEquals(2, msg.getRepeatedDoubleCount());
    assertTrue(789.0 == msg.getRepeatedDouble(0));
    assertTrue(456.0 == msg.getRepeatedDouble(1));
    msg.clearRepeatedDouble();
    assertEquals(0, msg.getRepeatedDoubleCount());
    msg.clearRepeatedDouble()
       .addRepeatedDouble(456.0);
    assertEquals(1, msg.getRepeatedDoubleCount());
    assertTrue(456.0 == msg.getRepeatedDouble(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedDoubleCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedDouble(123.0);
    assertEquals(1, msg.getRepeatedDoubleCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedDoubleCount());
    assertTrue(123.0 == newMsg.getRepeatedDouble(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedDouble(123.0)
       .addRepeatedDouble(456.0);
    assertEquals(2, msg.getRepeatedDoubleCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 20);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedDoubleCount());
    assertTrue(123.0 == newMsg.getRepeatedDouble(0));
    assertTrue(456.0 == newMsg.getRepeatedDouble(1));
  }

  public void testNanoRepeatedBool() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedBoolCount());
    msg.addRepeatedBool(true);
    assertEquals(1, msg.getRepeatedBoolCount());
    assertEquals(true, msg.getRepeatedBool(0));
    msg.addRepeatedBool(false);
    assertEquals(2, msg.getRepeatedBoolCount());
    assertEquals(true, msg.getRepeatedBool(0));
    assertEquals(false, msg.getRepeatedBool(1));
    msg.setRepeatedBool(0, false);
    assertEquals(2, msg.getRepeatedBoolCount());
    assertEquals(false, msg.getRepeatedBool(0));
    assertEquals(false, msg.getRepeatedBool(1));
    msg.clearRepeatedBool();
    assertEquals(0, msg.getRepeatedBoolCount());
    msg.clearRepeatedBool()
       .addRepeatedBool(true);
    assertEquals(1, msg.getRepeatedBoolCount());
    assertEquals(true, msg.getRepeatedBool(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedBoolCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedBool(false);
    assertEquals(1, msg.getRepeatedBoolCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedBoolCount());
    assertEquals(false, newMsg.getRepeatedBool(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedBool(true)
       .addRepeatedBool(false);
    assertEquals(2, msg.getRepeatedBoolCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedBoolCount());
    assertEquals(true, newMsg.getRepeatedBool(0));
    assertEquals(false, newMsg.getRepeatedBool(1));
  }

  public void testNanoRepeatedString() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedStringCount());
    msg.addRepeatedString("hello");
    assertEquals(1, msg.getRepeatedStringCount());
    assertEquals("hello", msg.getRepeatedString(0));
    msg.addRepeatedString("bye");
    assertEquals(2, msg.getRepeatedStringCount());
    assertEquals("hello", msg.getRepeatedString(0));
    assertEquals("bye", msg.getRepeatedString(1));
    msg.setRepeatedString(0, "boo");
    assertEquals(2, msg.getRepeatedStringCount());
    assertEquals("boo", msg.getRepeatedString(0));
    assertEquals("bye", msg.getRepeatedString(1));
    msg.clearRepeatedString();
    assertEquals(0, msg.getRepeatedStringCount());
    msg.clearRepeatedString()
       .addRepeatedString("hello");
    assertEquals(1, msg.getRepeatedStringCount());
    assertEquals("hello", msg.getRepeatedString(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedStringCount());

    // Test 1 entry and an empty string
    msg.clear()
       .addRepeatedString("");
    assertEquals(1, msg.getRepeatedStringCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedStringCount());
    assertEquals("", newMsg.getRepeatedString(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedString("hello")
       .addRepeatedString("world");
    assertEquals(2, msg.getRepeatedStringCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 16);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedStringCount());
    assertEquals("hello", newMsg.getRepeatedString(0));
    assertEquals("world", newMsg.getRepeatedString(1));
  }

  public void testNanoRepeatedBytes() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedBytesCount());
    msg.addRepeatedBytes(ByteStringNano.copyFromUtf8("hello"));
    assertEquals(1, msg.getRepeatedBytesCount());
    assertEquals("hello", msg.getRepeatedBytes(0).toStringUtf8());
    msg.addRepeatedBytes(ByteStringNano.copyFromUtf8("bye"));
    assertEquals(2, msg.getRepeatedBytesCount());
    assertEquals("hello", msg.getRepeatedBytes(0).toStringUtf8());
    assertEquals("bye", msg.getRepeatedBytes(1).toStringUtf8());
    msg.setRepeatedBytes(0, ByteStringNano.copyFromUtf8("boo"));
    assertEquals(2, msg.getRepeatedBytesCount());
    assertEquals("boo", msg.getRepeatedBytes(0).toStringUtf8());
    assertEquals("bye", msg.getRepeatedBytes(1).toStringUtf8());
    msg.clearRepeatedBytes();
    assertEquals(0, msg.getRepeatedBytesCount());
    msg.clearRepeatedBytes()
       .addRepeatedBytes(ByteStringNano.copyFromUtf8("hello"));
    assertEquals(1, msg.getRepeatedBytesCount());
    assertEquals("hello", msg.getRepeatedBytes(0).toStringUtf8());
    msg.clear();
    assertEquals(0, msg.getRepeatedBytesCount());

    // Test 1 entry and an empty byte array can be serialized
    msg.clear()
       .addRepeatedBytes(ByteStringNano.copyFromUtf8(""));
    assertEquals(1, msg.getRepeatedBytesCount());
    assertEquals("", msg.getRepeatedBytes(0).toStringUtf8());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedBytesCount());
    assertEquals("", newMsg.getRepeatedBytes(0).toStringUtf8());

    // Test 2 entries
    msg.clear()
       .addRepeatedBytes(ByteStringNano.copyFromUtf8("hello"))
       .addRepeatedBytes(ByteStringNano.copyFromUtf8("world"));
    assertEquals(2, msg.getRepeatedBytesCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 16);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedBytesCount());
    assertEquals("hello", newMsg.getRepeatedBytes(0).toStringUtf8());
    assertEquals("world", newMsg.getRepeatedBytes(1).toStringUtf8());
  }

  public void testNanoRepeatedGroup() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    TestAllTypesNano.RepeatedGroup group0 =
      new TestAllTypesNano.RepeatedGroup().a = 0);
    TestAllTypesNano.RepeatedGroup group1 =
      new TestAllTypesNano.RepeatedGroup().a = 1);
    TestAllTypesNano.RepeatedGroup group2 =
      new TestAllTypesNano.RepeatedGroup().a = 2);

    msg.addRepeatedGroup(group0);
    assertEquals(1, msg.getRepeatedGroupCount());
    assertEquals(0, msg.getRepeatedGroup(0).a);
    msg.addRepeatedGroup(group1);
    assertEquals(2, msg.getRepeatedGroupCount());
    assertEquals(0, msg.getRepeatedGroup(0).a);
    assertEquals(1, msg.getRepeatedGroup(1).a);
    msg.setRepeatedGroup(0, group2);
    assertEquals(2, msg.getRepeatedGroupCount());
    assertEquals(2, msg.getRepeatedGroup(0).a);
    assertEquals(1, msg.getRepeatedGroup(1).a);
    msg.clearRepeatedGroup();
    assertEquals(0, msg.getRepeatedGroupCount());
    msg.clearRepeatedGroup()
       .addRepeatedGroup(group1);
    assertEquals(1, msg.getRepeatedGroupCount());
    assertEquals(1, msg.getRepeatedGroup(0).a);
    msg.clear();
    assertEquals(0, msg.getRepeatedGroupCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedGroup(group0);
    assertEquals(1, msg.getRepeatedGroupCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedGroupCount());
    assertEquals(0, newMsg.getRepeatedGroup(0).a);

    // Test 2 entries
    msg.clear()
       .addRepeatedGroup(group0)
       .addRepeatedGroup(group1);
    assertEquals(2, msg.getRepeatedGroupCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 14);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedGroupCount());
    assertEquals(0, newMsg.getRepeatedGroup(0).a);
    assertEquals(1, newMsg.getRepeatedGroup(1).a);
  }


  public void testNanoRepeatedNestedMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    TestAllTypesNano.NestedMessage nestedMsg0 =
      new TestAllTypesNano.NestedMessage().setBb(0);
    TestAllTypesNano.NestedMessage nestedMsg1 =
      new TestAllTypesNano.NestedMessage().setBb(1);
    TestAllTypesNano.NestedMessage nestedMsg2 =
      new TestAllTypesNano.NestedMessage().setBb(2);

    msg.addRepeatedNestedMessage(nestedMsg0);
    assertEquals(1, msg.getRepeatedNestedMessageCount());
    assertEquals(0, msg.getRepeatedNestedMessage(0).bb);
    msg.addRepeatedNestedMessage(nestedMsg1);
    assertEquals(2, msg.getRepeatedNestedMessageCount());
    assertEquals(0, msg.getRepeatedNestedMessage(0).bb);
    assertEquals(1, msg.getRepeatedNestedMessage(1).bb);
    msg.setRepeatedNestedMessage(0, nestedMsg2);
    assertEquals(2, msg.getRepeatedNestedMessageCount());
    assertEquals(2, msg.getRepeatedNestedMessage(0).bb);
    assertEquals(1, msg.getRepeatedNestedMessage(1).bb);
    msg.clearRepeatedNestedMessage();
    assertEquals(0, msg.getRepeatedNestedMessageCount());
    msg.clearRepeatedNestedMessage()
       .addRepeatedNestedMessage(nestedMsg1);
    assertEquals(1, msg.getRepeatedNestedMessageCount());
    assertEquals(1, msg.getRepeatedNestedMessage(0).bb);
    msg.clear();
    assertEquals(0, msg.getRepeatedNestedMessageCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedNestedMessage(nestedMsg0);
    assertEquals(1, msg.getRepeatedNestedMessageCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedNestedMessageCount());
    assertEquals(0, newMsg.getRepeatedNestedMessage(0).bb);

    // Test 2 entries
    msg.clear()
       .addRepeatedNestedMessage(nestedMsg0)
       .addRepeatedNestedMessage(nestedMsg1);
    assertEquals(2, msg.getRepeatedNestedMessageCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedNestedMessageCount());
    assertEquals(0, newMsg.getRepeatedNestedMessage(0).bb);
    assertEquals(1, newMsg.getRepeatedNestedMessage(1).bb);
  }

  public void testNanoRepeatedForeignMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    NanoOuterClass.ForeignMessageNano foreignMsg0 =
      new NanoOuterClass.ForeignMessageNano().setC(0);
    NanoOuterClass.ForeignMessageNano foreignMsg1 =
      new NanoOuterClass.ForeignMessageNano().setC(1);
    NanoOuterClass.ForeignMessageNano foreignMsg2 =
      new NanoOuterClass.ForeignMessageNano().setC(2);

    msg.addRepeatedForeignMessage(foreignMsg0);
    assertEquals(1, msg.getRepeatedForeignMessageCount());
    assertEquals(0, msg.getRepeatedForeignMessage(0).getC());
    msg.addRepeatedForeignMessage(foreignMsg1);
    assertEquals(2, msg.getRepeatedForeignMessageCount());
    assertEquals(0, msg.getRepeatedForeignMessage(0).getC());
    assertEquals(1, msg.getRepeatedForeignMessage(1).getC());
    msg.setRepeatedForeignMessage(0, foreignMsg2);
    assertEquals(2, msg.getRepeatedForeignMessageCount());
    assertEquals(2, msg.getRepeatedForeignMessage(0).getC());
    assertEquals(1, msg.getRepeatedForeignMessage(1).getC());
    msg.clearRepeatedForeignMessage();
    assertEquals(0, msg.getRepeatedForeignMessageCount());
    msg.clearRepeatedForeignMessage()
       .addRepeatedForeignMessage(foreignMsg1);
    assertEquals(1, msg.getRepeatedForeignMessageCount());
    assertEquals(1, msg.getRepeatedForeignMessage(0).getC());
    msg.clear();
    assertEquals(0, msg.getRepeatedForeignMessageCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedForeignMessage(foreignMsg0);
    assertEquals(1, msg.getRepeatedForeignMessageCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedForeignMessageCount());
    assertEquals(0, newMsg.getRepeatedForeignMessage(0).getC());

    // Test 2 entries
    msg.clear()
       .addRepeatedForeignMessage(foreignMsg0)
       .addRepeatedForeignMessage(foreignMsg1);
    assertEquals(2, msg.getRepeatedForeignMessageCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedForeignMessageCount());
    assertEquals(0, newMsg.getRepeatedForeignMessage(0).getC());
    assertEquals(1, newMsg.getRepeatedForeignMessage(1).getC());
  }

  public void testNanoRepeatedImportMessage() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    UnittestImportNano.ImportMessageNano importMsg0 =
      new UnittestImportNano.ImportMessageNano().setD(0);
    UnittestImportNano.ImportMessageNano importMsg1 =
      new UnittestImportNano.ImportMessageNano().setD(1);
    UnittestImportNano.ImportMessageNano importMsg2 =
      new UnittestImportNano.ImportMessageNano().setD(2);

    msg.addRepeatedImportMessage(importMsg0);
    assertEquals(1, msg.getRepeatedImportMessageCount());
    assertEquals(0, msg.getRepeatedImportMessage(0).getD());
    msg.addRepeatedImportMessage(importMsg1);
    assertEquals(2, msg.getRepeatedImportMessageCount());
    assertEquals(0, msg.getRepeatedImportMessage(0).getD());
    assertEquals(1, msg.getRepeatedImportMessage(1).getD());
    msg.setRepeatedImportMessage(0, importMsg2);
    assertEquals(2, msg.getRepeatedImportMessageCount());
    assertEquals(2, msg.getRepeatedImportMessage(0).getD());
    assertEquals(1, msg.getRepeatedImportMessage(1).getD());
    msg.clearRepeatedImportMessage();
    assertEquals(0, msg.getRepeatedImportMessageCount());
    msg.clearRepeatedImportMessage()
       .addRepeatedImportMessage(importMsg1);
    assertEquals(1, msg.getRepeatedImportMessageCount());
    assertEquals(1, msg.getRepeatedImportMessage(0).getD());
    msg.clear();
    assertEquals(0, msg.getRepeatedImportMessageCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedImportMessage(importMsg0);
    assertEquals(1, msg.getRepeatedImportMessageCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedImportMessageCount());
    assertEquals(0, newMsg.getRepeatedImportMessage(0).getD());

    // Test 2 entries
    msg.clear()
       .addRepeatedImportMessage(importMsg0)
       .addRepeatedImportMessage(importMsg1);
    assertEquals(2, msg.getRepeatedImportMessageCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedImportMessageCount());
    assertEquals(0, newMsg.getRepeatedImportMessage(0).getD());
    assertEquals(1, newMsg.getRepeatedImportMessage(1).getD());
  }

  public void testNanoRepeatedNestedEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.addRepeatedNestedEnum(TestAllTypesNano.FOO);
    assertEquals(1, msg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.FOO, msg.getRepeatedNestedEnum(0));
    msg.addRepeatedNestedEnum(TestAllTypesNano.BAR);
    assertEquals(2, msg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.FOO, msg.getRepeatedNestedEnum(0));
    assertEquals(TestAllTypesNano.BAR, msg.getRepeatedNestedEnum(1));
    msg.setRepeatedNestedEnum(0, TestAllTypesNano.BAZ);
    assertEquals(2, msg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.BAZ, msg.getRepeatedNestedEnum(0));
    assertEquals(TestAllTypesNano.BAR, msg.getRepeatedNestedEnum(1));
    msg.clearRepeatedNestedEnum();
    assertEquals(0, msg.getRepeatedNestedEnumCount());
    msg.clearRepeatedNestedEnum()
       .addRepeatedNestedEnum(TestAllTypesNano.BAR);
    assertEquals(1, msg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.BAR, msg.getRepeatedNestedEnum(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedNestedEnumCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedNestedEnum(TestAllTypesNano.FOO);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.FOO, msg.getRepeatedNestedEnum(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedNestedEnum(TestAllTypesNano.FOO)
       .addRepeatedNestedEnum(TestAllTypesNano.BAR);
    assertEquals(2, msg.getRepeatedNestedEnumCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedNestedEnumCount());
    assertEquals(TestAllTypesNano.FOO, msg.getRepeatedNestedEnum(0));
    assertEquals(TestAllTypesNano.BAR, msg.getRepeatedNestedEnum(1));
  }

  public void testNanoRepeatedForeignEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_FOO);
    assertEquals(1, msg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_FOO, msg.getRepeatedForeignEnum(0));
    msg.addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAR);
    assertEquals(2, msg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_FOO, msg.getRepeatedForeignEnum(0));
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR, msg.getRepeatedForeignEnum(1));
    msg.setRepeatedForeignEnum(0, NanoOuterClass.FOREIGN_MICRO_BAZ);
    assertEquals(2, msg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAZ, msg.getRepeatedForeignEnum(0));
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR, msg.getRepeatedForeignEnum(1));
    msg.clearRepeatedForeignEnum();
    assertEquals(0, msg.getRepeatedForeignEnumCount());
    msg.clearRepeatedForeignEnum()
       .addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAR);
    assertEquals(1, msg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR, msg.getRepeatedForeignEnum(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedForeignEnumCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_FOO);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_FOO, msg.getRepeatedForeignEnum(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_FOO)
       .addRepeatedForeignEnum(NanoOuterClass.FOREIGN_MICRO_BAR);
    assertEquals(2, msg.getRepeatedForeignEnumCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedForeignEnumCount());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_FOO, msg.getRepeatedForeignEnum(0));
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR, msg.getRepeatedForeignEnum(1));
  }

  public void testNanoRepeatedImportEnum() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    msg.addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_FOO);
    assertEquals(1, msg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_FOO, msg.getRepeatedImportEnum(0));
    msg.addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_BAR);
    assertEquals(2, msg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_FOO, msg.getRepeatedImportEnum(0));
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR, msg.getRepeatedImportEnum(1));
    msg.setRepeatedImportEnum(0, UnittestImportNano.IMPORT_MICRO_BAZ);
    assertEquals(2, msg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAZ, msg.getRepeatedImportEnum(0));
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR, msg.getRepeatedImportEnum(1));
    msg.clearRepeatedImportEnum();
    assertEquals(0, msg.getRepeatedImportEnumCount());
    msg.clearRepeatedImportEnum()
       .addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_BAR);
    assertEquals(1, msg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR, msg.getRepeatedImportEnum(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedImportEnumCount());

    // Test 1 entry
    msg.clear()
       .addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_FOO);
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_FOO, msg.getRepeatedImportEnum(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_FOO)
       .addRepeatedImportEnum(UnittestImportNano.IMPORT_MICRO_BAR);
    assertEquals(2, msg.getRepeatedImportEnumCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedImportEnumCount());
    assertEquals(UnittestImportNano.IMPORT_MICRO_FOO, msg.getRepeatedImportEnum(0));
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR, msg.getRepeatedImportEnum(1));
  }

  public void testNanoRepeatedStringPiece() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedStringPieceCount());
    msg.addRepeatedStringPiece("hello");
    assertEquals(1, msg.getRepeatedStringPieceCount());
    assertEquals("hello", msg.getRepeatedStringPiece(0));
    msg.addRepeatedStringPiece("bye");
    assertEquals(2, msg.getRepeatedStringPieceCount());
    assertEquals("hello", msg.getRepeatedStringPiece(0));
    assertEquals("bye", msg.getRepeatedStringPiece(1));
    msg.setRepeatedStringPiece(0, "boo");
    assertEquals(2, msg.getRepeatedStringPieceCount());
    assertEquals("boo", msg.getRepeatedStringPiece(0));
    assertEquals("bye", msg.getRepeatedStringPiece(1));
    msg.clearRepeatedStringPiece();
    assertEquals(0, msg.getRepeatedStringPieceCount());
    msg.clearRepeatedStringPiece()
       .addRepeatedStringPiece("hello");
    assertEquals(1, msg.getRepeatedStringPieceCount());
    assertEquals("hello", msg.getRepeatedStringPiece(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedStringPieceCount());

    // Test 1 entry and an empty string
    msg.clear()
       .addRepeatedStringPiece("");
    assertEquals(1, msg.getRepeatedStringPieceCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedStringPieceCount());
    assertEquals("", newMsg.getRepeatedStringPiece(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedStringPiece("hello")
       .addRepeatedStringPiece("world");
    assertEquals(2, msg.getRepeatedStringPieceCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 16);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedStringPieceCount());
    assertEquals("hello", newMsg.getRepeatedStringPiece(0));
    assertEquals("world", newMsg.getRepeatedStringPiece(1));
  }

  public void testNanoRepeatedCord() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertEquals(0, msg.getRepeatedCordCount());
    msg.addRepeatedCord("hello");
    assertEquals(1, msg.getRepeatedCordCount());
    assertEquals("hello", msg.getRepeatedCord(0));
    msg.addRepeatedCord("bye");
    assertEquals(2, msg.getRepeatedCordCount());
    assertEquals("hello", msg.getRepeatedCord(0));
    assertEquals("bye", msg.getRepeatedCord(1));
    msg.setRepeatedCord(0, "boo");
    assertEquals(2, msg.getRepeatedCordCount());
    assertEquals("boo", msg.getRepeatedCord(0));
    assertEquals("bye", msg.getRepeatedCord(1));
    msg.clearRepeatedCord();
    assertEquals(0, msg.getRepeatedCordCount());
    msg.clearRepeatedCord()
       .addRepeatedCord("hello");
    assertEquals(1, msg.getRepeatedCordCount());
    assertEquals("hello", msg.getRepeatedCord(0));
    msg.clear();
    assertEquals(0, msg.getRepeatedCordCount());

    // Test 1 entry and an empty string
    msg.clear()
       .addRepeatedCord("");
    assertEquals(1, msg.getRepeatedCordCount());
    byte [] result = msg.toByteArray();
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesNano newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(1, newMsg.getRepeatedCordCount());
    assertEquals("", newMsg.getRepeatedCord(0));

    // Test 2 entries
    msg.clear()
       .addRepeatedCord("hello")
       .addRepeatedCord("world");
    assertEquals(2, msg.getRepeatedCordCount());
    result = msg.toByteArray();
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 16);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesNano.parseFrom(result);
    assertEquals(2, newMsg.getRepeatedCordCount());
    assertEquals("hello", newMsg.getRepeatedCord(0));
    assertEquals("world", newMsg.getRepeatedCord(1));
  }

  public void testNanoDefaults() throws Exception {
    TestAllTypesNano msg = new TestAllTypesNano();
    assertFalse(msg.hasDefaultInt32());
    assertEquals(41, msg.getDefaultInt32());
    assertFalse(msg.hasDefaultInt64());
    assertEquals(42, msg.getDefaultInt64());
    assertFalse(msg.hasDefaultUint32());
    assertEquals(43, msg.getDefaultUint32());
    assertFalse(msg.hasDefaultUint64());
    assertEquals(44, msg.getDefaultUint64());
    assertFalse(msg.hasDefaultSint32());
    assertEquals(-45, msg.getDefaultSint32());
    assertFalse(msg.hasDefaultSint64());
    assertEquals(46, msg.getDefaultSint64());
    assertFalse(msg.hasDefaultFixed32());
    assertEquals(47, msg.getDefaultFixed32());
    assertFalse(msg.hasDefaultFixed64());
    assertEquals(48, msg.getDefaultFixed64());
    assertFalse(msg.hasDefaultSfixed32());
    assertEquals(49, msg.getDefaultSfixed32());
    assertFalse(msg.hasDefaultSfixed64());
    assertEquals(-50, msg.getDefaultSfixed64());
    assertFalse(msg.hasDefaultFloat());
    assertTrue(51.5f == msg.getDefaultFloat());
    assertFalse(msg.hasDefaultDouble());
    assertTrue(52.0e3 == msg.getDefaultDouble());
    assertFalse(msg.hasDefaultBool());
    assertEquals(true, msg.getDefaultBool());
    assertFalse(msg.hasDefaultString());
    assertEquals("hello", msg.getDefaultString());
    assertFalse(msg.hasDefaultBytes());
    assertEquals("world", msg.getDefaultBytes().toStringUtf8());
    assertFalse(msg.hasDefaultNestedEnum());
    assertEquals(TestAllTypesNano.BAR, msg.getDefaultNestedEnum());
    assertFalse(msg.hasDefaultForeignEnum());
    assertEquals(NanoOuterClass.FOREIGN_MICRO_BAR, msg.getDefaultForeignEnum());
    assertFalse(msg.hasDefaultImportEnum());
    assertEquals(UnittestImportNano.IMPORT_MICRO_BAR, msg.getDefaultImportEnum());
  }
  */

  /**
   * Test that a bug in skipRawBytes() has been fixed:  if the skip skips
   * exactly up to a limit, this should not break things.
   */
  public void testSkipRawBytesBug() throws Exception {
    byte[] rawBytes = new byte[] { 1, 2 };
    CodedInputStreamNano input = CodedInputStreamNano.newInstance(rawBytes);

    int limit = input.pushLimit(1);
    input.skipRawBytes(1);
    input.popLimit(limit);
    assertEquals(2, input.readRawByte());
  }

  /**
   * Test that a bug in skipRawBytes() has been fixed:  if the skip skips
   * past the end of a buffer with a limit that has been set past the end of
   * that buffer, this should not break things.
   */
  public void testSkipRawBytesPastEndOfBufferWithLimit() throws Exception {
    byte[] rawBytes = new byte[] { 1, 2, 3, 4, 5 };
    CodedInputStreamNano input = CodedInputStreamNano.newInstance(rawBytes);

    int limit = input.pushLimit(4);
    // In order to expose the bug we need to read at least one byte to prime the
    // buffer inside the CodedInputStream.
    assertEquals(1, input.readRawByte());
    // Skip to the end of the limit.
    input.skipRawBytes(3);
    assertTrue(input.isAtEnd());
    input.popLimit(limit);
    assertEquals(5, input.readRawByte());
  }
}
