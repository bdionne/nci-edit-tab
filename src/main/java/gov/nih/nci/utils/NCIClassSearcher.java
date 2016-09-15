package gov.nih.nci.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.ClassSearcher;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import edu.stanford.protege.search.lucene.tab.ui.LuceneQueryPanel;

public class NCIClassSearcher implements ClassSearcher {
	
	private OWLEditorKit oek = null;
	
	public NCIClassSearcher(OWLEditorKit kit) {
		this.oek = kit;
	}

	@Override
	public OWLClass searchFor(Component parent) {


		LuceneQueryPanel panel = new LuceneQueryPanel(oek, LuceneQueryPanel.LuceneTabLayout.VERTICAL);
		panel.setPreferredSize(new Dimension(600, 400));
	    int response = JOptionPaneEx.showConfirmDialog(parent,
	    		"Lucene Query Dialog", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
	    OWLClass output = null;
	    if (response == JOptionPane.OK_OPTION) {
	        output = panel.getSelectedEntity().asOWLClass();
	    }	    
	    panel.dispose();
	    return output;
	}

}
