package gov.nih.nci.ui.dialog;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import gov.nih.nci.ui.NCIEditTab;

public class ComplexPropChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<OWLAnnotationProperty> complex_props;
	private String[] prop_strings;
	
	private JComboBox<String> combobox;
	
	
	public ComplexPropChooser(Set<OWLAnnotationProperty> props) {
		complex_props = props;
		prop_strings = populateStrings(complex_props);
		createUI();
	}
	
	private String[] populateStrings(Set<OWLAnnotationProperty> props) {
		List<String> strs = new ArrayList<String>();
		for (OWLAnnotationProperty p : props) {
			Optional<String> optl = NCIEditTab.currentTab().getRDFSLabel(p);
			if (optl.isPresent()) {
				strs.add(optl.get());
			}
		}
		String[] res = new String[strs.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = strs.get(i);
		}
		return res;
		
	}
	
	private OWLAnnotationProperty findProp(String s) {
		for (OWLAnnotationProperty p : complex_props) {
			Optional<String> optl = NCIEditTab.currentTab().getRDFSLabel(p);
			if (optl.isPresent()) {
				if (optl.get().equalsIgnoreCase(s)) {
					return p;
				}
				
			}
			
		}
		return null;
		
	}
	
	
    
   
    
    private JPanel createComboBoxPanel() {
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	combobox = new JComboBox<String>(prop_strings);   	
    	combobox.setPreferredSize(new Dimension(180, 20));
    	
    	
    	JLabel label = new JLabel("Select a complex property");  	   	
    	label.setPreferredSize(new Dimension(180, 20));
    	
    	panel.add(label, BorderLayout.WEST);
    	panel.add(combobox, BorderLayout.EAST);
    	panel.setPreferredSize(new Dimension(400, 25));
    	
    	return panel;
    }
    
    private void createUI(){
    	
    	
    	this.setLayout(new BorderLayout()); 
       
   		
   		JPanel comboboxpanel = createComboBoxPanel();
   		this.add(comboboxpanel);
   	}
    
    public OWLAnnotationProperty getChosenProp() {
    	return findProp((String) combobox.getSelectedItem());
    }
    
    public  OWLAnnotationProperty showDialog(OWLEditorKit owlEditorKit, String title) {    	
        int ret = new UIHelper(owlEditorKit).showDialog(title, this, JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.OK_OPTION) {
            return this.getChosenProp();
        }
        else {
            return null;
        }
    }
    
   
    
}
