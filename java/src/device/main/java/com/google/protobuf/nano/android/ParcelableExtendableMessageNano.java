// Protocol Buffers - Google's data interchange format
// Copyright 2014 Google Inc.  All rights reserved.
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

package com.google.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.ExtendableMessageNano;

import java.io.IOException;

/**
 * Base class for parcelable Protocol Buffer messages which also need to store unknown
 * fields, such as extensions.
 */
public abstract class ParcelableExtendableMessageNano<M extends ExtendableMessageNano<M>>
        extends ExtendableMessageNano<M> implements Parcelable {
    private static final String TAG = ParcelableExtendableMessageNano.class.getSimpleName();

    // Used by Parcelable
    @SuppressWarnings("unused")
    public static final Creator<ParcelableExtendableMessageNano> CREATOR =
            new Creator<ParcelableExtendableMessageNano>() {
        @Override
        public ParcelableExtendableMessageNano createFromParcel(Parcel in) {
            String className = in.readString();
            int dataLength = in.readInt();
            byte[] data = new byte[dataLength];
            in.readByteArray(data);

            ParcelableExtendableMessageNano proto = null;

            try {
                Class<?> clazz = Class.forName(className);
                Object instance = clazz.newInstance();
                proto = (ParcelableExtendableMessageNano) instance;
                proto = (ParcelableExtendableMessageNano) proto.mergeFrom(
                        CodedInputByteBufferNano.newInstance(data));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Exception trying to create proto from parcel", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Exception trying to create proto from parcel", e);
            } catch (InstantiationException e) {
                Log.e(TAG, "Exception trying to create proto from parcel", e);
            } catch (InvalidProtocolBufferNanoException e) {
                Log.e(TAG, "Exception trying to create proto from parcel", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception trying to create proto from parcel", e);
            }

            return proto;
        }

        @Override
        public ParcelableExtendableMessageNano[] newArray(int size) {
            return new ParcelableExtendableMessageNano[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClass().getName());
        byte[] data = toByteArray(this);
        out.writeInt(data.length);
        out.writeByteArray(data);
    }
}
