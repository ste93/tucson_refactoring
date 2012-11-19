/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.introspection.tools;

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

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EditSpec extends javax.swing.JFrame{
	
	private static final long serialVersionUID = 2491540632593263750L;
	TucsonTupleCentreId tid;
	String specFileName = "default.rsp";
	alice.util.jedit.JEditTextArea inputSpec;
	EnhancedACC context;
	
	private javax.swing.JButton bSave;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton bLoad;
	private javax.swing.JButton bOk;
	private javax.swing.JButton bGet;
	private javax.swing.JButton bSaveAs;
	private javax.swing.JTextField outputState;
	private javax.swing.JTextField caretPosition;

	/** Creates new form GUIEditTheory */
	public EditSpec(TucsonTupleCentreId tid_){
		
		initComponents();
		setTitle("ReSpecT specification tuples of tuplecentre < " + tid_.getName() + "@" + tid_.getNode() + ":" + tid_.getPort() + " >");
		inputSpec = new alice.util.jedit.JEditTextArea(new SpecificationTextArea());
		inputSpec.setTokenMarker(new SpecificationTokenMarker());
		inputSpec.setPreferredSize(new java.awt.Dimension(800, 600));
		
		java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
		
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 100.0;
		gridBagConstraints1.weighty = 95.0;
		
		getContentPane().add(inputSpec, gridBagConstraints1);		
		inputSpec.addCaretListener(new javax.swing.event.CaretListener(){
			public void caretUpdate(javax.swing.event.CaretEvent evt){
				caretPosition.setText("line " + (inputSpec.getCaretLine() + 1) + "   ");
			}
		});
		pack();
		
		tid = tid_;
		try{
			context = TucsonMetaACC.getContext(new TucsonAgentId("inspector_edit_spec_" + System.currentTimeMillis()));
		}catch(TucsonInvalidAgentIdException e){
			e.printStackTrace();
		}
				
	}

	public void exit(){
    	  try{
    		  context.exit();
    	  }catch (TucsonOperationNotPossibleException e){
    		  e.printStackTrace();
    	  }
	}
	
	private void initComponents(){
		
		java.awt.GridBagConstraints gridBagConstraints;

		outputState = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		bLoad = new javax.swing.JButton();
		bSave = new javax.swing.JButton();
		bSaveAs = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		bOk = new javax.swing.JButton();
		bGet = new javax.swing.JButton();
		caretPosition = new javax.swing.JTextField();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setTitle("S Inspector");
		addComponentListener(new java.awt.event.ComponentAdapter(){
			public void componentShown(java.awt.event.ComponentEvent evt){
				formComponentShown(evt);
			}
		});

		outputState.setBackground(Color.CYAN);
		outputState.setEditable(false);
		outputState.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		outputState.setMinimumSize(new java.awt.Dimension(2, 20));
		outputState.setPreferredSize(new java.awt.Dimension(2, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(outputState, gridBagConstraints);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		jPanel1.setLayout(new java.awt.GridBagLayout());

		bLoad.setFont(new java.awt.Font("Arial", 0, 11));
		bLoad.setText("Load");
		bLoad.setToolTipText("Load the specification from the chosen file");
		bLoad.setFocusPainted(false);
		bLoad.setPreferredSize(new java.awt.Dimension(80, 30));
		bLoad.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				bLoadActionPerformed(evt);
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
		bSave.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				bSaveActionPerformed(evt);
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
		bSaveAs.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				bSaveAsActionPerformed(evt);
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
		bOk.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				bOkActionPerformed(evt);
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
		bGet.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				bGetActionPerformed(evt);
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
		getContentPane().add(jPanel2, gridBagConstraints);

		caretPosition.setBackground(Color.CYAN);
		caretPosition.setEditable(false);
		caretPosition.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		caretPosition.setMinimumSize(new java.awt.Dimension(80, 20));
		caretPosition.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		getContentPane().add(caretPosition, gridBagConstraints);
	
	}

	private void bGetActionPerformed(java.awt.event.ActionEvent evt){
		try{
//			String spec = ""+context.get_s(tid, (Long) null).getLogicTupleListResult();
			String spec = "";
			List<LogicTuple> list = context.get_s(tid, (Long) null).getLogicTupleListResult();
			for(LogicTuple t: list)
				spec += format(t);
			inputSpec.setText(spec);
			outputState.setText("Specification read.");
		}catch (TucsonOperationNotPossibleException e){
			outputState.setText("Specification not available.");
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			outputState.setText("Specification not available.");
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			outputState.setText("Specification not available.");
			e.printStackTrace();
		}
	}

	private void bOkActionPerformed(java.awt.event.ActionEvent evt){
		try{
			String spec = inputSpec.getText();
			if(spec.isEmpty())
				context.set_s(tid, LogicTuple.parse("[]"), (Long) null);
			else
				context.set_s(tid, spec, (Long) null);
			outputState.setText("Specification set.");
		}catch (TucsonOperationNotPossibleException e){
			outputState.setText("Specification set failure (invalid specification).");
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			outputState.setText("Specification set failure (invalid specification).");
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			outputState.setText("Specification set failure (invalid specification).");
			e.printStackTrace();
		} catch (InvalidLogicTupleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String format(LogicTuple t) {
		String res = "";
		try{
			res = t.getName()+"(\n";
			res += "	"+t.getArg(0)+",\n";
			res += "	"+t.getArg(1)+",\n";
			res += "	"+t.getArg(2)+"\n";
			res += ").\n";
		}catch(InvalidTupleOperationException e){
			e.printStackTrace();
		}
		return res;
	}

	private void formComponentShown(java.awt.event.ComponentEvent evt){
		try{
			String spec = ""+context.get_s(tid, (Long) null).getLogicTupleListResult();
			inputSpec.setText(spec);
			outputState.setText("Specification read.");
		}catch (TucsonOperationNotPossibleException e){
			outputState.setText("Specification not available.");
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			outputState.setText("Specification not available.");
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			outputState.setText("Specification not available.");
			e.printStackTrace();
		}

	}

	private void bCheckActionPerformed(java.awt.event.ActionEvent evt){
		try{
			String spec = inputSpec.getText();
			LogicTuple t = alice.respect.core.RespectVMContext.checkReactionSpec(spec);
			if (t.getName().equals("valid"))
				outputState.setText("Specification Test OK.");
			else
				outputState.setText("Specification Test failed: error at/before line " + t.getArg(0).intValue() + ".");
		}catch (InvalidTupleOperationException e){
			e.printStackTrace();
		}
	}

	private void bSaveAsActionPerformed(java.awt.event.ActionEvent evt){
		
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION){
			java.io.File file = chooser.getSelectedFile();
			if (file != null){
				String name = file.getAbsolutePath();
				try{
					FileOutputStream out = new FileOutputStream(name);
					out.write(inputSpec.getText().getBytes());
					outputState.setText("Specification saved (file " + name + ").");
					specFileName = name;
				}catch (FileNotFoundException e){
					outputState.setText("Specification save failure (file " + name + ").");
					e.printStackTrace();
				} catch (IOException e) {
					outputState.setText("Specification save failure (file " + name + ").");
					e.printStackTrace();
				}
			}
		}
		
	}

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt){
		try{
			FileOutputStream out = new FileOutputStream(specFileName);
			out.write(inputSpec.getText().getBytes());
			outputState.setText("Specification saved (file " + specFileName + ").");
		}catch (FileNotFoundException e){
			outputState.setText("Specification save failure (file " + specFileName + " ).");
			e.printStackTrace();
		} catch (IOException e) {
			outputState.setText("Specification save failure (file " + specFileName + " ).");
			e.printStackTrace();
		}
	}

	private void bLoadActionPerformed(java.awt.event.ActionEvent evt){
		
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION){
			java.io.File file = chooser.getSelectedFile();
			if (file != null){
				String name = file.getAbsolutePath();
				try{
					FileInputStream in = new FileInputStream(name);
					byte[] text = new byte[in.available()];
					in.read(text);
					inputSpec.setText(new String(text));
					outputState.setText("Specification loaded (file " + name + ").");
					specFileName = name;
				}catch (FileNotFoundException e){
					outputState.setText("Specificatioon load failure (file " + name + ").");
					e.printStackTrace();
				} catch (IOException e) {
					outputState.setText("Specificatioon load failure (file " + name + ").");
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
