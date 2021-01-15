package gargoyle.ct.ui.impl.control;

import javax.swing.*;

public final class CTConfigMenuItem extends JCheckBoxMenuItem {
    private static final long serialVersionUID = -2199156620390967976L;

    @SuppressWarnings("TypeMayBeWeakened")
    public CTConfigMenuItem(CTConfigAction action) {
        super(action);
    }
}
