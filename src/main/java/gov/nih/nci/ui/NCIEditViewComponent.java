package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;


public class NCIEditViewComponent extends OWLClassAnnotationsViewComponent implements EditTabChangeListener {
	private static final long serialVersionUID = 1L;
	private EditPanel editPanel;
	
	
	
    public void initialiseClassView() throws Exception {
    	
    	editPanel = new EditPanel(getOWLEditorKit());
    	
        setLayout(new BorderLayout());
        add(editPanel);
        NCIEditTab.addListener(this);
        
    }

    @Override
	protected OWLClass updateView(OWLClass selectedClass) {
    	if (selectedClass != null) {
    		if (!NCIEditTab.currentTab().isRetired(selectedClass)) {
    			return selectedClass;
        	}    		
    	} else {
    		editPanel.setSelectedClass(null);
    	}    	
        return null;
	}

	@Override
	public void disposeView() {
		editPanel.disposeView();
		super.disposeView();		
	}

	
	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.ADD_PROP)) {
			editPanel.addNewComplexProp();
		} else if (event.isType(ComplexEditType.INIT_PROPS)) {
			editPanel.disposeView();
			this.remove(editPanel);			
			editPanel = new EditPanel(getOWLEditorKit());
	    	
	        setLayout(new BorderLayout());
	        add(editPanel);
			
		} else if (event.isType(ComplexEditType.MODIFY)) { 
			if (NCIEditTab.currentTab().isSplitting() ||
					NCIEditTab.currentTab().isMerging() ||
					NCIEditTab.currentTab().isCloning()) {
			} else {
				editPanel.enableButtons();
				NCIEditTab.currentTab().setEditInProgress(true);
				NCIEditTab.currentTab().setCurrentlyEditing(editPanel.getSelectedClass(), true);
			}

		} else if (event.isType(ComplexEditType.SELECTED)) {
			editPanel.setSelectedClass(NCIEditTab.currentTab().getCurrentlySelected());
			if (!NCIEditTab.currentTab().inComplexOp()) {
				getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
						"nci-edit-tab.EditView");
			}

		} else if (event.isType(ComplexEditType.COMMIT)) { 
			
			// need to test other ops here? I guess ok as merge/clone are eq
			if (NCIEditTab.currentTab().isSplitting()) {

			} else {
				NCIEditTab.currentTab().setEditInProgress(false);
				//NCIEditTab.currentTab().setCurrentlyEditing(null);
				//NCIEditTab.currentTab().refreshNavTree();
				editPanel.disableButtons();
			}
		}		
	}

}
