package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.transferhandler.ListTransferHandler;

public class NCIComplexEditViewComponent extends OWLClassAnnotationsViewComponent {

    private static final long serialVersionUID = 1L;
	private ComplexEditPanel complexEditPanel;
	private OWLFrameList<OWLAnnotationSubject> list;
    
    public void initialiseClassView() throws Exception {
    	list = new OWLFrameList<OWLAnnotationSubject>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
    	list.setTransferHandler(new ListTransferHandler());
    	complexEditPanel = new ComplexEditPanel(getOWLEditorKit(), list);
        setLayout(new BorderLayout());
        add(complexEditPanel);
        
    }

    protected void initialiseOntologyView() throws Exception {
        
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		//list.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
        return selectedClass;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
		
	}
}
