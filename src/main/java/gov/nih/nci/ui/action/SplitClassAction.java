package gov.nih.nci.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.FocusedComponentAction;

//import org.protege.editor.owl.ui.action.FocusedComponentAction;

public class SplitClassAction extends FocusedComponentAction<SplitClassTarget> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2497276618178521312L;


	protected Class<SplitClassTarget> initialiseAction() {
        return SplitClassTarget.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canSplitClass();
    }


    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().splitClass();
    }

	

	

}
