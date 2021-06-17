package gov.nih.nci.ui;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import gov.nih.nci.curator.IncrementalReasoner;
import gov.nih.nci.curator.owlapi.NCICurator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Oct-2007<br><br>
 */
public class UnsupportedConstructsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLAxiom, OWLAxiom>{

    public UnsupportedConstructsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, "Unsupported Constructs", "Unsupported Constructs", frame);
    }


    protected void clear() {

    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }


    public OWLObjectEditor<OWLAxiom> getObjectEditor() {
    	return null;
        
    }


    protected void refill(OWLOntology ontology) {
    }


    @SuppressWarnings("rawtypes")
	protected void refillInferred() {
    	try {
    		if (getOWLModelManager().getReasoner() instanceof IncrementalReasoner) {
    			NCICurator curator = ((IncrementalReasoner) getOWLModelManager().getReasoner()).getReasoner();
    			
    			Map<OWLClass, Set<OWLAxiom>> bad_cons = curator.getBadConstructs();
    			
    			for (OWLClass cls : bad_cons.keySet()) {
    				for (OWLAxiom ax : bad_cons.get(cls)) {
    					UnsupportedConstructsFrameSectionRow newRow = 
    							new UnsupportedConstructsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax);
    					addInferredRowIfNontrivial(newRow);

    				}
    			}

    			Map<OWLClass, Set<OWLAxiom>> bad_roles = curator.getBadRoles();

    			for (OWLClass cls : bad_roles.keySet()) {
    				for (OWLAxiom ax : bad_roles.get(cls)) {
    					UnsupportedConstructsFrameSectionRow newRow = 
    							new UnsupportedConstructsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax);
    					newRow.setEditingHint(" - Domain/Range issue");
    					addInferredRowIfNontrivial(newRow);

    				}
    			}
    			
    		}
    		
    	}
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 
  

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
        return false;
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom>> getRowComparator() {
        return (o1, o2) -> {

            int diff = o1.getAxiom().compareTo(o2.getAxiom());
            if(diff != 0) {
                return diff;
            }
            else if (o1.getOntology() == null  && o2.getOntology() == null) {
                return 0;
            }
            else if (o1.getOntology() == null) {
                return -1;
            }
            else if (o2.getOntology() == null) {
                return +1;
            }
            else {
                return o1.getOntology().compareTo(o2.getOntology());
            }
        };
    }
}
