package gargoyle.ct.prop.impl.simple;

import gargoyle.ct.prop.impl.CTBaseObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Objects;

public abstract class CTSimpleProperty<T> extends CTBaseObservableProperty<T> {
    private T value;

    @SuppressWarnings("unchecked")
    protected CTSimpleProperty(String name, T def) {
        super((Class<T>) def.getClass(), name);
        value = def;
    }

    protected CTSimpleProperty(Class<T> type, String name) {
        super(type, name);
    }

    @Override
    public void set(T value) {
        T oldValue = get();
        if (Objects.equals(oldValue, value)) {
            return;
        }
        this.value = value;
        Thread thread = firePropertyChange(value, oldValue);
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public T get() {
        return value;
    }

    @NotNull
    @Override
    public String toString() {
        return MessageFormat.format("CTSimpleProperty'{'name=''{0}'', value={1}'}'", name, value);
    }
}
