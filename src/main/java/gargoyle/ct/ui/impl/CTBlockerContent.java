package gargoyle.ct.ui.impl;

import gargoyle.ct.log.Log;
import gargoyle.ct.pref.CTPreferences;
import gargoyle.ct.task.CTTaskUpdatable;
import gargoyle.ct.task.impl.CTTask;
import gargoyle.ct.ui.CTBlockerTextProvider;
import gargoyle.ct.ui.CTInformer;
import gargoyle.ct.ui.CTWindow;
import gargoyle.ct.ui.util.CTDragHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import static gargoyle.ct.ui.impl.control.CTControlWindowImpl.SNAP;

public final class CTBlockerContent extends JPanel implements CTTaskUpdatable, CTWindow, CTInformer {
    private static final float ALIGNMENT_CENTER = 0.5f;
    private static final float ALIGNMENT_RIGHT = 1.0f;
    private static final int FONT_SIZE = 12;
    private static final int GAP = 10;
    private static final double MARGIN = 1.1;
    private static final long serialVersionUID = 1873262133224449177L;
    private final boolean big;
    @NotNull
    private final JLabel lblInfo;
    @NotNull
    private final JLabel lblMain;
    @NotNull
    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private final transient CTPreferences preferences;
    private transient CTBlockerTextProvider textProvider;
    private boolean draggable;

    public CTBlockerContent(@NotNull CTPreferences preferences, boolean big) {
        this.preferences = preferences;
        textProvider = new CTBlockerTextProvider(preferences);
        this.big = big;
        setLayout(new BorderLayout());
        lblMain = createMainLabel();
        lblMain.addComponentListener(new ContentComponentListener(this, lblMain));
        add(lblMain, BorderLayout.CENTER);
        lblInfo = createInfoLabel();
        lblInfo.addComponentListener(new ContentComponentListener(this, lblInfo));
        add(lblInfo, BorderLayout.SOUTH);
    }

    @NotNull
    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.GRAY);
        label.setAlignmentX(ALIGNMENT_RIGHT);
        label.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        adjust(this, label);
        return label;
    }

    @NotNull
    private JLabel createMainLabel() {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(ALIGNMENT_CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        adjust(this, label);
        return label;
    }

    static void adjust(Component container, @NotNull JLabel label) {
        if (!container.isVisible() || container.getHeight() == 0) return;
        Font font = new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE);
        label.setFont(font);
        label.setFont(new Font(Font.DIALOG,
                Font.PLAIN,
                Math.min((int) (FONT_SIZE * label.getWidth() /
                                (MARGIN * label.getFontMetrics(font).stringWidth(label.getText()))),
                        label.getHeight())));
    }

    @Override
    public void destroy() {
        setVisible(false);
    }

    @Override
    public void showMe() {
        setVisible(true);
        if (!draggable) {
            draggable = true;
            CTDragHelper.makeDraggable(this, SNAP);
        }
    }

    @Override
    public void doUpdate(@NotNull CTTask task, long currentMillis) {
        lblInfo.setText(textProvider.getInfoText(task, currentMillis));
        boolean visible = textProvider.isVisible(task, currentMillis);
        setVisible(visible);
        if (visible) {
            showText(textProvider.getColor(task, currentMillis), textProvider.getBlockerText(task, currentMillis, big));
        }
    }

    @Override
    public void showText(Color foreground, String text) {
        setForeground(foreground);
        lblMain.setForeground(foreground);
        lblMain.setText(text);
        adjust(this, lblMain);
        if (isVisible()) {
            Log.debug(text);
            repaint();
        } else {
            showMe();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        textProvider = new CTBlockerTextProvider(preferences);
    }

    @Override
    public void setBackground(Color bg) {
//        super.setBackground(color);
        if (lblMain != null) lblMain.setBackground(bg);
    }

    private static class ContentComponentListener extends ComponentAdapter {
        private final CTBlockerContent container;
        private final JLabel label;

        @SuppressWarnings("WeakerAccess")
        public ContentComponentListener(CTBlockerContent container, JLabel label) {
            this.container = container;
            this.label = label;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            adjust(container, label);
        }

        @Override
        public void componentShown(ComponentEvent e) {
            adjust(container, label);
        }
    }
}
