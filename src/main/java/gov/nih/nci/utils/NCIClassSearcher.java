package gov.nih.nci.utils;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.ClassSearcher;
import org.semanticweb.owlapi.model.OWLClass;

import edu.stanford.protege.search.lucene.tab.ui.LuceneQueryPanel;

public class NCIClassSearcher implements ClassSearcher {
	
	private OWLEditorKit oek = null;
	
	public NCIClassSearcher(OWLEditorKit kit) {
		this.oek = kit;
	}

	@Override
	public OWLClass searchFor(Component parent) {		

		final JButton okButton = new JButton( "OK" );

		LuceneQueryPanel panel = new LuceneQueryPanel(oek, LuceneQueryPanel.LuceneTabLayout.VERTICAL, okButton);
		panel.setPreferredSize(new Dimension(600, 400));	

		int response = JOptionPaneEx.showDialogUsingOkButton(parent,
				"Lucene Query Dialog", panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, okButton);
		OWLClass output = null;
		if (response == JOptionPane.OK_OPTION) {
			if (panel.getSelectedEntity() != null) {
				output = panel.getSelectedEntity().asOWLClass();
			}
		}	    
		panel.dispose();
		return output;

	}

}
