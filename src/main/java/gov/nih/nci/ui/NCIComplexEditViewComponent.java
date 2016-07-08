package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;

public class NCIComplexEditViewComponent extends OWLClassAnnotationsViewComponent implements EditTabChangeListener {

    private static final long serialVersionUID = 1L;
	private ComplexEditPanel complexEditPanel;
	
	    
    public void initialiseClassView() throws Exception {    	
    	
    	complexEditPanel = new ComplexEditPanel(getOWLEditorKit());    	
    	
        setLayout(new BorderLayout());
        add(complexEditPanel);
        NCIEditTab.addListener(this);
               
    }

    protected void initialiseOntologyView() throws Exception {
        
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    

    @Override
	protected OWLClass updateView(OWLClass selectedClass) {		
        return selectedClass;
	}

	@Override
	public void disposeView() {
		complexEditPanel.dispose();	
		
	}

	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.SPLIT)) {
			complexEditPanel.setRootObjects(NCIEditTab.currentTab().getSplitSource(),
					NCIEditTab.currentTab().getSplitTarget());
			getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
	                "org.protege.editor.owl.client.ComplexEditView");
		}
	}
	
	
}
