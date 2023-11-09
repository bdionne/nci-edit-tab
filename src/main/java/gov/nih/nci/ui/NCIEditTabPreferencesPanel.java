package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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

import com.google.common.base.Objects;

//import edu.stanford.protege.csv.export.ui.AddPropertyToExportDialogPanel;
import edu.stanford.protege.csv.export.ui.OwlEntityListCellRenderer;
import edu.stanford.protege.csv.export.ui.UiUtils;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.PreferencesChangeEvent;

public class NCIEditTabPreferencesPanel extends OWLPreferencesPanel {
	//Add comments here
	private static final long serialVersionUID = 1L;
	
	private MList immutablepropList;
	private MList complexpropList;
	private MList splitFromList;
	private MList mergeSourceList;
	private MList mergeTargetList;
	private MList retireList;
	
	private AxiomAnnotationButton immutAxiomAnnotationButton;
	private AxiomAnnotationButton complexAxiomAnnotationButton;
	private Map<OWLEntity, List<OWLEntity>> dependentAnnotations = new HashMap<OWLEntity, List<OWLEntity>>();
	private OwlEntityListItem selectedImmutablePropertyListItem;
	private OwlEntityListItem selectedComplexPropertyListItem;
	
	private OWLEditorKit editorKit = NCIEditTab.currentTab().getOWLEditorKit();

    private JLabel complexEditLabel = new JLabel("Complex Edit");

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
        JPanel p1=new JPanel();
        p1.setLayout(new GridBagLayout());
        p1.setMinimumSize(new Dimension(600, 120));
        //p1.setPreferredSize(new Dimension(600, 100));
        //p1.setPreferredSize(new Dimension(400, 500));
        //Insets insets = new Insets(2, 2, 2, 2);
        JScrollPane immutablepropScrollpane = new JScrollPane(immutablepropList);
        immutablepropScrollpane.setMinimumSize(new Dimension(600, 120));
        //immutablepropScrollpane.setPreferredSize(new Dimension(600, 100));
        immutablepropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        JLabel immutablepropLbl = new JLabel("Immutable Properties");
        //p1.add(immutablepropLbl, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.NONE, new Insets(15, 2, 2, 2), 0, 0));
        //p1.add("Immutable Properties", immutablepropScrollpane);
        p1.add(immutablepropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        JPanel p2=new JPanel();
        p2.setLayout(new GridBagLayout());
        p2.setMinimumSize(new Dimension(600, 120));
        //p2.setPreferredSize(new Dimension(600, 100));
        //p2.setPreferredSize(new Dimension(400, 500));
        JScrollPane complexpropScrollpane = new JScrollPane(complexpropList);
        complexpropScrollpane.setMinimumSize(new Dimension(600, 120));
        //complexpropScrollpane.setPreferredSize(new Dimension(600, 100));
        complexpropScrollpane.setBorder(UiUtils.MATTE_BORDER);
        JLabel complexpropLbl = new JLabel("Complex Properties");
        GridBagConstraints c = new GridBagConstraints();
        p2.add(complexpropScrollpane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Immutable", p1);
        tabbedPane.addTab("Complex", p2);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setMinimumSize(new Dimension(600, 120));
        //panel2.setPreferredSize(new Dimension(600, 100));
        //panel2.setPreferredSize(new Dimension(410, 510));
        panel2.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
       
        JSplitPane splitPane = new JSplitPane(SwingConstants.HORIZONTAL, panel, panel2); 
        this.add(splitPane);
    }
    
    private void setupPropertyList() {
    	setupImmutablePropertyList();
    	setupComplexPropertyList();
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
        immutablepropList.setCellRenderer(new OwlEntityListCellRenderer(editorKit, dependentAnnotations));
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
        complexpropList.setCellRenderer(new OwlEntityListCellRenderer(editorKit, dependentAnnotations));
        complexpropList.addKeyListener(keyAdapter);
        complexpropList.addMouseListener(mouseAdapter);
        complexpropList.setVisibleRowCount(5);
        complexpropList.setBorder(new EmptyBorder(2, 2, 0, 2));

        List<Object> data = new ArrayList<>();
        data.add(new OwlPropertyListHeaderItem());
        complexpropList.setListData(data.toArray());
        complexAxiomAnnotationButton = new AxiomAnnotationButton(event -> invokeComplexAxiomAnnotationHandler());
    }

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
        		dependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, immutablepropList));
    } 
    
    private void deleteImmutableProperty() {
        List items = getListItems(immutablepropList);
        items.remove(selectedImmutablePropertyListItem);
        immutablepropList.setListData(items.toArray());
    }
    
    private void addComplexProperty() {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(complexpropList),
        		dependentAnnotations, null).ifPresent(owlEntities -> addEntitiesToList(owlEntities, complexpropList));
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
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(immutablepropList),
        		dependentAnnotations, ent).ifPresent(owlEntities -> addImmutDepProps(owlEntities, ent));
    }

    private void addComplexDepProps(OWLEntity ent) {
    	AddPropertiesToPreferencesPanel.showDialog(editorKit, getEntities(complexpropList),
        		dependentAnnotations, ent).ifPresent(owlEntities -> addComplexDepProps(owlEntities, ent));
    }
    
    private void addImmutDepProps(List<OWLEntity> entities, OWLEntity ent) {
        dependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), immutablepropList);

    }
    
    private void addComplexDepProps(List<OWLEntity> entities, OWLEntity ent) {
        dependentAnnotations.put(ent, entities);
        addEntitiesToList(new ArrayList<OWLEntity>(), complexpropList);

    }
    
    private void addEntitiesToList(List<OWLEntity> entities, JList list) {
        List items = getListItems(list);
        items.addAll(entities.stream().map(OwlEntityListItem::new).collect(Collectors.toList()));
        list.setListData(items.toArray());

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
            return entity.getIRI().toQuotedString();
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
