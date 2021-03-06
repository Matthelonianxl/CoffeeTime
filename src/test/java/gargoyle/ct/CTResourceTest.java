package gargoyle.ct;

import gargoyle.ct.log.Log;
import gargoyle.ct.resource.Resource;
import gargoyle.ct.resource.internal.ClasspathResource;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CTResourceTest {
    private static final String DOT_TXT = ".txt";
    private static final String EXT_PROPERTIES = ".properties";
    private static final String LOCALE_RU = "ru";
    private static final String RB_PROPERTIES = "rb.properties";
    private static final String TXT = "txt";

    @Test
    public void testResourceExt() {
        Resource resource = new ClasspathResource(RB_PROPERTIES).withExtension(TXT);
        String url = resource.getLocation();
        Log.info(url);
        assertTrue(url.endsWith(DOT_TXT), "extension not changed");
    }

    @Test
    public void testResourceExtLocale() {
        Resource resource = new ClasspathResource(RB_PROPERTIES).withExtension(TXT).forLocale(new Locale(LOCALE_RU));
        String url = resource.getLocation();
        Log.info(url);
        assertTrue(url.endsWith(DOT_TXT), "don\'t change extension");
    }

    @Test
    public void testResourceLocale() {
        Resource resource = new ClasspathResource(RB_PROPERTIES).forLocale(new Locale(LOCALE_RU));
        String url = resource.getLocation();
        Log.info(url);
        assertTrue(url.endsWith(EXT_PROPERTIES), " should be properties");
    }
}
