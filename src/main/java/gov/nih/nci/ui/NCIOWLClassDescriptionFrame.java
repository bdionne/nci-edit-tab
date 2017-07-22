package gov.nih.nci.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.cls.OWLClassAssertionAxiomMembersSection;
import org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLDisjointClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLDisjointUnionAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLKeySection;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection;
import org.semanticweb.owlapi.model.OWLClass;

public class NCIOWLClassDescriptionFrame extends AbstractOWLFrame<OWLClass> {
	public NCIOWLClassDescriptionFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLEquivalentClassesAxiomFrameSection(editorKit, this));
        addSection(new OWLSubClassAxiomFrameSection(editorKit, this));
        addSection(new OWLClassGeneralClassAxiomFrameSection(editorKit, this));
        //addSection(new InheritedAnonymousClassesFrameSection(editorKit, this));
        addSection(new NCIInheritedAnonymousClassesFrameSection(editorKit, this));
        addSection(new OWLClassAssertionAxiomMembersSection(editorKit, this));
        addSection(new OWLKeySection(editorKit, this));
        addSection(new OWLDisjointClassesAxiomFrameSection(editorKit, this));
        addSection(new OWLDisjointUnionAxiomFrameSection(editorKit, this));
    }
}
