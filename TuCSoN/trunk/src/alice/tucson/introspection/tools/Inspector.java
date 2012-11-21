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

import java.awt.Color;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

import alice.tucson.introspection.InspectorContext;
import alice.tucson.introspection.InspectorProtocol;

/**
 * 
 * 
 * @author s.mariani@unibo.it
 */
public class Inspector extends javax.swing.JFrame{
	
	private static final long serialVersionUID = -3765811664087552414L;
	/**
	 * Package-visible reference to the Inspector core machinery.
	 */
	protected InspectorCore agent;
	private InspectorContext context;
	private TupleViewer tupleForm;
	private EventViewer pendingQueryForm;
	private ReactionViewer reactionForm;
	private EditSpec specForm;
	/**
	 * Package-visible reference to the Inspector session.
	 */
	protected InspectorProtocol protocol = new InspectorProtocol();
	private Object exit = new Object();
	private TucsonAgentId aid;
	/**
	 * Package-visible reference to the inspected tuplecentre.
	 */
	protected TucsonTupleCentreId tid;
	private boolean isSessionOpen;
	
//	private boolean loggingTuples = false;
//	private String logTupleFilename;
//	private FileWriter logTupleWriter;
//	private LogicTuple logTupleFilter;
//
//	private boolean loggingQueries = false;
//	private String logQueryFilename;
//	private FileWriter logQueryWriter;
//
//	private boolean loggingReactions = false;
//	private String logReactionFilename;
//	private FileWriter logReactionWriter;

	/*
	 * GUI stuff.
	 */
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel vmPanel;
	private javax.swing.JButton specBtn;
	private javax.swing.JButton resetVMBtn;
	private javax.swing.JPanel respectSetsPanel;
	private javax.swing.JButton reactionsBtn;
	private javax.swing.JPanel imgPanel;
	private javax.swing.JButton tuplesBtn;
	private javax.swing.JPanel chooseTC;
	private javax.swing.JButton pendingBtn;
	private javax.swing.JTextField inputName;
	private javax.swing.JTextField inputNode;
	private javax.swing.JTextField inputPort;
	private javax.swing.JTabbedPane controlPanel;
	private javax.swing.JLabel portno;
	private javax.swing.JLabel netid;
	private javax.swing.JLabel tname;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JCheckBox checkStepExecution;
	private javax.swing.JButton buttonNextStep;
	private javax.swing.JTextField stateBar;
	private javax.swing.JButton buttonInspect;

	/**
	 * Called when no default tuplecentre to monitor is given.
	 * 
	 * @param aid the name of the Inspector agent.
	 * 
	 * @throws Exception
	 */
	public Inspector(TucsonAgentId aid) throws Exception{
		initComponents();
		this.aid = aid;
		disableControlPanel();
		isSessionOpen = false;
		this.setVisible(true);		
	}

	/**
	 * Called when a default tuplecentre to inspect is given.
	 * 
	 * @param aid the name of the Inspector agent.
	 * @param tid the fullname of the tuplecentre to inspect.
	 * 
	 * @throws Exception
	 */
	public Inspector(TucsonAgentId aid, TucsonTupleCentreId tid) throws Exception{
		this(aid);
		this.tid = tid;
		inputName.setText(tid.getName());
		inputNode.setText(alice.util.Tools.removeApices(tid.getNode()));
		inputPort.setText(alice.util.Tools.removeApices(""+tid.getPort()));
		buttonInspectActionPerformed(null);
	}

	/*
	 * Forms getter.
	 */
	protected TupleViewer getTupleForm(){
		return tupleForm;
	}

	protected EventViewer getQueryForm(){
		return pendingQueryForm;
	}

	protected ReactionViewer getReactionForm(){
		return reactionForm;
	}

	/*
	 * 'On-exit' callbacks.
	 */
	protected void onTupleViewerExit(){
		try{
			protocol.tsetObservType = InspectorProtocol.NO_OBSERVATION;
			context.setProtocol(protocol);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void onReactionViewerExit(){
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

	/**
	 * GUI set up.
	 */
	private void initComponents(){
		
		java.awt.GridBagConstraints gridBagConstraints;

		controlPanel = new javax.swing.JTabbedPane();
		respectSetsPanel = new javax.swing.JPanel();
		tuplesBtn = new javax.swing.JButton();
		pendingBtn = new javax.swing.JButton();
		reactionsBtn = new javax.swing.JButton();
		specBtn = new javax.swing.JButton();
		vmPanel = new javax.swing.JPanel();
		resetVMBtn = new javax.swing.JButton();
		jPanel7 = new javax.swing.JPanel();
		checkStepExecution = new javax.swing.JCheckBox();
		buttonNextStep = new javax.swing.JButton();
		chooseTC = new javax.swing.JPanel();
		tname = new javax.swing.JLabel();
		netid = new javax.swing.JLabel();
		portno = new javax.swing.JLabel();
		inputName = new javax.swing.JTextField();
		inputNode = new javax.swing.JTextField();
		inputPort = new javax.swing.JTextField();
		buttonInspect = new javax.swing.JButton();
		imgPanel = new javax.swing.JPanel();
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
		controlPanel.setMaximumSize(new java.awt.Dimension(360, 120));
		controlPanel.setMinimumSize(new java.awt.Dimension(360, 120));
		controlPanel.setPreferredSize(new java.awt.Dimension(360, 120));
		
		respectSetsPanel.setLayout(new java.awt.GridBagLayout());
		respectSetsPanel.setToolTipText("Now inspecting TuCSoN tuplecentre sets");
		respectSetsPanel.setMinimumSize(new java.awt.Dimension(240, 100));
		respectSetsPanel.setPreferredSize(new java.awt.Dimension(240, 100));
		
		tuplesBtn.setFont(new java.awt.Font("Arial", 0, 11));
		tuplesBtn.setText("Tuple Space");
		tuplesBtn.setToolTipText("Inspect logic tuples set");
		tuplesBtn.setMaximumSize(new java.awt.Dimension(130, 25));
		tuplesBtn.setMinimumSize(new java.awt.Dimension(130, 25));
		tuplesBtn.setPreferredSize(new java.awt.Dimension(130, 25));
		tuplesBtn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				tuplesBtnActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		respectSetsPanel.add(tuplesBtn, gridBagConstraints);

		pendingBtn.setFont(new java.awt.Font("Arial", 0, 11));
		pendingBtn.setText("Pending Ops");
		pendingBtn.setToolTipText("Inspect pending TuCSoN operations set");
		pendingBtn.setMaximumSize(new java.awt.Dimension(130, 25));
		pendingBtn.setMinimumSize(new java.awt.Dimension(130, 25));
		pendingBtn.setPreferredSize(new java.awt.Dimension(130, 25));
		pendingBtn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				pendingBtnActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		respectSetsPanel.add(pendingBtn, gridBagConstraints);

		reactionsBtn.setFont(new java.awt.Font("Arial", 0, 11));
		reactionsBtn.setText("ReSpecT Reactions");
		reactionsBtn.setToolTipText("Inspect triggered ReSpecT rections set");
		reactionsBtn.setMaximumSize(new java.awt.Dimension(130, 25));
		reactionsBtn.setMinimumSize(new java.awt.Dimension(130, 25));
		reactionsBtn.setPreferredSize(new java.awt.Dimension(130, 25));
		reactionsBtn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				trigReactsBtnActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 30.0;
		gridBagConstraints.weighty = 100.0;
		respectSetsPanel.add(reactionsBtn, gridBagConstraints);

		specBtn.setFont(new java.awt.Font("Arial", 0, 11));
		specBtn.setText("Specification Space");
		specBtn.setToolTipText("Inspect ReSpecT specification tuples set");
		specBtn.setMaximumSize(new java.awt.Dimension(130, 25));
		specBtn.setMinimumSize(new java.awt.Dimension(130, 25));
		specBtn.setPreferredSize(new java.awt.Dimension(130, 25));
		specBtn.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				specBtnActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 50.0;
		gridBagConstraints.weighty = 100.0;
		respectSetsPanel.add(specBtn, gridBagConstraints);

		controlPanel.addTab("Sets", null, respectSetsPanel, "Now inspecting TuCSoN tuplecentre sets");

//		vmPanel.setLayout(new java.awt.GridBagLayout());
//		vmPanel.setToolTipText("Controlling Tuple Centre Virtual Machine");
//		vmPanel.setMinimumSize(new java.awt.Dimension(240, 100));
//		vmPanel.setPreferredSize(new java.awt.Dimension(240, 100));
//		resetVMBtn.setFont(new java.awt.Font("Arial", 0, 11));
//		resetVMBtn.setText("reset");
//		resetVMBtn.addActionListener(new java.awt.event.ActionListener(){
//			public void actionPerformed(java.awt.event.ActionEvent evt){
//				resetVMBtnActionPerformed(evt);
//			}
//		});
//
//		gridBagConstraints = new java.awt.GridBagConstraints();
//		gridBagConstraints.gridx = 2;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.weightx = 30.0;
//		vmPanel.add(resetVMBtn, gridBagConstraints);
//
//		jPanel7.setLayout(new java.awt.GridBagLayout());
//		jPanel7.setBorder(new javax.swing.border.TitledBorder("trace"));
//		jPanel7.setFont(new java.awt.Font("Arial", 0, 11));
//		jPanel7.setMinimumSize(new java.awt.Dimension(161, 100));
//		jPanel7.setPreferredSize(new java.awt.Dimension(161, 100));
//
//		checkStepExecution.setFont(new java.awt.Font("Arial", 0, 11));
//		checkStepExecution.setText("step execution");
//		checkStepExecution.addActionListener(new java.awt.event.ActionListener(){
//			public void actionPerformed(java.awt.event.ActionEvent evt){
//				checkStepExecutionActionPerformed(evt);
//			}
//		});
//
//		gridBagConstraints = new java.awt.GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.weightx = 20.0;
//		jPanel7.add(checkStepExecution, gridBagConstraints);
//
//		buttonNextStep.setFont(new java.awt.Font("Arial", 0, 11));
//		buttonNextStep.setText("next");
//		buttonNextStep.addActionListener(new java.awt.event.ActionListener(){
//			public void actionPerformed(java.awt.event.ActionEvent evt){
//				buttonNextStepActionPerformed(evt);
//			}
//		});
//
//		gridBagConstraints = new java.awt.GridBagConstraints();
//		gridBagConstraints.gridx = 2;
//		gridBagConstraints.gridy = 1;
//		jPanel7.add(buttonNextStep, gridBagConstraints);
//
//		gridBagConstraints = new java.awt.GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
//		gridBagConstraints.weightx = 70.0;
//		gridBagConstraints.weighty = 100.0;
//		vmPanel.add(jPanel7, gridBagConstraints);
//
//		controlPanel.addTab("Virtual Machine", null, vmPanel, "Controlling Tuple Centre Virtual Machine");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(controlPanel, gridBagConstraints);
		chooseTC.setLayout(new java.awt.GridBagLayout());

		chooseTC.setBorder(new javax.swing.border.TitledBorder(null, " Input tuplecentre to inspect: ",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 12)));
		chooseTC.setMaximumSize(new java.awt.Dimension(360, 160));
		chooseTC.setMinimumSize(new java.awt.Dimension(360, 160));
		chooseTC.setPreferredSize(new java.awt.Dimension(360, 160));
		
		tname.setText("tname");
		tname.setMaximumSize(new java.awt.Dimension(60, 20));
		tname.setMinimumSize(new java.awt.Dimension(60, 20));
		tname.setPreferredSize(new java.awt.Dimension(60, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(tname, gridBagConstraints);

		netid.setText("@netid");
		netid.setMaximumSize(new java.awt.Dimension(60, 20));
		netid.setMinimumSize(new java.awt.Dimension(60, 20));
		netid.setPreferredSize(new java.awt.Dimension(60, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(netid, gridBagConstraints);
		
		portno.setText(":portno");
		portno.setMaximumSize(new java.awt.Dimension(60, 20));
		portno.setMinimumSize(new java.awt.Dimension(60, 20));
		portno.setPreferredSize(new java.awt.Dimension(60, 20));
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(portno, gridBagConstraints);

		inputName.setText("default");
		inputName.setToolTipText("tuple centre name");
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(inputName, gridBagConstraints);

		inputNode.setText("localhost");
		inputNode.setToolTipText("IP address of the TuCSoN Node");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(inputNode, gridBagConstraints);

		inputPort.setText("20504");
		inputPort.setToolTipText("listening port of the TuCSoN Node");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		chooseTC.add(inputPort, gridBagConstraints);

		buttonInspect.setFont(new java.awt.Font("Arial", 0, 11));
		buttonInspect.setText("Inspect!");
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
		chooseTC.add(buttonInspect, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(chooseTC, gridBagConstraints);

		imgPanel.setLayout(new java.awt.GridBagLayout());

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/alice/tucson/images/logo.gif")));
		jLabel1.setPreferredSize(new java.awt.Dimension(90, 140));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		imgPanel.add(jLabel1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		getContentPane().add(imgPanel, gridBagConstraints);

		stateBar.setBackground(Color.CYAN);
		stateBar.setEditable(false);
		stateBar.setText("Waiting user input...");
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

				buttonInspect.setText("Quit");				
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
			buttonInspect.setText("Inspect!");
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

	/**
	 * Handles 'edit specification space' button.
	 * 
	 * @param evt 'specification' button pushing event.
	 */
	private void specBtnActionPerformed(java.awt.event.ActionEvent evt){
		specForm.setVisible(true);
	}

	/**
	 * Handles 'inspect triggered reactions' button.
	 * 
	 * @param evt 'reaction' button pushing event.
	 */
	private void trigReactsBtnActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.reactionsObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			reactionForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Handles 'inspect tuples' button.
	 * 
	 * @param evt 'tuples' button pushing event.
	 */
	private void tuplesBtnActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.tsetObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			tupleForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Handles 'reset VM' button.
	 * 
	 * @param evt 'reset' button pushing event.
	 */
	private void resetVMBtnActionPerformed(java.awt.event.ActionEvent evt){
		try{
			context.reset();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Handles 'pending query' button.
	 * 
	 * @param evt 'pending' button pushing event.
	 */
	private void pendingBtnActionPerformed(java.awt.event.ActionEvent evt){
		try{
			protocol.pendingQueryObservType = InspectorProtocol.PROACTIVE_OBSERVATION;
			context.setProtocol(protocol);
			pendingQueryForm.setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Close all open forms on exit (Inspector itself too).
	 * 
	 * @param evt closing window event.
	 */
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

	/**
	 * Since the tuplecentre to inspect is not chosen, the control panel is not
	 * showed.
	 */
	private void disableControlPanel(){
		setSize(380, 200);
		validate();
		chooseTC.setBorder(new javax.swing.border.TitledBorder(null, " Input tuplecentre to inspect: ",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 12)));
		controlPanel.setVisible(false);
		imgPanel.setVisible(true);
	}

	/**
	 * Once tuplecentre to inspect is chosen, the control panel appears.
	 */
	private void enableControlPanel(){
		setSize(380, 300);
		validate();
		chooseTC.setBorder(new javax.swing.border.TitledBorder(null, " Now inspecting tuplecentre: ",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 12)));
		controlPanel.setVisible(true);
		imgPanel.setVisible(false);
	}

	/**
	 * 
	 * 
	 * @param args
	 * 
	 * @throws TucsonGenericException
	 */
	public static void main(String args[]) throws TucsonGenericException{
		
		System.out.println("[Inspector]: Booting...");
		
		if (alice.util.Tools.isOpt(args, "-?") || alice.util.Tools.isOpt(args, "-help")){
			System.out.println("Argument Template: ");
			System.out.println("{-tname tuple centre name} {-netid ip address} {-portno listening port number} {-aid agent identifier} {-?}");
			System.exit(0);
		}

		String st_aid = null;
		if (alice.util.Tools.isOpt(args, "-aid"))
			st_aid = alice.util.Tools.getOpt(args, "-aid");
		else
			st_aid = "inspector" + System.currentTimeMillis();
//		String tcname = "default";
		String tcname = "";
		if (alice.util.Tools.isOpt(args, "-tcname"))
			tcname = alice.util.Tools.getOpt(args, "-tcname");
//		String netid = "localhost";
		String netid = "";
		if (alice.util.Tools.isOpt(args, "-netid"))
			netid = alice.util.Tools.getOpt(args, "-netid");
//		String port = "20504";
		String port = "";
		if (alice.util.Tools.isOpt(args, "-portno"))
			port = alice.util.Tools.getOpt(args, "-portno");

		TucsonAgentId aid = null;
		TucsonTupleCentreId tid = null;
		try{
			aid = new TucsonAgentId(st_aid);
			tid = new TucsonTupleCentreId(tcname, netid, port);
			System.out.println("[Inspector]: Inspector Agent Identifier: " + st_aid);
			System.out.println("[Inspector]: Tuple Centre Identifier: " + tid);
		}catch (TucsonInvalidAgentIdException e){
			System.err.println("[Inspector]: failure: " + e);
			System.exit(-1);
		}catch (TucsonInvalidTupleCentreIdException e){
//			System.err.println("[Inspector]: failure: " + e);
//			System.exit(-1);
			System.out.println("[Inspector]: Please input an admissible tuplecentre " +
					"name from the GUI...");
		}

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
