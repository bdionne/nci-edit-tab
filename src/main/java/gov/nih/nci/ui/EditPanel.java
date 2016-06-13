package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

public class EditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
	
	private Set<OWLAnnotationProperty> complexProperties;
    
    private List<PropertyTablePanel> tablePanelList = new ArrayList<PropertyTablePanel>();
    
	private JSplitPane splitPane;
    
    public EditPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        
        complexProperties = NCIEditTab.currentTab().getComplexProperties();
        
        createUI();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setBorder(BorderFactory.createTitledBorder("Description:"));
        
        JPanel complexPropertyPanel = new JPanel();
        complexPropertyPanel.setLayout(new BoxLayout(complexPropertyPanel, BoxLayout.Y_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Complex Properties", complexPropertyPanel);

        JScrollPane panel2 = new JScrollPane();//will add tree or list to it
        panel2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("General", panel2);
        
        Iterator it = complexProperties.iterator();
        while(it.hasNext()) {
        	addComplexPropertyTable(complexPropertyPanel, (OWLAnnotationProperty)it.next());
        }
        
        upperPanel.add(tabbedPane);
        
        JScrollPane lowerComp = new JScrollPane();// will add description list to it
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lowerPanel.add(lowerComp);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0.6);

		add(splitPane, BorderLayout.CENTER);
		
		setVisible(true);
		restoreDefaults();

        
    }
    
    private void addComplexPropertyTable(JPanel complexPropertyPanel, OWLAnnotationProperty complexProperty) {
    	Optional<String> tableName = NCIEditTab.currentTab().getRDFSLabel(complexProperty);
    	PropertyTablePanel tablePanel = new PropertyTablePanel(this.owlEditorKit, complexProperty, tableName.get());
    	complexPropertyPanel.add(tablePanel);
    	tablePanelList.add(tablePanel);
    }
    
    private void restoreDefaults() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	splitPane.setDividerLocation(splitPane.getSize().height /2);
            }
        });
    }    

    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
    
    public List<PropertyTablePanel> getPropertyTablePanelList() {
    	return tablePanelList;
    }

}
