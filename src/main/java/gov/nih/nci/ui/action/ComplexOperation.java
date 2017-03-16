package gov.nih.nci.ui.action;

import static gov.nih.nci.ui.event.ComplexEditType.PREMERGE;
import static gov.nih.nci.ui.event.ComplexEditType.PRERETIRE;
import static gov.nih.nci.ui.event.ComplexEditType.RETIRE;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;

public class ComplexOperation {
	
	private ComplexEditType type = ComplexEditType.EDIT;
	
	public void setType(ComplexEditType t) { type = t; }
	public ComplexEditType getType() { return type; }
	public boolean isType(ComplexEditType t) { return type == t; }
	
	private OWLClass source = null;
	public void setSource(OWLClass s) { source = s; }
	public OWLClass getSource() { return source; }
	
	
	private OWLClass target = null;
	public void setTarget(OWLClass t) { target = t; }
	public OWLClass getTarget() { return target; }
	
	private OWLClass currently_editing = null;
	public void setCurrentlyEditing(OWLClass c) { currently_editing = c; }
	public OWLClass getCurrentlyEditing() { return currently_editing; }
	
	
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
	
	public boolean isRetiring() {
		return (type == RETIRE || type == PRERETIRE);
	}
	
	public boolean isMerging() {
		return (type == ComplexEditType.MERGE || type == PREMERGE);
	}
	
	public boolean isSplitting() {
		return (type == ComplexEditType.SPLIT);
	}
	
	public boolean isCloning() {
		return (type == ComplexEditType.CLONE);
	}
	
	public void cancelSplit() {
		source = null;
		target = null;
	}
	
	public void completeSplit() {
		source = null;
		target = null;
	}
	
	public void cancelMerge() {
		source = null;
		target = null;
	}
	
	public void completeMerge() {
		source = null;
		target = null;
	}

	public boolean readyToMerge() {
		return (this.source != null) &&
				(this.target != null);
	}
	
	public boolean inComplexOp() {
		return type != ComplexEditType.EDIT;
	}

}
