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

package com.google.protobuf;

import com.google.protobuf.mini.CodedInputByteBufferMini;
import com.google.protobuf.mini.Extensions;
import com.google.protobuf.mini.Extensions.AnotherMessage;
import com.google.protobuf.mini.FileScopeEnumRefMini;
import com.google.protobuf.mini.InternalMini;
import com.google.protobuf.mini.MessageMini;
import com.google.protobuf.mini.MessageScopeEnumRefMini;
import com.google.protobuf.mini.MultipleImportingNonMultipleMini1;
import com.google.protobuf.mini.MultipleImportingNonMultipleMini2;
import com.google.protobuf.mini.MultipleNameClashMini;
import com.google.protobuf.mini.MiniHasOuterClass.TestAllTypesMiniHas;
import com.google.protobuf.mini.MiniOuterClass;
import com.google.protobuf.mini.MiniOuterClass.TestAllTypesMini;
import com.google.protobuf.mini.UnittestImportMini;
import com.google.protobuf.mini.UnittestMultipleMini;
import com.google.protobuf.mini.UnittestRecursiveMini.RecursiveMessageMini;
import com.google.protobuf.mini.UnittestSimpleMini.SimpleMessageMini;
import com.google.protobuf.mini.UnittestSingleMini.SingleMessageMini;
import com.google.protobuf.mini.UnittestStringutf8Mini.StringUtf8;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test mini runtime.
 *
 * @author ulas@google.com Ulas Kirazci
 */
public class MiniTest extends TestCase {
  public void setUp() throws Exception {
  }

  public void testSimpleMessageMini() throws Exception {
    SimpleMessageMini msg = new SimpleMessageMini();
    assertEquals(123, msg.d);
    assertEquals(null, msg.nestedMsg);
    assertEquals(SimpleMessageMini.BAZ, msg.defaultNestedEnum);

    msg.d = 456;
    assertEquals(456, msg.d);

    SimpleMessageMini.NestedMessage nestedMsg = new SimpleMessageMini.NestedMessage();
    nestedMsg.bb = 2;
    assertEquals(2, nestedMsg.bb);
    msg.nestedMsg = nestedMsg;
    assertEquals(2, msg.nestedMsg.bb);

    msg.defaultNestedEnum = SimpleMessageMini.BAR;
    assertEquals(SimpleMessageMini.BAR, msg.defaultNestedEnum);

    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    SimpleMessageMini newMsg = SimpleMessageMini.parseFrom(result);
    assertEquals(456, newMsg.d);
    assertEquals(2, msg.nestedMsg.bb);
    assertEquals(SimpleMessageMini.BAR, msg.defaultNestedEnum);
  }

  public void testRecursiveMessageMini() throws Exception {
    RecursiveMessageMini msg = new RecursiveMessageMini();
    assertTrue(msg.repeatedRecursiveMessageMini.length == 0);

    RecursiveMessageMini msg1 = new RecursiveMessageMini();
    msg1.id = 1;
    assertEquals(1, msg1.id);
    RecursiveMessageMini msg2 = new RecursiveMessageMini();
    msg2.id = 2;
    RecursiveMessageMini msg3 = new RecursiveMessageMini();
    msg3.id = 3;

    RecursiveMessageMini.NestedMessage nestedMsg = new RecursiveMessageMini.NestedMessage();
    nestedMsg.a = msg1;
    assertEquals(1, nestedMsg.a.id);

    msg.id = 0;
    msg.nestedMessage = nestedMsg;
    msg.optionalRecursiveMessageMini = msg2;
    msg.repeatedRecursiveMessageMini = new RecursiveMessageMini[] { msg3 };

    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 16);
    assertEquals(result.length, msgSerializedSize);

    RecursiveMessageMini newMsg = RecursiveMessageMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedRecursiveMessageMini.length);

    assertEquals(0, newMsg.id);
    assertEquals(1, newMsg.nestedMessage.a.id);
    assertEquals(2, newMsg.optionalRecursiveMessageMini.id);
    assertEquals(3, newMsg.repeatedRecursiveMessageMini[0].id);
  }

  public void testMiniRequiredInt32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.id = 123;
    assertEquals(123, msg.id);
    msg.clear().id = 456;
    assertEquals(456, msg.id);
    msg.clear();

    msg.id = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 3);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.id);
  }

  public void testMiniOptionalInt32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalInt32 = 123;
    assertEquals(123, msg.optionalInt32);
    msg.clear()
       .optionalInt32 = 456;
    assertEquals(456, msg.optionalInt32);
    msg.clear();

    msg.optionalInt32 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalInt32);
  }

  public void testMiniOptionalInt64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalInt64 = 123;
    assertEquals(123, msg.optionalInt64);
    msg.clear()
       .optionalInt64 = 456;
    assertEquals(456, msg.optionalInt64);
    msg.clear();
    assertEquals(0, msg.optionalInt64);

    msg.optionalInt64 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalInt64);
  }

  public void testMiniOptionalUint32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalUint32 = 123;
    assertEquals(123, msg.optionalUint32);
    msg.clear()
       .optionalUint32 = 456;
    assertEquals(456, msg.optionalUint32);
    msg.clear();
    assertEquals(0, msg.optionalUint32);

    msg.optionalUint32 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalUint32);
  }

  public void testMiniOptionalUint64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalUint64 = 123;
    assertEquals(123, msg.optionalUint64);
    msg.clear()
       .optionalUint64 = 456;
    assertEquals(456, msg.optionalUint64);
    msg.clear();
    assertEquals(0, msg.optionalUint64);

    msg.optionalUint64 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalUint64);
  }

  public void testMiniOptionalSint32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalSint32 = 123;
    assertEquals(123, msg.optionalSint32);
    msg.clear()
       .optionalSint32 = 456;
    assertEquals(456, msg.optionalSint32);
    msg.clear();
    assertEquals(0, msg.optionalSint32);

    msg.optionalSint32 = -123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(-123, newMsg.optionalSint32);
  }

  public void testMiniOptionalSint64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalSint64 = 123;
    assertEquals(123, msg.optionalSint64);
    msg.clear()
       .optionalSint64 = 456;
    assertEquals(456, msg.optionalSint64);
    msg.clear();
    assertEquals(0, msg.optionalSint64);

    msg.optionalSint64 = -123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(-123, newMsg.optionalSint64);
  }

  public void testMiniOptionalFixed32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalFixed32 = 123;
    assertEquals(123, msg.optionalFixed32);
    msg.clear()
       .optionalFixed32 = 456;
    assertEquals(456, msg.optionalFixed32);
    msg.clear();
    assertEquals(0, msg.optionalFixed32);

    msg.optionalFixed32 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalFixed32);
  }

  public void testMiniOptionalFixed64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalFixed64 = 123;
    assertEquals(123, msg.optionalFixed64);
    msg.clear()
       .optionalFixed64 = 456;
    assertEquals(456, msg.optionalFixed64);
    msg.clear();
    assertEquals(0, msg.optionalFixed64);

    msg.optionalFixed64 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalFixed64);
  }

  public void testMiniOptionalSfixed32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalSfixed32 = 123;
    assertEquals(123, msg.optionalSfixed32);
    msg.clear()
       .optionalSfixed32 = 456;
    assertEquals(456, msg.optionalSfixed32);
    msg.clear();
    assertEquals(0, msg.optionalSfixed32);

    msg.optionalSfixed32 = 123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(123, newMsg.optionalSfixed32);
  }

  public void testMiniOptionalSfixed64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalSfixed64 = 123;
    assertEquals(123, msg.optionalSfixed64);
    msg.clear()
       .optionalSfixed64 = 456;
    assertEquals(456, msg.optionalSfixed64);
    msg.clear();
    assertEquals(0, msg.optionalSfixed64);

    msg.optionalSfixed64 = -123;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(-123, newMsg.optionalSfixed64);
  }

  public void testMiniOptionalFloat() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalFloat = 123f;
    assertTrue(123.0f == msg.optionalFloat);
    msg.clear()
       .optionalFloat = 456.0f;
    assertTrue(456.0f == msg.optionalFloat);
    msg.clear();
    assertTrue(0.0f == msg.optionalFloat);

    msg.optionalFloat = -123.456f;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(-123.456f == newMsg.optionalFloat);
  }

  public void testMiniOptionalDouble() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalDouble = 123;
    assertTrue(123.0 == msg.optionalDouble);
    msg.clear()
       .optionalDouble = 456.0;
    assertTrue(456.0 == msg.optionalDouble);
    msg.clear();
    assertTrue(0.0 == msg.optionalDouble);

    msg.optionalDouble = -123.456;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 12);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(-123.456 == newMsg.optionalDouble);
  }

  public void testMiniOptionalBool() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalBool = true;
    assertTrue(msg.optionalBool);
    msg.clear()
       .optionalBool = true;
    assertTrue(msg.optionalBool);
    msg.clear();
    assertFalse(msg.optionalBool);

    msg.optionalBool = true;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 5);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalBool);
  }

  public void testMiniOptionalString() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalString = "hello";
    assertEquals("hello", msg.optionalString);
    msg.clear();
    assertTrue(msg.optionalString.isEmpty());
    msg.clear()
       .optionalString = "hello2";
    assertEquals("hello2", msg.optionalString);
    msg.clear();
    assertTrue(msg.optionalString.isEmpty());

    msg.optionalString = "bye";
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalString != null);
    assertEquals("bye", newMsg.optionalString);
  }

  public void testMiniOptionalBytes() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertFalse(msg.optionalBytes.length > 0);
    msg.optionalBytes = InternalMini.copyFromUtf8("hello");
    assertTrue(msg.optionalBytes.length > 0);
    assertEquals("hello", new String(msg.optionalBytes, "UTF-8"));
    msg.clear();
    assertFalse(msg.optionalBytes.length > 0);
    msg.clear()
       .optionalBytes = InternalMini.copyFromUtf8("hello");
    assertTrue(msg.optionalBytes.length > 0);
    msg.clear();
    assertFalse(msg.optionalBytes.length > 0);

    msg.optionalBytes = InternalMini.copyFromUtf8("bye");
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalBytes.length > 0);
    assertEquals("bye", new String(newMsg.optionalBytes, "UTF-8"));
  }

  public void testMiniOptionalGroup() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    TestAllTypesMini.OptionalGroup grp = new TestAllTypesMini.OptionalGroup();
    grp.a = 1;
    assertFalse(msg.optionalGroup != null);
    msg.optionalGroup = grp;
    assertTrue(msg.optionalGroup != null);
    assertEquals(1, msg.optionalGroup.a);
    msg.clear();
    assertFalse(msg.optionalGroup != null);
    msg.clear()
       .optionalGroup = new TestAllTypesMini.OptionalGroup();
    msg.optionalGroup.a = 2;
    assertTrue(msg.optionalGroup != null);
    msg.clear();
    assertFalse(msg.optionalGroup != null);

    msg.optionalGroup = grp;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalGroup != null);
    assertEquals(1, newMsg.optionalGroup.a);
  }

  public void testMiniOptionalNestedMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    TestAllTypesMini.NestedMessage nestedMsg = new TestAllTypesMini.NestedMessage();
    nestedMsg.bb = 1;
    assertFalse(msg.optionalNestedMessage != null);
    msg.optionalNestedMessage = nestedMsg;
    assertTrue(msg.optionalNestedMessage != null);
    assertEquals(1, msg.optionalNestedMessage.bb);
    msg.clear();
    assertFalse(msg.optionalNestedMessage != null);
    msg.clear()
       .optionalNestedMessage = new TestAllTypesMini.NestedMessage();
    msg.optionalNestedMessage.bb = 2;
    assertTrue(msg.optionalNestedMessage != null);
    msg.clear();
    assertFalse(msg.optionalNestedMessage != null);

    msg.optionalNestedMessage = nestedMsg;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalNestedMessage != null);
    assertEquals(1, newMsg.optionalNestedMessage.bb);
  }

  public void testMiniOptionalForeignMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    MiniOuterClass.ForeignMessageMini nestedMsg = new MiniOuterClass.ForeignMessageMini();
    nestedMsg.c = 1;
    assertFalse(msg.optionalForeignMessage != null);
    msg.optionalForeignMessage = nestedMsg;
    assertTrue(msg.optionalForeignMessage != null);
    assertEquals(1, msg.optionalForeignMessage.c);
    msg.clear();
    assertFalse(msg.optionalForeignMessage != null);
    msg.clear()
       .optionalForeignMessage = new MiniOuterClass.ForeignMessageMini();
    msg.optionalForeignMessage.c = 2;
    assertTrue(msg.optionalForeignMessage != null);
    msg.clear();
    assertFalse(msg.optionalForeignMessage != null);

    msg.optionalForeignMessage = nestedMsg;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalForeignMessage != null);
    assertEquals(1, newMsg.optionalForeignMessage.c);
  }

  public void testMiniOptionalImportMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    UnittestImportMini.ImportMessageMini nestedMsg = new UnittestImportMini.ImportMessageMini();
    nestedMsg.d = 1;
    assertFalse(msg.optionalImportMessage != null);
    msg.optionalImportMessage = nestedMsg;
    assertTrue(msg.optionalImportMessage != null);
    assertEquals(1, msg.optionalImportMessage.d);
    msg.clear();
    assertFalse(msg.optionalImportMessage != null);
    msg.clear()
       .optionalImportMessage = new UnittestImportMini.ImportMessageMini();
    msg.optionalImportMessage.d = 2;
    assertTrue(msg.optionalImportMessage != null);
    msg.clear();
    assertFalse(msg.optionalImportMessage != null);

    msg.optionalImportMessage = nestedMsg;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalImportMessage != null);
    assertEquals(1, newMsg.optionalImportMessage.d);
  }

  public void testMiniOptionalNestedEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalNestedEnum = TestAllTypesMini.BAR;
    assertEquals(TestAllTypesMini.BAR, msg.optionalNestedEnum);
    msg.clear()
       .optionalNestedEnum = TestAllTypesMini.BAZ;
    assertEquals(TestAllTypesMini.BAZ, msg.optionalNestedEnum);
    msg.clear();
    assertEquals(TestAllTypesMini.FOO, msg.optionalNestedEnum);

    msg.optionalNestedEnum = TestAllTypesMini.BAR;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(TestAllTypesMini.BAR, newMsg.optionalNestedEnum);
  }

  public void testMiniOptionalForeignEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalForeignEnum = MiniOuterClass.FOREIGN_MINI_BAR;
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, msg.optionalForeignEnum);
    msg.clear()
       .optionalForeignEnum = MiniOuterClass.FOREIGN_MINI_BAZ;
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAZ, msg.optionalForeignEnum);
    msg.clear();
    assertEquals(MiniOuterClass.FOREIGN_MINI_FOO, msg.optionalForeignEnum);

    msg.optionalForeignEnum = MiniOuterClass.FOREIGN_MINI_BAR;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, newMsg.optionalForeignEnum);
  }

  public void testMiniOptionalImportEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalImportEnum = UnittestImportMini.IMPORT_MINI_BAR;
    assertEquals(UnittestImportMini.IMPORT_MINI_BAR, msg.optionalImportEnum);
    msg.clear()
       .optionalImportEnum = UnittestImportMini.IMPORT_MINI_BAZ;
    assertEquals(UnittestImportMini.IMPORT_MINI_BAZ, msg.optionalImportEnum);
    msg.clear();
    assertEquals(UnittestImportMini.IMPORT_MINI_FOO, msg.optionalImportEnum);

    msg.optionalImportEnum = UnittestImportMini.IMPORT_MINI_BAR;
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(UnittestImportMini.IMPORT_MINI_BAR, newMsg.optionalImportEnum);
  }

  public void testMiniOptionalStringPiece() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalStringPiece = "hello";
    assertEquals("hello", msg.optionalStringPiece);
    msg.clear();
    assertTrue(msg.optionalStringPiece.isEmpty());
    msg.clear()
       .optionalStringPiece = "hello2";
    assertEquals("hello2", msg.optionalStringPiece);
    msg.clear();
    assertTrue(msg.optionalStringPiece.isEmpty());

    msg.optionalStringPiece = "bye";
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalStringPiece != null);
    assertEquals("bye", newMsg.optionalStringPiece);
  }

  public void testMiniOptionalCord() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalCord = "hello";
    assertEquals("hello", msg.optionalCord);
    msg.clear();
    assertTrue(msg.optionalCord.isEmpty());
    msg.clear()
       .optionalCord = "hello2";
    assertEquals("hello2", msg.optionalCord);
    msg.clear();
    assertTrue(msg.optionalCord.isEmpty());

    msg.optionalCord = "bye";
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertTrue(newMsg.optionalCord != null);
    assertEquals("bye", newMsg.optionalCord);
  }

  public void testMiniRepeatedInt32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedInt32.length);
    msg.repeatedInt32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedInt32[1]);
    assertEquals(456, msg.repeatedInt32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedInt32.length);
    msg.clear()
       .repeatedInt32 = new int[] { 456 };
    assertEquals(1, msg.repeatedInt32.length);
    assertEquals(456, msg.repeatedInt32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedInt32.length);

    // Test 1 entry
    msg.clear()
       .repeatedInt32 = new int[] { 123 };
    assertEquals(1, msg.repeatedInt32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedInt32.length);
    assertEquals(123, newMsg.repeatedInt32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedInt32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedInt32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedInt32.length);
    assertEquals(123, newMsg.repeatedInt32[0]);
    assertEquals(456, newMsg.repeatedInt32[1]);
  }

  public void testMiniRepeatedInt64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedInt64.length);
    msg.repeatedInt64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedInt64[1]);
    assertEquals(456, msg.repeatedInt64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedInt64.length);
    msg.clear()
       .repeatedInt64 = new long[] { 456 };
    assertEquals(1, msg.repeatedInt64.length);
    assertEquals(456, msg.repeatedInt64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedInt64.length);

    // Test 1 entry
    msg.clear()
       .repeatedInt64 = new long[] { 123 };
    assertEquals(1, msg.repeatedInt64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedInt64.length);
    assertEquals(123, newMsg.repeatedInt64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedInt64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedInt64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedInt64.length);
    assertEquals(123, newMsg.repeatedInt64[0]);
    assertEquals(456, newMsg.repeatedInt64[1]);
  }

  public void testMiniRepeatedUint32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedUint32.length);
    msg.repeatedUint32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedUint32[1]);
    assertEquals(456, msg.repeatedUint32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedUint32.length);
    msg.clear()
       .repeatedUint32 = new int[] { 456 };
    assertEquals(1, msg.repeatedUint32.length);
    assertEquals(456, msg.repeatedUint32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedUint32.length);

    // Test 1 entry
    msg.clear()
       .repeatedUint32 = new int[] { 123 };
    assertEquals(1, msg.repeatedUint32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedUint32.length);
    assertEquals(123, newMsg.repeatedUint32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedUint32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedUint32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedUint32.length);
    assertEquals(123, newMsg.repeatedUint32[0]);
    assertEquals(456, newMsg.repeatedUint32[1]);
  }

  public void testMiniRepeatedUint64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedUint64.length);
    msg.repeatedUint64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedUint64[1]);
    assertEquals(456, msg.repeatedUint64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedUint64.length);
    msg.clear()
       .repeatedUint64 = new long[] { 456 };
    assertEquals(1, msg.repeatedUint64.length);
    assertEquals(456, msg.repeatedUint64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedUint64.length);

    // Test 1 entry
    msg.clear()
       .repeatedUint64 = new long[] { 123 };
    assertEquals(1, msg.repeatedUint64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedUint64.length);
    assertEquals(123, newMsg.repeatedUint64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedUint64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedUint64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 10);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedUint64.length);
    assertEquals(123, newMsg.repeatedUint64[0]);
    assertEquals(456, newMsg.repeatedUint64[1]);
  }

  public void testMiniRepeatedSint32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedSint32.length);
    msg.repeatedSint32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedSint32[1]);
    assertEquals(456, msg.repeatedSint32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedSint32.length);
    msg.clear()
       .repeatedSint32 = new int[] { 456 };
    assertEquals(1, msg.repeatedSint32.length);
    assertEquals(456, msg.repeatedSint32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedSint32.length);

    // Test 1 entry
    msg.clear()
       .repeatedSint32 = new int[] { 123 };
    assertEquals(1, msg.repeatedSint32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedSint32.length);
    assertEquals(123, newMsg.repeatedSint32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedSint32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedSint32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedSint32.length);
    assertEquals(123, newMsg.repeatedSint32[0]);
    assertEquals(456, newMsg.repeatedSint32[1]);
  }

  public void testMiniRepeatedSint64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedSint64.length);
    msg.repeatedSint64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedSint64[1]);
    assertEquals(456, msg.repeatedSint64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedSint64.length);
    msg.clear()
       .repeatedSint64 = new long[] { 456 };
    assertEquals(1, msg.repeatedSint64.length);
    assertEquals(456, msg.repeatedSint64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedSint64.length);

    // Test 1 entry
    msg.clear()
       .repeatedSint64 = new long[] { 123 };
    assertEquals(1, msg.repeatedSint64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedSint64.length);
    assertEquals(123, newMsg.repeatedSint64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedSint64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedSint64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedSint64.length);
    assertEquals(123, newMsg.repeatedSint64[0]);
    assertEquals(456, newMsg.repeatedSint64[1]);
  }

  public void testMiniRepeatedFixed32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedFixed32.length);
    msg.repeatedFixed32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedFixed32[1]);
    assertEquals(456, msg.repeatedFixed32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedFixed32.length);
    msg.clear()
       .repeatedFixed32 = new int[] { 456 };
    assertEquals(1, msg.repeatedFixed32.length);
    assertEquals(456, msg.repeatedFixed32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedFixed32.length);

    // Test 1 entry
    msg.clear()
       .repeatedFixed32 = new int[] { 123 };
    assertEquals(1, msg.repeatedFixed32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedFixed32.length);
    assertEquals(123, newMsg.repeatedFixed32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedFixed32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedFixed32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 15);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedFixed32.length);
    assertEquals(123, newMsg.repeatedFixed32[0]);
    assertEquals(456, newMsg.repeatedFixed32[1]);
  }

  public void testMiniRepeatedFixed64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedFixed64.length);
    msg.repeatedFixed64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedFixed64[1]);
    assertEquals(456, msg.repeatedFixed64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedFixed64.length);
    msg.clear()
       .repeatedFixed64 = new long[] { 456 };
    assertEquals(1, msg.repeatedFixed64.length);
    assertEquals(456, msg.repeatedFixed64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedFixed64.length);

    // Test 1 entry
    msg.clear()
       .repeatedFixed64 = new long[] { 123 };
    assertEquals(1, msg.repeatedFixed64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 13);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedFixed64.length);
    assertEquals(123, newMsg.repeatedFixed64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedFixed64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedFixed64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 23);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedFixed64.length);
    assertEquals(123, newMsg.repeatedFixed64[0]);
    assertEquals(456, newMsg.repeatedFixed64[1]);
  }

  public void testMiniRepeatedSfixed32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedSfixed32.length);
    msg.repeatedSfixed32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedSfixed32[1]);
    assertEquals(456, msg.repeatedSfixed32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedSfixed32.length);
    msg.clear()
       .repeatedSfixed32 = new int[] { 456 };
    assertEquals(1, msg.repeatedSfixed32.length);
    assertEquals(456, msg.repeatedSfixed32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedSfixed32.length);

    // Test 1 entry
    msg.clear()
       .repeatedSfixed32 = new int[] { 123 };
    assertEquals(1, msg.repeatedSfixed32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedSfixed32.length);
    assertEquals(123, newMsg.repeatedSfixed32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedSfixed32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedSfixed32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 15);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedSfixed32.length);
    assertEquals(123, newMsg.repeatedSfixed32[0]);
    assertEquals(456, newMsg.repeatedSfixed32[1]);
  }

  public void testMiniRepeatedSfixed64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedSfixed64.length);
    msg.repeatedSfixed64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedSfixed64[1]);
    assertEquals(456, msg.repeatedSfixed64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedSfixed64.length);
    msg.clear()
       .repeatedSfixed64 = new long[] { 456 };
    assertEquals(1, msg.repeatedSfixed64.length);
    assertEquals(456, msg.repeatedSfixed64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedSfixed64.length);

    // Test 1 entry
    msg.clear()
       .repeatedSfixed64 = new long[] { 123 };
    assertEquals(1, msg.repeatedSfixed64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 13);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedSfixed64.length);
    assertEquals(123, newMsg.repeatedSfixed64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedSfixed64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedSfixed64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 23);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedSfixed64.length);
    assertEquals(123, newMsg.repeatedSfixed64[0]);
    assertEquals(456, newMsg.repeatedSfixed64[1]);
  }

  public void testMiniRepeatedFloat() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedFloat.length);
    msg.repeatedFloat = new float[] { 123f, 789f, 456f };
    assertEquals(789f, msg.repeatedFloat[1]);
    assertEquals(456f, msg.repeatedFloat[2]);
    msg.clear();
    assertEquals(0, msg.repeatedFloat.length);
    msg.clear()
       .repeatedFloat = new float[] { 456f };
    assertEquals(1, msg.repeatedFloat.length);
    assertEquals(456f, msg.repeatedFloat[0]);
    msg.clear();
    assertEquals(0, msg.repeatedFloat.length);

    // Test 1 entry
    msg.clear()
       .repeatedFloat = new float[] { 123f };
    assertEquals(1, msg.repeatedFloat.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedFloat.length);
    assertEquals(123f, newMsg.repeatedFloat[0]);

    // Test 2 entries
    msg.clear()
       .repeatedFloat = new float[] { 123f, 456f };
    assertEquals(2, msg.repeatedFloat.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 15);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedFloat.length);
    assertEquals(123f, newMsg.repeatedFloat[0]);
    assertEquals(456f, newMsg.repeatedFloat[1]);
  }

  public void testMiniRepeatedDouble() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedDouble.length);
    msg.repeatedDouble = new double[] { 123.0, 789.0, 456.0 };
    assertEquals(789.0, msg.repeatedDouble[1]);
    assertEquals(456.0, msg.repeatedDouble[2]);
    msg.clear();
    assertEquals(0, msg.repeatedDouble.length);
    msg.clear()
       .repeatedDouble = new double[] { 456.0 };
    assertEquals(1, msg.repeatedDouble.length);
    assertEquals(456.0, msg.repeatedDouble[0]);
    msg.clear();
    assertEquals(0, msg.repeatedDouble.length);

    // Test 1 entry
    msg.clear()
       .repeatedDouble = new double[] { 123.0 };
    assertEquals(1, msg.repeatedDouble.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 13);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedDouble.length);
    assertEquals(123.0, newMsg.repeatedDouble[0]);

    // Test 2 entries
    msg.clear()
       .repeatedDouble = new double[] { 123.0, 456.0 };
    assertEquals(2, msg.repeatedDouble.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 23);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedDouble.length);
    assertEquals(123.0, newMsg.repeatedDouble[0]);
    assertEquals(456.0, newMsg.repeatedDouble[1]);
  }

  public void testMiniRepeatedBool() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedBool.length);
    msg.repeatedBool = new boolean[] { false, true, false };
    assertTrue(msg.repeatedBool[1]);
    assertFalse(msg.repeatedBool[2]);
    msg.clear();
    assertEquals(0, msg.repeatedBool.length);
    msg.clear()
       .repeatedBool = new boolean[] { true };
    assertEquals(1, msg.repeatedBool.length);
    assertTrue(msg.repeatedBool[0]);
    msg.clear();
    assertEquals(0, msg.repeatedBool.length);

    // Test 1 entry
    msg.clear()
       .repeatedBool = new boolean[] { false };
    assertEquals(1, msg.repeatedBool.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedBool.length);
    assertFalse(newMsg.repeatedBool[0]);

    // Test 2 entries
    msg.clear()
       .repeatedBool = new boolean[] { true, false };
    assertEquals(2, msg.repeatedBool.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedBool.length);
    assertTrue(newMsg.repeatedBool[0]);
    assertFalse(newMsg.repeatedBool[1]);
  }

  public void testMiniRepeatedString() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedString.length);
    msg.repeatedString = new String[] { "hello", "bye", "boo" };
    assertEquals("bye", msg.repeatedString[1]);
    assertEquals("boo", msg.repeatedString[2]);
    msg.clear();
    assertEquals(0, msg.repeatedString.length);
    msg.clear()
       .repeatedString = new String[] { "boo" };
    assertEquals(1, msg.repeatedString.length);
    assertEquals("boo", msg.repeatedString[0]);
    msg.clear();
    assertEquals(0, msg.repeatedString.length);

    // Test 1 entry
    msg.clear()
       .repeatedString = new String[] { "" };
    assertEquals(1, msg.repeatedString.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedString.length);
    assertTrue(newMsg.repeatedString[0].isEmpty());

    // Test 2 entries
    msg.clear()
       .repeatedString = new String[] { "hello", "world" };
    assertEquals(2, msg.repeatedString.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 19);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedString.length);
    assertEquals("hello", newMsg.repeatedString[0]);
    assertEquals("world", newMsg.repeatedString[1]);
  }

  public void testMiniRepeatedBytes() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedBytes.length);
    msg.repeatedBytes = new byte[][] {
        InternalMini.copyFromUtf8("hello"),
        InternalMini.copyFromUtf8("bye"),
        InternalMini.copyFromUtf8("boo")
    };
    assertEquals("bye", new String(msg.repeatedBytes[1], "UTF-8"));
    assertEquals("boo", new String(msg.repeatedBytes[2], "UTF-8"));
    msg.clear();
    assertEquals(0, msg.repeatedBytes.length);
    msg.clear()
       .repeatedBytes = new byte[][] { InternalMini.copyFromUtf8("boo") };
    assertEquals(1, msg.repeatedBytes.length);
    assertEquals("boo", new String(msg.repeatedBytes[0], "UTF-8"));
    msg.clear();
    assertEquals(0, msg.repeatedBytes.length);

    // Test 1 entry
    msg.clear()
       .repeatedBytes = new byte[][] { InternalMini.copyFromUtf8("") };
    assertEquals(1, msg.repeatedBytes.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedBytes.length);
    assertTrue(newMsg.repeatedBytes[0].length == 0);

    // Test 2 entries
    msg.clear()
       .repeatedBytes = new byte[][] {
      InternalMini.copyFromUtf8("hello"),
      InternalMini.copyFromUtf8("world")
    };
    assertEquals(2, msg.repeatedBytes.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 19);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedBytes.length);
    assertEquals("hello", new String(newMsg.repeatedBytes[0], "UTF-8"));
    assertEquals("world", new String(newMsg.repeatedBytes[1], "UTF-8"));
  }

  public void testMiniRepeatedGroup() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    TestAllTypesMini.RepeatedGroup group0 =
      new TestAllTypesMini.RepeatedGroup();
    group0.a = 0;
    TestAllTypesMini.RepeatedGroup group1 =
      new TestAllTypesMini.RepeatedGroup();
    group1.a = 1;
    TestAllTypesMini.RepeatedGroup group2 =
      new TestAllTypesMini.RepeatedGroup();
    group2.a = 2;

    msg.repeatedGroup = new TestAllTypesMini.RepeatedGroup[] { group0, group1, group2 };
    assertEquals(3, msg.repeatedGroup.length);
    assertEquals(0, msg.repeatedGroup[0].a);
    assertEquals(1, msg.repeatedGroup[1].a);
    assertEquals(2, msg.repeatedGroup[2].a);
    msg.clear();
    assertEquals(0, msg.repeatedGroup.length);
    msg.clear()
       .repeatedGroup = new TestAllTypesMini.RepeatedGroup[] { group1 };
    assertEquals(1, msg.repeatedGroup.length);
    assertEquals(1, msg.repeatedGroup[0].a);
    msg.clear();
    assertEquals(0, msg.repeatedGroup.length);

    // Test 1 entry
    msg.clear()
       .repeatedGroup = new TestAllTypesMini.RepeatedGroup[] { group0 };
    assertEquals(1, msg.repeatedGroup.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedGroup.length);
    assertEquals(0, newMsg.repeatedGroup[0].a);

    // Test 2 entries
    msg.clear()
       .repeatedGroup = new TestAllTypesMini.RepeatedGroup[] { group0, group1 };
    assertEquals(2, msg.repeatedGroup.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 14);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedGroup.length);
    assertEquals(0, newMsg.repeatedGroup[0].a);
    assertEquals(1, newMsg.repeatedGroup[1].a);
  }

  public void testMiniRepeatedNestedMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    TestAllTypesMini.NestedMessage nestedMsg0 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg0.bb = 0;
    TestAllTypesMini.NestedMessage nestedMsg1 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg1.bb = 1;
    TestAllTypesMini.NestedMessage nestedMsg2 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg2.bb = 2;

    msg.repeatedNestedMessage =
        new TestAllTypesMini.NestedMessage[] { nestedMsg0, nestedMsg1, nestedMsg2 };
    assertEquals(3, msg.repeatedNestedMessage.length);
    assertEquals(0, msg.repeatedNestedMessage[0].bb);
    assertEquals(1, msg.repeatedNestedMessage[1].bb);
    assertEquals(2, msg.repeatedNestedMessage[2].bb);
    msg.clear();
    assertEquals(0, msg.repeatedNestedMessage.length);
    msg.clear()
       .repeatedNestedMessage = new TestAllTypesMini.NestedMessage[] { nestedMsg1 };
    assertEquals(1, msg.repeatedNestedMessage.length);
    assertEquals(1, msg.repeatedNestedMessage[0].bb);
    msg.clear();
    assertEquals(0, msg.repeatedNestedMessage.length);

    // Test 1 entry
    msg.clear()
       .repeatedNestedMessage = new TestAllTypesMini.NestedMessage[] { nestedMsg0 };
    assertEquals(1, msg.repeatedNestedMessage.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedNestedMessage.length);
    assertEquals(0, newMsg.repeatedNestedMessage[0].bb);

    // Test 2 entries
    msg.clear()
       .repeatedNestedMessage = new TestAllTypesMini.NestedMessage[] { nestedMsg0, nestedMsg1 };
    assertEquals(2, msg.repeatedNestedMessage.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedNestedMessage.length);
    assertEquals(0, newMsg.repeatedNestedMessage[0].bb);
    assertEquals(1, newMsg.repeatedNestedMessage[1].bb);
  }

  public void testMiniRepeatedForeignMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    MiniOuterClass.ForeignMessageMini foreignMsg0 =
      new MiniOuterClass.ForeignMessageMini();
    foreignMsg0.c = 0;
    MiniOuterClass.ForeignMessageMini foreignMsg1 =
      new MiniOuterClass.ForeignMessageMini();
    foreignMsg1.c = 1;
    MiniOuterClass.ForeignMessageMini foreignMsg2 =
      new MiniOuterClass.ForeignMessageMini();
    foreignMsg2.c = 2;

    msg.repeatedForeignMessage =
        new MiniOuterClass.ForeignMessageMini[] { foreignMsg0, foreignMsg1, foreignMsg2 };
    assertEquals(3, msg.repeatedForeignMessage.length);
    assertEquals(0, msg.repeatedForeignMessage[0].c);
    assertEquals(1, msg.repeatedForeignMessage[1].c);
    assertEquals(2, msg.repeatedForeignMessage[2].c);
    msg.clear();
    assertEquals(0, msg.repeatedForeignMessage.length);
    msg.clear()
       .repeatedForeignMessage = new MiniOuterClass.ForeignMessageMini[] { foreignMsg1 };
    assertEquals(1, msg.repeatedForeignMessage.length);
    assertEquals(1, msg.repeatedForeignMessage[0].c);
    msg.clear();
    assertEquals(0, msg.repeatedForeignMessage.length);

    // Test 1 entry
    msg.clear()
       .repeatedForeignMessage = new MiniOuterClass.ForeignMessageMini[] { foreignMsg0 };
    assertEquals(1, msg.repeatedForeignMessage.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedForeignMessage.length);
    assertEquals(0, newMsg.repeatedForeignMessage[0].c);

    // Test 2 entries
    msg.clear()
       .repeatedForeignMessage = new MiniOuterClass.ForeignMessageMini[] { foreignMsg0, foreignMsg1 };
    assertEquals(2, msg.repeatedForeignMessage.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedForeignMessage.length);
    assertEquals(0, newMsg.repeatedForeignMessage[0].c);
    assertEquals(1, newMsg.repeatedForeignMessage[1].c);
  }

  public void testMiniRepeatedImportMessage() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    UnittestImportMini.ImportMessageMini foreignMsg0 =
      new UnittestImportMini.ImportMessageMini();
    foreignMsg0.d = 0;
    UnittestImportMini.ImportMessageMini foreignMsg1 =
      new UnittestImportMini.ImportMessageMini();
    foreignMsg1.d = 1;
    UnittestImportMini.ImportMessageMini foreignMsg2 =
      new UnittestImportMini.ImportMessageMini();
    foreignMsg2.d = 2;

    msg.repeatedImportMessage =
        new UnittestImportMini.ImportMessageMini[] { foreignMsg0, foreignMsg1, foreignMsg2 };
    assertEquals(3, msg.repeatedImportMessage.length);
    assertEquals(0, msg.repeatedImportMessage[0].d);
    assertEquals(1, msg.repeatedImportMessage[1].d);
    assertEquals(2, msg.repeatedImportMessage[2].d);
    msg.clear();
    assertEquals(0, msg.repeatedImportMessage.length);
    msg.clear()
       .repeatedImportMessage = new UnittestImportMini.ImportMessageMini[] { foreignMsg1 };
    assertEquals(1, msg.repeatedImportMessage.length);
    assertEquals(1, msg.repeatedImportMessage[0].d);
    msg.clear();
    assertEquals(0, msg.repeatedImportMessage.length);

    // Test 1 entry
    msg.clear()
       .repeatedImportMessage = new UnittestImportMini.ImportMessageMini[] { foreignMsg0 };
    assertEquals(1, msg.repeatedImportMessage.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedImportMessage.length);
    assertEquals(0, newMsg.repeatedImportMessage[0].d);

    // Test 2 entries
    msg.clear()
       .repeatedImportMessage = new UnittestImportMini.ImportMessageMini[] { foreignMsg0, foreignMsg1 };
    assertEquals(2, msg.repeatedImportMessage.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedImportMessage.length);
    assertEquals(0, newMsg.repeatedImportMessage[0].d);
    assertEquals(1, newMsg.repeatedImportMessage[1].d);
  }

  public void testMiniRepeatedNestedEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedNestedEnum = new int[] {
        TestAllTypesMini.FOO,
        TestAllTypesMini.BAR,
        TestAllTypesMini.BAZ
    };
    assertEquals(3, msg.repeatedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedNestedEnum[0]);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedNestedEnum[1]);
    assertEquals(TestAllTypesMini.BAZ, msg.repeatedNestedEnum[2]);
    msg.clear();
    assertEquals(0, msg.repeatedNestedEnum.length);
    msg.clear()
       .repeatedNestedEnum = new int[] { TestAllTypesMini.BAR };
    assertEquals(1, msg.repeatedNestedEnum.length);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedNestedEnum[0]);
    msg.clear();
    assertEquals(0, msg.repeatedNestedEnum.length);

    // Test 1 entry
    msg.clear()
       .repeatedNestedEnum = new int[] { TestAllTypesMini.FOO };
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedNestedEnum[0]);

    // Test 2 entries
    msg.clear()
       .repeatedNestedEnum = new int[] { TestAllTypesMini.FOO, TestAllTypesMini.BAR };
    assertEquals(2, msg.repeatedNestedEnum.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedNestedEnum[0]);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedNestedEnum[1]);
  }

  public void testMiniRepeatedForeignEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedForeignEnum = new int[] {
        MiniOuterClass.FOREIGN_MINI_FOO,
        MiniOuterClass.FOREIGN_MINI_BAR,
        MiniOuterClass.FOREIGN_MINI_BAZ
    };
    assertEquals(3, msg.repeatedForeignEnum.length);
    assertEquals(MiniOuterClass.FOREIGN_MINI_FOO, msg.repeatedForeignEnum[0]);
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, msg.repeatedForeignEnum[1]);
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAZ, msg.repeatedForeignEnum[2]);
    msg.clear();
    assertEquals(0, msg.repeatedForeignEnum.length);
    msg.clear()
       .repeatedForeignEnum = new int[] { MiniOuterClass.FOREIGN_MINI_BAR };
    assertEquals(1, msg.repeatedForeignEnum.length);
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, msg.repeatedForeignEnum[0]);
    msg.clear();
    assertEquals(0, msg.repeatedForeignEnum.length);

    // Test 1 entry
    msg.clear()
       .repeatedForeignEnum = new int[] { MiniOuterClass.FOREIGN_MINI_FOO };
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedForeignEnum.length);
    assertEquals(MiniOuterClass.FOREIGN_MINI_FOO, msg.repeatedForeignEnum[0]);

    // Test 2 entries
    msg.clear()
       .repeatedForeignEnum = new int[] {
      MiniOuterClass.FOREIGN_MINI_FOO,
      MiniOuterClass.FOREIGN_MINI_BAR
    };
    assertEquals(2, msg.repeatedForeignEnum.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedForeignEnum.length);
    assertEquals(MiniOuterClass.FOREIGN_MINI_FOO, msg.repeatedForeignEnum[0]);
    assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, msg.repeatedForeignEnum[1]);
  }

  public void testMiniRepeatedImportEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedImportEnum = new int[] {
        UnittestImportMini.IMPORT_MINI_FOO,
        UnittestImportMini.IMPORT_MINI_BAR,
        UnittestImportMini.IMPORT_MINI_BAZ
    };
    assertEquals(3, msg.repeatedImportEnum.length);
    assertEquals(UnittestImportMini.IMPORT_MINI_FOO, msg.repeatedImportEnum[0]);
    assertEquals(UnittestImportMini.IMPORT_MINI_BAR, msg.repeatedImportEnum[1]);
    assertEquals(UnittestImportMini.IMPORT_MINI_BAZ, msg.repeatedImportEnum[2]);
    msg.clear();
    assertEquals(0, msg.repeatedImportEnum.length);
    msg.clear()
       .repeatedImportEnum = new int[] { UnittestImportMini.IMPORT_MINI_BAR };
    assertEquals(1, msg.repeatedImportEnum.length);
    assertEquals(UnittestImportMini.IMPORT_MINI_BAR, msg.repeatedImportEnum[0]);
    msg.clear();
    assertEquals(0, msg.repeatedImportEnum.length);

    // Test 1 entry
    msg.clear()
       .repeatedImportEnum = new int[] { UnittestImportMini.IMPORT_MINI_FOO };
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedImportEnum.length);
    assertEquals(UnittestImportMini.IMPORT_MINI_FOO, msg.repeatedImportEnum[0]);

    // Test 2 entries
    msg.clear()
       .repeatedImportEnum = new int[] {
      UnittestImportMini.IMPORT_MINI_FOO,
      UnittestImportMini.IMPORT_MINI_BAR
    };
    assertEquals(2, msg.repeatedImportEnum.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedImportEnum.length);
    assertEquals(UnittestImportMini.IMPORT_MINI_FOO, msg.repeatedImportEnum[0]);
    assertEquals(UnittestImportMini.IMPORT_MINI_BAR, msg.repeatedImportEnum[1]);
  }

  public void testMiniRepeatedStringPiece() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedStringPiece.length);
    msg.repeatedStringPiece = new String[] { "hello", "bye", "boo" };
    assertEquals("bye", msg.repeatedStringPiece[1]);
    assertEquals("boo", msg.repeatedStringPiece[2]);
    msg.clear();
    assertEquals(0, msg.repeatedStringPiece.length);
    msg.clear()
       .repeatedStringPiece = new String[] { "boo" };
    assertEquals(1, msg.repeatedStringPiece.length);
    assertEquals("boo", msg.repeatedStringPiece[0]);
    msg.clear();
    assertEquals(0, msg.repeatedStringPiece.length);

    // Test 1 entry
    msg.clear()
       .repeatedStringPiece = new String[] { "" };
    assertEquals(1, msg.repeatedStringPiece.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedStringPiece.length);
    assertTrue(newMsg.repeatedStringPiece[0].isEmpty());

    // Test 2 entries
    msg.clear()
       .repeatedStringPiece = new String[] { "hello", "world" };
    assertEquals(2, msg.repeatedStringPiece.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 19);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedStringPiece.length);
    assertEquals("hello", newMsg.repeatedStringPiece[0]);
    assertEquals("world", newMsg.repeatedStringPiece[1]);
  }

  public void testMiniRepeatedCord() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedCord.length);
    msg.repeatedCord = new String[] { "hello", "bye", "boo" };
    assertEquals("bye", msg.repeatedCord[1]);
    assertEquals("boo", msg.repeatedCord[2]);
    msg.clear();
    assertEquals(0, msg.repeatedCord.length);
    msg.clear()
       .repeatedCord = new String[] { "boo" };
    assertEquals(1, msg.repeatedCord.length);
    assertEquals("boo", msg.repeatedCord[0]);
    msg.clear();
    assertEquals(0, msg.repeatedCord.length);

    // Test 1 entry
    msg.clear()
       .repeatedCord = new String[] { "" };
    assertEquals(1, msg.repeatedCord.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 6);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedCord.length);
    assertTrue(newMsg.repeatedCord[0].isEmpty());

    // Test 2 entries
    msg.clear()
       .repeatedCord = new String[] { "hello", "world" };
    assertEquals(2, msg.repeatedCord.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 19);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedCord.length);
    assertEquals("hello", newMsg.repeatedCord[0]);
    assertEquals("world", newMsg.repeatedCord[1]);
  }

  public void testMiniRepeatedPackedInt32() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedPackedInt32.length);
    msg.repeatedPackedInt32 = new int[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedPackedInt32[1]);
    assertEquals(456, msg.repeatedPackedInt32[2]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedInt32.length);
    msg.clear()
       .repeatedPackedInt32 = new int[] { 456 };
    assertEquals(1, msg.repeatedPackedInt32.length);
    assertEquals(456, msg.repeatedPackedInt32[0]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedInt32.length);

    // Test 1 entry
    msg.clear()
       .repeatedPackedInt32 = new int[] { 123 };
    assertEquals(1, msg.repeatedPackedInt32.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedPackedInt32.length);
    assertEquals(123, newMsg.repeatedPackedInt32[0]);

    // Test 2 entries
    msg.clear()
       .repeatedPackedInt32 = new int[] { 123, 456 };
    assertEquals(2, msg.repeatedPackedInt32.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 9);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedPackedInt32.length);
    assertEquals(123, newMsg.repeatedPackedInt32[0]);
    assertEquals(456, newMsg.repeatedPackedInt32[1]);
  }

  public void testMiniRepeatedPackedSfixed64() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    assertEquals(0, msg.repeatedPackedSfixed64.length);
    msg.repeatedPackedSfixed64 = new long[] { 123, 789, 456 };
    assertEquals(789, msg.repeatedPackedSfixed64[1]);
    assertEquals(456, msg.repeatedPackedSfixed64[2]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedSfixed64.length);
    msg.clear()
       .repeatedPackedSfixed64 = new long[] { 456 };
    assertEquals(1, msg.repeatedPackedSfixed64.length);
    assertEquals(456, msg.repeatedPackedSfixed64[0]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedSfixed64.length);

    // Test 1 entry
    msg.clear()
       .repeatedPackedSfixed64 = new long[] { 123 };
    assertEquals(1, msg.repeatedPackedSfixed64.length);
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 14);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedPackedSfixed64.length);
    assertEquals(123, newMsg.repeatedPackedSfixed64[0]);

    // Test 2 entries
    msg.clear()
       .repeatedPackedSfixed64 = new long[] { 123, 456 };
    assertEquals(2, msg.repeatedPackedSfixed64.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 22);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedPackedSfixed64.length);
    assertEquals(123, newMsg.repeatedPackedSfixed64[0]);
    assertEquals(456, newMsg.repeatedPackedSfixed64[1]);
  }

  public void testMiniRepeatedPackedNestedEnum() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedPackedNestedEnum = new int[] {
        TestAllTypesMini.FOO,
        TestAllTypesMini.BAR,
        TestAllTypesMini.BAZ
    };
    assertEquals(3, msg.repeatedPackedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedPackedNestedEnum[0]);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedPackedNestedEnum[1]);
    assertEquals(TestAllTypesMini.BAZ, msg.repeatedPackedNestedEnum[2]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedNestedEnum.length);
    msg.clear()
       .repeatedPackedNestedEnum = new int[] { TestAllTypesMini.BAR };
    assertEquals(1, msg.repeatedPackedNestedEnum.length);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedPackedNestedEnum[0]);
    msg.clear();
    assertEquals(0, msg.repeatedPackedNestedEnum.length);

    // Test 1 entry
    msg.clear()
       .repeatedPackedNestedEnum = new int[] { TestAllTypesMini.FOO };
    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 7);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(1, newMsg.repeatedPackedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedPackedNestedEnum[0]);

    // Test 2 entries
    msg.clear()
       .repeatedPackedNestedEnum = new int[] { TestAllTypesMini.FOO, TestAllTypesMini.BAR };
    assertEquals(2, msg.repeatedPackedNestedEnum.length);
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 8);
    assertEquals(result.length, msgSerializedSize);

    newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(2, newMsg.repeatedPackedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, msg.repeatedPackedNestedEnum[0]);
    assertEquals(TestAllTypesMini.BAR, msg.repeatedPackedNestedEnum[1]);
  }

  public void testMiniRepeatedPackedSerializedSize() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedPackedInt32 = new int[] { 123, 789, 456 };
    int msgSerializedSize = msg.getSerializedSize();
    byte [] result = MessageMini.toByteArray(msg);
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 11);
    assertEquals(result.length, msgSerializedSize);
    TestAllTypesMini msg2 = new TestAllTypesMini();
    msg2.repeatedPackedInt32 = new int[] { 123, 789, 456 };
    byte [] result2 = new byte[msgSerializedSize];
    MessageMini.toByteArray(msg2, result2, 0, msgSerializedSize);

    // Check equal size and content.
    assertEquals(msgSerializedSize, msg2.getSerializedSize());
    assertTrue(Arrays.equals(result, result2));
  }

  public void testMiniRepeatedInt32ReMerge() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedInt32 = new int[] { 234 };
    byte [] result1 = MessageMini.toByteArray(msg);

    msg.clear().optionalInt32 = 789;
    byte [] result2 = MessageMini.toByteArray(msg);

    msg.clear().repeatedInt32 = new int[] { 123, 456 };
    byte [] result3 = MessageMini.toByteArray(msg);

    // Concatenate the three serializations and read as one message.
    byte [] result = new byte[result1.length + result2.length + result3.length];
    System.arraycopy(result1, 0, result, 0, result1.length);
    System.arraycopy(result2, 0, result, result1.length, result2.length);
    System.arraycopy(result3, 0, result, result1.length + result2.length, result3.length);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(789, newMsg.optionalInt32);
    assertEquals(3, newMsg.repeatedInt32.length);
    assertEquals(234, newMsg.repeatedInt32[0]);
    assertEquals(123, newMsg.repeatedInt32[1]);
    assertEquals(456, newMsg.repeatedInt32[2]);
  }

  public void testMiniRepeatedNestedEnumReMerge() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.repeatedNestedEnum = new int[] { TestAllTypesMini.FOO };
    byte [] result1 = MessageMini.toByteArray(msg);

    msg.clear().optionalInt32 = 789;
    byte [] result2 = MessageMini.toByteArray(msg);

    msg.clear().repeatedNestedEnum = new int[] { TestAllTypesMini.BAR, TestAllTypesMini.FOO };
    byte [] result3 = MessageMini.toByteArray(msg);

    // Concatenate the three serializations and read as one message.
    byte [] result = new byte[result1.length + result2.length + result3.length];
    System.arraycopy(result1, 0, result, 0, result1.length);
    System.arraycopy(result2, 0, result, result1.length, result2.length);
    System.arraycopy(result3, 0, result, result1.length + result2.length, result3.length);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(789, newMsg.optionalInt32);
    assertEquals(3, newMsg.repeatedNestedEnum.length);
    assertEquals(TestAllTypesMini.FOO, newMsg.repeatedNestedEnum[0]);
    assertEquals(TestAllTypesMini.BAR, newMsg.repeatedNestedEnum[1]);
    assertEquals(TestAllTypesMini.FOO, newMsg.repeatedNestedEnum[2]);
  }

  public void testMiniRepeatedNestedMessageReMerge() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    TestAllTypesMini.NestedMessage nestedMsg0 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg0.bb = 0;
    TestAllTypesMini.NestedMessage nestedMsg1 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg1.bb = 1;
    TestAllTypesMini.NestedMessage nestedMsg2 =
      new TestAllTypesMini.NestedMessage();
    nestedMsg2.bb = 2;

    msg.repeatedNestedMessage = new TestAllTypesMini.NestedMessage[] { nestedMsg0 };
    byte [] result1 = MessageMini.toByteArray(msg);

    msg.clear().optionalInt32 = 789;
    byte [] result2 = MessageMini.toByteArray(msg);

    msg.clear().repeatedNestedMessage =
        new TestAllTypesMini.NestedMessage[] { nestedMsg1, nestedMsg2 };
    byte [] result3 = MessageMini.toByteArray(msg);

    // Concatenate the three serializations and read as one message.
    byte [] result = new byte[result1.length + result2.length + result3.length];
    System.arraycopy(result1, 0, result, 0, result1.length);
    System.arraycopy(result2, 0, result, result1.length, result2.length);
    System.arraycopy(result3, 0, result, result1.length + result2.length, result3.length);

    TestAllTypesMini newMsg = TestAllTypesMini.parseFrom(result);
    assertEquals(789, newMsg.optionalInt32);
    assertEquals(3, newMsg.repeatedNestedMessage.length);
    assertEquals(nestedMsg0.bb, newMsg.repeatedNestedMessage[0].bb);
    assertEquals(nestedMsg1.bb, newMsg.repeatedNestedMessage[1].bb);
    assertEquals(nestedMsg2.bb, newMsg.repeatedNestedMessage[2].bb);
  }

  /**
   * Tests that code generation correctly wraps a single message into its outer
   * class. The class {@code SingleMessageMini} is imported from the outer
   * class {@code UnittestSingleMini}, whose name is implicit. Any error would
   * cause this method to fail compilation.
   */
  public void testMiniSingle() throws Exception {
    SingleMessageMini msg = new SingleMessageMini();
  }

  /**
   * Tests that code generation correctly skips generating the outer class if
   * unnecessary, letting a file-scope entity have the same name. The class
   * {@code MultipleNameClashMini} shares the same name with the file's outer
   * class defined explicitly, but the file contains no other entities and has
   * java_multiple_files set. Any error would cause this method to fail
   * compilation.
   */
  public void testMiniMultipleNameClash() throws Exception {
    MultipleNameClashMini msg = new MultipleNameClashMini();
    msg.field = 0;
  }

  /**
   * Tests that code generation correctly handles enums in different scopes in
   * a source file with the option java_multiple_files set to true. Any error
   * would cause this method to fail compilation.
   */
  public void testMiniMultipleEnumScoping() throws Exception {
    FileScopeEnumRefMini msg1 = new FileScopeEnumRefMini();
    msg1.enumField = UnittestMultipleMini.ONE;
    MessageScopeEnumRefMini msg2 = new MessageScopeEnumRefMini();
    msg2.enumField = MessageScopeEnumRefMini.TWO;
  }

  /**
   * Tests that code generation with mixed values of the java_multiple_files
   * options between the main source file and the imported source files would
   * generate correct references. Any error would cause this method to fail
   * compilation.
   */
  public void testMiniMultipleImportingNonMultiple() throws Exception {
    UnittestImportMini.ImportMessageMini importMsg = new UnittestImportMini.ImportMessageMini();
    MultipleImportingNonMultipleMini1 mini1 = new MultipleImportingNonMultipleMini1();
    mini1.field = importMsg;
    MultipleImportingNonMultipleMini2 mini2 = new MultipleImportingNonMultipleMini2();
    mini2.mini1 = mini1;
  }

  public void testMiniDefaults() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    for (int i = 0; i < 2; i++) {
      assertEquals(41, msg.defaultInt32);
      assertEquals(42, msg.defaultInt64);
      assertEquals(43, msg.defaultUint32);
      assertEquals(44, msg.defaultUint64);
      assertEquals(-45, msg.defaultSint32);
      assertEquals(46, msg.defaultSint64);
      assertEquals(47, msg.defaultFixed32);
      assertEquals(48, msg.defaultFixed64);
      assertEquals(49, msg.defaultSfixed32);
      assertEquals(-50, msg.defaultSfixed64);
      assertTrue(51.5f == msg.defaultFloat);
      assertTrue(52.0e3 == msg.defaultDouble);
      assertEquals(true, msg.defaultBool);
      assertEquals("hello", msg.defaultString);
      assertEquals("world", new String(msg.defaultBytes, "UTF-8"));
      assertEquals("dünya", msg.defaultStringNonascii);
      assertEquals("dünyab", new String(msg.defaultBytesNonascii, "UTF-8"));
      assertEquals(TestAllTypesMini.BAR, msg.defaultNestedEnum);
      assertEquals(MiniOuterClass.FOREIGN_MINI_BAR, msg.defaultForeignEnum);
      assertEquals(UnittestImportMini.IMPORT_MINI_BAR, msg.defaultImportEnum);
      assertEquals(Float.POSITIVE_INFINITY, msg.defaultFloatInf);
      assertEquals(Float.NEGATIVE_INFINITY, msg.defaultFloatNegInf);
      assertEquals(Float.NaN, msg.defaultFloatNan);
      assertEquals(Double.POSITIVE_INFINITY, msg.defaultDoubleInf);
      assertEquals(Double.NEGATIVE_INFINITY, msg.defaultDoubleNegInf);
      assertEquals(Double.NaN, msg.defaultDoubleNan);

      // Default values are not output, except for required fields.
      byte [] result = MessageMini.toByteArray(msg);
      int msgSerializedSize = msg.getSerializedSize();
      //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
      assertTrue(msgSerializedSize == 3);
      assertEquals(result.length, msgSerializedSize);
      msg.clear();
    }
  }

  public void testMiniWithHasParseFrom() throws Exception {
    TestAllTypesMiniHas msg = null;
    // Test false on creation, after clear and upon empty parse.
    for (int i = 0; i < 3; i++) {
      if (i == 0) {
        msg = new TestAllTypesMiniHas();
      } else if (i == 1) {
        msg.clear();
      } else if (i == 2) {
        msg = TestAllTypesMiniHas.parseFrom(new byte[0]);
      }
      assertFalse(msg.hasOptionalInt32);
      assertFalse(msg.hasOptionalString);
      assertFalse(msg.hasOptionalBytes);
      assertFalse(msg.hasOptionalNestedEnum);
      assertFalse(msg.hasDefaultInt32);
      assertFalse(msg.hasDefaultString);
      assertFalse(msg.hasDefaultBytes);
      assertFalse(msg.hasDefaultFloatNan);
      assertFalse(msg.hasDefaultNestedEnum);
      assertFalse(msg.hasId);
      msg.optionalInt32 = 123;
      msg.optionalNestedMessage = new TestAllTypesMiniHas.NestedMessage();
      msg.optionalNestedMessage.bb = 2;
      msg.optionalNestedEnum = TestAllTypesMini.BAZ;
    }

    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    //System.out.printf("mss=%d result.length=%d\n", msgSerializedSize, result.length);
    assertTrue(msgSerializedSize == 13);
    assertEquals(result.length, msgSerializedSize);

    // Has fields true upon parse.
    TestAllTypesMiniHas newMsg = TestAllTypesMiniHas.parseFrom(result);
    assertEquals(123, newMsg.optionalInt32);
    assertTrue(newMsg.hasOptionalInt32);
    assertEquals(2, newMsg.optionalNestedMessage.bb);
    assertTrue(newMsg.optionalNestedMessage.hasBb);
    assertEquals(TestAllTypesMiniHas.BAZ, newMsg.optionalNestedEnum);
    assertTrue(newMsg.hasOptionalNestedEnum);
  }

  public void testMiniWithHasSerialize() throws Exception {
    TestAllTypesMiniHas msg = new TestAllTypesMiniHas();
    msg.hasOptionalInt32 = true;
    msg.hasOptionalString = true;
    msg.hasOptionalBytes = true;
    msg.optionalNestedMessage = new TestAllTypesMiniHas.NestedMessage();
    msg.optionalNestedMessage.hasBb = true;
    msg.hasOptionalNestedEnum = true;
    msg.hasDefaultInt32 = true;
    msg.hasDefaultString = true;
    msg.hasDefaultBytes = true;
    msg.hasDefaultFloatNan = true;
    msg.hasDefaultNestedEnum = true;

    byte [] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    assertEquals(result.length, msgSerializedSize);

    // Now deserialize and find that all fields are set and equal to their defaults.
    TestAllTypesMiniHas newMsg = TestAllTypesMiniHas.parseFrom(result);
    assertTrue(newMsg.hasOptionalInt32);
    assertTrue(newMsg.hasOptionalString);
    assertTrue(newMsg.hasOptionalBytes);
    assertTrue(newMsg.optionalNestedMessage.hasBb);
    assertTrue(newMsg.hasOptionalNestedEnum);
    assertTrue(newMsg.hasDefaultInt32);
    assertTrue(newMsg.hasDefaultString);
    assertTrue(newMsg.hasDefaultBytes);
    assertTrue(newMsg.hasDefaultFloatNan);
    assertTrue(newMsg.hasDefaultNestedEnum);
    assertTrue(newMsg.hasId);
    assertEquals(0, newMsg.optionalInt32);
    assertEquals(0, newMsg.optionalString.length());
    assertEquals(0, newMsg.optionalBytes.length);
    assertEquals(0, newMsg.optionalNestedMessage.bb);
    assertEquals(TestAllTypesMiniHas.FOO, newMsg.optionalNestedEnum);
    assertEquals(41, newMsg.defaultInt32);
    assertEquals("hello", newMsg.defaultString);
    assertEquals("world", new String(newMsg.defaultBytes, "UTF-8"));
    assertEquals(TestAllTypesMiniHas.BAR, newMsg.defaultNestedEnum);
    assertEquals(Float.NaN, newMsg.defaultFloatNan);
    assertEquals(0, newMsg.id);
  }

  /**
   * Tests that fields with a default value of NaN are not serialized when
   * set to NaN. This is a special case as NaN != NaN, so normal equality
   * checks don't work.
   */
  public void testMiniNotANumberDefaults() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.defaultDoubleNan = 0;
    msg.defaultFloatNan = 0;
    byte[] result = MessageMini.toByteArray(msg);
    int msgSerializedSize = msg.getSerializedSize();
    assertTrue(msgSerializedSize > 3);

    msg.defaultDoubleNan = Double.NaN;
    msg.defaultFloatNan = Float.NaN;
    result = MessageMini.toByteArray(msg);
    msgSerializedSize = msg.getSerializedSize();
    assertEquals(3, msgSerializedSize);
  }

  /**
   * Test that a bug in skipRawBytes() has been fixed:  if the skip skips
   * exactly up to a limit, this should not break things.
   */
  public void testSkipRawBytesBug() throws Exception {
    byte[] rawBytes = new byte[] { 1, 2 };
    CodedInputByteBufferMini input = CodedInputByteBufferMini.newInstance(rawBytes);

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
    CodedInputByteBufferMini input = CodedInputByteBufferMini.newInstance(rawBytes);

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

  // Test a smattering of various proto types for printing
  public void testMessageMiniPrinter() {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.optionalInt32 = 14;
    msg.optionalFloat = 42.3f;
    msg.optionalString = "String \"with' both quotes";
    msg.optionalBytes = new byte[5];
    msg.optionalGroup = new TestAllTypesMini.OptionalGroup();
    msg.optionalGroup.a = 15;
    msg.repeatedInt64 = new long[2];
    msg.repeatedInt64[0] = 1L;
    msg.repeatedInt64[1] = -1L;
    msg.repeatedBytes = new byte[2][];
    msg.repeatedBytes[1] = new byte[5];
    msg.repeatedGroup = new TestAllTypesMini.RepeatedGroup[2];
    msg.repeatedGroup[0] = new TestAllTypesMini.RepeatedGroup();
    msg.repeatedGroup[0].a = -27;
    msg.repeatedGroup[1] = new TestAllTypesMini.RepeatedGroup();
    msg.repeatedGroup[1].a = -72;
    msg.optionalNestedMessage = new TestAllTypesMini.NestedMessage();
    msg.optionalNestedMessage.bb = 7;
    msg.repeatedNestedMessage = new TestAllTypesMini.NestedMessage[2];
    msg.repeatedNestedMessage[0] = new TestAllTypesMini.NestedMessage();
    msg.repeatedNestedMessage[0].bb = 77;
    msg.repeatedNestedMessage[1] = new TestAllTypesMini.NestedMessage();
    msg.repeatedNestedMessage[1].bb = 88;
    msg.optionalNestedEnum = TestAllTypesMini.BAZ;
    msg.repeatedNestedEnum = new int[2];
    msg.repeatedNestedEnum[0] = TestAllTypesMini.BAR;
    msg.repeatedNestedEnum[1] = TestAllTypesMini.FOO;

    String protoPrint = msg.toString();
    assertTrue(protoPrint.contains("TestAllTypesMini <"));
    assertTrue(protoPrint.contains("  optional_int32: 14"));
    assertTrue(protoPrint.contains("  optional_float: 42.3"));
    assertTrue(protoPrint.contains("  optional_double: 0.0"));
    assertTrue(protoPrint.contains("  optional_string: \"String \\u0022with\\u0027 both quotes\""));
    assertTrue(protoPrint.contains("  optional_bytes: [B@"));
    assertTrue(protoPrint.contains("  optionalGroup <\n    a: 15\n  >"));

    assertTrue(protoPrint.contains("  repeated_int64: 1"));
    assertTrue(protoPrint.contains("  repeated_int64: -1"));
    assertTrue(protoPrint.contains("  repeated_bytes: null\n  repeated_bytes: [B@"));
    assertTrue(protoPrint.contains("  repeatedGroup <\n    a: -27\n  >\n"
            + "  repeatedGroup <\n    a: -72\n  >"));
    assertTrue(protoPrint.contains("  optionalNestedMessage <\n    bb: 7\n  >"));
    assertTrue(protoPrint.contains("  repeatedNestedMessage <\n    bb: 77\n  >\n"
            + "  repeatedNestedMessage <\n    bb: 88\n  >"));
    assertTrue(protoPrint.contains("  optional_nested_enum: 3"));
    assertTrue(protoPrint.contains("  repeated_nested_enum: 2\n  repeated_nested_enum: 1"));
    assertTrue(protoPrint.contains("  default_int32: 41"));
    assertTrue(protoPrint.contains("  default_string: \"hello\""));
  }

  public void testExtensions() throws Exception {
    Extensions.ExtendableMessage message = new Extensions.ExtendableMessage();
    message.field = 5;
    message.setExtension(Extensions.someString, "Hello World!");
    message.setExtension(Extensions.someBool, true);
    message.setExtension(Extensions.someInt, 42);
    message.setExtension(Extensions.someLong, 124234234234L);
    message.setExtension(Extensions.someFloat, 42.0f);
    message.setExtension(Extensions.someDouble, 422222.0);
    message.setExtension(Extensions.someEnum, Extensions.FIRST_VALUE);
    AnotherMessage another = new AnotherMessage();
    another.string = "Foo";
    another.value = true;
    message.setExtension(Extensions.someMessage, another);

    message.setExtension(Extensions.someRepeatedString, list("a", "bee", "seeya"));
    message.setExtension(Extensions.someRepeatedBool, list(true, false, true));
    message.setExtension(Extensions.someRepeatedInt, list(4, 8, 15, 16, 23, 42));
    message.setExtension(Extensions.someRepeatedLong, list(4L, 8L, 15L, 16L, 23L, 42L));
    message.setExtension(Extensions.someRepeatedFloat, list(1.0f, 3.0f));
    message.setExtension(Extensions.someRepeatedDouble, list(55.133, 3.14159));
    message.setExtension(Extensions.someRepeatedEnum, list(Extensions.FIRST_VALUE,
        Extensions.SECOND_VALUE));
    AnotherMessage second = new AnotherMessage();
    second.string = "Whee";
    second.value = false;
    message.setExtension(Extensions.someRepeatedMessage, list(another, second));

    byte[] data = MessageMini.toByteArray(message);

    Extensions.ExtendableMessage deserialized = Extensions.ExtendableMessage.parseFrom(data);
    assertEquals(5, deserialized.field);
    assertEquals("Hello World!", deserialized.getExtension(Extensions.someString));
    assertEquals(Boolean.TRUE, deserialized.getExtension(Extensions.someBool));
    assertEquals(Integer.valueOf(42), deserialized.getExtension(Extensions.someInt));
    assertEquals(Long.valueOf(124234234234L), deserialized.getExtension(Extensions.someLong));
    assertEquals(Float.valueOf(42.0f), deserialized.getExtension(Extensions.someFloat));
    assertEquals(Double.valueOf(422222.0), deserialized.getExtension(Extensions.someDouble));
    assertEquals(Integer.valueOf(Extensions.FIRST_VALUE),
        deserialized.getExtension(Extensions.someEnum));
    assertEquals(another.string, deserialized.getExtension(Extensions.someMessage).string);
    assertEquals(another.value, deserialized.getExtension(Extensions.someMessage).value);
    assertEquals(list("a", "bee", "seeya"), deserialized.getExtension(Extensions.someRepeatedString));
    assertEquals(list(true, false, true), deserialized.getExtension(Extensions.someRepeatedBool));
    assertEquals(list(4, 8, 15, 16, 23, 42), deserialized.getExtension(Extensions.someRepeatedInt));
    assertEquals(list(4L, 8L, 15L, 16L, 23L, 42L), deserialized.getExtension(Extensions.someRepeatedLong));
    assertEquals(list(1.0f, 3.0f), deserialized.getExtension(Extensions.someRepeatedFloat));
    assertEquals(list(55.133, 3.14159), deserialized.getExtension(Extensions.someRepeatedDouble));
    assertEquals(list(Extensions.FIRST_VALUE,
        Extensions.SECOND_VALUE), deserialized.getExtension(Extensions.someRepeatedEnum));
    assertEquals("Foo", deserialized.getExtension(Extensions.someRepeatedMessage).get(0).string);
    assertEquals(true, deserialized.getExtension(Extensions.someRepeatedMessage).get(0).value);
    assertEquals("Whee", deserialized.getExtension(Extensions.someRepeatedMessage).get(1).string);
    assertEquals(false, deserialized.getExtension(Extensions.someRepeatedMessage).get(1).value);
  }

  public void testUnknownFields() throws Exception {
    // Check that we roundtrip (serialize and deserialize) unrecognized fields.
    AnotherMessage message = new AnotherMessage();
    message.string = "Hello World";
    message.value = false;

    byte[] bytes = MessageMini.toByteArray(message);
    int extraFieldSize = CodedOutputStream.computeStringSize(1001, "This is an unknown field");
    byte[] newBytes = new byte[bytes.length + extraFieldSize];
    System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
    CodedOutputStream.newInstance(newBytes, bytes.length, extraFieldSize).writeString(1001,
        "This is an unknown field");

    // Deserialize with an unknown field.
    AnotherMessage deserialized = AnotherMessage.parseFrom(newBytes);
    byte[] serialized = MessageMini.toByteArray(deserialized);

    assertEquals(newBytes.length, serialized.length);

    // Clear, and make sure it clears everything.
    deserialized.clear();
    assertEquals(0, MessageMini.toByteArray(deserialized).length);
  }

  public void testMergeFrom() throws Exception {
    SimpleMessageMini message = new SimpleMessageMini();
    message.d = 123;
    byte[] bytes = MessageMini.toByteArray(message);

    SimpleMessageMini newMessage = MessageMini.mergeFrom(new SimpleMessageMini(), bytes);
    assertEquals(message.d, newMessage.d);
  }

  public void testJavaKeyword() throws Exception {
    TestAllTypesMini msg = new TestAllTypesMini();
    msg.synchronized_ = 123;
    assertEquals(123, msg.synchronized_);
  }

  private <T> List<T> list(T first, T... remaining) {
    List<T> list = new ArrayList<T>();
    list.add(first);
    for (T item : remaining) {
      list.add(item);
    }
    return list;
  }
}
