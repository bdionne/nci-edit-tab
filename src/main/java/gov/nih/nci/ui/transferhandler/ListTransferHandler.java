package gov.nih.nci.ui.transferhandler;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

public class ListTransferHandler extends TransferHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3809002388495775198L;

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
		
		OWLFrameList<IRI> list = (OWLFrameList<IRI>)support.getComponent();
		list.setRootObject(data.get(0).getIRI());
		
		return true;
		
	}

}
