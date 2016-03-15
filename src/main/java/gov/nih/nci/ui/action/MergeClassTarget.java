package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface MergeClassTarget extends ActionTarget {
	
	boolean canMergeClass();


    void mergeClass();

}
