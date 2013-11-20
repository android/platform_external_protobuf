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

// Author: bduff@google.com (Brian Duff)

#include <google/protobuf/compiler/javanano/javanano_extension.h>
#include <google/protobuf/compiler/javanano/javanano_helpers.h>
#include <google/protobuf/io/printer.h>
#include <google/protobuf/stubs/strutil.h>
#include <google/protobuf/wire_format.h>

namespace google {
namespace protobuf {
namespace compiler {
namespace javanano {

using internal::WireFormat;
using internal::WireFormatLite;

namespace {

const char* GetTypeConstantName(const FieldDescriptor::Type type) {
  switch (type) {
    case FieldDescriptor::TYPE_INT32   : return "TYPE_INT32"   ;
    case FieldDescriptor::TYPE_UINT32  : return "TYPE_UINT32"  ;
    case FieldDescriptor::TYPE_SINT32  : return "TYPE_SINT32"  ;
    case FieldDescriptor::TYPE_FIXED32 : return "TYPE_FIXED32" ;
    case FieldDescriptor::TYPE_SFIXED32: return "TYPE_SFIXED32";
    case FieldDescriptor::TYPE_INT64   : return "TYPE_INT64"   ;
    case FieldDescriptor::TYPE_UINT64  : return "TYPE_UINT64"  ;
    case FieldDescriptor::TYPE_SINT64  : return "TYPE_SINT64"  ;
    case FieldDescriptor::TYPE_FIXED64 : return "TYPE_FIXED64" ;
    case FieldDescriptor::TYPE_SFIXED64: return "TYPE_SFIXED64";
    case FieldDescriptor::TYPE_FLOAT   : return "TYPE_FLOAT"   ;
    case FieldDescriptor::TYPE_DOUBLE  : return "TYPE_DOUBLE"  ;
    case FieldDescriptor::TYPE_BOOL    : return "TYPE_BOOL"    ;
    case FieldDescriptor::TYPE_STRING  : return "TYPE_STRING"  ;
    case FieldDescriptor::TYPE_BYTES   : return "TYPE_BYTES"   ;
    case FieldDescriptor::TYPE_ENUM    : return "TYPE_ENUM"    ;
    case FieldDescriptor::TYPE_GROUP   : return "TYPE_GROUP"   ;
    case FieldDescriptor::TYPE_MESSAGE : return "TYPE_MESSAGE" ;

    // No default because we want the compiler to complain if any new
    // types are added.
  }

  GOOGLE_LOG(FATAL) << "Can't get here.";
  return NULL;
}

}  // namespace

void SetVariables(const FieldDescriptor* descriptor, const Params params,
                  map<string, string>* variables) {
  (*variables)["name"] = RenameJavaKeywords(UnderscoresToCamelCase(descriptor));
  (*variables)["type"] = GetTypeConstantName(descriptor->type());
  (*variables)["tag"] = SimpleItoa(WireFormat::MakeTag(descriptor));
  if (!descriptor->is_repeated()) {
    // Not repeated; no need for non_packed_tag and packed_tag variables
  } else if (!descriptor->is_packable()) {
    // Not packable
    (*variables)["non_packed_tag"] = (*variables)["tag"];
    (*variables)["packed_tag"] = "0";
  } else if (descriptor->options().packed()){
    // Packable and packed
    (*variables)["non_packed_tag"] = SimpleItoa(WireFormatLite::MakeTag(
        descriptor->number(),
        WireFormat::WireTypeForFieldType(descriptor->type())));
    (*variables)["packed_tag"] = (*variables)["tag"];
  } else {
    // Packable and non-packed
    (*variables)["non_packed_tag"] = (*variables)["tag"];
    (*variables)["packed_tag"] = SimpleItoa(WireFormatLite::MakeTag(
        descriptor->number(), WireFormatLite::WIRETYPE_LENGTH_DELIMITED));
  }
  (*variables)["extends"] = ClassName(params, descriptor->containing_type());

  JavaType java_type = GetJavaType(descriptor->type());
  if (descriptor->is_repeated()) {
    switch (java_type) {
      case JAVATYPE_ENUM:
        (*variables)["class"] = "int[]";
        break;
      case JAVATYPE_MESSAGE:
        (*variables)["class"] =
            ClassName(params, descriptor->message_type()) + "[]";
        break;
      default:
        (*variables)["class"] = PrimitiveTypeName(java_type) + "[]";
        break;
    }
  } else {
    // For singular extensions, the type param must be boxed types.
    switch (java_type) {
      case JAVATYPE_ENUM:
        (*variables)["class"] = "java.lang.Integer";
        break;
      case JAVATYPE_MESSAGE:
        (*variables)["class"] = ClassName(params, descriptor->message_type());
        break;
      default:
        (*variables)["class"] = BoxedPrimitiveTypeName(java_type);
        break;
    }
  }
}

ExtensionGenerator::
ExtensionGenerator(const FieldDescriptor* descriptor, const Params& params)
  : params_(params), descriptor_(descriptor) {
  SetVariables(descriptor, params, &variables_);
}

ExtensionGenerator::~ExtensionGenerator() {}

void ExtensionGenerator::Generate(io::Printer* printer) const {
  printer->Print("\n");
  PrintFieldComment(printer, descriptor_);
  printer->Print(variables_,
    "public static final com.google.protobuf.nano.Extension<\n"
    "    $extends$,\n"
    "    $class$> $name$ =\n");
  if (descriptor_->is_repeated()) {
    printer->Print(variables_,
      "        com.google.protobuf.nano.Extension.createRepeated(\n"
      "            com.google.protobuf.nano.Extension.$type$,\n"
      "            $class$.class,\n"
      "            $tag$, $non_packed_tag$, $packed_tag$);\n");
  } else {
    printer->Print(variables_,
      "        com.google.protobuf.nano.Extension.create(\n"
      "            com.google.protobuf.nano.Extension.$type$,\n"
      "            $class$.class,\n"
      "            $tag$);\n");
  }
}

}  // namespace javanano
}  // namespace compiler
}  // namespace protobuf
}  // namespace google

