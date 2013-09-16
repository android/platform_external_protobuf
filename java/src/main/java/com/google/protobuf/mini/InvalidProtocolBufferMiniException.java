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

package com.google.protobuf.mini;

import java.io.IOException;

/**
 * Thrown when a protocol message being parsed is invalid in some way,
 * e.g. it contains a malformed varint or a negative byte length.
 *
 * @author kenton@google.com Kenton Varda
 */
public class InvalidProtocolBufferMiniException extends IOException {
  private static final long serialVersionUID = -1616151763072450476L;

  public InvalidProtocolBufferMiniException(final String description) {
    super(description);
  }

  static InvalidProtocolBufferMiniException truncatedMessage() {
    return new InvalidProtocolBufferMiniException(
      "While parsing a protocol message, the input ended unexpectedly " +
      "in the middle of a field.  This could mean either than the " +
      "input has been truncated or that an embedded message " +
      "misreported its own length.");
  }

  static InvalidProtocolBufferMiniException negativeSize() {
    return new InvalidProtocolBufferMiniException(
      "CodedInputStream encountered an embedded string or message " +
      "which claimed to have negative size.");
  }

  static InvalidProtocolBufferMiniException malformedVarint() {
    return new InvalidProtocolBufferMiniException(
      "CodedInputStream encountered a malformed varint.");
  }

  static InvalidProtocolBufferMiniException invalidTag() {
    return new InvalidProtocolBufferMiniException(
      "Protocol message contained an invalid tag (zero).");
  }

  static InvalidProtocolBufferMiniException invalidEndTag() {
    return new InvalidProtocolBufferMiniException(
      "Protocol message end-group tag did not match expected tag.");
  }

  static InvalidProtocolBufferMiniException invalidWireType() {
    return new InvalidProtocolBufferMiniException(
      "Protocol message tag had invalid wire type.");
  }

  static InvalidProtocolBufferMiniException recursionLimitExceeded() {
    return new InvalidProtocolBufferMiniException(
      "Protocol message had too many levels of nesting.  May be malicious.  " +
      "Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
  }

  static InvalidProtocolBufferMiniException sizeLimitExceeded() {
    return new InvalidProtocolBufferMiniException(
      "Protocol message was too large.  May be malicious.  " +
      "Use CodedInputStream.setSizeLimit() to increase the size limit.");
  }
}
