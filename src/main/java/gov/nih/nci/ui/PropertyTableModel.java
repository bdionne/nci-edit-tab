package gov.nih.nci.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import gov.nih.nci.ui.dialog.NCIClassCreationDialog;


public class PropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -702166512096011158L;

	OWLOntology ont;

	private OWLClass selection = null;
	
	public OWLClass getSelection() {return selection;}
	
	private OWLAnnotationProperty complexProp;
	
	public OWLAnnotationProperty getComplexProp() {
		return complexProp;
	}
	
	private Set<OWLAnnotationProperty> configuredAnnotations;
	private List<OWLAnnotationProperty> requiredAnnotationsList;
	private List<OWLAnnotation> annotations = new ArrayList<>();
	
	
	
	private List<OWLAnnotationAssertionAxiom> assertions = new ArrayList<OWLAnnotationAssertionAxiom>();
	
	public OWLAnnotationAssertionAxiom getAssertion(int idx) {
		return assertions.get(idx);
	}

	public PropertyTableModel(OWLEditorKit k, OWLAnnotationProperty complexProperty) {
		ont = k.getOWLModelManager().getActiveOntology();
		complexProp = complexProperty;
		configuredAnnotations = NCIEditTab.currentTab().getConfiguredAnnotationsForAnnotation(complexProp);
		requiredAnnotationsList = new ArrayList<OWLAnnotationProperty>(configuredAnnotations);
	}


	public int getRowCount() {
		if (annotations.size() > 0) {
			return annotations.size() / getColumnCount();
		}
		return 0;
		
	}


	public int getColumnCount() {
		if (this.configuredAnnotations != null) {
			return this.configuredAnnotations.size() + 1;
		} else {
			return 0;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		int index = rowIndex * getColumnCount() + columnIndex; 
		LiteralExtractor literalExtractor = new LiteralExtractor();

		if ( index < annotations.size() ) {
			OWLAnnotation annot = annotations.get(index);
			if (annot == null) {
				return null;
			} else {
				if (columnIndex == 0) {
					return literalExtractor.getLiteral(annot.getValue());
				}
				for (OWLAnnotationProperty aprop : configuredAnnotations) {

					if ( annot.getProperty().equals(aprop)) {
						return literalExtractor.getLiteral(annot.getValue());
					}

				}
			}
		}
		return null;
	}

	public Map<String, String> getSelectedPropertyLabel() {
		Map<String, String> propertyLabels = new HashMap<String, String>();
		propertyLabels.put("Value", "Value");
		int columnCount = getColumnCount();
		
		for (int i = 1; i < columnCount; i++) {
			String propShortForm = requiredAnnotationsList.get(i -1).getIRI().getShortForm();
			propertyLabels.put(propShortForm, getColumnName(i));
		}
		return propertyLabels;
	}
	
	public Map<String, String> getSelectedPropertyType() {
		Map<String, String> propertyTypes = new HashMap<String, String>();
		propertyTypes.put("Value", "TextArea");
		int columnCount = getColumnCount();
		IRI dataType;
		
		for (int i = 1; i < columnCount; i++) {
			String propShortForm = requiredAnnotationsList.get(i -1).getIRI().getShortForm();
			dataType = NCIEditTab.currentTab().getDataType(requiredAnnotationsList.get(i -1));

			if (isDataTypeTextArea(dataType)) {
				propertyTypes.put(propShortForm, "TextArea");
			} else if (isDataTypeTextField(dataType)) {
				propertyTypes.put(propShortForm, "TextField");
			} else if (isDataTypeCombobox(dataType)) {
				propertyTypes.put(propShortForm, "ComboBox");
			}
		}
		return propertyTypes;
	}

	public Map<String, String> getSelectedPropertyValues(int row) {
		Map<String, String> propertyValues = new HashMap<String, String>();
		if ( row < 0 ) {
			return propertyValues;
		}
		int columnCount = getColumnCount();
		int startIndex = row * columnCount;
		LiteralExtractor literalExtractor = new LiteralExtractor();
		String propShortForm;
		for (int i=0; i<columnCount; i++) {
			if (i==0) {
				propShortForm = "Value";
			} else {
				propShortForm = requiredAnnotationsList.get(i-1).getIRI().getShortForm();
			}
			OWLAnnotation annot = annotations.get(startIndex + i);
			if (annot != null) {
				// TODO: temporarily hardcode this, certain annotations always use system defaults
				// even when diting an existing row. We need to add this as a property of the annotation
				// or otherwise distinguish in ghte config file
				if (propShortForm.equals("Definition_Review_Date") ||
						propShortForm.equals("Definition_Reviewer_Name")) {
					OWLAnnotationProperty p = requiredAnnotationsList.get(i-1);
					propertyValues.put(propShortForm,
							NCIEditTab.currentTab().getDefaultValue(NCIEditTab.currentTab().getDataType(p)));
				} else {

					propertyValues.put(propShortForm, literalExtractor.getLiteral(annot.getValue()));
				}
			}
		}

		return propertyValues;
	}
	
	public Map<String, String> getDefaultPropertyValues() {
		Map<String, String> propertyValues = new HashMap<String, String>();
		
		for (OWLAnnotationProperty aprop : requiredAnnotationsList) {
			String propShortForm = aprop.getIRI().getShortForm();
			propertyValues.put(propShortForm,
					NCIEditTab.currentTab().getDefaultValue(NCIEditTab.currentTab().getDataType(aprop)));
		}
		return propertyValues;
	}

	public Map<String, ArrayList<String>> getSelectedPropertyOptions() {
		Map<String, ArrayList<String>> propertyOptions = new HashMap<String, ArrayList<String>>();
		for (OWLAnnotationProperty aprop : requiredAnnotationsList) {
			String propShortForm = aprop.getIRI().getShortForm();
			if (isDataTypeCombobox(NCIEditTab.currentTab().getDataType(aprop))) {
				ArrayList<String> optionList = new ArrayList<String>();
				optionList.addAll(NCIEditTab.currentTab().getEnumValues(NCIEditTab.currentTab().getDataType(aprop)));
				propertyOptions.put(propShortForm, optionList);
			}
		}
		return propertyOptions;
	}


	private boolean isDataTypeTextArea( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().toUpperCase().contains("TEXTAREA")) {
			result = true;
		}
		return result;
	}

	private boolean isDataTypeTextField( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().contains("string")) {
			result = true;
		}
		if (dataType.toString().endsWith("system")) {
			return true;
		}
	
		return result;
	}

	private boolean isDataTypeCombobox( IRI dataType ) {
		boolean result = false;
		//if (!isDataTypeTextArea(dataType) && !isDataTypeTextField(dataType)) {
		if (dataType.toString().endsWith("enum")) {
			result = true;
		}
		return result;
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

	public String getColumnName(int column) {

		if (column == 0) {
			return "Value";
		}
		return NCIEditTab.currentTab().getRDFSLabel(requiredAnnotationsList.get(column-1)).get();
	}

	public void setSelection(OWLClass cls) {

		this.selection = cls;
		loadAnnotations();
	}

	private void loadAnnotations() {

		if (selection != null) {
			annotations.clear();
			assertions.clear();
			for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(selection, ont)) {
				OWLAnnotation annot = ax.getAnnotation();
				if (annot.getProperty().equals(this.complexProp)) {
					assertions.add(ax);
					//annotations.add(annot);
					if (annot.getValue() != null) {

						annotations.add(annot);
					}
					Set<OWLAnnotation> annotSet = ax.getAnnotations();
					for (OWLAnnotationProperty req_a : configuredAnnotations) {
						OWLAnnotation found = null;
						for (OWLAnnotation owl_a : annotSet) {
							if (req_a.equals(owl_a.getProperty())) {
								found = owl_a;
							}
						}
						annotations.add(found);
					}
				}

			}  
			
		} else {
			annotations.clear();
		}

	}

	public boolean hasAnnotation() {
		return !annotations.isEmpty();
	}
	
}
