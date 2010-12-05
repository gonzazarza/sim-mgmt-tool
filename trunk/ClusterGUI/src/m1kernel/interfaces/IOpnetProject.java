package m1kernel.interfaces;

//exceptions
import java.util.Set;
import java.util.Vector;

import m1kernel.exceptions.OpnetLightException;
import m1kernel.exceptions.OpnetStrongException;

/** 
 * Interface to acces the Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1102
 */
public interface IOpnetProject {

	/*	
	================================================================================================================== 
	Static Attributes
	==================================================================================================================
	*/
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/**
	 * Load the project file
	 * 
	 *  @param		pPath				project path
	 *  @param 		pName				project name  
	 */
	public void loadProject(String pPath, String pName) throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 */
	public void setNetworksMap() throws OpnetStrongException, OpnetLightException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Run the op_mksim command
	 * 
	 * @return				the output stream in a string 
	 */
	public String runMKSIMCmd() throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * @return the complete list of supplementary environmental files and their included status  
	 */
	public Object[][] getFilesData() throws OpnetStrongException;	
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** 
	 * @param the data for the list of supplementary environmental files and their included status to set 
	 */
	public void setFilesData(Object[][] pData) throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Set the isIncluded status for the fileName
	 * 
	 * @param		pFileName			the item identifier
	 * @param		pIsIncluded			the item status
	 * 
	 */	
	public void setIncluded(String pFileName, boolean pIsIncluded) throws OpnetStrongException;

	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * @return the length of the files data  (a.k.a. the number of ef files) 
	 */
	public int getFilesDataLength();

	/* ------------------------------------------------------------------------------------------------------------ */	
		
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 			the set of network names prepared for the output text area  
	 */
	public String getOutputNetworkNames() ;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * @return the set of network names
	 */
	public Set<String> getNetworksNames() throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * @return the set of selected network names
	 */
	public Set<String> getSelectedNetworksNames() throws OpnetStrongException;

	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the content of the specified file
	 * 
	 * @param	pFileName			the name of the file to load
	 * 
	 */
	public String getEfFileContent(String pFileName) throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Return the code for the op_mksim command for the specified network
	 * 
	 *  @param			pNetName	the network name
	 *  @return						the op_mksim code
	 */
	public Vector<String> getNetworkMKSIMCode(String pNetName) throws OpnetStrongException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** @return the op_mksim command help */
	public String getMKSIMHelp();
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the projectName */
	public String getProjectName();
	
	
} // End interface IOpnetProject

