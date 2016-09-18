package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JButton;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.action.AddComplexTarget;
import gov.nih.nci.ui.action.CloneClassTarget;
import gov.nih.nci.ui.action.MergeClassTarget;
import gov.nih.nci.ui.action.RetireClassTarget;
import gov.nih.nci.ui.action.SplitClassTarget;
import gov.nih.nci.ui.dialog.BatchProcessingDialog;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;

public class NCIToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
implements CreateNewChildTarget, SplitClassTarget, CloneClassTarget, MergeClassTarget,
RetireClassTarget, AddComplexTarget {
	
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
				batchProcessPanel.setVisible(true);
				BatchProcessingDialog dl = new BatchProcessingDialog(batchProcessPanel, NCIEditTab.currentTab());
			}			
			
			public void dispose() {
				
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
        
    }	
	 
	@Override
	public boolean canRetireClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canRetire(getSelectedEntity()));
	}

	@Override
	public void retireClass() {
		OWLClass selectedClass = getSelectedEntity();
		NCIEditTab.currentTab().retire(selectedClass);
		
	}

	@Override
	public boolean canMergeClass() {
		return (getSelectedEntities().size() == 2 &&
				NCIEditTab.currentTab().canMerge());
	}

	@Override
	public void mergeClass() {
		System.out.println("OK, do the merge....");
	}

	@Override
	public boolean canCloneClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canClone(getSelectedEntity()));
	}

	@Override
	public void cloneClass() {
		splitOrCloneClass(true);		
	}

	@Override
	public boolean canSplitClass() {
		return (getSelectedEntities().size() == 1 &&
				NCIEditTab.currentTab().canSplit(getSelectedEntity()));
	}

	@Override
	public void splitClass() {
		splitOrCloneClass(false);
		
	}
	
	private void splitOrCloneClass(boolean clone_p) {
		NCIClassCreationDialog<OWLClass> dlg = new NCIClassCreationDialog<OWLClass>(getOWLEditorKit(),
				"Please enter a class name", OWLClass.class, Optional.empty(), Optional.empty());
		if (dlg.showDialog()) {
			NCIEditTab.currentTab().splitClass(dlg.getNewClass(), dlg.getOntChanges(), getSelectedEntity(), clone_p);
			getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(dlg.getNewClass());
			
		}		
	}
	
	

	@Override
	public boolean canCreateNewChild() {
		return (getSelectedEntities().size() == 1);
	}

	@Override
	public void createNewChild() {	
				
		OWLClass selectedClass = getSelectedEntity();
		
		OWLClass newCls = NCIEditTab.currentTab().createNewChild(selectedClass, Optional.empty(), Optional.empty());
		
		
		
		getTree().setSelectedOWLObject(newCls);
		this.getTree().refreshComponent();
		
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
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void addComplex() {
			OWLClass selectedClass = getSelectedEntity();
			NCIEditTab.currentTab().addComplex(selectedClass);
			// TODO Auto-generated method stub
			
		}

	
}
