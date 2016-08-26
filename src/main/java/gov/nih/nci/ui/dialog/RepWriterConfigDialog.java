/*****************************************************************************
 *
 * NGIT 2006. All Rights Reserved.
 * @Author: NGIT, Kim Ong, Iris Guo
 *
 *****************************************************************************/

package gov.nih.nci.ui.dialog;
import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.ui.NCIEditTabConstants;
//import gov.nih.nci.ui.NCIEditTab;
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
	JComboBox levelComboBox;

	JRadioButton allAttrs;
	JRadioButton noAttrs;
	JRadioButton attrsWithId;

	JTextField attrTF;

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
		// setLocation(360,300);
		setLocation(450, 300);
		setSize(new Dimension(450, 240));
		container.setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());

		JPanel ofp = new JPanel();
		JLabel label = new JLabel("Output File");
		fileLocationTxtField = new JTextField("report.log");
		JButton browseBtn = new JButton("Browse");
	    browseBtn.addActionListener(browseBtnListener);
	    
	    ofp.add(label);
	    ofp.add(fileLocationTxtField);
	    ofp.add(browseBtn);
		
		

		inputPanel.add(ofp, BorderLayout.NORTH);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		JTextField rootConcept = new JTextField();
		rootConcept.setEditable(false);
		if (selectedCls != null) {
			rootConcept.setText(selectedCls.getIRI().getShortForm());
		}
		rootPanel.add(rootConcept, BorderLayout.CENTER);
		
		JPanel rp = new JPanel();
		JLabel rc = new JLabel("Root Concept");
		rp.add(rc);
		rp.add(rootPanel);

		inputPanel.add(rp, BorderLayout.CENTER);

		String[] levels = new String[12];
		levels[0] = "All";
		for (int i = 1; i < levels.length; i++) {
			Integer int_obj = new Integer(i - 1);
			levels[i] = int_obj.toString();
		}

		levelComboBox = new JComboBox(levels);
		levelComboBox.setSelectedIndex(0);

		JPanel lc3 = new JPanel();
		lc3.add(new JLabel("Hierarchy Level"));
		lc3.add(levelComboBox);

		inputPanel.add(lc3, BorderLayout.SOUTH);

		container.add(inputPanel, BorderLayout.NORTH);

		JPanel attrsPlusId = new JPanel();
		attrsPlusId.setLayout(new BorderLayout());
		ButtonGroup yesnoGroup = new ButtonGroup();
		JPanel yesnoPanel = new JPanel();

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

		yesnoPanel.add(allAttrs);
		yesnoPanel.add(noAttrs);
		yesnoPanel.add(attrsWithId);
		
		JPanel lc4 = new JPanel();
		lc4.add(new JLabel("Select Attriburtes"));
		lc4.add(yesnoPanel);

		attrsPlusId.add(lc4, BorderLayout.NORTH);

		attrTF = new JTextField();
		attrTF.setBorder(null);
		attrTF.setEditable(false);
		attrsPlusId.add(attrTF, BorderLayout.CENTER);

		container.add(attrsPlusId, BorderLayout.CENTER);

		continueButton = new JButton("Continue");
		continueButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		JPanel okcancelPanel = new JPanel();
		okcancelPanel.add(continueButton);
		okcancelPanel.add(cancelButton);

		container.add(okcancelPanel, BorderLayout.SOUTH);

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
		} else if (action == noAttrs) {
			withAttributes = false;
			
		} else if (action == attrsWithId) {
			withAttributes = false;
			withoutAttrsWithId = true;

			if (attrsId != null) {
				attrTF.setText("ID: " + attrsId);
			} else {
				attrTF.setText("ID: rdf:ID");
			}

		}
	}

	public void beginOutput() {
		continueButton.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		continueButton.removeActionListener(this);
		System.out.println("Report writer in progress. Please wait...");
	}
	
	
	

}
