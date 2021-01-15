package gargoyle.ct.ui.impl.control;

import gargoyle.ct.messages.LocaleProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class CTLocalizableMenuItem extends JMenuItem {
    private static final long serialVersionUID = -3628913101849581469L;

    public CTLocalizableMenuItem(LocaleProvider provider, @NotNull CTLocalizableAction action) {
        super(action);
        provider.locale().addPropertyChangeListener(event -> update(action));
        action.init(this);
    }

    private void update(CTLocalizableAction action) {
        setText(action.getLocalizedText());
        setToolTipText(action.getToolTipLocalizedText());
    }
}
