package gargoyle.ct.ui.impl.control;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"ConstructorNotProtectedInAbstractClass", "WeakerAccess"})
public abstract class CTAction implements Action {
    private static final String ENABLED = "enabled";
    private final Map<String, Object> values = new HashMap<>();
    private SwingPropertyChangeSupport changeSupport;

    public CTAction(String text) {
        setText(text);
        setToolTipText(text);
        setEnabled(true);
    }

    public CTAction(String text, Icon icon) {
        setText(text);
        setToolTipText(text);
        setIcon(icon);
        setEnabled(true);
    }

    public CTAction(String text, String tooltipText, Icon icon) {
        setText(text);
        setToolTipText(tooltipText);
        setIcon(icon);
        setEnabled(true);
    }

    public CTAction(String text, String tooltipText) {
        setText(text);
        setToolTipText(tooltipText);
        setEnabled(true);
    }

    protected CTAction() {
        setEnabled(true);
    }

    public void init(@NotNull AbstractButton menuItem) {
        menuItem.setAction(this);
        menuItem.setText(getText());
        menuItem.setToolTipText(getToolTipText());
        Icon icon = getIcon();
        if (icon != null) {
            menuItem.setIcon(icon);
        }
    }

    protected final String getText() {
        return String.valueOf(getValue(Action.NAME));
    }

    protected final void setText(String text) {
        putValue(Action.NAME, text);
    }

    protected final String getToolTipText() {
        return String.valueOf(getValue(Action.SHORT_DESCRIPTION));
    }

    protected final void setToolTipText(String text) {
        putValue(Action.SHORT_DESCRIPTION, text);
    }

    @Nullable
    protected final Icon getIcon() {
        return (Icon) getValue(Action.SMALL_ICON);
    }

    protected final void setIcon(Icon icon) {
        putValue(Action.SMALL_ICON, icon);
    }

    @Nullable
    @Override
    public final Object getValue(String key) {
        return values.get(key);
    }

    @Override
    public final void putValue(String key, Object value) {
        Object oldValue = getValue(key);
        values.put(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public final boolean isEnabled() {
        Object value = getValue(ENABLED);
        return value == null ? false : (Boolean) value;
    }

    @Override
    public final void setEnabled(boolean b) {
        putValue(ENABLED, b);
    }

    @SuppressWarnings("InstanceVariableUsedBeforeInitialized")
    protected final synchronized void firePropertyChange(String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        if (changeSupport == null || oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public final synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public final synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }
}
