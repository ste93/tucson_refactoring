package it.unibo.mok.gui.impl.swing;

import it.unibo.mok.gui.impl.Executor;
import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.Simulator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class ControlPanel extends JPanel implements MouseListener {

    private static final int BORDER_BOT = 5;
    private static final int PANEL_HEIGHT = 90;
    private static final long serialVersionUID = -2181459811722896605L;

    private ControlPanelButton addMoleculeBtn;
    private ControlPanelButton clearBtn;
    private ControlPanelButton createLinkBtn;
    private ControlPanelButton createNodeBtn;
    private final Executor executor;
    private JComboBox<Executable> executorsCombo;
    private ControlPanelButton moveMoleculeBtn;
    private ControlPanelButton removeLinkBtn;
    private ControlPanelButton removeMoleculeBtn;
    private ControlPanelButton removeNodeBtn;
    private ControlPanelButton runBtn;
    private ControlPanelButton setMoleculeConcBtn;
    private final Simulator simulator;
    private JTabbedPane tabbedPane;
	private ControlPanelButton setFilterBtn;

    public ControlPanel(final Simulator simulator, final Executor executor) {
        this.executor = executor;
        this.simulator = simulator;
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.addTabPanels();
    }

    public void addExecutable(final Executable executable) {
        this.executorsCombo.addItem(executable);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (e.getComponent() == ControlPanel.this.createNodeBtn) {
                    ControlPanel.this.simulator.addNode();
                } else if (e.getComponent() == ControlPanel.this.removeNodeBtn) {
                    ControlPanel.this.simulator.removeNode();
                } else if (e.getComponent() == ControlPanel.this.createLinkBtn) {
                    ControlPanel.this.simulator.addLink();
                } else if (e.getComponent() == ControlPanel.this.removeLinkBtn) {
                    ControlPanel.this.simulator.removeLink();
                } else if (e.getComponent() == ControlPanel.this.addMoleculeBtn) {
                    ControlPanel.this.simulator.addMolecule();
                } else if (e.getComponent() == ControlPanel.this.removeMoleculeBtn) {
                    ControlPanel.this.simulator.removeMolecule();
                } else if (e.getComponent() == ControlPanel.this.moveMoleculeBtn) {
                    ControlPanel.this.simulator.moveMolecule();
                } else if (e.getComponent() == ControlPanel.this.setMoleculeConcBtn) {
                    ControlPanel.this.simulator.setMoleculeConc();
                } else if (e.getComponent() == ControlPanel.this.clearBtn) {
                    ControlPanel.this.simulator.clear();
                } else if (e.getComponent() == ControlPanel.this.setFilterBtn) {
                    new Thread(new Runnable() {
						@Override
						public void run() {
							String filter = askFilter();
							ControlPanel.this.executor.setFilter(filter);
						}	
                    }).start();
                } else if (e.getComponent() == ControlPanel.this.runBtn) {
                    if (ControlPanel.this.executor
                            .execute((Executable) ControlPanel.this.executorsCombo
                                    .getSelectedItem())) {
                        ControlPanel.this.switchRunButton(true);
                        ControlPanel.this.executorsCombo.setEnabled(false);
                    } else {
                        ControlPanel.this.executor.stopCurrentExecutable();
                        ControlPanel.this.executorsCombo.setEnabled(true);
                        ControlPanel.this.switchRunButton(false);
                    }
                }
            }

        }).start();
    }

    private void addTabPanels() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setBackground(Color.WHITE);
        this.tabbedPane.setFocusable(false);

        final JComponent panel1 = this.createExecutionPanel();
        final JComponent panel2 = this.createSimulationPanel();
        this.tabbedPane.addTab("", null, panel1);
        this.tabbedPane.addTab("", null, panel2);

        final JLabel tab1 = new JLabel("Execution".toUpperCase(),
                SwingConstants.CENTER);
        final JLabel tab2 = new JLabel("Simulation".toUpperCase(),
                SwingConstants.CENTER);
        final Dimension tabDimension = new Dimension(130, 20);
        final Font tabFont = new Font("Calibri", Font.PLAIN, 13);
        tab1.setPreferredSize(tabDimension);
        tab2.setPreferredSize(tabDimension);
        tab1.setFont(tabFont);
        tab2.setFont(tabFont);
        this.tabbedPane.setTabComponentAt(0, tab1);
        this.tabbedPane.setTabComponentAt(1, tab2);

        UIManager.getDefaults().put("TabbedPane.contentBorderInsets",
                new Insets(1, 0, 1, 0));

        this.add(this.tabbedPane, BorderLayout.CENTER);
        this.tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    private JComponent createExecutionPanel() {
        final JPanel panel = new JPanel(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(true);
        panel.setBackground(Color.white);
        final JLabel l1 = new JLabel("Executable to run:");
        final Font tabFont = new Font("Calibri", Font.PLAIN, 13);
        l1.setFont(tabFont);

        final JPanel p = new JPanel(new GridLayout(3, 1));
        this.executorsCombo = new JComboBox<>();
        this.executorsCombo.setPreferredSize(new Dimension(200, 25));
        this.executorsCombo.setFocusable(false);

        p.setOpaque(false);
        p.add(l1);
        p.add(this.executorsCombo);

        this.runBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT, 85,
                "<html><center>RUN</center</html>", "/icons/play3.png");
        this.runBtn.setVerticalAlignment(SwingConstants.TOP);
        this.runBtn.addMouseListener(this);
 
        this.setFilterBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT, 85,
                "<html><center>FILTER MOLECULE</center</html>", "/icons/spinner4.png");
        this.setFilterBtn.setVerticalAlignment(SwingConstants.TOP);
        this.setFilterBtn.addMouseListener(this);
        
        panel.add(this.runBtn);
        panel.add(p);
        panel.add(this.getSeparator(ControlPanel.PANEL_HEIGHT));
        panel.add(this.setFilterBtn);
                
        return panel;
    }

    private JComponent createSimulationPanel() {
        final JPanel panel = new JPanel(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(true);
        panel.setBackground(Color.white);

        this.clearBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT, 85,
                "<html><center>CLEAR</center</html>", "/icons/loop2.png");
        this.clearBtn.addMouseListener(this);

        this.createNodeBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT,
                90, "<html><center>ADD NODE</center</html>",
                "/icons/database.png");
        this.removeNodeBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT,
                90, "<html><center>REMOVE NODE</center</html>",
                "/icons/bin2.png");
        this.createNodeBtn.addMouseListener(this);
        this.removeNodeBtn.addMouseListener(this);

        this.createLinkBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT,
                80, "<html><center>ADD LINK</center</html>",
                "/icons/enlarge2.png");
        this.removeLinkBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT,
                80, "<html><center>REMOVE LINK</center</html>",
                "/icons/shrink2.png");
        this.createLinkBtn.addMouseListener(this);
        this.removeLinkBtn.addMouseListener(this);

        this.addMoleculeBtn = new ControlPanelButton(ControlPanel.PANEL_HEIGHT,
                80, "<html><center>ADD MOLECULE</center</html>",
                "/icons/spinner4.png");
        this.removeMoleculeBtn = new ControlPanelButton(
                ControlPanel.PANEL_HEIGHT, 80,
                "<html><center>REMOVE MOLECULE</center</html>",
                "/icons/cross.png");
        this.moveMoleculeBtn = new ControlPanelButton(
                ControlPanel.PANEL_HEIGHT, 80,
                "<html><center>TRANSFER MOLECULE</center</html>",
                "/icons/tab.png");
        this.setMoleculeConcBtn = new ControlPanelButton(
                ControlPanel.PANEL_HEIGHT, 100,
                "<html><center>CHANGE<br>CONCENTRATION</center</html>",
                "/icons/eyedropper.png");
        this.addMoleculeBtn.addMouseListener(this);
        this.removeMoleculeBtn.addMouseListener(this);
        this.moveMoleculeBtn.addMouseListener(this);
        this.setMoleculeConcBtn.addMouseListener(this);

        panel.add(this.clearBtn);
        panel.add(this.getSeparator(ControlPanel.PANEL_HEIGHT));
        panel.add(this.createNodeBtn);
        panel.add(this.removeNodeBtn);
        panel.add(this.getSeparator(ControlPanel.PANEL_HEIGHT));
        panel.add(this.createLinkBtn);
        panel.add(this.removeLinkBtn);
        panel.add(this.getSeparator(ControlPanel.PANEL_HEIGHT));
        panel.add(this.addMoleculeBtn);
        panel.add(this.removeMoleculeBtn);
        panel.add(this.moveMoleculeBtn);
        panel.add(this.setMoleculeConcBtn);

        return panel;
    }

    private JLabel getSeparator(final int height) {
        final JLabel separator = new JLabel();
        separator.setPreferredSize(new Dimension(1, height));
        final Border b = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(0, 0, ControlPanel.BORDER_BOT, 0),
                BorderFactory.createMatteBorder(0, 1, 0, 0,
                        Color.decode("#cccccc")));
        separator.setBorder(b);
        return separator;
    }

    private void switchRunButton(final boolean toStop) {
        String iconName;
        String text = null;
        if (toStop) {
            iconName = "/icons/cross.png";
            text = "STOP";
        } else {
            iconName = "/icons/play3.png";
            text = "RUN";
        }
        this.runBtn.setText(text);
        try {
            final ImageIcon icon = new ImageIcon(ImageIO.read(this.getClass()
                    .getResource(iconName)));
            this.runBtn.setIcon(icon);
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private String askFilter() {
        final JTextField filter = new JTextField("", 20);
        final JLabel l1 = new JLabel("Filter:");
        final Dimension d = new Dimension(100, 20);
        l1.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(filter);
        final JPanel p = new JPanel(new GridLayout(1, 1));
        p.add(firstRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Set molecule filter", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !filter.getText().trim().equals("")) {
        	return filter.getText();
        }
        return null;
    }
    


}
