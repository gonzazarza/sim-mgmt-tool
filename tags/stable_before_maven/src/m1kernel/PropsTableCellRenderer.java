package m1kernel;

/* 
 * Copyright (c) 2010-2013 Gonzalo Zarza. All rights reserved.
 * Copyright (c) 2010-2011 Departament d'Arquitectura de Computadors i Sistemes Operatius, Universitat Autonoma de Barcelona. All rights reserved. 
 *
 * This file is part of the Simulation Management Tool (sim-mgmt-tool).
 *
 * Simulation Management Tool (sim-mgmt-tool) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Simulation Management Tool (sim-mgmt-tool) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Simulation Management Tool (sim-mgmt-tool). If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0306
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
	/** Class constructor 
	 *
	 * @param	pSysUtils				the system utilities class
	 * 
	 */
	public PropsTableCellRenderer(ISysUtils pSysUtils){
		
		//gets and stores the name of the class
		this.className				= this.getClass().getName();	
		
		//set the system utilities class
		this.sysUtils				= pSysUtils;
		
        //inform the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
		
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);
		
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
			cell.setBackground(AppUtils.COLOR_NOT_APPLIED);
			//font color
			cell.setForeground(Color.BLACK);
			//font style
			cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
			
		} else if (column == 1){
			//set horizontal alignment for every status
			this.setHorizontalAlignment(SwingConstants.CENTER);
			
			if (value.equals(AppUtils.STAT_NOT_APPLIED)){
				//cell color
				cell.setBackground(AppUtils.COLOR_NOT_APPLIED);
				//font color
				cell.setForeground(Color.GRAY);
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			} else if (value.equals(AppUtils.STAT_DONE)){
				//cell color
				cell.setBackground(AppUtils.COLOR_DONE);
				//font color
				cell.setForeground(Color.WHITE);
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			} else if (value.equals(AppUtils.STAT_FAIL)){
				//cell color
				cell.setBackground(AppUtils.COLOR_FAIL);
				//font color
				cell.setForeground(Color.WHITE);	
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
				
			} else if (value.equals(AppUtils.STAT_RUNNING)){
				//cell color
				cell.setBackground(AppUtils.COLOR_RUNNING);
				//font color
				cell.setForeground(Color.WHITE);
				//font style
				cell.setFont(new Font(fOld.getFamily(), Font.PLAIN, fOld.getSize()));
			}
		}
		
		return cell;

	} // End Component getTableCellRenderer

	
} // End class PropsTableCellRenderer
