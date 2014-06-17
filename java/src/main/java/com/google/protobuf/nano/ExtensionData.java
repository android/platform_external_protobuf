package com.google.protobuf.nano;

import java.util.List;

/**
 * Stores values of extensions.
 */
public class ExtensionData<E extends Extension<?, T>, T> {
    final E extension;
    T value;

    ExtensionData(E extension, T value) {
        this.extension = extension;
        this.value = value;
    }

    List<UnknownFieldData> storeValueIn(List<UnknownFieldData> unknownFields) {
        return extension.setValueTo(value, unknownFields);
    }

    int computeSerializedSize() {
        return extension.computeSerializedSize(value);
    }
}
