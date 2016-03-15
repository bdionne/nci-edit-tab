package gov.nih.nci.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.FocusedComponentAction;

//import org.protege.editor.owl.ui.action.FocusedComponentAction;

public class MergeClassAction extends FocusedComponentAction<MergeClassTarget> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2497276618178521312L;


	protected Class<MergeClassTarget> initialiseAction() {
        return MergeClassTarget.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canMergeClass();
    }
    
    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().mergeClass();
    }


    

	

	

}
