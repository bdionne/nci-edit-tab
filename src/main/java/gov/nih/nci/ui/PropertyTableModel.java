package gov.nih.nci.ui;

import java.util.ArrayList;
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
    		return this.requiredAnnotations.size();
    	} else {
    		return 0;
    	}
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
    	
    	int i = 0;
    	LiteralExtractor literalExtractor = new LiteralExtractor();
    	for (OWLAnnotationProperty aprop : requiredAnnotations) {
    		if (columnIndex == i) {
    			for (OWLAnnotation annot : annotations) {
    				if (annot.getProperty().equals(aprop)) {
    					return literalExtractor.getLiteral(annot.getValue());
    				}
    			}
    		} else {
    			i++;
    		}
    	}
    	return i;
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
    	
    	return NCIEditTab.currentTab().getRDFSLabel(requiredAnnotationsList.get(column)).get();
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
    
}
