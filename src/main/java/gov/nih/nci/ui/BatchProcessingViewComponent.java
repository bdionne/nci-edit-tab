package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

public class BatchProcessingViewComponent extends OWLClassAnnotationsViewComponent{
	private static final long serialVersionUID = 111111L;
	private BatchProcessOutputPanel batchPanel;
    
    public void initialiseClassView() throws Exception {
    	batchPanel = new BatchProcessOutputPanel(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(batchPanel);
        
    }

    protected void initialiseOntologyView() throws Exception {
        
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		// TODO Auto-generated method stub
		//propsPanel.setSelectedCls(selectedClass);
		return selectedClass;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
		
	}
}
