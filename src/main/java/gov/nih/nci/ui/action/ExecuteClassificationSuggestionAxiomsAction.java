package gov.nih.nci.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.VacuousAxiomVisitor;
import org.protege.editor.owl.server.api.CommitBundle;
import org.protege.editor.owl.server.policy.CommitBundleImpl;
import org.protege.editor.owl.server.versioning.Commit;
import org.protege.editor.owl.server.versioning.api.ChangeHistory;
import org.protege.editor.owl.server.versioning.api.DocumentRevision;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nih.nci.ui.NCIEditTab;


/**
 * Author: Bob Dionne
 *
 */
public class ExecuteClassificationSuggestionAxiomsAction extends ProtegeOWLAction {

    
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(ExecuteClassificationSuggestionAxiomsAction.class);

    private OWLModelManagerListener listener = event -> {
        if (event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
            updateState();
        }
    };


	public void actionPerformed(ActionEvent e) {
		try {
			UIHelper uiHelper = new UIHelper(getOWLEditorKit());

			if (NCIEditTab.currentTab().isEditing()) {
				JOptionPane.showMessageDialog(this.getWorkspace(), 
						"Must save or clear ongoing edits first",
						"Can't execute suggestions",						 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			int result = uiHelper.showOptionPane("Perform edits suggested by classification?",
					"Do you want to add or remove relationships " + "suggested by the classifier?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				this.performAction();
			}

		} catch (Exception ex) {
			logger.error("An error occurred while performing edits suggested by classifier.", ex);
		}
	}


    private void updateState() {
        setEnabled(getOWLModelManager().isActiveOntologyMutable());
    }


    public void initialise() throws Exception {
        getOWLModelManager().addListener(listener);
        updateState();
    }
    
    public void performAction() {
		try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology inferredOnt = man.createOntology(IRI.create("http://another.com/ontology" + System.currentTimeMillis()));
            InferredOntologyGenerator ontGen = new InferredOntologyGenerator(getOWLModelManager().getReasoner(), new ArrayList<>());
            ontGen.addGenerator(new InferredSubClassAxiomGenerator());
           
            ontGen.fillOntology(man.getOWLDataFactory(), inferredOnt);
            
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            
            for (OWLAxiom ax : new TreeSet<>(inferredOnt.getAxioms())) {
                boolean add = true;
                if (getOWLModelManager().getActiveOntology().containsAxiom(ax)) {
                	add = false;
                }
                
                if (add) {
                	processAxiom(ax, changes);
                	
                }
            } 
            
            NCIEditTab.currentTab().disableHistoryRecording();
                 	
            getOWLModelManager().applyChanges(changes);
            getOWLEditorKit().getSearchManager().updateIndex(changes);
            
            ClientSession clientSession = ClientSession.getInstance(getOWLEditorKit());
            String comment = "Edits recommended by classifier";            
            Commit commit = ClientUtils.createCommit(clientSession.getActiveClient(), comment, changes);
    		DocumentRevision base = clientSession.getActiveVersionOntology().getHeadRevision();
    		CommitBundle commitBundle = new CommitBundleImpl(base, commit);
    		
    		ChangeHistory hist = clientSession.getActiveClient().commit(clientSession.getActiveProject(), commitBundle);
    		clientSession.getActiveVersionOntology().update(hist);
    		
    		NCIEditTab.currentTab().enableHistoryRecording();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void processAxiom(OWLAxiom ax, List<OWLOntologyChange> changes) {
    	if (VacuousAxiomVisitor.isVacuousAxiom(ax) || VacuousAxiomVisitor.involvesInverseSquared(ax)) {
    		return;
    	}
    	if (isInconsistent(ax)) {
    		return;
    	} else {
    		
    		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    		
    		List<OWLAxiom> supsToRemove = findCommonParents(ax);
    		for (OWLAxiom ax1 : supsToRemove) {
    			changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(), ax1));
    			
    		}
    	}
    }
	
	private boolean isInconsistent(OWLAxiom ax) {
    	if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
    		OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
    		return subax.getSuperClass().isOWLNothing();    
    	}
    	return false;
    }
	
	private List<OWLAxiom> findCommonParents(OWLAxiom newAxiom) {
    	List<OWLAxiom> results = new ArrayList<OWLAxiom>();
    	if (newAxiom.isOfType(AxiomType.SUBCLASS_OF)) {
    		OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) newAxiom;
    		OWLClass cls = subax.getSubClass().asOWLClass();
    		if (!subax.getSuperClass().isAnonymous()) {
    			OWLClass newParent = subax.getSuperClass().asOWLClass();
    			List<OWLClass> assertedParents = getAssertedParents(cls);
    			List<OWLClass> newParentAssertedParents = getAssertedParents(newParent);
    			for (OWLClass ap : assertedParents) {
    				if (newParentAssertedParents.contains(ap)) {
    					OWLAxiom newAx = 
    							getOWLModelManager().getOWLDataFactory().getOWLSubClassOfAxiom(cls, ap);
    					results.add(newAx);
    				}
    			}    			
    		}
    	}
    	return results;
    }
	
	private List<OWLClass> getAssertedParents(OWLClass cls) {
    	List<OWLClass> parents = new ArrayList<OWLClass>();
    	Set<OWLSubClassOfAxiom> axs = 
    			getOWLModelManager().getActiveOntology().getSubClassAxiomsForSubClass(cls);
    	
    	for(OWLSubClassOfAxiom ax : axs) {
    		if (!ax.getSuperClass().isAnonymous()) {
    			parents.add(ax.getSuperClass().asOWLClass());
    		}
    	}
    	
    	return parents;
    }


    public void dispose() {
        getOWLModelManager().removeListener(listener);
    }
}
