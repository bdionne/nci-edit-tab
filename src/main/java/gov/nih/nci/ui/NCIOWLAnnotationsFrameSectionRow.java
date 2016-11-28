package gov.nih.nci.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLOntology;

public class NCIOWLAnnotationsFrameSectionRow extends OWLAnnotationsFrameSectionRow {
	
	public NCIOWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit, 
			OWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> section, 
			OWLOntology ontology,
			OWLAnnotationSubject rootObject, OWLAnnotationAssertionAxiom axiom, boolean isEditable) {
		super(owlEditorKit, section, ontology, rootObject, axiom, isEditable);
	}

	public NCIOWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit,
			OWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> section,
			OWLOntology ontology, OWLAnnotationSubject rootObject, OWLAnnotationAssertionAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
		// TODO Auto-generated constructor stub
	}
	
	protected OWLObjectEditor<OWLAnnotation> getObjectEditor() {
        NCIOWLAnnotationEditor editor = new NCIOWLAnnotationEditor(getOWLEditorKit(),
        		NCIEditTab.currentTab().getComplexProperties());
        editor.setEditedObject(getAxiom().getAnnotation());
        return editor;
    }
	
	
}

