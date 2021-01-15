package gargoyle.ct.ui.impl;

import gargoyle.ct.messages.LocaleProvider;
import gargoyle.ct.messages.MessageProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CTLocalizableLabel extends JLabel {
    private static final long serialVersionUID = 1215028617118416457L;

    public CTLocalizableLabel(@NotNull MessageProvider messages, LocaleProvider provider, String textKey, String toolTipTextKey,
                              int alignment) {
        super(textKey, alignment);
        provider.locale().addPropertyChangeListener(event -> update(messages, textKey, toolTipTextKey));
        update(messages, textKey, toolTipTextKey);
        setIcon(null);
        setHorizontalAlignment(alignment);
    }

    private void update(MessageProvider messages, String textKey, String toolTipTextKey) {
        setText(messages.getMessage(textKey));
        setToolTipText(messages.getMessage(toolTipTextKey));
    }
}
