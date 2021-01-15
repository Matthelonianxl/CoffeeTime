package gargoyle.ct.ui.util.render;

import javax.swing.*;

public final class ToStringListCellRenderer<E> extends FunctionalListCellRenderer<E> {
    public ToStringListCellRenderer(final ListCellRenderer originalRenderer) {
        super(originalRenderer, e -> e == null ? "" : String.valueOf(e));
    }
}
