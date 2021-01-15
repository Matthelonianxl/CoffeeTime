package gargoyle.ct.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public final class CTSerializationUtil {
    private CTSerializationUtil() {
    }

    /**
     * pipe object through serialization
     *
     * @param orig object
     * @param <T>  type
     * @return - same object
     * @throws IOException any other error
     */
    @NotNull
    public static <T> T pipe(T orig) throws IOException {
        byte[] serialized = serialize(orig);
        return deserialize(serialized);
    }

    /**
     * deserialize object using native serialization
     *
     * @param bytes - source
     * @param <T>   - target type
     * @return - deserialized object
     * @throws IOException any other error
     * @see ObjectInput#readObject()
     */
    @NotNull
    @SuppressWarnings({"unchecked", "MethodCanBeVariableArityMethod"})
    public static <T> T deserialize(@NotNull byte[] bytes) throws IOException {
        try (final ObjectInput ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            try {
                return (T) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * serialize object using native serialization
     *
     * @param o   - source object
     * @param <T> type of object
     * @return - serialized byte array
     * @throws IOException any i\o error
     */
    public static <T> byte[] serialize(T o) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             final ObjectOutput oos = new ObjectOutputStream(out)) {
            oos.writeObject(o);
            oos.flush();
            return out.toByteArray();
        }
    }
}
