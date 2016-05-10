package gov.nih.nci.ui;

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

public class PropertyTablePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;

    private JPopupMenu popupMenu;
    
    private PropertyTableModel tableModel;
    
    private OWLAnnotationProperty complexProp;
    
    private String tableName;

    public PropertyTablePanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        initialiseOWLView();
        createPopupMenu();
    }
    
    public PropertyTablePanel(OWLEditorKit editorKit, OWLAnnotationProperty complexProperty, String tableName) {
        this.owlEditorKit = editorKit;
        this.complexProp = complexProperty;
        this.tableName = tableName;
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
        tableModel = new PropertyTableModel(owlEditorKit, complexProp);
        createUI();
        
    }


    private void createUI() {
        //setLayout(new BorderLayout());
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //Box box = new Box(BoxLayout.Y_AXIS);
        JTable table = new JTable(tableModel);
        
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowHeight(table.getRowHeight() + 4);
        table.setShowGrid(true);       
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(true);
        table.setFillsViewportHeight(true);
        
        
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
        
        

        JScrollPane sp = new JScrollPane(table);
    
        /*final JPanel tablePanel = new JPanel(new BorderLayout());
          
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
            tablePanel.add(sp);*/
            //tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
                                                                   //ComponentFactory.createTitledBorder("TestBAR")));
            //table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            //box.add(tablePanel);
        
        //sp.setOpaque(false);
        add(new Label(tableName));
        add(sp);
    }

    public void setSelectedCls(OWLClass cls) {
    	tableModel.setSelection(cls);
    	repaint();
    }



    
    

    

   
}
