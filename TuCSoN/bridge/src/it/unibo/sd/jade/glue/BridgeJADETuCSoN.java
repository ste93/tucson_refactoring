package it.unibo.sd.jade.glue;

import it.unibo.sd.jade.agents.TucsonAgentAsync;
import it.unibo.sd.jade.agents.TucsonAgentAsyncBehavoiur;
import it.unibo.sd.jade.coordination.AsyncOpResultData;
import it.unibo.sd.jade.coordination.ResultOpStorage;
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
public class BridgeJADETuCSoN {

    /**
     * metodo utilizzato per poter uniformare il tipo di ritorno delle
     * operazioni di coordinazione eseguite dall'agente jade
     * 
     * @param op
     * @return
     */
    private static TucsonOpCompletionEvent fromITucsonOpToTucsonOpComp(
            final ITucsonOperation op) {
        TucsonOpCompletionEvent ev;
        if (op.isInAll() // caso in cui ricevo una lista di tuple
                || op.isRdAll() || op.isGet() || op.isGetS() || op.isNoAll()) {
            ev =
                    new TucsonOpCompletionEvent(new TucsonOpId(op.getId()),
                            true, op.isOperationCompleted(),
                            op.isResultSuccess(), op.getLogicTupleListResult());
        } else { // caso in cui ricevo una tupla come risultato
            ev =
                    new TucsonOpCompletionEvent(new TucsonOpId(op.getId()),
                            true, op.isOperationCompleted(),
                            op.isResultSuccess(), op.getLogicTupleResult());
        }
        return ev;
    }

    private static void log(final String msg) {
        System.out.println("\n[BRIDGE]: " + msg);
    }

    private final EnhancedACC acc;

    private final Map<Behaviour, ResultOpStorage> mResultOpBehaviour;

    private final TucsonService service;

    /**
     * 
     * @param a
     *            the ACC this bridge should use
     * @param s
     *            the JADE service this bridge is made for
     */
    public BridgeJADETuCSoN(final EnhancedACC a, final TucsonService s) {
        this.acc = a;
        this.service = s;
        this.mResultOpBehaviour = new HashMap<Behaviour, ResultOpStorage>();
    }

    /**
     * metodo utilizzato per eliminare la struttura condivisa non più utilizzata
     * (il metodo deve essere chiamato appena prima della terminazione del
     * behaviour)
     * 
     * @param b
     *            behaviour tramite il quale si identifica la struttura
     *            condivisa associata al behaviour
     */
    public void cleanCoordinationStructure(final Behaviour b) {
        this.mResultOpBehaviour.remove(b);
    }

    /**
     * metodo asyncrono dove il programmatore controlla se il risultato è pronto
     * a runtime
     * 
     * @param action
     *            the TuCSoN action to be asynchronously executed
     * @return struttura che permette di controllare se il risultato è stato
     *         ricevuto o è ancora pendente
     * @throws ServiceException
     *             if the JADE service (TuCSoN node service) cannot be contacted
     */
    public AsyncOpResultData executeAsynch(final AbstractTucsonAction action)
            throws ServiceException {
        // Creo il comando verticale
        // chiamata asincrona senza settare il listener perchè il controllo del
        // completamento dell'operazione è a carico del programmatore tramite
        // la struttura ritornata AsyncOpResultData
        final GenericCommand cmd =
                new GenericCommand(TucsonSlice.EXECUTE_ASYNCH,
                        TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        cmd.addParam(null);
        final Object result = this.service.submit(cmd);
        final ITucsonOperation op = (ITucsonOperation) result;
        // si deve ritornare un oggetto che contiene la lista dove si andrà a
        // controllare se l'operazione è completata, e l'id della op
        final AsyncOpResultData asyncData =
                new AsyncOpResultData(op.getId(),
                        this.acc.getCompletionEventsList(),
                        this.acc.getPendingOperationsMap());
        return asyncData;
    }

    /**
     * metodo asyncrono dove il programmatore crea precedentemente il behaviour
     * che verrà attivato nell'agente quando il risultato è pronto
     * 
     * @param action
     *            the TuCSoN action to be asynchronously executed
     * @param behav
     *            the JADE behaviour to be used to handle the operation result
     * @param myAgent
     *            the JADE agent responsible for the chosen behaviour execution
     */
    public void executeAsynch(final AbstractTucsonAction action,
            final Behaviour behav, final Agent myAgent) {
        // Creo il comando verticale
        final GenericCommand cmd =
                new GenericCommand(TucsonSlice.EXECUTE_ASYNCH,
                        TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        try {
            new TucsonAgentAsyncBehavoiur("tucsonAgentAsync", cmd,
                    this.service, myAgent, behav).go();
        } catch (final TucsonInvalidAgentIdException e) {
            /*
             * cannot really happen
             */
        }
    }

    /**
     * esecuzione operazioni di coordinazione sospensive in modalità sincrona
     * eseguite direttamente dall'agente
     * 
     * @param action
     *            the TuCSoN action to be asynchronously executed
     * @param timeout
     *            the maximum timeout the JADE agent is willing to wait for
     *            operation completion
     * @throws ServiceException
     *             if the JADE service (TuCSoN node service) cannot be contacted
     */
    public void executeSynch(final AbstractTucsonAction action,
            final Long timeout) throws ServiceException {
        // Creo il comando verticale
        final GenericCommand cmd =
                new GenericCommand(TucsonSlice.EXECUTE_SYNCH,
                        TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        cmd.addParam(timeout);
        this.service.submit(cmd); // eseguo comando verticale
    }

    /**
     * esecuzione operazioni di coordinazione sospensive in modalità sincrona
     * 
     * @param action
     *            the TuCSoN action to execute
     * @param timeout
     *            the maximum timeout the caller is willing to wait
     * @param behav
     *            the JADE behaviour in charge of handling the result
     * @return the TuCSoN operation completion event
     * @throws ServiceException
     *             if the service cannot be contacted
     * @throws TucsonInvalidAgentIdException
     *             cannot really happen
     */
    public TucsonOpCompletionEvent executeSynch(
            final AbstractTucsonAction action, final Long timeout,
            final Behaviour behav) throws ServiceException,
            TucsonInvalidAgentIdException {
        ResultOpStorage ros;
        if (this.mResultOpBehaviour.get(behav) == null) { // ottengo o creo il
                                                          // gestore di
                                                          // memorizzazione dei
                                                          // risultati delle
                                                          // operazioni di
                                                          // coordinazione
            ros = new ResultOpStorage();
            this.mResultOpBehaviour.put(behav, ros);
        } else {
            ros = this.mResultOpBehaviour.get(behav);
            if (!ros.isReady()) {
                BridgeJADETuCSoN
                        .log("\n\n comportamento sbloccato da messaggio ricevuto\n\n");
                return null; // il comportamento è stato sbloccato da un
                             // messaggio ricevuto dall'agente e non da un
                             // restart del thread delegato a questo.
            }
        }
        // controllo se l'op è già stata eseguita e ritorna subito il risultato
        // memorizzato in mResultOpBehaviour
        final List<TucsonOpCompletionEvent> list = ros.getList();
        int nextRes = ros.getNextRes();
        if (list.size() > nextRes) { // sono già presenti dei risultati vecchi
            if (list.size() > nextRes) { // è un'operazione già eseguita e di
                                         // cui si ha il risultato
                ros.setNextRes(++nextRes); // si incrementa la prossima
                                           // operazione che si deve eseguire
                return ros.getList().get(nextRes - 1);
            }
        } else { // è la prima volta che si esegue l'operazione
            // controllo se l'operazione richiesta ha bisogno di attendere la
            // risposta
            if ((action instanceof Out) || (action instanceof OutS)
                    || (action instanceof OutAll) || (action instanceof Spawn)
                    || (action instanceof Set) || (action instanceof SetS)) {
                // Creo il comando verticale
                final GenericCommand cmd =
                        new GenericCommand(TucsonSlice.EXECUTE_SYNCH,
                                TucsonService.NAME, null);
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
                final TucsonOpCompletionEvent res =
                        BridgeJADETuCSoN.fromITucsonOpToTucsonOpComp(op);
                // aggiorno struttura condivisa
                ros.getList().add(res);
                ros.setNextRes(ros.getNextRes() + 1);
                ros.setReady(true);
                return res;
            }
            BridgeJADETuCSoN.log("eseguo comando asynch");
            final GenericCommand cmd =
                    new GenericCommand(TucsonSlice.EXECUTE_ASYNCH,
                            TucsonService.NAME, null);
            cmd.addParam(action);
            cmd.addParam(this.acc);
            final TucsonAgentAsync ta =
                    new TucsonAgentAsync("tucsonAgentAsync", ros, cmd,
                            this.service, behav);
            ros.setNextRes(0); // setto a 0 perchè l'agente si bloccherà e
                               // quando verrà riavviato ricomincerà da capo
                               // l'esecuzione
            ros.setReady(false);
            ta.go(); // avvio dell'agente tucson incaricato ad eseguire l'op
                     // e gestire il risultato
        }
        return null; // se si ritorna null vuol dire che il risultato non è
                     // pronto e di conseguenza si adotta il protocollo di
                     // sospensione e riattivazione del behaviour
    }
}
