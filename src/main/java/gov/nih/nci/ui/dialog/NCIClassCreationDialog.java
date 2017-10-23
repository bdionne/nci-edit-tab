package gov.nih.nci.ui.dialog;

import static gov.nih.nci.ui.NCIEditTabConstants.CODE_PROP;
import static gov.nih.nci.ui.NCIEditTabConstants.LABEL_PROP;
import static gov.nih.nci.ui.NCIEditTabConstants.PREF_NAME;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.ui.NCIEditTabConstants;

public class NCIClassCreationDialog<T extends OWLEntity> extends JPanel {
	
	public enum EntityCreationMode {
		PREVIEW, CREATE
    }

    /**
     *
     */
    private static final long serialVersionUID = -2790553738912229896L;

    public static final int FIELD_WIDTH = 40;

    private OWLEditorKit owlEditorKit;
    
    private JTextField preferredNameField;

    private Class<T> type;

    private final AugmentedJTextField entityIRIField = new AugmentedJTextField(FIELD_WIDTH, "IRI (auto-generated)");

    private final JTextArea messageArea = new JTextArea(1, FIELD_WIDTH);

    public NCIClassCreationDialog(OWLEditorKit owlEditorKit, String message, Class<T> type, Optional<String> prefName,
    		Optional<String> code) {
    	this(owlEditorKit, message, type, prefName, code, false);
          
    }
    
    public NCIClassCreationDialog(OWLEditorKit owlEditorKit, String message, Class<T> type, Optional<String> prefName,
    		Optional<String> code, boolean dontApply) {
    	this.dont_apply_changes = dontApply;
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        if (prefName.isPresent()) {
        	buildNewClass(prefName.get(), code);        	
        } else {
        	createUI(message);        	
        }        
    }

    private void createUI(String message) {
    	// this field is hidden an non-editable
    	entityIRIField.setVisible(false);
        entityIRIField.setEditable(false);
        
        setLayout(new BorderLayout());
        JPanel holder = new JPanel(new GridBagLayout());
        add(holder);
        Insets insets = new Insets(0, 0, 2, 2);

        int rowIndex = 0;

        holder.add(new JLabel("Name:"), new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));

        preferredNameField = new AugmentedJTextField(30, "Preferred Name");
        
        preferredNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        
        holder.add(new JLabel("Preferred Name:"), new GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        holder.add(preferredNameField, new GridBagConstraints(1, rowIndex, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        rowIndex++;
        holder.add(new JSeparator(), new GridBagConstraints(0, rowIndex, 2, 1, 100.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 5, 2), 0, 0));
        rowIndex++;

        messageArea.setBackground(null);
        messageArea.setBorder(null);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setFont(messageArea.getFont().deriveFont(12.0f));
        messageArea.setForeground(Color.RED);
        
        holder.add(messageArea, new GridBagConstraints(0, rowIndex, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));

        
        update();
    }

    public String getEntityName() {
        return preferredNameField.getText().trim();
    }
    
    private String possiblyEscape(String s) {
    	if (s.startsWith("'") &&
    			s.endsWith("'")) {
    		return s;    		
    	}
    	if (s.split(" ").length > 1) {
    		return "'" + s + "'";
    	} else {
    		return s;
    	}
    }



    public <T extends OWLEntity> boolean showDialog() {
    	int ret = JOptionPane.OK_OPTION;
    	while (ret == JOptionPane.OK_OPTION) {
    		ret = new UIHelper(owlEditorKit).showValidatingDialog("Create a new " + type.getSimpleName(), this, this.preferredNameField);
    		if (ret == JOptionPane.OK_OPTION) {
    			OWLEntityFinder finder = owlEditorKit.getOWLModelManager().getOWLEntityFinder();
    			Set<OWLClass> entities = finder.getMatchingOWLClasses(getEntityName());
    			if (!entities.isEmpty()) {
    				boolean c_exists = false;
    			
    				for (OWLClass c : entities) {
    					if (owlEditorKit.getModelManager().getRendering(c).equals(possiblyEscape(getEntityName()))) {
    						c_exists = true;
    						break;
    					}
    				}
    				if (c_exists) {
    					int allow = JOptionPane.showConfirmDialog(this, "Preferred name already exists", "warning",
    							JOptionPane.OK_CANCEL_OPTION);
    					if (allow == JOptionPane.OK_OPTION) {
    						if (buildNewClass(getEntityName(), Optional.empty())) {
    							return true;
    						}

    					} 
    				} else {
    					if (buildNewClass(getEntityName(), Optional.empty())) {
							return true;
						}
    				}
    			} else {
    				if (buildNewClass(getEntityName(), Optional.empty())) {
    					return true;
    				}
    			}


    		}
    	} 
    	return false;
    }
    
 

    public IRI getBaseIRI() {
        return null; // let this be managed by the EntityFactory for now - we could add a selector later
    }

    private void update() {
        try {

            entityIRIField.setText("");
            messageArea.setText("");
            if (preferredNameField.getText().trim().isEmpty()) {
                return;
            }
            OWLEntityCreationSet<?> creationSet = owlEditorKit.getModelManager().getOWLEntityFactory().preview(type, getEntityName(),
					getBaseIRI());
            if(creationSet == null) {
            	return;
            }
            OWLEntity owlEntity = creationSet.getOWLEntity();
            String iriString = owlEntity.getIRI().toString();
            entityIRIField.setText(iriString);
        }
        catch (RuntimeException | OWLEntityCreationException e) {
        	// safely ignore these, as the name is checked later
            Throwable cause = e.getCause();
            if (cause != null) {
                if(cause instanceof OWLEntityCreationException) {
                    //messageArea.setText("Entity already exists");
                }
                else {
                    //messageArea.setText(cause.getMessage());
                }
            }
            else {
                //messageArea.setText(e.getMessage());
            }
        }

    }    

    public JComponent getFocusComponent() {
        return preferredNameField;
    }
    
    private OWLClass newClass = null;
    
    public OWLClass getNewClass() {return newClass;}
    
    private List<OWLOntologyChange> ont_changes = null;
    private boolean dont_apply_changes = false;
    
    public List<OWLOntologyChange> getOntChanges() {return ont_changes;}
    		
    
    public boolean buildNewClass(String preferredName, Optional<String> code) {
    	
    	if (preferredName == null || preferredName.equals("")) {
    		JOptionPane.showMessageDialog(this, "Preferred name is required", "Warning", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
    	
    	if (!NCIEditTab.currentTab().validPrefName(preferredName)){
    		JOptionPane.showMessageDialog(this, "Preferred name cannot contain special characters, ! or ?", "Warning", JOptionPane.WARNING_MESSAGE);
    		return false;    		
    	}
    	
    	String gen_code = "";
    	
    	if (code.isPresent()) {
    		gen_code = code.get();
    	} else {
    		List<String> codes = NCIEditTab.currentTab().generateCodes(1);
    		gen_code = codes.get(0);
    	}

		OWLEntityCreationSet<OWLClass> newSet = null;

		try {
			newSet = owlEditorKit.getModelManager().getOWLEntityFactory().createOWLEntity(
					OWLClass.class, gen_code, null);
		} catch (OWLEntityCreationException e) {
			e.printStackTrace();
		}
		
		OWLClass newClass = newSet.getOWLEntity();

		
		List<OWLOntologyChange> changes = new ArrayList<>();
		changes.addAll(newSet.getOntologyChanges());
		final OWLModelManager mngr = owlEditorKit.getModelManager();
		final OWLDataFactory df = mngr.getOWLDataFactory();
		

		OWLLiteral con = df.getOWLLiteral(gen_code, OWL2Datatype.RDF_PLAIN_LITERAL);
		OWLLiteral pref_name_val = df.getOWLLiteral(preferredName, OWL2Datatype.RDF_PLAIN_LITERAL);

		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(CODE_PROP, newClass.getIRI(), con);
		changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
		OWLAxiom ax2 = df.getOWLAnnotationAssertionAxiom(LABEL_PROP, newClass.getIRI(), pref_name_val);
		OWLAxiom ax3 = df.getOWLAnnotationAssertionAxiom(PREF_NAME, newClass.getIRI(), pref_name_val);
		changes.add(new AddAxiom(mngr.getActiveOntology(), ax2));
		changes.add(new AddAxiom(mngr.getActiveOntology(), ax3));
		
		OWLAnnotationProperty full_syn = NCIEditTab.currentTab().getFullSyn();
		
		OWLAxiom new_axiom = df.getOWLAnnotationAssertionAxiom(full_syn, newClass.getIRI(), pref_name_val);
		
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		Set<OWLAnnotationProperty> req_props = NCIEditTab.currentTab().getRequiredAnnotationsForAnnotation(full_syn);
		
		for (OWLAnnotationProperty prop : req_props) {
			
			String val = NCIEditTab.currentTab().getDefaultValue(NCIEditTab.currentTab().getDataType(prop), NCIEditTabConstants.DEFAULT_SOURCE_NEW_CLASS);
			if (val == null) {
				val = "No_Default";

			}
			OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(val, OWL2Datatype.RDF_PLAIN_LITERAL));
			anns.add(new_ann);


		}
		
		OWLAxiom new_new_axiom = new_axiom.getAxiomWithoutAnnotations().getAnnotatedAxiom(anns);
				
		changes.add(new AddAxiom(mngr.getActiveOntology(), new_new_axiom));
		
				
		this.ont_changes = changes;
		this.newClass = newClass;
		
		if (!dont_apply_changes) {
			mngr.applyChanges(ont_changes);
		}
		
		return true;
    }
    
    
}
