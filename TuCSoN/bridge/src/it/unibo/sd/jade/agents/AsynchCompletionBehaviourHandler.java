package it.unibo.sd.jade.agents;

import it.unibo.sd.jade.coordination.IAsynchCompletionBehaviour;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.GenericCommand;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import java.util.List;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * TucsonAgentAsyncBehavoiur:agente TuCSoN che esegue operazioni di
 * coordinazione in modalità asincrona e gestisce il suo completamento avviando
 * un nuovo behaviour JADE predefinito
 * 
 * @author lucasangiorgi
 * 
 */
public class AsynchCompletionBehaviourHandler extends AbstractTucsonAgent {
    private final Behaviour behav; // nuovo comportamento da aggiungere
                                   // all'agente JADE
    private final GenericCommand cmd; // comando verticale da
                                      // eseguire(esecuzione dell'operazione di
                                      // coordinazione sospensiva)
    private final Agent myAgent; // agente JADE a cui aggiungere il
                                 // comportamento
    private ITucsonOperation result; // risultato operazione dell'operazione di
                                     // coordianzione sospensiva
    private final TucsonService service; // servizio incaricato ad eseguire il
                                         // comando

    /**
     * 
     * @param id
     *            the id of this TuCSoN agent
     * @param c
     *            the command to dispatch to the JADE middleware
     * @param s
     *            the TuCSoN service to interact with
     * @param a
     *            the JADE agent this TuCSoN agent represents
     * @param b
     *            the JADE Behaviour to handle
     * @throws TucsonInvalidAgentIdException
     *             if the given String is not a valid representation of a TuCSoN
     *             agent id
     */
    public AsynchCompletionBehaviourHandler(final String id,
            final GenericCommand c, final TucsonService s, final Agent a,
            final Behaviour b) throws TucsonInvalidAgentIdException {
        super(id);
        this.cmd = c;
        this.service = s;
        this.myAgent = a;
        this.behav = b;
        this.result = null;
    }

    /*
     * metodo utilizzato solo dalle chiamate asincrone (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tuplecentre
     * .core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        // TODO Auto-generated method stub
        System.out
                .println("\n\n\n[TucsonAgent]RISULTATO RICEVUTO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        // ros.list.add((ITucsonOperation)result);
        final EnhancedAsynchACC acc = (EnhancedAsynchACC) this.cmd.getParam(1);
        final List<TucsonOpCompletionEvent> list = acc
                .getCompletionEventsList();
        TucsonOpCompletionEvent ev = null;
        // ricerca del risultato dell'operazione richiesta contenuto nella
        // risposta della chiamata asincrona di oggetto ITucsonOperation
        boolean trovato = false;
        synchronized (list) {
            for (int i = 0; i < list.size() && !trovato; i++) {
                if (list.get(i).getOpId().getId() == this.result.getId()) { // operazione
                                                                            // richiesta
                                                                            // trovata
                    trovato = true;
                    ev = list.remove(i); // prelevo l'operazione completata
                                         // dalla coda dei messaggi completati
                }
            }
        }
        // controllo se il behaviour passato rispetta il contratto richiesto
        if (this.behav instanceof IAsynchCompletionBehaviour) {
            final IAsynchCompletionBehaviour b = (IAsynchCompletionBehaviour) this.behav;
            b.setTucsonOpCompletionEvent(ev);
            this.myAgent.addBehaviour(this.behav); // non ci sono problemi
                                                   // accessi concorrenti!å
        } else {
            System.err
                    .println("behaviour non implementa interfaccia \"ICompletitionOpAsync\"");
        }
    }

    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        /*
         * not used atm
         */
    }

    @Override
    protected void main() {
        this.cmd.addParam(this); // listener aggiunto dopo aver creato l'oggetto
                                 // che fa da listener
        try {
            this.result = (ITucsonOperation) this.service.submit(this.cmd); // esecuzione
                                                                            // operazione
                                                                            // di
                                                                            // coordinazione
        } catch (final ServiceException e) {
            e.printStackTrace();
        }
    }
}
