package gargoyle.ct;

import gargoyle.ct.cmd.args.CTArgs;
import gargoyle.ct.cmd.impl.CTCmdImpl;
import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.CTConfigs;
import gargoyle.ct.config.CTStandardConfigs;
import gargoyle.ct.config.convert.CTUnitConverter;
import gargoyle.ct.config.convert.impl.CTConfigsConverter;
import gargoyle.ct.ex.CTException;
import gargoyle.ct.log.Log;
import gargoyle.ct.mutex.CTMutex;
import gargoyle.ct.mutex.impl.FileMutex;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.pref.impl.CTPreferencesImpl;
import gargoyle.ct.pref.impl.prop.CTPrefProperty;
import gargoyle.ct.prop.impl.CTPropertyChangeManager;
import gargoyle.ct.resource.Resource;
import gargoyle.ct.resource.impl.CTConfigResource;
import gargoyle.ct.resource.internal.ClasspathResource;
import gargoyle.ct.resource.internal.LocalResource;
import gargoyle.ct.task.CTTaskUpdatable;
import gargoyle.ct.task.helper.CTTimeHelper;
import gargoyle.ct.task.helper.impl.CTTimeHelperImpl;
import gargoyle.ct.task.impl.CTTimer;
import gargoyle.ct.ui.CTApp;
import gargoyle.ct.ui.impl.*;
import gargoyle.ct.ui.impl.control.CTShowingFrame;
import gargoyle.ct.util.CTNumberUtil;
import gargoyle.ct.util.CTStreamUtil;
import gargoyle.ct.ver.CTVersionInfo;
import gargoyle.ct.ver.impl.CTVersionInfoImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class CT implements CTApp {
    private static final String CONFIG_CHARSET = StandardCharsets.UTF_8.name();
    private static final String CONFIG_NAME = "CT.cfg";
    private static final String DOT = ".";
    private static final String HELP_PAGE = "html/help.html";
    private static final String HTML = "html";
    private static final String MSG_ALREADY_RUNNING = "App already running";
    private static final String MSG_CANNOT_LOAD_0 = "Cannot load {0}";
    private static final String MSG_DEBUG_MODE = "debug mode";
    private static final String MSG_FAKE_TIME_SET_0 = "fake time set {0, time, HH:mm:ss}";
    private static final String NOT_FOUND_0 = "Not found {0}";
    private static final String MSG_CANNOT_SET_LOOK_AND_FEEL = "Cannot set LookAndFeel";
    private static final String MSG_CANNOT_OPEN_CONFIG = "Cannot open config";
    private static final String MSG_CANNOT_READ_CONFIG = "Cannot read config";
    private static final String PAGE_0_NOT_FOUND = "Page {0} not found";
    private static final String SLASH = "/";
    private static final String URL_ICON_BIG_W = "/icon/{0}/64/icon64.png";
    private static final String URL_ICON_MEDIUM_W = "/icon/{0}/32/icon32.png";
    private static final String URL_ICON_SMALL_W = "/icon/{0}/16/icon16.png";
    @NotNull
    private final List<CTBlocker> blockers;
    private final CTUnitConverter<CTConfigs> configsConverter = new CTConfigsConverter();
    @NotNull
    private final CTControl control;
    @NotNull
    private final Frame owner;
    @NotNull
    private final CTPreferences preferences;
    @NotNull
    private final CTTimeHelper timeHelper;
    @NotNull
    private final CTTimer timer;
    @NotNull
    private final CTVersionInfo versionInfo;
    @Nullable
    private Resource configResource;
    private final CTMutex mutex;
    private CTPreferencesDialog preferencesDialog;

    private CT() {
        mutex = new FileMutex(CT.class.getSimpleName());
        if (checkRunning()) {
            throw new CTException(MSG_ALREADY_RUNNING);
        }
        CTPreferences preferences = new CTPreferencesImpl(CT.class);
        this.preferences = preferences;
        CTTimeHelper timeHelper = new CTTimeHelperImpl();
        this.timeHelper = timeHelper;
        List<CTBlocker> blockers = CTBlocker.forAllDevices(this.preferences);
        this.blockers = blockers;
        List<CTTaskUpdatable> updatables = new ArrayList<>(blockers);
        owner = new CTShowingFrame();
        CTControl control = new CTControl(this, owner);
        this.control = control;
        preferences.addPropertyChangeListener(control);
        updatables.add(control);
        updatables.add(new CTSoundUpdatable(preferences));
        timer = new CTTimer(timeHelper, updatables);
        versionInfo = new CTVersionInfoImpl();
    }

    private boolean checkRunning() {
        return !mutex.acquire();
    }

    public static void main(String[] args) {
        setSystemLookAndFeel();
        CTCmdImpl cmd = new CTCmdImpl(args);
        new CT().init(cmd.isDebug(), cmd.getFakeTime()).overridePreferences(cmd).start();
    }

    @NotNull
    private CT overridePreferences(@NotNull CTArgs cmd) {
        for (String name : preferences.getPropertyNames()) {
            if (cmd.has(name)) {
                CTPrefProperty<Object> property = preferences.getProperty(name);
                property.set(cmd.get(name, CTNumberUtil.getDefault(property.type())));
            }
        }
        return this;
    }

    @NotNull
    private CT init(boolean debug, long fakeTime) {
        if (debug) {
            Log.info(MSG_DEBUG_MODE);
        }
        if (fakeTime != 0) {
            setFakeTime(fakeTime);
            Log.info(MSG_FAKE_TIME_SET_0, fakeTime);
        }
        for (CTBlocker blocker : blockers) {
            blocker.debug(debug);
        }
        return this;
    }

    private void setFakeTime(long fakeTime) {
        timeHelper.setFakeTime(fakeTime);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (@NotNull ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                InstantiationException ex) {
            throw new CTException(MSG_CANNOT_SET_LOOK_AND_FEEL, ex);
        }
    }

    private void start() {
        CTPrefProperty<CTConfig> property = preferences.config();
        List<CTConfig> configs = loadConfigs(false).getConfigs();
        if (property.isPresent() && configs.contains(property.get())) {
            control.arm(property.get());
        } else {
            control.arm(configs.iterator().next());
        }
    }

    @Override
    public void about() {
        new CTAboutDialog(this, owner).showMe();
    }

    @Override
    public void arm(CTConfig config) {
        preferences.config().set(config);
        timer.arm(config, timeHelper.currentTimeMillis());
    }

    @Override
    public void browseConfigs() {
        if (configResource != null && Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            File file;
            try {
                String path = Objects.requireNonNull(configResource.toURL()).getPath();
                file = new File(path.startsWith(SLASH) ? path.substring(1) : path);
            } catch (IOException e) {
                throw new CTException(MSG_CANNOT_OPEN_CONFIG, e);
            }
            if (desktop.isSupported(Action.EDIT)) {
                try {
                    desktop.edit(file);
                    return;
                } catch (IOException e) {
                    Log.error(e, e.getLocalizedMessage());
                }
            }
            if (desktop.isSupported(Action.OPEN)) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    showError(e);
                }
            }
        }
    }

    private void showError(@NotNull Exception e) {
        Log.error(e, e.getMessage());
        JOptionPane.showMessageDialog(owner, e.getLocalizedMessage(), versionInfo.getProjectName(), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void exit() {
        for (CTBlocker blocker : blockers) {
            blocker.dispose();
        }
        timer.terminate();
        mutex.release();
        preferences.removePropertyChangeListener(control);
        CTPropertyChangeManager.getInstance().removePropertyChangeListeners();
    }

    @Override
    public void help() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            ClassLoader loader = CT.class.getClassLoader();
            Locale locale = Locale.getDefault();
            //noinspection StringConcatenation
            for (Resource resource : new Resource[]{new ClasspathResource(loader, HELP_PAGE).forLocale(locale),
                    new ClasspathResource(loader,
                            SLASH + HELP_PAGE).forLocale(locale)}) {
                if (resource.exists()) {
                    try (InputStream stream = resource.getInputStream()) {
                        File tempFile = File.createTempFile(CT.class.getName(), DOT + HTML);
                        Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Desktop.getDesktop().browse(tempFile.toURI());
                        tempFile.deleteOnExit();
                    } catch (IOException ex) {
                        Log.error(ex, PAGE_0_NOT_FOUND, HELP_PAGE);
                    }
                    return;
                } else {
                    //noinspection StringConcatenation
                    Log.debug(PAGE_0_NOT_FOUND, HELP_PAGE + ": " + resource.getLocation());
                }
            }
            Log.error(PAGE_0_NOT_FOUND, HELP_PAGE);
        }
    }

    @Override
    public CTConfigs loadConfigs(boolean reload) {
        CTConfigs configs;
        if (configResource == null || reload) {
            CTConfigResource configResource = CTConfigResource.findLocalConfig(CONFIG_NAME, false);
            if (configResource != null && configResource.exists()) {
                try (InputStream stream = configResource.getInputStream()) {
                    configs = configsConverter.parse(CTStreamUtil.convertStreamToString(stream, CONFIG_CHARSET));
                    if (configs.getConfigs().isEmpty()) {
                        configs = new CTStandardConfigs();
                    }
                } catch (IOException ex) {
                    Log.error(ex, MSG_CANNOT_LOAD_0, configResource);
                    configs = new CTStandardConfigs();
                }
            } else {
                if (configResource == null) {
                    Log.warn(NOT_FOUND_0, CONFIG_NAME);
                } else {
                    Log.warn(NOT_FOUND_0, configResource);
                }
                configs = new CTStandardConfigs();
                configResource = CTConfigResource.forURL(LocalResource.getHomeDirectoryLocation(), CONFIG_NAME);
                saveConfigs(configs, configResource);
            }
            this.configResource = configResource;
        } else {
            try (InputStream stream = configResource.getInputStream()) {
                configs = configsConverter.parse(CTStreamUtil.convertStreamToString(stream, CONFIG_CHARSET));
            } catch (IOException ex) {
                throw new CTException(MSG_CANNOT_READ_CONFIG, ex);
            }
        }
        return configs;
    }

    @Override
    public void saveConfigs(CTConfigs configs) {
        saveConfigs(configs, configResource);
    }

    @Nullable
    @Override
    public CTConfig showNewConfig() {
        try {
            return new CTNewConfigDialog(owner, preferences, this).showMe();
        } catch (IllegalArgumentException ex) {
            Log.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public void showPreferences() {
        if (preferencesDialog == null) {
            preferencesDialog = new CTPreferencesDialog(this, owner);
        }
        preferencesDialog.showMe();
    }

    @Override
    public void unarm() {
        timer.unarm();
    }

    @NotNull
    @Override
    public CTPreferences getPreferences() {
        return preferences;
    }

    @NotNull
    @Override
    public CTVersionInfo getVersionInfo() {
        return versionInfo;
    }

    private void saveConfigs(CTConfigs configs, @Nullable Resource configResource) {
        if (configResource != null) {
            try (OutputStream stream = configResource.getOutputStream()) {
                CTStreamUtil.write(stream, configsConverter.format(TimeUnit.MINUTES, configs), CONFIG_CHARSET);
            } catch (IOException ex) {
                Log.warn(NOT_FOUND_0, configResource);
            }
        }
    }

    @Override
    public URL getBigIcon() {
        String bw = preferences.iconStyle().get(CTPreferences.ICON_STYLE.BW).getPath();
        return CT.class.getResource(
                MessageFormat.format(URL_ICON_BIG_W, bw));
    }

    @Override
    public URL getMediumIcon() {
        String bw = preferences.iconStyle().get(CTPreferences.ICON_STYLE.BW).getPath();
        return CT.class.getResource(
                MessageFormat.format(URL_ICON_MEDIUM_W, bw));
    }

    @Override
    public URL getSmallIcon() {
        String bw = preferences.iconStyle().get(CTPreferences.ICON_STYLE.BW).getPath();
        return CT.class.getResource(
                MessageFormat.format(URL_ICON_SMALL_W, bw));
    }
}
