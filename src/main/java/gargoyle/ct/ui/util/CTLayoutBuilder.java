package gargoyle.ct.ui.util;

import gargoyle.ct.messages.Described;
import gargoyle.ct.messages.LocaleProvider;
import gargoyle.ct.messages.MessageProvider;
import gargoyle.ct.messages.MessageProviderEx;
import gargoyle.ct.pref.CTPreferences.SUPPORTED_LOCALES;
import gargoyle.ct.pref.impl.prop.CTPrefProperty;
import gargoyle.ct.prop.CTNumberProperty;
import gargoyle.ct.prop.CTObservableProperty;
import gargoyle.ct.prop.CTProperty;
import gargoyle.ct.ui.impl.CTLocalizableLabel;
import gargoyle.ct.ui.util.render.MessageProviderListCellRenderer;
import gargoyle.ct.util.CTNumberUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

@SuppressWarnings("MethodMayBeStatic")
public class CTLayoutBuilder {
    private static final int GAP = 5;
    @NotNull
    private final GridBagConstraints controlConstraints;
    @NotNull
    private final GridBagConstraints labelConstraints;
    @NotNull
    private final GridBagLayout layout;
    @NotNull
    private final Container pane;

    public CTLayoutBuilder(Container pane) {
        layout = new GridBagLayout();
        pane.setLayout(layout);
        this.pane = pane;
        labelConstraints = createConstraints(0.0, 1);
        controlConstraints = createConstraints(1.0, GridBagConstraints.REMAINDER);
    }

    @NotNull
    private static GridBagConstraints createConstraints(double weightX, int gridWidth) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = weightX;
        constraints.gridwidth = gridWidth;
        constraints.insets = new Insets(GAP, GAP, GAP, GAP);
        return constraints;
    }

    public void addLabeledControl(JLabel label, JComponent control) {
        layout.setConstraints(label, labelConstraints);
        pane.add(label);
        layout.setConstraints(control, controlConstraints);
        pane.add(control);
    }

    public void build() {
        pane.validate();
    }

    @NotNull
    public JCheckBox createCheckBox(@NotNull CTProperty<Boolean> property) {
        JCheckBox control = new JCheckBox();
        control.setSelected(property.get());
        control.addActionListener(event -> property.set(control.isSelected()));
        return control;
    }

    @SuppressWarnings({"unchecked", "TypeMayBeWeakened", "SameParameterValue"})
    public <E extends Enum<E> & Described> JComboBox<E> createComboBox(@NotNull MessageProvider messages, @NotNull Class<E> type, @NotNull CTPrefProperty<E> property,
                                                                       boolean allowNull) {
        E[] enumConstants = type.getEnumConstants();
        JComboBox<E> control;
        if (allowNull) {
            Vector<E> list = new Vector<>(Arrays.asList(enumConstants));
            list.add(0, null);
            control = new JComboBox<>(list);
        } else {
            control = new JComboBox<>(enumConstants);
        }
        control.setSelectedItem(property.get());
        control.addActionListener(event -> property.set((E) control.getSelectedItem()));
        control.setRenderer(new MessageProviderListCellRenderer<>(control.getRenderer(), messages));
        return control;
    }

    @NotNull
    public JLabel createLocalizableLabel(@NotNull MessageProvider messages, @NotNull LocaleProvider provider, String textKey,
                                         String toolTipTextKey) {
        return new CTLocalizableLabel(messages, provider, textKey, toolTipTextKey, SwingConstants.TRAILING);
    }

    @NotNull
    public JLabel createLocalizableLabel(@NotNull MessageProviderEx messages, String textKey, String toolTipTextKey) {
        return new CTLocalizableLabel(messages, messages, textKey, toolTipTextKey, SwingConstants.TRAILING);
    }

    @NotNull
    @SuppressWarnings("UseOfPropertiesAsHashtable")
    private static <T extends Enum<T>> Dictionary getLabels(Class<T> type) {
        Properties properties = new Properties();
        T[] enumConstants = type.getEnumConstants();
        int length = enumConstants.length;
        for (int i = 0; i < length; i++) {
            T value = enumConstants[i];
            properties.put(i, new JLabel(String.valueOf(value)));
        }
        return properties;
    }

    public <T extends Enum<T>> JSlider createSlider(@NotNull Class<T> type, @NotNull CTProperty<T> property, boolean allowNull) {
        T[] enumConstants = type.getEnumConstants();
        JSlider control;
        if (allowNull) {
            Collection<T> list = new ArrayList<>(Arrays.asList(enumConstants));
//            list.add(0, null);
            control = new JSlider(new DefaultBoundedRangeModel(toIndex(property.get()), 1, 1, list.size()));
        } else {
            control = new JSlider(new DefaultBoundedRangeModel(toIndex(property.get()),
                    0,
                    0,
                    enumConstants.length - 1));
        }
        control.setValue(toIndex(property.get()));
        control.addChangeListener(event -> property.set(fromIndex(type, control.getValue())));
        control.setLabelTable(getLabels(type));
        control.setSnapToTicks(true);
        control.setPaintLabels(true);
        control.setPaintTicks(true);
        control.setPaintTrack(true);
        return control;
    }

    @NotNull
    public JSlider createSlider(@NotNull CTProperty<Integer> property, Integer min, Integer max) {
        JSlider control = new JSlider(CTNumberUtil.toInt(min), CTNumberUtil.toInt(max));
        control.setValue(property.get());
        control.addChangeListener(event -> property.set(CTNumberUtil.toRange((Integer) control.getValue(), min, max)));
        return control;
    }

    private static <T extends Enum<T>> int toIndex(T value) {
        return value.ordinal();
    }

    private static <T extends Enum<T>> T fromIndex(Class<T> type, int index) {
        return type.getEnumConstants()[index];
    }

    @NotNull
    public <T extends Number> JSlider createSlider(@NotNull Class<T> type, @NotNull CTNumberProperty<T> property, T min, T max) {
        JSlider control = new JSlider(CTNumberUtil.toInt(min), CTNumberUtil.toInt(max));
        control.setValue(property.get().intValue());
        control.addChangeListener(event -> property.set(CTNumberUtil.fromInt(type,
                CTNumberUtil.toRange(min,
                        max,
                        control.getValue())
        )));
        return control;
    }

    @NotNull
    public JSpinner createSpinner(@NotNull CTProperty<Integer> property, Integer min, Integer max) {
        JSpinner control = new JSpinner(new SpinnerNumberModel());
        control.setValue(property.get());
        control.addChangeListener(event -> {
            Object value = control.getValue();
            property.set(CTNumberUtil.toRange(CTNumberUtil.getInteger(value), min, max));
        });
        return control;
    }

    @NotNull
    public <T extends Number> JSpinner createSpinner(@NotNull Class<T> type, @NotNull CTProperty<T> property, T min, T max) {
        JSpinner control = new JSpinner(new SpinnerNumberModel());
        control.setValue(property.get().intValue());
        control.addChangeListener(event -> property.set(CTNumberUtil.fromInt(type,
                CTNumberUtil.toRange(min, max, CTNumberUtil.toInt((Number) control.getValue())
                ))));
        return control;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> JSpinner createSpinner(@NotNull Class<T> type, @NotNull CTProperty<T> property, boolean allowNull) {
        T[] enumConstants = type.getEnumConstants();
        JSpinner control;
        if (allowNull) {
            //noinspection UseOfObsoleteCollectionType
            Vector<T> list = new Vector<>(Arrays.asList(enumConstants));
            list.add(0, null);
            control = new JSpinner(new SpinnerListModel(list));
        } else {
            control = new JSpinner(new SpinnerListModel(enumConstants));
        }
        control.setValue(property.get());
        control.addChangeListener(event -> property.set((T) control.getValue()));
        return control;
    }

    @NotNull
    public JTextArea createTextArea(@NotNull CTProperty<String> property) {
        JTextArea control = new JTextArea();
        control.setText(property.get());
        control.addKeyListener(new PropertyKeyAdapter(property, control));
        return control;
    }

    @NotNull
    public JTextField createTextField(@NotNull CTProperty<String> property) {
        JTextField control = new JTextField();
        control.setText(property.get());
        control.addKeyListener(new PropertyKeyAdapter(property, control));
        return control;
    }

    @NotNull
    public JToggleButton createToggleButton(@NotNull CTProperty<Boolean> property,
                                            @NotNull CTObservableProperty<SUPPORTED_LOCALES> localeProperty) {
        JToggleButton control = new JToggleButton();
        control.setSelected(property.get());
        Locale initialLocale = localeProperty.get().getLocale();
        control.setText(control.isSelected() ? getYesString(initialLocale) : getNoString(initialLocale));
        control.addActionListener(event -> {
            Locale currentLocale = localeProperty.get().getLocale();
            control.setText(control.isSelected() ? getYesString(currentLocale) : getNoString(currentLocale));
            property.set(control.isSelected());
        });
        return control;
    }

    private static String getNoString(Locale locale) {
        return UIManager.getString("OptionPane.noButtonText", locale);
    }

    private static String getYesString(Locale locale) {
        return UIManager.getString("OptionPane.yesButtonText", locale);
    }

    private static class PropertyKeyAdapter extends KeyAdapter {
        private final JTextComponent control;
        private final CTProperty<String> property;

        public PropertyKeyAdapter(CTProperty<String> property, JTextComponent control) {
            this.property = property;
            this.control = control;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            property.set(control.getText());
        }
    }
}
