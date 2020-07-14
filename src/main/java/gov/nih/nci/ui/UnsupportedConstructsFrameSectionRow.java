package gov.nih.nci.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.List;
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
public class UnsupportedConstructsFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom> {

    private OWLAxiom axiom;
    
    private String editingHint = "";
    
    public void setEditingHint(String s) {
    	editingHint = s;
    }


    public UnsupportedConstructsFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLOntology, OWLAxiom, OWLAxiom> section, OWLOntology ontology, OWLOntology rootObject,
                                         OWLAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.axiom = axiom;
    }


    protected OWLAxiom createAxiom(OWLAxiom editedObject) {
        return editedObject;
    }


    protected OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    public List<OWLAxiom> getManipulatableObjects() {
  
        return Arrays.asList(axiom);
    }
    
    public String getRendering() {
    	return super.getRendering() + editingHint;
    }
}
