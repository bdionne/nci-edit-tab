package gov.nih.nci.ui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.Icon;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

import gov.nih.nci.ui.action.CloneClassTarget;
import gov.nih.nci.ui.action.MergeClassTarget;
import gov.nih.nci.ui.action.RetireClassTarget;
import gov.nih.nci.ui.action.SplitClassTarget;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;

public class NCIToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
implements CreateNewChildTarget, SplitClassTarget, CloneClassTarget, MergeClassTarget,
RetireClassTarget {
	
	private static final Icon ADD_SUB_ICON = OWLIcons.getIcon("class.add.sub.png");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

	public NCIToldOWLClassHierarchyViewComponent() {}	

	public void performExtraInitialisation() throws Exception {

		addAction(new AbstractOWLTreeAction<OWLClass>("Add subclass", ADD_SUB_ICON, getTree().getSelectionModel()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				createNewChild();
			}

			protected boolean canPerform(OWLClass cls) {
				return canCreateNewChild();
			}
		}, "A", "A");
		
		getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
            public boolean canDrop(Object child, Object parent) {
                return false;
            }
            public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {}
            public void add(OWLClass child, OWLClass parent) {}
        });
        getAssertedTree().setPopupMenuId(new PopupMenuId("[NCIAssertedClassHierarchy]")); 
		

    }

	@Override
	public boolean canRetireClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canRetire(getSelectedEntity()));
	}

	@Override
	public void retireClass() {
		OWLClass selectedClass = getSelectedEntity();
		NCIEditTab.currentTab().retire(selectedClass);
		
	}

	@Override
	public boolean canMergeClass() {
		return (getSelectedEntities().size() == 2 &&
				NCIEditTab.currentTab().canMerge());
	}

	@Override
	public void mergeClass() {
		System.out.println("OK, do the merge....");
	}

	@Override
	public boolean canCloneClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canClone(getSelectedEntity()));
	}

	@Override
	public void cloneClass() {
		
	}

	@Override
	public boolean canSplitClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canSplit(getSelectedEntity()));
	}

	@Override
	public void splitClass() {
		OWLEntityCreationSet<OWLClass> set = NCIClassCreationDialog.showDialog(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class);
		OWLClass selectedClass = getSelectedEntity();


		if (set != null){

			NCIEditTab.currentTab().splitClass(set, selectedClass);

			getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
		}
	}
	
	

	@Override
	public boolean canCreateNewChild() {
		return (getSelectedEntities().size() == 1);
	}

	@Override
	public void createNewChild() {
		OWLEntityCreationSet<OWLClass> set = NCIClassCreationDialog.showDialog(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class);
		
		OWLClass newClass = set.getOWLEntity();
		String preferredName = newClass.getIRI().getRemainder().or("NONE");
		
		String gen_code = NCIEditTab.currentTab().generateCode();
		
		OWLEntityCreationSet<OWLClass> newSet = null;
		
		try {
			newSet = getOWLEditorKit().getModelManager().getOWLEntityFactory().createOWLEntity(
					OWLClass.class, gen_code, null);
			
			newClass = newSet.getOWLEntity();
			
			
            OWLClass selectedClass = getSelectedEntity();
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.addAll(newSet.getOntologyChanges());
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(selectedClass)){
                OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(newSet.getOWLEntity(), selectedClass);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            
            
           
            
            OWLLiteral con = df.getOWLLiteral(gen_code);
            OWLLiteral pref_name_val = df.getOWLLiteral(preferredName);
            
            OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(NCIEditTab.CODE_PROP, newClass.getIRI(), con);
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            OWLAxiom ax2 = df.getOWLAnnotationAssertionAxiom(NCIEditTab.LABEL_PROP, newClass.getIRI(), pref_name_val);
            OWLAxiom ax3 = df.getOWLAnnotationAssertionAxiom(NCIEditTab.PREF_NAME, newClass.getIRI(), pref_name_val);
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax2));
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax3));
            
            mngr.applyChanges(changes);
            getTree().setSelectedOWLObject(newClass);
		} catch (OWLEntityCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	 protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
	        return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
	    }

	    @Override
	    protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
	        return Optional.of(getOWLModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider());
	    }

	
}
