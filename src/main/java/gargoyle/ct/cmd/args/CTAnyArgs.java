package gargoyle.ct.cmd.args;

import gargoyle.ct.convert.Converter;

public interface CTAnyArgs {
    <T> T get(Class<T> type, String key);

    <T> T get(Class<T> type, String key, T def);

    <T> T get(int index, T def);

    <T> T get(String key, T def);

    <T> T get(int index, Class<T> type, Converter<T> converter);

    <T> T get(int index, Class<T> type, Converter<T> converter, T def);

    <T> T get(String key, Class<T> type, Converter<T> converter);

    <T> T get(String key, Class<T> type, Converter<T> converter, T def);

    boolean has(int index);

    boolean has(String key);

    int size();
}
