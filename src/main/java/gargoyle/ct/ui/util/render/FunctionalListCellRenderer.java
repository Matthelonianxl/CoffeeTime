package gargoyle.ct.ui.util.render;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class FunctionalListCellRenderer<E> implements ListCellRenderer<E> {
    private final Function<E, String> messageProvider;
    private final ListCellRenderer originalRenderer;

    public FunctionalListCellRenderer(final ListCellRenderer originalRenderer,
                                      final Function<E, String> messageProvider) {
        this.originalRenderer = originalRenderer;
        this.messageProvider = messageProvider;
    }

    public Component getListCellRendererComponent(
            JList<? extends E> list,
            E value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        String message = messageProvider.apply(value);
        //noinspection unchecked
        return originalRenderer.getListCellRendererComponent(list, message, index, isSelected, cellHasFocus);
    }
}
