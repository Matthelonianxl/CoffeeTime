package gargoyle.ct;

import gargoyle.ct.messages.impl.CTFixedLocaleProvider;
import gargoyle.ct.resource.Resource;
import gargoyle.ct.resource.internal.TempLocalResource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CTTempLocalResourceTest {
    @NotNull
    private final Logger logger = LoggerFactory.getLogger(CTTempLocalResourceTest.class);
    private Resource resource;

    @Test
    void createResource() {
        assertNotNull(resource, "resource is null");
    }

    @Test
    void exists() {
        assertTrue(resource.exists(), resource + " no exist");
    }

    @BeforeEach
    public void setUp() {
        resource = new TempLocalResource("resource.txt")
                .forLocale(new CTFixedLocaleProvider().getLocale());
    }

    @Test
    void toURL() throws IOException {
        assertNotNull(resource.toURL(), "url is null");
    }
}
