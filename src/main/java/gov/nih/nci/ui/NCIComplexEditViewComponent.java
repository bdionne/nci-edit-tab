package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;
import gov.nih.nci.ui.transferhandler.ListTransferHandler;

public class NCIComplexEditViewComponent extends OWLClassAnnotationsViewComponent implements EditTabChangeListener {

    private static final long serialVersionUID = 1L;
	private ComplexEditPanel complexEditPanel;
	private OWLFrameList<OWLAnnotationSubject> upperPanelList;
	private OWLFrameList<OWLAnnotationSubject> lowerPanelList;
	    
    public void initialiseClassView() throws Exception {
    	upperPanelList = new OWLFrameList<OWLAnnotationSubject>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
    	lowerPanelList = new OWLFrameList<OWLAnnotationSubject>(getOWLEditorKit(), new OWLAnnotationsFrame(getOWLEditorKit()));
    	
    	complexEditPanel = new ComplexEditPanel(getOWLEditorKit(), upperPanelList, lowerPanelList);
    	upperPanelList.setTransferHandler(new ListTransferHandler(complexEditPanel));
    	lowerPanelList.setTransferHandler(new ListTransferHandler(complexEditPanel));
    	
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
		//upperPanelList.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
		//lowerPanelList.setRootObject(selectedClass == null ? null : selectedClass.getIRI());
        return selectedClass;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
		this.upperPanelList.dispose();
		this.lowerPanelList.dispose();
		
	}

	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.SPLIT)) {
			upperPanelList.setRootObject(NCIEditTab.currentTab().getSplitSource().getIRI());
			lowerPanelList.setRootObject(NCIEditTab.currentTab().getSplitTarget().getIRI());
			getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
	                "nci-edit-tab.ComplexEditView");
		}
	}
	
	/*public ComplexEditPanel getComplexEditPanel() {
	return complexEditPanel;
}

	public OWLFrameList<OWLAnnotationSubject> getList() {
		return list;
	}*/
}
