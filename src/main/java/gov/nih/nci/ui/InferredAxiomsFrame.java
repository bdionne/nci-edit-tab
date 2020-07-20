package gov.nih.nci.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLOntology;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Oct-2007<br><br>
 */
public class InferredAxiomsFrame extends AbstractOWLFrame<OWLOntology> {

    public InferredAxiomsFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new InferredAxiomsFrameSection(owlEditorKit, this));
        addSection(new UnsupportedConstructsFrameSection(owlEditorKit, this));
    }
}
