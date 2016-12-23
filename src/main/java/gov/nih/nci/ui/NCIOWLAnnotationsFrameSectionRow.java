package gov.nih.nci.ui;

import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLOntology;

public class NCIOWLAnnotationsFrameSectionRow extends OWLAnnotationsFrameSectionRow {
	
	private Set<OWLAnnotationProperty> propsToExclude;
	
	public NCIOWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit, 
			OWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> section, 
			OWLOntology ontology,
			OWLAnnotationSubject rootObject, Set<OWLAnnotationProperty> exclude, OWLAnnotationAssertionAxiom axiom, boolean isEditable) {
		super(owlEditorKit, section, ontology, rootObject, axiom, isEditable);
		propsToExclude = exclude;
	}

	public NCIOWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit,
			OWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> section,
			OWLOntology ontology, OWLAnnotationSubject rootObject, 
			Set<OWLAnnotationProperty> exclude, OWLAnnotationAssertionAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
		propsToExclude = exclude;
		// TODO Auto-generated constructor stub
	}
	
	protected OWLObjectEditor<OWLAnnotation> getObjectEditor() {
        NCIOWLAnnotationEditor editor = new NCIOWLAnnotationEditor(getOWLEditorKit(),
        		propsToExclude);
        editor.setEditedObject(getAxiom().getAnnotation());
        return editor;
    }
	
	
	
	
}

