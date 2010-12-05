package m1kernel;

//classes
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
//exceptions
import m1kernel.exceptions.OpnetExceptionClass;
import m1kernel.exceptions.OpnetStrongException;
import m1kernel.exceptions.OpnetLightException;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.IOpnetProject;
import m1kernel.interfaces.ISysUtils;

/** 
 * Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1028
 */
public class OpnetProject implements IOpnetProject {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private ISysUtils									sysUtils			= null;				//system-based utilities
	private IAppUtils									appUtils			= null;				//application-based utilities
	private String										className			= "unknown";		//class name
	private String										fileSeparator		= "/";				//system file separator
	private String										lineSeparator		= "\n";				//system line separator
	private String										projectName			= "";				//project name
	private String										projectPath			= "";				//project path	
	private HashMap<String,HashMap<String,OpnetJob>>	networksMap			= null;				//project networks map
	private int											efFilesNum			= 0;				//number of ef files
	//logic control attributes
	private boolean										isProjecLoaded		= false;			//project load flag
	private boolean										isNetworksMapSet	= false;			//networks map load flag
	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor 
	 */
	public OpnetProject(ISysUtils pSysUtils){
		
		//get the class name  
		this.className				= this.getClass().getName(); 
		
		//set the utilities classes
		this.sysUtils				= pSysUtils;
		this.appUtils				= new AppUtils(this.sysUtils);
		
		//load separators
		this.fileSeparator			= System.getProperty("file.separator");
		this.lineSeparator			= System.getProperty("line.separator");
		
		//initialize the networks map
		this.networksMap			= new HashMap<String,HashMap<String,OpnetJob>>();
		
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);		
		
	} // End constructor
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* PUBLIC METHODS																								*/
	/* ------------------------------------------------------------------------------------------------------------ */	
	/**
	 * Load the project file
	 * 
	 *  @param		pPath				project path
	 *  @param 		pName				project name  
	 */
	public void loadProject(String pPath, String pName) throws OpnetStrongException {
	
		//initialize the load project flag
		this.isProjecLoaded			= false;
		
		//get the project name
		if (pPath != null){
			this.projectPath		= pPath;
		} else {
			//exception
			throw new OpnetStrongException("Project path not valid");
		}
		
		//get the project path
		if (pName != null){
			this.projectName		= pName;
		} else {			
			//exception
			throw new OpnetStrongException("Project name not valid");
		}
		
		//update the load project flag
		this.isProjecLoaded			= true;
		
	} // End boolean loadProject
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 */
	public void setNetworksMap() throws OpnetStrongException, OpnetLightException {
		
		//check the previous operation status
		if (!this.isProjecLoaded) {
			throw new OpnetStrongException("Unable to set the networks map: project not loaded");
		}
		
		//initialize the networks map set flag
		this.isNetworksMapSet		= false;
		
		//local attributes
		boolean			completed	= false;
		Vector<String>	files		= new Vector<String>();
		Vector<String>	networks	= new Vector<String>();
		
		//load the file list
		completed					= this.appUtils.loadFileList(this.projectPath);
		
		//get the file list
		if (completed){
			//load the list of valid files names
			files					= this.appUtils.getFileList();
			
			//load the list of unique network names
			networks				= this.appUtils.parseFileNamesList(files, true);
			
			//check the existence of illegal file names
			if (this.appUtils.existIllegalEFFileNames()){
				throw new OpnetLightException("Illegal supplementary environmental files found (see log).", OpnetExceptionClass.MSG_TYPE_WARNING);
			}
			
			//set the networks map (with empty opnet jobs)
			for (int i = 0; i < networks.size(); i++){
				this.networksMap.put(networks.get(i), new HashMap<String, OpnetJob>());				
			}
			
			//load the corresponding ef files lists
			completed				= this.loadEfFileLists();
			
			//check the operation status
			if (!completed){
				throw new OpnetStrongException("Unable to load the supplementary environmental files lists");
			}			
			
		} else {
			//exception
			throw new OpnetStrongException("Unable to load the file list");
		}
				
		//update the networks map set flag
		this.isNetworksMapSet		= true;
		
	} // End boolean setNetworksMap	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * @return the complete list of supplementary environmental files and their included status  
	 */
	public Object[][] getFilesData() throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to get the files data: networks map not set");
		}
		
		//local attributes
		int							tableSize	= this.efFilesNum;
		int							localSize	= 0;
		Object[][]					tableData	= null;
		Iterator<String>			itOutter	= this.networksMap.keySet().iterator();
		Iterator<String>			itInner		= null;
		HashMap<String,OpnetJob>	itemMap		= null;
		String						netName		= "";
		String						fileName	= "";
		boolean						included	= false;
		
		//avoid the empty files situation
		if (this.efFilesNum == 0){
			throw new OpnetStrongException("Supplementary environmental files list not loaded");
		}
		
		//set the table data size
		tableData								= new Object[tableSize][2];
		
		//load the table data
		while (itOutter.hasNext()){
			//get the network name
			netName								= itOutter.next();
			//get the opnetjob vector
			itemMap								= this.networksMap.get(netName);
			//get the inner iterator			
			itInner								= itemMap.keySet().iterator();
			
			//load the data
			while (itInner.hasNext()){
				//check the array bounds
				if (localSize == tableSize){
					throw new OpnetStrongException("Wrong files table data array bounds");
				}				
				//get the file name
				fileName						= itInner.next();
				//get the included value
				included						= itemMap.get(fileName).isEfFileIncluded();
				//set the file name
				tableData[localSize][0]			= fileName;
				//set the included value
				tableData[localSize][1]			= included;                     
				//update the counter
				localSize++;
			}			
		}		
		
		return (tableData);		
		
	} // End getFilesData
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * @param the data for the list of supplementary environmental files and their included status to set 
	 */
	public void setTableData(Object[][] pData) throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to set the files data: networks map not set");
		}
		
		//avoid the null pointer exception
		if (pData == null){
			throw new OpnetStrongException("The supplementary environmental files data to set cannot be null");
		}
		
		//local attributes
		int					tableSize	= pData.length;
		String				fileName	= "";
		String				netName		= "";
		boolean				included	= false;		
				
		//load the data
		for (int i = 0; i < tableSize; i++){
			
			//get the raw data
			fileName					= (String)	pData[i][0];
			included					= (Boolean) pData[i][1];
			//get the net name
			netName						= this.appUtils.parseSingleFileName(fileName);
			
			//locate the map entry
			if (this.networksMap.containsKey(netName)){
				
				//check if the file name exists
				if (this.networksMap.get(netName).containsKey(fileName)){
					//set the included value
					this.networksMap.get(netName).get(fileName).setEfFileIncluded(included);
				} else {
					throw new OpnetStrongException("File name " + fileName + " not found in the networks map");
				}								
				
			} else {
				throw new OpnetStrongException("Network name " + netName + " not found in the networks map");
			}
			
		}	
		
		
	} // End setEFFilesTableData()
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 			the set of network names prepared for the output text area  
	 */
	public String getOutputNetworkNames(){
		
		//local attributes
		String		outNetNames		= "";
		
		//get the console net names
		outNetNames					= this.appUtils.getConsoleNetworkNames();
				
		return(outNetNames);		
		
	} // End String getConsoleNetworkNames
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* PRIVATE METHODS																								*/
	/* ------------------------------------------------------------------------------------------------------------ */
	/**
	 * Load the list of ef files related to the specified network name and the default set of parameters 
	 * for the op_mksim command
	 * 
	 * @return				operation status
	 */
	private boolean loadEfFileLists(){
		
		//status flag
		boolean				status		= false;
		
		//local attributes
		String				netName		= null;
		String				fileName	= null;
		Vector<String>		list		= null;
		Iterator<String>	it			= this.networksMap.keySet().iterator();
		OpnetJob			item		= null;
				
		//initialize the files counter 
		this.efFilesNum					= 0;
		
		//load the ef file list
		while (it.hasNext()){
			
			//get the network name
			netName						= it.next();						
			//get the files list for the network name
			list						= this.appUtils.getEFFiles(netName);						
			//load the list
			for (int i = 0; i < list.size(); i++){
				
				//get the file name
				fileName				= list.get(i);				
				//create the new item
				item					= new OpnetJob();
				//--- load the op_mksim code
				item.setEfFileMKSIMCode(this.appUtils.getDefaultParamsMKSIM(netName));
				//create the inner hash map
				this.networksMap.get(netName).put(fileName, item);				
				//update the ef files counter
				this.efFilesNum++;
			}
									
		}		
		
		//update the flag
		status							= true;
		
		//exit
		return (status);
		
	} // End boolean loadEfFileLists

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Load the default list of sim files command parameters for each simulation file (.sim)   
	 */
	private boolean getSimFilesDTSIMCode(){
		
		//status flag
		boolean						status		= false;
		
		//local attributes
		Iterator<String>			itOutter	= this.networksMap.keySet().iterator();
		HashMap<String,OpnetJob>	itemMap		= null;
		Iterator<String>			itInner		= null;
		OpnetJob					item		= null;
		String						netName		= "";
		String						fileName	= "";
		String						simName		= "";
			
		//avoid the empty element
		if (itOutter == null){ return (status); } 
		
		//load parameters
		while (itOutter.hasNext()){
			
			//get the net name
			netName						= itOutter.next();			
			//get the inner map
			itemMap						= this.networksMap.get(netName);
			//get the inner map iterator
			itInner						= itemMap.keySet().iterator();
			
			//load the data
			while (itInner.hasNext()){
				//get the file name
				fileName				= itInner.next();
				//get the opnet job item
				item					= itemMap.get(fileName);
				//get the sim file name
				simName					= item.getSimFileName();
				//load the default sim file parameters
				item.setSimFileDTSIMCode(this.appUtils.getDefaultParamsDTSIM(this.projectPath, simName));
				//set the item
				itemMap.put(fileName, item);
			}	
			
			//set the item map
			this.networksMap.put(netName, itemMap);
			
		}
				
		//update the flag
		status							= true;
		
		//exit
		return (status);
		
	} // End boolean loadEFFilesMKSIMCode
	
	
		
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the projectName */
	public String getProjectName() { return projectName; }
	
	
	
	
} // End class OpnetProject

