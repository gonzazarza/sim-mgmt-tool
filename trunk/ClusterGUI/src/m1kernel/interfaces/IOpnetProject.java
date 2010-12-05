package m1kernel.interfaces;

//exceptions
import m1kernel.exceptions.OpnetLightException;
import m1kernel.exceptions.OpnetStrongException;

/** 
 * Interface to acces the Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1027
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
	
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 */
	public void setNetworksMap() throws OpnetStrongException, OpnetLightException;
	
	
	/**
	 * @return the complete list of supplementary environmental files and their included status  
	 */
	public Object[][] getFilesData() throws OpnetStrongException;	
	
		
	/** 
	 * @param the data for the list of supplementary environmental files and their included status to set 
	 */
	public void setTableData(Object[][] pData) throws OpnetStrongException;
	
	
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 			the set of network names prepared for the output text area  
	 */
	public String getOutputNetworkNames();
	
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the projectName */
	public String getProjectName();
	
	
} // End interface IOpnetProject

