package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class SimplePropProcessor extends EditProcessor {
	
	public SimplePropProcessor(NCIEditTab t) {
		
		super(t);		
		
	}
	
	public Vector<String> validateData(Vector<String> v) {
		

		Vector<String> w = super.validateData(v);

		if (classToEdit != null) {
			try {

				String prop_iri = (String) v.elementAt(2);
				String prop_value;
				String new_prop_value;

				if (!tab.supportsProperty(prop_iri)) {
					String error_msg = " -- property " + prop_iri
							+ " is not identifiable.";
					w.add(error_msg);
					return w;
				} else if (tab.isReadOnlyProperty(prop_iri)) {
					String error_msg = " -- property "
							+ prop_iri + ", it is read-only.";
					w.add(error_msg);
					return w;
				}

				switch (operation) {
				case DELETE:
					prop_value = (String) v.elementAt(3);				

					if (!tab.hasPropertyValue(classToEdit, prop_iri,
							prop_value)) {
						String error_msg = " -- property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") does not exist.";
						w.add(error_msg);
					}
					break;
				case DEL_ALL:
					// TODO: Is there anything to validate here?
					break;
				case MODIFY:
					prop_value = (String) v.elementAt(3);
					
					if (!tab.hasPropertyValue(classToEdit, prop_iri,
							prop_value)) {
						String error_msg = " -- property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") does not exist.";
						w.add(error_msg);
					}
					
					new_prop_value = (String) v.elementAt(4);
					
					if (tab.hasPropertyValue(classToEdit, prop_iri,
							new_prop_value)) {
						String error_msg = " -- property already exists.";
						w.add(error_msg);
					}
					break;
				case NEW:
					prop_value = (String) v.elementAt(3);
					if (tab.hasPropertyValue(classToEdit, prop_iri,
							prop_value)) {
						String error_msg = " -- property already exists.";
						w.add(error_msg);
					}
					if (checkBatchProperty(
							prop_iri, prop_value)
							&& checkBatchPropertyNotFullSynPT(
									prop_iri, prop_value)) {

					} else {
						// TODO: add some error messages here
						//w.add(tab.getFilter().getErrorMessage());
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				w.add("Exception caught" + e.toString());
			}
		}

		return w;
	}

	
	public boolean processData(Vector<String> w) {
		
		boolean retval = false;
		
		OWLAnnotationProperty ap = tab.lookUpShort((String) w.elementAt(2));
		String prop_value;	
		
		switch (operation) {
		case DELETE:
			prop_value = (String) w.elementAt(3);
			tab.removeAnnotationToClass(classToEdit, ap, prop_value);
			break;
		case DEL_ALL:
			break;
		case MODIFY:
			prop_value = (String) w.elementAt(3);
			String new_prop_value = (String) w.elementAt(4);
			tab.removeAnnotationToClass(classToEdit, ap, prop_value);
			tab.addAnnotationToClass(classToEdit, ap, new_prop_value);
			possiblySyncPreferredTerm(classToEdit, ap, new_prop_value);			
			break;
		case NEW:
			prop_value = (String) w.elementAt(3);			
			tab.addAnnotationToClass(classToEdit, ap, prop_value);
			
			/**
			if (attributeName.compareToIgnoreCase(NCIEditTabConstants.ALTLABEL) == 0) {
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
				retval = wrapper.addAnnotationProperty(hostClass, NCIEditTab.ALTLABEL, 
						owlModel.createRDFSLiteral(attributeValue, owlModel.getSystemFrames().getXmlLiteralType()));
			} else {
				retval = wrapper.addAnnotationProperty(name, attributeName,
						attributeValue);
				
			}	
			*/		
			
			break;
		default:
			break;
		
		}
	
		return retval;
	}
	
	

	
	
	private boolean checkBatchProperty(String propName, String value) {
		// TODO:
		return true;
	}
	
	private boolean  checkBatchPropertyNotFullSynPT(String propName, String value) {
		// TODO:
		return true;
	}
	
	public void possiblySyncPreferredTerm(OWLClass ocl, OWLAnnotationProperty prop,
			String value) {
		/**
		if (name.compareTo(NCIEditTab.ALTLABEL) == 0) {

			String tn = ComplexPropertyParser.getPtNciTermName(value);
			if (tn != null) {
				// need to mod preferred name and rdfs:label
				OWLNamedClass cls = wrapper.getOWLNamedClass(cls_name);
				String pn = wrapper.getPropertyValue(cls, NCIEditTab.PREFLABEL);
				String rdl = wrapper.getPropertyValue(cls, "rdfs:label");
				wrapper.modifyAnnotationProperty(cls_name,
						NCIEditTab.PREFLABEL, pn, tn);
				wrapper.modifyAnnotationProperty(cls_name, "rdfs:label", rdl,
						tn);

			}
		} else if (name.compareTo(NCIEditTab.PREFLABEL) == 0) {

			OWLNamedClass cls = wrapper.getOWLNamedClass(cls_name);
			ArrayList<String> pvals = wrapper.getPropertyValues(cls,
					NCIEditTab.ALTLABEL);
			for (String s : pvals) {
				String tn = ComplexPropertyParser.getPtNciTermName(s);
				if (tn != null) {
					HashMap<String, String> hm = ComplexPropertyParser
							.parseXML(s);
					String newfspt = ComplexPropertyParser.replaceFullSynValue(
							hm, "term-name", value);
					wrapper.modifyAnnotationProperty(cls_name,
							NCIEditTab.ALTLABEL, s, newfspt);
				}
			}

			String rdl = wrapper.getPropertyValue(cls, "rdfs:label");
			wrapper
					.modifyAnnotationProperty(cls_name, "rdfs:label", rdl,
							value);

		}
		*/

	}

}
