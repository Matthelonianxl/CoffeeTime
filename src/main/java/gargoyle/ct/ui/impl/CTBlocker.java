package gargoyle.ct.ui.impl;

import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.task.CTTaskUpdatable;
import gargoyle.ct.task.impl.CTTask;
import gargoyle.ct.ui.CTBlockerTextProvider;
import gargoyle.ct.ui.CTInformer;
import gargoyle.ct.ui.CTWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CTBlocker extends JWindow implements CTTaskUpdatable, CTWindow, CTInformer {
    private static final long serialVersionUID = 4716380852101644265L;
    @NotNull
    private final CTBlockerContent content;
    @NotNull
    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private final transient CTPreferences preferences;
    private transient CTBlockerTextProvider textProvider;

    private CTBlocker(@NotNull CTPreferences preferences, GraphicsDevice device) {
        this.preferences = preferences;
        textProvider = new CTBlockerTextProvider(preferences);
        setBounds(device.getDefaultConfiguration().getBounds());
        setAlwaysOnTop(true);
        toFront();
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        content = new CTBlockerContent(preferences, true);
        container.add(content, BorderLayout.CENTER);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(@NotNull WindowEvent e) {
                holdFocus(e);
            }
        });
    }

    void holdFocus(@NotNull WindowEvent event) {
        if (isVisible()) {
            event.getWindow().requestFocus();
        }
    }

    public static List<CTBlocker> forAllDevices(@NotNull CTPreferences preferences) {
        List<CTBlocker> devices = new ArrayList<>();
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            devices.add(new CTBlocker(preferences, device));
        }
        return Collections.unmodifiableList(devices);
    }

    public static CTBlocker forDefaultDevice(@NotNull CTPreferences preferences) {
        return new CTBlocker(preferences, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
    }

    public static CTBlocker forDevice(@NotNull CTPreferences preferences, @NotNull GraphicsDevice device) {
        return new CTBlocker(preferences, device);
    }

    public void debug(boolean debug) {
        setAlwaysOnTop(!debug);
        MouseListener clickToDestroy = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                destroy();
            }
        };
        removeMouseListener(clickToDestroy);
        if (debug) {
            addMouseListener(clickToDestroy);
        }
    }

    @Override
    public void destroy() {
        content.destroy();
        setVisible(false);
        dispose();
    }

    @Override
    public void showMe() {
        content.showMe();
        setVisible(true);
        toFront();
    }

    @Override
    public void doUpdate(@NotNull CTTask task, long currentMillis) {
        content.doUpdate(task, currentMillis);
        boolean block = preferences.block().get();
        boolean visible = block && textProvider.isVisible(task, currentMillis);
        setVisible(visible);
        content.setVisible(visible);
        if (visible) {
            showText(textProvider.getColor(task, currentMillis),
                    textProvider.getBlockerText(task, currentMillis, true));
        }
    }

    @Override
    public void showText(Color foreground, String text) {
        content.showText(foreground, text);
        setForeground(foreground);
        if (isVisible()) {
            repaint();
            toFront();
        } else {
            showMe();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        textProvider = new CTBlockerTextProvider(preferences);
    }

    @Override
    public void setBackground(Color bgColor) {
        content.setBackground(bgColor);
    }
}
