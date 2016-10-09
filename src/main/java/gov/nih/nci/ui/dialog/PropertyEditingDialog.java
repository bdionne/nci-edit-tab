package gov.nih.nci.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.protege.editor.owl.OWLEditorKit;

import org.protege.editor.owl.ui.UIHelper;

import gov.nih.nci.ui.NCIEditTabConstants;

public class PropertyEditingDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> proptypemap;
	private Map<String, String> propvaluemap;
	private Map<String, Object> propcomponentmap;
	private Map<String, ArrayList<String>> propoptions;
	private Map<String, String> proplabelmap;
	
	private String type;
	
    public PropertyEditingDialog(String type, Map<String, String> proptype, Map<String, String> propvalue, Map<String, ArrayList<String>> propoptions, Map<String, String> proplabel){
    	
      this.proptypemap = proptype;
      this.propvaluemap = propvalue;
      this.propoptions = propoptions;
      this.proplabelmap = proplabel;
      this.type = type;
      
      createUI();
    }
    
    private ArrayList<String> getTextAreaProperties (){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Iterator<String> itor = proptypemap.keySet().iterator();
    	
    	while(itor.hasNext()){
    		String key = itor.next();
    		if(proptypemap.get(key).equals("TextArea")){
    			list.add(key);
    		}
    	}
    	
    	return list;
    }
    
    private ArrayList<String> getTextFieldProperties(){
	
    	ArrayList<String> list = new ArrayList<String>();
        Iterator<String> itor = proptypemap.keySet().iterator();
    	
    	while(itor.hasNext()){
    		String key = itor.next();
    		if(proptypemap.get(key).equals("TextField")){
    			list.add(key);
    		}
    	}
	    return list;	
    }
    
    private ArrayList<String> getComboBoxProperties(){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Iterator<String> itor = proptypemap.keySet().iterator();
    	
    	while(itor.hasNext()){
    		String key = itor.next();
    		if(proptypemap.get(key).equals("ComboBox")){
    			list.add(key);
    		}
    	}
	    return list;	
    }

    private JPanel createTextAreaPanel(String prop){
    	
    	JTextArea area = new JTextArea();
    	//if(type == NCIEditTabConstants.EDIT && propvaluemap != null){
    	if(type != NCIEditTabConstants.DELETE && propvaluemap != null){
    	   area.setText(propvaluemap.get(prop));
    	}
    	
    	JPanel areaPanel = new JPanel(new BorderLayout());
    	
    	areaPanel.add(new JLabel(proplabelmap.get(prop)), BorderLayout.NORTH);
    	areaPanel.add(new JScrollPane(area), BorderLayout.CENTER);
    	areaPanel.setPreferredSize(new Dimension(400, 100));
    	
    	propcomponentmap.put(prop, area);   	
    	
    	return areaPanel;
    }
    
    private JPanel createTextFieldPanel(String prop){
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	JTextField textfield= new JTextField();
    	textfield.setPreferredSize(new Dimension(180, 20));
    	//if((type == NCIEditTabConstants.EDIT ||
    			//type == NCIEditTabConstants.ADD) && propvaluemap != null){
    	if(type != NCIEditTabConstants.DELETE && propvaluemap != null){
    		textfield.setText(propvaluemap.get(prop));
    	}
    	
    	//JLabel label = new JLabel(prop); 
    	JLabel label = new JLabel(proplabelmap.get(prop));
    	label.setPreferredSize(new Dimension(180, 20));
    	
    	panel.add(label, BorderLayout.WEST);
    	panel.add(textfield, BorderLayout.EAST);
    	
    	panel.setPreferredSize(new Dimension(400, 25));
    	
    	propcomponentmap.put(prop, textfield);
    	
    	return panel;
    }
    
    private JPanel createComboBoxPanel(String prop, String[] options){
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	JComboBox<String> combobox = new JComboBox<String>(options);   	
    	combobox.setPreferredSize(new Dimension(180, 20));
    	//if(type == NCIEditTabConstants.EDIT && propvaluemap != null){
    	if(type != NCIEditTabConstants.DELETE && propvaluemap != null){
    		combobox.setSelectedItem(propvaluemap.get(prop));
    	}
    	
    	JLabel label = new JLabel(proplabelmap.get(prop));  	   	
    	label.setPreferredSize(new Dimension(180, 20));
    	
    	panel.add(label, BorderLayout.WEST);
    	panel.add(combobox, BorderLayout.EAST);
    	panel.setPreferredSize(new Dimension(400, 25));
    	
    	propcomponentmap.put(prop, combobox);
    	
    	return panel;
    }
    
    private void createUI(){
    	propcomponentmap = new HashMap<String, Object>();
    	
    	ArrayList<String> textAreaProps = getTextAreaProperties();
    	ArrayList<String> textFieldProps = getTextFieldProperties();
    	ArrayList<String> comboBoxProps = getComboBoxProperties();
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	
    	for(int i = 0; i < textAreaProps.size(); i++){
    		
    		JPanel areapanel = createTextAreaPanel(textAreaProps.get(i));
    		
    		this.add(areapanel);
    	}
    	
       for(int i =0; i < textFieldProps.size(); i++){
    		
    		JPanel textfieldpanel = createTextFieldPanel(textFieldProps.get(i));
    		this.add(textfieldpanel);
    	}
       
       for(int i =0; i < comboBoxProps.size(); i++){
   		
   		JPanel comboboxpanel = createComboBoxPanel(comboBoxProps.get(i), propoptions.get(comboBoxProps.get(i)).toArray(new String[propoptions.get(comboBoxProps.get(i)).size()]));
   		this.add(comboboxpanel);
   	}
    }
    
    public  HashMap<String, String> showDialog(OWLEditorKit owlEditorKit, String title) {    	
        int ret = new UIHelper(owlEditorKit).showDialog(title, this, JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.OK_OPTION) {
            return getPropertyValueMap();
        }
        else {
            return null;
        }
    }
    
    private HashMap<String, String> getPropertyValueMap(){
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	
        Iterator<String> itor = propcomponentmap.keySet().iterator();
    	
    	while(itor.hasNext()){
    		String key = itor.next();
    		Object obj = propcomponentmap.get(key);
    		if(obj instanceof JTextField){
    			data.put(key, ((JTextField)obj).getText().trim());
    		}
    		else if(obj instanceof JTextArea){
    			data.put(key, ((JTextArea)obj).getText().trim());
    		}
    		else if(obj instanceof JComboBox){
    			data.put(key,  (String)((JComboBox<?>)obj).getSelectedItem());
    		}
    	}
    	
    	return data;
    	
    }
    
}
