package m1kernel;

//classes
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
//exceptions
import m1kernel.exceptions.OpnetStrongException;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.IOpnetProject;
import m1kernel.interfaces.ISysUtils;

/** 
 * Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1102
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
	private String										lineBreak			= "\n";				//system line separator
	private String										projectName			= "";				//project name
	private String										projectPath			= "";				//project path	
	private HashMap<String,HashMap<String,OpnetJob>>	networksMap			= null;				//project networks map
	private int											efFilesNum			= 0;				//number of ef files
	//logic control attributes
	private boolean										isProjecLoaded		= false;			//project load flag
	private boolean										isNetworksMapSet	= false;			//networks map load flag
	private boolean										isRunMKSIMDone		= false;			//op_mksim run flag
	private boolean										isSimSetupDone		= false;			//sims setup flag
	private boolean										isSimSubmitDone		= false;			//sims submit flag
	private boolean										isQueueRunning		= false;			//queue checks run flag
	
	
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
		this.lineBreak				= System.getProperty("line.separator");
		
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
	public void setNetworksMap() throws OpnetStrongException {
		
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
	 * Run the op_mksim command
	 * 
	 * @return				the output stream in a string 
	 */
	public String runMKSIMCmd() throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to run the network op_mksim code: networks map not set");
		}
		
		//initialize run op_mksim done flag
		this.isRunMKSIMDone				= false;
				
		//local attributes
		Set<String>			selNetNames	= new HashSet<String>();
		Iterator<String>	itSet		= null;
		String				netName		= null;
		Vector<String>		mkSimCode	= null;
		Iterator<String>	itVec		= null;
		StringBuffer		lineCode	= null;
		StringBuffer		outBuffer	= new StringBuffer("");
		String				output		= null;
		
		//get the list of selected networks names
		try {			
			//get the names
			selNetNames					= this.getSelectedNetworksNames();			
			//check the net names status
			if (selNetNames != null){
				
				//get the iterator
				itSet					= selNetNames.iterator();				
				//get the op_mksim code and runs the op_mksim command
				while (itSet.hasNext()){
		
					//get the net name
					netName				= itSet.next();
					//get the code
					mkSimCode			= this.getNetworkMKSIMCode(netName);
	
					//reset the code buffer
					lineCode			= new StringBuffer("");
					
					//generates a single string from the vector
					if (mkSimCode != null && mkSimCode.size() >0){
						//iterates over the vector
						itVec			= mkSimCode.iterator();						
						while (itVec.hasNext()){							
							lineCode.append(itVec.next());
							lineCode.append(this.lineBreak);							
						}												
					} else {
						throw new OpnetStrongException("op_mksim codes vector == null");
					}
					
					//run the op_mksim
					output				= this.appUtils.runCommandMKSim(this.projectPath, lineCode.toString());
					
					//load the output
					if (output != null){
						outBuffer.append(output);
					} else {
						throw new OpnetStrongException("There were errors running the op_mksim command");
					}
				}				
				
			} else {
				throw new OpnetStrongException("Selected networks names set == null");
			}
			
		} catch (OpnetStrongException e) {
			throw new OpnetStrongException(e.getMessage());
		}		
		
		//update run op_mksim done flag
		this.isRunMKSIMDone				= true;
		
		//return the output
		return (outBuffer.toString());
		
	} // End String runMKSIMCmd
	
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
	public void setFilesData(Object[][] pData) throws OpnetStrongException {
		
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
		
		
	} // End setFilesData()
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the isIncluded status for the fileName
	 * 
	 * @param		pFileName			the item identifier
	 * @param		pIsIncluded			the item status
	 * 
	 */	
	public void setIncluded(String pFileName, boolean pIsIncluded) throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to set the included status: networks map not set");
		}
		
		//local attributes
		String						netName		= this.appUtils.parseSingleFileName(pFileName);
		
		if (netName != null){
			
			//check the outer map
			if (this.networksMap.containsKey(netName)){
				//check the inner map
				if (this.networksMap.get(netName).containsKey(pFileName)){
					
					//set the included status
					this.networksMap.get(netName).get(pFileName).setEfFileIncluded(pIsIncluded);
					
				} else {
					//file name not found
					throw new OpnetStrongException("File name " + pFileName + " not found in the networks map");
				}
			} else {
				//network name not found
				throw new OpnetStrongException("Network name " + netName + " not found in the networks map");
			}
						
		} else {
			throw new OpnetStrongException("Unable to get the net name: wrong file name " + pFileName);
		}		
		
	} // End setIncluded
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 			the set of network names prepared for the output text area  
	 */
	public String getOutputNetworkNames(){
				
		//local attributes
		String		outNetNames		= null;
		
		//get the console net names
		outNetNames					= this.appUtils.getConsoleNetworkNames();

		//avoid the null pointer exception
		if (outNetNames == null){
			outNetNames				= "";
		}
		
		//return the console net names
		return(outNetNames);		
		
	} // End String getConsoleNetworkNames
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * @return the length of the files data  (a.k.a. the number of ef files) 
	 */
	public int getFilesDataLength() {
		
		//return the length
		return (this.efFilesNum);
		
	} // End getFilesDataLength	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * @return the set of network names
	 */
	public Set<String> getNetworksNames() throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to set get the networks names: networks map not set");
		}
		
		//local attributes
		Set<String>			netNames	=  this.networksMap.keySet();
		
		//return the net names
		return (netNames);
		
	} // End Set<String> getNetworksNames
		
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * @return the set of selected network names
	 */
	public Set<String> getSelectedNetworksNames() throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to set get the networks names: networks map not set");
		}
		
		//local attributes
		Set<String>					selNetNames	= new HashSet<String>();
		Iterator<String>			itOuter		= this.networksMap.keySet().iterator();
		Iterator<String>			itInner		= null;
		HashMap<String,OpnetJob>	itemOuter	= null;
		String						netName		= "";
		String						fileName	= "";
		boolean						included	= false;
		
		//check the network selected status
		while (itOuter.hasNext()){
			//reset the included flag
			included							= false;
			//get the net name
			netName								= itOuter.next();
			//get the net hash map
			itemOuter							= this.networksMap.get(netName);
			//get the inner iterator
			itInner								= itemOuter.keySet().iterator();
			//check the status of the entire file names for each list
			while (itInner.hasNext()){
				//get the file name
				fileName						= itInner.next();
				//check the included status
				if (itemOuter.get(fileName).isEfFileIncluded()){
					included					= true;
					break;
				}
			}
			//add the net name if at least one file name is included
			if (included){
				selNetNames.add(netName);
			}
			
		}
		
		
		//return the selected net names
		return(selNetNames);
		
	} // End Set<String> getSelectedNetworksNames()

	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the content of the specified file
	 * 
	 * @param	pFileName			the name of the file to load
	 * 
	 */
	public String getEfFileContent(String pFileName) throws OpnetStrongException {
	
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to load the ef file content: networks map not set");
		}	
		
		//local attributes
		String		fileContent		= "";
		boolean		completed		= false;
		
		//load the file
		completed					= this.appUtils.loadFileContent(this.projectPath, pFileName);
		
		//get the content
		if (completed){			
			fileContent				= this.appUtils.getEfContents().toString();			
		} else {
			//load error
			throw new OpnetStrongException("Unable to load the ef file content: AppUtils class error");
		}		
		
		//exit
		return(fileContent);
		
	} // End String getEfFileContent
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the code for the op_mksim command for the specified network
	 * 
	 *  @param			pNetName	the network name
	 *  @return						the op_mksim code
	 */
	public Vector<String> getNetworkMKSIMCode(String pNetName) throws OpnetStrongException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetStrongException("Unable to load the network op_mksim code: networks map not set");
		}
		
		//local attributes
		Vector<String>		mkSimCode	= null;
		Iterator<String>	itInner		= null;
		String				fileName	= ""; 
		
		//check the outer map
		if (this.networksMap.containsKey(pNetName)){
			//get the iterator for the network
			itInner						= this.networksMap.get(pNetName).keySet().iterator();
			//get the first op_mksim code (in theory all the networks file name shared the same code)
			if (itInner.hasNext()){
				fileName				= itInner.next();				
				mkSimCode				= this.networksMap.get(pNetName).get(fileName).getEfFileMKSIMCode();
			}
			
		} else {
			//network not found
			throw new OpnetStrongException("Network name " + pNetName + " not found in the networks map");
		}		
		
		//return the op_mksim code
		return (mkSimCode);
		
	} // End Vector<String> getNetworkMKSIMCode
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the op_mksim command help */
	public String getMKSIMHelp(){
		
		//local attributes
		String			help	= this.appUtils.getMKSimHelp();
		
		//return the help
		return(help);
		
	} // End String getMKSimHelp
	
	
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

