package gov.nih.nci.ui;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;


public class BatchEditIRIsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	OWLOntology ontology;
	OWLEditorKit owlEditKit;
	private static final int COLUMN_COUNT = 3;
	private static final int FIRST_COLUMN = 0;
	private static final int SECOND_COLUMN = 1;
	private static final int THIRD_COLUMN = 2;
	private static final String FIRST_COLUMN_NAME = "RDFS Label";
	private static final String SECOND_COLUMN_NAME = "IRI Fragment";
	private static final String THIRD_COLUMN_NAME = "IRI";
	private static final String EMPTY_STRING = "";
	private Set<OWLAnnotationProperty> annProps;
	private Set<OWLObjectProperty> objProps;
	private List<OWLProperty> props;
	
	public BatchEditIRIsTableModel(OWLEditorKit k) {
		owlEditKit = k;
		ontology = k.getOWLModelManager().getActiveOntology();
		initProps();
	}
	
	private void initProps() {
		props = new ArrayList<OWLProperty>();
		annProps = ontology.getAnnotationPropertiesInSignature();		
		objProps = ontology.getObjectPropertiesInSignature();
		
		if (annProps != null && !annProps.isEmpty()) {
			props.addAll(asSortedList(annProps));		
		}
		
		if (objProps != null && !objProps.isEmpty()) {
			props.addAll(asSortedList(objProps));
		}
		
	}
	
	public int getRowCount() {
		return annProps.size() + objProps.size(); 		
	}


	public int getColumnCount() {
		
		return COLUMN_COUNT;
		
	}
	
	public String getColumnName(int column) {

		if (column == FIRST_COLUMN) {
			return FIRST_COLUMN_NAME;
		} else if (column == SECOND_COLUMN) {
			return SECOND_COLUMN_NAME;
		} else if (column == THIRD_COLUMN) {
			return THIRD_COLUMN_NAME;
		}
		return EMPTY_STRING;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		if (props != null && !props.isEmpty()) {
			if (rowIndex < props.size()) {
				OWLProperty prop = props.get(rowIndex);
				if (columnIndex == FIRST_COLUMN) {
					return getRDFSLabel(prop);
				} else if (columnIndex == SECOND_COLUMN) {
					return prop.getIRI().getShortForm();
				} else if (columnIndex == THIRD_COLUMN) {
					return prop.getIRI();
				}
			}
		}
		return null;
	}

	private String getRDFSLabel(OWLProperty prop) {
		if (prop != null) {
			return getRDFSLabelnoPopup(prop).get();
		}
		return EMPTY_STRING;
	}
	
	private Optional<String> getRDFSLabelnoPopup(OWLNamedObject oobj) {
		// TODO: fall back to IRI if no label
		if (oobj == null) {
			return Optional.empty();
		}
		if (topOrBot(oobj)) {
			return Optional.of(oobj.getIRI().getShortForm());			
		}
		if (ontology != null) {
			for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
					.getRDFSLabel())) {
				OWLAnnotationValue av = annotation.getValue();
				com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					return Optional.of(ol.get().getLiteral());
				}
			}
		}

		return Optional.of(oobj.getIRI().getShortForm());


	}
	
	private boolean topOrBot(OWLNamedObject obj) {
		if (ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().equals(obj) 
			|| ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing().equals(obj)) {
			return true;
		}
		
		return false;
	}
	

 	
	private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list, new Comparator<T>() {

		  @Override
			public int compare(T o1, T o2) {
				// single quotes are used by Protege-OWL when the browser
				// text has space in it, but they are displayed without the
				// quotes
				// this messes up the sort order
				String s1 = ((OWLProperty)o1).getIRI().getShortForm();
				String s2 = ((OWLProperty)o2).getIRI().getShortForm();
				if (s1.startsWith("'")) {
					s1 = s1.substring(1, s1.length() - 1);
				}
				if (s2.startsWith("'")) {
					s2 = s2.substring(1, s2.length() - 1);
				}
				return s1.compareTo(s2);
			}

		});
	  return list;
	}
}
