package gov.nih.nci.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import gov.nih.nci.ui.PropertyTableModel.LiteralExtractor;

public final class PropertyUtil {
	//private static PropertyUtil util;	
	private List<OWLAnnotationProperty> annProps;
	
	private PropertyUtil() {
		//util = this;
	}
	
	/*public void setAnnotationProperties(OWLAnnotationProperty complexProperty) {
		annProps = NCIEditTab.currentTab().getConfiguredAnnotationsForAnnotation(complexProperty);
	}*/
	
	public static Map<String, String> getDefaultSelectedPropertyLabel(List<OWLAnnotationProperty> annProps) {
		Map<String, String> propertyLabels = new HashMap<String, String>();
		propertyLabels.put("Value", "Value");
		Iterator<OWLAnnotationProperty> iter = annProps.iterator();
		
		while(iter.hasNext()) {
			OWLAnnotationProperty annProp = iter.next();
			String propShortForm = annProp.getIRI().getShortForm();
			String columnName = NCIEditTab.currentTab().getRDFSLabel(annProp).get();
			propertyLabels.put(propShortForm, columnName);
		}
		
		return propertyLabels;
	}
	
	/*public String getDefaultColumnName(int column) {
		if (column == 0) {
			return NCIEditTabConstants.PROPTABLE_VALUE_COLUMN;
		}
		return NCIEditTab.currentTab().getRDFSLabel(requiredAnnotationsList.get(column-1)).get();
	}*/
	
	public static Map<String, String> getSelectedPropertyType(List<OWLAnnotationProperty> annProps) {
		Map<String, String> propertyTypes = new HashMap<String, String>();
		propertyTypes.put("Value", "TextArea");
		Iterator<OWLAnnotationProperty> iter = annProps.iterator();
		IRI dataType;
		
		while(iter.hasNext()) {
			OWLAnnotationProperty annProp = iter.next();
			String propShortForm = annProp.getIRI().getShortForm();
			dataType = NCIEditTab.currentTab().getDataType(annProp);
			
			if (dataType != null) {

				if (isDataTypeTextArea(dataType)) {
					propertyTypes.put(propShortForm, "TextArea");
				} else if (isDataTypeTextField(dataType)) {
					propertyTypes.put(propShortForm, "TextField");
				} else if (isDataTypeCombobox(dataType)) {
					propertyTypes.put(propShortForm, "ComboBox");
				}
			} else {
				propertyTypes.put(propShortForm, "TextField");				
			}
		}
		return propertyTypes;
	}
	
	public static Map<String, String> getDefaultPropertyValues(List<OWLAnnotationProperty> annProps) {
		Map<String, String> propertyValues = new HashMap<String, String>();
		Iterator<OWLAnnotationProperty> iter = annProps.iterator();
		
		while(iter.hasNext()) {
			OWLAnnotationProperty annProp = iter.next();
			String propShortForm = annProp.getIRI().getShortForm();
			propertyValues.put(propShortForm,
					NCIEditTab.currentTab().getDefaultValue(NCIEditTab.currentTab().getDataType(annProp), NCIEditTabConstants.DEFAULT_SOURCE_NEW_PROPERTY));
		}
		return propertyValues;
	}
	
	public static Map<String, ArrayList<String>> getSelectedPropertyOptions(List<OWLAnnotationProperty> annProps) {
		Map<String, ArrayList<String>> propertyOptions = new HashMap<String, ArrayList<String>>();
		Iterator<OWLAnnotationProperty> iter = annProps.iterator();
		
		while(iter.hasNext()) {
			OWLAnnotationProperty annProp = iter.next();
			String propShortForm = annProp.getIRI().getShortForm();
			if (isDataTypeCombobox(NCIEditTab.currentTab().getDataType(annProp))) {
				ArrayList<String> optionList = new ArrayList<String>();
				optionList.addAll(NCIEditTab.currentTab().getEnumValues(NCIEditTab.currentTab().getDataType(annProp)));
				propertyOptions.put(propShortForm, optionList);
			}
		}
		return propertyOptions;
	}
	
	public static Map<String, String> getSelectedPropertyValues(List<OWLAnnotation> annotations) {
		Map<String, String> propertyValues = new HashMap<String, String>();
		LiteralExtractor literalExtractor = new LiteralExtractor();
		String propShortForm = null;
		
		for (OWLAnnotation annotation : annotations) {
			
			if (annotation != null) {
				propShortForm = annotation.getProperty().getIRI().getShortForm();
			
				// TODO: temporarily hardcode this, certain annotations always use system defaults
				// even when diting an existing row. We need to add this as a property of the annotation
				// or otherwise distinguish in ghte config file
				if (propShortForm.equals("Definition_Review_Date") ||
						propShortForm.equals("Definition_Reviewer_Name")) {
					OWLAnnotationProperty p = annotation.getProperty();
					propertyValues.put(propShortForm,
							NCIEditTab.currentTab().getDefaultValue(NCIEditTab.currentTab().getDataType(p), ""));
				} else {

					propertyValues.put(propShortForm, literalExtractor.getLiteral(annotation.getValue()));
				}
			} else {
				propertyValues.put(propShortForm, null);
			}
		}

		return propertyValues;
	}
	
	private static boolean isDataTypeTextArea( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().toUpperCase().contains("TEXTAREA")) {
			result = true;
		}
		return result;
	}
	
	private static boolean isDataTypeTextField( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().contains("string")) {
			result = true;
		}
		if (dataType.toString().endsWith("system")) {
			return true;
		}
	
		return result;
	}
	
	private static boolean isDataTypeCombobox( IRI dataType ) {
		boolean result = false;
		if (dataType != null) {
			if (dataType.toString().endsWith("enum")) {
				result = true;
			}
		}
		return result;
	}

	static class LiteralExtractor implements OWLAnnotationValueVisitor {

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
}
