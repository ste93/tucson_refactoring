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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class EditSpec extends javax.swing.JFrame {

    private static final long serialVersionUID = 2491540632593263750L;

    private static String format(final LogicTuple t) {
        String res = "";
        try {
            res = t.getName() + "(\n";
            res += "	" + t.getArg(0) + ",\n";
            res += "	" + t.getArg(1) + ",\n";
            res += "	" + t.getArg(2) + "\n";
            res += ").\n";
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static String predFormat(final LogicTuple t) {
        String res = "";
        try {
            if (!":-".equals(t.getName())) {
                res = t.toString() + ".\n";
            } else {
                res = t.getArg(0) + " :-\n";
                res += "    " + t.getArg(1) + ".\n";
            }
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
        return res;
    }

    private javax.swing.JButton bGet;
    private javax.swing.JButton bLoad;

    private javax.swing.JButton bOk;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bSaveAs;
    private javax.swing.JTextField caretPosition;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField outputState;
    EnhancedACC context;
    alice.util.jedit.JEditTextArea inputSpec;

    String specFileName = "default.rsp";

    TucsonTupleCentreId tid;

    /** Creates new form GUIEditTheory */
    public EditSpec(final TucsonTupleCentreId tid_) {

        this.initComponents();
        this.setTitle("ReSpecT specification tuples of tuplecentre < "
                + tid_.getName() + "@" + tid_.getNode() + ":" + tid_.getPort()
                + " >");
        this.inputSpec =
                new alice.util.jedit.JEditTextArea(new SpecificationTextArea());
        this.inputSpec.setTokenMarker(new SpecificationTokenMarker());
        this.inputSpec.setPreferredSize(new java.awt.Dimension(800, 600));

        final java.awt.GridBagConstraints gridBagConstraints1 =
                new java.awt.GridBagConstraints();

        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 95.0;

        this.getContentPane().add(this.inputSpec, gridBagConstraints1);
        this.inputSpec.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(final javax.swing.event.CaretEvent evt) {
                EditSpec.this.caretPosition.setText("line "
                        + (EditSpec.this.inputSpec.getCaretLine() + 1) + "   ");
            }
        });
        this.pack();

        this.tid = tid_;
        try {
            this.context =
                    TucsonMetaACC.getContext(new TucsonAgentId("'$Inspector-"
                            + System.currentTimeMillis() + "'"));
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }

    }

    public void exit() {
        try {
            this.context.exit();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        }
    }

    private void bGetActionPerformed() {
        try {
            String spec = "";
            final List<LogicTuple> list =
                    this.context.get_s(this.tid, (Long) null)
                            .getLogicTupleListResult();
            for (final LogicTuple t : list) {
                if ("reaction".equals(t.getName())) {
                    spec += EditSpec.format(t);
                } else {
                    spec += EditSpec.predFormat(t);
                }
            }
            this.inputSpec.setText(spec);
            this.outputState.setText("ReSpecT specification read.");
        } catch (final TucsonOperationNotPossibleException e) {
            this.outputState.setText("" + e);
        } catch (final UnreachableNodeException e) {
            this.outputState.setText("TuCSoN Node is unreachable.");
        } catch (final OperationTimeOutException e) {
            this.outputState.setText("TuCSoN operation timeout exceeded.");
        } catch (final InvalidTupleOperationException e) {
            this.outputState.setText("" + e);
        }
    }

    private void bLoadActionPerformed() {

        final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            final java.io.File file = chooser.getSelectedFile();
            if (file != null) {
                final String name = file.getAbsolutePath();
                FileInputStream in = null;
                try {
                    in = new FileInputStream(name);
                    final byte[] text = new byte[in.available()];
                    in.read(text);
                    this.inputSpec.setText(new String(text));
                    this.outputState
                            .setText("ReSpecT specification loaded from file '"
                                    + name + "'.");
                    this.specFileName = name;
                } catch (final FileNotFoundException e) {
                    this.outputState.setText("File '" + name + "' not found.");
                } catch (final IOException e) {
                    this.outputState.setText("" + e);
                } finally {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void bOkActionPerformed() {
        try {
            final String spec = this.inputSpec.getText();
            if (spec.isEmpty()) {
                this.context.set_s(this.tid, LogicTuple.parse("[]"),
                        (Long) null);
            } else {
                this.context.set_s(this.tid, spec, (Long) null);
            }
            this.outputState.setText("ReSpecT specification set.");
        } catch (final TucsonOperationNotPossibleException e) {
            this.outputState.setText("" + e);
        } catch (final UnreachableNodeException e) {
            this.outputState.setText("TuCSoN Node is unreachable.");
        } catch (final OperationTimeOutException e) {
            this.outputState.setText("TuCSoN operation timeout exceeded.");
        } catch (final InvalidLogicTupleException e) {
            this.outputState.setText("Invalid ReSpecT specification given.");
        }
    }

    private void bSaveActionPerformed() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(this.specFileName);
            out.write(this.inputSpec.getText().getBytes());
            this.outputState.setText("ReSpecT specification saved on file '"
                    + this.specFileName + "'.");
        } catch (final FileNotFoundException e) {
            this.outputState.setText("File '" + this.specFileName
                    + "' not found.");
        } catch (final IOException e) {
            this.outputState.setText("" + e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void bSaveAsActionPerformed() {

        final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        final int returnVal = chooser.showSaveDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            final java.io.File file = chooser.getSelectedFile();
            if (file != null) {
                final String name = file.getAbsolutePath();
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(name);
                    out.write(this.inputSpec.getText().getBytes());
                    this.outputState
                            .setText("ReSpecT specification saved on file '"
                                    + name + "'.");
                    this.specFileName = name;
                } catch (final FileNotFoundException e) {
                    this.outputState.setText("File '" + name + "' not found.");
                } catch (final IOException e) {
                    this.outputState.setText("" + e);
                } finally {
                    try {
                        out.close();
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void formComponentShown() {
        try {
            final String spec =
                    ""
                            + this.context.get_s(this.tid, (Long) null)
                                    .getLogicTupleListResult();
            this.inputSpec.setText(spec);
            this.outputState.setText("ReSpecT specification read.");
        } catch (final TucsonOperationNotPossibleException e) {
            this.outputState.setText("" + e);
        } catch (final UnreachableNodeException e) {
            this.outputState.setText("TuCSoN Node is unreachable.");
        } catch (final OperationTimeOutException e) {
            this.outputState.setText("TuCSoN operation timeout exceeded.");
        }

    }

    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        this.outputState = new javax.swing.JTextField();
        this.jPanel2 = new javax.swing.JPanel();
        this.jPanel1 = new javax.swing.JPanel();
        this.bLoad = new javax.swing.JButton();
        this.bSave = new javax.swing.JButton();
        this.bSaveAs = new javax.swing.JButton();
        this.jPanel3 = new javax.swing.JPanel();
        this.bOk = new javax.swing.JButton();
        this.bGet = new javax.swing.JButton();
        this.caretPosition = new javax.swing.JTextField();

        this.getContentPane().setLayout(new java.awt.GridBagLayout());

        this.setTitle("S Inspector");
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(final java.awt.event.ComponentEvent evt) {
                EditSpec.this.formComponentShown();
            }
        });

        this.outputState.setBackground(Color.CYAN);
        this.outputState.setEditable(false);
        this.outputState.setBorder(new javax.swing.border.LineBorder(
                new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        this.getContentPane().add(this.outputState, gridBagConstraints);

        this.jPanel2.setLayout(new java.awt.GridBagLayout());

        this.jPanel1.setLayout(new java.awt.GridBagLayout());

        this.bLoad.setFont(new java.awt.Font("Arial", 0, 11));
        this.bLoad.setText("Load");
        this.bLoad
                .setToolTipText("Load the specification from the chosen file");
        this.bLoad.setFocusPainted(false);
        this.bLoad.setPreferredSize(new java.awt.Dimension(80, 30));
        this.bLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bLoadActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.jPanel1.add(this.bLoad, gridBagConstraints);

        this.bSave.setFont(new java.awt.Font("Arial", 0, 11));
        this.bSave.setText("Save");
        this.bSave
                .setToolTipText("Save the specification to the previously (default) chosen file");
        this.bSave.setFocusPainted(false);
        this.bSave.setPreferredSize(new java.awt.Dimension(80, 30));
        this.bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bSaveActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        this.jPanel1.add(this.bSave, gridBagConstraints);

        this.bSaveAs.setFont(new java.awt.Font("Arial", 0, 11));
        this.bSaveAs.setText("Save As");
        this.bSaveAs
                .setToolTipText("Save the specification to the chosen file");
        this.bSaveAs.setFocusPainted(false);
        this.bSaveAs.setPreferredSize(new java.awt.Dimension(80, 30));
        this.bSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bSaveAsActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        this.jPanel1.add(this.bSaveAs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        this.jPanel2.add(this.jPanel1, gridBagConstraints);

        this.jPanel3.setLayout(new java.awt.GridBagLayout());

        this.bOk.setFont(new java.awt.Font("Arial", 0, 11));
        this.bOk.setText("< set_s >");
        this.bOk.setToolTipText("Sets the specification space of the tuplecentre");
        this.bOk.setFocusPainted(false);
        this.bOk.setPreferredSize(new java.awt.Dimension(70, 30));
        this.bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bOkActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        this.jPanel3.add(this.bOk, gridBagConstraints);

        this.bGet.setFont(new java.awt.Font("Arial", 0, 11));
        this.bGet.setText("< get_s >");
        this.bGet
                .setToolTipText("Gets the specification space of the tuplecentre");
        this.bGet.setActionCommand("bRefresh");
        this.bGet.setPreferredSize(new java.awt.Dimension(70, 30));
        this.bGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bGetActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        this.jPanel3.add(this.bGet, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        this.jPanel2.add(this.jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 10.0;
        this.getContentPane().add(this.jPanel2, gridBagConstraints);

        this.caretPosition.setBackground(Color.CYAN);
        this.caretPosition.setEditable(false);
        this.caretPosition.setBorder(new javax.swing.border.LineBorder(
                new java.awt.Color(0, 0, 0)));
        this.caretPosition.setMinimumSize(new java.awt.Dimension(80, 20));
        this.caretPosition.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        this.getContentPane().add(this.caretPosition, gridBagConstraints);

    }

}
