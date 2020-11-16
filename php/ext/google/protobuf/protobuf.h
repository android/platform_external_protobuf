// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// https://developers.google.com/protocol-buffers/
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

#ifndef PHP_PROTOBUF_H_
#define PHP_PROTOBUF_H_

#include <php.h>
#include <stdbool.h>

#include "php-upb.h"

<<<<<<< HEAD   (06eefd Skip ab/6749736 in stage.)
#define PHP_PROTOBUF_EXTNAME "protobuf"
#define PHP_PROTOBUF_VERSION "3.9.1"

#define MAX_LENGTH_OF_INT64 20
#define SIZEOF_INT64 8

/* From Chromium. */
#define ARRAY_SIZE(x) \
    ((sizeof(x)/sizeof(0[x])) / ((size_t)(!(sizeof(x) % sizeof(0[x])))))

// -----------------------------------------------------------------------------
// PHP7 Wrappers
// ----------------------------------------------------------------------------
=======
const zval *get_generated_pool();
>>>>>>> BRANCH (2514f0 Removed protoc-artifacts/target directory)

#if PHP_VERSION_ID < 70300
#define GC_ADDREF(h) ++GC_REFCOUNT(h)
#define GC_DELREF(h) --GC_REFCOUNT(h)
#endif

// Since php 7.4, the write_property() object handler now returns the assigned
// value (after possible type coercions) rather than void.
// https://github.com/php/php-src/blob/PHP-7.4.0/UPGRADING.INTERNALS#L171-L173
#if PHP_VERSION_ID < 70400
#define PROTO_RETURN_VAL void
#else
#define PROTO_RETURN_VAL zval*
#endif

// Sine php 8.0, the Object Handlers API was changed to receive zend_object*
// instead of zval* and zend_string* instead of zval* for property names.
// https://github.com/php/php-src/blob/php-8.0.0beta1/UPGRADING.INTERNALS#L37-L39
#if PHP_VERSION_ID < 80000
#define PROTO_VAL zval
#define PROTO_STR zval
#define PROTO_MSG_P(obj) (Message*)Z_OBJ_P(obj)
#define PROTO_STRVAL_P(obj) Z_STRVAL_P(obj)
#define PROTO_STRLEN_P(obj) Z_STRLEN_P(obj)
#else
#define PROTO_VAL zend_object
#define PROTO_STR zend_string
#define PROTO_MSG_P(obj) (Message*)(obj)
#define PROTO_STRVAL_P(obj) ZSTR_VAL(obj)
#define PROTO_STRLEN_P(obj) ZSTR_LEN(obj)
#endif

#define PHP_PROTOBUF_VERSION "3.14.0"

// ptr -> PHP object cache. This is a weak map that caches lazily-created
// wrapper objects around upb types:
//  * upb_msg* -> Message
//  * upb_array* -> RepeatedField
//  * upb_map*, -> MapField
//  * upb_msgdef* -> Descriptor
//  * upb_enumdef* -> EnumDescriptor
//  * zend_class_entry* -> Descriptor
//
// Each wrapped object should add itself to the map when it is constructed, and
// remove itself from the map when it is destroyed. This is how we ensure that
// the map only contains live objects. The map is weak so it does not actually
// take references to the cached objects.
void ObjCache_Add(const void *key, zend_object *php_obj);
void ObjCache_Delete(const void *key);
bool ObjCache_Get(const void *key, zval *val);

// PHP class name map. This is necessary because the pb_name->php_class_name
// transformation is non-reversible, so when we need to look up a msgdef or
// enumdef by PHP class, we can't turn the class name into a pb_name.
//  * php_class_name -> upb_msgdef*
//  * php_class_name -> upb_enumdef*
void NameMap_AddMessage(const upb_msgdef *m);
void NameMap_AddEnum(const upb_enumdef *m);
const upb_msgdef *NameMap_GetMessage(zend_class_entry *ce);
const upb_enumdef *NameMap_GetEnum(zend_class_entry *ce);

<<<<<<< HEAD   (06eefd Skip ab/6749736 in stage.)
void descriptor_name_set(Descriptor *desc, const char *name);

PHP_PROTO_WRAP_OBJECT_START(FieldDescriptor)
  const upb_fielddef* fielddef;
PHP_PROTO_WRAP_OBJECT_END

PHP_METHOD(FieldDescriptor, getName);
PHP_METHOD(FieldDescriptor, getNumber);
PHP_METHOD(FieldDescriptor, getLabel);
PHP_METHOD(FieldDescriptor, getType);
PHP_METHOD(FieldDescriptor, isMap);
PHP_METHOD(FieldDescriptor, getEnumType);
PHP_METHOD(FieldDescriptor, getMessageType);

extern zend_class_entry* field_descriptor_type;

PHP_PROTO_WRAP_OBJECT_START(EnumDescriptor)
  const upb_enumdef* enumdef;
  zend_class_entry* klass;  // begins as NULL
PHP_PROTO_WRAP_OBJECT_END

PHP_METHOD(EnumDescriptor, getValue);
PHP_METHOD(EnumDescriptor, getValueCount);

extern zend_class_entry* enum_descriptor_type;

PHP_PROTO_WRAP_OBJECT_START(EnumValueDescriptor)
  const char* name;
  int32_t number;
PHP_PROTO_WRAP_OBJECT_END

PHP_METHOD(EnumValueDescriptor, getName);
PHP_METHOD(EnumValueDescriptor, getNumber);

extern zend_class_entry* enum_value_descriptor_type;

// -----------------------------------------------------------------------------
// Message class creation.
// -----------------------------------------------------------------------------

void* message_data(MessageHeader* msg);
void custom_data_init(const zend_class_entry* ce,
                      MessageHeader* msg PHP_PROTO_TSRMLS_DC);

// Build PHP class for given descriptor. Instead of building from scratch, this
// function modifies existing class which has been partially defined in PHP
// code.
void build_class_from_descriptor(
    PHP_PROTO_HASHTABLE_VALUE php_descriptor TSRMLS_DC);

extern zend_class_entry* message_type;
extern zend_object_handlers* message_handlers;

// -----------------------------------------------------------------------------
// Message layout / storage.
// -----------------------------------------------------------------------------

/*
 * In c extension, each protobuf message is a zval instance. The zval instance
 * is like union, which can be used to store int, string, zend_object_value and
 * etc. For protobuf message, the zval instance is used to store the
 * zend_object_value.
 *
 * The zend_object_value is composed of handlers and a handle to look up the
 * actual stored data. The handlers are pointers to functions, e.g., read,
 * write, and etc, to access properties.
 *
 * The actual data of protobuf messages is stored as MessageHeader in zend
 * engine's central repository. Each MessageHeader instance is composed of a
 * zend_object, a Descriptor instance and the real message data.
 *
 * For the reason that PHP's native types may not be large enough to store
 * protobuf message's field (e.g., int64), all message's data is stored in
 * custom memory layout and is indexed by the Descriptor instance.
 *
 * The zend_object contains the zend class entry and the properties table. The
 * zend class entry contains all information about protobuf message's
 * corresponding PHP class. The most useful information is the offset table of
 * properties. Because read access to properties requires returning zval
 * instance, we need to convert data from the custom layout to zval instance.
 * Instead of creating zval instance for every read access, we use the zval
 * instances in the properties table in the zend_object as cache.  When
 * accessing properties, the offset is needed to find the zval property in
 * zend_object's properties table. These properties will be updated using the
 * data from custom memory layout only when reading these properties.
 *
 * zval
 * |-zend_object_value obj
 *   |-zend_object_handlers* handlers -> |-read_property_handler
 *   |                                   |-write_property_handler
 *   |                              ++++++++++++++++++++++
 *   |-zend_object_handle handle -> + central repository +
 *                                  ++++++++++++++++++++++
 *  MessageHeader <-----------------|
 *  |-zend_object std
 *  | |-class_entry* ce -> class_entry
 *  | |                    |-HashTable properties_table (name->offset)
 *  | |-zval** properties_table <------------------------------|
 *  |                         |------> zval* property(cache)
 *  |-Descriptor* desc (name->offset)
 *  |-void** data <-----------|
 *           |-----------------------> void* property(data)
 *
 */

#define MESSAGE_FIELD_NO_CASE ((size_t)-1)

struct MessageField {
  size_t offset;
  int cache_index;  // Each field except oneof field has a zval cache to avoid
                    // multiple creation when being accessed.
  size_t case_offset;   // for oneofs, a uint32. Else, MESSAGE_FIELD_NO_CASE.
};

struct MessageLayout {
  const upb_msgdef* msgdef;
  MessageField* fields;
  size_t size;
};

PHP_PROTO_WRAP_OBJECT_START(MessageHeader)
  void* data;  // Point to the real message data.
               // Place needs to be consistent with map_parse_frame_data_t.
  Descriptor* descriptor;  // Kept alive by self.class.descriptor reference.
PHP_PROTO_WRAP_OBJECT_END

MessageLayout* create_layout(const upb_msgdef* msgdef);
void layout_init(MessageLayout* layout, void* storage,
                 zend_object* object PHP_PROTO_TSRMLS_DC);
zval* layout_get(MessageLayout* layout, const void* storage,
                 const upb_fielddef* field, CACHED_VALUE* cache TSRMLS_DC);
void layout_set(MessageLayout* layout, MessageHeader* header,
                const upb_fielddef* field, zval* val TSRMLS_DC);
void layout_merge(MessageLayout* layout, MessageHeader* from,
                  MessageHeader* to TSRMLS_DC);
const char* layout_get_oneof_case(MessageLayout* layout, const void* storage,
                                  const upb_oneofdef* oneof TSRMLS_DC);
void free_layout(MessageLayout* layout);
uint32_t* slot_oneof_case(MessageLayout* layout, const void* storage,
                          const upb_fielddef* field);
void* slot_memory(MessageLayout* layout, const void* storage,
                  const upb_fielddef* field);

PHP_METHOD(Message, clear);
PHP_METHOD(Message, mergeFrom);
PHP_METHOD(Message, readOneof);
PHP_METHOD(Message, writeOneof);
PHP_METHOD(Message, whichOneof);
PHP_METHOD(Message, __construct);

// -----------------------------------------------------------------------------
// Encode / Decode.
// -----------------------------------------------------------------------------

// Maximum depth allowed during encoding, to avoid stack overflows due to
// cycles.
#define ENCODE_MAX_NESTING 63

// Constructs the upb decoder method for parsing messages of this type.
// This is called from the message class creation code.
const upb_pbdecodermethod *new_fillmsg_decodermethod(Descriptor *desc,
                                                     const void *owner);
void serialize_to_string(zval* val, zval* return_value TSRMLS_DC);
void merge_from_string(const char* data, int data_len, Descriptor* desc,
                       MessageHeader* msg);

PHP_METHOD(Message, serializeToString);
PHP_METHOD(Message, mergeFromString);
PHP_METHOD(Message, serializeToJsonString);
PHP_METHOD(Message, mergeFromJsonString);
PHP_METHOD(Message, discardUnknownFields);

// -----------------------------------------------------------------------------
// Type check / conversion.
// -----------------------------------------------------------------------------

bool protobuf_convert_to_int32(zval* from, int32_t* to);
bool protobuf_convert_to_uint32(zval* from, uint32_t* to);
bool protobuf_convert_to_int64(zval* from, int64_t* to);
bool protobuf_convert_to_uint64(zval* from, uint64_t* to);
bool protobuf_convert_to_float(zval* from, float* to);
bool protobuf_convert_to_double(zval* from, double* to);
bool protobuf_convert_to_bool(zval* from, int8_t* to);
bool protobuf_convert_to_string(zval* from);

void check_repeated_field(const zend_class_entry* klass, PHP_PROTO_LONG type,
                          zval* val, zval* return_value);
void check_map_field(const zend_class_entry* klass, PHP_PROTO_LONG key_type,
                     PHP_PROTO_LONG value_type, zval* val, zval* return_value);

PHP_METHOD(Util, checkInt32);
PHP_METHOD(Util, checkUint32);
PHP_METHOD(Util, checkInt64);
PHP_METHOD(Util, checkUint64);
PHP_METHOD(Util, checkEnum);
PHP_METHOD(Util, checkFloat);
PHP_METHOD(Util, checkDouble);
PHP_METHOD(Util, checkBool);
PHP_METHOD(Util, checkString);
PHP_METHOD(Util, checkBytes);
PHP_METHOD(Util, checkMessage);
PHP_METHOD(Util, checkMapField);
PHP_METHOD(Util, checkRepeatedField);

// -----------------------------------------------------------------------------
// Native slot storage abstraction.
// -----------------------------------------------------------------------------

#define NATIVE_SLOT_MAX_SIZE sizeof(uint64_t)

size_t native_slot_size(upb_fieldtype_t type);
bool native_slot_set(upb_fieldtype_t type, const zend_class_entry* klass,
                     void* memory, zval* value TSRMLS_DC);
// String/Message is stored differently in array/map from normal message fields.
// So we need to make a special method to handle that.
bool native_slot_set_by_array(upb_fieldtype_t type,
                              const zend_class_entry* klass, void* memory,
                              zval* value TSRMLS_DC);
bool native_slot_set_by_map(upb_fieldtype_t type, const zend_class_entry* klass,
                            void* memory, zval* value TSRMLS_DC);
void native_slot_init(upb_fieldtype_t type, void* memory, CACHED_VALUE* cache);
// For each property, in order to avoid conversion between the zval object and
// the actual data type during parsing/serialization, the containing message
// object use the custom memory layout to store the actual data type for each
// property inside of it.  To access a property from php code, the property
// needs to be converted to a zval object. The message object is not responsible
// for providing such a zval object. Instead the caller needs to provide one
// (cache) and update it with the actual data (memory).
void native_slot_get(upb_fieldtype_t type, const void* memory,
                     CACHED_VALUE* cache TSRMLS_DC);
// String/Message is stored differently in array/map from normal message fields.
// So we need to make a special method to handle that.
void native_slot_get_by_array(upb_fieldtype_t type, const void* memory,
                     CACHED_VALUE* cache TSRMLS_DC);
void native_slot_get_by_map_key(upb_fieldtype_t type, const void* memory,
                                int length, CACHED_VALUE* cache TSRMLS_DC);
void native_slot_get_by_map_value(upb_fieldtype_t type, const void* memory,
                                  CACHED_VALUE* cache TSRMLS_DC);
void native_slot_get_default(upb_fieldtype_t type,
                             CACHED_VALUE* cache TSRMLS_DC);

// -----------------------------------------------------------------------------
// Map Field.
// -----------------------------------------------------------------------------

extern zend_object_handlers* map_field_handlers;
extern zend_object_handlers* map_field_iter_handlers;

PHP_PROTO_WRAP_OBJECT_START(Map)
  upb_fieldtype_t key_type;
  upb_fieldtype_t value_type;
  const zend_class_entry* msg_ce;  // class entry for value message
  upb_strtable table;
PHP_PROTO_WRAP_OBJECT_END

PHP_PROTO_WRAP_OBJECT_START(MapIter)
  Map* self;
  upb_strtable_iter it;
PHP_PROTO_WRAP_OBJECT_END

void map_begin(zval* self, MapIter* iter TSRMLS_DC);
void map_next(MapIter* iter);
bool map_done(MapIter* iter);
const char* map_iter_key(MapIter* iter, int* len);
upb_value map_iter_value(MapIter* iter, int* len);

// These operate on a map-entry msgdef.
const upb_fielddef* map_entry_key(const upb_msgdef* msgdef);
const upb_fielddef* map_entry_value(const upb_msgdef* msgdef);

void map_field_create_with_field(const zend_class_entry* ce,
                                 const upb_fielddef* field,
                                 CACHED_VALUE* map_field PHP_PROTO_TSRMLS_DC);
void map_field_create_with_type(const zend_class_entry* ce,
                                upb_fieldtype_t key_type,
                                upb_fieldtype_t value_type,
                                const zend_class_entry* msg_ce,
                                CACHED_VALUE* map_field PHP_PROTO_TSRMLS_DC);
void* upb_value_memory(upb_value* v);

#define MAP_KEY_FIELD 1
#define MAP_VALUE_FIELD 2

// These operate on a map field (i.e., a repeated field of submessages whose
// submessage type is a map-entry msgdef).
bool is_map_field(const upb_fielddef* field);
const upb_fielddef* map_field_key(const upb_fielddef* field);
const upb_fielddef* map_field_value(const upb_fielddef* field);

bool map_index_set(Map *intern, const char* keyval, int length, upb_value v);

PHP_METHOD(MapField, __construct);
PHP_METHOD(MapField, offsetExists);
PHP_METHOD(MapField, offsetGet);
PHP_METHOD(MapField, offsetSet);
PHP_METHOD(MapField, offsetUnset);
PHP_METHOD(MapField, count);
PHP_METHOD(MapField, getIterator);

PHP_METHOD(MapFieldIter, rewind);
PHP_METHOD(MapFieldIter, current);
PHP_METHOD(MapFieldIter, key);
PHP_METHOD(MapFieldIter, next);
PHP_METHOD(MapFieldIter, valid);

// -----------------------------------------------------------------------------
// Repeated Field.
// -----------------------------------------------------------------------------

extern zend_object_handlers* repeated_field_handlers;
extern zend_object_handlers* repeated_field_iter_handlers;

PHP_PROTO_WRAP_OBJECT_START(RepeatedField)
#if PHP_MAJOR_VERSION < 7
  zval* array;
=======
// We need our own assert() because PHP takes control of NDEBUG in its headers.
#ifdef PBPHP_ENABLE_ASSERTS
#define PBPHP_ASSERT(x)                                                    \
  do {                                                                     \
    if (!(x)) {                                                            \
      fprintf(stderr, "Assertion failure at %s:%d %s", __FILE__, __LINE__, \
              #x);                                                         \
      abort();                                                             \
    }                                                                      \
  } while (false)
>>>>>>> BRANCH (2514f0 Removed protoc-artifacts/target directory)
#else
#define PBPHP_ASSERT(x) \
  do {                  \
  } while (false && (x))
#endif

#endif  // PHP_PROTOBUF_H_
