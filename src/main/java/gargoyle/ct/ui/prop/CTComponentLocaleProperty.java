package gargoyle.ct.ui.prop;

import gargoyle.ct.pref.CTPreferences.SUPPORTED_LOCALES;
import gargoyle.ct.prop.impl.CTBaseObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CTComponentLocaleProperty extends CTBaseObservableProperty<SUPPORTED_LOCALES> {
    @NotNull
    private final Component component;

    public CTComponentLocaleProperty(Component component) {
        super(SUPPORTED_LOCALES.class, component.getName());
        this.component = component;
    }

    @Override
    public SUPPORTED_LOCALES get() {
        return SUPPORTED_LOCALES.findSimilar(component.getLocale(), SUPPORTED_LOCALES.findSimilar());
    }

    @Override
    public void set(@NotNull SUPPORTED_LOCALES value) {
        component.setLocale(value.getLocale());
    }
}
