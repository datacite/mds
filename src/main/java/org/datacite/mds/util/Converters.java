package org.datacite.mds.util;

import org.springframework.core.convert.converter.Converter;

public class Converters {
    public static Converter<byte[], String> getByteArrayConverter() {
        return new Converter<byte[], String>() {
            public String convert(byte[] bytes) {
                return new String(bytes);
            }
        };
    }
}
