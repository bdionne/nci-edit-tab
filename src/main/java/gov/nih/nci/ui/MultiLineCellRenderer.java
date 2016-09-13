package gov.nih.nci.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MultiLineCellRenderer() {    
		setLineWrap(true);    
		setWrapStyleWord(true);    
		setOpaque(true);    
		setEditable(false);  
	}  
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) { 
			if (isSelected) {      
				setForeground(table.getSelectionForeground());      
				setBackground(table.getSelectionBackground());    
			}    
			else {      
				setForeground(table.getForeground());      
				setBackground(table.getBackground());    
			}    
			
			setFont(table.getFont());    
			
			if (hasFocus) {      
				setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );      
				if (table.isCellEditable(row, column)) {        
					setForeground( UIManager.getColor("Table.focusCellForeground") );        
					setBackground( UIManager.getColor("Table.focusCellBackground") );      
				}    
			}    
			else {      
				setBorder(new EmptyBorder(1, 2, 1, 2));    
			}    
			
			setText((value == null) ? "" : value.toString()); 
			//calculate row height for value column
			
			//table.setRowHeight(row, getPreferredSize().height + row * 10);

			return this;  
		}
		
	}
