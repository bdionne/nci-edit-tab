package gov.nih.nci.utils.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.utils.PropertyCheckUtil;

public class ComplexPropProcessor extends EditProcessor {
	
	private Map<String, String> qualifiers = new HashMap<String, String>();
	private Map<String, String> new_qualifiers = new HashMap<String, String>();
	
	String prop_iri = null;
	String prop_value = null;
	String new_prop_value = null;
	
	
	public ComplexPropProcessor(NCIEditTab t) {
		super(t);
	}
	
	public ArrayList<Vector<String>> validateData(Vector<String> v) {
		PropertyCheckUtil pcUtil = new PropertyCheckUtil();
		ArrayList<Vector<String>> err_warn = super.validateData(v);
		Vector<String> w = err_warn.get(0);
		
		if (classToEdit != null) {
			try {
				
				// in all cases we need the prop id
				prop_iri = (String) v.elementAt(2);
				
				prop_value = null;
				new_prop_value = null;
				
				if (tab.isRestricted(classToEdit, tab.lookUpShort(prop_iri))) {
					String error_msg = " -- property " + prop_iri
							+ " is restricted.";
					w.add(error_msg);
					return err_warn;
					
				}
				
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



				prop_value = (String) v.elementAt(3);

				qualifiers = new HashMap<String, String>();
				new_qualifiers = new HashMap<String, String>();

				int pairs = 4;
				while ((pairs < v.size()) &&
						(v.elementAt(pairs) != null) &&
						!v.elementAt(pairs).equals(prop_iri)) {
					String ann = v.elementAt(pairs++);
					if (v.elementAt(pairs) != null) {
						// ok, we have two more
						String ann_val = v.elementAt(pairs++);
						qualifiers.put(ann, ann_val);
					} else {
						// error, qualifiers come in pairs
					}
				}
				if (operation.equals(EditOp.MODIFY)) {
					// prop_id is only a delimiter to break while loop above
					new_prop_value = v.elementAt(++pairs);
					pairs++;

					while ((pairs < v.size()) &&
							(v.elementAt(pairs) != null) &&
							!v.elementAt(pairs).equals(prop_iri)) {
						String ann = v.elementAt(pairs++);
						if (v.elementAt(pairs) != null) {
							// ok, we have two more
							String ann_val = v.elementAt(pairs++);
							new_qualifiers.put(ann, ann_val);
						} else {
							// error, qualifiers come in pairs
						}
					}	

				}
				switch (operation) {
				case DELETE:
				case MODIFY:								

					if (!tab.hasComplexPropertyValue(classToEdit, prop_iri,
							prop_value, qualifiers)) {
						String error_msg = " -- complex property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") does not exist.";
						w.add(error_msg);
						return err_warn;
					}
					
					if (tab.hasComplexPropertyValue(classToEdit, prop_iri,
							new_prop_value, new_qualifiers)) {
						String error_msg = " -- complex property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") already exists.";
						w.add(error_msg);
						return err_warn;
					}
					
					String new_qual_errors = pcUtil.checkQualifierTypes(prop_iri, new_qualifiers);
					if (new_qual_errors != null) {
						w.add(new_qual_errors);
						return err_warn;
					}
					break;				
				case NEW:				
					if (tab.hasComplexPropertyValue(classToEdit, prop_iri,
							prop_value, qualifiers)) {
						String error_msg = " -- complex property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") already exists.";
						w.add(error_msg);
						return err_warn;
					}
					String qual_errors = pcUtil.checkQualifierTypes(prop_iri, qualifiers);
					if (qual_errors != null) {
						w.add(qual_errors);
						return err_warn;
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
		switch (operation) {
		case DELETE:
			tab.removeComplexAnnotationProperty(classToEdit, prop_iri, prop_value, qualifiers);
			break;
		case MODIFY:
			tab.removeComplexAnnotationProperty(classToEdit, prop_iri, prop_value, qualifiers);
			tab.addComplexAnnotationProperty(classToEdit, prop_iri, new_prop_value, new_qualifiers);
			break;		
		case NEW:
			tab.addComplexAnnotationProperty(classToEdit, prop_iri, prop_value, qualifiers);
			break;		
		default:
			break;
		}
		
		return true;
	}
	
}
