package gargoyle.ct.ui.impl.control;

import javax.swing.*;

public final class CTMenuItem extends JMenuItem {
    private static final long serialVersionUID = -5435250463745762683L;

    public CTMenuItem(CTAction action) {
        action.init(this);
    }
}
