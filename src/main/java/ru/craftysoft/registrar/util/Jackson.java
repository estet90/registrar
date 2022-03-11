package ru.craftysoft.registrar.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Singleton
public class Jackson {

    private final ObjectMapper objectMapper;

    public String toString(Object object) {
        return wrap(() -> this.objectMapper.writeValueAsString(object));
    }

    public byte[] toByteArray(Object object) {
        return wrap(() -> this.objectMapper.writeValueAsBytes(object));
    }


    public <T> T read(InputStream is, TypeReference<T> type) {
        return wrap(() -> this.objectMapper.readValue(is, type));
    }

    public <T> T read(byte[] bytes, TypeReference<T> type) {
        return wrap(() -> this.objectMapper.readValue(bytes, type));
    }

    public <T> T read(String string, TypeReference<T> type) {
        return wrap(() -> this.objectMapper.readValue(string, type));
    }

    public <T> T read(InputStream is, Class<T> type) {
        return wrap(() -> this.objectMapper.readValue(is, type));
    }

    public <T> T read(byte[] bytes, Class<T> type) {
        return wrap(() -> this.objectMapper.readValue(bytes, type));
    }

    public <T> T read(String string, Class<T> type) {
        return wrap(() -> this.objectMapper.readValue(string, type));
    }

    private <T> T wrap(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface ThrowableSupplier<T> {
        T get() throws IOException;
    }

}
