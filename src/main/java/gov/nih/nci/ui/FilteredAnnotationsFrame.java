package gov.nih.nci.ui;

import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

public class FilteredAnnotationsFrame extends AbstractOWLFrame<OWLAnnotationSubject> {
	
	public FilteredAnnotationsFrame(OWLEditorKit man, Set<OWLAnnotationProperty> props) {
        super(man.getModelManager().getOWLOntologyManager());
        addSection(new NCIAnnotationFrameSection(man, this, props));
    }

}
