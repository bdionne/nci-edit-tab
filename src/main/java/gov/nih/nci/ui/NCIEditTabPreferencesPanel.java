package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.ui.framelist.AxiomAnnotationButton;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import edu.stanford.protege.csv.export.ui.UiUtils;
import edu.stanford.protege.metaproject.ConfigurationManager;
import edu.stanford.protege.metaproject.api.Project;
import edu.stanford.protege.metaproject.api.ProjectId;
import edu.stanford.protege.metaproject.api.ProjectOptions;
import edu.stanford.protege.metaproject.api.exception.UnknownProjectIdException;
import edu.stanford.protege.metaproject.impl.DescriptionImpl;
import edu.stanford.protege.metaproject.impl.NameImpl;
import edu.stanford.protege.metaproject.impl.ProjectIdImpl;
import edu.stanford.protege.metaproject.impl.ProjectOptionsImpl;
import edu.stanford.protege.metaproject.impl.UserIdImpl;
import edu.stanford.protege.search.lucene.tab.engine.QueryType;
import edu.stanford.protege.search.lucene.tab.ui.LuceneUiUtils;
//import edu.stanford.protege.search.lucene.tab.ui.OwlEntityComboBox;
import edu.stanford.protege.search.lucene.tab.ui.OwlEntityComboBoxChangeHandler;
import edu.stanford.protege.search.lucene.tab.ui.TabPreferences;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.PreferencesChangeEvent;
import gov.nih.nci.utils.OwlEntityComboBox;
import gov.nih.nci.utils.ProjectOptionsBuilder;
import gov.nih.nci.utils.ProjectOptionsConfigManager;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

public class NCIEditTabPreferencesPanel extends OWLPreferencesPanel {
	//Add comments here
	private static final long serialVersionUID = 1L;
	
	private MList immutablepropList;
	private MList complexpropList;
	//private MList splitFromList;
	//private MList mergeSourceList;
	//private MList mergeTargetList;
	//private MList retirepropList;
	
	private AxiomAnnotationButton immutAxiomAnnotationButton;
	private AxiomAnnotationButton complexAxiomAnnotationButton;
	//private AxiomAnnotationButton retireAxiomAnnotationButton;
	//private AxiomAnnotationButton mergeSourceAxiomAnnotationButton;
	//private AxiomAnnotationButton mergeTargetAxiomAnnotationButton;
	private Map<OWLEntity, List<OWLEntity>> immutDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	private Map<OWLEntity, List<OWLEntity>> complexDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	//private Map<OWLEntity, List<OWLEntity>> retireDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	//private Map<OWLEntity, List<OWLEntity>> mergeSourceDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	//private Map<OWLEntity, List<OWLEntity>> mergeTargetDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	
	private OwlEntityListItem selectedImmutablePropertyListItem;
	private OwlEntityListItem selectedComplexPropertyListItem;
	//private OwlEntityListItem selectedRetirePropertyListItem;
	//private OwlEntityListItem selectedMergeSourcePropertyListItem;
	//private OwlEntityListItem selectedMergeTargetPropertyListItem;
	private OWLClass retireCptRootClass;
	
	//private OwlEntityComboBox retireCptRootComboBox;
	private JTextField retireCptRootTxtfld;
	private JButton retireCptRootSearchBtn;
	private File selectedFile;
	private JTextField  userSelectedFilePathTxtfld;
	private JButton exportBtn;
	private OwlEntityComboBox retireDesignNoteComboBox;
	private OwlEntityComboBox retireEditorNoteComboBox;
	private OwlEntityComboBox retireCptStatusComboBox;
	private OwlEntityComboBox retireParentComboBox;
	private OwlEntityComboBox retireChildComboBox;
	private OwlEntityComboBox retireRoleComboBox;
	private OwlEntityComboBox retireInRoleComboBox;
	private OwlEntityComboBox retireAssocComboBox;
	private OwlEntityComboBox retireInAssocComboBox;
	
	private OwlEntityComboBox mergeSourceComboBox;
	private OwlEntityComboBox mergeTargetComboBox;
	private OwlEntityComboBox mergeDesignNoteComboBox;
	private OwlEntityComboBox mergeEditorNoteComboBox;
	private OwlEntityComboBox splitFromComboBox;
	
	/*private OwlEntityComboBoxChangeHandler rcrComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler rdnComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler renComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler rcsComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler rpComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler rcComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler rrComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler raComboBoxChangeHandler;
	
	private OwlEntityComboBoxChangeHandler msComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler mtComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler mdnComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler menComboBoxChangeHandler;
	private OwlEntityComboBoxChangeHandler sfComboBoxChangeHandler;*/
	
	private OWLEditorKit editorKit = null;
	List<OWLEntity> properties = new ArrayList<OWLEntity>();
	private Map<String, Set<String>> projectOptions = new HashMap<>();
	
    //private JLabel complexEditLabel = new JLabel("Complex Edit");

    JCheckBox cbSplit = new JCheckBox("Split");
    JCheckBox cbCopy = new JCheckBox("Copy");
    JCheckBox cbMerge = new JCheckBox("Merge");
    JCheckBox cbDualEdits = new JCheckBox("Dual Edits");
    JCheckBox cbRetire = new JCheckBox("Retire");

    @Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.setPreferredSize(new Dimension(400, 140));
        add(panel, BorderLayout.SOUTH);
        //panel.setPreferredSize(new Dimension(700,800));

        panel.addGroup("ComplexEdit");
      //  panel.addGroupComponent(complexEditLabel);

        cbSplit.setSelected(NCIEditTabPreferences.getFnSplit());
        cbCopy.setSelected(NCIEditTabPreferences.getFnCopy());
        cbMerge.setSelected(NCIEditTabPreferences.getFnMerge());
        cbDualEdits.setSelected(NCIEditTabPreferences.getFnDualEdits());
        cbRetire.setSelected(NCIEditTabPreferences.getFnRetire());

        panel.addGroupComponent(cbSplit); 
        panel.addGroupComponent(cbCopy);
        panel.addGroupComponent(cbMerge);
        panel.addGroupComponent(cbDualEdits);
        panel.addGroupComponent(cbRetire);

        panel.addVerticalPadding();

        cbSplit.addActionListener(e -> {
            NCIEditTabPreferences.setFnSplit(cbSplit.isSelected());
        });
        cbCopy.addActionListener(e -> {
        	NCIEditTabPreferences.setFnCopy(cbCopy.isSelected());
        });
        cbMerge.addActionListener(e -> {
        	NCIEditTabPreferences.setFnMerge(cbMerge.isSelected());
        });
        cbDualEdits.addActionListener(e -> {
            NCIEditTabPreferences.setFnDualEdits(cbDualEdits.isSelected());
        });
        cbRetire.addActionListener(e -> {
            NCIEditTabPreferences.setFnRetire(cbRetire.isSelected());
        });
        
        if (getEditorKit() == null) {
        	this.add(panel);
        	return;
        }
        
        setupPropertyList();
        
      //  panel.addGroup("Server Configuration");
        JPanel immutPanel =new JPanel();
        immutPanel.setLayout(new GridBagLayout());
        immutPanel.setPreferredSize(new Dimension(400, 250));
        //p1.setPreferredSize(new Dimension(600, 100));
        //p1.setPreferredSize(new Dimension(400, 500));
        //Insets insets = new Insets(2, 2, 2, 2);
        JScrollPane immutablepropScrollpane = new JScrollPane(immutablepropList);
        immutablepropScrollpane.setPreferredSize(new Dimension(400, 250));
        //immutablepropScrollpane.setPreferredSize(new Dimension(600, 100));
        immutablepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        JLabel immutablepropLbl = new JLabel("Immutable Properties");
        //p1.add(immutablepropLbl, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 2), 0, 0));
        //p1.add("Immutable Properties", immutablepropScrollpane);
        immutPanel.add(immutablepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel complexPanel =new JPanel();
        complexPanel.setLayout(new GridBagLayout());
        complexPanel.setPreferredSize(new Dimension(400, 250));
        //p2.setPreferredSize(new Dimension(600, 100));
        //p2.setPreferredSize(new Dimension(400, 500));
        JScrollPane complexpropScrollpane = new JScrollPane(complexpropList);
        complexpropScrollpane.setPreferredSize(new Dimension(400, 250));
        //complexpropScrollpane.setPreferredSize(new Dimension(600, 100));
        complexpropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        //JLabel complexpropLbl = new JLabel("Complex Properties");
        //GridBagConstraints c = new GridBagConstraints();
        complexPanel.add(complexpropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel retirePanel =new JPanel();
        retirePanel.setLayout(new GridBagLayout());
        retirePanel.setPreferredSize(new Dimension(400, 250));
        /*JScrollPane retirepropScrollpane = new JScrollPane(retirepropList);
        retirepropScrollpane.setPreferredSize(new Dimension(400, 140));
        retirepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        retirePanel.add(retirepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        */
        JLabel retireCptRoot = new JLabel("Retired Concept Root");
        retireCptRoot.setPreferredSize(new Dimension(100, 35));
        JLabel retiredesignnote = new JLabel("Design Note");
        retiredesignnote.setPreferredSize(new Dimension(100, 35));
        JLabel retireeditornote = new JLabel("Editor Note");
        retireeditornote.setPreferredSize(new Dimension(100, 35));
        JLabel retireCptStatus = new JLabel("Deprecated Concept Status");
        retireCptStatus.setPreferredSize(new Dimension(100, 35));
        JLabel retireParent = new JLabel("Deprecated Parent");
        retireParent.setPreferredSize(new Dimension(100, 35));
        JLabel retireChild = new JLabel("Deprecated Child");
        retireChild.setPreferredSize(new Dimension(100, 35));
        JLabel retireRole = new JLabel("Deprecated Role");
        retireRole.setPreferredSize(new Dimension(100, 35));
        JLabel retireInRole = new JLabel("Deprecated In Role");
        retireInRole.setPreferredSize(new Dimension(100, 35));
        JLabel retireAssoc = new JLabel("Deprecated Association");
        retireAssoc.setPreferredSize(new Dimension(100, 35));
        JLabel retireInAssoc = new JLabel("Deprecated In Association");
        retireInAssoc.setPreferredSize(new Dimension(100, 35));
        
        /*retireCptRootComboBox = new OwlEntityComboBox(editorKit);
        retireCptRootComboBox.addItemListener(retireCptRootItemListener);
        retireCptRootComboBox.addItems(getProperties());
        retireCptRootComboBox.setPreferredSize(new Dimension(460, 35));
        rcrComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireCptRootComboBox);*/
        retireCptRootTxtfld =  new JTextField();
        retireCptRootTxtfld.setPreferredSize(new Dimension(370, 35));
        retireCptRootTxtfld.setEditable(false);
        retireCptRootTxtfld.setBackground(Color.WHITE);
        retireCptRootTxtfld.setText(NCIEditTabPreferences.getRetireConceptRoot());
        
        retireCptRootSearchBtn = new JButton("Search");
        retireCptRootSearchBtn.setPreferredSize(new Dimension(55, 35));
        
        retireCptRootSearchBtn.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
            	Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		        if(focusOwner == null) {
		            return;
		        }
		        
		        retireCptRootClass = NCIEditTab.currentTab().getOWLEditorKit().getWorkspace().searchForClass(focusOwner);
		        if (retireCptRootClass != null) {
		        	String retireCptRootStr = retireCptRootClass.getIRI().getRemainder().get();
		        	retireCptRootTxtfld.setText(retireCptRootStr);
		        	NCIEditTabPreferences.setRetireConceptRoot(retireCptRootStr);
		        }	
            }
        });
        
        retireDesignNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireDesignNoteComboBox.addItemListener(retireDesignNoteItemListener);
        //retireDesignNoteComboBox.addItems(getProperties());
        retireDesignNoteComboBox.setPreferredSize(new Dimension(440, 35));
        retireDesignNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireDesignNote()));
        //retireDesignNoteComboBox.getModel().setSelectedItem(NCIEditTabPreferences.getRetireDesignNote());
        
        //rdnComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireDesignNoteComboBox);
        
        retireEditorNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireEditorNoteComboBox.addItemListener(retireEditorNoteItemListener);
        //retireEditorNoteComboBox.addItems();
        retireEditorNoteComboBox.setPreferredSize(new Dimension(440, 35));
        //retireEditorNoteComboBox.setSelectedItem(NCIEditTabPreferences.getRetireEditorNote());
        retireEditorNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireEditorNote()));
        //renComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireEditorNoteComboBox);
        
        retireCptStatusComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireCptStatusComboBox.addItemListener(retireCptStatusItemListener);
        //retireCptStatusComboBox.addItems(getProperties());
        retireCptStatusComboBox.setPreferredSize(new Dimension(440, 35));
        retireCptStatusComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireConceptStatus()));
        //rcsComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireCptStatusComboBox);
        
        retireParentComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireParentComboBox.addItemListener(retireParentItemListener);
        //retireParentComboBox.addItems(getProperties());
        retireParentComboBox.setPreferredSize(new Dimension(440, 35));
        retireParentComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireParent()));
        //rpComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireParentComboBox);
        
        retireChildComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireChildComboBox.addItemListener(retireChildItemListener);
        //retireChildComboBox.addItems(getProperties());
        retireChildComboBox.setPreferredSize(new Dimension(440, 35));
        retireChildComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireChild()));
        //rcComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireChildComboBox);
        
        retireRoleComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireRoleComboBox.addItemListener(retireRoleItemListener);
        //retireRoleComboBox.addItems(getProperties());
        retireRoleComboBox.setPreferredSize(new Dimension(440, 35));
        retireRoleComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireRole()));
        //rrComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireRoleComboBox);
        
        retireInRoleComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireInRoleComboBox.addItemListener(retireInRoleItemListener);
        //retireInRoleComboBox.addItems(getProperties());
        retireInRoleComboBox.setPreferredSize(new Dimension(440, 35));
        retireInRoleComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireInRole()));
        
        retireAssocComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireAssocComboBox.addItemListener(retireAssocItemListener);
        //retireAssocComboBox.addItems(getProperties());
        retireAssocComboBox.setPreferredSize(new Dimension(440, 35));
        retireAssocComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireAssoc()));
        
        retireInAssocComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireInAssocComboBox.addItemListener(retireInAssocItemListener);
        //retireInAssocComboBox.addItems(getProperties());
        retireInAssocComboBox.setPreferredSize(new Dimension(440, 35));
        retireInAssocComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireInAssoc()));
        //raComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireAssocComboBox);
        
        retirePanel.add(retireCptRoot, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        //mergePanel.add(mergeSourceScrollpane, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 40));
        //retirePanel.add(retireCptRootComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        retirePanel.add(retireCptRootTxtfld, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 8), 0, 0));
        retirePanel.add(retireCptRootSearchBtn, new GridBagConstraints(2, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 8, 2, 0), 0, 0));
        
        retirePanel.add(retiredesignnote, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireDesignNoteComboBox, new GridBagConstraints(1, 1, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        retirePanel.add(retireeditornote, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireEditorNoteComboBox, new GridBagConstraints(1, 2, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        retirePanel.add(retireCptStatus, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireCptStatusComboBox, new GridBagConstraints(1, 3, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireParent, new GridBagConstraints(0, 4, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireParentComboBox, new GridBagConstraints(1, 4, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireChild, new GridBagConstraints(0, 5, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireChildComboBox, new GridBagConstraints(1, 5, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireRole, new GridBagConstraints(0, 6, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireRoleComboBox, new GridBagConstraints(1, 6, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireInRole, new GridBagConstraints(0, 7, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireInRoleComboBox, new GridBagConstraints(1, 7, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireAssoc, new GridBagConstraints(0, 8, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireAssocComboBox, new GridBagConstraints(1, 8, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        retirePanel.add(retireInAssoc, new GridBagConstraints(0, 9, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireInAssocComboBox, new GridBagConstraints(1, 9, 2, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        JPanel mergePanel =new JPanel();
        mergePanel.setLayout(new GridBagLayout());
        mergePanel.setPreferredSize(new Dimension(400, 140));
        JLabel mergesrc = new JLabel("Source");
        mergesrc.setPreferredSize(new Dimension(40, 35));
        JLabel mergetgt = new JLabel("Target");
        mergetgt.setPreferredSize(new Dimension(40, 35));
        JLabel mergedesignnote = new JLabel("Design Note");
        mergedesignnote.setPreferredSize(new Dimension(40, 35));
        JLabel mergeeditornote = new JLabel("Editor Note");
        mergeeditornote.setPreferredSize(new Dimension(40, 35));
      
        mergeSourceComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeSourceComboBox.addItemListener(mergeSourceItemListener);
        //mergeSourceComboBox.addItems(getProperties());
        mergeSourceComboBox.setPreferredSize(new Dimension(500, 35));
        mergeSourceComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeSource()));
        //msComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeSourceComboBox);
        
        mergeTargetComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeTargetComboBox.addItemListener(mergeTargetItemListener);
        //mergeTargetComboBox.addItems(getProperties());
        mergeTargetComboBox.setPreferredSize(new Dimension(500, 35));
        mergeTargetComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeTarget()));
        //mtComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeTargetComboBox);
        
        mergeDesignNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeDesignNoteComboBox.addItemListener(mergeDNItemListener);
        //mergeDesignNoteComboBox.addItems(getProperties());
        mergeDesignNoteComboBox.setPreferredSize(new Dimension(500, 35));
        mergeDesignNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeDesignNote()));
        //mdnComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeDesignNoteComboBox);
        
        mergeEditorNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeEditorNoteComboBox.addItemListener(mergeENItemListener);
        //mergeEditorNoteComboBox.addItems(getProperties());
        mergeEditorNoteComboBox.setPreferredSize(new Dimension(500, 35));
        mergeEditorNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeEditorNote()));
        //menComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeEditorNoteComboBox);
        
        mergePanel.add(mergesrc, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        //mergePanel.add(mergeSourceScrollpane, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 40));
        mergePanel.add(mergeSourceComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        mergePanel.add(mergetgt, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeTargetComboBox, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergedesignnote, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeDesignNoteComboBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeeditornote, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        mergePanel.add(mergeEditorNoteComboBox, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        JPanel splitPanel =new JPanel();
        splitPanel.setLayout(new FlowLayout());
        //splitPanel.setLayout(new BorderLayout());
        //splitPanel.setLayout(new GridBagLayout());
        splitPanel.setPreferredSize(new Dimension(400, 40));
        JLabel splitfrom = new JLabel("Split from");
        splitfrom.setPreferredSize(new Dimension(150, 40));
        
        splitFromComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        splitFromComboBox.addItemListener(splitFromItemListener);
        //splitFromComboBox.addItems(getProperties());
        splitFromComboBox.setPreferredSize(new Dimension(500, 40));
        splitFromComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getSplitFrom()));
        //sfComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(splitFromComboBox);
        
        //splitPanel.add(splitfrom, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        //splitPanel.add(splitFromComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        splitPanel.add(splitfrom);
        splitPanel.add(splitFromComboBox);
        //splitPanel.add(splitfrom, BorderLayout.WEST);
        //splitPanel.add(splitFromComboBox, BorderLayout.EAST);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(400, 250));
        /*tabbedPane.addTab("Immutable", immutPanel);
        tabbedPane.addTab("Complex", complexPanel);
        tabbedPane.addTab("Retire", retirePanel);
        tabbedPane.addTab("Merge", mergePanel);
        tabbedPane.addTab("Split", splitPanel);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);*/
        tabbedPane.add(new JScrollPane(immutPanel), "Immutable");
        tabbedPane.add(new JScrollPane(complexPanel), "Complex");
        tabbedPane.add(new JScrollPane(retirePanel), "Retire");
        tabbedPane.add(new JScrollPane(mergePanel), "Merge");
        tabbedPane.add(new JScrollPane(splitPanel), "Split");
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setMinimumSize(new Dimension(400, 285));
        //panel2.setPreferredSize(new Dimension(600, 100));
        //panel2.setPreferredSize(new Dimension(410, 510));
        panel2.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        //panel2.add(new JSeparator(SwingConstants.VERTICAL));
        
        userSelectedFilePathTxtfld =  new JTextField(28);
        //userSelectedFilePathTxtfld.setPreferredSize(new Dimension(310, 35));
        //userSelectedFilePathTxtfld.setEditable(false);
        userSelectedFilePathTxtfld.setBackground(Color.WHITE);
        
        exportBtn = new JButton("Export");
        exportBtn.setPreferredSize(new Dimension(95, 35));
        
        exportBtn.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
            	selectedFile = UIUtil.saveFile(NCIEditTabPreferencesPanel.this.getRootPane(), "Specify export json file", "JSON file", Collections.singleton("json"), "export.json");
                if(selectedFile != null) {        	
                	userSelectedFilePathTxtfld.setText(selectedFile.getAbsolutePath() /*+ selectedFile.getName()*/);
                }
            }
        });
        
        JPanel exportPanel = new JPanel();
        exportPanel.setLayout(new FlowLayout());
        exportPanel.setPreferredSize(new Dimension(400, 35));
        exportPanel.add(userSelectedFilePathTxtfld);
        exportPanel.add(exportBtn);
        panel2.add(exportPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        //panel2.add(userSelectedFilePathTxtfld, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        //panel2.add(exportBtn, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
               
        JSplitPane splitPane = new JSplitPane(SwingConstants.HORIZONTAL, panel, panel2); 
        this.add(splitPane);
    }
    
    public OWLEditorKit getEditorKit() {
    	if (NCIEditTab.currentTab() != null && editorKit == null) {
    		editorKit = NCIEditTab.currentTab().getOWLEditorKit();
    	}
    	return editorKit;
    }
    
    private OWLEntity getProps (String prop) {
    	OWLEntity propResult = null;
    	List<OWLEntity> propList = getProperties();
    	for (OWLEntity ent : propList) {
    		if (ent.getIRI().getRemainder().get().equals(prop)) {
    			propResult = ent;
    		}
    	}
    	return propResult;
    }
    
    private void setupPropertyList() {
    	setupImmutablePropertyList();
    	setupComplexPropertyList();
    	//setupRetirePropertyList();
    	//setupMergePropertyList();
    	//setupSplitPropertyList();
    }

    protected List<OWLEntity> getProperties() {
    	if (properties.isEmpty()) {
    		properties = LuceneUiUtils.getProperties(getEditorKit());
    	}
        return properties;
    }
    
    private void setupImmutablePropertyList() {
    	immutablepropList = new MList() {
    		protected void handleAdd() {
    			addImmutableProperty();
    		}

    		protected void handleDelete() {
    			deleteImmutableProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));

    			/*if (o instanceof MListItem) {      
    				//if(!axiomAnnotationButton.getName().equals("Annotations")) {
    					axiomAnnotationButton.setAnnotationPresent(true);
    					buttons.add(axiomAnnotationButton); 
    				//}
    			}*/

    			return buttons;
    		}
        };
        
        immutablepropList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        immutablepropList.addListSelectionListener(immutablepropListSelectionListener);
        immutablepropList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(getEditorKit(), immutDependentAnnotations));
        immutablepropList.addKeyListener(keyAdapter);
        immutablepropList.addMouseListener(mouseAdapter);
        immutablepropList.setVisibleRowCount(5);
        immutablepropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        Vector propertylabel = new Vector();
        List<OWLEntity> data = new ArrayList<OWLEntity>();
        propertylabel.add(new OwlPropertyListHeaderItem());
        List<String> imPropList = NCIEditTabPreferences.getImmutPropList();
        for (String str : imPropList) {
        	data.add(getProps(str));
        }
        this.immutablepropList.setListData(propertylabel);
        this.addEntitiesToList(data, immutablepropList);
        immutAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeImmutAxiomAnnotationHandler());
    }

    private void setupComplexPropertyList() {
    	complexpropList = new MList() {
    		protected void handleAdd() {
    			addComplexProperty();
    		}

    		protected void handleDelete() {
    			deleteComplexProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));

    			if (o instanceof MListItem) {        		
    				complexAxiomAnnotationButton.setAnnotationPresent(true);
    				buttons.add(complexAxiomAnnotationButton); 
    			}

    			return buttons;
    		}
        };
        
        complexpropList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        complexpropList.addListSelectionListener(complexpropListSelectionListener);
        complexpropList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(getEditorKit(), complexDependentAnnotations));
        complexpropList.addKeyListener(keyAdapter);
        complexpropList.addMouseListener(mouseAdapter);
        complexpropList.setVisibleRowCount(5);
        complexpropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        Vector propertylabel = new Vector();
        List<OWLEntity> data = new ArrayList<OWLEntity>();
        propertylabel.add(new OwlPropertyListHeaderItem());
        List<String> cPropList = NCIEditTabPreferences.getComplexPropList();
        List<String> cPropAnnotList = new ArrayList<String>();
        OWLEntity compProp;
        //List<OWLEntity> dataAnnot = new ArrayList<OWLEntity>();
        for (String str : cPropList) {
        	cPropAnnotList.clear();
        	//dataAnnot.clear();
        	List<OWLEntity> dataAnnot = new ArrayList<OWLEntity>();
        	compProp = getProps(str);
        	data.add(compProp);
        	cPropAnnotList = NCIEditTabPreferences.getComplexPropAnnotationList(str);
        	for (String annot : cPropAnnotList) {
        		dataAnnot.add(getProps(annot));
        	}
        	complexDependentAnnotations.put(compProp, dataAnnot);
            //addEntitiesToList(new ArrayList<OWLEntity>(), complexpropList);
        	
        }
        complexpropList.setListData(propertylabel);
        this.addEntitiesToList(data, complexpropList);
        complexAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeComplexAxiomAnnotationHandler());
    }

    private ListSelectionListener immutablepropListSelectionListener = e -> {
        if(immutablepropList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(immutablepropList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedImmutablePropertyListItem = (OwlEntityListItem) immutablepropList.getSelectedValue();
            }
        }
    };

    private ListSelectionListener complexpropListSelectionListener = e -> {
        if(complexpropList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(complexpropList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedComplexPropertyListItem = (OwlEntityListItem) complexpropList.getSelectedValue();
            }
        }
    };

    private ItemListener retireDesignNoteItemListener = new ItemListener() {
    	@Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireDesignNoteComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireDesignNote(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireEditorNoteItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireEditorNoteComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireEditorNote(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireCptStatusItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireCptStatusComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireConceptStatus(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireParentItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireParentComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireParent(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireChildItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireChildComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireChild(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireRoleItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireRoleComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireRole(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireInRoleItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireInRoleComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireInRole(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireAssocItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireAssocComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireAssoc(obj.toString());
                }
            }
        }
    };
    
    private ItemListener retireInAssocItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireInAssocComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setRetireInAssoc(obj.toString());
                }
            }
        }
    };
    
    private ItemListener mergeSourceItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)mergeSourceComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setMergeSource(obj.toString());
                }
            }
        }
    };
    
    private ItemListener mergeTargetItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)mergeTargetComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setMergeTarget(obj.toString());
                }
            }
        }
    };
    
    private ItemListener mergeDNItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)mergeDesignNoteComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setMergeDesignNote(obj.toString());
                }
            }
        }
    };
    
    private ItemListener mergeENItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)mergeEditorNoteComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setMergeEditorNote(obj.toString());
                }
            }
        }
    };
    
    private ItemListener splitFromItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)splitFromComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setSplitFrom(obj.toString());
                }
            }
        }
    };
    
    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                if(e.getSource().equals(immutablepropList) && immutablepropList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addImmutableProperty();
                } else if(e.getSource().equals(complexpropList) && complexpropList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addComplexProperty();
                } 
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if(e.getSource().equals(immutablepropList) && immutablepropList.getSelectedValue() instanceof OwlEntityListItem) {
                    deleteImmutableProperty();
                } else if(e.getSource().equals(complexpropList) && complexpropList.getSelectedValue() instanceof OwlEntityListItem) {
                    deleteComplexProperty();
                } 
            }
        }
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                if(e.getSource().equals(immutablepropList) && immutablepropList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addImmutableProperty();
                } else if(e.getSource().equals(complexpropList) && complexpropList.getSelectedValue() instanceof OwlPropertyListHeaderItem) {
                    addComplexProperty();
                } 
            }
        }
    };
    
    private void addImmutableProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(getEditorKit(), getEntities(immutablepropList),
        		immutDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, immutablepropList));
    } 
    
    private void deleteImmutableProperty() {
        List items = getListItems(immutablepropList);
        items.remove(selectedImmutablePropertyListItem);
        immutablepropList.setListData(items.toArray());
    }
    
    private void addComplexProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(getEditorKit(), getEntities(complexpropList),
        		complexDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, complexpropList));
    } 
    
    private void deleteComplexProperty() {
        List items = getListItems(complexpropList);
        items.remove(selectedComplexPropertyListItem);
        complexpropList.setListData(items.toArray());
    }
    
    private List<OWLEntity> getEntities(JList list) {
        List<OWLEntity> entities = new ArrayList<>();
        for(Object obj : getListItems(list)) {
            if(obj instanceof OwlEntityListItem) {
                entities.add(((OwlEntityListItem)obj).getEntity());
            }
        }
        return entities;
    }

    private List<?> getListItems(JList list) {
        List<Object> properties = new ArrayList<>();
        ListModel model = list.getModel();
        for(int i = 0; i < model.getSize(); i++) {
            properties.add(model.getElementAt(i));
        }
        return properties;
    }
    
    private void invokeImmutAxiomAnnotationHandler() {
        Object obj = immutablepropList.getSelectedValue();
        if (obj instanceof OwlEntityListItem)
        	addImmutDepProps(((OwlEntityListItem)obj).getEntity());
        
    }
    
    private void invokeComplexAxiomAnnotationHandler() {
        Object obj = complexpropList.getSelectedValue();
        if (obj instanceof OwlEntityListItem)
        	addComplexDepProps(((OwlEntityListItem)obj).getEntity());
        
    }
    
    /*private void invokeRetireAxiomAnnotationHandler() {
        Object obj = retirepropList.getSelectedValue();
        if (obj instanceof OwlEntityListItem)
        	addRetireDepProps(((OwlEntityListItem)obj).getEntity());
        
    }*/
    
    private void addImmutDepProps(OWLEntity ent) {
    	AddPropertiesToPreferencesPanel.showDialog(getEditorKit(), getEntities(immutablepropList),
        		immutDependentAnnotations, ent).ifPresent(owlEntities -> addImmutDepProps(owlEntities, ent));
    }

    private void addComplexDepProps(OWLEntity ent) {
    	AddPropertiesToPreferencesPanel.showDialog(getEditorKit(), getEntities(complexpropList),
        		complexDependentAnnotations, ent).ifPresent(owlEntities -> addComplexDepProps(owlEntities, ent));
    }
    
    private void addImmutDepProps(List<OWLEntity> entities, OWLEntity ent) {
        immutDependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), immutablepropList);
        //NCIEditTabPreferences.setImmutProps(immutablepropList.);
    }
    
    private void addComplexDepProps(List<OWLEntity> entities, OWLEntity ent) {
        complexDependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), complexpropList);

    }
    
    private void addEntitiesToList(List<OWLEntity> entities, JList list) {
        List items = getListItems(list);
        items.addAll(entities.stream().map(OwlEntityListItem::new).collect(Collectors.toList()));
        list.setListData(items.toArray());

    }

    public OWLClass getRetireCptRootClass() {
    	return retireCptRootClass;
    }
    
    public OWLProperty getRetireDNProperty() {
    	return (OWLProperty) retireDesignNoteComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireENProperty() {
        return (OWLProperty) retireEditorNoteComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireCptStatusProperty() {
        return (OWLProperty) retireCptStatusComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireParentProperty() {
        return (OWLProperty) retireParentComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireChildProperty() {
        return (OWLProperty) retireChildComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireRoleProperty() {
        return (OWLProperty) retireRoleComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireInRoleProperty() {
        return (OWLProperty) retireInRoleComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireAssocProperty() {
        return (OWLProperty) retireAssocComboBox.getSelectedItem();
    }
    
    public OWLProperty getRetireInAssocProperty() {
        return (OWLProperty) retireInAssocComboBox.getSelectedItem();
    }
    
    public OWLProperty getMergeSourceProperty() {
        return (OWLProperty) mergeSourceComboBox.getSelectedItem();
    }
    
    public OWLProperty getMergeTargetProperty() {
        return (OWLProperty) mergeTargetComboBox.getSelectedItem();
    }
    
    public OWLProperty getMergeDNProperty() {
        return (OWLProperty) mergeDesignNoteComboBox.getSelectedItem();
    }
    
    public OWLProperty getMergeENProperty() {
        return (OWLProperty) mergeEditorNoteComboBox.getSelectedItem();
    }
    
    public OWLProperty getSplitFromProperty() {
        return (OWLProperty) splitFromComboBox.getSelectedItem();
    }

    @Override
    public void dispose() throws Exception {
        // NO-OP
    }

    @Override
    public void applyChanges() {
    	NCIEditTabPreferences.setFnSplit(cbSplit.isSelected());
    	NCIEditTabPreferences.setFnCopy(cbCopy.isSelected());
    	NCIEditTabPreferences.setFnMerge(cbMerge.isSelected());
    	NCIEditTabPreferences.setFnDualEdits(cbDualEdits.isSelected());
    	NCIEditTabPreferences.setFnRetire(cbRetire.isSelected());
    	
    	NCIEditTab.currentTab().fireChange(new PreferencesChangeEvent(NCIEditTab.currentTab(), 
				ComplexEditType.PREFMODIFY));
    	
    	SaveProjectOptions();
    }
    
    public void SaveProjectOptions() {
    	//ProjectOptionsBuilder poBuilder = new ProjectOptionsBuilder();
    	LoadProjectOptions();
    	//String defaultStr = "OptionsConfig";
    	//ProjectId pid = new ProjectIdImpl (defaultStr);
    	//Project proj = ConfigurationManager.getFactory().getProject(pid, defaultStr, new NameImpl(defaultStr), new DescriptionImpl(defaultStr), new UserIdImpl(defaultStr), Optional.of(new ProjectOptionsImpl(projectOptions)));
    	//poBuilder.setProject(proj);
    	ProjectOptions projOptions = new ProjectOptionsImpl(projectOptions);
    	ProjectOptionsConfigManager.saveProjectOptionsFile(projOptions, userSelectedFilePathTxtfld.getText());
    }
    
    private void LoadProjectOptions() {
    	Set<String> retireCptRootValue = new TreeSet();
    	Set<String> designNoteValue = new TreeSet();
    	Set<String> editorNoteValue = new TreeSet();
    	Set<String> retireCptStatusValue = new TreeSet();
    	Set<String> retireParentValue = new TreeSet();
    	Set<String> retireChildValue = new TreeSet();
    	Set<String> retireRoleValue = new TreeSet();
    	Set<String> retireInRoleValue = new TreeSet();
    	Set<String> retireAssocValue = new TreeSet();
    	Set<String> retireInAssocValue = new TreeSet();
    	Set<String> mergeSourceValue = new TreeSet();
    	Set<String> mergeTargetValue = new TreeSet();
    	Set<String> splitFromValue = new TreeSet();
    	
    	if (getRetireCptRootClass() != null) {
	    	retireCptRootValue.add(getRetireCptRootClass().getIRI().getIRIString());
	    	this.projectOptions.put(NCIEditTabConstants.RETIRE_CONCEPTS_ROOT_NAME, retireCptRootValue);
	    	NCIEditTabPreferences.setRetireConceptRoot(getRetireCptRootClass().getIRI().getRemainder().get());
    	}
    	if (getRetireDNProperty() != null) {
    		designNoteValue.add(getRetireDNProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DESIGN_NOTE_NAME, designNoteValue);
    		NCIEditTabPreferences.setRetireDesignNote(getRetireDNProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireENProperty() != null) {
    		editorNoteValue.add(getRetireENProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.EDITOR_NOTE_NAME, editorNoteValue);
    		NCIEditTabPreferences.setRetireEditorNote(getRetireENProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireCptStatusProperty() != null) {
    		retireCptStatusValue.add(getRetireCptStatusProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_CONCEPT_STATUS_PROP_NAME, retireCptStatusValue);
    		NCIEditTabPreferences.setRetireConceptStatus(getRetireCptStatusProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireParentProperty() != null) {
    		retireParentValue.add(getRetireParentProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_PARENT_NAME, retireParentValue);
    		NCIEditTabPreferences.setRetireParent(getRetireParentProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireChildProperty() != null) {
    		retireChildValue.add(getRetireChildProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_CHILD_NAME, retireChildValue);
    		NCIEditTabPreferences.setRetireChild(getRetireChildProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireRoleProperty() != null) {
    		retireRoleValue.add(getRetireRoleProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_ROLE_NAME, retireRoleValue);
    		NCIEditTabPreferences.setRetireRole(getRetireRoleProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireInRoleProperty() != null) {
    		retireInRoleValue.add(getRetireInRoleProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_IN_ROLE_NAME, retireInRoleValue);
    		NCIEditTabPreferences.setRetireInRole(getRetireInRoleProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireAssocProperty() != null) {
    		retireAssocValue.add(getRetireAssocProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_ASSOC_NAME, retireAssocValue);
    		NCIEditTabPreferences.setRetireAssoc(getRetireAssocProperty().getIRI().getRemainder().get());
    	}
    	if (getRetireInAssocProperty() != null) {
    		retireInAssocValue.add(getRetireInAssocProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEPR_IN_ASSOC_NAME, retireInAssocValue);
    		NCIEditTabPreferences.setRetireInAssoc(getRetireInAssocProperty().getIRI().getRemainder().get());
    	}
    	if (getMergeSourceProperty() != null) {
    		mergeSourceValue.add(getMergeSourceProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.MERGE_SOURCE_NAME, mergeSourceValue);
    		NCIEditTabPreferences.setMergeSource(getMergeSourceProperty().getIRI().getRemainder().get());
    	}
    	if (getMergeTargetProperty() != null) {
    		mergeTargetValue.add(getMergeTargetProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.MERGE_TARGET_NAME, mergeTargetValue);
    		NCIEditTabPreferences.setMergeTarget(getMergeTargetProperty().getIRI().getRemainder().get());
    	}
    	if (getSplitFromProperty() != null) {
    		splitFromValue.add(getSplitFromProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.SPLIT_FROM_NAME, splitFromValue);
    		NCIEditTabPreferences.setSplitFrom(getSplitFromProperty().getIRI().getRemainder().get());
    	}
    	//Load immutable properties
    	ListModel immutListModel = null;
    	//int count = immutListModel.getSize();
    	OwlEntityListItem entity = null;
    	Set<String> immutPropSet = new TreeSet();	
    	List<String> immutPropList = new ArrayList<String>();
    	if (this.immutablepropList != null) {
    		immutListModel = this.immutablepropList.getModel();
	    	for (int i=0; i < immutListModel.getSize(); i++) {
	    		if (immutListModel.getElementAt(i) instanceof OwlEntityListItem) {
	    			entity = (OwlEntityListItem)immutListModel.getElementAt(i);
	    			immutPropSet.add(entity.getEntity().getIRI().getIRIString());
	    			immutPropList.add(entity.getEntity().getIRI().getRemainder().get());
	    		}
	    	}
	    	this.projectOptions.put(NCIEditTabConstants.IMMUTABLE_PROPS, immutPropSet);    	
	    	NCIEditTabPreferences.setImmutPropList(immutPropList);
    	}
    	//Load complex properties
    	ListModel compPropListModel = null;
    	OwlEntityListItem cpentity = null;
    	Set<String> complexPropSet = new TreeSet();
    	List<String> compPropList = new ArrayList<String>();
    	if (this.complexpropList != null) {
    		compPropListModel = this.complexpropList.getModel();
	    	for (int i=0; i < compPropListModel.getSize(); i++) {
	    		if (compPropListModel.getElementAt(i) instanceof OwlEntityListItem) {
	    			cpentity = (OwlEntityListItem)compPropListModel.getElementAt(i);
	    			complexPropSet.add(cpentity.getEntity().getIRI().getIRIString());
	    			compPropList.add(cpentity.getEntity().getIRI().getRemainder().get());
	    		}
	    	}
	    	this.projectOptions.put(NCIEditTabConstants.COMPLEX_PROPS, complexPropSet);
	    	NCIEditTabPreferences.setComplexPropList(compPropList);
    	}
    	//Load complex property dependent annotations
    	List<String> compPropAnnotList = new ArrayList<String>();
    	for (Entry<OWLEntity, List<OWLEntity>> entry: complexDependentAnnotations.entrySet()) {
    		OWLEntity comppropKey = entry.getKey();
    		List<OWLEntity> compAnnotationList = entry.getValue();
    		OWLEntity cpannotent = null;
    		Set<String> compDependentAnnotationSet = new TreeSet();
    		compPropAnnotList.clear();
    		for (int i=0; i<compAnnotationList.size(); i++) {
            	cpannotent = compAnnotationList.get(i);
            	compDependentAnnotationSet.add(cpannotent.getIRI().getIRIString());
            	compPropAnnotList.add(cpannotent.getIRI().getRemainder().get());
            }
            this.projectOptions.put(comppropKey.getIRI().getIRIString(), compDependentAnnotationSet);
            NCIEditTabPreferences.setComplexPropAnnotationList(comppropKey.getIRI().getRemainder().get(), compPropAnnotList);
    	}
    }
    /*private Project getProject() {
    	ClientSession clientSession = ClientSession.getInstance(editorKit);
    	LocalHttpClient lhc = (LocalHttpClient) clientSession.getActiveClient();
    	Project project = null;
    	if (lhc != null) {
			try {
				project = lhc.getCurrentConfig().getProject(clientSession.getActiveProject());
			} catch (UnknownProjectIdException e) {
				e.printStackTrace();
			}
		}
    	return project;
    }*/
   
    /**
     * Property list header item
     */
    public class OwlPropertyListHeaderItem implements MListSectionHeader {

        @Override
        public String getName() {
            return "Properties";
        }

        @Override
        public boolean canAdd() {
            return true;
        }
    }
    
    /**
     * OWLEntity list item
     */
    public class OwlEntityListItem implements MListItem {
        private OWLEntity entity;

        /**
         * Constructor
         *
         * @param entity OWL entity
         */
        public OwlEntityListItem(OWLEntity entity) {
            this.entity = checkNotNull(entity);
        }

        public OWLEntity getEntity() {
            return entity;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public void handleEdit() {
        }

        @Override
        public boolean isDeleteable() {
            return true;
        }

        @Override
        public boolean handleDelete() {
            return true;
        }

        @Override
        public String getTooltip() {
            //return entity.getIRI().toQuotedString();
        	return "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OwlEntityListItem)) {
                return false;
            }
            OwlEntityListItem that = (OwlEntityListItem) o;
            return Objects.equal(entity, that.entity);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(entity);
        }
    }
}
