package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

public class BatchEditIRIsViewComponent extends OWLClassAnnotationsViewComponent
{

    private static final long serialVersionUID = 1L;
	private BatchEditIRIsPanel batchEditIRIsPanel;	

	
	public BatchEditIRIsViewComponent() {
		
	}

	@Override
	public void initialiseClassView() throws Exception {
		batchEditIRIsPanel = new BatchEditIRIsPanel(getOWLEditorKit());
    	setLayout(new BorderLayout());
        add(batchEditIRIsPanel);
        
	}
	
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		batchEditIRIsPanel.setSelectedClass(selectedClass);		
        return selectedClass;
	}

	@Override
	public void disposeView() {
		batchEditIRIsPanel.disposeView();
	}

	
	
}

