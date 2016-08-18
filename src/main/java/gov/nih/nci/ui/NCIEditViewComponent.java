package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.util.List;

import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;


public class NCIEditViewComponent extends OWLClassAnnotationsViewComponent implements OWLSelectionModelListener,
EditTabChangeListener {
	private static final long serialVersionUID = 1L;
	private EditPanel editPanel;
	
    public void initialiseClassView() throws Exception {
    	
    	editPanel = new EditPanel(getOWLEditorKit());
    	
        setLayout(new BorderLayout());
        add(editPanel);
        this.getOWLWorkspace().getOWLSelectionModel().addListener(this);
        NCIEditTab.addListener(this);
        
    }

    protected void initialiseOntologyView() throws Exception {
        
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		editPanel.setSelectedClass(selectedClass);
        return selectedClass;
	}

	@Override
	public void disposeView() {
		editPanel.disposeView();		
		this.getOWLWorkspace().getOWLSelectionModel().removeListener(this);
		super.disposeView();
		
		
	}

	

	@Override
	public void selectionChanged() throws Exception {
		if (this.isShowing()) {

		} else {
			if (NCIEditTab.currentTab().isRetiring()) {
				getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
		                "nci-edit-tab.EditView");
			}
		}
	}

	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.ADD_PROP)) {
			editPanel.addNewComplexProp();
		}
		
	}

}
