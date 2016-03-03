package it.unibo.mok.inspector.tucson;

import it.unibo.mok.gui.impl.model.mok.MoKCompartment;
import it.unibo.mok.gui.impl.model.mok.MoKMolecule;
import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.GUI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.introspection4gui.Inspector4Gui;
import alice.tucson.introspection4gui.Inspector4GuiObserver;
import alice.tuplecentre.api.Tuple;

public class TucsonInspector implements Executable, Inspector4GuiObserver {

    public static final String NETWORK_FILE = "network.config";

    private static void alert(final String message) {
        JOptionPane.showMessageDialog(null, "<html>" + message + "</html>");
    }

    private ExecutorService executor;
    private GUI gui;
    private List<LinkTransferMonitor> linkMonitors;
    private List<TucsonTupleCentreId> tcIds;
    private List<Inspector4Gui> tcInspectors;

    @Override
    public void onNewTrasfer(final String tccSource, final String tccDest,
            final String tuple) {
        System.out.println("Transfering tuple " + tuple.toString() + " "
                + tccSource + " -> " + tccDest);
        if (this.gui.addLink(tccSource, tccDest)) {
            this.linkMonitors.add(new LinkTransferMonitor(tccSource, tccDest,
                    this.gui));
        }
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                for (final LinkTransferMonitor linkMonitor : TucsonInspector.this.linkMonitors) {
                    if (linkMonitor.getFirst().equals(tccSource) && linkMonitor.getSecond().equals(tccDest)) {
                        linkMonitor.doTransfer(tuple, true);
                        break;
                    } else if (linkMonitor.getFirst().equals(tccDest) && linkMonitor.getSecond().equals(tccSource)) {
                        linkMonitor.doTransfer(tuple, false);
                        break;
                    }
                }
            }
        });

    }

    @Override
    public void onNewTuple(final Tuple tuple, final TucsonTupleCentreId ttc) {
        // System.out.println("Adding tuple: "+tuple.toString());
        final MoKMolecule m = new MoKMolecule(tuple.toString(), 1);
        this.gui.addMolecule(m, ttc.getName());
    }

    @Override
    public void onNewTupleCenter(final TucsonTupleCentreId ttc) {
        if (this.gui.addNode(new MoKCompartment(ttc.getName(), ttc.getNode(),
                ttc.getPort()))) {
            this.initTupleCenterInspector(ttc);
        }
    }

    @Override
    public void onRemovedTuple(final Tuple tupleRemoved,
            final TucsonTupleCentreId ttc) {
        // System.out.println("Removing tuple: "+tupleRemoved.toString());
        this.gui.setMoleculeConcentration(tupleRemoved.toString(),
                ttc.getName(), 0);
    }

    @Override
    public void run() {
        for (final TucsonTupleCentreId tcId : this.tcIds) {
            this.onNewTupleCenter(tcId);
        }
    }

    @Override
    public void setGUI(final GUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean setup() {
        this.tcIds = new ArrayList<>();
        this.tcInspectors = new ArrayList<>();
        this.linkMonitors = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() + 1);
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(
                TucsonInspector.NETWORK_FILE))) {
            while ((line = br.readLine()) != null) {
                final String[] token = line.split(" @ ");
                final String[] address = token[1].split(" : ");
                final TucsonTupleCentreId id = new TucsonTupleCentreId(
                        token[0], address[0], address[1]);
                // Check for errors
                id.getName();
                id.getNode();
                id.getPort();
                this.tcIds.add(id);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            if (line != null) {
                TucsonInspector.alert("Error while parsing "
                        + TucsonInspector.NETWORK_FILE + " in line:<br><br><b>"
                        + line + "</b><br>&nbsp;");
            } else {
                TucsonInspector.alert("Error while parsing "
                        + TucsonInspector.NETWORK_FILE);
            }
            return false;
        }
    }

    @Override
    public void stop() {
        for (final Inspector4Gui inspector4Gui : this.tcInspectors) {
            inspector4Gui.quit();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    private void initTupleCenterInspector(final TucsonTupleCentreId tcId) {
        try {
            final Inspector4Gui inspector4Gui = new Inspector4Gui(tcId);
            inspector4Gui.addOberver(this);
            this.tcInspectors.add(inspector4Gui);
            inspector4Gui.start();
        } catch (final Exception e) {
            e.printStackTrace();
            TucsonInspector.alert("Inspector4Gui cannot connect to:<br><br><b>"
                    + tcId.getName() + " @ " + tcId.getNode() + " : "
                    + tcId.getPort() + "</b><br>&nbsp;");
        }
    }

	@Override
	public void setFilter(final String filter) {
		LogicTuple t = null;   
		try {
			if (filter != null) {
				t = LogicTuple.parse(filter);
			}
			if (tcInspectors != null) {
				for (Inspector4Gui insp : tcInspectors) {
					insp.setFilter(t);
				}
			}
		} catch (final InvalidLogicTupleException e) {
			e.printStackTrace();
		}
	}

}
