package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.tree.UserRendering;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

import gov.nih.nci.ui.action.AddComplexTarget;
import gov.nih.nci.ui.action.CloneClassTarget;
import gov.nih.nci.ui.action.MergeClassTarget;
import gov.nih.nci.ui.action.RetireClassTarget;
import gov.nih.nci.ui.action.SplitClassTarget;
import gov.nih.nci.ui.dialog.BatchProcessingDialog;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;

public class NCIToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
implements CreateNewChildTarget, SplitClassTarget, CloneClassTarget, MergeClassTarget,
RetireClassTarget, AddComplexTarget, SelectionDriver {
	
	private static final Icon ADD_SUB_ICON = OWLIcons.getIcon("class.add.sub.png");
	private static final JButton batchbutton = new JButton("Batch Load/Edit");
	private static final JButton searchbutton = new JButton("Search");
	private static final BatchProcessOutputPanel batchProcessPanel = new BatchProcessOutputPanel();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

	public NCIToldOWLClassHierarchyViewComponent() {}	

	public void performExtraInitialisation() throws Exception {

		batchProcessPanel.setVisible(false);
		add(batchProcessPanel, BorderLayout.SOUTH);
		
		addAction(new AbstractOWLTreeAction<OWLClass>("Add subclass", ADD_SUB_ICON, getTree().getSelectionModel()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				createNewChild();
			}

			protected boolean canPerform(OWLClass cls) {
				return canCreateNewChild();
			}
		}, "A", "A");
		
		addAction(new DisposableAction("Batch Load/Edit", batchbutton.getIcon()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				if (!NCIEditTab.currentTab().isEditing()) {
					batchProcessPanel.setVisible(true);
					BatchProcessingDialog dl = new BatchProcessingDialog(batchProcessPanel, NCIEditTab.currentTab());
				} else {
					warn("Can't perform batch load/edit while editing in progress.");
					
				}
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			
		}, "B", "B");
		
		addAction(new DisposableAction("Search", searchbutton.getIcon()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				
				Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
		        if(focusOwner == null) {
		            return;
		        }
		        
		        OWLClass cls = getOWLWorkspace().searchForClass(focusOwner);
		        if (cls != null) {
				setSelectedEntity(cls);
		        }			
				
			}

			public void dispose() {
				
			}
		}, "C", "C");
		
		getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
            public boolean canDrop(Object child, Object parent) {
                return false;
            }
            public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {}
            public void add(OWLClass child, OWLClass parent) {}
        });
        getAssertedTree().setPopupMenuId(new PopupMenuId("[NCIAssertedClassHierarchy]")); 
        
        getAssertedTree().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				if (NCIEditTab.currentTab().isRetiring()) {
					getTree().setSelectedOWLObject(NCIEditTab.currentTab().getRetireClass());	
				} else if (NCIEditTab.currentTab().isEditing()) {
					getTree().setSelectedOWLObject(NCIEditTab.currentTab().getCurrentlyEditing());
				} else if (NCIEditTab.currentTab().isSplitting()) {
					// if merging or cloning splitting returns the same result
					getTree().setSelectedOWLObject(NCIEditTab.currentTab().getSplitSource());
				} else {
					if (NCIEditTab.currentTab().isRetired(getTree().getSelectedOWLObject())) {
						//NCIEditTab.currentTab().setCurrentlyEditing(null);
						NCIEditTab.currentTab().fireChange(new EditTabChangeEvent(NCIEditTab.currentTab(), ComplexEditType.READ));
						//NCIEditTab.currentTab().selectClass(getTree().getSelectedOWLObject());

					} else {
						NCIEditTab.currentTab().selectClass(getTree().getSelectedOWLObject());
					}
				}				
			}        	
        });
        
        NCIEditTab.setNavTree(this);               
    }
	
	private void warn(String msg) {
		JOptionPane.showMessageDialog(this, 
				msg, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		if (NCIEditTab.currentTab().inComplexOp()) {
			
		} else {
			setSelectedEntity(selectedClass);
		}   
		
        return selectedClass;
	}
	
	@Override
	public void setSelectedEntity(OWLClass entity) {

		getTree().setSelectedOWLObject(entity);		
		NCIEditTab.currentTab().selectClass(entity);

	} 
	
	private boolean isFree() {
		return NCIEditTab.currentTab().isFree();
		
	}
	 
	@Override
	public boolean canRetireClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canRetire(getSelectedEntity()) &&
				isFree());
	}

	@Override
	public void retireClass() {
		OWLClass selectedClass = getSelectedEntity();
		NCIEditTab.currentTab().retire(selectedClass);
		refreshTree();
		
	}
	
	public void refreshTree() {
		this.getTree().refreshComponent();	
	}

	@Override
	public boolean canMergeClass() {
		return (getSelectedEntities().size() == 2 &&
				NCIEditTab.currentTab().canMerge() &&
				isFree());
	}

	@Override
	public void mergeClass() {
		System.out.println("OK, do the merge....");
	}

	@Override
	public boolean canCloneClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canClone(getSelectedEntity()) &&
				isFree());
	}

	@Override
	public void cloneClass() {
		splitOrCloneClass(true);		
	}

	@Override
	public boolean canSplitClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canSplit(getSelectedEntity()) &&
				isFree());
	}

	@Override
	public void splitClass() {
		splitOrCloneClass(false);
		
	}
	
	private void splitOrCloneClass(boolean clone_p) {
		if (clone_p) {
			NCIEditTab.currentTab().setOp(ComplexEditType.CLONE);
		} else {
			NCIEditTab.currentTab().setOp(ComplexEditType.SPLIT);
		}
		NCIClassCreationDialog<OWLClass> dlg = new NCIClassCreationDialog<OWLClass>(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class, Optional.empty(), Optional.empty());
		if (dlg.showDialog()) {
			NCIEditTab.currentTab().splitClass(dlg.getNewClass(), getSelectedEntity(), clone_p);
			
		}		
	}
	
	private boolean isRestricted(OWLClass cls) {
		if (cls.equals(NCIEditTabConstants.RETIRE_ROOT) ||
				cls.equals(NCIEditTabConstants.PRE_MERGE_ROOT) ||
				cls.equals(NCIEditTabConstants.PRE_RETIRE_ROOT) ||
				NCIEditTab.currentTab().isRetired(cls) ||
				cls.getIRI().getShortForm().equals("Thing") ||
				cls.getIRI().getShortForm().equals("Retired_Concepts")) {
			return true;
		} else {
			return false;
		}		
	}
	
	

	@Override
	public boolean canCreateNewChild() {
		if (NCIEditTab.currentTab().isEditing()) {
			return false;
		}
		if (getSelectedEntities().size() == 1) {
			if (NCIEditTab.currentTab().isWorkFlowManager()) {
				return true;
			} else {
				// modeler can't create at top
				if (isRestricted(getSelectedEntity())) {
					return false;
				} else {
					return true;
				}
			}
			
		}
		return false;
	}

	@Override
	public void createNewChild() {	

		OWLClass selectedClass = getSelectedEntity();
		if (NCIEditTab.currentTab().isEditing()) {
			JOptionPane.showMessageDialog(this, 
					"Can't create new class while edit in progress.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		NCIEditTab.currentTab().enableBatchMode();
		OWLClass newCls = NCIEditTab.currentTab().createNewChild(selectedClass, Optional.empty(), Optional.empty(), false);
		if (newCls != null) {
			getTree().setSelectedOWLObject(newCls);

			NCIEditTab.currentTab().disableBatchMode();

			NCIEditTab.currentTab().classModified();
			NCIEditTab.currentTab().setNew(true);
			this.getTree().refreshComponent();
		}

	}
	
	 protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
	        return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
	    }

	    
	 @Override
	 protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
		 return Optional.of(getOWLModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider());
	 }
	    

		@Override
		public boolean canAddComplex() {
			return isFree();
		}

		@Override
		public void addComplex() {
			OWLClass selectedClass = getSelectedEntity();
			NCIEditTab.currentTab().addComplex(selectedClass);
		}

		@Override
		protected UserRendering getUserRenderer() {
			return new UserRendering() {

				@Override
				public String render(String in) {

					if (NCIEditTab.currentTab().isRetiring()) {
						OWLClass cls = NCIEditTab.currentTab().getRetireClass();
						if (cls != null) {
							String orig = 
									getOWLEditorKit().getOWLModelManager().getRendering(cls);
							if (in.equals(orig)) {
								return in + "(retiring...)";
							}
						}
					};

					if (NCIEditTab.currentTab().isEditing()) {
						OWLClass cls = NCIEditTab.currentTab().getCurrentlyEditing(); 
						if (cls != null) {
							String orig = 
									getOWLEditorKit().getOWLModelManager().getRendering(cls);
							if (in.equals(orig)) {
								return in + "(editing...)";
							}
						}

					}
					if (NCIEditTab.currentTab().isSplitting()) {
						OWLClass cls = NCIEditTab.currentTab().getSplitSource(); 
						if (cls != null) {
							String orig = 
									getOWLEditorKit().getOWLModelManager().getRendering(cls);
							if (in.equals(orig)) {
								return in + "(splitting...)";
							}
						}

					}
					return in;
				}

			};
		}

		@Override
		public Component asComponent() {
			return this;
		}

		    @Override
		    public Optional<OWLObject> getSelection() {
		        return Optional.ofNullable(getSelectedEntity());
		    }

		

	
}
