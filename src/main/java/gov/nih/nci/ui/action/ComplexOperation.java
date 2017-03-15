package gov.nih.nci.ui.action;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;

public class ComplexOperation {
	
	private ComplexEditType type = ComplexEditType.EDIT;
	
	private OWLClass source = null;
	
	private OWLClass target = null;
	
	private OWLClass currently_editing = null;
	
	public void setSource(OWLClass s) { source = s; }
	public void setTarget(OWLClass t) { target = t; }
	
	public ComplexOperation() {
		
	}
	
	public ComplexOperation(OWLClass s, OWLClass t) {
		source = s;
		target = t;
	}
	
	public ComplexOperation(OWLClass s, OWLClass t, ComplexEditType typ) {
		this(s, t);
		type = typ;		
	}

}
