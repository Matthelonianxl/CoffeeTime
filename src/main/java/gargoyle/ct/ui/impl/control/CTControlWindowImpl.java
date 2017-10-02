package gargoyle.ct.ui.impl.control;

import gargoyle.ct.log.Log;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.pref.CTPropertyChangeEvent;
import gargoyle.ct.ui.CTControlWindow;
import gargoyle.ct.ui.CTIconProvider;
import gargoyle.ct.ui.impl.CTBlockerContent;
import gargoyle.ct.ui.util.CTDragHelper;
import gargoyle.ct.util.CTTimeUtil;

import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;

public final class CTControlWindowImpl extends JWindow implements CTControlWindow {
    private static final String MSG_TOOLTIP_ERROR = "tooltip error";
    private static final String MSG_TRANSPARENCY_NOT_SUPPORTED = "transparency not supported";
    private static final int SNAP = 20;
    private static final int TOOLTIP_OFFSET = 30;
    private static final String TOOL_TIP_MANAGER_ENABLE_TOOL_TIP_MODE = "ToolTipManager.enableToolTipMode";
    private static final long serialVersionUID = 6345130901927558555L;
    private final CTIconContent iconContent;
    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private final transient CTPreferences preferences;
    private final CTBlockerContent textContent;
    private boolean iconMode = true;
    private volatile boolean live = true;
    private volatile boolean reshow;

    public CTControlWindowImpl(Frame owner, CTPreferences preferences, CTIconProvider iconProvider, JPopupMenu menu) {
        super(owner);
        this.preferences = preferences;
        UIManager.getDefaults().put(TOOL_TIP_MANAGER_ENABLE_TOOL_TIP_MODE, "");
        setAlwaysOnTop(true);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        textContent = new CTBlockerContent(preferences, false);
        iconContent = new CTIconContent(preferences, iconProvider);
        showIconContent();
        pack();
        setComponentPopupMenu(menu);
        Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        setLocation(screenSize.width - getWidth(), screenSize.height - getHeight());
        getOwner().setLocation(getLocation());
        CTDragHelper.makeDraggable(textContent, SNAP);
        CTDragHelper.makeDraggable(iconContent, SNAP);
        CTDragHelper.makeDraggable(this, SNAP);
        addComponentListener(new OwnerUpdater(owner));
        initToolTip();
        MouseListener updater = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                onMouseMoved(true, false);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                onMouseMoved(false, true);
            }
        };
        textContent.addMouseListener(updater);
        iconContent.addMouseListener(updater);
    }

    @SuppressWarnings("MethodMayBeStatic")
    private void initToolTip() {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setDismissDelay(1000);
        toolTipManager.setInitialDelay(100);
        toolTipManager.setReshowDelay(100);
        toolTipManager.setEnabled(true);
        toolTipManager.setLightWeightPopupEnabled(true);
    }

    private void showIconContent() {
        Container pane = getContentPane();
        pane.remove(textContent);
        pane.add(iconContent, BorderLayout.CENTER);
        iconContent.repaint();
        transparency(true);
    }

    private void transparency(boolean transparent) {
        CTPreferences preferences = this.preferences;
        try {
            int oldOpacity = (int) Math.round((double) getOpacity() * CTPreferences.OPACITY_PERCENT);
            int newOpacity = iconMode && preferences.transparency().get() && transparent ?
                    preferences.transparencyLevel().get() :
                    CTPreferences.OPACITY_PERCENT;
            if (oldOpacity == newOpacity) {
                return;
            }
            setOpacity((float) (newOpacity / (double) CTPreferences.OPACITY_PERCENT));
        } catch (UnsupportedOperationException ex) {
            Log.warn(ex, MSG_TRANSPARENCY_NOT_SUPPORTED);
        }
    }

    @SuppressWarnings("WeakerAccess")
    void onMouseMoved(boolean reshow, boolean transparency) {
        this.reshow = reshow;
        transparency(transparency);
    }

    private void setComponentPopupMenu(JPopupMenu menu) {
        textContent.setComponentPopupMenu(menu);
        iconContent.setComponentPopupMenu(menu);
    }

    @Override
    public void destroy() {
        setVisible(false);
        if (live) {
            live = false;
            dispose();
            getOwner().dispose();
            live = true;
        }
    }

    @Override
    public void showMe() {
        setVisible(true);
    }

    @Override
    public void onPropertyChange(CTPropertyChangeEvent event) {
        String key = event.getName();
        if (CTPreferences.PREF_TRANSPARENCY.equals(key) || CTPreferences.PREF_TRANSPARENCY_LEVEL.equals(key)) {
            transparency(true);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public void setTextMode(boolean textMode) {
        boolean oldIconMode = iconMode;
        boolean newIconMode = !textMode;
        if (oldIconMode == newIconMode) {
            return;
        }
        iconMode = newIconMode;
        transparency(true);
        if (textMode && !preferences.block().get()) {
            showTextContent();
        } else {
            showIconContent();
        }
    }

    private void showTextContent() {
        Container pane = getContentPane();
        pane.remove(iconContent);
        pane.add(textContent, BorderLayout.CENTER);
        textContent.repaint();
    }

    @Override
    public void setToolTipText(String text) {
        textContent.setToolTipText(text);
        iconContent.setToolTipText(text);
        if (reshow && text != null && !text.isEmpty()) {
            try {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                double v = pointerInfo.getDevice().getDisplayMode().getHeight() - pointerInfo.getLocation().getY();
                int delta = v < 100 ? -TOOLTIP_OFFSET : TOOLTIP_OFFSET;
                if (textContent.isVisible()) {
                    ToolTipManager.sharedInstance()
                            .mouseMoved(new MouseEvent(textContent,
                                    MouseEvent.MOUSE_MOVED,
                                    CTTimeUtil.currentTimeMillis(),
                                    0,
                                    getWidth(),
                                    getHeight() + delta,
                                    0,
                                    false));
                }
                if (iconContent.isVisible()) {
                    ToolTipManager.sharedInstance()
                            .mouseMoved(new MouseEvent(iconContent,
                                    MouseEvent.MOUSE_MOVED,
                                    CTTimeUtil.currentTimeMillis(),
                                    0,
                                    getWidth(),
                                    getHeight() + delta,
                                    0,
                                    false));
                }
            } catch (RuntimeException ex) {
                Log.debug(ex, MSG_TOOLTIP_ERROR);
            }
        }
    }

    @Override
    public void showText(Color foreground, String text) {
        textContent.showText(foreground, text);
        textContent.repaint();
    }

    private static class OwnerUpdater extends ComponentAdapter {
        private final Frame owner;

        public OwnerUpdater(Frame owner) {
            this.owner = owner;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            owner.setSize(e.getComponent().getSize());
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            owner.setLocation(e.getComponent().getLocation());
        }
    }
}