package gargoyle.ct.pref.impl.prop;

import gargoyle.ct.pref.CTPreferencesProvider;
import gargoyle.ct.prop.CTNumberProperty;

public class CTPrefDoubleProperty extends CTPrefProperty<Double> implements CTNumberProperty<Double> {
    public CTPrefDoubleProperty(CTPreferencesProvider provider, String name) {
        this(provider, name, 0);
    }

    public CTPrefDoubleProperty(CTPreferencesProvider provider, String name, double def) {
        super(Double.class, provider, name, def);
    }
}
