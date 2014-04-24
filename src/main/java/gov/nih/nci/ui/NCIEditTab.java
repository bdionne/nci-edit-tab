package gov.nih.nci.ui;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.model.history.HistoryManager;

public class NCIEditTab extends OWLWorkspaceViewsTab {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	// use undo/redo facility
	private HistoryManager history;
	

	public NCIEditTab() {
		setToolTipText("Custom Editor for NCI");
	}

    @Override
	public void initialise() {
		super.initialise();
		history = this.getOWLModelManager().getHistoryManager();
		log.info("NCI Edit Tab initialized");
	}

	@Override
	public void dispose() {
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
}
