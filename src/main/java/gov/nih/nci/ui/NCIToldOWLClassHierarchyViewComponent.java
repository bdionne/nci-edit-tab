package gov.nih.nci.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.tree.OWLTreePreferences;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

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
				NCIEditTab.canRetire());
	}

	@Override
	public void retireClass() {
		
	}

	@Override
	public boolean canMergeClass() {
		return (getSelectedEntities().size() == 2 &&
				NCIEditTab.canMerge());
	}

	@Override
	public void mergeClass() {
		System.out.println("OK, do the merge....");
	}

	@Override
	public boolean canCloneClass() {
		return (getSelectedEntities().size() == 1);
	}

	@Override
	public void cloneClass() {
		
	}

	@Override
	public boolean canSplitClass() {
		return (getSelectedEntities().size() == 1);
	}

	@Override
	public void splitClass() {
		OWLEntityCreationSet<OWLClass> set = NCIClassCreationDialog.showDialog(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class);
		
		if (set != null) {
			OWLClass newClass = set.getOWLEntity();
			OWLClass selectedClass = getSelectedEntity();
			
			OWLAnnotationSubject newObject = (OWLAnnotationSubject)selectedClass.getIRI();
			
			updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
			//complexEditPanel.getUpperPanelList().setRootObject(newObject);
			//complexEditPanel.getLowerPanelList().setRootObject(newObject);
			//complexEditPanel.setEnableUnselectedRadioButtons(false);
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
		
        if (set != null){
            OWLClass newClass = set.getOWLEntity();
            System.out.println(newClass.getIRI().getRemainder().or("NONE"));
            OWLClass selectedClass = getSelectedEntity();
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.addAll(set.getOntologyChanges());
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(selectedClass)){
                OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(set.getOWLEntity(), selectedClass);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            
            IRI brcal = IRI.create("&owl2lexevs;Brca1");
            
            Set<OWLAxiom> refs = 
            		getOWLEditorKit().getOWLModelManager().getActiveOntology().getReferencingAxioms(selectedClass);
            
            System.out.println("The refs are: " + refs.toString());
            
            
            
            IRI codeIri = IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl#C-00000029");
            
            String code = newClass.getIRI().getRemainder().or("NONE");
            
            OWLAnnotationProperty codeProp = df.getOWLAnnotationProperty(codeIri);
            
            OWLLiteral con = df.getOWLLiteral(code);
            
            OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(codeProp, newClass.getIRI(), con);
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            
            mngr.applyChanges(changes);
            getTree().setSelectedOWLObject(newClass);
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
