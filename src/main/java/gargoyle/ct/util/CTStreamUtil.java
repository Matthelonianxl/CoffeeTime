package gargoyle.ct.util;

import gargoyle.ct.log.Log;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Scanner;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public final class CTStreamUtil {
    private static final String DELIMITER = "\\A";

    private CTStreamUtil() {
    }

    @NotNull
    public static String convertStreamToString(@NotNull InputStream is, @NotNull String charsetName) {
        try (Scanner scanner = new Scanner(is, charsetName); Scanner s = scanner.useDelimiter(DELIMITER)) {
            return s.hasNext() ? s.next() : "";
        } catch (RuntimeException ex) {
            Log.error(ex, ex.getMessage());
            throw ex;
        }
    }

    public static void write(@NotNull OutputStream stream, @NotNull String content, @NotNull String charsetName) {
        try (Writer writer = new OutputStreamWriter(stream, charsetName)) {
            writer.write(content);
            writer.flush();
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IOException ex) {
            throw new ResourceException(ex.getLocalizedMessage(), ex);
        }
    }
}
