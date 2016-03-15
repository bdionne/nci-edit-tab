package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface SplitClassTarget extends ActionTarget {
	
	boolean canSplitClass();


    void splitClass();

}
