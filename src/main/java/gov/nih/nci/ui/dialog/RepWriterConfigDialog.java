/*****************************************************************************
 *
 * NGIT 2006. All Rights Reserved.
 * @Author: NGIT, Kim Ong, Iris Guo
 *
 *****************************************************************************/

package gov.nih.nci.ui.dialog;
import gov.nih.nci.ui.NCIEditTabConstants;

/**
import edu.stanford.smi.protege.util.FileField;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import gov.nih.nci.protegex.edit.NCIEditTab;
import gov.nih.nci.protegex.edit.OWLWrapper;
import gov.nih.nci.protegex.panel.ReportWriterPanel;
import gov.nih.nci.protegex.util.MsgDialog;
**/
import gov.nih.nci.ui.ReportWriterPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.protege.editor.core.ui.util.UIUtil;
import org.semanticweb.owlapi.model.OWLClass;

public class RepWriterConfigDialog extends JDialog implements ActionListener {


	
	private static final long serialVersionUID = 123456034L;

	boolean btnPressed;

	int windowClosingTag = 0;

	JButton fInputButton, fOutputButton;
	JButton continueButton, cancelButton;

	JTextField fInputFile, fOutputFile;

	ReportWriterPanel repPanel;
	

	String infile;// = fInputTf.getText();
	String outfile;// = fOutputFile.getText();

	OWLClass selectedCls;

	//FileField outputFileField = null;
	JComboBox<String> levelComboBox;

	JRadioButton allAttrs;
	JRadioButton noAttrs;
	JRadioButton attrsWithId;

	JLabel attrTF;

	boolean withAttributes = true;
	boolean withoutAttrsWithId = false;

	String attrsId = null;
	
	private File selectedFile = null;
	private JTextField fileLocationTxtField;
	
	
	private ActionListener browseBtnListener = e -> {
        selectedFile = UIUtil.saveFile(repPanel, "Choose file location", "Report Writer output", null, "report.log");
        fileLocationTxtField.setText(selectedFile.getAbsolutePath());
        
    };

	public RepWriterConfigDialog(ReportWriterPanel tab, OWLClass sel) {
		super((JFrame) tab.getTopLevelAncestor(), "Report Writer", true);

		this.repPanel = tab;
		selectedCls = sel;

		attrsId = NCIEditTabConstants.CODE_PROP.getIRI().getShortForm();
		 

		this.infile = "";
		this.outfile = "";

		this.setResizable(false);
		initialize();
	}

	public File getOutputFile() {
		if (selectedFile != null) {
			return selectedFile;
		} else {
			return new File(fileLocationTxtField.getText());
		}
	}

	public boolean getWithAttributes() {
		return withAttributes;
	}
	
	public boolean getWithoutAttrsWithId() {
		return withoutAttrsWithId;
	}

	public String getAttrsId() {
		if (withoutAttrsWithId) {

			return attrsId;
		} else {
			return null;

		}
	}

	public void initialize() {
		Container container = this.getContentPane();
		setLocation(450, 300);
		setSize(new Dimension(470, 280));
		this.setResizable(true);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.setPreferredSize(new Dimension(300, 40));
		//rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));
		String labeltext = "";
		if (selectedCls != null) {
			labeltext = "  Root Concept: " + selectedCls.getIRI().getShortForm();
		}
		JLabel rc = new JLabel(labeltext);
		rootPanel.add(rc, BorderLayout.WEST);
		container.add(rootPanel);
		
		JPanel ofp = new JPanel();
		JLabel label = new JLabel("Output File");
		fileLocationTxtField = new JTextField("report.log");
		fileLocationTxtField.setPreferredSize(new Dimension(200, 25));
		JButton browseBtn = new JButton("Browse");
	    browseBtn.addActionListener(browseBtnListener);
	    
	    ofp.add(label);
	    ofp.add(fileLocationTxtField);
	    ofp.add(browseBtn);
		
	    JPanel cp1 = new JPanel(new BorderLayout());
	    cp1.add(ofp, BorderLayout.WEST);
	    container.add(cp1);

		String[] levels = new String[12];
		levels[0] = "All";
		for (int i = 1; i < levels.length; i++) {
			Integer int_obj = new Integer(i - 1);
			levels[i] = int_obj.toString();
		}

		levelComboBox = new JComboBox<String>(levels);
		levelComboBox.setPreferredSize(new Dimension(80, 25));
		levelComboBox.setSelectedIndex(0);

		JPanel lc3 = new JPanel();
		lc3.setPreferredSize(new Dimension(280, 30));
		lc3.add(new JLabel("Hierarchy Level"));
		lc3.add(levelComboBox);

		JPanel cp2 = new JPanel(new BorderLayout());
		cp2.add(lc3, BorderLayout.WEST);
		container.add(cp2);
		
		ButtonGroup yesnoGroup = new ButtonGroup();
		
		allAttrs = new JRadioButton("Attributes");
		allAttrs.setSelected(true);
		allAttrs.addActionListener(this);

		noAttrs = new JRadioButton("No Attributes");
		noAttrs.addActionListener(this);

		attrsWithId = new JRadioButton("Attributes with Id");
		attrsWithId.addActionListener(this);

		yesnoGroup.add(allAttrs);
		yesnoGroup.add(noAttrs);
		yesnoGroup.add(attrsWithId);

		JPanel lc4 = new JPanel();
		lc4.setPreferredSize(new Dimension(390, 30));
		lc4.add(new JLabel("Select Attriburtes"));
		lc4.add(allAttrs);
		lc4.add(noAttrs);
		lc4.add(attrsWithId);
		
		JPanel cp3 = new JPanel(new BorderLayout());
		cp3.add(lc4, BorderLayout.WEST);
		
		container.add(cp3);

		JPanel attrsPlusId = new JPanel(new BorderLayout());
		attrsPlusId.setPreferredSize(new Dimension(390, 30));
		attrTF = new JLabel();
		attrsPlusId.add(attrTF, BorderLayout.EAST);

		container.add(attrsPlusId);

		continueButton = new JButton("Continue");
		continueButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		JPanel okcancelPanel = new JPanel();
		okcancelPanel.setPreferredSize(new Dimension(300, 40));
		okcancelPanel.add(continueButton);
		okcancelPanel.add(cancelButton);

		container.add(okcancelPanel);

		pack();

		repPanel.enableReportButton(false);
		setVisible(true);
		
	}

	public boolean getOKBtnPressed() {
		return btnPressed;
	}

	public int getLevel() {
		int level = levelComboBox.getSelectedIndex();
		return level - 1;
	}

	public void actionPerformed(ActionEvent event) {
		Object action = event.getSource();
		if (action == continueButton) {
			File outputFile = getOutputFile();
			if (outputFile == null) {
				JOptionPane.showMessageDialog(this, "Input and output files are required.", "Warning", JOptionPane.WARNING_MESSAGE);
				
				return;
			}

			btnPressed = true;
			repPanel.enableReportButton(true);
			dispose();

		} else if (action == cancelButton) {
			btnPressed = false;
			repPanel.enableReportButton(true);
			dispose();
		} else if (action == allAttrs) {
			withAttributes = true;
			attrTF.setText("");
		} else if (action == noAttrs) {
			withAttributes = false;
			attrTF.setText("");
		} else if (action == attrsWithId) {
			withAttributes = false;
			withoutAttrsWithId = true;

			if (attrsId != null) {
				attrTF.setText("ID: " + attrsId + "   ");
			} else {
				attrTF.setText("ID: rdf:ID   ");
			}

		}
	}

	public void beginOutput() {
		continueButton.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		continueButton.removeActionListener(this);
		System.out.println("Report writer in progress. Please wait...");
	}
	
	
	

}
