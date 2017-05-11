package gov.nih.nci.utils.batch;

import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import gov.nih.nci.ui.NCIEditTab;

public class SimplePropProcessor extends EditProcessor {
	
	String prop_iri = null;
	String prop_value = null;
	String new_prop_value = null;
	
	public SimplePropProcessor(NCIEditTab t) {
		
		super(t);		
		
	}
	
	public Vector<String> validateData(Vector<String> v) {
		

		Vector<String> w = super.validateData(v);

		if (classToEdit != null) {
			try {

				prop_iri = (String) v.elementAt(2);
				

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
					if (!checkBatchProperty(prop_iri, new_prop_value)) {
						String error_msg = " -- property value has invalid type.";
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
					if (!checkBatchProperty(
							prop_iri, prop_value)) {
						String error_msg = " -- property value has invalid type.";
						w.add(error_msg);
						
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
		
		boolean retval = true;
		
		OWLAnnotationProperty ap = tab.lookUpShort(prop_iri);
				
		switch (operation) {
		case DELETE:
			tab.removeAnnotationFromClass(classToEdit, ap, prop_value);
			break;
		case MODIFY:
			tab.removeAnnotationFromClass(classToEdit, ap, prop_value);
			tab.addAnnotationToClass(classToEdit, ap, new_prop_value);		
			break;
		case NEW:
			tab.addAnnotationToClass(classToEdit, ap, prop_value);			
			break;
		default:
			break;
		
		}
	
		return retval;
	}
	
	

	
	
	private boolean checkBatchProperty(String propName, String value) {
		return tab.checkType(propName, value);
	}
	
	

}
