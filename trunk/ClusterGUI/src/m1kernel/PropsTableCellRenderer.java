package m1kernel;

//awt classes
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
//swing classes
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
//interfaces
import m1kernel.interfaces.ISysUtils;

/**
 * Modified table cell renderer class for the properties table (to modify cell color and fonts)
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1022
 */
public class PropsTableCellRenderer extends DefaultTableCellRenderer {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private static final long			serialVersionUID 		= 1L;				//default version
	private ISysUtils					sysUtils				= null;				//system based utilities
	private String						className				= "unknown";		//class name
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor */
	public PropsTableCellRenderer(ISysUtils pSysUtils){
		
		//gets and stores the name of the class
		this.className				= this.getClass().getName();	
		
		//set the system utilities class
		this.sysUtils				= pSysUtils;
		
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);
		
	} // End constructor
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component	cell 	= super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		Font		fOld	= cell.getFont();
		
		
		
		//avoid the null pointer exception
		if (value == null){ return (null); }
		
		//change cell color and font
		if (column == 0){
			//set horizontal alignment
			this.setHorizontalAlignment(SwingConstants.LEFT);			
			//cell color
			cell.setBackground(new Color(211, 211, 211));
			//font color
			cell.setForeground(Color.BLACK);
			//font style
			cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
			
		} else if (column == 1){
			//set horizontal alignment for every status
			this.setHorizontalAlignment(SwingConstants.CENTER);
			
			if (value.equals(AppKernel.STAT_NOT_APPLIED)){
				//cell color
				cell.setBackground(AppUtils.COLOR_NOT_APPLIED);
				//font color
				cell.setForeground(Color.GRAY);
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			} else if (value.equals(AppKernel.STAT_DONE)){
				//cell color
				cell.setBackground(AppUtils.COLOR_DONE);
				//font color
				cell.setForeground(Color.WHITE);
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			} else if (value.equals(AppKernel.STAT_FAIL)){
				//cell color
				cell.setBackground(AppUtils.COLOR_FAIL);
				//font color
				cell.setForeground(Color.WHITE);	
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			}
		}
		
		return cell;

	} // End Component getTableCellRenderer

	
} // End class PropsTableCellRenderer
