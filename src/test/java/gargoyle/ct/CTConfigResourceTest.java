package gargoyle.ct;

import gargoyle.ct.resource.impl.CTConfigResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CTConfigResourceTest {
    private static final String CT_CFG = "CT.cfg";
    private static final String HTTP_ENDPOINT = "http://www.example.com/";

    @Test
    public void testFileResource() throws MalformedURLException {
        CTConfigResource resource = CTConfigResource.forURL(new URL("file:///"));
        assertTrue(resource.exists(), "root file does not exist");
    }

    @Test
    public void testFindResource() {
        assumeTrue(() -> CTConfigResourceTest.class.getClassLoader().getResource(CT_CFG) != null,
                String.format("resource %s does not exist", CT_CFG));
        CTConfigResource resource = CTConfigResource.findLocalConfig(CT_CFG, true);
        assertNotNull(resource, "cannot find config file");
        assertTrue(resource.exists(), "config file does not exist");
    }

    @Test
    public void testHttpResource() throws MalformedURLException {
        CTConfigResource resource = CTConfigResource.forURL(new URL(HTTP_ENDPOINT));
        assumeTrue(() -> {
            try {
                return new URL(HTTP_ENDPOINT).getContent() != null;
            } catch (IOException e) {
                return false;
            }
        }, String.format("%s is unreachable", HTTP_ENDPOINT));
        assertTrue(resource.exists(), () -> String.format("%s does not exist", HTTP_ENDPOINT));
    }
}
