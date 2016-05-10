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
				NCIEditTab.currentTab().canRetire());
	}

	@Override
	public void retireClass() {
		
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
				NCIEditTab.currentTab().canClone());
	}

	@Override
	public void cloneClass() {
		
	}

	@Override
	public boolean canSplitClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canSplit());
	}

	@Override
	public void splitClass() {
		OWLEntityCreationSet<OWLClass> set = NCIClassCreationDialog.showDialog(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class);
		OWLClass selectedClass = getSelectedEntity();
		
		
        if (set != null){
            Map<IRI, IRI> replacementIRIMap = new HashMap<>();
            replacementIRIMap.put(selectedClass.getIRI(), set.getOWLEntity().getIRI());
            OWLModelManager mngr = getOWLModelManager();
            OWLObjectDuplicator dup = new OWLObjectDuplicator(mngr.getOWLDataFactory(), replacementIRIMap);
            List<OWLOntologyChange> changes = new ArrayList<>(set.getOntologyChanges());

            changes.addAll(duplicateClassAxioms(selectedClass, dup));
            
            changes.addAll(duplicateAnnotations(selectedClass, dup));

           

            
            
            OWLClass newClass = set.getOWLEntity();
            
            IRI codeIri = IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl#C-00000029");
            
            String code = newClass.getIRI().getRemainder().or("NONE");
            
            OWLDataFactory df = mngr.getOWLDataFactory();
            
            OWLAnnotationProperty codeProp = df.getOWLAnnotationProperty(codeIri);
            
            OWLLiteral con = df.getOWLLiteral(code);
            
            OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(codeProp, newClass.getIRI(), con);
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            
            mngr.applyChanges(changes);
            
            NCIEditTab.currentTab().splitClass(selectedClass, newClass);
            
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
        }
        
		
		
	}
	
	private List<OWLOntologyChange> duplicateClassAxioms(OWLClass selectedClass, OWLObjectDuplicator dup) {
		List<OWLOntologyChange> changes = new ArrayList<>();

		OWLOntology ont = getOWLModelManager().getActiveOntology();

		for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
			if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
				OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
				changes.add(new AddAxiom(ont, duplicatedAxiom));
			}
		}

		return changes;
	}

    private List<OWLOntologyChange> duplicateAnnotations(OWLClass selectedClass, OWLObjectDuplicator dup) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        OWLModelManagerEntityRenderer ren = getOWLModelManager().getOWLEntityRenderer();
        List<IRI> annotIRIs = null;
        String selectedClassName = null;
        if (ren instanceof OWLEntityAnnotationValueRenderer){
            selectedClassName = getOWLModelManager().getRendering(selectedClass);
            annotIRIs = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        }

        LiteralExtractor literalExtractor = new LiteralExtractor();

        OWLOntology ont = getOWLModelManager().getActiveOntology();
        
        IRI codeIri = IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl#C-00000029");
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        
        OWLAnnotationProperty codeProp = df.getOWLAnnotationProperty(codeIri);
        
        for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(selectedClass, ont)) {
        	final OWLAnnotation annot = ax.getAnnotation();
        	if (annotIRIs == null || !annotIRIs.contains(annot.getProperty().getIRI())) {
        		if (!annot.getProperty().equals(codeProp)) {
        			String label = literalExtractor.getLiteral(annot.getValue());
        			if (label == null || !label.equals(selectedClassName)){
        				OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
        				changes.add(new AddAxiom(ont, duplicatedAxiom));
        			}
        		}
        	}
        }
        
        return changes;
    }


    class LiteralExtractor implements OWLAnnotationValueVisitor {

        private String label;

        public String getLiteral(OWLAnnotationValue value){
            label = null;
            value.accept(this);
            return label;
        }

        public void visit(IRI iri) {
            // do nothing
        }


        public void visit(OWLAnonymousIndividual owlAnonymousIndividual) {
            // do nothing
        }


        public void visit(OWLLiteral literal) {
            label = literal.getLiteral();
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
