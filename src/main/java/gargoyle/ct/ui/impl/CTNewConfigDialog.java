package gargoyle.ct.ui.impl;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.convert.CTUnitConverter;
import gargoyle.ct.config.convert.impl.CTConfigConverter;
import gargoyle.ct.log.Log;
import gargoyle.ct.messages.impl.CTMessages;
import gargoyle.ct.messages.impl.CTPreferencesLocaleProvider;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.ui.CTDialog;
import gargoyle.ct.ui.CTIconProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class CTNewConfigDialog implements CTDialog<CTConfig> {
    private static final String LOC_NEW_CONFIG = "messages/new_config";
    private static final String STR_CANCEL = "cancel";
    private static final String STR_CONFIG_PATTERN = "##U/##U/##U";
    private static final String STR_NEW_CONFIG_TOOLTIP = "new-config-field.tooltip";
    private static final String STR_OK = "ok";
    private static final String STR_TITLE = "new-config-title";
    private final CTUnitConverter<CTConfig> configConverter = new CTConfigConverter();
    @NotNull
    private final CTMessages messages;
    private final Component owner;
    private Icon icon;

    public CTNewConfigDialog(Component owner, @NotNull CTPreferences preferences, @NotNull CTIconProvider iconProvider) {
        this.owner = owner;
        messages = new CTMessages(new CTPreferencesLocaleProvider(preferences), LOC_NEW_CONFIG);
        updateIcon(iconProvider);
        preferences.iconStyle().addPropertyChangeListener(event -> updateIcon(iconProvider));
    }

    void updateIcon(@NotNull CTIconProvider iconProvider) {
        icon = new ImageIcon(iconProvider.getMediumIcon());
    }

    @Nullable
    @Override
    public CTConfig showMe() {
        while (true) {
            JFormattedTextField field;
            try {
                field = new JFormattedTextField(new MaskFormatter(STR_CONFIG_PATTERN));
            } catch (ParseException e) {
                throw new IllegalArgumentException(STR_CONFIG_PATTERN, e);
            }
            field.setToolTipText(messages.getMessage(STR_NEW_CONFIG_TOOLTIP));
            int result = JOptionPane.showOptionDialog(owner,
                    field,
                    messages.getMessage(STR_TITLE),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icon,
                    new Object[]{messages.getMessage(STR_OK),
                            messages.getMessage(STR_CANCEL)},
                    null);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    return configConverter.parse(String.valueOf(field.getValue()));
                } catch (IllegalArgumentException ex) {
                    Log.debug(ex, ex.getMessage());
                }
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.NO_OPTION) {
                return null;
            }
        }
    }
}
