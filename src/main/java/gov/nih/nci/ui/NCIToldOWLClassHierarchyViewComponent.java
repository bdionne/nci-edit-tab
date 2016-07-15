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

import gov.nih.nci.ui.action.AddComplexTarget;
import gov.nih.nci.ui.action.CloneClassTarget;
import gov.nih.nci.ui.action.MergeClassTarget;
import gov.nih.nci.ui.action.RetireClassTarget;
import gov.nih.nci.ui.action.SplitClassTarget;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;

public class NCIToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
implements CreateNewChildTarget, SplitClassTarget, CloneClassTarget, MergeClassTarget,
RetireClassTarget, AddComplexTarget {
	
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
		NCIClassCreationDialog dlg = new NCIClassCreationDialog(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class);
		if (dlg.showDialog()) {
			NCIEditTab.currentTab().splitClass(dlg.getNewClass(), dlg.getOntChanges(), getSelectedEntity());
			getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(dlg.getNewClass());
			
		}

			
		
	}
	
	

	@Override
	public boolean canCreateNewChild() {
		return (getSelectedEntities().size() == 1);
	}

	@Override
	public void createNewChild() {	
				
		OWLClass selectedClass = getSelectedEntity();
		
		OWLClass newCls = NCIEditTab.currentTab().createNewChild(selectedClass);
		
		getTree().setSelectedOWLObject(newCls);
		
	}
	
	 protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
	        return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
	    }

	    @Override
	    protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
	        return Optional.of(getOWLModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider());
	    }

		@Override
		public boolean canAddComplex() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void addComplex() {
			OWLClass selectedClass = getSelectedEntity();
			NCIEditTab.currentTab().addComplex(selectedClass);
			// TODO Auto-generated method stub
			
		}

	
}
