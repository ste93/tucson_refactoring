package it.unibo.sd.jade.agents;

import it.unibo.sd.jade.coordination.TucsonOpResult;
import it.unibo.sd.jade.service.TucsonService;
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
 * TucsonAgentAsync: agente TuCSoN che esegue operazioni di coordinazione in
 * modalità asincrona e gestisce il suo completamento riavviando il behaviour
 * JADE chiamante
 * 
 * @author lucasangiorgi
 * 
 */
public class SynchCompletionBehaviourHandler extends AbstractTucsonAgent {
    private final Behaviour behav; // comportamento da "riavviare" comando
                                   // restart()
    private final GenericCommand cmd; // comando verticale da
                                      // eseguire(esecuzione dell'operazione di
                                      // coordinazione sospensiva)
    private ITucsonOperation result; // risultato operazione dell'operazione di
                                     // coordianzione sospensiva
    private final TucsonOpResult ros; // struttura condivisa dove memorizzare
                                      // il risultato
    private final TucsonService service; // servizio incaricato ad eseguire il
                                         // comando

    /**
     * 
     * @param id
     *            the id of this TuCSoN agent
     * @param r
     *            the data structure storing the operations result
     * @param c
     *            the command to dispatch to the JADE middleware
     * @param s
     *            the TuCSoN service to interact with
     * @param b
     *            the JADE Behaviour to handle
     * @throws TucsonInvalidAgentIdException
     *             if the given String is not a valid representation of a TuCSoN
     *             agent id
     */
    public SynchCompletionBehaviourHandler(final String id,
            final TucsonOpResult r, final GenericCommand c,
            final TucsonService s, final Behaviour b)
            throws TucsonInvalidAgentIdException {
        super(id);
        this.ros = r;
        this.cmd = c;
        this.service = s;
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
        final EnhancedAsynchACC acc = (EnhancedAsynchACC) this.cmd.getParam(1);
        final List<TucsonOpCompletionEvent> list = acc
                .getCompletionEventsList();
        // ricerca del risultato dell'operazione richiesta contenuto nella
        // risposta della chiamata asincrona di oggetto ITucsonOperation
        boolean trovato = false;
        synchronized (list) {
            for (int i = 0; i < list.size() && !trovato; i++) {
                if (list.get(i).getOpId().getId() == this.result.getId()) { // operazione
                                                                            // richiesta
                                                                            // trovata
                    trovato = true;
                    final TucsonOpCompletionEvent ev = list.remove(i); // prelevo
                                                                       // l'operazione
                                                                       // completata
                                                                       // dalla
                                                                       // coda
                                                                       // dei
                                                                       // messaggi
                                                                       // completati
                    this.ros.getTucsonCompletionEvents().add(ev); // salvare il
                                                                  // risultato
                                                                  // nella
                    // struttura dedicata
                    this.ros.setReady(true); // segnalo che al behaviour JADE
                                             // che
                                             // è pronto il risultato
                                             // dell'operazione pendente
                }
            }
        }
        this.behav.restart();
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
            System.err.println(e);
        }
    }
}
