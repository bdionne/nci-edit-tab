package gov.nih.nci.utils.batch;

import java.util.Vector;

import gov.nih.nci.ui.NCIEditTab;

public class ParentsProcessor extends EditProcessor {

	public ParentsProcessor(NCIEditTab t) {
		super(t);
		// TODO Auto-generated constructor stub
	}
	
	public Vector<String> validateData(Vector<String> v) {
		return super.validateData(v);
	}
	
	public boolean processData(Vector<String> data) {
		return true;
	}

}
