package gargoyle.ct.config;

import gargoyle.ct.util.CTTimeUtil;
import gargoyle.ct.util.Defend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CTConfig implements Serializable, ObjectInputValidation {
    private static final String FORMAT_NAME = "{0,number,00}/{1,number,00}";
    private static final String MSG_NOT_VALID = "convert is not valid";
    private static final String STR_INVALID = "invalid";
    private static final long serialVersionUID = -898699928298432564L;
    private final long block;
    private String name;
    private final long warn;
    private final long whole;

    public CTConfig() {
        Defend.isTrue(isValid(0L, 0L, 0L), MSG_NOT_VALID);
        whole = 0L;
        block = 0L;
        warn = 0L;
        name = STR_INVALID;
    }

    public long getWhole(@NotNull TimeUnit unit) {
        return CTTimeUtil.fromMillis(unit, whole);
    }

    public CTConfig(long whole, long block, long warn) {
        Defend.isTrue(isValid(whole, block, warn), MSG_NOT_VALID);
        this.whole = whole;
        this.block = block;
        this.warn = warn;
        name = name(TimeUnit.MINUTES, whole, block);
    }

    public CTConfig(@NotNull TimeUnit unit, long whole, long block, long warn) {
        this(CTTimeUtil.toMillis(unit, whole), CTTimeUtil.toMillis(unit, block), CTTimeUtil.toMillis(unit, warn));
        name = name(unit, CTTimeUtil.toMillis(unit, whole), CTTimeUtil.toMillis(unit, block));
    }

    @SuppressWarnings({"MethodMayBeStatic", "WeakerAccess"})
    protected boolean isValid(long wholeMillis, long blockMillis, long warnMillis) {
        return wholeMillis > blockMillis && blockMillis > warnMillis;
    }

    private static String name(@NotNull TimeUnit unit, long whole, long block) {
        return MessageFormat.format(FORMAT_NAME,
                CTTimeUtil.fromMillis(unit, whole),
                CTTimeUtil.fromMillis(unit, block));
    }

    public long getBlock(@NotNull TimeUnit unit) {
        return CTTimeUtil.fromMillis(unit, block);
    }

    public long getBlock() {
        return block;
    }

    public long getWhole() {
        return whole;
    }

    public String getName() {
        return name;
    }

    public long getWarn() {
        return warn;
    }

    public long getWarn(@NotNull TimeUnit unit) {
        return CTTimeUtil.fromMillis(unit, warn);
    }

    public boolean isValid() {
        return isValid(whole, block, warn);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (int) (block ^ block >>> 32);
        result = prime * result + (int) (warn ^ warn >>> 32);
        result = prime * result + (int) (whole ^ whole >>> 32);
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
        CTConfig other = (CTConfig) obj;
        return block == other.block && warn == other.warn && whole == other.whole;
    }

    @NotNull
    @Override
    public String toString() {
        return MessageFormat.format("CTConfig [name={0}, whole={1}, block={2}, warn={3}]", name, whole, block, warn);
    }

    @NotNull
    public String name(@NotNull TimeUnit unit) {
        return name(unit, whole, block);
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        if (isNotValid()) {
            throw new InvalidObjectException("invalid configuration");
        }
    }

    public boolean isNotValid() {
        return !isValid(whole, block, warn);
    }
}
