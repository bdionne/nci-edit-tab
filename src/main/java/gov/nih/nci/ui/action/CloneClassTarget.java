package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface CloneClassTarget extends ActionTarget {
	
	boolean canCloneClass();


    void cloneClass();

}
