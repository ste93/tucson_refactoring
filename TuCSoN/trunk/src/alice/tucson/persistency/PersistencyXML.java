package alice.tucson.persistency;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import alice.logictuple.LogicTuple;
import alice.respect.core.RespectVMContext.ModType;
import alice.tucson.api.TucsonTupleCentreId;

public class PersistencyXML {
    public static final String PREDICATE_NODE = "predicate";
    public static final String PREDICATES_NODE = "predicates";
    public static final String ROOT_NODE = "snapshot";
    public static final String SPEC_NODE = "spec";
    public static final String SPEC_TUPLES_NODE = "specTuples";
    public static final String TUPLE_NODE = "tuple";
    public static final String TUPLES_NODE = "tuples";
    public static final String UPDATE_NODE = "update";
    public static final String UPDATES_NODE = "updates";
    private final long finishCreationPersistencyXML;
    private long finishParse;
    private long finishWriteFile;
    private long finishWriteUpdate;
    private final long initCreationPersistencyXML;
    private long initParse;
    private long initWriteFile;
    private long initWriteUpdate;
    private String pDate;
    private TucsonTupleCentreId pFileName;
    private String pPath;
    private File xmlFile;

    public PersistencyXML(final String fileName) {
        this.initCreationPersistencyXML = System.nanoTime();
        this.xmlFile = new File(fileName);
        this.finishCreationPersistencyXML = System.nanoTime();
        this.log("Time elapsed for creation of PersistencyXML: "
                + (this.finishCreationPersistencyXML - this.initCreationPersistencyXML)
                / 1000000 + " milliseconds.");
    }

    public PersistencyXML(final String path, final TucsonTupleCentreId fileName) {
        this.initCreationPersistencyXML = System.nanoTime();
        this.pFileName = fileName;
        this.pPath = path;
        this.finishCreationPersistencyXML = System.nanoTime();
        this.log("Time elapsed for creation of PersistencyXML: "
                + (this.finishCreationPersistencyXML - this.initCreationPersistencyXML)
                / 1000000 + " milliseconds.");
    }

    public List<String> getNodeInfo(final Node node, final String childName) {
        final List<String> nodeInfo = new LinkedList<String>();
        final NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            final Node n = childs.item(i);
            if (n.getNodeName().equals(childName)) {
                nodeInfo.add(n.getTextContent());
            }
        }
        return nodeInfo;
    }

    public PersistencyData parse() {
        this.initParse = System.nanoTime();
        PersistencyData pData = new PersistencyData();
        List<String> tuples = null;
        List<String> specTuple = null;
        List<String> predicates = null;
        List<String> updates = null;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(this.xmlFile);
            // Lista dei figli del root element <snapshot>
            final NodeList nodeList = document.getDocumentElement()
                    .getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(PersistencyXML.TUPLES_NODE)) {
                        tuples = this.getNodeInfo(node,
                                PersistencyXML.TUPLE_NODE);
                    } else if (elem.getNodeName().equals(
                            PersistencyXML.SPEC_TUPLES_NODE)) {
                        specTuple = this.getNodeInfo(node,
                                PersistencyXML.SPEC_NODE);
                    } else if (elem.getNodeName().equals(
                            PersistencyXML.PREDICATES_NODE)) {
                        predicates = this.getNodeInfo(node,
                                PersistencyXML.PREDICATE_NODE);
                    } else if (elem.getNodeName().equals(
                            PersistencyXML.UPDATES_NODE)) {
                        updates = this.getNodeInfo(node,
                                PersistencyXML.UPDATE_NODE);
                    }
                }
            }
            pData = new PersistencyData(tuples, specTuple, predicates, updates);
            this.finishParse = System.nanoTime();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.log("Time elapsed for parse metod: "
                + (this.finishParse - this.initParse) / 1000000
                + " milliseconds.");
        return pData;
    }

    public void write(final PersistencyData pData) {
        this.initWriteFile = System.currentTimeMillis();
        try {
            final long now = System.currentTimeMillis();
            final Date d = new Date(now);
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd_HH.mm.ss");
            final String date = sdf.format(d);
            this.pDate = date;
            final String pXMLFileName = this.pFileName.getName() + "_at_"
                    + this.pFileName.getNode() + "_at_"
                    + this.pFileName.getPort();
            this.xmlFile = new File(this.pPath, "tc_" + pXMLFileName + "_"
                    + date + ".xml");
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();
            final Document document = documentBuilder.newDocument();
            final Element rootElement = document
                    .createElement(PersistencyXML.ROOT_NODE);
            // Set attribute tc to rootElement
            rootElement.setAttribute("tc", this.pFileName.toString());
            // Set attribute time to rootElement
            rootElement.setAttribute("time", this.pDate);
            document.appendChild(rootElement);
            final Element tuples = document
                    .createElement(PersistencyXML.TUPLES_NODE);
            final Iterator<LogicTuple> it = pData.getTupleSet().getIterator();
            while (it.hasNext()) {
                final Element tuple = document
                        .createElement(PersistencyXML.TUPLE_NODE);
                tuple.setTextContent(it.next().toString());
                tuples.appendChild(tuple);
            }
            rootElement.appendChild(tuples);
            final Element specTuples = document
                    .createElement(PersistencyXML.SPEC_TUPLES_NODE);
            final Iterator<LogicTuple> itS = pData.getTupleSpecSet()
                    .getIterator();
            while (itS.hasNext()) {
                final Element spec = document
                        .createElement(PersistencyXML.SPEC_NODE);
                spec.setTextContent(itS.next().toString());
                specTuples.appendChild(spec);
            }
            rootElement.appendChild(specTuples);
            final Element predicates = document
                    .createElement(PersistencyXML.PREDICATES_NODE);
            final Iterator<LogicTuple> itP = pData.getPrologPredicates()
                    .getIterator();
            while (itP.hasNext()) {
                final Element predicate = document
                        .createElement(PersistencyXML.PREDICATE_NODE);
                predicate.setTextContent(itP.next().toString());
                predicates.appendChild(predicate);
            }
            rootElement.appendChild(predicates);
            // write the content into xml file
            final TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(document);
            final StreamResult result = new StreamResult(this.xmlFile);
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
            this.finishWriteFile = System.currentTimeMillis();
            this.log("Time elapsed for write file: "
                    + (this.finishWriteFile - this.initWriteFile) / 1000
                    + " milliseconds.");
            this.log("File saved!");
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeUpdate(final LogicTuple update, final ModType mode) {
        this.initWriteUpdate = System.nanoTime();
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(this.xmlFile);
            final Element root = doc.getDocumentElement();
            Element updates;
            final NodeList updsElement = doc
                    .getElementsByTagName(PersistencyXML.UPDATES_NODE);
            if (updsElement.getLength() == 0) {
                updates = doc.createElement(PersistencyXML.UPDATES_NODE);
                root.appendChild(updates);
            } else {
                updates = (Element) updsElement.item(0);
            }
            final Element upd = doc.createElement(PersistencyXML.UPDATE_NODE);
            switch (mode) {
                case ADD_TUPLE:
                    upd.setTextContent("(+t) " + update);
                    break;
                case ADD_SPEC:
                    upd.setTextContent("(+s) " + update);
                    break;
                case ADD_PRED:
                    upd.setTextContent("(+p) " + update);
                    break;
                case DEL_TUPLE:
                    upd.setTextContent("(-t) " + update);
                    break;
                case DEL_SPEC:
                    upd.setTextContent("(-s) " + update);
                    break;
                case DEL_PRED:
                    upd.setTextContent("(-p) " + update);
                    break;
                case EMPTY_TUPLES:
                    upd.setTextContent("(et)");
                    break;
                case EMPTY_SPEC:
                    upd.setTextContent("(es)");
                    break;
                case EMPTY_PRED:
                    upd.setTextContent("(ep)");
                    break;
                default:
                    break;
            }
            updates.appendChild(upd);
            // Updates Time attribute
            final long now = System.currentTimeMillis();
            final Date d = new Date(now);
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd_HH.mm.ss");
            final String date = sdf.format(d);
            updates.setAttribute("time", date);
            // write the content into xml file
            final TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(this.xmlFile);
            transformer.transform(source, result);
            this.finishWriteUpdate = System.nanoTime();
            this.log("Time elapsed for WriteUpdate: "
                    + (this.finishWriteUpdate - this.initWriteUpdate) / 1000000
                    + " milliseconds.");
            this.log("File updated!");
        } catch (final TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void log(final String s) {
        System.out.println("[" + this.getClass().getName() + "] " + s);
    }
}
