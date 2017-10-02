package gargoyle.ct.pref.impl.prop;

import gargoyle.ct.pref.CTPreferencesProvider;

public class CTPrefCharProperty extends CTPrefProperty<Character> {
    @SuppressWarnings("MagicCharacter")
    public CTPrefCharProperty(CTPreferencesProvider provider, String name) {
        this(provider, name, '\0');
    }

    public CTPrefCharProperty(CTPreferencesProvider provider, String name, Character def) {
        super(Character.class, provider, name, def);
    }
}