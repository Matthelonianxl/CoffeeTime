package gargoyle.ct.ui.util.render;

import gargoyle.ct.messages.Described;
import gargoyle.ct.messages.MessageProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class MessageProviderListCellRenderer<E extends Described> extends FunctionalListCellRenderer<E> {
    public MessageProviderListCellRenderer(final ListCellRenderer originalRenderer,
                                           @NotNull final MessageProvider messageProvider) {
        super(originalRenderer, e -> {
            try {
                return e == null ? "" : messageProvider.getMessage(e.getDescription());
            } catch (RuntimeException e1) {
                return e.getDescription();
            }
        });
    }
}
