package gov.nih.nci.ui.transferhandler;


import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;
import gov.nih.nci.ui.RetirePanel;

public class RetireTransferHandler extends TransferHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3809002388495775198L;
	private RetirePanel retirePanel;
	
	public RetireTransferHandler(RetirePanel retirePanel) {
		this.retirePanel = retirePanel;
	}

	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support) {
		if( !canImport(support)) {
			return false;
		}
		
		List<OWLClass> data; 
		try {
			data = (List<OWLClass>) support.getTransferable().getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
			
			
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		if (NCIEditTab.currentTab().canRetire(data.get(0))) {
			retirePanel.setOWLClass(data.get(0));
		} else {
			JOptionPane.showMessageDialog(retirePanel, "Unable to retire", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		return true;
		
	}
}
