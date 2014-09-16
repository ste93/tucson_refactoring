package alice.tucson.persistency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import alice.respect.core.TupleSet;
import alice.respect.core.tupleset.ITupleSet;
import alice.tucson.api.TucsonTupleCentreId;

public class PersistencyXML {

	public static final String ROOT_NODE = "snapshot";
	public static final String TUPLES_NODE = "tuples";
	public static final String TUPLE_NODE = "tuple";
	public static final String SPEC_TUPLES_NODE = "specTuples";
	public static final String SPEC_NODE = "spec";
	public static final String PREDICATES_NODE = "predicates";
	public static final String PREDICATE_NODE = "predicate";
	public static final String UPDATES_NODE = "updates";
	public static final String UPDATE_NODE = "update";
    private String pDate;
    private TucsonTupleCentreId pFileName;
    private String pPath;
    private File xmlFile;
    
    private long initCreationPersistencyXML;
    private long finishCreationPersistencyXML;
    
    private long initParse;
    private long finishParse;
    
    private long initWriteUpdate;
    private long finishWriteUpdate;
    
    private long initWriteFile;
    private long finishWriteFile;
    
	public PersistencyXML(String path, TucsonTupleCentreId fileName)
	{
		this.initCreationPersistencyXML = System.currentTimeMillis();
		
		this.pFileName = fileName;
		this.pPath = path;
		this.finishCreationPersistencyXML=System.currentTimeMillis();
		
		log("Time elapsed for creation of PersistencyXML: "+((this.finishCreationPersistencyXML-this.initCreationPersistencyXML)/1000));
	}
	
	public PersistencyXML(String fileName)
	{
		this.initCreationPersistencyXML = System.currentTimeMillis();
		
		this.xmlFile = new File(fileName);
		
		this.finishCreationPersistencyXML=System.currentTimeMillis();
		
		log("Time elapsed for creation of PersistencyXML: "+((this.finishCreationPersistencyXML-this.initCreationPersistencyXML)/1000));
	}
	
	public List<String> getNodeInfo(Node node, String childName)
	{
		List<String> nodeInfo = new LinkedList<String>();
		NodeList childs = node.getChildNodes();
		
		for(int i = 0; i < childs.getLength(); i++)
		{
			Node n = childs.item(i);
			if(n.getNodeName().equals(childName))
			{
				nodeInfo.add(n.getTextContent());
			}
		}
		return nodeInfo;
	}
	
	public PersistencyData parse()
	{
		
		this.initParse=System.currentTimeMillis();
		
		PersistencyData pData = new PersistencyData();
		List<String> tuples = null;
		List<String> specTuple = null;
		List<String> predicates = null;
		List<String> updates = null;
        try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(this.xmlFile);
			
			// Lista dei figli del root element <snapshot>
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
	             Node node = nodeList.item(i);

	             if (node.getNodeType() == Node.ELEMENT_NODE) 
	             {
	            	 Element elem = (Element) node;
	            	 if(elem.getNodeName().equals(TUPLES_NODE))
	            		tuples = getNodeInfo(node,TUPLE_NODE);
	            	 else if(elem.getNodeName().equals(SPEC_TUPLES_NODE))
	            		specTuple = getNodeInfo(node, SPEC_NODE);
	            	 else if(elem.getNodeName().equals(PREDICATES_NODE))
	            		predicates = getNodeInfo(node, PREDICATE_NODE);
	            	 else if(elem.getNodeName().equals(UPDATES_NODE))
	            		 updates = getNodeInfo(node,UPDATE_NODE);
	             }
	        }
        	
			pData = new PersistencyData(tuples,specTuple,predicates,updates);
			
			this.finishParse=System.currentTimeMillis();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        this.log("Time elapsed for parse metod: "+((this.finishParse-this.initParse)/1000));
        
		return pData;
	}
	
	public void writeUpdate(LogicTuple update, ModType mode)
	{
		
		this.initWriteUpdate=System.currentTimeMillis();
		
		try {
			DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();   
			DocumentBuilder db = dbf.newDocumentBuilder();   
			Document doc = db.parse(this.xmlFile);  
			Element root = doc.getDocumentElement();   

			Element updates;
			NodeList updsElement = doc.getElementsByTagName(UPDATES_NODE);
			if(updsElement.getLength() == 0)
			{
				updates = doc.createElement(UPDATES_NODE);
				root.appendChild(updates);
			}
			else
				updates = (Element)updsElement.item(0);

			Element upd = doc.createElement(UPDATE_NODE);
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
			long now = System.currentTimeMillis();
			Date d = new Date(now);
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			String date = sdf.format(d);
			updates.setAttribute("time", date);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(this.xmlFile);
			transformer.transform(source, result);
			
			this.finishWriteUpdate=System.currentTimeMillis();
			
			
			this.log("Time elapsed for WriteUpdate: "+((this.finishWriteUpdate-this.initWriteUpdate)/1000));
			
			this.log("File updated!");

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(PersistencyData pData)
	{	
		
		this.initWriteFile=System.currentTimeMillis();
		
		try 
		{
			long now = System.currentTimeMillis();
			Date d = new Date(now);
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			String date = sdf.format(d);
			this.pDate = date;
			String pXMLFileName = this.pFileName.getName() + "_at_" + this.pFileName.getNode()
					+ "_at_" + this.pFileName.getPort();
			this.xmlFile = new File(this.pPath, "tc_" + pXMLFileName + "_" + date + ".xml");
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement(ROOT_NODE);
			// Set attribute tc to rootElement
			rootElement.setAttribute("tc", this.pFileName.toString());
			// Set attribute time to rootElement

			rootElement.setAttribute("time", this.pDate);
			document.appendChild(rootElement);
			
			Element tuples = document.createElement(TUPLES_NODE);
            final Iterator<LogicTuple> it = pData.getTupleSet().getIterator();
            while (it.hasNext()) 
            {
				Element tuple = document.createElement(TUPLE_NODE);
				tuple.setTextContent(it.next().toString());
				tuples.appendChild(tuple);
            }
			rootElement.appendChild(tuples);
			
			Element specTuples = document.createElement(SPEC_TUPLES_NODE);
			final Iterator<LogicTuple> itS = pData.getTupleSpecSet().getIterator();
	        while (itS.hasNext()) 
	        {
				Element spec = document.createElement(SPEC_NODE);
				spec.setTextContent(itS.next().toString());
				specTuples.appendChild(spec);
	        }
	        rootElement.appendChild(specTuples);
	        
			Element predicates = document.createElement(PREDICATES_NODE);
            final Iterator<LogicTuple> itP = pData.getPrologPredicates().getIterator();
	        while (itP.hasNext()) 
	        {
				Element predicate = document.createElement(PREDICATE_NODE);
				predicate.setTextContent(itP.next().toString());
				predicates.appendChild(predicate);
	        }
	        rootElement.appendChild(predicates);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(this.xmlFile);
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			this.finishWriteFile=System.currentTimeMillis();
			
			this.log("Time elapsed for write file: "+((this.finishWriteFile-this.initWriteFile)/1000));
			
			this.log("File saved!");

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void log(String s){
		System.out.println("["+this.getClass().getName()+"] "+s);
	}
}
