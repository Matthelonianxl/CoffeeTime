package gargoyle.ct.ui.impl;

import gargoyle.ct.ex.CTException;
import gargoyle.ct.log.Log;
import gargoyle.ct.messages.impl.CTMessages;
import gargoyle.ct.ui.CTApp;
import gargoyle.ct.ui.CTDialog;
import gargoyle.ct.ui.CTIconProvider;
import gargoyle.ct.ui.util.CTDragHelper;
import gargoyle.ct.ui.util.render.GraphPaperLayout;
import gargoyle.ct.ver.CTVersionInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class CTAboutDialog extends JDialog implements CTDialog<Void> {
    private static final float FONT_BIG = 1.2f;
    private static final float FONT_SMALL = 0.8f;
    private static final String FORMAT_LABEL = "%s:";
    private static final String LABEL_ORGANIZATION_NAME = "label_organization_name";
    private static final String LABEL_ORGANIZATION_URL = "label_organization_url";
    private static final String LABEL_PROJECT_DESCRIPTION = "label_project_description";
    private static final String LABEL_PROJECT_NAME = "label_project_name";
    private static final String LABEL_PROJECT_VERSION = "label_project_version";
    private static final String LOC_ABOUT = "messages/about";
    private static final long serialVersionUID = 4721343233167467386L;
    @NotNull
    private final transient CTMessages about;
    @NotNull
    private final Window owner;
    @NotNull
    private final transient CTVersionInfo version;

    public CTAboutDialog(CTApp app, Window owner) {
        super(owner.getOwner(), ModalityType.APPLICATION_MODAL);
        this.owner = owner;
        about = new CTMessages(LOC_ABOUT);
        version = app.getVersionInfo();
        init(app);
    }

    private void addHyperlink(@NotNull String descriptionKey, String content, URI address, int y) {
        addField(descriptionKey, content, address, y);
    }

    private void addField(@NotNull String descriptionKey, String content, @Nullable URI address, int y) {
        Container pane = getContentPane();
        JLabel descriptionLabel = new JLabel(String.format(FORMAT_LABEL, about.getMessage(descriptionKey)));
        adjustFontSize(descriptionLabel, FONT_SMALL);
        descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pane.add(descriptionLabel, new Rectangle(0, y, 1, 1));
        JLabel contentLabel = address == null ? new JLabel(content) : new JHyperlink(content, address);
        adjustFontSize(contentLabel, FONT_BIG);
        pane.add(contentLabel, new Rectangle(1, y, 1, 1));
        descriptionLabel.setLabelFor(contentLabel);
    }

    @SuppressWarnings("MethodMayBeStatic")
    private void adjustFontSize(JComponent component, float mul) {
        Font font = component.getFont();
        component.setFont(font.deriveFont(font.getSize() * mul));
    }

    private void addHyperlink(@NotNull String descriptionKey, String content, @Nullable String address, int y) {
        try {
            addField(descriptionKey, content, address == null ? null : new URI(address), y);
        } catch (URISyntaxException e) {
            throw new CTException(descriptionKey, e);
        }
    }

    private void addLabel(@NotNull String descriptionKey, String content, int y) {
        addField(descriptionKey, content, null, y);
    }

    private void init(CTIconProvider app) {
        setTitle(version.getProjectName());
        setIconImage(new ImageIcon(app.getSmallIcon()).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container pane = getContentPane();
        pane.setLayout(new GraphPaperLayout(2, 6, 6, 3));
        addLabel(LABEL_PROJECT_NAME, version.getProjectName(), 0);
        addLabel(LABEL_PROJECT_DESCRIPTION, version.getProjectDescription(), 1);
        addLabel(LABEL_PROJECT_VERSION, version.getProjectVersion(), 2);
        addLabel(LABEL_ORGANIZATION_NAME, version.getOrganizationName(), 3);
        addHyperlink(LABEL_ORGANIZATION_URL, version.getOrganizationUrl(), version.getOrganizationUrl(), 4);
        pane.add(new JButton(new OkAction(this)), new Rectangle(0, 5, 2, 1));
        pack();
    }

    @Override
    public Void showMe() {
        CTDragHelper.setLocationRelativeTo(this, owner);
        setVisible(true);
        return null;
    }

    private static class JHyperlink extends JLabel {
        private static final long serialVersionUID = -2543205042883642083L;
        final URI address;

        public JHyperlink(String message, URI address) {
            super(String.format("<html><u>%s</u></html>", message));
            this.address = address;
            setForeground(Color.BLUE.darker());
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new HyperlinkLabelMouseAdapter());
        }

        private class HyperlinkLabelMouseAdapter extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(address);
                    }
                } catch (@NotNull UnsupportedOperationException | IOException ex) {
                    Log.error(ex, ex.getLocalizedMessage());
                }
            }
        }
    }

    private static class OkAction extends AbstractAction {
        private static final long serialVersionUID = -4568926242772773378L;
        private final CTAboutDialog dialog;

        public OkAction(CTAboutDialog dialog) {
            super("OK");
            this.dialog = dialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }

        @NotNull
        @Override
        public OkAction clone() throws CloneNotSupportedException {
            return (OkAction) super.clone();
        }
    }
}
