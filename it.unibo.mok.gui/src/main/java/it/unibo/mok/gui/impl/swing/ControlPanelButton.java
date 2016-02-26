package it.unibo.mok.gui.impl.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ControlPanelButton extends JLabel implements MouseListener {

    private static final long serialVersionUID = -2666348474559442570L;
    private final Color defaultColor;
    private final Color highlightColor;

    public ControlPanelButton(final int height, final int width,
            final String text, final String iconFile) {
        super(text);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.TOP);
        this.defaultColor = Color.decode("#FFFFFF");
        this.highlightColor = Color.decode("#efefef");
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        final Font tabFont = new Font("Calibri", Font.PLAIN, 13);
        this.setOpaque(true);
        this.setFocusable(false);
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(this);
        this.setBackground(this.defaultColor);
        this.setFont(tabFont);
        try {
            final ImageIcon icon = new ImageIcon(ImageIO.read(this.getClass()
                    .getResource(iconFile)));
            this.setIcon(icon);
            this.setHorizontalTextPosition(SwingConstants.CENTER);
            this.setVerticalTextPosition(SwingConstants.BOTTOM);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        this.setBackground(this.highlightColor);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        this.setBackground(this.defaultColor);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

}
