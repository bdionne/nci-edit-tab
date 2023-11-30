package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
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

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.AxiomAnnotationButton;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLProperty;

import com.google.common.base.Objects;

import edu.stanford.protege.csv.export.ui.UiUtils;
import edu.stanford.protege.search.lucene.tab.engine.QueryType;
import edu.stanford.protege.search.lucene.tab.ui.LuceneUiUtils;
import edu.stanford.protege.search.lucene.tab.ui.OwlEntityComboBox;
import edu.stanford.protege.search.lucene.tab.ui.OwlEntityComboBoxChangeHandler;
import edu.stanford.protege.search.lucene.tab.ui.TabPreferences;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.PreferencesChangeEvent;

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
	
	private OwlEntityComboBox retireCptRootComboBox;
	private OwlEntityComboBox retireDesignNoteComboBox;
	private OwlEntityComboBox retireEditorNoteComboBox;
	private OwlEntityComboBox retireCptStatusComboBox;
	private OwlEntityComboBox retireParentComboBox;
	private OwlEntityComboBox retireChildComboBox;
	private OwlEntityComboBox retireRoleComboBox;
	private OwlEntityComboBox retireAssocComboBox;
	
	private OwlEntityComboBox mergeSourceComboBox;
	private OwlEntityComboBox mergeTargetComboBox;
	private OwlEntityComboBox mergeDesignNoteComboBox;
	private OwlEntityComboBox mergeEditorNoteComboBox;
	private OwlEntityComboBox splitFromComboBox;
	
	private OwlEntityComboBoxChangeHandler rcrComboBoxChangeHandler;
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
	private OwlEntityComboBoxChangeHandler sfComboBoxChangeHandler;
	
	private OWLEditorKit editorKit = NCIEditTab.currentTab().getOWLEditorKit();

    //private JLabel complexEditLabel = new JLabel("Complex Edit");

    JCheckBox cbSplit = new JCheckBox("Split");
    JCheckBox cbCopy = new JCheckBox("Copy");
    JCheckBox cbMerge = new JCheckBox("Merge");
    JCheckBox cbDualEdits = new JCheckBox("Dual Edits");
    JCheckBox cbRetire = new JCheckBox("Retire");

    @Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        setupPropertyList();
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
        
        //add server configuration
        
      //  panel.addGroup("Server Configuration");
        JPanel immutPanel =new JPanel();
        immutPanel.setLayout(new GridBagLayout());
        immutPanel.setPreferredSize(new Dimension(400, 200));
        //p1.setPreferredSize(new Dimension(600, 100));
        //p1.setPreferredSize(new Dimension(400, 500));
        //Insets insets = new Insets(2, 2, 2, 2);
        JScrollPane immutablepropScrollpane = new JScrollPane(immutablepropList);
        immutablepropScrollpane.setPreferredSize(new Dimension(400, 140));
        //immutablepropScrollpane.setPreferredSize(new Dimension(600, 100));
        immutablepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        JLabel immutablepropLbl = new JLabel("Immutable Properties");
        //p1.add(immutablepropLbl, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 2), 0, 0));
        //p1.add("Immutable Properties", immutablepropScrollpane);
        immutPanel.add(immutablepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel complexPanel =new JPanel();
        complexPanel.setLayout(new GridBagLayout());
        complexPanel.setPreferredSize(new Dimension(400, 200));
        //p2.setPreferredSize(new Dimension(600, 100));
        //p2.setPreferredSize(new Dimension(400, 500));
        JScrollPane complexpropScrollpane = new JScrollPane(complexpropList);
        complexpropScrollpane.setPreferredSize(new Dimension(400, 140));
        //complexpropScrollpane.setPreferredSize(new Dimension(600, 100));
        complexpropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        //JLabel complexpropLbl = new JLabel("Complex Properties");
        //GridBagConstraints c = new GridBagConstraints();
        complexPanel.add(complexpropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel retirePanel =new JPanel();
        retirePanel.setLayout(new GridBagLayout());
        retirePanel.setPreferredSize(new Dimension(400, 200));
        /*JScrollPane retirepropScrollpane = new JScrollPane(retirepropList);
        retirepropScrollpane.setPreferredSize(new Dimension(400, 140));
        retirepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        retirePanel.add(retirepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        */
        JLabel retireCptRoot = new JLabel("Retired Concept Root");
        retireCptRoot.setPreferredSize(new Dimension(80, 35));
        JLabel retiredesignnote = new JLabel("Design Note");
        retiredesignnote.setPreferredSize(new Dimension(80, 35));
        JLabel retireeditornote = new JLabel("Editor Note");
        retireeditornote.setPreferredSize(new Dimension(80, 35));
        JLabel retireCptStatus = new JLabel("Deprecated Concept Status");
        retireCptStatus.setPreferredSize(new Dimension(80, 35));
        JLabel retireParent = new JLabel("Deprecated Parent");
        retireParent.setPreferredSize(new Dimension(80, 35));
        JLabel retireChild = new JLabel("Deprecated Child");
        retireChild.setPreferredSize(new Dimension(80, 35));
        JLabel retireRole = new JLabel("Deprecated Role");
        retireRole.setPreferredSize(new Dimension(80, 35));
        JLabel retireAssoc = new JLabel("Deprecated Association");
        retireAssoc.setPreferredSize(new Dimension(80, 35));
        
        retireCptRootComboBox = new OwlEntityComboBox(editorKit);
        retireCptRootComboBox.addItemListener(retireCptRootItemListener);
        retireCptRootComboBox.addItems(getProperties());
        retireCptRootComboBox.setPreferredSize(new Dimension(460, 35));
        rcrComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireCptRootComboBox);
        
        retireDesignNoteComboBox = new OwlEntityComboBox(editorKit);
        retireDesignNoteComboBox.addItemListener(retireDesignNoteItemListener);
        retireDesignNoteComboBox.addItems(getProperties());
        retireDesignNoteComboBox.setPreferredSize(new Dimension(460, 35));
        rdnComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireDesignNoteComboBox);
        
        retireEditorNoteComboBox = new OwlEntityComboBox(editorKit);
        retireEditorNoteComboBox.addItemListener(retireEditorNoteItemListener);
        retireEditorNoteComboBox.addItems(getProperties());
        retireEditorNoteComboBox.setPreferredSize(new Dimension(460, 35));
        renComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireEditorNoteComboBox);
        
        retireCptStatusComboBox = new OwlEntityComboBox(editorKit);
        retireCptStatusComboBox.addItemListener(retireCptStatusItemListener);
        retireCptStatusComboBox.addItems(getProperties());
        retireCptStatusComboBox.setPreferredSize(new Dimension(460, 35));
        rcsComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireCptStatusComboBox);
        
        retireParentComboBox = new OwlEntityComboBox(editorKit);
        retireParentComboBox.addItemListener(retireParentItemListener);
        retireParentComboBox.addItems(getProperties());
        retireParentComboBox.setPreferredSize(new Dimension(460, 35));
        rpComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireParentComboBox);
        
        retireChildComboBox = new OwlEntityComboBox(editorKit);
        retireChildComboBox.addItemListener(retireChildItemListener);
        retireChildComboBox.addItems(getProperties());
        retireChildComboBox.setPreferredSize(new Dimension(460, 35));
        rcComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireChildComboBox);
        
        retireRoleComboBox = new OwlEntityComboBox(editorKit);
        retireRoleComboBox.addItemListener(retireRoleItemListener);
        retireRoleComboBox.addItems(getProperties());
        retireRoleComboBox.setPreferredSize(new Dimension(460, 35));
        rrComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireRoleComboBox);
        
        retireAssocComboBox = new OwlEntityComboBox(editorKit);
        retireAssocComboBox.addItemListener(retireAssocItemListener);
        retireAssocComboBox.addItems(getProperties());
        retireAssocComboBox.setPreferredSize(new Dimension(460, 35));
        raComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(retireAssocComboBox);
        
        retirePanel.add(retireCptRoot, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        //mergePanel.add(mergeSourceScrollpane, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 40));
        retirePanel.add(retireCptRootComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        retirePanel.add(retiredesignnote, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireDesignNoteComboBox, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireeditornote, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireEditorNoteComboBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        retirePanel.add(retireCptStatus, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireCptStatusComboBox, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireParent, new GridBagConstraints(0, 4, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireParentComboBox, new GridBagConstraints(1, 4, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireChild, new GridBagConstraints(0, 5, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireChildComboBox, new GridBagConstraints(1, 5, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireRole, new GridBagConstraints(0, 6, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireRoleComboBox, new GridBagConstraints(1, 6, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireAssoc, new GridBagConstraints(0, 7, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        retirePanel.add(retireAssocComboBox, new GridBagConstraints(1, 7, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        
        
        JPanel mergePanel =new JPanel();
        mergePanel.setLayout(new GridBagLayout());
        mergePanel.setPreferredSize(new Dimension(400, 200));
        JLabel mergesrc = new JLabel("Source");
        mergesrc.setPreferredSize(new Dimension(40, 35));
        JLabel mergetgt = new JLabel("Target");
        mergetgt.setPreferredSize(new Dimension(40, 35));
        JLabel mergedesignnote = new JLabel("Design Note");
        mergedesignnote.setPreferredSize(new Dimension(40, 35));
        JLabel mergeeditornote = new JLabel("Editor Note");
        mergeeditornote.setPreferredSize(new Dimension(40, 35));
      
        mergeSourceComboBox = new OwlEntityComboBox(editorKit);
        mergeSourceComboBox.addItemListener(mergeSourceItemListener);
        mergeSourceComboBox.addItems(getProperties());
        mergeSourceComboBox.setPreferredSize(new Dimension(500, 35));
        msComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeSourceComboBox);
        
        mergeTargetComboBox = new OwlEntityComboBox(editorKit);
        mergeTargetComboBox.addItemListener(mergeTargetItemListener);
        mergeTargetComboBox.addItems(getProperties());
        mergeTargetComboBox.setPreferredSize(new Dimension(500, 35));
        mtComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeTargetComboBox);
        
        mergeDesignNoteComboBox = new OwlEntityComboBox(editorKit);
        mergeDesignNoteComboBox.addItemListener(mergeDNItemListener);
        mergeDesignNoteComboBox.addItems(getProperties());
        mergeDesignNoteComboBox.setPreferredSize(new Dimension(500, 35));
        mdnComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeDesignNoteComboBox);
        
        mergeEditorNoteComboBox = new OwlEntityComboBox(editorKit);
        mergeEditorNoteComboBox.addItemListener(mergeENItemListener);
        mergeEditorNoteComboBox.addItems(getProperties());
        mergeEditorNoteComboBox.setPreferredSize(new Dimension(500, 35));
        menComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(mergeEditorNoteComboBox);
        
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
        
        splitFromComboBox = new OwlEntityComboBox(editorKit);
        splitFromComboBox.addItemListener(splitFromItemListener);
        splitFromComboBox.addItems(getProperties());
        splitFromComboBox.setPreferredSize(new Dimension(500, 40));
        sfComboBoxChangeHandler = new OwlEntityComboBoxChangeHandler(splitFromComboBox);
        
        //splitPanel.add(splitfrom, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        //splitPanel.add(splitFromComboBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 2, 0), 0, 0));
        splitPanel.add(splitfrom);
        splitPanel.add(splitFromComboBox);
        //splitPanel.add(splitfrom, BorderLayout.WEST);
        //splitPanel.add(splitFromComboBox, BorderLayout.EAST);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Immutable", immutPanel);
        tabbedPane.addTab("Complex", complexPanel);
        tabbedPane.addTab("Retire", retirePanel);
        tabbedPane.addTab("Merge", mergePanel);
        tabbedPane.addTab("Split", splitPanel);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setMinimumSize(new Dimension(400, 200));
        //panel2.setPreferredSize(new Dimension(600, 100));
        //panel2.setPreferredSize(new Dimension(410, 510));
        panel2.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
       
        JSplitPane splitPane = new JSplitPane(SwingConstants.HORIZONTAL, panel, panel2); 
        this.add(splitPane);
    }
    
    private void setupPropertyList() {
    	setupImmutablePropertyList();
    	setupComplexPropertyList();
    	//setupRetirePropertyList();
    	//setupMergePropertyList();
    	//setupSplitPropertyList();
    }

    protected List<OWLEntity> getProperties() {
        return LuceneUiUtils.getProperties(editorKit);
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
        immutablepropList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, immutDependentAnnotations));
        immutablepropList.addKeyListener(keyAdapter);
        immutablepropList.addMouseListener(mouseAdapter);
        immutablepropList.setVisibleRowCount(5);
        immutablepropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        immutablepropList.setListData(data.toArray());
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
        complexpropList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, complexDependentAnnotations));
        complexpropList.addKeyListener(keyAdapter);
        complexpropList.addMouseListener(mouseAdapter);
        complexpropList.setVisibleRowCount(5);
        complexpropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        complexpropList.setListData(data.toArray());
        complexAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeComplexAxiomAnnotationHandler());
    }

    /*private void setupRetirePropertyList() {
    	retirepropList = new MList() {
    		protected void handleAdd() {
    			addRetireProperty();
    		}

    		protected void handleDelete() {
    			deleteRetireProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));

    			return buttons;
    		}
        };
        
        retirepropList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        retirepropList.addListSelectionListener(retirepropListSelectionListener);
        retirepropList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, retireDependentAnnotations));
        retirepropList.addKeyListener(keyAdapter);
        retirepropList.addMouseListener(mouseAdapter);
        retirepropList.setVisibleRowCount(5);
        retirepropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        retirepropList.setListData(data.toArray());
        retireAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeRetireAxiomAnnotationHandler());
    }*/
    
    private void setupMergePropertyList() {
    	//setupMergeSourcePropertyList();
    	//setupMergeTargetPropertyList();
    }
    
    /*private void setupMergeSourcePropertyList() {
    	mergeSourceList = new MList() {
    		protected void handleAdd() {
    			addMergeSourceProperty();
    		}

    		protected void handleDelete() {
    			deleteMergeSourceProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));
    			return buttons;
    		}
        };
        
        mergeSourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mergeSourceList.addListSelectionListener(mergepropListSelectionListener);
        mergeSourceList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, mergeSourceDependentAnnotations));
        mergeSourceList.addKeyListener(keyAdapter);
        mergeSourceList.addMouseListener(mouseAdapter);
        mergeSourceList.setVisibleRowCount(5);
        mergeSourceList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        mergeSourceList.setListData(data.toArray());
        mergeSourceAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeRetireAxiomAnnotationHandler());
    }*/
    
    /*private void setupMergeTargetPropertyList() {
    	mergeTargetList = new MList() {
    		protected void handleAdd() {
    			addMergeTargetProperty();
    		}

    		protected void handleDelete() {
    			deleteMergeTargetProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));
    			return buttons;
    		}
        };
        
        mergeTargetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mergeTargetList.addListSelectionListener(mergepropListSelectionListener);
        mergeTargetList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, mergeTargetDependentAnnotations));
        mergeTargetList.addKeyListener(keyAdapter);
        mergeTargetList.addMouseListener(mouseAdapter);
        mergeTargetList.setVisibleRowCount(5);
        mergeTargetList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        mergeTargetList.setListData(data.toArray());
        mergeTargetAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeRetireAxiomAnnotationHandler());
    }
    
    private void setupSplitPropertyList() {
    	splitFromList = new MList() {
    		protected void handleAdd() {
    			addSplitFromProperty();
    		}

    		protected void handleDelete() {
    			deleteSplitFromProperty();
    		}

    		@Override
    		protected List<MListButton> getButtons(Object o) {

    			List<MListButton> buttons = new ArrayList<>(super.getButtons(o));
    			return buttons;
    		}
        };
        
        splitFromList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        splitFromList.addListSelectionListener(splitpropListSelectionListener);
        splitFromList.setCellRenderer(new NCIEditTabOwlEntityListCellRenderer(editorKit, splitFromDependentAnnotations));
        splitFromList.addKeyListener(keyAdapter);
        splitFromList.addMouseListener(mouseAdapter);
        splitFromList.setVisibleRowCount(5);
        splitFromList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        splitFromList.setListData(data.toArray());
        splitFromAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeSplitFromAxiomAnnotationHandler());
    }*/
    
    private ListSelectionListener immutablepropListSelectionListener = e -> {
        if(immutablepropList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(immutablepropList.getSelectedValue() instanceof OwlEntityListItem) {
                //selectedImmutablePropertyListItem = (OwlEntityListItem) immutablepropList.getSelectedValue();
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
    
    /*private ListSelectionListener retirepropListSelectionListener = e -> {
        if(retirepropList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(retirepropList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedRetirePropertyListItem = (OwlEntityListItem) retirepropList.getSelectedValue();
            }
        }
    };*/
    
    /*private ListSelectionListener mergepropListSelectionListener = e -> {
        if(mergeSourceList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(mergeSourceList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedMergeSourcePropertyListItem = (OwlEntityListItem) mergeSourceList.getSelectedValue();
            }
        }
        
        if(mergeTargetList.getSelectedValue() != null && !e.getValueIsAdjusting()) {
            if(mergeTargetList.getSelectedValue() instanceof OwlEntityListItem) {
                selectedMergeTargetPropertyListItem = (OwlEntityListItem) mergeTargetList.getSelectedValue();
            }
        }
    };*/
    
    private ItemListener retireCptRootItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object obj = e.getItem();
                if (obj instanceof String) {
                    ((JTextField)retireCptRootComboBox.getEditor().getEditorComponent()).setText(obj.toString());
                }
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
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(immutablepropList),
        		immutDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, immutablepropList));
    } 
    
    private void deleteImmutableProperty() {
        List items = getListItems(immutablepropList);
        items.remove(selectedImmutablePropertyListItem);
        immutablepropList.setListData(items.toArray());
    }
    
    private void addComplexProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(complexpropList),
        		complexDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, complexpropList));
    } 
    
    private void deleteComplexProperty() {
        List items = getListItems(complexpropList);
        items.remove(selectedComplexPropertyListItem);
        complexpropList.setListData(items.toArray());
    }
    
    /*private void addRetireProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(retirepropList),
        		retireDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, retirepropList));
    } 
    
    private void deleteRetireProperty() {
        List items = getListItems(retirepropList);
        items.remove(selectedRetirePropertyListItem);
        retirepropList.setListData(items.toArray());
    }*/
    
    /*private void addMergeSourceProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(mergeSourceList),
        		mergeSourceDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, mergeSourceList));
    } 
    
    private void deleteMergeSourceProperty() {
        List items = getListItems(mergeSourceList);
        items.remove(selectedMergeSourcePropertyListItem);
        mergeSourceList.setListData(items.toArray());
    }
    
    private void addMergeTargetProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(mergeTargetList),
        		mergeTargetDependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, mergeTargetList));
    } 
    
    private void deleteMergeTargetProperty() {
        List items = getListItems(mergeTargetList);
        items.remove(selectedMergeTargetPropertyListItem);
        mergeTargetList.setListData(items.toArray());
    }*/
    
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
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(immutablepropList),
        		immutDependentAnnotations, ent).ifPresent(owlEntities -> addImmutDepProps(owlEntities, ent));
    }

    private void addComplexDepProps(OWLEntity ent) {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(complexpropList),
        		complexDependentAnnotations, ent).ifPresent(owlEntities -> addComplexDepProps(owlEntities, ent));
    }
    
    /*private void addRetireDepProps(OWLEntity ent) {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(retirepropList),
        		retireDependentAnnotations, ent).ifPresent(owlEntities -> addRetireDepProps(owlEntities, ent));
    }*/
    
    private void addImmutDepProps(List<OWLEntity> entities, OWLEntity ent) {
        immutDependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), immutablepropList);

    }
    
    private void addComplexDepProps(List<OWLEntity> entities, OWLEntity ent) {
        complexDependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), complexpropList);

    }
    
    /*private void addRetireDepProps(List<OWLEntity> entities, OWLEntity ent) {
        retireDependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), retirepropList);

    }*/
    
    private void addEntitiesToList(List<OWLEntity> entities, JList list) {
        List items = getListItems(list);
        items.addAll(entities.stream().map(OwlEntityListItem::new).collect(Collectors.toList()));
        list.setListData(items.toArray());

    }

    public OWLProperty getRetireCptRootProperty() {
        return (OWLProperty) retireCptRootComboBox.getSelectedItem();
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
    
    public OWLProperty getRetireAssocProperty() {
        return (OWLProperty) retireAssocComboBox.getSelectedItem();
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
