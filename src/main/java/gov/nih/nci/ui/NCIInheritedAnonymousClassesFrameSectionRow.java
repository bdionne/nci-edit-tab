package gov.nih.nci.ui;

import java.util.Arrays;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.preferences.GeneralPreferencesPanel;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;

public class NCIInheritedAnonymousClassesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLClassExpression> {
	
	private String dispAnon = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID)
			.getString(GeneralPreferencesPanel.ANON_ANCESTORS_DISPLAY, 
					GeneralPreferencesPanel.AnonDispNone);
	
	public NCIInheritedAnonymousClassesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClass, OWLClassAxiom, OWLClassExpression> section,
	            OWLOntology ontology, OWLClass rootObject, OWLClassAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
	}
	
	public NCIInheritedAnonymousClassesFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClass, OWLClassAxiom, OWLClassExpression> section,
            OWLOntology ontology, OWLClass rootObject, OWLClassAxiom axiom, boolean isEditable) {
		super(owlEditorKit, section, ontology, rootObject, axiom, isEditable);
	}
	
	protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
		if (getAxiom() instanceof OWLSubClassOfAxiom) {
			OWLClassExpression superCls = ((OWLSubClassOfAxiom) getAxiom()).getSuperClass();
			return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(superCls, AxiomType.SUBCLASS_OF);
		}
		else {
			Set<OWLClassExpression> descs = new HashSet<>(((OWLEquivalentClassesAxiom) getAxiom()).getClassExpressions());
			descs.remove(getRootObject());
			OWLClassExpression desc;
			if (descs.isEmpty()){
				// in the weird case that something is asserted equiv to itself
				desc = getRootObject();
			}
			else{
				desc = descs.iterator().next();
			}
			return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(desc, AxiomType.EQUIVALENT_CLASSES);
		}
	}
	
	
	protected OWLClassAxiom createAxiom(OWLClassExpression editedObject) {
		if (getAxiom() instanceof OWLSubClassOfAxiom) {
			return getOWLDataFactory().getOWLSubClassOfAxiom(getRoot(), editedObject);
		}
		else {
			return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRoot(),
	                                                        editedObject));
		}
	}
	
	
	public List<OWLClassExpression> getManipulatableObjects() {
		if (getAxiom() instanceof OWLSubClassOfAxiom) {
			return Arrays.asList(((OWLSubClassOfAxiom) getAxiom()).getSuperClass());
		}
		else {
			Set<OWLClassExpression> descs = new HashSet<>(((OWLEquivalentClassesAxiom) getAxiom()).getClassExpressions());
			descs.remove(getRootObject());
			if (descs.isEmpty()){
				// in the weird case that something is asserted equiv to itself
				OWLClassExpression cls = getRootObject();
				return Arrays.asList(cls);
			}
			return Arrays.asList(descs.iterator().next());
		}
	}
	
	public String getRendering() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPrefix());
        for (Iterator<? extends OWLObject> it = getManipulatableObjects().iterator(); it.hasNext();) {
            OWLObject obj = it.next();
            sb.append(getObjectRendering(obj));
            if (it.hasNext()) {
                sb.append(getDelimeter());
            }
        }
        if (dispAnon == GeneralPreferencesPanel.AnonDispAll) {
        	sb.append(" (" + getObjectRendering(getRootObject()) + ")");
        }
        sb.append(getSuffix());
        return sb.toString();
    }
	
	
	public String getTooltip() {
		return "Inherited from " + getOWLModelManager().getRendering(getRootObject());
	}
}
