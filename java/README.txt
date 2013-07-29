Protocol Buffers - Google's data interchange format
Copyright 2008 Google Inc.

This directory contains the Java Protocol Buffers runtime library.

Installation - With Maven
=========================

The Protocol Buffers build is managed using Maven.  If you would
rather build without Maven, see below.

1) Install Apache Maven if you don't have it:

     http://maven.apache.org/

2) Build the C++ code, or obtain a binary distribution of protoc.  If
   you install a binary distribution, make sure that it is the same
   version as this package.  If in doubt, run:

     $ protoc --version

   You will need to place the protoc executable in ../src.  (If you
   built it yourself, it should already be there.)

3) Run the tests:

     $ mvn test

   If some tests fail, this library may not work correctly on your
   system.  Continue at your own risk.

4) Install the library into your Maven repository:

     $ mvn install

5) If you do not use Maven to manage your own build, you can build a
   .jar file to use:

     $ mvn package

   The .jar will be placed in the "target" directory.

Installation - 'Lite' Version - With Maven
==========================================

Building the 'lite' version of the Java Protocol Buffers library is
the same as building the full version, except that all commands are
run using the 'lite' profile.  (see
http://maven.apache.org/guides/introduction/introduction-to-profiles.html)

E.g. to install the lite version of the jar, you would run:

  $ mvn install -P lite

The resulting artifact has the 'lite' classifier.  To reference it
for dependency resolution, you would specify it as:

  <dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>${version}</version>
    <classifier>lite</classifier>
  </dependency>

Installation - Without Maven
============================

If you would rather not install Maven to build the library, you may
follow these instructions instead.  Note that these instructions skip
running unit tests.

1) Build the C++ code, or obtain a binary distribution of protoc.  If
   you install a binary distribution, make sure that it is the same
   version as this package.  If in doubt, run:

     $ protoc --version

   If you built the C++ code without installing, the compiler binary
   should be located in ../src.

2) Invoke protoc to build DescriptorProtos.java:

     $ protoc --java_out=src/main/java -I../src \
         ../src/google/protobuf/descriptor.proto

3) Compile the code in src/main/java using whatever means you prefer.

4) Install the classes wherever you prefer.

Micro version
============================

The runtime and generated code for MICRO_RUNTIME is smaller
because it does not include support for the descriptor and
reflection, and enums are generated as integer constants in
the parent message or the file's outer class. Also, not
currently supported are packed repeated elements or
extensions.

To create a jar file for the runtime and run tests invoke
"mvn package -P micro" from the <protobuf-root>/java
directory. The generated jar file is
<protobuf-root>java/target/protobuf-java-2.2.0-micro.jar.

If you wish to compile the MICRO_RUNTIME your self, place
the 7 files below, in <root>/com/google/protobuf and
create a jar file for use with your code and the generated
code:

ByteStringMicro.java
CodedInputStreamMicro.java
CodedOutputStreamMicro.java
InvalidProtocolBufferException.java
MessageMicro.java
WireFormatMicro.java

If you wish to change on the code generator it is located
in /src/google/protobuf/compiler/javamicro.

To generate code for the MICRO_RUNTIME invoke protoc with
--javamicro_out command line parameter. javamicro_out takes
a series of optional sub-parameters separated by commas
and a final parameter, with a colon separator, which defines
the source directory. Sub-parameters begin with a name
followed by an equal and if that sub-parameter has multiple
parameters they are seperated by "|". The command line options
are:

opt                  -> speed or space
java_use_vector      -> true or false
java_package         -> <file-name>|<package-name>
java_outer_classname -> <file-name>|<package-name>
java_multiple_files  -> true or false

opt:
  This changes the code generation to optimize for speed,
  opt=speed, or space, opt=space. When opt=speed this
  changes the code generation for strings so that multiple
  conversions to Utf8 are eliminated. The default value
  is opt=space.

java_use_vector:
  Is a boolean flag either java_use_vector=true or
  java_use_vector=false. When java_use_vector=true the
  code generated for repeated elements uses
  java.util.Vector and when java_use_vector=false the
  java.util.ArrayList<> is used. When java.util.Vector
  is used the code must be compiled with Java 1.3 and
  when ArrayList is used Java 1.5 or above must be used.
  The using javac the source parameter may be used to
  control the version of the source: "javac -source 1.3".
  You can also change the <source> xml element for the
  maven-compiler-plugin. Below is for 1.5 sources:

      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>1.5</source>
          <target>1.5</target>
        </configuration>
      </plugin>

  When compiling for 1.5 java_use_vector=false or not
  present where the default value is false.

  And below would be for 1.3 sources note when changing
  to 1.3 you must also set java_use_vector=true:

      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>1.3</source>
          <target>1.5</target>
        </configuration>
      </plugin>

java_package:
  The allows setting/overriding the java_package option
  and allows a package name for a file to be specified
  on the command line, overriding any
  "option java_package xxxx" in the file. The default
  if not present is to use the value from the package
  statment or "option java_package xxxx" in the file.

java_outer_classname:
  This allows the setting/overriding of the outer
  class name option and associates a class name
  to a file, overriding any
  "option java_outer_classname xxxx" in the file. The
  default if not present is to use the value from the
  in-file option or infer a suitable class name from
  the file name. Unlike previous implementations, the
  outer class will be generated even if the source
  file contains only one message and no explicitly
  defined outer class name. This is to re-align with
  the specs and the main Java generator's behavior.
  See below for an exception.

java_multiple_files:
  This allows setting/overriding the java_multiple_files
  option. If true, all file-scope messages are generated
  as top-level classes, i.e. siblings of the file's
  outer class. The outer class is generated only if
  necessary for the file-scope enum constants. If there
  are no file-scope enums, the outer class name can be
  used by a file-scope message. If false, all file-scope
  messages and enum constants are contained in the outer
  class, whose name cannot be reused by a file-scope
  message. When overriding this option in the command
  line sub-parameter, all source files and their imports
  are considered to have the overridden value.


Below are a series of examples for clarification of the
various javamicro_out parameters using
src/test/proto/simple-data-protos.proto:

package testprotobuf;

message SimpleData {
  optional fixed64 id = 1;
  optional string description = 2;
  optional bool ok = 3 [default = false];
};

Assuming you've only compiled and not installed protoc and
your current working directory java/, then a simple
command line to compile simple-data would be:

../src/protoc --javamicro_out=. src/test/proto/simple-data-protos.proto

This will create testprotobuf/SimpleDataDef.java, which
is an outer class containing the message class SimpleData.

The directory testprotobuf is created because on line 1
of simple-data.proto is "package testprotobuf;". If you
wanted a different package name you could use the
java_package option command line sub-parameter:

../src/protoc '--javamicro_out=java_package=src/test/proto/simple-data-protos.proto|my_package:.' src/test/proto/simple-data-protos.proto

Here you see the new java_package sub-parameter which
itself needs two parameters the file name and the
package name, these are separated by "|". Now you'll
find my_package/SimpleDataProtos.java.

If you wanted to also change the optimization for
speed you'd add opt=speed with the comma seperator
as follows:

../src/protoc '--javamicro_out=opt=speed,java_package=src/test/proto/simple-data-protos.proto|my_package:.' src/test/proto/simple-data-protos.proto

If you also wanted a different outer class name you'd
do the following:

../src/protoc '--javamicro_out=opt=speed,java_package=src/test/proto/simple-data-protos.proto|my_package,java_outer_classname=src/test/proto/simple-data-protos.proto|OuterName:.' src/test/proto/simple-data-protos.proto

Now you'll find my_package/OuterName.java.

If you don't want the outer class at all, do this:

../src/protoc '--javamicro_out=opt=speed,java_package=src/test/proto/simple-data-protos.proto|my_package,java_multiple_files=true:.' src/test/proto/simple-data-protos.proto

Now you'll find my_package/SimpleData.java without
the outer class.

As mentioned java_package, java_outer_classname and
java_multiple_files may also be specified in the file.
In the example below we must define
java_outer_classname because otherwise the outer class
and one of the message classes will have the same name:
src/test/proto/sample-message.proto:

package testmicroruntime;

option java_package = "com.example";
option java_outer_classname = "SampleMessageProtos";

message SampleMessage {
  required int32 id = 1;
}

message SampleMessageContainer {
  required SampleMessage message = 1;
}

This could be compiled using:

../src/protoc --javamicro_out=. src/test/proto/sample-message.proto

With the result will be com/example/SampleMessageProtos.java

Alternatively, the line with option java_outer_classname
can be replaced with

option java_multiple_files = true;

If so, despite the clash between the message class name
and the implicit outer class name, because the outer
class is not needed, compiling this file with the same
command line will succeed and yield two message classes:

com/example/SampleMessage.java
com/example/SampleMessageContainer.java


Nano version
============================

Nano is even smaller than micro, especially in the number of generated
functions. It is like micro except:

- No setter/getter/hazzer functions.
- Has state is not available. Outputs all fields not equal to their
  default. (See important implications below.)
- CodedInputStreamMicro is renamed to CodedInputByteBufferNano and can
  only take byte[] (not InputStream).
- Similar rename from CodedOutputStreamMicro to
  CodedOutputByteBufferNano.
- Repeated fields are in arrays, not ArrayList or Vector.
- Unset messages/groups are null, not an immutable empty default
  instance.
- Required fields are always serialized.
- toByteArray(...) and mergeFrom(...) are now static functions of
  MessageNano.
- "bytes" are of java type byte[].

IMPORTANT: If you have fields with defaults

How fields with defaults are serialized has changed. Because we don't
keep "has" state, any field equal to its default is assumed to be not
set and therefore is not serialized. Consider the situation where we
change the default value of a field. Senders compiled against an older
version of the proto continue to match against the old default, and
don't send values to the receiver even though the receiver assumes the
new default value. Therefore, think carefully about the implications
of changing the default value.

IMPORTANT: If you have "bytes" fields with non-empty defaults

Because the byte buffer is now of mutable type byte[], the default
static final cannot be exposed through a public field. Each time a
message's constructor or clear() function is called, the default value
(kept in a private byte[]) is cloned. This causes a small memory
penalty. This is not a problem if the field has no default or is an
empty default.

Nano Generator options

java_nano_generate_has:
  If true, generates a public boolean variable has<fieldname>
  accompanying the optional or required field (not present for
  repeated fields, groups or messages). It is set to false initially
  and upon clear(). If parseFrom(...) reads the field from the wire,
  it is set to true. This is a way for clients to inspect the "has"
  value upon parse. If it is set to true, writeTo(...) will ALWAYS
  output that field (even if field value is equal to its
  default).

  IMPORTANT: This option costs an extra 4 bytes per primitive field in
  the message. Think carefully about whether you really need this. In
  many cases reading the default works and determining whether the
  field was received over the wire is irrelevant.

To use nano protobufs:

- Link with the generated jar file
  <protobuf-root>java/target/protobuf-java-2.3.0-nano.jar.
- Invoke with --javanano_out, e.g.:

../src/protoc '--javanano_out=java_package=src/test/proto/simple-data.proto|my_package,java_outer_classname=src/test/proto/simple-data.proto|OuterName:.' src/test/proto/simple-data.proto

Contributing to nano:

Please add/edit tests in NanoTest.java.

Please run the following steps to test:

- cd external/protobuf
- ./configure
- Run "make -j12 check" and verify all tests pass.
- cd java
- Run "mvn test" and verify all tests pass.
- cd ../../..
- . build/envsetup.sh
- lunch 1
- "make -j12 aprotoc libprotobuf-java-2.3.0-nano aprotoc-test-nano-params" and
  check for build errors.
- repo sync -c -j256
- "make -j12" and check for build errors


Usage
=====

The complete documentation for Protocol Buffers is available via the
web at:

  http://code.google.com/apis/protocolbuffers/
