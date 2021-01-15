package gargoyle.ct.ui.util;

import gargoyle.ct.util.CTNumberUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public final class CTDragHelper {
    private CTDragHelper() {
    }

    public static void makeDraggable(@NotNull Component component, int snap) {
        makeDraggable0(component, snap, SwingUtilities.getWindowAncestor(component));
    }

    private static void makeDraggable0(Component component, int snap, @NotNull Component window) {
        Point mouseDownLocation = new Point();
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(@NotNull MouseEvent e) {
                mouseDownLocation.setLocation(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownLocation.setLocation(0, 0);
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(@NotNull MouseEvent e) {
                Point currentLocation = e.getLocationOnScreen();
                Point point = new Point(currentLocation.x - mouseDownLocation.x,
                        currentLocation.y - mouseDownLocation.y);
                snap(point, window.getSize(), snap);
                window.setLocation(point);
            }
        });
    }

    static void snap(Point p, @NotNull Dimension size, int snap) {
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (p.x - bounds.x < snap) {
            p.x = bounds.x;
        }
        if (p.y - bounds.y < snap) {
            p.y = bounds.y;
        }
        if (bounds.x + bounds.width - p.x - size.width < snap) {
            p.x = bounds.x + bounds.width - size.width;
        }
        if (bounds.y + bounds.height - p.y - size.height < snap) {
            p.y = bounds.y + bounds.height - size.height;
        }
    }

    public static void setLocationRelativeTo(Window comp, Window owner) {
        Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        comp.setLocationRelativeTo(owner);
        comp.setLocation(
                CTNumberUtil.toRange(comp.getX(), 0, screenSize.width - comp.getWidth()),
                CTNumberUtil.toRange(comp.getY(), 0, screenSize.height - comp.getHeight())
        );
    }
}
