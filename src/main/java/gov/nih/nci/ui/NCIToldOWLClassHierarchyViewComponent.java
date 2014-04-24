package gov.nih.nci.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;

public class NCIToldOWLClassHierarchyViewComponent extends ToldOWLClassHierarchyViewComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPopupMenu popupMenu;

	public NCIToldOWLClassHierarchyViewComponent() {
		
		// TODO Auto-generated constructor stub
	}
	
	private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("Split/Clone") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
                

            }
        });
    }

	public void performExtraInitialisation() throws Exception {
        
		super.performExtraInitialisation();
		super.getTree().setDragEnabled(false);

		createPopupMenu();
		
		
		getTree().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    handleTablePopupRequest(e);
                }
            }


            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    handleTablePopupRequest(e);
                }
            }

            private void handleTablePopupRequest(MouseEvent e) {
            	popupMenu.show(getTree(), e.getX(), e.getY());
            }
        });
		
		
		

        
		

    }
}
