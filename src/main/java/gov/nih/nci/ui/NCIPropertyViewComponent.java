package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

public class NCIPropertyViewComponent extends AbstractOWLClassViewComponent {

    private static final long serialVersionUID = 1L;
	private PropertyTablePanel propsPanel;
    
    public void initialiseClassView() throws Exception {
    	propsPanel = new PropertyTablePanel(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(propsPanel);
        
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
