package org.bicknese.cfwarning;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

public class TagLibrary extends AbstractUIPlugin {
	
	private HashMap<String,HashMap<String,HashMap<String,String>>> tagsMap;
	
	private static TagLibrary instance = null;
	
	private TagLibrary() {
		tagsMap = new HashMap<String,HashMap<String,HashMap<String,String>>>();
		createTagsMap();
	}
	
	public static TagLibrary getInstance() {
		
		if (instance == null)
			instance = new TagLibrary();
		
		return instance;
		
	}
	
	private Document getTagXML() {
		
		try {
			InputStream inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/tags.xml"), false);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			doc.getDocumentElement().normalize();
			inputStream.close();
			
			return doc;
			
		} catch (Exception e) {
			System.out.println("getTagXML"+e);
		} 
		
		return null;
		
	}
	
	private NodeList getTagList() {
		
		return getTagXML().getElementsByTagName("tag");
		
	}
	
	private NodeList getAttributeList(Element tagElement) {
		
		NodeList attributesList = tagElement.getElementsByTagName("attributes");
		Element attributesElement = (Element) attributesList.item(0);
		
		return attributesElement.getElementsByTagName("attribute");
		
	}
	
	private void createTagsMap() {
		
		try {
		
			NodeList tags = getTagList();

			for (int tag = 0; tag < tags.getLength(); tag++) {

				Node tagNode = tags.item(tag);
				
				if (tagNode.getNodeType() == Node.ELEMENT_NODE) {

					Element tagElement = (Element) tagNode;
					String tagName = tagElement.getAttribute("name");
					
					NodeList attributes = getAttributeList(tagElement);
					
					if (! tagsMap.containsKey(tagName)) {
						tagsMap.put(tagName, new HashMap<String,HashMap<String,String>>());
					}
					
					HashMap<String,HashMap<String,String>> currentTag = (HashMap<String,HashMap<String,String>>)tagsMap.get(tagName);
					
					for (int i = 0; i < attributes.getLength(); i++) {
						
						Node attributeNode = attributes.item(i);
						
						if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {
							
							NamedNodeMap attribute = attributeNode.getAttributes();

							Node attributeName = attribute.getNamedItem("name");

							currentTag.put(attributeName.getNodeValue(), new HashMap<String,String>());

							HashMap<String,String> currentAttribute = (HashMap<String,String>)currentTag.get(attributeName.getNodeValue());

							Node attributeAttr = attribute.getNamedItem("attr");
							if (null != attributeAttr) {
								currentAttribute.put("attr", attributeAttr.getNodeValue());
							}

							Node attributeValue = attribute.getNamedItem("value");
							if (null != attributeValue) {
								currentAttribute.put("value", attributeValue.getNodeValue());
							}
							
						}
						
					}
															
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public Boolean isReturnTag(String tagName) {
		return tagsMap.containsKey(tagName);
	}
	
	public Boolean isAttribute(String tagName, String attributeName) {
		return tagsMap.get(tagName).containsKey(attributeName);
	}
	
	public Boolean hasExtraAttributes(String tagName, String attributeName) {
		return ! tagsMap.get(tagName).get(attributeName).isEmpty();
	}
	
	public Boolean isReturnTag(String tagName, String attributeName, String attrName, String valueName) {
		return tagsMap.get(tagName).get(attributeName).containsKey(attrName) && tagsMap.get(tagName).get(attributeName).get(attrName) == valueName;
	}
	
	public String returnValue(String tagName, Hashtable<String,String> tagAttributes) {
		
		if(!isReturnTag(tagName))
			return "";
		
		String name = "";
		String attribute = "";
		
		HashMap<String, HashMap<String, String>> tag = tagsMap.get(tagName);
		
		Set<String> attributeKeys = tag.keySet();
		
		for (String element : attributeKeys) {
			
			if(tagAttributes.containsKey(element)) {
				name = tagAttributes.get(element);
				attribute = element;
			}
			
		}
		
		if(attribute.compareTo("") == 0)
			return "";
		
		if(tag.get(attribute).isEmpty())
			return name;
		
		String attr = tag.get(attribute).get("attr");
		String value = tag.get(attribute).get("value");
		
		if(tagAttributes.containsKey(attr) && tagAttributes.get(attr).compareToIgnoreCase(value) == 0)
			return name;
		
		return "";
	}
	
}