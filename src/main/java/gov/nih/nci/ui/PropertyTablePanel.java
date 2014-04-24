package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLClass;

public class PropertyTablePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;

    private JPopupMenu popupMenu;
    
    private PropertyTableModel tableModel;

    public PropertyTablePanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        initialiseOWLView();
        createPopupMenu();
    }


    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("Show axioms") {
            public void actionPerformed(ActionEvent e) {
                

            }
        });
    }





    protected void initialiseOWLView() {
        tableModel = new PropertyTableModel(owlEditorKit);
        createUI();
        
    }


    private void createUI() {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        JTable table = new JTable(tableModel);
        
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowHeight(table.getRowHeight() + 4);
        table.setShowGrid(true);       
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(true);
        
        
        table.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    handleTablePopupRequest(table, e);
                }
            }


            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    handleTablePopupRequest(table, e);
                }
            }

            private void handleTablePopupRequest(JTable table, MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if(row == -1 || col == -1) {
                    return;
                }
                popupMenu.show(table, e.getX(), e.getY());
                

            }});
        
        

            final JPanel tablePanel = new JPanel(new BorderLayout());
            
          
            tablePanel.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }


                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }

                private void showMenu(MouseEvent e) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new AbstractAction("Copy metrics to clipboard") {

                        public void actionPerformed(ActionEvent e) {
                            //exportCSV();
                        }
                    });
                    menu.show(tablePanel, e.getX(), e.getY());
                }
            });
            tablePanel.add(table);
            tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
                                                                    ComponentFactory.createTitledBorder("Test")));
            table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            box.add(tablePanel);
        
        JScrollPane sp = new JScrollPane(box);
        sp.setOpaque(false);
        add(sp);
    }

    public void setSelectedCls(OWLClass cls) {
    	tableModel.setSelection(cls);
    	repaint();
    }



    
    

    

   
}
