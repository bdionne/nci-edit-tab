package gov.nih.nci.ui.action;

import static gov.nih.nci.ui.event.ComplexEditType.*;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;

public class ComplexOperation {
	
	private ComplexEditType type = EDIT;
	
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
	
	private List<OWLClass> already_seen = null;
	
	private OWLClass class_to_retire = null;
	public OWLClass getRetireClass() { return class_to_retire; }
	public void setRetireClass(OWLClass c) { class_to_retire = c; }
	
	private List<OWLClass> retire_parents = null;
	public List<OWLClass> getRetireParents() { return retire_parents; }
	public void setRetireParents(List<OWLClass> p) { retire_parents = p; }
	
	
	
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
		return (type == RETIRE);
	}
	
	public boolean isMerging() {
		return (type == ComplexEditType.MERGE);
	}
	
	public boolean isSplitting() {
		return (type == ComplexEditType.SPLIT);
	}
	
	public boolean isCloning() {
		return (type == ComplexEditType.CLONE);
	}
	
	public boolean isDual() {
		return (type == ComplexEditType.DUAL);
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
	
	public boolean isChangedEditFocus(List<OWLClass> subjects) {
		if ((type == EDIT) && (subjects.size() > 1)) {
			// can only edit one at a time
			return true;			
		}
		if ((type == EDIT) && (currently_editing == null) && (subjects.size() == 1)) {
			// first edit, no change in focus
			currently_editing = subjects.get(0);
			return false;			
		}
		
		if ((type == EDIT) && (subjects.size() == 1) && (currently_editing.equals(subjects.get(0)))) {
			// subsequent edits, no change in focus
			return false;			
		}
		
		if ((type == EDIT) && (subjects.size() == 1) && (!currently_editing.equals(subjects.get(0)))) {
			// new subject edited, change in focus
			return true;			
		}
		
		if (((type == SPLIT) || (type == CLONE)) && (currently_editing == null) && (subjects.size() == 1)) {
			// initial state
			currently_editing = subjects.get(0);
			return false;
		} else if ((type == SPLIT) || (type == CLONE)) {
			for (OWLClass c : subjects) {
				if (!(c.equals(source) ||
						c.equals(target))) {
					return true;
				}
			}
		} 
		
		if (type == MERGE || type == DUAL) {
			if ((source == null) || (target == null)) {
				return true;
			}
			// check intitial state
			if (currently_editing == null) {
				currently_editing = subjects.get(0);
			}
			for (OWLClass c : subjects) {
				if (!(c.equals(source) ||
						c.equals(target))) {
					if (type == DUAL) {
						return true;
					}
				}
			}
		}
		
		if (type == RETIRE) {
			// check intitial state
			if (currently_editing == null) {
				currently_editing = subjects.get(0);
				already_seen = new ArrayList<OWLClass>();
			} else {
				already_seen.add(currently_editing);
				for (OWLClass c : subjects) {
					if (!already_seen.contains(c)) {
						currently_editing = c;
						break;
					}
				}
				
			}

		}
		
		return false;
		
	}
	
	public String toString() {
		return type.toString();
	}

}
