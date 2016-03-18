package gov.nih.nci.ui;

import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

import edu.stanford.protege.metaproject.Utils;
import edu.stanford.protege.metaproject.api.Metaproject;
import edu.stanford.protege.metaproject.api.Operation;
import edu.stanford.protege.metaproject.api.Project;
import edu.stanford.protege.metaproject.api.User;
import edu.stanford.protege.metaproject.api.exception.ProjectNotInPolicyException;
import edu.stanford.protege.metaproject.api.exception.UnknownOperationIdException;
import edu.stanford.protege.metaproject.api.exception.UnknownProjectIdException;
import edu.stanford.protege.metaproject.api.exception.UnknownUserIdException;
import edu.stanford.protege.metaproject.api.exception.UserNotInPolicyException;
import gov.nih.nci.utils.MetaprojectGen;

public class NCIEditTab extends OWLWorkspaceViewsTab {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	public static Operation MERGE;
	
	// use undo/redo facility
	//private HistoryManager history;
	
	private Metaproject metaproject;
	
	private static Set<Operation> operations;

	public NCIEditTab() {
		setToolTipText("Custom Editor for NCI");
	}
	
	

    @Override
	public void initialise() {
		super.initialise();
		//.history = this.getOWLModelManager().getHistoryManager();
		log.info("NCI Edit Tab initialized");
		
		metaproject = MetaprojectGen.getMetaproject();
		
		
		
		try {
			// need a clean way, maybe enums, to associate Operations to ids/names
			
			User bob = metaproject.getUserRegistry().getUser(Utils.getUserId("001"));
			Project proj = metaproject.getProjectRegistry().getProject(Utils.getProjectId("001"));
			MERGE = metaproject.getOperationRegistry().getOperation(Utils.getOperationId("002"));
			operations = metaproject.getPolicy().getOperationsInProject(bob.getId(), proj.getId());
			
			
		} catch (UserNotInPolicyException | ProjectNotInPolicyException |
				UnknownUserIdException | UnknownProjectIdException | UnknownOperationIdException e) {
			e.printStackTrace();
		}
		
		log.info("NCI Metaproject loaded" + metaproject);
		
		
		
	}
    
    public static boolean canMerge() {
    	return operations.contains(MERGE);
    }

	@Override
	public void dispose() {
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
}
