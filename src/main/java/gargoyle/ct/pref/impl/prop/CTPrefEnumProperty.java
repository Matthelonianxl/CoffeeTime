package gargoyle.ct.pref.impl.prop;

import gargoyle.ct.convert.impl.EnumConverter;
import gargoyle.ct.pref.CTPreferencesProvider;

public class CTPrefEnumProperty<E extends Enum<E>> extends CTPrefProperty<E> {
    public CTPrefEnumProperty(CTPreferencesProvider provider, Class<E> type, String name) {
        super(type, new EnumConverter<>(type), provider, name, null);
    }

    public CTPrefEnumProperty(CTPreferencesProvider provider, Class<E> type, String name, E def) {
        super(type, new EnumConverter<>(type), provider, name, def);
    }

    public CTPrefEnumProperty(CTPreferencesProvider provider, Class<E> type) {
        super(type, new EnumConverter<>(type), provider, type.getSimpleName(), null);
    }

    public CTPrefEnumProperty(CTPreferencesProvider provider, Class<E> type, E def) {
        super(type, new EnumConverter<>(type), provider, type.getSimpleName(), def);
    }
}
