package gov.nih.nci.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditorPreferences;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.Namespaces;

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

    public NCIClassCreationDialog(OWLEditorKit owlEditorKit, String message, Class<T> type) {
        this.owlEditorKit = owlEditorKit;
        this.type = type;
        createUI(message);
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


    /**
     * Gets the entity creation set
     * @return The entity creation set
     * @throws RuntimeException which wraps an {@link OWLEntityCreationException} if there was a problem
     */
    public OWLEntityCreationSet<T> getOWLEntityCreationSet() throws RuntimeException {
    	return getOWLEntityCreationSet(EntityCreationMode.CREATE);
    }
    
	public OWLEntityCreationSet<T> getOWLEntityCreationSet(EntityCreationMode preview) throws RuntimeException {
		try {

			switch (preview) {
			case CREATE:
				return owlEditorKit.getModelManager().getOWLEntityFactory().createOWLEntity(type, getEntityName(),
						getBaseIRI());
			case PREVIEW:
				return owlEditorKit.getModelManager().getOWLEntityFactory().preview(type, getEntityName(),
						getBaseIRI());
			default:
				throw new IllegalStateException(
						"Programmer error - report this (with stack trace) to the Protege 4 mailing list");
			}

		} catch (OWLEntityCreationException e) {
			throw new RuntimeException(e);
		}
	}

    public static <T extends OWLEntity> OWLEntityCreationSet<T> showDialog(OWLEditorKit owlEditorKit, String message, Class<T> type) {

            NCIClassCreationDialog<T> panel = new NCIClassCreationDialog<>(owlEditorKit, message, type);
            int ret = new UIHelper(owlEditorKit).showValidatingDialog("Create a new " + type.getSimpleName(), panel, panel.preferredNameField);
            if (ret == JOptionPane.OK_OPTION) {
                return panel.getOWLEntityCreationSet();
            }
            else {
                return null;
            }
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
            OWLEntityCreationSet<?> creationSet = getOWLEntityCreationSet(EntityCreationMode.PREVIEW);
            if(creationSet == null) {
            	return;
            }
            OWLEntity owlEntity = creationSet.getOWLEntity();
            String iriString = owlEntity.getIRI().toString();
            entityIRIField.setText(iriString);
        }
        catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if(cause instanceof OWLOntologyCreationException) {
                    messageArea.setText("Entity already exists");
                }
                else {
                    messageArea.setText(cause.getMessage());
                }
            }
            else {
                messageArea.setText(e.getMessage());
            }
        }

    }    

    public JComponent getFocusComponent() {
        return preferredNameField;
    }
}
