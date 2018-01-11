package gov.nih.nci.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import gov.nih.nci.ui.BatchProcessOutputPanel;
import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.utils.batch.BatchEditTask;
import gov.nih.nci.utils.batch.BatchLoadTask;
import gov.nih.nci.utils.batch.BatchTask;
import gov.nih.nci.utils.batch.BatchTask.TaskType;

/**
 * @Author: NGIT, Kim Ong; Iris Guo
 */

public class BatchProcessingDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3817605737614597419L;
	private static final Logger log = Logger.getLogger(BatchProcessingDialog.class);

	JButton fStartButton, fCancelButton, fInputButton, fOutputButton;

	JTextField fInputTf, fOutputTf;

	JComboBox<TaskType> batchType = null;
	
	JLabel fileDelimLbl;
	JTextField fileDelim;
	
	public static final String FILE_DELIMITER = ",", PROPERTY_VALUES_DELIMITER = "\t";
	protected static final String LAST_USED_FOLDER = "";
	protected static final String LAST_BATCH_TYPE = "";
	
	private List<TaskType> batch_type_items = new ArrayList<TaskType>();
	
	
	NCIEditTab tab;

	BatchProcessOutputPanel be = null;	

	String infile;
	String outfile;
	int btindex;
	
	File inputFolder;


	TaskType edit_type = TaskType.LOAD;

	public BatchProcessingDialog(BatchProcessOutputPanel b, NCIEditTab tab) {
		be = b;
		this.tab = tab;
		
		this.infile = "";
		this.outfile = "";
		this.btindex = 0;

		setModal(true);

		this.setTitle("Batch Processor");
		init();
	}

	
	
	private JPanel createFileField(String label, String extension, String type){
		
	  JPanel panel = new JPanel();
	  panel.setPreferredSize(new Dimension(470, 30));
	  JLabel lb = new JLabel(label);
	  
	  if (type == "input") {
		  fInputTf = new  JTextField();	  
		  fInputTf.setPreferredSize(new Dimension(250, 25));
	  } else{		  
		  fOutputTf = new  JTextField();		  
		  fOutputTf.setPreferredSize(new Dimension(250, 25));
	  }
	  
	  JButton btn = new JButton();
	  btn.setText("browse");
	  
	  
	  btn.addActionListener(new ActionListener() {

		  public void actionPerformed(ActionEvent e) {
			  if(type == "input"){
				  
				  Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());   
				  JFileChooser fc = new JFileChooser(prefs.getString(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
				  
				  //to do - add file filter
				  int select = fc.showOpenDialog(BatchProcessingDialog.this);
				  if (select == JFileChooser.APPROVE_OPTION) {
					  prefs.putString(LAST_USED_FOLDER, fc.getSelectedFile().getParent());
					  
					  File file = fc.getSelectedFile();
					  infile = file.getAbsolutePath();
					  fInputTf.setText(infile);

					  String filename = file.getName();
					  String filedir = infile.replaceFirst(filename, "");

					  inputFolder = new File(filedir);
				  }
			  } else {
				  JFileChooser fc = new JFileChooser();
				  if(inputFolder != null){
					  fc.setCurrentDirectory(inputFolder);
				  }
				  //todo - add file extension filter
				  int select = fc.showSaveDialog(BatchProcessingDialog.this);

				  if (select == JFileChooser.APPROVE_OPTION) {
					  File file = fc.getSelectedFile();
					  outfile = file.getAbsolutePath();
					  fOutputTf.setText(outfile);
				  }
			  }
		  }
	  });
	  
	  panel.add(lb);
	  if(type == "input"){
		  panel.add(fInputTf);		  
	  } else {
		  panel.add(fOutputTf);
	  }
	  
	  panel.add(btn);
	  
	  return panel;
	}

	public void init() {
		try {
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());

			this.setLocation(450, 520);

			JPanel filePanel = new JPanel();
			filePanel.setLayout(new BorderLayout());

			
			filePanel.add(createFileField("Input  File  ", "dat","input"), BorderLayout.NORTH);
			
			filePanel.add(createFileField("Output File", "out","output"), BorderLayout.CENTER);

			container.add(filePanel, BorderLayout.NORTH);
			
			batch_type_items.add(BatchTask.TaskType.LOAD);
			batch_type_items.add(BatchTask.TaskType.EDIT_SIMPLE_PROPS);
			batch_type_items.add(BatchTask.TaskType.EDIT_COMPLEX_PROPS);
			batch_type_items.add(BatchTask.TaskType.EDIT_PARENTS);
			batch_type_items.add(BatchTask.TaskType.EDIT_ROLES);
			
			batchType = new JComboBox<TaskType>();
			
			for (TaskType t : batch_type_items) {
				batchType.addItem(t);
			}
			
			Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());   		  
			btindex = prefs.getInt(LAST_BATCH_TYPE, 0);
			if( btindex >= 0 && btindex <= 4 ) {
				batchType.setSelectedItem(batch_type_items.get(btindex)); 
				edit_type = (TaskType) batchType.getSelectedItem();
			}
		
			batchType.addActionListener(new ActionListener() {

				  public void actionPerformed(ActionEvent e) {
					  
					  prefs.putInt(LAST_BATCH_TYPE, batchType.getSelectedIndex());
					  edit_type = (TaskType) batchType.getSelectedItem();
					  
				  }
			  });
			
			
			JPanel labelcombopanel = new JPanel();
			labelcombopanel.setPreferredSize(new Dimension(420, 30));
			labelcombopanel.add(new JLabel("Batch Type"));
			labelcombopanel.add(batchType, BorderLayout.CENTER);

			fileDelimLbl = new JLabel("Field Delimiter                     ");
			
			fileDelim = new JTextField(this.PROPERTY_VALUES_DELIMITER);
			fileDelim.setPreferredSize(new Dimension(100, 25));
	        
	        fileDelim.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
	        fileDelim.addKeyListener(keyListener);
						
			JPanel filedelimpanel = new JPanel();
			filedelimpanel.setPreferredSize(new Dimension(420, 25));
			filedelimpanel.add(fileDelimLbl);
			filedelimpanel.add(fileDelim, BorderLayout.CENTER);
			
			JPanel propvaluedelimpanel = new JPanel();
			propvaluedelimpanel.setPreferredSize(new Dimension(420, 25));
			
			JPanel centerPanel = new JPanel();
			centerPanel.setPreferredSize(new Dimension(420, 100));
			centerPanel.add(labelcombopanel, BorderLayout.NORTH);
			centerPanel.add(filedelimpanel, BorderLayout.CENTER);
			centerPanel.add(propvaluedelimpanel, BorderLayout.SOUTH);
			container.add(centerPanel, BorderLayout.CENTER);
			
			fStartButton = new JButton("Start");
			fStartButton.addActionListener(this);

			fCancelButton = new JButton("Cancel");
			fCancelButton.addActionListener(this);

			JPanel btnPanel = new JPanel();
			btnPanel.setPreferredSize(new Dimension(420, 35));
			btnPanel.add(fStartButton);
			btnPanel.add(fCancelButton);

			container.add(btnPanel, BorderLayout.SOUTH);
            
			pack();
			this.setVisible(true);

		} catch (Exception ex) {
			log.warn("Exception caught", ex);
		}
	}
	
	private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.getSource().equals(fileDelim)) {
                    fileDelim.setText(fileDelim.getText() + "\t");
                }
            } else {
                super.keyReleased(e);
            }
        }
    };

	public String getInfile() {
		if (fInputTf != null) {
		   return  fInputTf.getText();
		}		
		return "";
	}

	public String getOutfile() {
		
		if(fOutputTf != null){
			return fOutputTf.getText();
		}
		
		return "";
	}

	public String getToday() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
		String today = sdf.format(cal.getTime());
		return today;
	}

	public void outputErrors(String outputfile, Vector<String> v) {
		if (outputfile == null || v == null)
			return;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					outputfile)));
			String msg = getToday() + "\n";
			writer.println(msg);

			be.getTextArea().append(msg);
			be.getTextArea().append("\n");

			for (int i = 0; i < v.size(); i++) {
				msg = (String) v.elementAt(i);

				be.getTextArea().append(msg);
				be.getTextArea().append("\n");
				writer.println(msg);
			}
			writer.close();
		} catch (Exception e) {
			log.warn("Exception caught", e);
			try {
				writer.close();
			} catch (Exception ex) {
				log.warn("Exception caught", ex);
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		Object action = event.getSource();

		if (action == fCancelButton) {
			dispose();
		} else if (action == fStartButton) {
			infile = getInfile();
			outfile = getOutfile();

			if (infile == null || infile.compareTo("") == 0) {				
				return;
			}

			else if (outfile == null || outfile.compareTo("") == 0) {				
				return;
			} else if (infile.equals(outfile)) {
				JOptionPane.showMessageDialog(this, "Output file is same as input file. Please use different file name/extension.");
            	
			} else {
				setVisible(false);
				TaskProgressDialog tpd = null;

				BatchTask task = null;
				if (edit_type == TaskType.LOAD) {
					
					//task = new BatchLoadTask(be, tab, infile, outfile);
					task = new BatchLoadTask(be, tab, infile, outfile, fileDelim.getText());
					
					tpd = new TaskProgressDialog(new JFrame(), 
							"Batch Load Progress Status", task, tab);
				} else {
					//task = new BatchEditTask(be, tab, infile, outfile);
					task = new BatchEditTask(be, tab, infile, outfile, fileDelim.getText(), edit_type);
					
					tpd = new TaskProgressDialog(new JFrame(),
							"Batch Edit Progress Status", task, tab);
				}

				task.openPrintWriter(outfile);

				
				if (!task.canProceed()) {
					return;
				}
				

				if (tpd != null) {
					task.begin();
					tpd.run();
				}			

				try {
			        
					
					task.complete();
					task.closePrintWriter();
				} catch (Exception e) {

				}
				dispose();
			}
		}
	}
}
