package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;


public class ReadOnlyClassViewComponent extends OWLClassAnnotationsViewComponent implements EditTabChangeListener {
	private static final long serialVersionUID = 1L;
	private ReadOnlyClassPanel editPanel;
	
	
	
    public void initialiseClassView() throws Exception {
    	
    	editPanel = new ReadOnlyClassPanel(getOWLEditorKit());
    	
        setLayout(new BorderLayout());
        add(editPanel);
        NCIEditTab.addListener(this);
        
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
    	if (selectedClass != null &&
    			NCIEditTab.currentTab().isRetired(selectedClass)) {
    		editPanel.setSelectedClass(selectedClass);
    		return selectedClass;
    	} else {
    		editPanel.setSelectedClass(null);
    		return null;
    	}
    }

	@Override
	public void disposeView() {
		editPanel.disposeView();
		super.disposeView();		
	}

	
	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.INIT_PROPS)) {
			editPanel.disposeView();
			this.remove(editPanel);			
			editPanel = new ReadOnlyClassPanel(getOWLEditorKit());
	    	
	        setLayout(new BorderLayout());
	        add(editPanel);
			
		} else if (event.isType(ComplexEditType.READ)) {
			getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
					"nci-edit-tab.ReadOnlyClass");
			

		}	
	}

}
