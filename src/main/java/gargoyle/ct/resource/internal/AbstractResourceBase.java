package gargoyle.ct.resource.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarFile;

@SuppressWarnings("MethodMayBeStatic")
public abstract class AbstractResourceBase {
    private static final String KEY_USER_AGENT = "User-Agent";
    private static final String METHOD_HEAD = "HEAD";
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_JAR = "jar";
    private static final String VALUE_USER_AGENT =
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)";

    protected boolean isReadable(@NotNull URL url) {
        if (!exists(url)) {
            return false;
        }
        try (InputStream inputStream = getInputStream(url)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected boolean exists(@Nullable URL url) {
        if (url == null) {
            return false;
        }
        try {
            final String protocol = url.getProtocol();
            if (SCHEME_HTTP.equalsIgnoreCase(protocol)) {
                URLConnection connection = url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection huc = (HttpURLConnection) connection;
                    huc.setInstanceFollowRedirects(false);
                    huc.setRequestMethod(METHOD_HEAD);
                    huc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                    huc.connect();
                    int responseCode = huc.getResponseCode();
                    huc.disconnect();
                    return responseCode == HttpURLConnection.HTTP_OK;
                }
            }
            if (SCHEME_FILE.equalsIgnoreCase(protocol)) {
                return Files.exists(Paths.get(url.toURI()));
            }
            if (SCHEME_JAR.equalsIgnoreCase(protocol)) {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    JarURLConnection juc = (JarURLConnection) connection;
                    juc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                    juc.connect();
                    try (JarFile jarFile = juc.getJarFile()) {
                        return jarFile != null;
                    }
                }
            }
            return fail(protocol);
        } catch (@NotNull IOException | URISyntaxException ex) {
            return false;
        }
    }

    @NotNull
    private <T> T fail(String protocol) {
        throw new UnsupportedOperationException("unsupported protocol: " + protocol);
    }

    protected InputStream getInputStream(@NotNull URL url) throws IOException {
        final String protocol = url.getProtocol();
        if (SCHEME_HTTP.equalsIgnoreCase(protocol)) {
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection) connection;
                huc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                return connection.getInputStream();
            }
        }
        if (SCHEME_FILE.equalsIgnoreCase(protocol)) {
            try {
                return Files.newInputStream(Paths.get(url.toURI()));
            } catch (URISyntaxException e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }
        if (SCHEME_JAR.equalsIgnoreCase(protocol)) {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection) {
                JarURLConnection juc = (JarURLConnection) connection;
                juc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                return connection.getInputStream();
            }
        }
        return fail(protocol);
    }

    protected boolean isWritable(@NotNull URL url) {
        try (OutputStream outputStream = getOutputStream(url, false)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected OutputStream getOutputStream(@NotNull URL url, boolean create) throws IOException {
        final String protocol = url.getProtocol();
        if (SCHEME_HTTP.equalsIgnoreCase(protocol)) {
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection) connection;
                huc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                return connection.getOutputStream();
            }
        }
        if (SCHEME_FILE.equalsIgnoreCase(protocol)) {
            try {
                Path path = Paths.get(url.toURI());
                //noinspection BooleanVariableAlwaysNegated
                boolean exists = Files.exists(path);
                try {
                    return Files.newOutputStream(path, StandardOpenOption.CREATE);
                } finally {
                    if (!create && !exists) {
                        Files.delete(path);
                    }
                }
            } catch (URISyntaxException e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }
        if (SCHEME_JAR.equalsIgnoreCase(protocol)) {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection) {
                JarURLConnection juc = (JarURLConnection) connection;
                juc.setRequestProperty(KEY_USER_AGENT, VALUE_USER_AGENT);
                return connection.getOutputStream();
            }
        }
        return fail(protocol);
    }
}
