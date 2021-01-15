package gargoyle.ct.resource.internal;

import gargoyle.ct.messages.util.UTF8Control;
import gargoyle.ct.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle.Control;

/**
 * represents virtual {@link Resource} (means physical existence is not required)
 */
public class VirtualResource extends AbstractResource {
    protected static final String DOT = ".";
    private static final char CHAR_DOT = '.';
    private final Resource baseResource;
    private final Control control = UTF8Control.getControl(StandardCharsets.UTF_8);
    private Locale locale;

    protected VirtualResource(Resource baseResource, String location) {
        super(location);
        this.baseResource = baseResource;
    }

    protected VirtualResource(String location) {
        super(location);
        baseResource = this;
    }

    @NotNull
    @Override
    public Resource forLocale(@NotNull Locale locale) {
        String location = baseResource.getLocation();
        int l = location.lastIndexOf(CHAR_DOT);
        String baseName = location.substring(0, l);
        String suffix = location.substring(l + 1);
        for (Locale specificLocale : control.getCandidateLocales(baseName, locale)) {
            String loc = control.toResourceName(control.toBundleName(baseName, specificLocale), suffix);
            VirtualResource resource = null;
            try {
                resource = createResource(baseResource, loc);
            } catch (RuntimeException ignored) {
            }
            if (resource != null && resource.exists()) {
                resource.locale = locale;
                return resource;
            }
        }
        return baseResource;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    protected VirtualResource createResource(@Nullable Resource base, String location) {
        return base == null ? new VirtualResource(location) : new VirtualResource(base, location);
    }

    @Nullable
    @Override
    public URL toURL() throws IOException {
        return Objects.equals(baseResource, this) ? super.toURL() : new URL(baseResource.toURL(), getLocation());
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @NotNull
    public String getBaseName() {
        String location = getLocation();
        int l = location.lastIndexOf(DOT);
        return l < 0 ? location : location.substring(0, l);
    }

    @Nullable
    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public Resource withExtension(String extension) {
        String location = getLocation();
        //noinspection StringOperationCanBeSimplified
        String
                loc =
                new StringBuilder().append(location.substring(0, location.lastIndexOf(CHAR_DOT)))
                        .append(DOT).append(extension).toString();
        return createResource(null, loc);
    }

    @Override
    public boolean isBase() {
        return baseResource == this;
    }

    @NotNull
    public String getExtension() {
        String location = getLocation();
        return location.substring(location.lastIndexOf(DOT) + 1);
    }

    @NotNull
    public String getName() {
        String location = getLocation();
        int l1 = location.lastIndexOf('/');
        if (l1 < 0) {
            int l2 = location.lastIndexOf(File.separatorChar);
            return l2 < 0 ? location : location.substring(l2);
        } else {
            return location.substring(l1);
        }
    }

    public String getRelativeLocation() {
        String thisLocation = getLocation();
        String baseLocation = baseResource.getLocation();
        int minLength = Math.min(thisLocation.length(), baseLocation.length());
        for (int i = 0; i < minLength; i++) {
            if (thisLocation.charAt(i) != baseLocation.charAt(i)) {
                return thisLocation.substring(i);
            }
        }
        return thisLocation;
    }
}
