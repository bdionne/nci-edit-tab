package gov.nih.nci.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.usage.UsageFilter;
import org.protege.editor.owl.ui.usage.UsagePreferences;
import org.protege.editor.owl.ui.usage.UsageTree;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * 
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 * 
 * Code copied from org.protege.editor.owl.ui.usage.Usage in protege-owl
 * which provides more function and support the Usage panel
 * Author: Bob Dionne<br>
 * 
 */
public class UsagePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -570830166794213904L;
	private UsageTree tree;
    
    public UsagePanel(OWLEditorKit owlEditorKit) {
        setLayout(new BorderLayout());

        tree = new UsageTree(owlEditorKit);      

        add(new JScrollPane(tree), BorderLayout.CENTER);
   }


    public void setOWLEntity(OWLEntity entity) {
    	// set preferences here in case user changes them in entities tab
    	UsagePreferences.getInstance().setFilterActive(UsageFilter.filterSelf, true);
    	UsagePreferences.getInstance().setFilterActive(UsageFilter.filterDisjoints, true);
    	UsagePreferences.getInstance().setFilterActive(UsageFilter.filterDifferent, true);
    	UsagePreferences.getInstance().setFilterActive(UsageFilter.filterNamedSubsSupers, false);
    	UsagePreferences.getInstance().setFilterActive(UsageFilter.filterDifferent, true);  

    	tree.setOWLEntity(entity);        
    }
    
    public int getCount() {
    	return tree.getRowCount();
    }
    
}


