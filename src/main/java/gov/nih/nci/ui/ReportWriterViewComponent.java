package gov.nih.nci.ui;

import java.awt.BorderLayout;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

public class ReportWriterViewComponent extends OWLClassAnnotationsViewComponent implements OWLSelectionModelListener
{

    private static final long serialVersionUID = 1L;
	private ReportWriterPanel reportPanel;	

	
	public ReportWriterViewComponent() {
		//NCIEditTab.addListener(this);
		
	}

	@Override
	public void initialiseClassView() throws Exception {
		reportPanel = new ReportWriterPanel(getOWLEditorKit());
    	setLayout(new BorderLayout());
        add(reportPanel);
        
	}
	
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		reportPanel.setSelectedClass(selectedClass);
		
        return selectedClass;
	}

	@Override
	public void disposeView() {
		reportPanel.dispose();
	}

	@Override
	public void selectionChanged() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
