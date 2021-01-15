package gargoyle.ct.pref.impl;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.convert.CTUnitConverter;
import gargoyle.ct.config.convert.impl.CTConfigConverter;
import gargoyle.ct.convert.Converter;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.pref.impl.prop.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CTPreferencesImpl extends CTBasePreferences implements CTPreferences {
    @SuppressWarnings("MagicNumber")
    public CTPreferencesImpl(@NotNull Class<?> clazz) {
        super(clazz);
        addProperty(new CTPrefBooleanProperty(this, PREF_BLOCK, false));
        CTPrefProperty<Integer> transparencyLevel = new CTPrefIntegerProperty(this, PREF_TRANSPARENCY_LEVEL, 30);
        addProperty(transparencyLevel);
        transparencyLevel.addPropertyChangeListener(event -> {
            if (event.getNewValue() < 1) event.getProperty().set(1);
        });
        addProperty(new CTPrefEnumProperty<>(this, ICON_STYLE.class, PREF_ICON_STYLE, ICON_STYLE.BW));
        addProperty(new CTPrefBooleanProperty(this, PREF_SOUND, true));
        addProperty(new CTPrefBooleanProperty(this, PREF_TRANSPARENCY, true));
        addProperty(new CTPrefEnumProperty<>(this, SUPPORTED_LOCALES.class, SUPPORTED_LOCALES.findSimilar()));
        addProperty(new CTPrefObjectProperty<>(CTConfig.class, this, PREF_CONFIG, new ConfigConverterAdapter()));
    }

    @NotNull
    @Override
    public CTPrefProperty<Boolean> block() {
        return getProperty(PREF_BLOCK);
    }

    @NotNull
    @Override
    public CTPrefProperty<CTConfig> config() {
        return getProperty(PREF_CONFIG);
    }

    @NotNull
    @Override
    public CTPrefProperty<ICON_STYLE> iconStyle() {
        return getProperty(PREF_ICON_STYLE);
    }

    @NotNull
    @Override
    public CTPrefProperty<SUPPORTED_LOCALES> supportedLocales() {
        return getProperty(SUPPORTED_LOCALES.class);
    }

    @NotNull
    @Override
    public CTPrefProperty<Boolean> sound() {
        return getProperty(PREF_SOUND);
    }

    @NotNull
    @Override
    public CTPrefProperty<Boolean> transparency() {
        return getProperty(PREF_TRANSPARENCY);
    }

    @NotNull
    @Override
    public CTPrefProperty<Integer> transparencyLevel() {
        return getProperty(PREF_TRANSPARENCY_LEVEL);
    }

    private static class ConfigConverterAdapter implements Converter<CTConfig> {
        private final CTUnitConverter<CTConfig> converter = new CTConfigConverter();

        @NotNull
        @Override
        public String format(CTConfig data) {
            return converter.format(TimeUnit.MINUTES, data);
        }

        @NotNull
        @Override
        public CTConfig parse(String data) {
            return converter.parse(data);
        }
    }
}
