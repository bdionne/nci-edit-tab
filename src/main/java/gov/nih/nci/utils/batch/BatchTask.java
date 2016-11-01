package gov.nih.nci.utils.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import gov.nih.nci.ui.BatchProcessOutputPanel;
import gov.nih.nci.ui.NCIEditTab;

/**
 * @Author: Bob Dionne
 */
public class BatchTask {
	
	private static final Logger log = Logger.getLogger(BatchTask.class);

	public static enum TaskType {
		LOAD, EDIT
	};
	
	NCIEditTab tab = null;

	boolean done = false;

	private boolean canProceed = true;

	public boolean canProceed() {
		return canProceed;
	}
	
	public boolean complete() {
		tab.commitChanges();
		tab.disableBatchMode();
		return true;
	}
	
	public void cancelTask() {
		closePrintWriter();
		cancelled = true;		
		tab.undoChanges();
		tab.disableBatchMode();
	}
	
	public boolean begin() {
		tab.enableBatchMode();
		return true;
	}
	
	String infile = null;

	String outfile = null;
	
	String fieldDelim = null;

	Vector<String> data_vec = null;

	String message;
	int max = 10000;
	int min = 0;
	boolean cancelled = false;
	boolean canCancel = true;
	String title = null;
	
	BatchProcessOutputPanel bp = null;

	PrintWriter pw = null;
	TaskType batchtype = TaskType.LOAD;

	public BatchTask(BatchProcessOutputPanel be, NCIEditTab t) {
		bp = be;
		tab = t;
		setMax(10000);
		cancelled = false;
		String title = "Batch Processing";
		setTitle(title);
		setMessage("Batch processing in progress, please wait ...");
	}

	public void setType(TaskType type) {
		batchtype = type;
	}

	protected void setMax(int max) {
		this.max = max;
	}

	

	/**
	 * Gets the title for this task.
	 */

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setPrintWriter(PrintWriter pw) {
		this.pw = pw;
	}

	public PrintWriter openPrintWriter(String outputfile) {
		if (outputfile == null)
			return null;
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(outputfile)));
			this.pw = writer;
			print(getToday() + "\n");
			return writer;
		} catch (Exception e) {
			return null;
		}
	}

	public void print(String msg) {
		if (pw != null) {
			pw.println(msg);
		}
		bp.getTextArea().append(msg + "\n");

	}

	public void closePrintWriter() {
		if (pw == null)
			return;
		try {
			pw.close();
			pw = null;
		} catch (Exception e) {
			log.warn("Exception caught", e);
		}
	}

	/**
	 * Gets the minimum progress value for this task.
	 */
	public int getProgressMin() {
		return min;
	}

	/**
	 * Gets the maximum progress value for this task.
	 */
	public int getProgressMax() {
		return max;
	}

	/**
	 * Checks whether this Task has been cancelled. Unless either method is
	 * overloaded, this will return true after cancelTask has been called (e.g.,
	 * via the cancel button).
	 * 
	 * @return true if this has been cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCanCancel(boolean b) {
		canCancel = b;
	}

	/**
	 * Determines if the task can be cancelled
	 * 
	 * @return <code>true</code> if the task can be cancelled, or
	 *         <code>false</code> if the task cannot be cancelled.
	 */
	public boolean isPossibleToCancel() {
		return canCancel;
	}

	public boolean processTask(int taskId) {

		return true;
	}

	public boolean 	checkNoErrors(Vector<String> w, int i) {
		Vector<String> errors = this.validateData(w);
		if (errors.size() > 0) {
			for (int j = 0; j < errors.size(); j++) {
				print("record " + (i+1) + ": " + errors.elementAt(j));
			}
			return false;

		}
		return true;
	}

	public String getToday() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
		String today = sdf.format(cal.getTime());
		return today;
	}

	public Vector<String> getData(String filename) {
		Vector<String> v = new Vector<String>();
		if (filename == null)
			return v;
		String s;
		BufferedReader inFile = null;
		try {
			inFile = new BufferedReader(new FileReader(filename));
			while ((s = inFile.readLine()) != null) {
				s = s.trim();
				if (s.length() > 0) {
					if (s.startsWith("#")) {
						// ignore comment lines
					} else {
						v.add(s);
					}
				}
			}
			inFile.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		return v;
	}

	public Vector<String> validateData(Vector<String> v) {
		return null;
	}

	public Vector<String> getTokenStr(String value, int no_tokens) {
		Vector<String> tokenValues = new Vector<String>();
		// make sure there are enough values, even if all empty
		String[] toks = value.split(fieldDelim);
		for (int i = 0; i < no_tokens; i++) {
			String elem = "NA";
			if ((i < toks.length) && !(toks[i].compareTo("") == 0)) {
				elem = toks[i];
			}
			tokenValues.addElement(elem);
		}
		return tokenValues;
	}


}
