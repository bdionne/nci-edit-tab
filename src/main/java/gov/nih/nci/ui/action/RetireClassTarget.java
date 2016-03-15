package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface RetireClassTarget extends ActionTarget {
	
	boolean canRetireClass();


    void retireClass();

}
