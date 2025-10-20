package gov.nih.nci.ui.action;

import org.protege.editor.owl.ui.action.ActionTarget;

public interface UnMergeClassTarget extends ActionTarget {
	
	boolean canUnMergeClass();


    void unmergeClass();

}
