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

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.search.ui.ExportDialogPanel;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;

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
 * @author bitdiddle
 * 
 */
public class ReportWriterPanel extends JPanel implements ActionListener
{

	public static final long serialVersionUID = 123456001L;

	private NCIEditTab tab = null;

	private JButton configure = null;

	JButton startButton;

	JButton interruptButton;

	boolean interrupted;

	private JProgressBar progress;

	int iCtr = 0;

	SwingWorker worker;

	PrintWriter pw = null;

	OWLClass root;

	File exportFile = null;

	private int max;

	private String newline = System.getProperty("line.separator");
		
	private Vector<String> classList;

	boolean completed = false;

	int maxlevel;

	boolean withAttributes = false;

	boolean withoutAttrsWithId = false;

	String attrsId = null;

	Vector messages;

	QuickSortVecStrings sort = null;

	HashSet<String> hset = null;

	HashSet<String> complexProps = null;

	JTextArea reportTextArea = null;

	private ReportWriterConfigPanel configPanel;

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
	
	// TODO: NOTE!!!! This is just a sketch to see how the approach looks in the
	// UI
	protected boolean onInputFileExport() {
		
		JFileChooser chooser = new JFileChooser("Enter file of identifiers");
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			final List<OWLEntity> ocl = new ArrayList<OWLEntity>();

			BufferedReader inFile = null;
			String s = null;
			try {
				inFile = new BufferedReader(new FileReader(chooser
						.getSelectedFile().getAbsolutePath()));
				while ((s = inFile.readLine()) != null) {
					s = s.trim();
					OWLClass foo = this.tab.getClass(s); 
					if (foo != null) {
						ocl.add(foo);
					} else {
						reportTextArea
						.append("\nOWLNamedClass does not exist for code:" + s);
						
					}

				}
				inFile.close();
			} catch (Exception e) {
				System.err.println(e.getLocalizedMessage());
				return false;
			}
			
			boolean success = false;
			try {
				success = ExportDialogPanel.showDialog(oek, "", ocl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//ExportToCsvUtil.setExportBrowserText(false);
			//ExportToCsvUtil.setExportMetadata(true);
			//ExportToCsvUtil.setExportSuperclass(true);
/**
			NCIExportToCsvAction exportAction = new NCIExportToCsvAction(tab
					.getKnowledgeBase(), configPanel, true) {

				public static final long serialVersionUID = 123456772L;

				public Collection<Instance> getInstancesToExport() {
					Collection<Instance> allClassesToExport = new ArrayList<Instance>();
					List<Cls> selectedClsesInPanel = new ArrayList<Cls>(
							getExportConfigurationPanel()
									.getExportedClassesInPanel());
					Collections.sort(selectedClsesInPanel,
							new FrameComparator());
					for (Cls cls : selectedClsesInPanel) {
						allClassesToExport.add(cls);

					}
					return allClassesToExport;

				}

				protected ArrayList<Cls> getInitialExportClses() {
					return ocl;

				}

			};

			exportAction.actionPerformed(null);
			boolean success = exportAction.exportCompletedSuccessful();
			*/
			if (!success) {
				reportTextArea
						.append("\nExport with LQT configuration did not complete.");
			} else {
				reportTextArea
						.append("\nExported using the LQT configuration finished successfully.\n");
				reportTextArea
						.append("Exported file: HOW TO ADD THE NAME? \n");
			}
			return success;

		} else {
			return false;
		}
	}

	protected boolean onLQTExport() {
		/**
		ExportToCsvUtil.setExportBrowserText(false);
		ExportToCsvUtil.setExportMetadata(true);
		ExportToCsvUtil.setExportSuperclass(true);

		NCIExportToCsvAction exportAction = new NCIExportToCsvAction(tab
				.getKnowledgeBase(), configPanel, true) {
			public static final long serialVersionUID = 122256792L;

			public Collection<Instance> getInstancesToExport() {
				Collection<Instance> allClassesToExport = new LinkedHashSet<Instance>();
				List<Cls> selectedClsesInPanel = new ArrayList<Cls>(
						getExportConfigurationPanel()
								.getExportedClassesInPanel());
				Collections.sort(selectedClsesInPanel, new FrameComparator());
				for (Cls cls : selectedClsesInPanel) {
					allClassesToExport.add(cls);
					List<Cls> subclasses = new ArrayList<Cls>(cls
							.getSubclasses());
					Collections.sort(subclasses, new FrameComparator());
					for (Iterator<Cls> iterator = subclasses.iterator(); iterator
							.hasNext();) {
						Cls subclas = (Cls) iterator.next();
						if (subclas instanceof RDFResource
								&& !((RDFResource) subclas).isAnonymous()) {
							allClassesToExport.add(subclas);
						}
					}
				}
				return allClassesToExport;
			}

			@Override
			protected Collection<Cls> getInitialExportClses() {
				Collection selection = tab.getSelection();
				Collection<Cls> res = new ArrayList<Cls>();
				if (selection == null || selection.size() == 0) {
					return res;
				} else {
					Iterator it = selection.iterator();
					while (it.hasNext()) {
						FrameWithBrowserText fbt = (FrameWithBrowserText) it
								.next();
						res.add((Cls) fbt.getFrame());
					}
				}
				res.remove(tab.getOWLModel().getOWLThingClass()); // owl:Thing
				// is
				// always
				// there?!
				return res;
			}
		};

		exportAction.actionPerformed(null);
		boolean success = exportAction.exportCompletedSuccessful();
		if (!success) {
			reportTextArea
					.append("\nExport with LQT configuration did not complete.");
		} else {
			reportTextArea
					.append("\nExported using the LQT configuration finished successfully.\n");
			reportTextArea.append("Exported file: "
					+ exportAction.getExportFile().getAbsolutePath() + "\n");
		}
		return success;
		*/
		return true;
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

			startButton.setEnabled(true);
			configure.setEnabled(false);

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

		hset = new HashSet<String>();
		complexProps = new HashSet<String>();
		Set<OWLAnnotationProperty> cprops = tab.getComplexProperties();
		for (OWLAnnotationProperty p : cprops) {
			hset.add(p.getIRI().getShortForm());
			complexProps.add(p.getIRI().getShortForm());
		}

		//hset.add("ID");

		//sort = new QuickSortVecStrings();

		this.classList = new Vector<String>();

		JTextField fileName = new JTextField();
		fileName.setColumns(45);

		configure = new JButton("Configure");
		configure.addActionListener(this);

		startButton = new JButton("Start");
		startButton.addActionListener(startListener);
		startButton.setEnabled(false);

		interruptButton = new JButton("Cancel");
		interruptButton.addActionListener(interruptListener);
		interruptButton.setEnabled(false);

		progress = new JProgressBar();
		// JLabel progress_msg = new JLabel("foo");
		progress.setPreferredSize(new Dimension(250, 10));
		progress.setMinimum(0);
		progress.setMaximum(100);
		progress.setValue(0);

		JPanel textAreaPanel = new JPanel(new BorderLayout());
		reportTextArea = new JTextArea(25, 45);

		reportTextArea.setEditable(false);

		reportTextArea.setTabSize(2);
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(configure);
		buttonPanel.add(startButton);
		buttonPanel.add(interruptButton);
		buttonPanel.add(progress);

		configure.setEnabled(true);

		textAreaPanel.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);
		textAreaPanel.add(buttonPanel, BorderLayout.SOUTH);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(textAreaPanel);

		textAreaPanel.setBorder(BorderFactory.createTitledBorder("Report"));

		add(box);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
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
				if (printable(line)) {
					pw.println(line);
				}
			}
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean printable(String line) {
		if (line.contains(":DIRECT-SUPERCLASSES"))
			return false;
		if (line.contains(":DIRECT-TYPE"))
			return false;
		if (line.contains("@_"))
			return false;
		if (line.contains("rdf:type"))
			return false;

		return true;
	}
	
	private HashMap<OWLClass, OWLClass> alreadySeen = new HashMap<OWLClass, OWLClass>();

	private String getClsData(PrintWriter pw, OWLClass superCls,
			int level, int maxLevel, boolean withAttributes, String attrid) 

	{

		if (interrupted)
			return "Cancelled";
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

			
			if (withAttributes && (alreadySeen.get(superCls) == null)) {
				// output slot data
				getSlots(superCls, level, tabString);
				// getEquivalentClasses((RDFSClass) superCls, tabString);
				//getAnonymousSuperclasses((RDFSClass) superCls, tabString);
			}
			

			writeToFile();
			classList.clear();

			iCtr++;
			// System.out.println("foo         foo              foo " + iCtr);

			final int peg = iCtr;
			updateStatus(peg);
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			Thread.sleep(300);

			alreadySeen.put(superCls, superCls);
			
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
					if (alreadySeen.get(sub) != null) {
						
					} else {
						reportTextArea.append("adding next level\n");

						getClsData(pw, sub, level,
								maxLevel, withAttributes, attrid);
						
					}
				}
			}
			
		}

		catch (InterruptedException e) {
			updateStatus(0);
			return "Interrupted";
		}

		catch (Exception e) {			
			return "Error";
		}

		return "All Done";

	}

	
	private void getSlots(OWLClass cls, int level, String tabString) {
		Set<OWLAnnotation> annotations = tab.getAnnotations(cls);
		//Vector<String> slotname_vec = new Vector<String>();
		//Vector<String> slotvalue_vec = new Vector<String>();
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

				com.google.common.base.Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
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
				String entry = "";
				com.google.common.base.Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
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
				com.google.common.base.Optional<OWLLiteral> strentry = ann.getValue().asLiteral();
				if (strentry.isPresent()) {
					entry = strentry.get().getLiteral();
				}
				classList.add("\t" + tabString + slotname + ": " + entry);
				
				Set<OWLAnnotation> quals = tab.getDependentAnnotations(cls,ap);
				for (OWLAnnotation qualAnn : quals) {
					OWLAnnotationProperty qap = qualAnn.getProperty();
					String qslotname = qap.getIRI().getShortForm();
					String qentry = "";
					com.google.common.base.Optional<OWLLiteral> qstrentry = qualAnn.getValue().asLiteral();
					if (qstrentry.isPresent()) {
						qentry = qstrentry.get().getLiteral();
					}
					classList.add("\t\t" + tabString + qslotname + ": " + qentry);
					
				}
				// more to do
			}
			
		}
		/**
		
		Collection ownslots = cls.getOwnSlots();

		

		RDFProperty property = tab.getOWLModel().getRDFProperty(
				RDFSNames.Slot.COMMENT);
		OWLNamedClass owlCls = (OWLNamedClass) cls;
		Collection c2 = owlCls.getPropertyValues(property);
		if (c2.size() == 0) {
			slotname_vec.add("comment");
			slotvalue_vec.add("");
		} else {
			for (Iterator it = c2.iterator(); it.hasNext();) {
				Object value = it.next();
				slotname_vec.add("comment");
				slotvalue_vec.add((String) value);
			}
		}

		if (ownslots != null) {
			Iterator j = ownslots.iterator();
			while (j.hasNext()) {
				Slot slot = (Slot) j.next();
				Collection slotColl = cls.getOwnSlotValues(slot);

				if (slotColl == null || slotColl.isEmpty())
					continue;
				String slotName = slot.getBrowserText();
				if (slotName.equalsIgnoreCase("owl:equivalentClass")) {
					continue;
				}
				if (slotName.equals(Model.Slot.NAME)) {
					continue;
				} else if (slotName.equals(Model.Slot.DIRECT_SUBCLASSES))
					continue;
				else if (slotName.equals(Model.Slot.DIRECT_INSTANCES))
					continue;
				else if (slotName.equalsIgnoreCase("protege.classificationStatus"))
					continue;
				else if (slotName.equalsIgnoreCase("protege.inferredSubclassOf"))
					continue;
				else if (slotName.equalsIgnoreCase("protege.inferredSuperclassOf"))
					continue;
				else {

					if (slotColl != null) {
						Iterator i = slotColl.iterator();
						while (i.hasNext()) {
							Object obj = i.next();
							ValueType type = wrapper.getObjectValueType(obj);
							String entry = wrapper.convertObjecttoString(obj,
									type);
							try {
								if (slotName.compareTo(NCIEditTab.SUBCLASSOF) != 0) {
									// classList.add("\t" + tabString + slotname
									// + ": " + entry);
									slotname_vec.add(slotName);
									slotvalue_vec.add(entry);
								}
							} catch (Exception e) {
								Log.getLogger().log(Level.WARNING,
										"Exception caught", e);
								;
							}
						}
					}
					// getOwnslot(slotName, slotColl, tabString);
				}
			}
			sort(slotname_vec, slotvalue_vec, tabString);

		}
		*/
	}
	/**

	private void sort(Vector v1, Vector v2, String tabString) {

		// code
		for (int i = 0; i < v1.size(); i++) {
			String slotname = (String) v1.elementAt(i);
			if (slotname.compareTo("code") == 0) {
				String entry = (String) v2.elementAt(i);
				classList.add("\t" + tabString + slotname + ": " + entry);
				break;
			}
		}
		for (int i = 0; i < v1.size(); i++) {
			String slotname = (String) v1.elementAt(i);
			if (slotname.compareTo("Preferred_Name") == 0) {
				String entry = (String) v2.elementAt(i);
				classList.add("\t" + tabString + slotname + ": " + entry);
				break;
			}
		}
		classList.add("");

		Vector<String> w = new Vector<String>();
		for (int i = 0; i < v1.size(); i++) {
			String slotname = (String) v1.elementAt(i);
			if (!hset.contains(slotname)) {
				// this guy is not complex prop
				String entry = (String) v2.elementAt(i);
				w.add("\t" + tabString + slotname + ": " + entry);
			}
		}
		try {
			sort.sort(w);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < w.size(); i++) {
			String s = (String) w.elementAt(i);
			classList.add(s);
		}
		classList.add("");

		// ALTLABEL
		w = new Vector<String>();
		for (int i = 0; i < v1.size(); i++) {
			String slotname = (String) v1.elementAt(i);
			if (slotname.equals("ALTLABEL")) {
				String entry = (String) v2.elementAt(i);
				w.add("\t" + tabString + slotname + ": " + entry);
			}
		}
		try {
			sort.sort(w);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < w.size(); i++) {
			String s = (String) w.elementAt(i);
			classList.add(s);
		}
		classList.add("");

		// DEFINITION
		w = new Vector<String>();
		for (int i = 0; i < v1.size(); i++) {
			String slotname = (String) v1.elementAt(i);
			if (slotname.compareTo("ID") != 0
					&& complexProps.contains(slotname)) {
				String entry = (String) v2.elementAt(i);
				w.add(slotname + ": " + entry);
			}
		}

		sortAnnotationData(w, tabString);
		classList.add(newline);

	}

	private void sortAnnotationData(Vector v, String tabString) {
		try {
			sort.sort(v);
			for (int i = 0; i < v.size(); i++) {
				String s = (String) v.elementAt(i);
				int pos = s.indexOf(":");
				String name = s.substring(0, pos);
				String value = s.substring(pos + 1, s.length());

				HashMap<String, String> map = ComplexPropertyParser
						.parseXML(value);
				String text = "";
				if (map.containsKey("def-definition")) {
					text = (String) map.get("def-definition");
					classList.add("\t" + tabString + name + ": " + text);
					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						String qName = (String) it.next();
						if (qName.compareTo("def-definition") != 0
								&& qName.compareTo("root") != 0) {
							String qValue = (String) map.get(qName);
							classList.add("\t\t" + tabString + qName + ": "
									+ qValue);
						}
					}

				} else if (map.containsKey("go-term")) {
					text = (String) map.get("go-term");
					classList.add("\t" + tabString + name + ": " + text);

					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						String qName = (String) it.next();
						if (qName.compareTo("go-term") != 0
								&& qName.compareTo("root") != 0) {
							String qValue = (String) map.get(qName);
							classList.add("\t\t" + tabString + qName + ": "
									+ qValue);
						}
					}
				} else if (map.containsKey("def-definition")) {
					text = (String) map.get("def-definition");
					classList.add("\t" + tabString + name + ": " + text);

					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						String qName = (String) it.next();
						if (qName.compareTo("def-definition") != 0
								&& qName.compareTo("root") != 0) {
							String qValue = (String) map.get(qName);
							classList.add("\t\t" + tabString + qName + ": "
									+ qValue);
						}
					}
				} else if (map.containsKey("term-name")) {
					text = (String) map.get("term-name");
					classList.add("\t" + tabString + name + ": " + text);

					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						String qName = (String) it.next();
						if (qName.compareTo("term-name") != 0
								&& qName.compareTo("root") != 0) {
							String qValue = (String) map.get(qName);
							classList.add("\t\t" + tabString + qName + ": "
									+ qValue);
						}
					}
				}
			}
			// classList.add(newline);
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Exception caught", e);
		}
	}
	*/
	
	
	/**

	private void getAnonymousSuperclasses(RDFSClass cls, String tabString) {
		HashSet<String> hset = new HashSet<String>();
		ArrayList<RDFSClass> v = wrapper.getDirectSuperclassItems(cls,
				NCIConditionsTableModel.SET_SUPERCLASS);
		for (RDFSClass c : v) {
			String value = "Named Superclass: " + c.getPrefixedName();

			if (!hset.contains(value)) {
				classList.add("\t" + tabString + value);
				hset.add(value);
			}

		}

		classList.add(newline);
		v = wrapper.getDirectSuperclassItems(cls,
				NCIConditionsTableModel.SET_RESTRICTION);

		for (RDFSClass c : v) {
			String value = c.getBrowserText();
			if (!hset.contains(value)) {
				// classList.add("\t" + tabString + value );
				classList.add("\t" + tabString + "Restricton: " + value);
				hset.add(value);
			}
		}

		classList.add(newline);
		v = wrapper.getDefinitionItems(cls,
				NCIConditionsTableModel.SET_SUPERCLASS);

		for (RDFSClass c : v) {

			String value = "Named Superclass: " + c.getLocalName();

			if (!hset.contains(value)) {
				classList.add("\t" + tabString + value);
				hset.add(value);
			}

		}

		classList.add(newline);
		v = wrapper.getDefinitionItems(cls,
				NCIConditionsTableModel.SET_RESTRICTION);

		for (RDFSClass c : v) {

			String value = c.getBrowserText();
			if (!hset.contains(value)) {
				// classList.add("\t" + tabString + value);
				classList.add("\t" + tabString + "Restricton: " + value
						+ " [defined]");
				hset.add(value);
			}

		}

	}
	*/

	void updateStatus(final int i) {
		Runnable doSetProgressBarValue = new Runnable() {
			public void run() {
				reportTextArea.append("" + i + " out of " + max
						+ " completed.\n");
				progress.setValue(i);
			}
		};
		SwingUtilities.invokeLater(doSetProgressBarValue);
	}

	Object generateReport() {
		//rwp.initializeProgressBar();
		iCtr = 0;
		
	    getClsData(pw, root, 0, maxlevel, withAttributes, attrsId);
		

		if (interrupted) {
			progress.setValue(0);
			startButton.setEnabled(false);
			interruptButton.setEnabled(false);
			configure.setEnabled(true);
			return "Report generation cancelled.";
		}
		configure.setEnabled(true);
		return "Report generation completed.";
	}
	

	ActionListener startListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			startButton.setEnabled(false);
			interruptButton.setEnabled(true);

			reportTextArea.setText("Report generation in progress ...");

			worker = new SwingWorker() {
				public Object construct() {

					reportTextArea
							.append("Calculating tree size. Please wait ...\n");

					alreadySeen = new HashMap<OWLClass, OWLClass>();

					if (maxlevel == -1) {
						maxlevel = getMaxLevel((OWLClass) root);
					}
					// reinit alreadySeen for reuse
					alreadySeen = new HashMap<OWLClass, OWLClass>();

					max = getTreeSize(root, 0, maxlevel);
					// reinit already seen as it's used to compute max
					alreadySeen = new HashMap<OWLClass, OWLClass>();

					reportTextArea.append("Tree size: " + max + "\n");

					progress.setMaximum(max);
					// rwp.showProgressBar(true);

					iCtr = 0;

					return generateReport();
				}

				public void finished() {
					// startButton.setEnabled(true);
					interruptButton.setEnabled(false);
					reportTextArea.append(get().toString());
					interrupted = false;
				}

				
			};
			worker.start();
		}
	};

	ActionListener interruptListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			// rwp.initializeProgressBar();
			interruptButton.setEnabled(false);
			worker.interrupt();
			startButton.setEnabled(false);
			interrupted = true;

			try {
				if (pw != null) {
					pw.flush();
					pw.close();
				}
				pw = null;
			} catch (Exception ex) {

			}
		}
	};

	private int getMaxLevel(OWLClass cls) {
		
		alreadySeen.put(cls, cls);
		if (interrupted)
			return 0;
		try {
			List<OWLClass> subclasses = tab.getDirectSubClasses(cls);
			if (subclasses.isEmpty()) {
				return 1;
			}
			
			int max = 0;
			for (OWLClass sub : subclasses) {
				if (alreadySeen.get(sub) != null) {
					return max;
				} else {
					int m = getMaxLevel(sub);
					if (m > max) {
						max = m;
					}
				}
			}
			return max + 1;
			
			
		} catch (Exception e) {
			updateStatus(0);
			return 0;
		}
	}

	private int getTreeSize(OWLClass cls, int level, int maxlevel) {

		alreadySeen.put(cls, cls);
		

		if (interrupted)
			return 0;
		try {
			
			int num_subs = 0;
			if (level < maxlevel) {
				level++;
				List<OWLClass> subclasses = tab.getDirectSubClasses(cls);
				
				for (OWLClass sub : subclasses) {
					num_subs = num_subs
							+ getTreeSize(sub, level, maxlevel);
					
				}
				
				
			}
			return num_subs + 1;
		} catch (Exception e) {
			updateStatus(0);
			return 0;
		}
	}

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
		// TODO Auto-generated method stub
		
	}

	

}
