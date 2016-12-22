package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.server.http.messages.History;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.dialog.ComplexPropChooser;
import gov.nih.nci.ui.event.ComplexEditType;

public class ReadOnlyClassPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;	
	
	private OWLFrameList<OWLClass> list;	
	private OWLFrameList<OWLAnnotationSubject> gen_props;	
	private OWLClass currentClass = null;
    JScrollPane descrPane;
    JSplitPane split;
    
    private boolean read_only = true;
    
    public ReadOnlyClassPanel(OWLEditorKit editorKit) {
    	this(editorKit, true);    	
    }
    
    public ReadOnlyClassPanel(OWLEditorKit editorKit, boolean ro) {
    	read_only = ro;
        this.owlEditorKit = editorKit;
        createUI();        
    }
    
    
    
    private void createUI() {
    	setLayout(new BorderLayout());   
        gen_props = new OWLFrameList<OWLAnnotationSubject>(owlEditorKit, 
        		new FilteredAnnotationsFrame(owlEditorKit, new HashSet<OWLAnnotationProperty>(), 
        				new HashSet<OWLAnnotationProperty>()), read_only) {
							private static final long serialVersionUID = 1L;
        };
          
        JScrollPane generalSP = new JScrollPane(gen_props);//will add tree or list to it
        generalSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        list = new OWLFrameList<OWLClass>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit), read_only) {
			private static final long serialVersionUID = 1L;
        	        	
        };
        
        descrPane = new JScrollPane(list);
        descrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);        
        
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                generalSP, descrPane);
        
        
        add(split);
		setVisible(true);
		
    }
    
    public void setSelectedClass(OWLClass cls) {
    	if (cls != null) {
    		currentClass = cls;
    		list.setRootObject(cls);
    		if (cls != null) {
    			gen_props.setRootObject(cls.getIRI());
    		}

    	} else {
    		
    		list.setRootObject(null);
    		gen_props.setRootObject(null);
    		
    	}
    	
    	split.setDividerLocation(0.80);
    	
    }
    
    public OWLClass getSelectedClass() {
    	return this.currentClass;
    }
    
   
    
    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
   
    
    
    public void disposeView() {
    	list.dispose();
    	gen_props.dispose();
    }
   
}
