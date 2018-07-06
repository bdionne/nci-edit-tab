package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public abstract class EditProcessor {
	
	protected NCIEditTab tab = null;
	
	protected OWLClass classToEdit = null;
	
	protected static enum EditOp { NEW, MODIFY, DELETE };
	
	protected EditOp operation = null;
		
	public EditProcessor(NCIEditTab t) { tab = t; }
	
	public Vector<String> validateData(Vector<String> v) {
		Vector<String> errors = new Vector<String>();
		
		try {

			String cls_iri = (String) v.elementAt(0);
			String op = (String) v.elementAt(1);
			
			classToEdit = tab.getClass(cls_iri);			

			if (classToEdit == null) {
				String error_msg = " -- concept " + cls_iri
						+ " does not exist.";
				errors.add(error_msg);

			} else if (tab.isRetired(classToEdit) &&
					!tab.isWorkFlowManager()) {
				errors.add(" -- concept " + cls_iri + " is retired, cannot edit");
			}
			
			try {
				operation = EditOp.valueOf(op.toUpperCase());
			} catch (IllegalArgumentException e) {
				String error_msg = " -- action " + op
						+ " is not supported.";
				errors.add(error_msg);
				
			}
			


		} catch (Exception e) {
			errors.add("Exception caught" + e.toString());
		}
		
		return errors;
	}
	
	public boolean processData(Vector<String> data) {
		return true;
	}

}
