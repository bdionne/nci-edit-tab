package gov.nih.nci.utils.batch;

import java.util.ArrayList;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import gov.nih.nci.ui.NCIEditTab;

public class SimplePropProcessor extends EditProcessor {
	
	String prop_iri = null;
	String prop_value = null;
	String new_prop_value = null;
	
	public SimplePropProcessor(NCIEditTab t) {
		
		super(t);		
		
	}
	
	public ArrayList<Vector<String>> validateData(Vector<String> v) {
		

		ArrayList<Vector<String>> err_warn = super.validateData(v);
		
		Vector<String> w = err_warn.get(0);
		Vector<String> warnings = err_warn.get(1);
		
		if (!(v.size() >= 4)) {

			String error_msg = " -- input file should have 4 fields for editing Simple Property.";
			w.add(error_msg);
			return err_warn;

		}

		if (classToEdit != null) {
			try {

				prop_iri = (String) v.elementAt(2);
				

				if (!tab.supportsProperty(prop_iri)) {
					String error_msg = " -- property " + prop_iri
							+ " is not identifiable.";
					w.add(error_msg);
					return err_warn;
				} else if (tab.isReadOnlyProperty(prop_iri)) {
					String error_msg = " -- property "
							+ prop_iri + ", it is read-only.";
					w.add(error_msg);
					return err_warn;
				}

				switch (operation) {
				case DELETE:
					if (v.size() == 4) {
						prop_value = (String) v.elementAt(3);				
	
						if (!tab.hasPropertyValue(classToEdit, prop_iri,
								prop_value)) {
							String error_msg = " -- property " + "("
									+ prop_iri + ", "
									+ prop_value
									+ ") does not exist.";
							w.add(error_msg);
						}
					} else {
						String error_msg = " -- input file should have 4 fields for deleting Simple Property.";
						w.add(error_msg);
					}
					break;
				case MODIFY:
					if (v.size() == 5) {
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
						if (!checkBatchProperty(prop_iri, new_prop_value, warnings)) {
							String error_msg = " -- property value has invalid type.";
							w.add(error_msg);						
						}
						
					} else {
						String error_msg = " -- input file should have 5 fields for modifying Simple Property.";
						w.add(error_msg);
					}
					break;
				case NEW:
					if ( v.size() == 4 ) {
						prop_value = (String) v.elementAt(3);
						if (!checkCorrectlyQuoted(prop_value)) {
							String error_msg = " -- property value not correctly quoted.";
							w.add(error_msg);
							
						}
						if (tab.hasPropertyValue(classToEdit, prop_iri,
								prop_value)) {
							String error_msg = " -- property already exists.";
							w.add(error_msg);
						}
						if (!checkBatchProperty(
								prop_iri, prop_value, warnings)) {
							String error_msg = " -- property value has invalid type.";
							w.add(error_msg);
							
						}
						
					} else {
						String error_msg = " -- input file should have 4 fields for adding Simple Property.";
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

		return err_warn;
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
	
	

	
	
	public boolean checkBatchProperty(String propName, String value, 
			Vector<String> warn ) {
		if (!tab.checkAnyURIValue(prop_iri, value)) {
			String warn_msg = " -- the filler value is not an entity in this terminology";
			warn.add(warn_msg);						
		}
		
		return tab.checkType(propName, value);
	}
	
	
	
	private boolean checkCorrectlyQuoted(String s) {
		if (s.startsWith("\"")) {
			return s.endsWith("\"");
		}
		return true;
	}
	
	

}
