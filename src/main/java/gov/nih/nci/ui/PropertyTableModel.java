package gov.nih.nci.ui;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class PropertyTableModel extends AbstractTableModel {
   
	private static final long serialVersionUID = -702166512096011158L;

	OWLOntology ont;
    
    private OWLClass selection = null;


    public PropertyTableModel(OWLEditorKit k) {
        this.ont = k.getOWLModelManager().getActiveOntology();
    }


    public int getRowCount() {
    	if (selection != null) {
    		return ont.getAnnotationAssertionAxioms(selection.getIRI()).size();
    	} else {
    		return 0;
    	}
    }


    public int getColumnCount() {
        return 2;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
    	
    	Set<OWLAnnotationAssertionAxiom> props = ont.getAnnotationAssertionAxioms(selection.getIRI());
    	
             
        int i = 0;
        for (OWLAnnotationAssertionAxiom p : props) {
        	if (rowIndex == i) {
        		if (columnIndex == 0) {
        			
        			return p.getAnnotation();
        		} else {
        			return p.getValue();
        		}
        	} else {
        		i++;
        	}
        }
		return i;
    }


    public String getColumnName(int column) {
        if (column == 0) {
            return "Metric";
        }
        else {
            return "Value";
        }
    }
    
    public void setSelection(OWLClass cls) {
    	
    	this.selection = cls;
    }


    
}
