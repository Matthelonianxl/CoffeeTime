package gargoyle.ct.cmd.args;

import gargoyle.ct.convert.Converters;

public interface CTArgs extends CTAnyArgs {

    default boolean getBoolean(int index) {
        return getBoolean(index, false);
    }

    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    default boolean getBoolean(int index, boolean def) {
        return get(index, Boolean.class, Converters.get(Boolean.class), def);
    }

    default boolean getBoolean(String key, boolean def) {
        return get(key, Boolean.class, Converters.get(Boolean.class), def);
    }

    default byte getByte(int index) {
        return getByte(index, (byte) 0);
    }

    default byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    default byte getByte(int index, byte def) {
        return get(index, Byte.class, Converters.get(Byte.class), def);
    }

    default byte getByte(String key, byte def) {
        return get(key, Byte.class, Converters.get(Byte.class), def);
    }

    default byte[] getBytes(int index) {
        return getBytes(index, new byte[0]);
    }

    default byte[] getBytes(String key) {
        return getBytes(key, new byte[0]);
    }

    default byte[] getBytes(int index, byte[] def) {
        return get(index, byte[].class, Converters.get(byte[].class), def);
    }

    default byte[] getBytes(String key, byte[] def) {
        return get(key, byte[].class, Converters.get(byte[].class), def);
    }

    default char getChar(int index) {
        return getChar(index, '\0');
    }

    default char getChar(String key) {
        return getChar(key, '\0');
    }

    default char getChar(int index, char def) {
        return get(index, Character.class, Converters.get(Character.class), def);
    }

    default char getChar(String key, char def) {
        return get(key, Character.class, Converters.get(Character.class), def);
    }

    default double getDouble(int index) {
        return getDouble(index, 0.0d);
    }

    default double getDouble(String key) {
        return getDouble(key, 0.0d);
    }

    default double getDouble(int index, double def) {
        return get(index, Double.class, Converters.get(Double.class), def);
    }

    default double getDouble(String key, double def) {
        return get(key, Double.class, Converters.get(Double.class), def);
    }

    default float getFloat(int index) {
        return getFloat(index, 0.0f);
    }

    default float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    default float getFloat(int index, float def) {
        return get(index, Float.class, Converters.get(Float.class), def);
    }

    default float getFloat(String key, float def) {
        return get(key, Float.class, Converters.get(Float.class), def);
    }

    default int getInteger(int index) {
        return getInteger(index, 0);
    }

    default int getInteger(String key) {
        return getInteger(key, 0);
    }

    default int getInteger(int index, int def) {
        return get(index, Integer.class, Converters.get(Integer.class), def);
    }

    default int getInteger(String key, int def) {
        return get(key, Integer.class, Converters.get(Integer.class), def);
    }

    default long getLong(int index) {
        return getLong(index, 0L);
    }

    default long getLong(String key) {
        return getLong(key, 0L);
    }

    default long getLong(int index, long def) {
        return get(index, Long.class, Converters.get(Long.class), def);
    }

    default long getLong(String key, long def) {
        return get(key, Long.class, Converters.get(Long.class), def);
    }

    default short getShort(int index) {
        return getShort(index, (short) 0);
    }

    default short getShort(String key) {
        return getShort(key, (short) 0);
    }

    default short getShort(int index, short def) {
        return get(index, Short.class, Converters.get(Short.class), def);
    }

    default short getShort(String key, short def) {
        return get(key, Short.class, Converters.get(Short.class), def);
    }

    default String getString(int index) {
        return getString(index, "");
    }

    default String getString(String key) {
        return getString(key, "");
    }

    default String getString(int index, String def) {
        return get(index, String.class, Converters.get(String.class), def);
    }

    default String getString(String key, String def) {
        return get(key, String.class, Converters.get(String.class), def);
    }

}
