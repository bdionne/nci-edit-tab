package gov.nih.nci.utils.batch;

import java.util.Vector;

public abstract class EditProcessor {
	
	public EditProcessor() {}
	
	public Vector<String> validateData(Vector<String> v) {
		Vector<String> w = new Vector<String>();
		
		return w;
	}
	
	public boolean processData(Vector<String> data) {
		return true;
	}

}
