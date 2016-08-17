package gov.nih.nci.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import gov.nih.nci.ui.BatchProcessOutputPanel;
import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.utils.batch.BatchEditTask;
import gov.nih.nci.utils.batch.BatchLoadTask;
import gov.nih.nci.utils.batch.BatchTask;

/**
 * @Author: NGIT, Kim Ong; Iris Guo
 */

public class BatchProcessingDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3817605737614597419L;
	private static final Logger log = Logger.getLogger(BatchProcessingDialog.class);

	JButton fStartButton, fCancelButton, fInputButton, fOutputButton;

	JTextField fInputTf, fOutputTf;

	JComboBox<String> batchType = null;

	NCIEditTab tab;

	BatchProcessOutputPanel be = null;	

	String infile;
	String outfile;

	public static final int BATCH_LOADER = 2;

	public static final int BATCH_EDITOR = 1;

	int type = BATCH_EDITOR;

	public BatchProcessingDialog(BatchProcessOutputPanel b, NCIEditTab tab) {
		be = b;
		this.tab = tab;
		
		this.infile = "";
		this.outfile = "";

		setModal(true);

		this.setTitle("Batch Processor");
		init();
	}

	public void setBatchProcessType(int type) {
		this.type = type;
	}
	
	private JPanel createFileField(String label, String extension, String type){
		
	  JPanel panel = new JPanel();
	  panel.setPreferredSize(new Dimension(350, 30));
	  JLabel lb = new JLabel(label);
	  JTextField field = new  JTextField();
	  
	  field.setPreferredSize(new Dimension(250, 25));
	  
	  JButton btn = new JButton();
	  btn.setText("browse");
	  
	  
	  btn.addActionListener(new ActionListener(){
		  
		  public void actionPerformed(ActionEvent e){
			  if(type == "input"){
				  JFileChooser fc = new JFileChooser();
				  //to do - add file filter
				  int select = fc.showOpenDialog(BatchProcessingDialog.this);
				  if (select == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            infile = file.getAbsolutePath();
			            field.setText(infile);
				  }
			  }
			  else{
				  JFileChooser fc = new JFileChooser();
				  //todo - add file extension filter
				  int select = fc.showSaveDialog(BatchProcessingDialog.this);
				  
				  if (select == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            outfile = file.getAbsolutePath();
			            field.setText(outfile);
				  }
			  }
		  }
	  });
	  panel.add(lb);
	  panel.add(field);
	  panel.add(btn);
	  
	  return panel;
	}

	public void init() {
		try {
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());

			this.setLocation(450, 450);

			JPanel filePanel = new JPanel();
			filePanel.setLayout(new BorderLayout());

			
			filePanel.add(createFileField("Input File", "dat","input"), BorderLayout.NORTH);
			
			filePanel.add(createFileField("Output File", "out","output"), BorderLayout.CENTER);

			container.add(filePanel, BorderLayout.NORTH);

			String[] types = new String[] { "Edit", "Load" };
			batchType = new JComboBox<String>(types);
			batchType.setSelectedIndex(0);
			batchType.addActionListener(this);

			
			JPanel labelcombopanel = new JPanel();
			labelcombopanel.setPreferredSize(new Dimension(350, 50));
			labelcombopanel.add(new JLabel("Batch Type"));
			labelcombopanel.add(batchType, BorderLayout.CENTER);
			container.add(labelcombopanel, BorderLayout.CENTER);
			
			fStartButton = new JButton("Start");
			fStartButton.addActionListener(this);

			fCancelButton = new JButton("Cancel");
			fCancelButton.addActionListener(this);

			JPanel btnPanel = new JPanel();
			btnPanel.add(fStartButton);
			btnPanel.add(fCancelButton);

			container.add(btnPanel, BorderLayout.SOUTH);

			pack();
			this.setVisible(true);

		} catch (Exception ex) {
			log.warn("Exception caught", ex);

		}
	}

	public String getInfile() {
		return  infile;
	}

	public String getOutfile() {		
		return outfile;
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

		if (action == batchType) {
			// TODO: Bob, this is disgusting
			type = batchType.getSelectedIndex() + 1;
		} else if (action == fCancelButton) {
			dispose();
		} else if (action == fStartButton) {
			infile = getInfile();
			outfile = getOutfile();

			if (infile == null || infile.compareTo("") == 0) {				
				return;
			}

			else if (outfile == null || outfile.compareTo("") == 0) {				
				return;
			} else {
				setVisible(false);
				TaskProgressDialog tpd = null;

				BatchTask task = null;
				if (type == BATCH_LOADER) {
					
					task = new BatchLoadTask(tab, infile, outfile);
					
					task.setType(BatchTask.TaskType.LOAD);
					tpd = new TaskProgressDialog(new JFrame(), 
							"Batch Load Progress Status", task);
				} else if (type == BATCH_EDITOR) {
					task = new BatchEditTask(tab, infile, outfile);
					task.setType(BatchTask.TaskType.EDIT);
					tpd = new TaskProgressDialog(new JFrame(),
							"Batch Edit Progress Status", task);
				}

				task.openPrintWriter(outfile);

				//task.validateData();
				if (!task.canProceed()) {
					//MsgDialog
					//		.error(
					//				(JFrame) tab.getTopLevelAncestor(),
					//				"Severe input data issues detected, cannot proceed. "
					//						+ "Please correct all errors and try again.");
					return;
				}

				if (tpd != null) {
					tpd.run();
				}

				dispose();

				if (tpd != null) {
					//MsgDialog.ok((JFrame) tab.getTopLevelAncestor(),
					//	getTitle(), "Completed actions: "
					//			+ tpd.getNumCompleted());
				}
				try {
					task.closePrintWriter();
				} catch (Exception e) {

				}

				if (task.isCancelled()) {
					//be.enableButton("clearButton", true);
				} else {
					//	be.enableButton("inputButton", false);
					//	be.enableButton("saveButton", true);
					//	be.enableButton("clearButton", true);
				}

			}

		}

	}
}
