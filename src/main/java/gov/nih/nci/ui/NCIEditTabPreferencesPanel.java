package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.AxiomAnnotationButton;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLProperty;

import com.google.common.base.Objects;

import edu.stanford.protege.csv.export.ui.UiUtils;
import edu.stanford.protege.metaproject.api.ProjectOptions;
import edu.stanford.protege.metaproject.impl.ProjectOptionsImpl;
import edu.stanford.protege.search.lucene.tab.ui.LuceneUiUtils;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.PreferencesChangeEvent;
import gov.nih.nci.utils.OwlEntityComboBox;
import gov.nih.nci.utils.ProjectOptionsConfigManager;

public class NCIEditTabPreferencesPanel extends OWLPreferencesPanel {
	//Add comments here
	private static final long serialVersionUID = 1L;
	
	private MList immutablepropList;
	private MList complexpropList;
	
	private AxiomAnnotationButton immutAxiomAnnotationButton;
	private AxiomAnnotationButton complexAxiomAnnotationButton;

	private Map<OWLEntity, List<OWLEntity>> immutDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	private Map<OWLEntity, List<OWLEntity>> complexDependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();

	private OwlEntityListItem selectedImmutablePropertyListItem;
	private OwlEntityListItem selectedComplexPropertyListItem;
	private OWLClass retireCptRootClass;

	private JTextField retireCptRootTxtfld;
	private JButton retireCptRootSearchBtn;
	private File selectedFile;
	private JLabel exportFilepathlbl;
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
	private OwlEntityComboBox codePropComboBox;
	private OwlEntityComboBox labelPropComboBox;
	private OwlEntityComboBox prefNameComboBox;
	private OwlEntityComboBox definitionComboBox;
	private OwlEntityComboBox fullyQualSynComboBox;
	private OwlEntityComboBox reviewDateComboBox;
	private OwlEntityComboBox reviewerNameComboBox;
	private OwlEntityComboBox defSourceComboBox;
	private OwlEntityComboBox synTypeComboBox;
	private OwlEntityComboBox synSourceComboBox;
	private OwlEntityComboBox semanticTypeComboBox;
	
	private OWLEditorKit editorKit = null;
	List<OWLEntity> properties = new ArrayList<OWLEntity>();
	private Map<String, Set<String>> projectOptions = new HashMap<>();

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
        
        panel.addGroup("ComplexEdit");
      
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
        JScrollPane immutablepropScrollpane = new JScrollPane(immutablepropList);
        immutablepropScrollpane.setPreferredSize(new Dimension(400, 250));
        immutablepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        JLabel immutablepropLbl = new JLabel("Immutable Properties");
        immutPanel.add(immutablepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel complexPanel =new JPanel();
        complexPanel.setLayout(new GridBagLayout());
        complexPanel.setPreferredSize(new Dimension(400, 250));
        JScrollPane complexpropScrollpane = new JScrollPane(complexpropList);
        complexpropScrollpane.setPreferredSize(new Dimension(400, 250));
        complexpropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        complexPanel.add(complexpropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel retirePanel =new JPanel();
        retirePanel.setLayout(new GridBagLayout());
        retirePanel.setPreferredSize(new Dimension(400, 250));
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
        retireDesignNoteComboBox.setPreferredSize(new Dimension(440, 35));
        retireDesignNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireDesignNote()));
       
        retireEditorNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireEditorNoteComboBox.addItemListener(retireEditorNoteItemListener);
        retireEditorNoteComboBox.setPreferredSize(new Dimension(440, 35));
        retireEditorNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireEditorNote()));
        
        retireCptStatusComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireCptStatusComboBox.addItemListener(retireCptStatusItemListener);
        retireCptStatusComboBox.setPreferredSize(new Dimension(440, 35));
        retireCptStatusComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireConceptStatus()));
        
        retireParentComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireParentComboBox.addItemListener(retireParentItemListener);
        retireParentComboBox.setPreferredSize(new Dimension(440, 35));
        retireParentComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireParent()));
        
        retireChildComboBox = new OwlEntityComboBox(getEditorKit(), getProperties());
        retireChildComboBox.addItemListener(retireChildItemListener);
        retireChildComboBox.setPreferredSize(new Dimension(440, 35));
        retireChildComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireChild()));
        
        retireRoleComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireRoleComboBox.addItemListener(retireRoleItemListener);
        retireRoleComboBox.setPreferredSize(new Dimension(440, 35));
        retireRoleComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireRole()));
        
        retireInRoleComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireInRoleComboBox.addItemListener(retireInRoleItemListener);
        retireInRoleComboBox.setPreferredSize(new Dimension(440, 35));
        retireInRoleComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireInRole()));
        
        retireAssocComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireAssocComboBox.addItemListener(retireAssocItemListener);
        retireAssocComboBox.setPreferredSize(new Dimension(440, 35));
        retireAssocComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireAssoc()));
        
        retireInAssocComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        retireInAssocComboBox.addItemListener(retireInAssocItemListener);
        retireInAssocComboBox.setPreferredSize(new Dimension(440, 35));
        retireInAssocComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getRetireInAssoc()));
        
        retirePanel.add(retireCptRoot, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
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
        mergeSourceComboBox.setPreferredSize(new Dimension(500, 35));
        mergeSourceComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeSource()));
        
        mergeTargetComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeTargetComboBox.addItemListener(mergeTargetItemListener);
        mergeTargetComboBox.setPreferredSize(new Dimension(500, 35));
        mergeTargetComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeTarget()));
        
        mergeDesignNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeDesignNoteComboBox.addItemListener(mergeDNItemListener);
        mergeDesignNoteComboBox.setPreferredSize(new Dimension(500, 35));
        mergeDesignNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeDesignNote()));
        
        mergeEditorNoteComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        mergeEditorNoteComboBox.addItemListener(mergeENItemListener);
        mergeEditorNoteComboBox.setPreferredSize(new Dimension(500, 35));
        mergeEditorNoteComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getMergeEditorNote()));
        
        mergePanel.add(mergesrc, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        mergePanel.add(mergeSourceComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        mergePanel.add(mergetgt, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeTargetComboBox, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergedesignnote, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeDesignNoteComboBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        mergePanel.add(mergeeditornote, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        mergePanel.add(mergeEditorNoteComboBox, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        JPanel splitPanel =new JPanel();
        splitPanel.setLayout(new FlowLayout());
        splitPanel.setPreferredSize(new Dimension(400, 40));
        JLabel splitfrom = new JLabel("Split from");
        splitfrom.setPreferredSize(new Dimension(150, 40));
        
        splitFromComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        splitFromComboBox.addItemListener(splitFromItemListener);
        splitFromComboBox.setPreferredSize(new Dimension(500, 40));
        splitFromComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getSplitFrom()));
        
        splitPanel.add(splitfrom);
        splitPanel.add(splitFromComboBox);
        
        /* JPanel codePanel =new JPanel();
        codePanel.setLayout(new FlowLayout());
        codePanel.setPreferredSize(new Dimension(400, 40));
        JLabel codeProp = new JLabel("Code Prop");
        codeProp.setPreferredSize(new Dimension(150, 120));
        
        codePropComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        codePropComboBox.addItemListener(codePropItemListener);
        codePropComboBox.setPreferredSize(new Dimension(500, 40));
        codePropComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getCodeProp()));
        
        codePanel.add(codeProp);
        codePanel.add(codePropComboBox);*/
        JPanel codePanel =new JPanel();
        codePanel.setLayout(new GridBagLayout());
        codePanel.setPreferredSize(new Dimension(400, 250));
        JLabel codeProp = new JLabel("Code Prop");
        codeProp.setPreferredSize(new Dimension(40, 35));
        JLabel labelProp = new JLabel("Label Prop");
        labelProp.setPreferredSize(new Dimension(40, 35));
        JLabel prefName = new JLabel("Pref Name");
        prefName.setPreferredSize(new Dimension(40, 35));
        JLabel definition = new JLabel("Definition");
        definition.setPreferredSize(new Dimension(40, 35));
        JLabel fullyQualSyn = new JLabel("Fully Qual Syn");
        fullyQualSyn.setPreferredSize(new Dimension(40, 35));
        JLabel reviewDate = new JLabel("Review Date");
        reviewDate.setPreferredSize(new Dimension(40, 35));
        JLabel reviewerName = new JLabel("Reviewer Name");
        reviewerName.setPreferredSize(new Dimension(40, 35));
        JLabel defSource = new JLabel("Def Source");
        defSource.setPreferredSize(new Dimension(40, 35));
        JLabel synType = new JLabel("Syn Type");
        synType.setPreferredSize(new Dimension(40, 35));
        JLabel synSource = new JLabel("Syn Source");
        synSource.setPreferredSize(new Dimension(40, 35));
        JLabel semanticType = new JLabel("Semantic Type");
        semanticType.setPreferredSize(new Dimension(40, 35));
        
        codePropComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        codePropComboBox.addItemListener(codePropItemListener);
        codePropComboBox.setPreferredSize(new Dimension(500, 40));
        codePropComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getCodeProp()));
        
        labelPropComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        labelPropComboBox.addItemListener(labelPropItemListener);
        labelPropComboBox.setPreferredSize(new Dimension(500, 35));
        labelPropComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getLabelProp()));
        
        prefNameComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        prefNameComboBox.addItemListener(prefNameItemListener);
        prefNameComboBox.setPreferredSize(new Dimension(500, 35));
        prefNameComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getPrefName()));
        
        definitionComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        definitionComboBox.addItemListener(definitionItemListener);
        definitionComboBox.setPreferredSize(new Dimension(500, 35));
        definitionComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getDefinition()));
        
        fullyQualSynComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        fullyQualSynComboBox.addItemListener(fullyQualSynItemListener);
        fullyQualSynComboBox.setPreferredSize(new Dimension(500, 35));
        fullyQualSynComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getFullyQualSyn()));
        
        reviewDateComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        reviewDateComboBox.addItemListener(reviewDateItemListener);
        reviewDateComboBox.setPreferredSize(new Dimension(500, 35));
        reviewDateComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getReviewDate()));
        
        reviewerNameComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        reviewerNameComboBox.addItemListener(reviewerNameItemListener);
        reviewerNameComboBox.setPreferredSize(new Dimension(500, 35));
        reviewerNameComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getReviewerName()));
        
        defSourceComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        defSourceComboBox.addItemListener(defSourceItemListener);
        defSourceComboBox.setPreferredSize(new Dimension(500, 35));
        defSourceComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getDefSource()));
        
        synTypeComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        synTypeComboBox.addItemListener(synTypeItemListener);
        synTypeComboBox.setPreferredSize(new Dimension(500, 35));
        synTypeComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getSynType()));
        
        synSourceComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        synSourceComboBox.addItemListener(synSourceItemListener);
        synSourceComboBox.setPreferredSize(new Dimension(500, 35));
        synSourceComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getSynSource()));
        
        semanticTypeComboBox = new OwlEntityComboBox(getEditorKit(), getProperties() );
        semanticTypeComboBox.addItemListener(semanticTypeItemListener);
        semanticTypeComboBox.setPreferredSize(new Dimension(500, 35));
        semanticTypeComboBox.setSelectedItem(getProps(NCIEditTabPreferences.getSemanticType()));
        
        codePanel.add(codeProp, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        codePanel.add(codePropComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        codePanel.add(labelProp, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(labelPropComboBox, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(prefName, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(prefNameComboBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(definition, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(definitionComboBox, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(fullyQualSyn, new GridBagConstraints(0, 4, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(fullyQualSynComboBox, new GridBagConstraints(1, 4, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(reviewDate, new GridBagConstraints(0, 5, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(reviewDateComboBox, new GridBagConstraints(1, 5, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(reviewerName, new GridBagConstraints(0, 6, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(reviewerNameComboBox, new GridBagConstraints(1, 6, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(defSource, new GridBagConstraints(0, 7, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(defSourceComboBox, new GridBagConstraints(1, 7, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(synType, new GridBagConstraints(0, 8, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(synTypeComboBox, new GridBagConstraints(1, 8, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(synSource, new GridBagConstraints(0, 9, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(synSourceComboBox, new GridBagConstraints(1, 9, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(semanticType, new GridBagConstraints(0, 10, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        codePanel.add(semanticTypeComboBox, new GridBagConstraints(1, 10, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(400, 250));
        tabbedPane.add(new JScrollPane(retirePanel), "Retire");
        tabbedPane.add(new JScrollPane(mergePanel), "Merge");
        tabbedPane.add(new JScrollPane(splitPanel), "Split");
        tabbedPane.add(new JScrollPane(codePanel), "Others");
        tabbedPane.add(new JScrollPane(immutPanel), "Immutable");
        tabbedPane.add(new JScrollPane(complexPanel), "Complex");
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setMinimumSize(new Dimension(400, 285));
        panel2.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        exportFilepathlbl = new JLabel();
        exportFilepathlbl.setText("File will be saved to working directory if not specified");
        exportFilepathlbl.setFont(new Font("Verdana", Font.PLAIN, 14));
        userSelectedFilePathTxtfld =  new JTextField(28);
        userSelectedFilePathTxtfld.setBackground(Color.WHITE);
        userSelectedFilePathTxtfld.setPreferredSize(new Dimension(300, 35));
        
        exportBtn = new JButton("Export");
        //exportBtn.setPreferredSize(new Dimension(100, 35));
        
        exportBtn.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
            	selectedFile = UIUtil.saveFile(NCIEditTabPreferencesPanel.this.getRootPane(), "Specify export json file", "JSON file", Collections.singleton("json"), "export.json");
                if(selectedFile != null) {        	
                	userSelectedFilePathTxtfld.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        
        JPanel exportPanel = new JPanel();
        exportPanel.setLayout(new GridBagLayout());
        exportPanel.setPreferredSize(new Dimension(400, 75));
        exportPanel.add(exportFilepathlbl, new GridBagConstraints(0, 0, 2, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        exportPanel.add(userSelectedFilePathTxtfld, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        exportPanel.add(exportBtn, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        panel2.add(exportPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        if (NCIEditTab.currentTab() != null && isModeler()) {
        	immutablepropList.setEnabled(false);
        	complexpropList.setEnabled(false);
        	userSelectedFilePathTxtfld.setEnabled(false);
        	exportBtn.setEnabled(false);
        	disableComponents(immutPanel);
        	disableComponents(complexPanel);
        	disableComponents(retirePanel);
        	disableComponents(mergePanel);
        	disableComponents(splitPanel);
        	disableComponents(codePanel);
        	tabbedPane.setEnabled(false);
        	panel2.setEnabled(false);
        }
        
        JSplitPane splitPane = new JSplitPane(SwingConstants.HORIZONTAL, panel, panel2); 
        this.add(splitPane);
    }
    
    private boolean isModeler() {
    	return NCIEditTab.currentTab().getWorkspace().isReadOnly(null);
    	//return false;
    }
    
    private void disableComponents(JComponent component) {
    	Component[] com = component.getComponents();
    	//Inside you action event where you want to disable everything
    	//Do the following
    	for (int a = 0; a < com.length; a++) {
    	     com[a].setEnabled(false);
    	}
    }
    
    public OWLEditorKit getEditorKit() {
    	if (editorKit != null) {
    		return editorKit;
    	}
    	if (NCIEditTab.currentTab() != null) {
    		editorKit = NCIEditTab.currentTab().getOWLEditorKit();
    	} else if (ProtegeManager.getInstance().getEditorKitManager().getEditorKitCount() > 0) {
    		editorKit = (OWLEditorKit)ProtegeManager.getInstance().getEditorKitManager().getEditorKits().get(0);
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
        for (String str : cPropList) {
        	cPropAnnotList.clear();
        	List<OWLEntity> dataAnnot = new ArrayList<OWLEntity>();
        	compProp = getProps(str);
        	data.add(compProp);
        	cPropAnnotList = NCIEditTabPreferences.getComplexPropAnnotationList(str);
        	for (String annot : cPropAnnotList) {
        		dataAnnot.add(getProps(annot));
        	}
        	complexDependentAnnotations.put(compProp, dataAnnot);
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
    
    private ItemListener codePropItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)codePropComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setCodeProp(obj.toString());
                }
            }
        }
    };
    
    private ItemListener labelPropItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)labelPropComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setLabelProp(obj.toString());
                }
            }
        }
    };
    
    private ItemListener prefNameItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)prefNameComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                    //NCIEditTabPreferences.setPrefName(obj.toString());
                }
            }
        }
    };
    
    private ItemListener definitionItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)definitionComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener fullyQualSynItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)fullyQualSynComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener reviewDateItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)reviewDateComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener reviewerNameItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)reviewerNameComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener defSourceItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)defSourceComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener synTypeItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)synTypeComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener synSourceItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)synSourceComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
            }
        }
    };
    
    private ItemListener semanticTypeItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)semanticTypeComboBox.getEditor().getEditorComponent()).setText(obj.toString());
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

    public OWLProperty getCodePropProperty() {
        return (OWLProperty) codePropComboBox.getSelectedItem();
    }
    
    public OWLProperty getLabelPropProperty() {
        return (OWLProperty) labelPropComboBox.getSelectedItem();
    }
    
    public OWLProperty getPrefNameProperty() {
        return (OWLProperty) prefNameComboBox.getSelectedItem();
    }
    
    public OWLProperty getDefinitionProperty() {
        return (OWLProperty) definitionComboBox.getSelectedItem();
    }
    
    public OWLProperty getFullyQualSynProperty() {
        return (OWLProperty) fullyQualSynComboBox.getSelectedItem();
    }
    
    public OWLProperty getReviewDateProperty() {
        return (OWLProperty) reviewDateComboBox.getSelectedItem();
    }
    
    public OWLProperty getReviewerNameProperty() {
        return (OWLProperty) reviewerNameComboBox.getSelectedItem();
    }
    
    public OWLProperty getDefSourceProperty() {
        return (OWLProperty) defSourceComboBox.getSelectedItem();
    }
    
    public OWLProperty getSynTypeProperty() {
        return (OWLProperty) synTypeComboBox.getSelectedItem();
    }
    
    public OWLProperty getSynSourceProperty() {
        return (OWLProperty) synSourceComboBox.getSelectedItem();
    }
    
    public OWLProperty getSemanticTypeProperty() {
        return (OWLProperty) semanticTypeComboBox.getSelectedItem();
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
    	LoadProjectOptions();
    	ProjectOptions projOptions = new ProjectOptionsImpl(projectOptions);
    	String filePath = userSelectedFilePathTxtfld.getText();
    	if (filePath == null || filePath.isEmpty()) {
    		filePath = "project-options.json";
    	}
    	ProjectOptionsConfigManager.saveProjectOptionsFile(projOptions, filePath);
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
    	Set<String> codePropValue = new TreeSet();
    	Set<String> labelPropValue = new TreeSet();
    	Set<String> prefNameValue = new TreeSet();
    	Set<String> definitionValue = new TreeSet();
    	Set<String> fullyQualSynValue = new TreeSet();
    	Set<String> reviewDateValue = new TreeSet();
    	Set<String> reviewerNameValue = new TreeSet();
    	Set<String> defSourceValue = new TreeSet();
    	Set<String> synTypeValue = new TreeSet();
    	Set<String> synSourceValue = new TreeSet();
    	Set<String> semanticTypeValue = new TreeSet();
    	
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
    	if (getCodePropProperty() != null) {
    		codePropValue.add(getCodePropProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.CODE_PROP_NAME, codePropValue);
    		NCIEditTabPreferences.setCodeProp(getCodePropProperty().getIRI().getRemainder().get());
    	}
    	if (getLabelPropProperty() != null) {
    		labelPropValue.add(getLabelPropProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.LABEL_PROP_NAME, labelPropValue);
    		NCIEditTabPreferences.setLabelProp(getLabelPropProperty().getIRI().getRemainder().get());
    	}
    	if (getPrefNameProperty() != null) {
    		prefNameValue.add(getPrefNameProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.PREF_NAME_NAME, prefNameValue);
    		NCIEditTabPreferences.setPrefName(getPrefNameProperty().getIRI().getRemainder().get());
    	}
    	if (getDefinitionProperty() != null) {
    		definitionValue.add(getDefinitionProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEFINITION_NAME, definitionValue);
    		NCIEditTabPreferences.setDefinition(getDefinitionProperty().getIRI().getRemainder().get());
    	}
    	if (getFullyQualSynProperty() != null) {
    		fullyQualSynValue.add(getFullyQualSynProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.FULLY_QUAL_SYN_NAME, fullyQualSynValue);
    		NCIEditTabPreferences.setFullyQualSyn(getFullyQualSynProperty().getIRI().getRemainder().get());
    	}
    	if (getReviewDateProperty() != null) {
    		reviewDateValue.add(getReviewDateProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.REVIEW_DATE_NAME, reviewDateValue);
    		NCIEditTabPreferences.setReviewDate(getReviewDateProperty().getIRI().getRemainder().get());
    	}
    	if (getReviewerNameProperty() != null) {
    		reviewerNameValue.add(getReviewerNameProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.REVIEWER_NAME_NAME, reviewerNameValue);
    		NCIEditTabPreferences.setReviewerName(getReviewerNameProperty().getIRI().getRemainder().get());
    	}
    	if (getDefSourceProperty() != null) {
    		defSourceValue.add(getDefSourceProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.DEF_SOURCE_NAME, defSourceValue);
    		NCIEditTabPreferences.setDefSource(getDefSourceProperty().getIRI().getRemainder().get());
    	}
    	if (getSynTypeProperty() != null) {
    		synTypeValue.add(getSynTypeProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.SYN_TYPE_NAME, synTypeValue);
    		NCIEditTabPreferences.setSynType(getSynTypeProperty().getIRI().getRemainder().get());
    	}
    	if (getSynSourceProperty() != null) {
    		synSourceValue.add(getSynSourceProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.SYN_SOURCE_NAME, synSourceValue);
    		NCIEditTabPreferences.setSynSource(getSynSourceProperty().getIRI().getRemainder().get());
    	}
    	if (getSemanticTypeProperty() != null) {
    		semanticTypeValue.add(getSemanticTypeProperty().getIRI().getIRIString());
    		this.projectOptions.put(NCIEditTabConstants.SEMANTIC_TYPE_NAME, semanticTypeValue);
    		NCIEditTabPreferences.setSemanticType(getSemanticTypeProperty().getIRI().getRemainder().get());
    	}
    	
    	//Load immutable properties
    	ListModel immutListModel = null;
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
