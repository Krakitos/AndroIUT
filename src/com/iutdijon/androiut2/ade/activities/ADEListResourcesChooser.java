package com.iutdijon.androiut2.ade.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iutdijon.androiut2.R;

public class ADEListResourcesChooser extends ListActivity {

	public final static String RESOURCE_BASE_INTENT_EXTRA = "resourceBase";
	
	private Element resourceBaseTree = null;
	public ADEListResourcesChooser() {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ADEResource res = (ADEResource) l.getAdapter().getItem(position);
		
		List<Element> children = res.getNode().getChildren();
		if(children.size() != 0){
			((ArrayAdapter<ADEResource>)l.getAdapter()).clear();
			for (int i = 0; i < children.size(); i++) {
				((ArrayAdapter<ADEResource>)l.getAdapter()).add(new ADEResource(children.get(i)));
			}
	
			((ArrayAdapter<ADEResource>)l.getAdapter()).notifyDataSetChanged();
		}else{
			setResult(Integer.parseInt(res.getID()));
			finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			String resourceBase = getIntent().getStringExtra(RESOURCE_BASE_INTENT_EXTRA);
			resourceBaseTree = queryForDerivedResource(resourceBase.substring(0, resourceBase.indexOf('/')));
			
			if(resourceBaseTree == null){
				throw new UnsupportedDataTypeException("Impossible de déterminer la section associée au compte");
			}
			
			
			setListAdapter(new ArrayAdapter<ADEResource>(this, android.R.layout.simple_list_item_1, ADEResource.generateListOfResourceFromNode(resourceBaseTree.getChildren())));
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Demande la liste des descendants dans le XML ADE pour l'id passé.
	 * @param resourceBase ID du conteneur global de la section de l'édudiant
	 * @return Map<Nom ADE, id ADE> des descendants
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws NotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws JDOMException 
	 */
	private Element queryForDerivedResource(String sectionIdentifier) throws NotFoundException, IOException, SAXException, ParserConfigurationException, JDOMException{
		
		Element sectionRoot = null;
		
		Document xml = new SAXBuilder().build(getResources().openRawResource(R.raw.resources_iut));
		
		sectionRoot = getElementByAttributeValue(xml.getRootElement(), sectionIdentifier);
				
		return sectionRoot;
	}
	
	public static Element getElementByAttributeValue(Element rootElement, String attributeValue) {

		Element found = null;
	    if (rootElement != null && rootElement.getChildren().size() != 0) {
	        List<Element> nodeList = rootElement.getChildren();

	        for (int i = 0; i < nodeList.size(); i++) {
	            Element subNode = nodeList.get(i);

	            if (subNode.hasAttributes()) {
	                Attribute attrNode = subNode.getAttribute("name");
	                if(attrNode != null){
	                    if(attrNode.getValue().equalsIgnoreCase(attributeValue)){
	                    	return subNode;
	                    }
	                    found = getElementByAttributeValue(subNode, attributeValue);
	                }               
	            }
	        }
	    }
	    return found;
	}
	
	private static class ADEResource{
		private final Element node;
		
		public ADEResource(Element e) {
			node = e;
		}
		
		public String getID(){
			return node.getAttributeValue("id");
		}
		
		public String getName(){
			return node.getAttributeValue("name");
		}
		
		public Element getNode(){
			return node;
		}
		
		@Override
		public String toString(){
			return getName();
		}
		
		public static List<ADEResource> generateListOfResourceFromNode(List<? extends Element> values){
			List<ADEResource> resources = new ArrayList<ADEListResourcesChooser.ADEResource>(values.size());
			
			for (Iterator<? extends Element> val = values.iterator(); val.hasNext();) {
				Element adeResource = val.next();
				resources.add(new ADEResource(adeResource));
			}
			
			return resources;
		}
	}
}


