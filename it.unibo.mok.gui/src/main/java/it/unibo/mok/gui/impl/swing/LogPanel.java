package it.unibo.mok.gui.impl.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class LogPanel extends JPanel {

    private static final long serialVersionUID = -9040033240803139104L;

    private static String labelText(final String text) {
        return "<html>" + text + "</html>";
    }

    private final JLabel compsLabel;
    private final JLabel configLabel;
    private final JLabel linksLabel;

    private final JLabel movingMolecules;

    public LogPanel(final Properties config) {
        super(new GridLayout(4, 1));
        this.setOpaque(true);
        this.setBackground(Color.white);
        final TitledBorder titled = new TitledBorder("Info");
        this.setBorder(BorderFactory.createCompoundBorder(titled,
                new EmptyBorder(4, 8, 10, 50)));

        final Font f = new Font("Calibri", Font.PLAIN, 14);

        this.configLabel = new JLabel();
        this.configLabel.setFont(f);
        this.setFps(0);
        this.add(this.configLabel);

        this.compsLabel = new JLabel();
        this.compsLabel.setFont(f);
        this.setNodesNumber(0);
        this.add(this.compsLabel);

        this.linksLabel = new JLabel();
        this.linksLabel.setFont(f);
        this.setLinksNumber(0);
        this.add(this.linksLabel);

        this.movingMolecules = new JLabel();
        this.movingMolecules.setFont(f);
        this.setMovingMoleculesNumber(0);
        this.add(this.movingMolecules);
    }

    public void clear() {
        this.setNodesNumber(0);
        this.setLinksNumber(0);
        this.setMovingMoleculesNumber(0);
    }

    public void setFps(final int n) {
        this.configLabel.setText(LogPanel.labelText("<b>" + n
                + "</b>&nbsp;&nbsp;fps"));
    }

    public void setLinksNumber(final int n) {
        this.linksLabel.setText(LogPanel.labelText("<b>" + n
                + "</b>&nbsp;&nbsp;links"));
    }

    public void setMovingMoleculesNumber(final int n) {
        this.movingMolecules.setText(LogPanel.labelText("<b>" + n
                + "</b>&nbsp;&nbsp;transfers"));
    }

    public void setNodesNumber(final int n) {
        this.compsLabel.setText(LogPanel.labelText("<b>" + n
                + "</b>&nbsp;&nbsp;nodes"));
    }

}
