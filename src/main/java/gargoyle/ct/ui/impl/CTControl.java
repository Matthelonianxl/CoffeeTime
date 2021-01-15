package gargoyle.ct.ui.impl;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.CTConfigs;
import gargoyle.ct.messages.impl.CTMessages;
import gargoyle.ct.messages.impl.CTPreferencesLocaleProvider;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.pref.CTPropertyChangeEvent;
import gargoyle.ct.pref.CTPropertyChangeListener;
import gargoyle.ct.task.CTTaskUpdatable;
import gargoyle.ct.task.impl.CTTask;
import gargoyle.ct.ui.CTApp;
import gargoyle.ct.ui.CTBlockerTextProvider;
import gargoyle.ct.ui.CTControlActions;
import gargoyle.ct.ui.CTControlWindow;
import gargoyle.ct.ui.impl.control.*;
import gargoyle.ct.util.Defend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CTControl implements CTControlActions, CTTaskUpdatable, CTPropertyChangeListener<Object> {
    private static final String LOC_MESSAGES = "messages.control";
    private static final String STR_ABOUT = "about";
    private static final String STR_BROWSE_CONFIGS = "browse-configs";
    private static final String STR_BROWSE_CONFIGS_TOOLTIP = "browse-configs.tooltip";
    private static final String STR_EXIT = "exit";
    private static final String STR_EXIT_TOOLTIP = "exit.tooltip";
    private static final String STR_HELP = "help";
    private static final String STR_HELP_TOOLTIP = "help.tooltip";
    private static final String STR_NEW_CONFIG = "new-config";
    private static final String STR_NEW_CONFIG_TOOLTIP = "new-config.tooltip";
    private static final String STR_PREFERENCES = "preferences";
    private static final String STR_PREFERENCES_TOOLTIP = "preferences.tooltip";
    private static final String STR_UNARM = "unarm";
    private static final String STR_UNARM_TOOLTIP = "unarm.tooltip";
    @NotNull
    private final CTControlActions app;
    @NotNull
    private final CTControlWindow controlWindow;
    @NotNull
    private final ButtonGroup group;
    @NotNull
    private final CTBlockerTextProvider textProvider;
    private JPopupMenu menu;
    private CTLocalizableMenuItem stopMenuItem;

    public CTControl(@NotNull CTApp app, Frame owner) {
        this.app = app;
        CTPreferences preferences = app.getPreferences();
        textProvider = new CTBlockerTextProvider(preferences);
        group = new ButtonGroup();
        controlWindow = new CTControlWindowImpl(owner, preferences, app,
                createMenu(new CTMessages(new CTPreferencesLocaleProvider(preferences),
                        LOC_MESSAGES), app.loadConfigs(false)));
        controlWindow.showMe();
    }

    private JPopupMenu createMenu(@NotNull CTMessages messages, @NotNull CTConfigs configs) {
        menu = new JPopupMenu();
        addConfigs(menu, configs, null);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(new CTLocalizableMenuItem(messages,
                new CTLocalizableAction(messages, STR_NEW_CONFIG, STR_NEW_CONFIG_TOOLTIP) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onNewConfig(configs, menu);
                    }
                }));
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        stopMenuItem = new CTLocalizableMenuItem(messages, new CTLocalizableAction(messages, STR_UNARM,
                STR_UNARM_TOOLTIP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                unarm();
            }
        });
        stopMenuItem.setEnabled(false);
        menu.add(stopMenuItem);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(new CTLocalizableMenuItem(messages,
                new CTLocalizableAction(messages, STR_BROWSE_CONFIGS, STR_BROWSE_CONFIGS_TOOLTIP) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        browseConfigs();
                    }
                }));
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(new CTLocalizableMenuItem(messages,
                new CTLocalizableAction(messages, STR_PREFERENCES, STR_PREFERENCES_TOOLTIP) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showPreferences();
                    }
                }));
        menu.add(new CTLocalizableMenuItem(messages, new CTLocalizableAction(messages, STR_HELP, STR_HELP_TOOLTIP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                help();
            }
        }));
        menu.add(new CTLocalizableMenuItem(messages, new CTLocalizableAction(messages, STR_ABOUT, "") {
            @Override
            public void actionPerformed(ActionEvent e) {
                about();
            }
        }));
        menu.add(new CTLocalizableMenuItem(messages, new CTLocalizableAction(messages, STR_EXIT, STR_EXIT_TOOLTIP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        }));
        return menu;
    }

    private void addConfigs(@NotNull JPopupMenu menu, @NotNull CTConfigs configs, @Nullable CTConfig toArm) {
        Defend.isTrue(toArm == null || configs.hasConfig(toArm), "invalid config");
        for (CTConfig config : configs.getConfigs()) {
            addConfig(menu, config);
        }
        if (toArm != null) {
            arm(toArm);
        }
    }

    private void addConfig(@NotNull JPopupMenu menu, @NotNull CTConfig config) {
        CTConfigMenuItem menuItem = new CTConfigMenuItem(new CTConfigAction(this, config));
        group.add(menuItem);
        menu.insert(menuItem, group.getButtonCount() - 1);
    }

    private void onNewConfig(@NotNull CTConfigs configs, @NotNull JPopupMenu menu) {
        CTConfig config = showNewConfig();
        if (config != null && config.isValid() && !configs.hasConfig(config)) {
            configs.addConfig(config);
            addConfig(menu, config);
            saveConfigs(configs);
        }
    }

    @Override
    public void about() {
        app.about();
    }

    @Override
    public void arm(CTConfig config) {
        AbstractButton item = findItem(config);
        if (item != null) {
            stopMenuItem.setEnabled(true);
            group.clearSelection();
            group.setSelected(item.getModel(), true);
            if (item.getAction() instanceof CTConfigAction) {
                app.arm(((CTConfigAction) item.getAction()).getConfig());
            }
        }
    }

    @Override
    public void browseConfigs() {
        app.browseConfigs();
    }

    @Override
    public void exit() {
        controlWindow.destroy();
        app.unarm();
        app.exit();
    }

    @Override
    public void help() {
        app.help();
    }

    @Override
    public CTConfigs loadConfigs(boolean reload) {
        CTConfigs configs = app.loadConfigs(reload);
        if (reload) {
            replaceConfigs(menu, configs);
        }
        return configs;
    }

    @Override
    public void saveConfigs(CTConfigs configs) {
        app.saveConfigs(configs);
    }

    @Override
    public CTConfig showNewConfig() {
        return app.showNewConfig();
    }

    @Override
    public void showPreferences() {
        app.showPreferences();
    }

    @Override
    public void unarm() {
        stopMenuItem.setEnabled(false);
        group.clearSelection();
        app.unarm();
    }

    private void replaceConfigs(@NotNull JPopupMenu menu, @NotNull CTConfigs configs) {
        CTConfig selectedConfig = getSelectedConfig();
        removeConfigs(menu);
        addConfigs(menu, configs, selectedConfig);
        // if (selectedConfig != null && findItem(selectedConfig) != null) {
        //     arm(selectedConfig);
        // }
    }

    @Nullable
    private CTConfig getSelectedConfig() {
        CTConfig selectedConfig = null;
        List<AbstractButton> elements = Collections.list(group.getElements());
        for (AbstractButton element : elements) {
            Defend.instanceOf(element, CTConfigMenuItem.class, "not a config added");
            if (element.getModel().isSelected()) {
                if (element.getAction() instanceof CTConfigAction) {
                    selectedConfig = ((CTConfigAction) element.getAction()).getConfig();
                }
            }
        }
        return selectedConfig;
    }

    private void removeConfigs(@NotNull JPopupMenu menu) {
        List<AbstractButton> elements = Collections.list(group.getElements());
        for (AbstractButton element : elements) {
            Defend.instanceOf(element, CTConfigMenuItem.class, "not a config to remove");
            group.remove(element);
            menu.remove(element);
        }
    }

    @Nullable
    private AbstractButton findItem(CTConfig config) {
        for (AbstractButton button : Collections.list(group.getElements())) {
            Action action = button.getAction();
            if (action instanceof CTConfigAction) {
                if (Objects.equals(((CTConfigAction) action).getConfig(), config)) {
                    return button;
                }
            }
        }
        return null;
    }

    @Override
    public void doUpdate(@NotNull CTTask task, long currentMillis) {
        controlWindow.setToolTipText(textProvider.getToolTipText(task, currentMillis));
//        lblInfo.setText(textProvider.getInfoText(task, currentMillis));
        boolean visible = textProvider.isVisible(task, currentMillis);
        controlWindow.setTextMode(visible);
        if (visible) {
            controlWindow.showText(textProvider.getColor(task, currentMillis),
                    textProvider.getBlockerText(task, currentMillis, false));
        }
    }

    @Override
    public void onPropertyChange(CTPropertyChangeEvent<Object> event) {
        controlWindow.onPropertyChange(event);
    }
}
