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

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.introspection.InspectorContext;
import alice.tucson.introspection.InspectorProtocol;

import java.io.FileWriter;

public class Inspector extends javax.swing.JFrame{
	
	private static final long serialVersionUID = -3765811664087552414L;
	InspectorCore agent;
	InspectorContext context;
	TupleViewer tupleForm;
	EventViewer pendingQueryForm;
	ReactionViewer reactionForm;
	EditSpec specForm;
	InspectorProtocol protocol = new InspectorProtocol();
	Object exit = new Object();
	TucsonAgentId aid;
	TucsonTupleCentreId tid;
	private boolean isSessionOpen;
	
	boolean loggingTuples = false;
	String logTupleFilename;
	FileWriter logTupleWriter;
	LogicTuple logTupleFilter;

	boolean loggingQueries = false;
	String logQueryFilename;
	FileWriter logQueryWriter;

	boolean loggingReactions = false;
	String logReactionFilename;
	FileWriter logReactionWriter;

	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JButton jButton7;
	private javax.swing.JButton jButton6;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JButton jButton5;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JButton jButton4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton jButton2;
	private javax.swing.JTextField inputName;
	private javax.swing.JButton buttonNextStep;
	private javax.swing.JTextField inputNode;
	private javax.swing.JTextField inputPort;
	private javax.swing.JTabbedPane controlPanel;
	private javax.swing.JLabel jLabelPort;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JCheckBox checkStepExecution;
	private javax.swing.JTextField stateBar;
	private javax.swing.JButton buttonInspect;

	/** Creates new form Inspector */
	public Inspector(TucsonAgentId aid) throws Exception{
		initComponents();
		this.aid = aid;
		disableControlPanel();
		isSessionOpen = false;
		this.setVisible(true);		
	}

	/** Creates new form Inspector */
	public Inspector(TucsonAgentId aid, TucsonTupleCentreId tid) throws Exception{
		this(aid);
		this.tid = tid;
		inputName.setText(tid.getName());
		inputNode.setText(alice.util.Tools.removeApices(tid.getNode()));
		inputPort.setText(alice.util.Tools.removeApices(""+tid.getPort()));
		buttonInspectActionPerformed(null);
	}

	TupleViewer getTupleForm(){
		return tupleForm;
	}

	EventViewer getQueryForm(){
		return pendingQueryForm;
	}

	ReactionViewer getReactionForm(){
		return reactionForm;
	}

	void onTupleViewerExit(){
		try{
			protocol.tsetObservType = InspectorProtocol.NO_OBSERVATION;
			context.setProtocol(protocol);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	void onReactionViewerExit(){
		try{
			protocol.reactionsObservType = InspectorProtocol.NO_OBSERVATION;
			context.setProtocol(protocol);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	void onEventViewerExit(){
		try{
			protocol.pendingQueryObservType = InspectorProtocol.NO_OBSERVATION;
			context.setProtocol(protocol);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void initComponents(){
		
		java.awt.GridBagConstraints gridBagConstraints;

		controlPanel = new javax.swing.JTabbedPane();
		jPanel4 = new javax.swing.JPanel();
		jButton4 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton5 = new javax.swing.JButton();
		jButton7 = new javax.swing.JButton();
		jPanel6 = new javax.swing.JPanel();
		jButton6 = new javax.swing.JButton();
		jPanel7 = new javax.swing.JPanel();
		checkStepExecution = new javax.swing.JCheckBox();
		buttonNextStep = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabelPort = new javax.swing.JLabel();
		inputName = new javax.swing.JTextField();
		inputNode = new javax.swing.JTextField();
		inputPort = new javax.swing.JTextField();
		buttonInspect = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		stateBar = new javax.swing.JTextField();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setTitle("TuCSoN Inspector");
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(java.awt.event.WindowEvent evt){
				exitForm(evt);
			}
		});

		controlPanel.setFont(new java.awt.Font("Arial", 0, 11));
		controlPanel.setMaximumSize(new java.awt.Dimension(340, 120));
		controlPanel.setMinimumSize(new java.awt.Dimension(340, 120));
		controlPanel.setPreferredSize(new java.awt.Dimension(340, 120));
		
		jPanel4.setLayout(new java.awt.GridBagLayout());
		jPanel4.setToolTipText("Inspecting Main Tuple Centre Sets");
		jPanel4.setMinimumSize(new java.awt.Dimension(240, 100));
		jPanel4.setPreferredSize(new java.awt.Dimension(240, 100));
		
		jButton4.setFont(new java.awt.Font("Arial", 0, 11));
		jButton4.setText("tuples");
		jButton4.setToolTipText("Inspecting Tuple Set");
		jButton4.setMaximumSize(new java.awt.Dimension(120, 25));
		jButton4.setMinimumSize(new java.awt.Dimension(120, 25));
		jButton4.setPreferredSize(new java.awt.Dimension(120, 25));
		jButton4.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				jButton4ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		jPanel4.add(jButton4, gridBagConstraints);

		jButton2.setFont(new java.awt.Font("Arial", 0, 11));
		jButton2.setText("pending");
		jButton2.setToolTipText("Inspecting Pending Queries Set");
		jButton2.setMaximumSize(new java.awt.Dimension(120, 25));
		jButton2.setMinimumSize(new java.awt.Dimension(120, 25));
		jButton2.setPreferredSize(new java.awt.Dimension(120, 25));
		jButton2.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				jButton2ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		jPanel4.add(jButton2, gridBagConstraints);

		jButton5.setFont(new java.awt.Font("Arial", 0, 11));
		jButton5.setText("reactions");
		jButton5.setToolTipText("Inspecting Triggered Rections Sets");
		jButton5.setMaximumSize(new java.awt.Dimension(120, 25));
		jButton5.setMinimumSize(new java.awt.Dimension(120, 25));
		jButton5.setPreferredSize(new java.awt.Dimension(120, 25));
		jButton5.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				jButton5ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		jPanel4.add(jButton5, gridBagConstraints);

		jButton7.setFont(new java.awt.Font("Arial", 0, 11));
		jButton7.setText("specification");
		jButton7.setToolTipText("Inspecting Specification Tuples Set");
		jButton7.setMaximumSize(new java.awt.Dimension(120, 25));
		jButton7.setMinimumSize(new java.awt.Dimension(120, 25));
		jButton7.setPreferredSize(new java.awt.Dimension(120, 25));
		jButton7.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				jButton7ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 50.0;
		gridBagConstraints.weighty = 100.0;
		jPanel4.add(jButton7, gridBagConstraints);

		controlPanel.addTab("Sets", null, jPanel4, "Inspecting Main Tuple Centre Sets");

		jPanel6.setLayout(new java.awt.GridBagLayout());
		jPanel6.setToolTipText("Controlling Tuple Centre Virtual Machine");
		jPanel6.setMinimumSize(new java.awt.Dimension(240, 100));
		jPanel6.setPreferredSize(new java.awt.Dimension(240, 100));
		jButton6.setFont(new java.awt.Font("Arial", 0, 11));
		jButton6.setText("reset");
		jButton6.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				jButton6ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 30.0;
		jPanel6.add(jButton6, gridBagConstraints);

		jPanel7.setLayout(new java.awt.GridBagLayout());
		jPanel7.setBorder(new javax.swing.border.TitledBorder("trace"));
		jPanel7.setFont(new java.awt.Font("Arial", 0, 11));
		jPanel7.setMinimumSize(new java.awt.Dimension(161, 100));
		jPanel7.setPreferredSize(new java.awt.Dimension(161, 100));

		checkStepExecution.setFont(new java.awt.Font("Arial", 0, 11));
		checkStepExecution.setText("step execution");
		checkStepExecution.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				checkStepExecutionActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 20.0;
		jPanel7.add(checkStepExecution, gridBagConstraints);

		buttonNextStep.setFont(new java.awt.Font("Arial", 0, 11));
		buttonNextStep.setText("next");
		buttonNextStep.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				buttonNextStepActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		jPanel7.add(buttonNextStep, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 70.0;
		gridBagConstraints.weighty = 100.0;
		jPanel6.add(jPanel7, gridBagConstraints);

		controlPanel.addTab("Virtual Machine", null, jPanel6, "Controlling Tuple Centre Virtual Machine");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(controlPanel, gridBagConstraints);
		jPanel1.setLayout(new java.awt.GridBagLayout());

		jPanel1.setBorder(new javax.swing.border.TitledBorder(null, "tuple centre information",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 12)));
		jPanel1.setMaximumSize(new java.awt.Dimension(340, 140));
		jPanel1.setMinimumSize(new java.awt.Dimension(340, 140));
		jPanel1.setPreferredSize(new java.awt.Dimension(340, 180));
		
		jLabel3.setText("name");
		jLabel3.setMaximumSize(new java.awt.Dimension(40, 16));
		jLabel3.setMinimumSize(new java.awt.Dimension(40, 20));
		jLabel3.setPreferredSize(new java.awt.Dimension(40, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(jLabel3, gridBagConstraints);

		jLabel4.setText("node");
		jLabel4.setMaximumSize(new java.awt.Dimension(40, 16));
		jLabel4.setMinimumSize(new java.awt.Dimension(40, 20));
		jLabel4.setPreferredSize(new java.awt.Dimension(40, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(jLabel4, gridBagConstraints);
		
		jLabelPort.setText("port");
		jLabelPort.setMaximumSize(new java.awt.Dimension(40, 16));
		jLabelPort.setMinimumSize(new java.awt.Dimension(40, 20));
		jLabelPort.setPreferredSize(new java.awt.Dimension(40, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(jLabelPort, gridBagConstraints);

		inputName.setText("default");
		inputName.setToolTipText("tuple centre name");
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(inputName, gridBagConstraints);

		inputNode.setText("localhost");
		inputNode.setToolTipText("IP address of the TuCSoN Node");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(inputNode, gridBagConstraints);

		inputPort.setText("20504");
		inputPort.setToolTipText("listening port of the TuCSoN Node");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		jPanel1.add(inputPort, gridBagConstraints);

		buttonInspect.setFont(new java.awt.Font("Arial", 0, 11));
		buttonInspect.setText("inspect");
		buttonInspect.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				buttonInspectActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.weightx = 100.0;
		gridBagConstraints.weighty = 30.0;
		jPanel1.add(buttonInspect, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/alice/tucson/images/logo.gif")));
		jLabel1.setPreferredSize(new java.awt.Dimension(90, 140));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel2.add(jLabel1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		getContentPane().add(jPanel2, gridBagConstraints);

		stateBar.setBackground(new java.awt.Color(224, 214, 163));
		stateBar.setEditable(false);
		stateBar.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				stateBarActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		getContentPane().add(stateBar, gridBagConstraints);

	}

	private void stateBarActionPerformed(java.awt.event.ActionEvent evt){
		// TODO
	}

	private void buttonInspectActionPerformed(java.awt.event.ActionEvent evt){

		if (!isSessionOpen){
			
			try{
				
				String name = inputName.getText();
				String address = inputNode.getText();
				String port = inputPort.getText();

				tid = new TucsonTupleCentreId(name, address, port);
				agent = new InspectorCore(this, aid, tid);					
				context = agent.getContext();
				agent.start();
								
				tupleForm = new TupleViewer(this);
				pendingQueryForm = new EventViewer(this);
				reactionForm = new ReactionViewer(this);
				specForm = new EditSpec(tid);
				inputName.setText(tid.getName());
				inputNode.setText(alice.util.Tools.removeApices(tid.getNode()));
				inputPort.setText(alice.util.Tools.removeApices(""+tid.getPort()));
				checkStepExecution.setSelected(false);
				buttonNextStep.setEnabled(false);
				inputName.setEditable(false);
				inputNode.setEditable(false);
				inputPort.setEditable(false);

				buttonInspect.setText("quit");				
				enableControlPanel();
				isSessionOpen = true;				
				stateBar.setText("Inspector Session Opened.");
			
			}catch (TucsonInvalidTupleCentreIdException e) {
				stateBar.setText("Operation Failed: " + e);
				e.printStackTrace();
			} catch (Exception e) {
				stateBar.setText("Operation Failed: " + e);
				e.printStackTrace();
			}
			
		}else{
			
			specForm.exit();
			agent.quit();
			buttonInspect.setText("inspect");
			disableControlPanel();
			inputName.setEditable(true);
			inputNode.setEditable(true);
			inputPort.setEditable(true);
			isSessionOpen = false;
			stateBar.setText("Inspector Session Closed.");
			
		}
		
	}

	private void buttonNextStepActionPerformed(java.awt.event.ActionEvent evt){
		try{
			context.nextStep();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void checkStepExecutionActionPerformed(java.awt.event.ActionEvent evt){
		try{
			if (checkStepExecution.isSelected()){
				protocol.tracing = true;
				context.setProtocol(protocol);
				buttonNextStep.setEnabled(true);
			}else{
				protocol.tracing = false;
				context.setProtocol(protocol);
				buttonNextStep.setEnabled(false);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void jButton7ActionPerformed(java.awt.event.ActionEvent evt){
		specForm.setVisible(true);
	}

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.reactionsObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			reactionForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.tsetObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			tupleForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void jButton6ActionPerformed(java.awt.event.ActionEvent evt){
		try{
			context.reset();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.pendingQueryObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			pendingQueryForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt){
		if (isSessionOpen){
			try{
				context.exit();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		if (tupleForm != null)
			tupleForm.dispose();
		if (pendingQueryForm != null)
			pendingQueryForm.dispose();
		if (reactionForm != null)
			reactionForm.dispose();
		if (specForm != null)
			specForm.dispose();
		this.dispose();
		synchronized (exit){
			exit.notify();
		}
	}

	private void disableControlPanel(){
		setSize(380, 200);
		validate();
		controlPanel.setVisible(false);
		jPanel2.setVisible(true);
	}

	private void enableControlPanel(){
		setSize(380, 300);
		validate();
		controlPanel.setVisible(true);
		jPanel2.setVisible(false);
	}

	/**
	 * Launch the Inspector tool
	 * 
	 * @param args
	 * 
	 * @throws TucsonGenericException
	 */
	public static void main(String args[]) throws TucsonGenericException{
		
		System.out.println("Booting Inspector ...");
		
		if (alice.util.Tools.isOpt(args, "-?") || alice.util.Tools.isOpt(args, "-help")){
			System.out.println("Argument Template: ");
			System.out.println("{-tcname tuple centre name} {-netid ip address} {-port listening port number} {-aid agent identifier} {-?}");
			System.exit(0);
		}

		String st_aid = null;
		if (alice.util.Tools.isOpt(args, "-aid"))
			st_aid = alice.util.Tools.getOpt(args, "-aid");
		else{
			st_aid = "inspector" + System.currentTimeMillis();
		}
		
		String tcname = null;
		if (alice.util.Tools.isOpt(args, "-tcname"))
			tcname = alice.util.Tools.getOpt(args, "-tcname");
		
		String netid = null;
		if (alice.util.Tools.isOpt(args, "-netid"))
			netid = alice.util.Tools.getOpt(args, "-netid");
		
		String port = null;
		if (alice.util.Tools.isOpt(args, "-port"))
			port = alice.util.Tools.getOpt(args, "-port");

		TucsonAgentId aid = null;
		TucsonTupleCentreId tid = null;
		try{
			aid = new TucsonAgentId(st_aid);
			tid = new TucsonTupleCentreId(tcname, netid, port);
		}catch (TucsonInvalidAgentIdException e){
			System.err.println("[Inspector]: failure: " + e);
			System.exit(-1);
		}catch (TucsonInvalidTupleCentreIdException e){
			System.err.println("[Inspector]: failure: " + e);
			System.exit(-1);
		}
		
		System.out.println("[Inspector]: Boot ended correctly");
		System.out.println("[Inspector]: Inspector Agent Identifier: " + st_aid);
		System.out.println("[Inspector]: Tuple Centre Identifier: " + tid);

		try{
			Inspector form = null;
			if (tid != null)
				form = new Inspector(aid, tid);
			else
				form = new Inspector(aid);
			synchronized (form.exit){
				try{
					form.exit.wait();
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
			System.out.println("[Inspector]: I quit, see you next time :)");
			System.exit(0);
		}catch(InterruptedException e){
			System.err.println("[Inspector]: " + e);
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			System.err.println("[Inspector]: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
