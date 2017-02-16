package gov.nih.nci.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.FocusedComponentAction;

//import org.protege.editor.owl.ui.action.FocusedComponentAction;

public class CloneClassAction extends FocusedComponentAction<CloneClassTarget> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2497276618178521312L;


	protected Class<CloneClassTarget> initialiseAction() {
        return CloneClassTarget.class;
    }


    protected boolean canPerform() {
    	return (getCurrentTarget() != null && getCurrentTarget().canCloneClass());
    }
    
    public void actionPerformed(ActionEvent e) {
    	// if focus has been lost and popup not updated, target will be null
    	if (getCurrentTarget() != null) {
    		getCurrentTarget().cloneClass();
    	}
    }


    

	

	

}
