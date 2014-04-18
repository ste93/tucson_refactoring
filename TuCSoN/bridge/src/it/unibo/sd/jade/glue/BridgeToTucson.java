package it.unibo.sd.jade.glue;

import it.unibo.sd.jade.agents.AsynchCompletionBehaviourHandler;
import it.unibo.sd.jade.agents.SynchCompletionBehaviourHandler;
import it.unibo.sd.jade.coordination.AsynchTucsonOpResult;
import it.unibo.sd.jade.coordination.TucsonOpResult;
import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.bulk.OutAll;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.operations.ordinary.Set;
import it.unibo.sd.jade.operations.ordinary.Spawn;
import it.unibo.sd.jade.operations.specification.OutS;
import it.unibo.sd.jade.operations.specification.SetS;
import it.unibo.sd.jade.service.TucsonService;
import it.unibo.sd.jade.service.TucsonSlice;
import jade.core.Agent;
import jade.core.GenericCommand;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * classe che permette di comunicare dal "mondo" JADE al "mondo" TuCSoN
 * 
 * @author lucasangiorgi
 * 
 */
public class BridgeToTucson {
    private static void log(final String msg) {
        System.out.println("\n[BRIDGE]: " + msg);
    }

    /**
     * metodo utilizzato per poter uniformare il tipo di ritorno delle
     * operazioni di coordinazione eseguite dall'agente jade
     * 
     * @param op
     * @return
     */
    private static TucsonOpCompletionEvent toTucsonCompletionEvent(
            final ITucsonOperation op) {
        TucsonOpCompletionEvent ev;
        if (op.isInAll() // caso in cui ricevo una lista di tuple
                || op.isRdAll() || op.isGet() || op.isGetS() || op.isNoAll()) {
            ev = new TucsonOpCompletionEvent(new TucsonOpId(op.getId()), true,
                    op.isOperationCompleted(), op.isResultSuccess(),
                    op.getLogicTupleListResult());
        } else { // caso in cui ricevo una tupla come risultato
            ev = new TucsonOpCompletionEvent(new TucsonOpId(op.getId()), true,
                    op.isOperationCompleted(), op.isResultSuccess(),
                    op.getLogicTupleResult());
        }
        return ev;
    }

    private final EnhancedACC acc;
    private final TucsonService service;
    private final Map<Behaviour, TucsonOpResult> tucsonOpResultsMap;

    /**
     * 
     * @param a
     *            the ACC the JADE bridge needs to interact with TuCSoN service
     * @param s
     *            the TuCSoN service this bridge is interacting with
     */
    public BridgeToTucson(final EnhancedACC a, final TucsonService s) {
        this.acc = a;
        this.service = s;
        this.tucsonOpResultsMap = new HashMap<Behaviour, TucsonOpResult>();
    }

    /**
     * metodo asyncrono dove il programmatore controlla se il risultato è pronto
     * a runtime (controllo a polling)
     * 
     * @param action
     *            the TuCSoN action to be carried out
     * @return struttura che permette di controllare se il risultato è stato
     *         ricevuto o è ancora pendente
     * @throws ServiceException
     *             if the service cannot be contacted
     */
    public AsynchTucsonOpResult asynchronousInvocation(
            final AbstractTucsonAction action) throws ServiceException {
        // Creo il comando verticale
        // chiamata asincrona senza settare il listener perchè il controllo del
        // completamento dell'operazione è a carico del programmatore tramite
        // la struttura ritornata AsyncOpResultData
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        cmd.addParam(null);
        final Object result = this.service.submit(cmd);
        if (result instanceof ITucsonOperation) {
            final ITucsonOperation op = (ITucsonOperation) result;
            // si deve ritornare un oggetto che contiene la lista dove si andrà
            // a
            // controllare se l'operazione è completata, e l'id della op
            final AsynchTucsonOpResult asyncData = new AsynchTucsonOpResult(
                    op.getId(), this.acc.getCompletionEventsList(),
                    this.acc.getPendingOperationsMap());
            return asyncData;
        }
        return null;
    }

    /**
     * metodo asyncrono dove il programmatore crea precedentemente il behaviour
     * che verrà attivato nell'agente quando il risultato è pronto (controllo a
     * interrupt)
     * 
     * @param action
     *            the TuCSoN action to be carried out
     * @param behav
     *            the JADE behaviour handling the rusult
     * @param myAgent
     *            the JADE agent responsible for execution of the behaviour
     */
    public void asynchronousInvocation(final AbstractTucsonAction action,
            final Behaviour behav, final Agent myAgent) {
        // Creo il comando verticale
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        try {
            new AsynchCompletionBehaviourHandler("tucsonAgentAsync", cmd,
                    this.service, myAgent, behav).go();
        } catch (final TucsonInvalidAgentIdException e) {
            /*
             * cannot really happen
             */
        }
    }

    /**
     * metodo utilizzato per eliminare la struttura condivisa non più utilizzata
     * (il metodo deve essere chiamato appena prima della terminazione del
     * behaviour) o quando si vuole chiamare una stessa operazione in un
     * cyclebehaviour perchè altrimenti questa operazione verrebbe eseguita una
     * sola volta e restituirebbe sempre lo stesso risultato anche se la si
     * richiama più volte
     * 
     * @param b
     *            behaviour tramite il quale si identifica la struttura
     *            condivisa associata al behaviour (behaviour da cui si sono
     *            invocate le operazioni di cooridnazione)
     */
    public void clearTucsonOpResult(final Behaviour b) {
        this.tucsonOpResultsMap.remove(b);
    }

    /**
     * esecuzione operazioni di coordinazione sospensive in modalità sincrona
     * 
     * @param action
     *            the TuCSoN action to be carried out
     * @param timeout
     *            the maximum waiting time for completion reception
     * @param behav
     *            comportamento che invoca la chiamata
     * @return the TuCSoN operation completion event
     * @throws ServiceException
     *             if the service cannot be contacted
     */
    public TucsonOpCompletionEvent synchronousInvocation(
            final AbstractTucsonAction action, final Long timeout,
            final Behaviour behav) throws ServiceException {
        TucsonOpResult ros;
        if (this.tucsonOpResultsMap.get(behav) == null) { // ottengo o creo il
            // gestore di
            // memorizzazione dei
            // risultati delle
            // operazioni di
            // coordinazione
            ros = new TucsonOpResult();
            this.tucsonOpResultsMap.put(behav, ros);
        } else {
            ros = this.tucsonOpResultsMap.get(behav);
            if (!ros.isReady()) {
                BridgeToTucson
                        .log("\n\n comportamento sbloccato da messaggio ricevuto\n\n");
                return null; // il comportamento è stato sbloccato da un
                             // messaggio ricevuto dall'agente e non da un
                             // restart del thread delegato a questo.
            }
        }
        // controllo se l'op è già stata eseguita e ritorna subito il risultato
        // memorizzato in mResultOpBehaviour
        final List<TucsonOpCompletionEvent> list = ros
                .getTucsonCompletionEvents();
        int nextRes = ros.getNextRes();
        if (list.size() > nextRes) { // sono già presenti dei risultati vecchi
            ros.setNextRes(++nextRes); // si incrementa la prossima
                                       // operazione che si deve eseguire
            return ros.getTucsonCompletionEvents().get(nextRes - 1);
        }
        // è la prima volta che si esegue l'operazione
        // controllo se l'operazione richiesta ha bisogno di attendere la
        // risposta
        if (action instanceof Out || action instanceof OutS
                || action instanceof OutAll || action instanceof Spawn
                || action instanceof Set || action instanceof SetS) {
            // Creo il comando verticale
            final GenericCommand cmd = new GenericCommand(
                    TucsonSlice.EXECUTE_SYNCH, TucsonService.NAME, null);
            cmd.addParam(action);
            cmd.addParam(this.acc);
            cmd.addParam(timeout);
            Object result;
            result = this.service.submit(cmd); // eseguo comando
                                               // verticale
            final ITucsonOperation op = (ITucsonOperation) result;
            // creo il TucsonOpCompletionEvent per salvarlo nella struttura
            // condivisa
            // utilizzato della funzione fromITucsonOpToTucsonOpComp perchè
            // i risultati vengono restituiti rispetto al contratto
            // ITucsonOperation
            final TucsonOpCompletionEvent res = BridgeToTucson
                    .toTucsonCompletionEvent(op);
            // aggiorno struttura condivisa
            ros.getTucsonCompletionEvents().add(res);
            ros.setNextRes(ros.getNextRes() + 1);
            ros.setReady(true);
            return res;
        }
        // operazioni che necessitano di sospendere il behaviour
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        SynchCompletionBehaviourHandler ta = null;
        try {
            ta = new SynchCompletionBehaviourHandler("tucsonAgentAsync", ros,
                    cmd, this.service, behav);
        } catch (final TucsonInvalidAgentIdException e) {
            /*
             * cannot really happen
             */
        }
        ros.setNextRes(0); // setto a 0 perchè l'agente si bloccherà e
                           // quando verrà riavviato ricomincerà da capo
                           // l'esecuzione
        ros.setReady(false);
        ta.go(); // avvio dell'agente tucson incaricato ad eseguire l'op
                 // e gestire il risultato
        return null; // se si ritorna null vuol dire che il risultato non è
                     // pronto e di conseguenza si adotta il protocollo di
                     // sospensione e riattivazione del behaviour
    }
}
