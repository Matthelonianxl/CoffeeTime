package gargoyle.ct.cmd.args.impl;

import gargoyle.ct.cmd.args.CTArgs;
import gargoyle.ct.convert.Converter;
import gargoyle.ct.convert.Converters;
import gargoyle.ct.log.Log;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CTArgsImpl implements CTArgs {
    private static final String EQ = "=";
    private static final String MSG_INVALID_ARGUMENT = "Invalid argument \"{0}\" value \"{1}\", using default \"{2}\"";
    @NotNull
    private final LinkedHashMap<String, String> params;

    public CTArgsImpl(String[] args) {
        synchronized (this) {
            params = new LinkedHashMap<>();
            if (args != null) {
                for (String arg : args) {
                    if (arg != null) {
                        if (arg.contains(EQ)) {
                            String[] pair = arg.split(EQ, 2);
                            String key = pair[0].trim();
                            (params).put(key, pair[1].trim());
                        }
                        else {
                            String value = arg.trim();
                            (params).put(value, value);
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> T get(@NotNull Class<T> type, String key) {
        return get(key, type, Converters.get(type), null);
    }

    @Override
    public <T> T get(@NotNull Class<T> type, String key, T def) {
        return get(key, type, Converters.get(type), def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(int index, @NotNull T def) {
        return get(index, (Class<T>) def.getClass(), Converters.get((Class<T>) def.getClass()), def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, @NotNull T def) {
        return get(key, (Class<T>) def.getClass(), Converters.get((Class<T>) def.getClass()), def);
    }

    @Override
    public boolean has(int index) {
        return params.size() > index;
    }

    @Override
    public boolean has(String key) {
        return params.containsKey(key);
    }

    @Override
    public int size() {
        return params.size();
    }

    @Override
    public <T> T get(int index, Class<T> type, Converter<T> converter) {
        return get(index, type, converter, null);
    }

    @Override
    public <T> T get(int index, Class<T> type, Converter<T> converter, T def) {
        if (params.size() > index) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            Map.Entry<String, String> next = iterator.next();
            for (int i = 0; iterator.hasNext() && i < index; i++) {
                next = iterator.next();
            }
            return get(next.getKey(), type, converter, def);
        }
        else
            return def;
    }

    @Override
    public <T> T get(String key, Class<T> type, Converter<T> converter) {
        return get(key, type, converter, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, Converter<T> converter, T def) {
        if (params.containsKey(key)) {
            String value = params.get(key);
            try {
                return converter.parse(value);
            } catch (IllegalArgumentException ex) {
                Log.error(MSG_INVALID_ARGUMENT, key, value, def);
                return def;
            }
        }
        else {
            return def;
        }
    }
}
