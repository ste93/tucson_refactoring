package it.unibo.mok.gui.impl.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {

    private static final long serialVersionUID = 8908061103618981760L;
    private final JLabel l;

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(new Color(240, 240, 240));
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                Color.decode("#999999")));
        this.l = new JLabel(" MoK System GUI    |    Giacomo Dradi 726868");
        // l.setFont(new Font("Calibri", Font.PLAIN, 13));
        this.l.setPreferredSize(new Dimension(400, 18));
        this.add(this.l);
    }
}
