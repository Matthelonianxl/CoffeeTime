package gargoyle.ct.prop.impl.simple;

import gargoyle.ct.prop.CTNumberProperty;

public class CTSimpleDoubleProperty extends CTSimpleProperty<Double> implements CTNumberProperty<Double> {
    public CTSimpleDoubleProperty(String name) {
        this(name, 0);
    }

    public CTSimpleDoubleProperty(String name, double def) {
        super(name, def);
    }
}
