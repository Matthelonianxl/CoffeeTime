package gargoyle.ct.convert.impl;

import gargoyle.ct.convert.Converter;
import gargoyle.ct.util.CTSerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;

public class SerializableConverter<T extends Serializable> implements Converter<T> {
    private final BytesConverter converter = new BytesConverter();

    @Override
    public String format(T data) {
        try {
            return converter.format(CTSerializationUtil.serialize(data));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @NotNull
    @Override
    public T parse(@NotNull String data) {
        try {
            return CTSerializationUtil.deserialize(converter.parse(data));
        } catch (IOException ex) {
            throw new IllegalArgumentException(data, ex);
        }
    }
}
