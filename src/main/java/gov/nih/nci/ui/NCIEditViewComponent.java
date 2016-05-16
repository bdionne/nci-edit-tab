package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.util.List;

import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;


public class NCIEditViewComponent extends OWLClassAnnotationsViewComponent {
	private static final long serialVersionUID = 1L;
	private EditPanel editPanel;
	
    public void initialiseClassView() throws Exception {
    	
    	editPanel = new EditPanel(getOWLEditorKit());
    	
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
		
		List<PropertyTablePanel> tablePanelList = editPanel.getPropertyTablePanelList();
		for (PropertyTablePanel tablePanel : tablePanelList) {
			tablePanel.setSelectedCls(selectedClass);
		}
        return selectedClass;
	}

	@Override
	public void disposeView() {
		super.disposeView();
		
	}

}
