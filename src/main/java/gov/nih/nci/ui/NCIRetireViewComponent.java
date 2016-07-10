package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;

public class NCIRetireViewComponent extends OWLClassAnnotationsViewComponent implements OWLSelectionModelListener,
EditTabChangeListener {

    private static final long serialVersionUID = 1L;
	private RetirePanel retirePanel;	

	@Override
	public void handleChange(EditTabChangeEvent event) {
		if (event.isType(ComplexEditType.RETIRE)) {
			
			retirePanel.setOWLClass(NCIEditTab.currentTab().getRetireClass());
			getOWLEditorKit().getWorkspace().getViewManager().bringViewToFront(
	                "nci-edit-tab.RetireView");
			
			setHeaderText(NCIEditTab.currentTab().getRetireClass().asOWLClass().getIRI().getShortForm());
		}
	}

	@Override
	public void initialiseClassView() throws Exception {
		retirePanel = new RetirePanel(getOWLEditorKit());
    	setLayout(new BorderLayout());
        add(retirePanel);
        NCIEditTab.addListener(this);
	}
	
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {		
        return selectedClass;
	}

	@Override
	public void disposeView() {
		retirePanel.dispose();
	}

	@Override
	public void selectionChanged() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
