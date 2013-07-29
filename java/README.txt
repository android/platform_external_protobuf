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

opt={speed,space}:
  This changes the code generation to optimize for speed,
  opt=speed, or space, opt=space. When opt=speed this
  changes the code generation for strings so that multiple
  conversions to Utf8 are eliminated. The default value
  is opt=space.

java_use_vector={true,false}:
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

java_package=<file-name>|<package-name>:
  The allows setting/overriding the java_package option
  for the given file from the command line.

java_outer_classname=<file-name>|<outer-classname>:
  This allows the setting/overriding of the
  java_outer_classname option for the given file from
  the command line.

java_multiple_files={true,false}:
  This allows setting/overriding the java_multiple_files
  option in all source files and their imported files from
  the command line.


More about MICRO_RUNTIME and the various Java options:

option java_package = "<package-name>"; (in-file)
java_package=<file-name>|<package-name> (command line)
  Specifies the Java package for the class(es) generated
  from the file. If omitted, the protocol buffer package
  (defined with the "package <package-name>;" line) is
  used. If the package line is also omitted, the files
  are placed in Java's default package.

option java_outer_classname = "<class-name>"; (in-file)
java_outer_classname=<file-name>|<class-name> (command line)
  Associates an outer class name to a file. If omitted,
  the outer class name is the file name converted to
  CamelCase. The source file will be compiled to a Java
  class with this outer class name, which nests all
  classes generated from the file-scope messages and
  enums. Previously, javamicro_out would never infer
  the outer class name and would skip the outer class
  generation if an outer class name has not been given
  explicitly. To re-align with java_out, javamicro_out
  will now always generate the outer class, except in a
  special case which will be detailed in the
  "java_multiple_files" option below.

option java_multiple_files = {true,false}; (in-file)
java_multiple_files={true,false} (command line)
  Whether the file should be compiled into multiple Java
  files in the same Java package. This affects where
  classes are placed and how the protocol buffer entities
  are referenced in the Java files. If false, which is the
  default, the file will be compiled into one Java file,
  which is the outer class, nesting all the classes and
  constants generated from file-scope messages and enums.
  If true, all file-scope messages are compiled into
  top-level classes, i.e. siblings of the file's outer
  class, in separate files. The outer class will only
  be used for file-scope enum constants. NOTE: when
  overriding this option from the command line, all source
  files and their imported files are considered to have
  the overriding value. This may change the fully
  qualified names of the references to the imported
  messages and enum values.


Below are a series of examples for clarification of the
various parameters and options. Assuming this file:

src/proto/simple-data-protos.proto:

    package testprotobuf;

    message SimpleData {
      optional fixed64 id = 1;
      optional string description = 2;
      optional bool ok = 3 [default = false];
    };

and the compiled protoc in the current working directory,
then a simple command line to compile this file would be:

./protoc --javamicro_out=. src/proto/simple-data-protos.proto

This will create testprotobuf/SimpleDataProtos.java, which
has the following content (extremely simplified):

    package testprotobuf;

    public final class SimpleDataProtos {
      public static final class SimpleData
          extends MessageMicro {
        ...
      }
    }

The message SimpleData is compiled into the SimpleData
class, nested in the file's outer class SimpleDataProtos,
whose name is implicitly defined by the proto file name
"simple-data-protos".

The directory, aka Java package, testprotobuf is created
because on line 1 of simple-data-protos.proto is
"package testprotobuf;". If you wanted a different
package name you could use the java_package option in the
file:

    option java_package = "my_package";

or in command line sub-parameter:

./protoc '--javamicro_out=\
java_package=src/proto/simple-data-protos.proto|my_package:\
.' src/proto/simple-data-protos.proto

Here you see the new java_package sub-parameter which
itself needs two parameters the file name and the
package name, these are separated by "|". The value set
in the command line overrides the value set in the file.
Now you'll find SimpleDataProtos.java in the my_package/
directory.

If you wanted to also change the optimization for
speed you'd add opt=speed with the comma seperator
as follows:

./protoc '--javamicro_out=\
opt=speed,\
java_package=src/proto/simple-data-protos.proto|my_package:
.' src/proto/simple-data-protos.proto

If you also wanted a different outer class name you'd
do the following:

./protoc '--javamicro_out=\
opt=speed,\
java_package=src/proto/simple-data-protos.proto|my_package,\
java_outer_classname=src/proto/simple-data-protos.proto|OuterName:\
.' src/proto/simple-data-protos.proto

Now you'll find my_package/OuterName.java and the
message class SimpleData nested in it.

As mentioned java_package, java_outer_classname and
java_multiple_files may also be specified in the file.
In the example below we must define
java_outer_classname because otherwise the outer class
and one of the message classes will have the same name,
which is forbidden to prevent name ambiguity:

src/proto/sample-message.proto:

    package testmicroruntime;

    option java_package = "com.example";
    option java_outer_classname = "SampleMessageProtos";

    enum MessageType {
      SAMPLE = 1;
      EXAMPLE = 2;
    }

    message SampleMessage {
      required int32 id = 1;
      required MessageType type = 2;
    }

    message SampleMessageContainer {
      required SampleMessage message = 1;
    }

This could be compiled using:

./protoc --javamicro_out=. src/proto/sample-message.proto

and the output will be:

com/example/SampleMessageProtos.java:

    package com.example;

    public final class SampleMessageProtos {
      public static final int SAMPLE = 1;
      public static final int EXAMPLE = 2;
      public static final class SampleMessage
          extends MessageMicro {
        ...
      }
      public static final class SampleMessageContainer
          extends MessageMicro {
        ...
      }
    }

As you can see the file-scope enum MessageType is
disassembled into two integer constants in the outer class.
In javamicro_out, all enums are disassembled and compiled
into integer constants in the parent scope (the containing
message's class or the file's (i.e. outer) class).

You may prefer the file-scope messages to be saved in
separate files. You can do this by setting the option
java_multiple_files to true, in either the file like this:

    option java_multiple_files = true;

or the command line like this:

./protoc --javamicro_out=\
java_multiple_files=true:\
. src/proto/sample-message.proto

The java_multiple_files option causes javamicro to use a
separate file for each file-scope message, which resides
directly in the Java package alongside the outer class:

com/example/SampleMessageProtos.java:

    package com.example;
    public final class SampleMessageProtos {
      public static final int SAMPLE = 1;
      public static final int EXAMPLE = 2;
    }

com/example/SampleMessage.java:

    package com.example;
    public final class SampleMessage
        extends MessageMicro {
      ...
    }

com/example/SampleMessageContainer.java:

    package com.example;
    public final class SampleMessageContainer
        extends MessageMicro {
      ...
    }

As you can see, the outer class now contains only the
integer constants, generated from the file-scope enum
"MessageType". Please note that message-scope enums are
still generated as integer constants in the message class.


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

java_package           -> <file-name>|<package-name>
java_outer_classname   -> <file-name>|<package-name>
java_multiple_files    -> true or false
java_nano_generate_has -> true or false

java_package:
java_outer_classname:
java_multiple_files:
  Same as Micro version.

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

./protoc '--javanano_out=\
java_package=src/proto/simple-data.proto|my_package,\
java_outer_classname=src/proto/simple-data.proto|OuterName:\
.' src/proto/simple-data.proto

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
