package gov.nih.nci.utils.batch;

import java.util.Vector;

import gov.nih.nci.ui.BatchProcessOutputPanel;
import gov.nih.nci.ui.NCIEditTab;

/**
 * @Author: NGIT, Kim Ong; Iris Guo
 */

public class BatchEditTask extends BatchTask {
	
	
	
	TaskType batchtype = null;
	EditProcessor processor = null;
	
	
	
	private void initProcessor(TaskType t) {
		if (t.equals(TaskType.EDIT_SIMPLE_PROPS)) {
			processor = new SimplePropProcessor(tab);
		} else if (t.equals(TaskType.EDIT_COMPLEX_PROPS)) {
			processor = new ComplexPropProcessor(tab);			
		}		
	}
	
	public BatchEditTask(BatchProcessOutputPanel be, NCIEditTab tab, String infile, String outfile, 
			String fileDelim, TaskType t) {
		
		super(be, tab);
		this.infile = infile;
		this.outfile = outfile;
		this.fieldDelim = fileDelim;
		this.batchtype = t;
		
		initProcessor(batchtype);	

		data_vec = getData(infile);
		setMax(data_vec.size());
		setMessage("Batch Edit processing in progress, please wait ...");
	}
	
	public boolean processTask(int taskId) {

		String s = (String) data_vec.elementAt(taskId);

		super.print("processing: " + s);

		try {

			Vector<String> w = parseTokens(s);
			
			if (super.checkNoErrors(w, taskId)) {
				
			} else {
				return false;
			}
			
			boolean retval = processor.processData(w);

			if (retval) {
				/**
				tab.recordHistory(NCIEditTab.EVSHistoryAction.MODIFY, wrapper
						.getOWLNamedClass(name), "");
						*/
				super.print("\t Done.");
			} else {
				super.print("\t Failed.");
			}
			
		} catch (Exception ex) {
			print("Server Error occurred:");
			ex.printStackTrace();
			super.print(" Failed.");
			data_vec.remove(taskId);
			this.setMax(max - 1);
			return false;
		}

		return true;
	}

	

	public Vector<String> validateData(Vector<String> v) {
		
		return processor.validateData(v);

	}
}

