package gargoyle.ct.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

public class CTConfigs implements Serializable {
    private static final String MSG_NOT_VALID_CONVERT_0 = "not valid convert: {0}";
    private static final long serialVersionUID = 2024075953874239351L;
    private final Map<String, CTConfig> configs = new LinkedHashMap<>();

    public CTConfigs(@NotNull CTConfig... configs) {
        setConfigs(configs);
    }

    private void setConfigs(CTConfig[] configs) {
        synchronized (this.configs) {
            this.configs.clear();
            for (CTConfig config : configs) {
                addConfig(config);
            }
        }
    }

    public void addConfig(@NotNull CTConfig config) {
        synchronized (configs) {
            String name = config.getName();
            if (!configs.containsKey(name)) {
                configs.put(name, config);
            }
        }
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public CTConfigs(@NotNull List<CTConfig> configs) {
        setConfigs(configs);
    }

    public CTConfig getConfig(String name) {
        return configs.get(name);
    }

    @NotNull
    public List<CTConfig> getConfigs() {
        return Collections.unmodifiableList(new LinkedList<>(configs.values()));
    }

    private void setConfigs(Iterable<CTConfig> configs) {
        synchronized (this.configs) {
            this.configs.clear();
            for (CTConfig config : configs) {
                addConfig(config);
            }
        }
    }

    public boolean hasConfig(@NotNull CTConfig config) {
        return configs.containsKey(config.getName());
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + configs.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CTConfigs other = (CTConfigs) obj;
        return Objects.equals(configs, other.configs);
    }

    @NotNull
    @Override
    public String toString() {
        return MessageFormat.format("CTConfigs [configs={0}]", configs);
    }

    public void validate() throws InvalidObjectException {
        for (CTConfig config : configs.values()) {
            if (config == null || config.isNotValid()) {
                throw new InvalidObjectException(MessageFormat.format(MSG_NOT_VALID_CONVERT_0, config));
            }
        }
    }
}
