package gargoyle.ct.messages.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public final class UTF8Control extends Control {
    private static final String PROPERTIES = "properties";
    private static UTF8Control instance;
    private Charset charset;

    private UTF8Control(Charset charset) {
        this.charset = charset;
    }

    public static UTF8Control getControl() {
        return getControl(StandardCharsets.UTF_8);
    }

    public static synchronized UTF8Control getControl(Charset charset) {
        if (instance == null) {
            instance = new UTF8Control(charset);
        }
        return instance;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Nullable
    private static InputStream getStream(@NotNull ClassLoader loader, boolean reload, String resourceName) throws IOException {
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            }
        } else {
            return loader.getResourceAsStream(resourceName);
        }
        return null;
    }

    @Nullable
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, @NotNull ClassLoader loader, boolean reload)
            throws IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, PROPERTIES);
        try (InputStream stream = getStream(loader, reload, resourceName)) {
            return stream != null ? new PropertyResourceBundle(new InputStreamReader(stream, charset)) : null;
        }
    }
}
