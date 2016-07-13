package gov.nih.nci.ui.action;



import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.FocusedComponentAction;

//import org.protege.editor.owl.ui.action.FocusedComponentAction;

public class AddComplexAction extends FocusedComponentAction<AddComplexTarget> {


	private static final long serialVersionUID = 2497276618178521312L;


	protected Class<AddComplexTarget> initialiseAction() {
        return AddComplexTarget.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canAddComplex();
    }
    
    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().addComplex();
    }


    

	

	

}
