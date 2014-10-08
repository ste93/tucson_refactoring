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
    public static final String ROOT_NODE = "persistency";
    public static final String SNAPSHOT_NODE = "snapshot";
    public static final String SPEC_NODE = "spec";
    public static final String SPEC_TUPLES_NODE = "specTuples";
    public static final String TUPLE_NODE = "tuple";
    public static final String TUPLES_NODE = "tuples";
    public static final String UPDATE_NODE = "update";
    public static final String UPDATES_NODE = "updates";
    public static final String ACTION_ATTRIBUTE = "action";
    public static final String SUBJECT_ATTRIBUTE = "subject";
    public static final String TIME_ATTRIBUTE = "time";
    public static final String TC_ATTRIBUTE = "tc";
    public static final String ADD_OPERATION = "addition";
    public static final String DEL_OPERATION = "deletion";
    public static final String CLEAN_OPERATION = "clean";
    private String pDate;
    private TucsonTupleCentreId pFileName;
    private String pPath;
    private File xmlFile;

    public PersistencyXML(final String fileName) {
        this.xmlFile = new File(fileName);
    }

    public PersistencyXML(final String path, final TucsonTupleCentreId fileName) {
        this.pFileName = fileName;
        this.pPath = path;
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
        PersistencyData pData = new PersistencyData();
        List<String> tuples = new LinkedList<String>();
        List<String> specTuple = new LinkedList<String>();
        List<String> predicates = new LinkedList<String>();
        List<String> updates = new LinkedList<String>();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(this.xmlFile);
            
            // SNAPSHOT
            final NodeList snapshotList = document.getElementsByTagName(SNAPSHOT_NODE);
            final Node snapshotNode = snapshotList.item(0);
            final NodeList snapshotChilds = snapshotNode.getChildNodes();
           
            for (int i = 0; i < snapshotChilds.getLength(); i++)
            {
            	Node node = snapshotChilds.item(i);
            	if(node.getNodeType() == Node.ELEMENT_NODE)
            	{
            		Element elem = (Element)node;
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
                    }
            	}
            }
            
            // UPDATES
            final NodeList updatesList = document.getElementsByTagName(UPDATES_NODE);
            final Node updatesNode = updatesList.item(0);
            final NodeList updatesChilds = updatesNode.getChildNodes();
            for(int i = 0; i < updatesChilds.getLength(); i++)
            {
            	Node node = updatesChilds.item(i);
            	if(node.getNodeType() == Node.ELEMENT_NODE)
            	{
            		Element elem = (Element)node;
            		if (elem.getNodeName().equals(PersistencyXML.UPDATE_NODE))
            		{
            			String update = this.getUpdateInfo(elem);
            			if(update != null)
            				updates.add(update);
            		}
            	}
            }
           
            pData = new PersistencyData(tuples, specTuple, predicates, updates);
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
        return pData;
    }

    public String getUpdateInfo(Element elem)
    {
    	String nodeInfo = null;
    	String attrAction = elem.getAttribute(ACTION_ATTRIBUTE);
		String attrSubject = elem.getAttribute(SUBJECT_ATTRIBUTE);
		String body = elem.getTextContent();
		
		String subject = null;
		if(attrSubject.equals(TUPLE_NODE))
			subject = "t";
		else if(attrSubject.equals(SPEC_NODE))
			subject = "s";
		else if(attrSubject.equals(PREDICATE_NODE))
			subject = "p";
		
		String operation = null;
		if(attrAction.equals(ADD_OPERATION))
			operation = "+";
		else if(attrAction.equals(DEL_OPERATION))
			operation = "-";
		else if(attrAction.equalsIgnoreCase(CLEAN_OPERATION))
			operation = "e";
		
		if(subject != null && operation != null && body != null)
			nodeInfo = "("+operation+subject+")"+" "+body;
		
    	return nodeInfo;
    }
    
    public void write(final PersistencyData pData) {
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
            document.appendChild(rootElement);
            final Element snapshotElement = document
                    .createElement(PersistencyXML.SNAPSHOT_NODE);
            // Set attribute tc to rootElement
            		snapshotElement.setAttribute(TC_ATTRIBUTE, this.pFileName.toString());
            // Set attribute time to rootElement
            snapshotElement.setAttribute(TIME_ATTRIBUTE, this.pDate);
            rootElement.appendChild(snapshotElement);
            final Element tuples = document
                    .createElement(PersistencyXML.TUPLES_NODE);
            final Iterator<LogicTuple> it = pData.getTupleSet().getIterator();
            while (it.hasNext()) {
                final Element tuple = document
                        .createElement(PersistencyXML.TUPLE_NODE);
                tuple.setTextContent(it.next().toString());
                tuples.appendChild(tuple);
            }
            snapshotElement.appendChild(tuples);
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
            snapshotElement.appendChild(specTuples);
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
            snapshotElement.appendChild(predicates);
            // write the content into xml file
            final TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            final DOMSource source = new DOMSource(document);
            final StreamResult result = new StreamResult(this.xmlFile);
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeUpdate(final LogicTuple update, final ModType mode) {
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
                	upd.setAttribute(SUBJECT_ATTRIBUTE, TUPLE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, ADD_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case ADD_SPEC:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, SPEC_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, ADD_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case ADD_PRED:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, PREDICATE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, ADD_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case DEL_TUPLE:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, TUPLE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, DEL_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case DEL_SPEC:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, SPEC_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, DEL_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case DEL_PRED:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, PREDICATE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, DEL_OPERATION);
                    upd.setTextContent(update.toString());
                    break;
                case EMPTY_TUPLES:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, TUPLE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, CLEAN_OPERATION);
                    break;
                case EMPTY_SPEC:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, SPEC_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, CLEAN_OPERATION);
                    break;
                case EMPTY_PRED:
                	upd.setAttribute(SUBJECT_ATTRIBUTE, PREDICATE_NODE);
                	upd.setAttribute(ACTION_ATTRIBUTE, CLEAN_OPERATION);
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
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(this.xmlFile);
            transformer.transform(source, result);
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
