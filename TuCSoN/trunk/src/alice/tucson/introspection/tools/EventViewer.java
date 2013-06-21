/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.introspection.tools;

import java.awt.Color;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.introspection.GetSnapshotMsg;
import alice.tucson.introspection.InspectorProtocol;

public class EventViewer extends javax.swing.JFrame {

    private static final long serialVersionUID = -9040554109345956651L;
    private javax.swing.JButton buttonAcceptFilterLog;
    private javax.swing.JButton buttonAcceptPattern;

    private javax.swing.JButton buttonBrowse;

    private javax.swing.JButton buttonGet;

    private javax.swing.JCheckBox checkFilterLog;

    private javax.swing.JCheckBox checkFilterView;

    private javax.swing.JCheckBox checkLogEnable;

    private final alice.tucson.introspection.InspectorContext context;

    private javax.swing.JTextField inputFileLog;

    private javax.swing.JTextField inputFilterLog;

    private javax.swing.JTextField inputFilterView;

    private javax.swing.JLabel jLabel2;

    private javax.swing.JLabel jLabel3;

    private javax.swing.JPanel jPanel1;

    private javax.swing.JPanel jPanel10;

    private javax.swing.JPanel jPanel2;

    private javax.swing.JPanel jPanel3;

    private javax.swing.JPanel jPanel4;

    private javax.swing.JPanel jPanel5;

    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private final Inspector mainForm;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JTextField outputNoItems;
    private javax.swing.JTextField outputState;
    private javax.swing.JTextField outputTime;
    private javax.swing.JTextField outputVmTime;
    private javax.swing.JRadioButton radioProactive;
    private javax.swing.JRadioButton radioReactive;

    /** Creates new form TupleForm */
    public EventViewer(final Inspector mainForm_) {
        this.initComponents();
        this.pack();
        this.mainForm = mainForm_;
        this.context = this.mainForm.agent.getContext();
        this.setTitle("Pending TuCSoN operations set of tuplecentre < "
                + this.mainForm.tid.getName() + "@"
                + this.mainForm.tid.getNode() + ":"
                + this.mainForm.tid.getPort() + " >");
        this.setSize(520, 460);
        this.radioProactive.setSelected(true);
        this.radioReactive.setSelected(false);
        this.buttonGet.setEnabled(false);
        this.buttonAcceptPattern.setEnabled(false);
        this.buttonAcceptFilterLog.setEnabled(false);
        this.inputFileLog.setText(this.mainForm.agent.logQueryFilename);
        this.outputState.setText("Ready for pending operations inspection.");
    }

    public void setLocalTime(final long l) {
        this.outputTime.setText(new Long(l).toString());
    }

    public void setNItems(final long l) {
        this.outputNoItems.setText(new Long(l).toString());
    }

    public void setText(final String st) {
        this.outputArea.setText(st);
    }

    public void setVMTime(final long l) {
        this.outputVmTime.setText(new Long(l).toString());
    }

    private void buttonAcceptFilterLogActionPerformed() {
        final String st = this.inputFilterLog.getText();
        LogicTuple t = null;
        try {
            t = LogicTuple.parse(st);
        } catch (final InvalidLogicTupleException e) {
            this.outputState
                    .setText("Please input an admissible tuple template...");
        }
        if (t == null) {
            this.outputState
                    .setText("Please input an admissible tuple template...");
        } else {
            this.mainForm.agent.logOpFilter = t;
            this.buttonGetActionPerformed();
        }
    }

    private void buttonAcceptPatternActionPerformed() {
        try {
            final String st = this.inputFilterView.getText();
            final LogicTuple t = LogicTuple.parse(st);
            if (t == null) {
                this.outputState
                        .setText("Given template is not an admissible Prolog term.");
            } else {
                this.mainForm.protocol.wsetFilter = t;
                this.context.setProtocol(this.mainForm.protocol);
            }
            this.buttonGetActionPerformed();
        } catch (final InvalidLogicTupleException e) {
            this.outputState
                    .setText("Given template is not an admissible Prolog term.");
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

    private void buttonBrowseActionPerformed() {
        final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            final java.io.File file = chooser.getSelectedFile();
            if (file != null) {
                final String name = file.getAbsolutePath();
                this.inputFileLog.setText(name);
                this.mainForm.agent.changeLogQueryFile(name);
            }
        }
    }

    private void buttonGetActionPerformed() {
        try {
            this.context.getSnapshot(GetSnapshotMsg.WSET);
            this.outputState.setText("Observation done.");
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

    private void checkFilterLogActionPerformed() {
        try {
            if (this.checkFilterLog.isSelected()) {
                this.buttonAcceptFilterLog.setEnabled(true);
                this.outputState
                        .setText("Please input an admissible tuple template...");
            } else {
                this.buttonAcceptFilterLog.setEnabled(false);
                this.mainForm.agent.logOpFilter = null;
            }
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

    private void checkFilterViewActionPerformed() {
        try {
            if (this.checkFilterView.isSelected()) {
                this.buttonAcceptPattern.setEnabled(true);
                this.outputState
                        .setText("Please input an admissible tuple template...");
            } else {
                this.buttonAcceptPattern.setEnabled(false);
                this.mainForm.protocol.wsetFilter = null;
                this.context.setProtocol(this.mainForm.protocol);
            }
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

    private void checkLogEnableActionPerformed() {
        if (this.checkLogEnable.isSelected()) {
            this.mainForm.agent.loggingQueries = true;
            this.outputState.setText("Please choose the output log file...");
        } else {
            this.mainForm.agent.loggingQueries = false;
        }
    }

    private void exitForm() {
        this.mainForm.onEventViewerExit();
    }

    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.outputArea = new javax.swing.JTextArea();
        this.jTabbedPane1 = new javax.swing.JTabbedPane();
        this.jPanel1 = new javax.swing.JPanel();
        this.jPanel2 = new javax.swing.JPanel();
        this.radioReactive = new javax.swing.JRadioButton();
        this.radioProactive = new javax.swing.JRadioButton();
        this.buttonGet = new javax.swing.JButton();
        this.jPanel3 = new javax.swing.JPanel();
        this.jPanel4 = new javax.swing.JPanel();
        this.checkFilterView = new javax.swing.JCheckBox();
        this.buttonAcceptPattern = new javax.swing.JButton();
        this.inputFilterView = new javax.swing.JTextField();
        this.jPanel5 = new javax.swing.JPanel();
        this.jPanel6 = new javax.swing.JPanel();
        this.checkFilterLog = new javax.swing.JCheckBox();
        this.buttonAcceptFilterLog = new javax.swing.JButton();
        this.inputFilterLog = new javax.swing.JTextField();
        this.jPanel8 = new javax.swing.JPanel();
        this.jLabel3 = new javax.swing.JLabel();
        this.inputFileLog = new javax.swing.JTextField();
        this.buttonBrowse = new javax.swing.JButton();
        this.checkLogEnable = new javax.swing.JCheckBox();
        this.jPanel10 = new javax.swing.JPanel();
        this.outputState = new javax.swing.JTextField();
        this.jPanel9 = new javax.swing.JPanel();
        this.outputTime = new javax.swing.JTextField();
        this.jLabel2 = new javax.swing.JLabel();
        this.outputNoItems = new javax.swing.JTextField();
        this.outputVmTime = new javax.swing.JTextField();

        this.getContentPane().setLayout(new java.awt.GridBagLayout());

        this.setTitle("W Inspector");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent evt) {
                EventViewer.this.exitForm();
            }
        });

        this.outputArea.setEditable(false);
        this.outputArea.setFont(new java.awt.Font("Courier New", 0, 12));
        this.jScrollPane1.setViewportView(this.outputArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        this.getContentPane().add(this.jScrollPane1, gridBagConstraints);

        this.jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11));
        this.jPanel1.setLayout(new java.awt.GridBagLayout());

        this.jPanel2.setLayout(new java.awt.GridBagLayout());
        this.jPanel2.setBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EtchedBorder(), "type"));
        this.jPanel2.setFont(new java.awt.Font("Arial", 0, 11));
        this.radioReactive.setFont(new java.awt.Font("Arial", 0, 11));
        this.radioReactive
                .setText("REACTIVE: update observations only upon request.");
        this.radioReactive
                .setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.radioReactive
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.radioReactiveActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 30.0;
        this.jPanel2.add(this.radioReactive, gridBagConstraints);

        this.radioProactive.setFont(new java.awt.Font("Arial", 0, 11));
        this.radioProactive
                .setText("PROACTIVE: update observations as soon as events happen.");
        this.radioProactive
                .setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.radioProactive
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.radioProactiveActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        this.jPanel2.add(this.radioProactive, gridBagConstraints);

        this.buttonGet.setFont(new java.awt.Font("Arial", 0, 11));
        this.buttonGet.setText("Observe!");
        this.buttonGet
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        this.buttonGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EventViewer.this.buttonGetActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 70.0;
        this.jPanel2.add(this.buttonGet, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        this.jPanel1.add(this.jPanel2, gridBagConstraints);

        this.jTabbedPane1.addTab("Observation", null, this.jPanel1, "");

        this.jPanel3.setLayout(new java.awt.GridBagLayout());

        this.jPanel4.setLayout(new java.awt.GridBagLayout());
        this.jPanel4.setBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EtchedBorder(), "filtering"));
        this.checkFilterView.setFont(new java.awt.Font("Arial", 0, 11));
        this.checkFilterView
                .setText("Filter observed tuples using the following template:");
        this.checkFilterView
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.checkFilterViewActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 10.0;
        this.jPanel4.add(this.checkFilterView, gridBagConstraints);

        this.buttonAcceptPattern.setFont(new java.awt.Font("Arial", 0, 11));
        this.buttonAcceptPattern.setText("Match!");
        this.buttonAcceptPattern
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this
                                .buttonAcceptPatternActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        this.jPanel4.add(this.buttonAcceptPattern, gridBagConstraints);

        this.inputFilterView.setFont(new java.awt.Font("Courier New", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 50.0;
        this.jPanel4.add(this.inputFilterView, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        this.jPanel3.add(this.jPanel4, gridBagConstraints);

        this.jTabbedPane1.addTab("Filter", null, this.jPanel3, "");

        this.jPanel5.setLayout(new java.awt.GridBagLayout());

        this.jPanel6.setLayout(new java.awt.GridBagLayout());
        this.jPanel6.setBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EtchedBorder(), "filtering"));
        this.jPanel6.setFont(new java.awt.Font("Arial", 0, 11));
        this.checkFilterLog.setFont(new java.awt.Font("Arial", 0, 11));
        this.checkFilterLog
                .setText("Filter observed tuples using the following template:");
        this.checkFilterLog
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.checkFilterLogActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 10.0;
        this.jPanel6.add(this.checkFilterLog, gridBagConstraints);

        this.buttonAcceptFilterLog.setFont(new java.awt.Font("Arial", 0, 11));
        this.buttonAcceptFilterLog.setText("Match!");
        this.buttonAcceptFilterLog
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this
                                .buttonAcceptFilterLogActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        this.jPanel6.add(this.buttonAcceptFilterLog, gridBagConstraints);

        this.inputFilterLog.setFont(new java.awt.Font("Courier New", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        this.jPanel6.add(this.inputFilterLog, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 70.0;
        this.jPanel5.add(this.jPanel6, gridBagConstraints);

        this.jPanel8.setLayout(new java.awt.GridBagLayout());
        this.jPanel8.setBorder(new javax.swing.border.TitledBorder(
                new javax.swing.border.EtchedBorder(), "store"));
        this.jPanel8.setFont(new java.awt.Font("Arial", 0, 11));
        this.jLabel3.setFont(new java.awt.Font("Arial", 0, 11));
        this.jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.jLabel3.setText("dump observations on file: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        this.jPanel8.add(this.jLabel3, gridBagConstraints);
        this.inputFileLog.setFont(new java.awt.Font("Courier New", 0, 12));
        this.inputFileLog
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.inputFileLogActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        this.jPanel8.add(this.inputFileLog, gridBagConstraints);
        this.buttonBrowse.setFont(new java.awt.Font("Arial", 0, 11));
        this.buttonBrowse.setText("Browse");
        this.buttonBrowse
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.buttonBrowseActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 5.0;
        this.jPanel8.add(this.buttonBrowse, gridBagConstraints);
        this.checkLogEnable
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        EventViewer.this.checkLogEnableActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        this.jPanel8.add(this.checkLogEnable, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 90.0;
        gridBagConstraints.weighty = 30.0;
        this.jPanel5.add(this.jPanel8, gridBagConstraints);

        this.jTabbedPane1.addTab("Log", null, this.jPanel5, "");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 10.0;
        this.getContentPane().add(this.jTabbedPane1, gridBagConstraints);

        this.jPanel10.setLayout(new java.awt.GridBagLayout());

        this.outputState.setBackground(Color.CYAN);
        this.outputState.setEditable(false);
        this.outputState.setBorder(new javax.swing.border.LineBorder(
                new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        this.jPanel10.add(this.outputState, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 5.0;
        this.getContentPane().add(this.jPanel10, gridBagConstraints);

        this.jPanel9.setLayout(new java.awt.GridBagLayout());

        this.jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.jLabel2.setText("# observations: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 20.0;
        this.jPanel9.add(this.jLabel2, gridBagConstraints);

        this.outputNoItems.setEditable(false);
        this.outputNoItems.setFont(new java.awt.Font("Courier New", 0, 12));
        this.outputNoItems
                .setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 20.0;
        this.jPanel9.add(this.outputNoItems, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 5.0;
        this.getContentPane().add(this.jPanel9, gridBagConstraints);

    }

    private void inputFileLogActionPerformed() {
        this.mainForm.agent.changeLogQueryFile(this.inputFileLog.getText());
    }

    private void radioProactiveActionPerformed() {
        try {
            this.mainForm.protocol.pendingQueryObservType =
                    InspectorProtocol.PROACTIVE_OBSERVATION;
            this.context.setProtocol(this.mainForm.protocol);
            this.buttonGet.setEnabled(false);
            this.radioReactive.setSelected(false);
            this.outputState.setText("PROACTIVE observation selected.");
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

    private void radioReactiveActionPerformed() {
        try {
            this.mainForm.protocol.pendingQueryObservType =
                    InspectorProtocol.REACTIVE_OBSERVATION;
            this.context.setProtocol(this.mainForm.protocol);
            this.buttonGet.setEnabled(true);
            this.radioProactive.setSelected(false);
            this.outputState
                    .setText("REACTIVE observation selected, push button 'Observe!' to update.");
        } catch (final Exception e) {
            this.outputState.setText("" + e);
        }
    }

}
