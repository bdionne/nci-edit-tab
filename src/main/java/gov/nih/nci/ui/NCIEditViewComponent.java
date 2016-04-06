package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.transferhandler.ListTransferHandler;

public class NCIEditViewComponent extends OWLClassAnnotationsViewComponent {
	private static final long serialVersionUID = 1L;
	private EditPanel editPanel;
	//private OWLFrameList<OWLAnnotationSubject> upperPanelList;
	//private OWLFrameList<OWLAnnotationSubject> lowerPanelList;
    
    public void initialiseClassView() throws Exception {
    	//upperPanelList = new OWLFrameList<OWLAnnotationSubject>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
    	//lowerPanelList = new OWLFrameList<OWLAnnotationSubject>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
    	
    	editPanel = new EditPanel(getOWLEditorKit());
    	//upperPanelList.setTransferHandler(new ListTransferHandler(complexEditPanel));
    	//lowerPanelList.setTransferHandler(new ListTransferHandler(complexEditPanel));
    	
        setLayout(new BorderLayout());
        add(editPanel);
        
    }

    protected void initialiseOntologyView() throws Exception {
        
    }

    protected void disposeOntologyView() {
        // do nothing
    }

    

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		//upperPanelList.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
		//lowerPanelList.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
        return selectedClass;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
		//this.upperPanelList.dispose();
		//this.lowerPanelList.dispose();
		
	}

}
