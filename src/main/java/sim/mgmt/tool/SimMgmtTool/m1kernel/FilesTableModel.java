package sim.mgmt.tool.SimMgmtTool.m1kernel;

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

//classes
import javax.swing.table.AbstractTableModel;
//interfaces
import sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces.ISysUtils;

/**
 * Modified table model class to edit boolean fields (checkboxs) for the ef files table
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2010.1021
 */
public class FilesTableModel extends AbstractTableModel {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private static final long			serialVersionUID 		= 1L;				//default version
	private String[]					columnNames				= null;				//table header
	private Object[][]					data					= null;				//table data
	private String						className				= null;				//class name
	private ISysUtils					sysUtils				= null;				//system-based utilities
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor 
	 *
	 * @param 	pSysUtils 				the system utilities class
	 * @param 	pData 					the table data
	 * @param	pColumnNames 			the table column headers
	 *
	 */
	public FilesTableModel(ISysUtils pSysUtils, Object[][] pData, String[] pColumnNames){
		
		//get and stores the name of the class
		this.className				= this.getClass().getName();	
		
		//set the system utilities class
		this.sysUtils				= pSysUtils;
		
        //inform the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
		
		//set the column names and the data
		if (pData != null){
			this.data					= pData;
		} else {			
			this.data					= this.setDefaultData();
		}
		if (pColumnNames != null){
			this.columnNames			= pColumnNames;
		} else {
			this.columnNames			= this.setDefaultColumnNames();
		}		
				
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);
		
	} // End constructor
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/** Reset the table model data and column names 
	 * 
	 * @param	pColumnNames		the new columnNames
	 * @param	pData				the new data
	 * @return						the operation status
	 */
	public boolean resetModel(String[] pColumnNames, Object[][] pData){
		
		//status flag
		boolean		status			= false;
		
		//delete previous data
		this.columnNames			= null;
		this.data					= null;
		
		//set new values
		//--- data
		if (pData != null){
			this.data				= pData;
		} else {
			this.data				= this.setDefaultData();
		}
		//--- column names
		if (pColumnNames != null){
			this.columnNames		= pColumnNames;
		} else {
			this.columnNames		= this.setDefaultColumnNames();
		}		
		
		status						= true;
		
		return(status);
		
	} // End boolean resetModel	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Clean the table model data and column names */
	public void cleanModel(){
		
		//clean the column and data
		this.columnNames			= this.setDefaultColumnNames();
		this.data					= this.setDefaultData();
		
	} // End cleanModel
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the default column names 
	 *
	 * @return		the default column names 
	 * 
	 */
	private String[] setDefaultColumnNames(){
		
		//local attributes
		String[]	lNames			= new String[]{"File name", "Include?"};
		
		return (lNames);
		
	} // End String[] setDefaultColumnNames	
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** Set the default data values 
	 *
	 * @return		the default data 
	 * 
	 */
	private Object[][] setDefaultData(){
		
		//local attributes
		Object[][] 	lData			= new Object[1][2];
			
		lData[0][0]					= new String("");
		lData[0][1]					= new String("");
		
		return (lData);
		
	} // End Object[][] setDefaultData
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the column count */
	public int getColumnCount() {
        return(columnNames.length);		
         
    } // End int getColumnCount 

	/* ------------------------------------------------------------------------------------------------------------ */
 
	/** @return the row count */
    public int getRowCount() {
         return(data.length);
         
    } // End int getRowCount 
     
	/* ------------------------------------------------------------------------------------------------------------ */

    /** @return the column name */
    @Override
	public String getColumnName(int col) {
        return(columnNames[col]);
         
    } // End String getColumName
     
	/* ------------------------------------------------------------------------------------------------------------ */

    /** @return the cell value */
    public Object getValueAt(int row, int col) {
         return(data[row][col]);
         
    } // End object getValueAt
    
	/* ------------------------------------------------------------------------------------------------------------ */
    
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes"})
    /** @return the column class */
	public Class getColumnClass(int c) {
         return getValueAt(0, c).getClass();
         
    } // End class getColumnClass
    
	/* ------------------------------------------------------------------------------------------------------------ */
        
    /** @return the editable property of a cell */
    @Override
	public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
    	if (col != 1) {
            return false;
        }
		return true;
    } // End boolean isCellEditable
    
	/* ------------------------------------------------------------------------------------------------------------ */

    /** Set the cell value */
    @Override
	public void setValueAt(Object value, int row, int col) {
    
        data[row][col] = value;
        // Normally, one should call fireTableCellUpdated() when 
        // a value is changed.  However, doing so in this demo
        // causes a problem with TableSorter.  The tableChanged()
        // call on TableSorter that results from calling
        // fireTableCellUpdated() causes the indices to be regenerated
        // when they shouldn't be.  Ideally, TableSorter should be
        // given a more intelligent tableChanged() implementation,
        // and then the following line can be uncommented.
        // fireTableCellUpdated(row, col);
        
    } // End void setValueAt
    

} // End class FileTableModel
