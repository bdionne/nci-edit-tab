/**
 * 
 */
package gov.nih.nci.ui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
//import javax.swing.SwingWorker;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import edu.stanford.protege.csv.export.ui.ExportDialogPanel;
//import gov.nih.nci.ui.dialog.LQTExportDialog;
import gov.nih.nci.ui.dialog.RepWriterConfigDialog;
import gov.nih.nci.utils.QuickSortVecStrings;
/**
import edu.stanford.smi.protege.action.ExportToCsvUtil;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.query.ui.NCIExportToCsvAction;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.ModalDialog.CloseCallback;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;

import gov.nih.nci.protegex.dialog.RepWriterConfigDialog;
import gov.nih.nci.protegex.edit.NCIConditionsTableModel;
import gov.nih.nci.protegex.edit.NCIEditTab;
import gov.nih.nci.protegex.edit.OWLWrapper;
import gov.nih.nci.protegex.util.ClsUtil;
import gov.nih.nci.protegex.util.ComplexPropertyParser;
import gov.nih.nci.protegex.util.QuickSortVecStrings;
**/
import gov.nih.nci.utils.SwingWorker;

/**
 * @author Bob Dionne
 * @author Yinghua Xu
 * 
 */
public class ReportWriterPanel extends JPanel implements ActionListener
{

	public static final long serialVersionUID = 123456001L;

	private NCIEditTab tab = null;

	private JButton configure = null;

	JButton clearButton;


	int iCtr = 0;
	

	PrintWriter pw = null;

	OWLClass root;

	File exportFile = null;


	private Vector<String> classList;

	boolean completed = false;

	int maxlevel;

	boolean withAttributes = false;

	boolean withoutAttrsWithId = false;

	String attrsId = null;

	QuickSortVecStrings sort = null;

	HashSet<String> complexProps = null;

	JTextArea reportTextArea = null;

	private ReportWriterConfigPanel configPanel;
	
	protected static final String LAST_USED_FOLDER = "Enter file of identifiers";

	private enum ExportType {
		CLASSIC, LQTEXPORT, FILEINPUT
	};
	
	private OWLEditorKit oek;
	
	private OWLClass selected = null;
	
	public void setSelectedClass(OWLClass cls) {selected = cls;}

	public void showConfigDialog() {
		configPanel = new ReportWriterConfigPanel();
		int ret = new UIHelper(oek).showDialog("Report Type", configPanel, configPanel.classicRadioButton);
		if (ret == JOptionPane.OK_OPTION) {
			switch (configPanel.getSelection()) {
			case CLASSIC:
				onReportWriterClassicExport();
				break;
			case LQTEXPORT:
				onLQTExport();
				break;
			case FILEINPUT:
				onInputFileExport();
			default:
				break;
			}
		}
	}
	
	protected boolean onInputFileExport() {
		
		Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());   
		JFileChooser chooser = new JFileChooser(prefs.getString(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
		
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			prefs.putString(LAST_USED_FOLDER, chooser.getSelectedFile().getParent());
			final List<OWLEntity> ocl = new ArrayList<OWLEntity>();

			BufferedReader inFile = null;
			String s = null;
			try {
				inFile = new BufferedReader(new FileReader(chooser
						.getSelectedFile().getAbsolutePath()));
				while ((s = inFile.readLine()) != null) {
					s = s.trim();
					if (!s.equals("")) {
						OWLClass foo = this.tab.getClass(s); 
						if (foo != null) {
							ocl.add(foo);
						} else {
							reportTextArea
							.append("\nOWLNamedClass does not exist for code:" + s);

						}
					}

				}
				inFile.close();
			} catch (Exception e) {
				System.err.println(e.getLocalizedMessage());
				return false;
			}
			
			boolean success = false;
			try {
				success = ExportDialogPanel.showDialog(oek, "", ocl, true);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!success) {
				reportTextArea
						.append("\nExport with LQT configuration did not complete.");
			} else {
				reportTextArea.append("\nNo of classes processed: " + ocl.size() + "\n");
				reportTextArea
						.append("\nExported using the LQT configuration finished successfully.\n");
				
			}
			return success;

		} else {
			return false;
		}
	}

	protected boolean onLQTExport() {
		if ((selected == null) || selected.isTopEntity()) {
			JOptionPane.showMessageDialog(this, "Please select a root concept for the report \n", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		List<OWLEntity> ocl = fetchDownClosure(selected);
		

		boolean success = false;
		try {
			success = ExportDialogPanel.showDialog(oek, "", ocl, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!success) {
			reportTextArea
					.append("\nExport with LQT configuration did not complete.");
		} else {
			reportTextArea.append("\nNo of classes processed: " + ocl.size() + "\n");
			reportTextArea
					.append("\nExported using the LQT configuration finished successfully.\n");
		}
		return success;
		
	}
	
	private List<OWLEntity> fetchDownClosure(OWLClass e) {
    	List<OWLEntity> res = new ArrayList<OWLEntity>();
    	
    	res.add(e);
    	
        List<OWLClass> subClasses = tab.getDirectSubClasses(e);
        Collections.sort(subClasses, new Comparator<OWLClass>() {

			public int compare(OWLClass o1, OWLClass o2) {
				// single quotes are used by Protege-OWL when the browser
				// text has space in it, but they are displayed without the
				// quotes
				// this messes up the sort order
				String s1 = o1.getIRI().getShortForm();
				String s2 = o2.getIRI().getShortForm();
				if (s1.startsWith("'")) {
					s1 = s1.substring(1, s1.length() - 1);
				}
				if (s2.startsWith("'")) {
					s2 = s2.substring(1, s2.length() - 1);
				}
				return s1.compareTo(s2);
			}

		});
        for (OWLClass sub : subClasses) {
        	res.addAll(fetchDownClosure(sub));
        }
        return res;

    }

	
	protected boolean onReportWriterClassicExport() {
		if ((selected == null) || selected.isTopEntity()) {
			JOptionPane.showMessageDialog(this, "Please select a root concept for the report \n", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}				

		RepWriterConfigDialog ReportWriter_dlg = new RepWriterConfigDialog(
				this, selected);

		if (ReportWriter_dlg.getOKBtnPressed()) {
			exportFile = ReportWriter_dlg.getOutputFile();
			try {
				pw = new PrintWriter(new BufferedWriter(new FileWriter(
						exportFile)));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Could not open file for writing:\n "
						+ exportFile.getAbsolutePath(), "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}

			maxlevel = ReportWriter_dlg.getLevel();
			withAttributes = ReportWriter_dlg.getWithAttributes();

			attrsId = ReportWriter_dlg.getAttrsId();
			withoutAttrsWithId = ReportWriter_dlg.getWithoutAttrsWithId();

			root = selected;

			reportTextArea.append((String) generateReport());
			reportTextArea
			.append("\nExported using the classis configuration finished successfully.\n");
			

		}
		return true;
		
	}



	public void enableReportButton(boolean enabled) {
		configure.setEnabled(enabled);
	}

	public ReportWriterPanel(OWLEditorKit k) {
		
		super(new BorderLayout());
		oek = k;
		tab = NCIEditTab.currentTab();
		init();

	}
	
	private void init() {

		complexProps = new HashSet<String>();
		List<OWLAnnotationProperty> cprops = tab.getComplexProperties();
		for (OWLAnnotationProperty p : cprops) {
			complexProps.add(p.getIRI().getShortForm());
		}

		this.classList = new Vector<String>();

		JTextField fileName = new JTextField();
		fileName.setColumns(45);

		configure = new JButton("Configure");
		configure.addActionListener(this);
		
		clearButton = new JButton("Clear");
		clearButton.addActionListener(clearListener);
		clearButton.setEnabled(true);


		JPanel textAreaPanel = new JPanel(new BorderLayout());
		reportTextArea = new JTextArea(25, 45);

		reportTextArea.setEditable(false);

		reportTextArea.setTabSize(2);
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(configure);
		buttonPanel.add(clearButton);

		configure.setEnabled(true);

		textAreaPanel.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);
		textAreaPanel.add(buttonPanel, BorderLayout.SOUTH);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(textAreaPanel);

		textAreaPanel.setBorder(BorderFactory.createTitledBorder("Report"));

		add(box);
	}

	public void actionPerformed(ActionEvent arg0) {
		showConfigDialog();
	}

	
	private void writeToFile() {
		if (pw == null)
			return;
		try {
			String line = "";
			int length = classList.size();

			for (int i = 0; i < length; i++) {
				line = (String) classList.elementAt(i);
				pw.println(line);
			}
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getClsData(PrintWriter pw, OWLClass superCls,
			int level, int maxLevel, boolean withAttributes, String attrid) 

	{

		if (maxLevel != -1 && level > maxLevel)
			return "";

		if (superCls == null) {
			return "";
		}

		String tabString = "";
		for (int i = 0; i < level; i++) {
			tabString += "\t";
		}

		try {
			if (withAttributes) {
				classList.add("");
			}

			String pt = null;
			Optional<String> supLabel = tab.getRDFSLabel(superCls);
			if (supLabel.isPresent()) {
				pt = supLabel.get();
			} else {
				pt = superCls.getIRI().getShortForm();
			}
			

			if (withoutAttrsWithId) {
				classList.add(tabString + pt + " ("
						+ superCls.getIRI().getShortForm() + ")");
				
			} else {
				classList.add(tabString + pt);

			}

			
			if (withAttributes) {
				getSlots(superCls, tabString);				
			}
			

			writeToFile();
			classList.clear();

			iCtr++;
			
			List<OWLClass> subclasses = tab.getDirectSubClasses(superCls);

			Collections.sort(subclasses, new Comparator<OWLClass>() {

				public int compare(OWLClass o1, OWLClass o2) {
					// single quotes are used by Protege-OWL when the browser
					// text has space in it, but they are displayed without the
					// quotes
					// this messes up the sort order
					String s1 = o1.getIRI().getShortForm();
					String s2 = o2.getIRI().getShortForm();
					if (s1.startsWith("'")) {
						s1 = s1.substring(1, s1.length() - 1);
					}
					if (s2.startsWith("'")) {
						s2 = s2.substring(1, s2.length() - 1);
					}
					return s1.compareTo(s2);
				}

			});

			

			level++;
			for (OWLClass sub : subclasses) {
				if (sub.getIRI().getShortForm().compareTo(superCls.getIRI().getShortForm()) != 0) {
					getClsData(pw, sub, level,
							maxLevel, withAttributes, attrid);
				
				}
			}
			
		}


		catch (Exception e) {			
			return "Error";
		}

		return "All Done";

	}

	
	private void getSlots(OWLClass cls, String tabString) {
		Set<OWLAnnotation> annotations = tab.getAnnotations(cls);
		for (OWLAnnotation ann : annotations) {
			OWLAnnotationProperty ap = ann.getProperty();
			String slotname = "";
			String entry = "";
			if (ap.equals(NCIEditTabConstants.CODE_PROP) ||
					ap.equals(NCIEditTabConstants.PREF_NAME)) {

				if (ap.equals(NCIEditTabConstants.CODE_PROP)) {
					Optional<String> lab = tab.getRDFSLabel(ap);
					if (lab.isPresent()) {
						slotname = lab.get();
					}				
				} else if (ap.equals(NCIEditTabConstants.PREF_NAME)) {
					slotname = ann.getProperty().getIRI().getShortForm();				
				}

				Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
				if (strentry.isPresent()) {
					entry = strentry.get().getLiteral();
				}
				classList.add("\t" + tabString + slotname + ": " + entry);
			}

		}

		classList.add("");

		for (OWLAnnotation ann : annotations) {
			OWLAnnotationProperty ap = ann.getProperty();
			String slotname = ann.getProperty().getIRI().getShortForm();			
			if (this.complexProps.contains(slotname)) {
				// more to do
			} else {
				if (ap.equals(NCIEditTabConstants.CODE_PROP)) {
					Optional<String> lab = tab.getRDFSLabel(ap);
					if (lab.isPresent()) {
						slotname = lab.get();
					}					
				}
				String entry = "";
				Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
				if (strentry.isPresent()) {
					entry = strentry.get().getLiteral();
				}
				classList.add("\t" + tabString + slotname + ": " + entry);
			}

		}

		classList.add("");

		for (OWLAnnotation ann : annotations) {
			OWLAnnotationProperty ap = ann.getProperty();
			String slotname = ap.getIRI().getShortForm();
			String entry = "";
			if (this.complexProps.contains(slotname)) {
				Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
				if (strentry.isPresent()) {
					entry = strentry.get().getLiteral();
				}
				classList.add("\t" + tabString + slotname + ": " + entry);

				Set<OWLAnnotation> quals = tab.getDependentAnnotations(cls,ap);
				for (OWLAnnotation qualAnn : quals) {
					OWLAnnotationProperty qap = qualAnn.getProperty();
					String qslotname = qap.getIRI().getShortForm();
					String qentry = "";
					Optional<OWLLiteral> qstrentry = qualAnn.getValue().asLiteral();
					if (qstrentry.isPresent()) {
						qentry = qstrentry.get().getLiteral();
					}
					classList.add("\t\t" + tabString + qslotname + ": " + qentry);
				}
			}
		}
		
		classList.add("");

		Set<String> parents = tab.getLogicalRes(cls, "parents");
		for (String par : parents) {
			String value = "Named Superclass: " + par;
			classList.add("\t" + tabString + value);
		}
		
		classList.add("");

		Set<String> roles = tab.getLogicalRes(cls, "roles");
		for (String rol : roles) {
			String value = "Restriction: " + rol;
			classList.add("\t" + tabString + value);
		}

	}
	

	
	Object generateReport() {
		
		iCtr = 0;
	    getClsData(pw, root, 0, maxlevel, withAttributes, attrsId);
		
		return "\nNo of classes processed: " + iCtr + "\n";
	}
	
	ActionListener clearListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			reportTextArea.setText("");
			
		}
		
	};
	class ReportWriterConfigPanel extends JPanel {
		private static final long serialVersionUID = 5569432370041174566L;

		private JRadioButton classicRadioButton;

		private JRadioButton lqtRadioButton;

		private JRadioButton inputFileRadioButton;

		public ReportWriterConfigPanel() {
			buildUI();
		}

		protected void buildUI() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(getTextComponent());
			addRadioBoxOptions();
		}

		protected JTextArea getTextComponent() {
			JTextArea textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			textArea.setText("Please select the report type:");
			return textArea;
		}

		protected void addRadioBoxOptions() {
			classicRadioButton = new JRadioButton(
					"Export as report with attributes", true);
			lqtRadioButton = new JRadioButton("Export using LQT", false);
			inputFileRadioButton = new JRadioButton(
					"Export using an input file", false);

			classicRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			lqtRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputFileRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);

			ButtonGroup group = new ButtonGroup();
			group.add(classicRadioButton);
			group.add(lqtRadioButton);
			group.add(inputFileRadioButton);

			JPanel gridPanel = new JPanel(new GridLayout(5, 1));
			gridPanel.add(classicRadioButton);
			gridPanel.add(lqtRadioButton);
			gridPanel.add(inputFileRadioButton);
			gridPanel.add(Box.createRigidArea(new Dimension(0, 10)));

			add(Box.createRigidArea(new Dimension(0, 5)));

			add(gridPanel);
		}

		public ExportType getSelection() {
			if (classicRadioButton.isSelected()) {
				return ExportType.CLASSIC;
			} else if (lqtRadioButton.isSelected()) {
				return ExportType.LQTEXPORT;
			} else if (inputFileRadioButton.isSelected()) {
				return ExportType.FILEINPUT;
			}
			return ExportType.CLASSIC;
		}
	}

	public void dispose() {
		
	}
}
