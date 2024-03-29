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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 *
 * @author Unknown...
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 *
 */
public class EditSpec extends javax.swing.JFrame {

    private static final long serialVersionUID = 2491540632593263750L;
    private javax.swing.JTextField caretPosition;
    private EnhancedACC context;
    private final alice.util.jedit.JEditTextArea inputSpec;
    private javax.swing.JTextField outputState;
    private String specFileName = "default.rsp";
    private final TucsonTupleCentreId tid;

    /**
     * Creates new form GUIEditTheory
     *
     * @param t
     *            the identifier of the tuple centre under inspection
     */
    public EditSpec(final TucsonTupleCentreId t) {
        super();
        this.initComponents();
        this.setTitle("ReSpecT specification tuples of tuplecentre < "
                + t.getName() + "@" + t.getNode() + ":" + t.getPort() + " >");
        this.inputSpec = new alice.util.jedit.JEditTextArea(
                new SpecificationTextArea());
        this.inputSpec.setTokenMarker(new SpecificationTokenMarker());
        this.inputSpec.setPreferredSize(new java.awt.Dimension(800, 600));
        final java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 100.0;
        gridBagConstraints1.weighty = 95.0;
        this.getContentPane().add(this.inputSpec, gridBagConstraints1);
        this.inputSpec.addCaretListener(new javax.swing.event.CaretListener() {

            @Override
            public void caretUpdate(final javax.swing.event.CaretEvent evt) {
                EditSpec.this.caretPosition.setText("line "
                        + (EditSpec.this.inputSpec.getCaretLine() + 1) + "   ");
            }
        });
        this.pack();
        this.tid = t;
        try {
            this.context = TucsonMetaACC.getContext(new TucsonAgentId(
                    "'$Inspector-" + System.currentTimeMillis() + "'"));
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void exit() {
        try {
            this.context.exit();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        }
    }

    /**
     * get tuple centre specification space
     */
    private void bGetActionPerformed() {
        new SpecWorker("get", this.context, this.tid, this, null).start();
    }

    private void bLoadActionPerformed() {
        final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            final java.io.File file = chooser.getSelectedFile();
            if (file != null) {
                final String name = file.getAbsolutePath();
                try (final FileInputStream in = new FileInputStream(name)) {
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
                    this.outputState.setText(e.toString());
                }
            }
        }
    }

    private void bOkActionPerformed() {
        new SpecWorker("set", this.context, this.tid, this, this.inputSpec)
                .start();
    }

    private void bSaveActionPerformed() {
        try (final FileOutputStream out = new FileOutputStream(
                this.specFileName)) {
            out.write(this.inputSpec.getText().getBytes());
            this.outputState.setText("ReSpecT specification saved on file '"
                    + this.specFileName + "'.");
        } catch (final FileNotFoundException e) {
            this.outputState.setText("File '" + this.specFileName
                    + "' not found.");
        } catch (final IOException e) {
            this.outputState.setText(e.toString());
        }
    }

    private void bSaveAsActionPerformed() {
        final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        final int returnVal = chooser.showSaveDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            final java.io.File file = chooser.getSelectedFile();
            if (file != null) {
                final String name = file.getAbsolutePath();
                try (final FileOutputStream out = new FileOutputStream(name)) {
                    out.write(this.inputSpec.getText().getBytes());
                    this.outputState
                            .setText("ReSpecT specification saved on file '"
                                    + name + "'.");
                    this.specFileName = name;
                } catch (final FileNotFoundException e) {
                    this.outputState.setText("File '" + name + "' not found.");
                } catch (final IOException e) {
                    this.outputState.setText(e.toString());
                }
            }
        }
    }

    private void formComponentShown() {
        this.bGetActionPerformed();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        this.outputState = new javax.swing.JTextField();
        final JPanel jPanel2 = new JPanel();
        final JPanel jPanel1 = new JPanel();
        final JButton bLoad = new JButton();
        final JButton bSave = new JButton();
        final JButton bSaveAs = new JButton();
        final JPanel jPanel3 = new JPanel();
        final JButton bOk = new JButton();
        final JButton bGet = new JButton();
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
        jPanel2.setLayout(new java.awt.GridBagLayout());
        jPanel1.setLayout(new java.awt.GridBagLayout());
        bLoad.setFont(new java.awt.Font("Arial", 0, 11));
        bLoad.setText("Load");
        bLoad.setToolTipText("Load the specification from the chosen file");
        bLoad.setFocusPainted(false);
        bLoad.setPreferredSize(new java.awt.Dimension(80, 30));
        bLoad.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bLoadActionPerformed();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(bLoad, gridBagConstraints);
        bSave.setFont(new java.awt.Font("Arial", 0, 11));
        bSave.setText("Save");
        bSave.setToolTipText("Save the specification to the previously (default) chosen file");
        bSave.setFocusPainted(false);
        bSave.setPreferredSize(new java.awt.Dimension(80, 30));
        bSave.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bSaveActionPerformed();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(bSave, gridBagConstraints);
        bSaveAs.setFont(new java.awt.Font("Arial", 0, 11));
        bSaveAs.setText("Save As");
        bSaveAs.setToolTipText("Save the specification to the chosen file");
        bSaveAs.setFocusPainted(false);
        bSaveAs.setPreferredSize(new java.awt.Dimension(80, 30));
        bSaveAs.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bSaveAsActionPerformed();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel1.add(bSaveAs, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel1, gridBagConstraints);
        jPanel3.setLayout(new java.awt.GridBagLayout());
        bOk.setFont(new java.awt.Font("Arial", 0, 11));
        bOk.setText("< set_s >");
        bOk.setToolTipText("Sets the specification space of the tuplecentre");
        bOk.setFocusPainted(false);
        bOk.setPreferredSize(new java.awt.Dimension(70, 30));
        bOk.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bOkActionPerformed();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel3.add(bOk, gridBagConstraints);
        bGet.setFont(new java.awt.Font("Arial", 0, 11));
        bGet.setText("< get_s >");
        bGet.setToolTipText("Gets the specification space of the tuplecentre");
        bGet.setActionCommand("bRefresh");
        bGet.setPreferredSize(new java.awt.Dimension(70, 30));
        bGet.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                EditSpec.this.bGetActionPerformed();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel3.add(bGet, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 10.0;
        this.getContentPane().add(jPanel2, gridBagConstraints);
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

    /**
     * reports the result of get operation in SpecWorker
     *
     * @param spec
     *            the ReSpecT specification read
     */
    protected void getCompletion(final StringBuffer spec) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                EditSpec.this.inputSpec.setText(spec.toString());
                EditSpec.this.outputState
                        .setText("ReSpecT specification read.");
            }
        });
    }

    /**
     * reports the result of set operation in SpecWorker
     */
    protected void setCompletion() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                EditSpec.this.outputState.setText("ReSpecT specification set.");
            }
        });
    }
}
