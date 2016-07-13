package gov.nih.nci.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	//private List<OWLAnnotationProperty> complexProperties;
	private OWLAnnotationProperty complexProp;
	private Set<OWLAnnotationProperty> requiredAnnotations;
	private List<OWLAnnotationProperty> requiredAnnotationsList;
	private List<OWLAnnotation> annotations = new ArrayList<>();

	public PropertyTableModel(OWLEditorKit k) {
		this.ont = k.getOWLModelManager().getActiveOntology();
		//complexProperties = new ArrayList<OWLAnnotationProperty>(NCIEditTab.currentTab().getComplexProperties());
	}

	public PropertyTableModel(OWLEditorKit k, OWLAnnotationProperty complexProperty) {
		this.ont = k.getOWLModelManager().getActiveOntology();
		this.complexProp = complexProperty;
		this.requiredAnnotations = NCIEditTab.currentTab().getRequiredAnnotationsForAnnotation(complexProp);
		this.requiredAnnotationsList = new ArrayList<OWLAnnotationProperty>(this.requiredAnnotations);
	}


	public int getRowCount() {
		int i = 0;

		if (selection != null) {
			//return ont.getAnnotationAssertionAxioms(selection.getIRI()).size();
			for (OWLAnnotation annot : annotations) {
				int j = 0;
				OWLAnnotationProperty annotProp = annot.getProperty();
				Iterator<OWLAnnotation> iter = annotations.iterator();
				while (iter.hasNext()) {
					OWLAnnotationProperty temp = iter.next().getProperty();
					if (temp.equals(annotProp)) {
						j++;
					}
				}
				if (j > i) {
					i=j;
				}
			}
			return i;
		} else {
			return 0;
		}
	}


	public int getColumnCount() {
		if (this.requiredAnnotations != null) {
			return this.requiredAnnotations.size() + 1;
		} else {
			return 0;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		int index = rowIndex * getColumnCount() + columnIndex;
		LiteralExtractor literalExtractor = new LiteralExtractor();

		OWLAnnotation annot = annotations.get(index);
		if (columnIndex == 0) {
			return literalExtractor.getLiteral(annot.getValue());
		}
		for (OWLAnnotationProperty aprop : requiredAnnotations) {

			if ( annot.getProperty().equals(aprop)) {
				return literalExtractor.getLiteral(annot.getValue());
			}

		}
		return null;
	}

	public HashMap<String, String> getSelectedPropertyType() {
		HashMap<String, String> propertyTypes = new HashMap<String, String>();
		propertyTypes.put("Value", "TextArea");
		int columnCount = getColumnCount();
		IRI dataType;
		String columnName;
		for (int i = 1; i < columnCount; i++) {
			dataType = NCIEditTab.currentTab().getDataType(requiredAnnotationsList.get(i -1));
			columnName = getColumnName(i);


			if (isDataTypeTextArea(dataType)) {
				propertyTypes.put(columnName, "TextArea");
			} else if (isDataTypeTextField(dataType)) {
				propertyTypes.put(columnName, "TextField");
			} else {
				propertyTypes.put(columnName, "ComboBox");
				propertyTypes.put(columnName, "TextField");
			}
		}
		return propertyTypes;
	}

	public HashMap<String, String> getSelectedPropertyValue(int row) {
		HashMap<String, String> propertyValues = new HashMap<String, String>();
		if ( row < 0 ) {
			//NCIClassCreationDialog.showDialog(this.ont,
			//"Please select a property row", OWLClass.class);
			return propertyValues;
		}
		int columnCount = getColumnCount();
		int startIndex = row * columnCount;
		LiteralExtractor literalExtractor = new LiteralExtractor();

		for (int i=0; i<columnCount; i++) {
			OWLAnnotation annot = annotations.get(startIndex + i);
			propertyValues.put(getColumnName(i), literalExtractor.getLiteral(annot.getValue()));
		}

		return propertyValues;
	}

	public HashMap<String, ArrayList<String>> getSelectedPropertyOptions() {
		HashMap<String, ArrayList<String>> propertyOptions = new HashMap<String, ArrayList<String>>();
		for (OWLAnnotationProperty aprop : requiredAnnotationsList) {

			if (isDataTypeCombobox(NCIEditTab.currentTab().getDataType(aprop))) {
				ArrayList<String> optionList = new ArrayList<String>();
				optionList.addAll(NCIEditTab.currentTab().getEnumValues(NCIEditTab.currentTab().getDataType(aprop)));
				propertyOptions.put(NCIEditTab.currentTab().getRDFSLabel(aprop).get(), optionList);
			}
		}
		return propertyOptions;
	}


	private boolean isDataTypeTextArea( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().contains("textarea")) {
			result = true;
		}
		return result;
	}

	private boolean isDataTypeTextField( IRI dataType ) {
		boolean result = false;
		if (dataType.toString().contains("string")) {
			result = true;
		}
		return result;
	}

	private boolean isDataTypeCombobox( IRI dataType ) {
		boolean result = false;
		if (!isDataTypeTextArea(dataType) && !isDataTypeTextField(dataType)) {
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
			for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(selection, ont)) {
				OWLAnnotation annot = ax.getAnnotation();
				if (annot.getProperty().equals(this.complexProp)) {
					//annotations.add(annot);
					if (annot.getValue() != null) {

						annotations.add(annot);
					}
					Set<OWLAnnotation> annotSet = ax.getAnnotations();
					Iterator<OWLAnnotation> iter = annotSet.iterator();
					while (iter.hasNext()) {
						annot = iter.next();
						if (this.requiredAnnotations.contains(annot.getProperty())) {
							annotations.add(annot);
						}
					}
				}

			}   
		}

	}

	public boolean hasAnnotation() {
		return !annotations.isEmpty();
	}
	
	public void addRow(HashMap<String, String> data) {
		         // remove a row from your internal data structure
		     	int columnCount = getColumnCount();
		     	for (int i = 0; i < columnCount; i++) {
		     		String columnName = getColumnName(i);
		     		String value = data.get(columnName);
		     		//OWLAnnotation annot = new OWLAnnotation()
		     		//OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
		     		
		     	}
		      		
		     	int row = getRowCount();
		         fireTableRowsInserted(row, row);
		     }
		     
		     /*public void updateRow(int row) {
		         // remove a row from your internal data structure
		         fireTableRowsUpdated(row, row);
		     }*/
		      
		     public void removeRow(int row) {
		         int columnCount = getColumnCount();
		         int minIndex = row * columnCount;
		         int maxIndex = (row + 1) * columnCount;
		         annotations.subList(minIndex, maxIndex).clear();
		         fireTableRowsDeleted(row, row);
		     }



}
