package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;



public class ComplexEditPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;

    private JPopupMenu popupMenu;
    
    private OWLFrameList<OWLAnnotationSubject> list;
    
    private JButton splitButton;

    private JButton copyButton;
    
    private JButton preMergeButton;
    
    private JButton mergeButton;
    
    private JButton saveButton;
    
    private JButton clearButton;
    
    public ComplexEditPanel(OWLEditorKit editorKit, OWLFrameList<OWLAnnotationSubject> list) {
        this.owlEditorKit = editorKit;
        this.list = list;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        //Box box = new Box(BoxLayout.Y_AXIS);
        JScrollPane comp = new JScrollPane(list);
        comp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        
        upperPanel.add(comp);
        
        JTextField txtTest = new JTextField();
        txtTest.setText("Test");
        txtTest.setColumns(10);
        lowerPanel.setSize(1000,1000);
        lowerPanel.add(txtTest);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(280);

		add(splitPane, BorderLayout.CENTER);
		add(createJButtonPanel(), BorderLayout.SOUTH);
		add(createRadioButtonPanel(), BorderLayout.NORTH);
        
    }
    
    private JPanel createJButtonPanel() {
		JPanel panel = new JPanel();
		panel.add(saveButton = createButton("Save", true));
		panel.add(clearButton = createButton("Clear", true));
		return panel;
	}
    
    private JPanel createRadioButtonPanel() {
    	JPanel panel = new JPanel();
    	JRadioButton splitButton = new JRadioButton("Split");
    	JRadioButton copyButton = new JRadioButton("Copy");
    	JRadioButton mergeButton = new JRadioButton("Merge");
    	JRadioButton retireButton = new JRadioButton("Retire");
    	ButtonGroup btnGrp = new ButtonGroup();
    	btnGrp.add(splitButton);
    	btnGrp.add(copyButton);
    	btnGrp.add(mergeButton);
    	btnGrp.add(retireButton);
    	panel.add(splitButton);
    	panel.add(copyButton);
    	panel.add(mergeButton);
    	panel.add(retireButton);
    	return panel;
    }

    /**
     * Creates a button with a specific label.
     * 
     * @param label The button label.
     * @param enable If true, enables this button.
     * @return The newly created button.
     */
    protected JButton createButton(String label, boolean enable) {
    	JButton button = new JButton(label);
        button.setEnabled(enable);
        return button;
    }   
}
