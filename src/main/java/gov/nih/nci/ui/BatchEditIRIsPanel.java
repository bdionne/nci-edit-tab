package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLClass;

public class BatchEditIRIsPanel extends JPanel /*implements ActionListener*/ {

	public static final long serialVersionUID = 123456001L;

	private NCIEditTab tab = null;
	
	private OWLEditorKit owlEditorKit;
	
	private BatchEditIRIsTableModel tableModel;
	private JTable iriTable;
	private JScrollPane sp;
	
	public BatchEditIRIsPanel(OWLEditorKit editorKit) {
		
		super(new BorderLayout());
		owlEditorKit = editorKit;
		tab = NCIEditTab.currentTab();
		init();

	}
	
	private void init() {
		tableModel = new BatchEditIRIsTableModel(owlEditorKit);
		createUI();
	}
	
	private void createUI() {
		setLayout(new BorderLayout());
    	
		iriTable = new JTable(tableModel);
        
		iriTable.setGridColor(Color.LIGHT_GRAY);
		iriTable.setRowHeight(iriTable.getRowHeight() + 4);
		iriTable.setShowGrid(true);       
		iriTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		iriTable.getTableHeader().setReorderingAllowed(true);
		iriTable.setFillsViewportHeight(true);   
		iriTable.setAutoCreateRowSorter(true);
		iriTable.setCellSelectionEnabled(true);
		
		ActionListener listener = new ActionListener() {
			 public void actionPerformed(ActionEvent event) {
				 doCopy();
			 }//end actionPerformed(ActionEvent)
		};

		final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

		iriTable.registerKeyboardAction(listener, "Copy", stroke, JComponent.WHEN_FOCUSED);
		
		sp = new JScrollPane(iriTable);
        //createLabelHeader(tableName, addButton, editButton, deleteButton);
        //add(tableHeaderPanel, BorderLayout.NORTH);
        //tableHeaderPanel.setVisible(false);        
        sp.setVisible(true);
        //sp.setPreferredSize(new Dimension(180, 150));
        add(sp, BorderLayout.CENTER);
		
	}
	
	/*@Override
	public void actionPerformed(ActionEvent e) {
		
	}*/
	
	private void doCopy() {
	    int col = iriTable.getSelectedColumn();
	    int row = iriTable.getSelectedRow();
	    if (col != -1 && row != -1) {
	        Object value = iriTable.getValueAt(row, col);
	        String data;
	        if (value == null) {
	            data = "";
	        } else {
	            data = value.toString();
	        }//end if

	        final StringSelection selection = new StringSelection(data);     

	        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(selection, selection);
	    }//end if
	}//end doCopy()
	
	public void setSelectedClass(OWLClass cls) {
		
	}
	
	public void disposeView() {
		
	}
}
