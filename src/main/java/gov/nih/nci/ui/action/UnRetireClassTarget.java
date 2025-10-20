package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface UnRetireClassTarget extends ActionTarget {
	
	boolean canUnRetireClass();


    void unretireClass();

}
