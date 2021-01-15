package gargoyle.ct.ui.impl;

import gargoyle.ct.messages.MessageProviderEx;
import gargoyle.ct.messages.impl.CTMessages;
import gargoyle.ct.messages.impl.CTPreferencesLocaleProvider;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.pref.CTPreferences.SUPPORTED_LOCALES;
import gargoyle.ct.prop.CTProperty;
import gargoyle.ct.ui.CTApp;
import gargoyle.ct.ui.CTDialog;
import gargoyle.ct.ui.util.CTDragHelper;
import gargoyle.ct.ui.util.CTLayoutBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CTPreferencesDialog extends JDialog implements CTDialog<Void> {
    private static final String LOC_MESSAGES = "messages.preferences";
    private static final String STR_BLOCK = CTPreferences.PREF_BLOCK;
    private static final String STR_BLOCK_TOOLTIP = "block.tooltip";
    private static final String STR_ICON_STYLE = CTPreferences.PREF_ICON_STYLE;
    private static final String STR_SOUND = CTPreferences.PREF_SOUND;
    private static final String STR_SOUND_TOOLTIP = "sound.tooltip";
    private static final String STR_ICON_STYLE_TOOLTIP = "icon-style.tooltip";
    private static final String STR_SUPPORTED_LOCALES = "supported-locales";
    private static final String STR_SUPPORTED_LOCALES_TOOLTIP = "supported-locales.tooltip";
    private static final String STR_TITLE = "preferences-title";
    private static final String STR_TRANSPARENCY = CTPreferences.PREF_TRANSPARENCY;
    private static final String STR_TRANSPARENCY_LEVEL = CTPreferences.PREF_TRANSPARENCY_LEVEL;
    private static final String STR_TRANSPARENCY_LEVEL_TOOLTIP = "transparency-level.tooltip";
    private static final String STR_TRANSPARENCY_TOOLTIP = "transparency.tooltip";
    private static final long serialVersionUID = 4767295798528273381L;

    public CTPreferencesDialog(CTApp app, Window owner) {
        super(owner, ModalityType.MODELESS);
        CTPreferences preferences = app.getPreferences();
        setIconImage(new ImageIcon(app.getSmallIcon()).getImage());
        init(new CTMessages(new CTPreferencesLocaleProvider(preferences), LOC_MESSAGES), preferences, getContentPane());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private void init(MessageProviderEx messages, CTPreferences preferences, @NotNull Container pane) {
        CTLayoutBuilder layoutBuilder = new CTLayoutBuilder(pane);
        setTitle(messages.getMessage(STR_TITLE));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages, STR_BLOCK, STR_BLOCK_TOOLTIP),
                layoutBuilder.createCheckBox(preferences.block()));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages, STR_SOUND, STR_SOUND_TOOLTIP),
                layoutBuilder.createCheckBox(preferences.sound()));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages,
                STR_TRANSPARENCY,
                STR_TRANSPARENCY_TOOLTIP),
                layoutBuilder.createCheckBox(preferences.transparency()));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages,
                STR_TRANSPARENCY_LEVEL,
                STR_TRANSPARENCY_LEVEL_TOOLTIP),
                createTransparencyLevelControl(layoutBuilder, preferences.transparencyLevel()));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages,
                STR_ICON_STYLE,
                STR_ICON_STYLE_TOOLTIP),
                layoutBuilder.createComboBox(messages, CTPreferences.ICON_STYLE.class,
                        preferences.iconStyle(),
                        false));
        layoutBuilder.addLabeledControl(layoutBuilder.createLocalizableLabel(messages,
                STR_SUPPORTED_LOCALES,
                STR_SUPPORTED_LOCALES_TOOLTIP),
                layoutBuilder.createComboBox(messages, SUPPORTED_LOCALES.class,
                        preferences.supportedLocales(),
                        false));
        layoutBuilder.build();
    }

    @NotNull
    private static JSlider createTransparencyLevelControl(CTLayoutBuilder layoutBuilder, @NotNull CTProperty<Integer> property) {
        int maxOpacity = CTPreferences.OPACITY_PERCENT;
        JSlider control = layoutBuilder.createSlider(property, 0, maxOpacity);
        control.setExtent(maxOpacity / 10);
        control.setPaintLabels(true);
        control.setPaintTicks(true);
        control.setMajorTickSpacing(maxOpacity / 5);
        control.setMinorTickSpacing(maxOpacity / 10);
        return control;
    }

    @Nullable
    @Override
    public Void showMe() {
        setVisible(true);
        CTDragHelper.setLocationRelativeTo(this, getOwner());
        requestFocus();
        return null;
    }
}
