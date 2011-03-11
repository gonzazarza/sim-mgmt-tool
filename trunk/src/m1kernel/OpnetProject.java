package m1kernel;

//classes
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.InternalException;
import org.ggf.drmaa.SessionFactory;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.JobTemplate;
//exceptions
import m1kernel.exceptions.OpnetExceptionClass;
import m1kernel.exceptions.OpnetHeavyException;
import m1kernel.exceptions.OpnetLightException;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.IOpnetProject;
import m1kernel.interfaces.ISysUtils;

/** 
 * Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2011.0311
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
	private String										lineBreak			= "\n";				//system line separator
	private String										projectName			= "";				//project name
	private String										projectPath			= "";				//project path	
	private HashMap<String,HashMap<String,OpnetJob>>	networksMap			= null;				//project networks map
	private int											efFilesNum			= 0;				//number of ef files
	private ConsoleJob									simFileHelp			= null;				//sim files help
	private Session										queueSession		= null;				//sim queue session
	private HashMap<String,String>						idsInfo				= null;				//sim jobs id info container
	//logic control attributes
	private boolean										isProjecLoaded		= false;			//project load flag
	private boolean										isNetworksMapSet	= false;			//networks map load flag
	private boolean										isRunMKSIMDone		= false;			//op_mksim run flag
	private boolean										isSimHelpLoaded		= false;			//sim file help load flag
	private boolean										isSimSetupDone		= false;			//sims setup flag
	private boolean										isSimSubmitDone		= false;			//sims submit flag	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor 
	 * @param 		pSysUtils				the system utilities class 
	 */
	public OpnetProject(ISysUtils pSysUtils){
		
		//get the class name  
		this.className				= this.getClass().getName(); 
		
		//set the utilities classes
		this.sysUtils				= pSysUtils;
		this.appUtils				= new AppUtils(this.sysUtils);
		
		//informs the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
				
		//load separators
		this.lineBreak				= System.getProperty("line.separator");
		
		//initialize the networks map
		this.networksMap			= new HashMap<String,HashMap<String,OpnetJob>>();
		
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);		
		
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
	 *  @param		pPath					project path
	 *  @param 		pName					project name  
	 * 	@throws 	OpnetHeavyException		if one of the parameters is null
	 *  @throws		OpnetLightException		if there are old sim files in the project directory
	 */
	public void loadProject(String pPath, String pName) throws OpnetHeavyException, OpnetLightException {
	
		//initialize the load project flag
		this.isProjecLoaded			= false;
		
		//get the project name
		if (pPath != null){
			this.projectPath		= pPath;
		} else {
			//exception
			throw new OpnetHeavyException("Project path not valid");
		}
		
		//get the project path
		if (pName != null){
			this.projectName		= pName;
		} else {			
			//exception
			throw new OpnetHeavyException("Project name not valid");
		}
		
		//update the load project flag
		this.isProjecLoaded			= true;
		
		//check if there are old sim files
		if (this.oldSimFilesFound()){
			throw new OpnetLightException("Old sim files found in the project path (this may lead to errors)", OpnetExceptionClass.MSG_TYPE_WARNING);
		}		
		
	} // End boolean loadProject
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 * 
	 * @throws 		OpnetHeavyException		if the previous operation is not applied 
	 * 										or if it is not possible to load the ef files list 
	 */
	public void setNetworksMap() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isProjecLoaded) {
			throw new OpnetHeavyException("Unable to set the networks map: project not loaded");
		}
		
		//initialize the networks map set flag
		this.isNetworksMapSet		= false;
		
		//local attributes  
		boolean			success		= false;
		Vector<String>	files		= new Vector<String>();
		Vector<String>	networks	= new Vector<String>();
		
		//load the file list
		success						= this.appUtils.loadFileList(this.projectPath);
		
		//get the file list
		if (success){
			//load the list of valid files names
			files					= this.appUtils.getFileList();
			
			//load the list of unique network names
			networks				= this.appUtils.parseFileNamesList(files, true);
			
			//set the networks map (with empty opnet jobs)
			for (int i = 0; i < networks.size(); i++){
				this.networksMap.put(networks.get(i), new HashMap<String, OpnetJob>());				
			}
			
			//load the corresponding ef files lists
			success					= this.loadEfFileLists();
			
			//check the operation status
			if (!success){
				throw new OpnetHeavyException("Unable to load the supplementary environmental files lists");
			}			
			
		} else {
			//exception
			throw new OpnetHeavyException("Unable to load the file list");
		}
		
		//update the networks map set flag
		this.isNetworksMapSet		= true;
		
	} // End boolean setNetworksMap	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Run the op_mksim command
	 *
	 * @return								the command output streams as a vector of strings
	 * @throws 		OpnetHeavyException		if the previous operation is not applied
	 * 										or if it is not possible to run the op_mksim command
	 */
	public Vector<String> runMKSIMCmd() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to run the network op_mksim code: networks map not set");
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
		ConsoleJob			item		= null;
		Vector<String>		outVec		= new Vector<String>();
		boolean				selected	= false;
		boolean				success		= false;
		
		
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
		
					//update the selected flag
					if (!selected){
						selected		= true;
					}
					
					//get the net name
					netName				= itSet.next();
					//get the code
					mkSimCode			= this.getNetworkMKSIMCode(netName);
	
					//reset the code buffer
					lineCode			= new StringBuffer("");
					
					//generates a single string from the vector
					if (mkSimCode != null && mkSimCode.size() > 0){
						//iterates over the vector
						itVec			= mkSimCode.iterator();						
						while (itVec.hasNext()){							
							lineCode.append(itVec.next());
							lineCode.append(this.lineBreak);							
						}												
					} else {
						throw new OpnetHeavyException("op_mksim codes vector == null");
					}
					
					//run the op_mksim and load the console output
					item 				= this.appUtils.runCommandMKSim(this.projectPath, lineCode.toString());
					
					if (!item.stderrActive()){
						//save the output
						if (item.stdoutActive()){							
							outVec.add("##############################################" + this.lineBreak);
							outVec.add(" op_mksim for net: " + netName 					+ this.lineBreak);
							outVec.add("##############################################"	+ this.lineBreak);
							outVec.add(item.getStdout());						
						} 
						//load the sim file name
						this.loadSimFileName(netName);
						//set the compiled flag
						this.setNetMKSIMCompiledFlag(netName, true);
						//load the sim file help
						this.loadSimHelp(netName);
					} else {
						//set the compiled flag
						this.setNetMKSIMCompiledFlag(netName, false);
						//throw an exception
						throw new OpnetHeavyException("Unable to run the op_mksim command (see the log file)");
					}
					
				}
				
				//last check!
				if (selected){
					//update run op_mksim done flag
					this.isRunMKSIMDone	= true;
				}
				
				
			} else {
				throw new OpnetHeavyException("Selected networks names set == null");
			}
			
		} catch (OpnetHeavyException e) {
			throw new OpnetHeavyException(e.getMessage());
		}		
		
		//load the default sim jobs params
		success							= this.getSimFilesDTSIMCode();
		
		if (!success){
			throw new OpnetHeavyException("Unable to load the simulation jobs default params (see the log file)");
		}
		
		//return the output
		return (outVec);
		
	} // End Vector<String> runMKSIMCmd
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the complete list of supplementary environmental files and their included status
	 * 
	 * 	@return 							the complete list of files and their included status  
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the environmental files list was not loaded.
	 * 										if there are errors in the files table data array bounds.
	 */
	public Object[][] getFilesData() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to get the files data: networks map not set");
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
			throw new OpnetHeavyException("Supplementary environmental files list not loaded");
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
					throw new OpnetHeavyException("Wrong files table data array bounds");
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
	 * Set the data for the list of supplementary environmental files and their included status
	 * 
	 * @param 	pData						the data for the list of supplementary environmental files and their included status to set 
	 * @throws 	OpnetHeavyException			if the previous operation was not applied.
	 * 										if the pData parameter is null.
	 * 										if it was not possible to set the data.
	 */
	public void setFilesData(Object[][] pData) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to set the files data: networks map not set");
		}
		
		//avoid the null pointer exception
		if (pData == null){
			throw new OpnetHeavyException("The supplementary environmental files data to set cannot be null");
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
					throw new OpnetHeavyException("File name " + fileName + " not found in the networks map");
				}								
				
			} else {
				throw new OpnetHeavyException("Network name " + netName + " not found in the networks map");
			}
			
		}	
		
		
	} // End setFilesData()
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the isIncluded status for the fileName
	 * 
	 * @param		pFileName				the item identifier
	 * @param		pIsIncluded				the item status
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 										if the operation fails for some reason.
	 * 
	 */	
	public void setIncluded(String pFileName, boolean pIsIncluded) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to set the included status: networks map not set");
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
					throw new OpnetHeavyException("File name " + pFileName + " not found in the networks map");
				}
			} else {
				//network name not found
				throw new OpnetHeavyException("Network name " + netName + " not found in the networks map");
			}
						
		} else {
			throw new OpnetHeavyException("Unable to get the net name: wrong file name " + pFileName);
		}		
		
	} // End setIncluded
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 								the set of network names prepared for the output text area  
	 * 
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
	 * Return the number of supplementary environmental files
	 * 
	 * @return 								the length of the files data  (a.k.a. the number of ef files) 
	 */
	public int getFilesDataLength() {
		
		//return the length
		return (this.efFilesNum);
		
	} // End getFilesDataLength	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the list of network names
	 * 
	 * @return 								the set of network names
	 * @throws		 OpnetHeavyException	if the previous operation was not applied.
	 */
	public Set<String> getNetworksNames() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to set get the networks names: networks map not set");
		}
		
		//local attributes
		Set<String>			netNames	=  this.networksMap.keySet();
		
		//return the net names
		return (netNames);
		
	} // End Set<String> getNetworksNames
		
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Return the list of selected networks
	 * 
	 * @return								the set of selected network names
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 				
	 */
	public Set<String> getSelectedNetworksNames() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to get the selected networks names: networks map not set");
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
	 * Return the names of the network names successfully compiled
	 * 
	 * @return 							the set of compiled network names
	 * @throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 
	 */
	public Set<String> getCompiledNetworksNames() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isRunMKSIMDone) {
			throw new OpnetHeavyException("Unable to get the compiled networks names: op_mksim command not applied");
		}
		
		//local attributes
		Set<String>					compNetNames	= new HashSet<String>();
		Iterator<String>			itOuter			= this.networksMap.keySet().iterator();
		Iterator<String>			itInner			= null;
		HashMap<String,OpnetJob>	itemOuter		= null;
		String						netName			= "";
		String						fileName		= "";
		boolean						compiled		= false;
		
		//check the network compiled status
		while (itOuter.hasNext()){
			//reset the compiled flag
			compiled								= false;
			//get the net name
			netName									= itOuter.next();
			//get the net hash map
			itemOuter								= this.networksMap.get(netName);
			//get the inner iterator
			itInner									= itemOuter.keySet().iterator();
			//check the status of the entire file names for each list
			while (itInner.hasNext()){
				//get the file name
				fileName							= itInner.next();
				//check the compiled status
				if (itemOuter.get(fileName).isEfFileMKSIMCompiled()){
					compiled						= true;
					break;
				}
			}
			//add the net name if at least one file name is compiled
			if (compiled){
				compNetNames.add(netName);
			}
			
		}
		
		//return the compiled net names
		return(compNetNames);
		
	} // End Set<String> getCompiledNetworksNames

	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Return the names of the jobs for the sim files successfully compiled
	 * 
	 * @return 							the set of jobs for the compiled sim files
	 * @throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 									if there is an error finding the sim file.
	 */
	public Set<String> getCompiledSimJobsNames() throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isRunMKSIMDone) {
			throw new OpnetHeavyException("Unable to get the compiled sim job names: op_mksim command not applied");
		}
		
		//local attributes
		Set<String>					compSimNames	= new HashSet<String>();
		Iterator<String>			itOuter			= this.networksMap.keySet().iterator();
		Iterator<String>			itInner			= null;
		HashMap<String,OpnetJob>	itemOuter		= null;
		String						netName			= "";
		String						fileName		= "";
		OpnetJob					item			= null;
		boolean						compiled		= false;
		boolean						included		= false;
		
		//check the network compiled status
		while (itOuter.hasNext()){
			//get the net name
			netName									= itOuter.next();
			//get the net hash map
			itemOuter								= this.networksMap.get(netName);
			//get the inner iterator
			itInner									= itemOuter.keySet().iterator();
			//check the status of the entire file names map for each list
			while (itInner.hasNext()){
				//get the file name
				fileName							= itInner.next();
				item								= this.networksMap.get(netName).get(fileName);
				compiled							= item.isEfFileMKSIMCompiled();
				included							= item.isEfFileIncluded();
				//save the name
				if (included && compiled){
					//get the sim file name
					compSimNames.add(fileName);
//					compSimNames.add(item.getSimFileName());					
				} else {
					//error
//					throw new OpnetHeavyException("Sim file " + fileName + " included == " + Boolean.toString(included) + ", compiled == " + Boolean.toString(compiled));
				}
				
			}
			
		}
		
		//return the compiled sim files names
		return(compSimNames);
		
	} // End Set<String> getCompiledSimFilesNames
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the content of the specified file
	 * 
	 * @param		pFileName				the name of the file to load
	 * @return								the file content
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 										if it was not possible to read the ef file content.
	 * 
	 */
	public String getEfFileContent(String pFileName) throws OpnetHeavyException {
	
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to load the ef file content: networks map not set");
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
			throw new OpnetHeavyException("Unable to load the ef file content: AppUtils class error");
		}		
		
		//exit
		return(fileContent);
		
	} // End String getEfFileContent
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the code for the op_mksim command for the specified network
	 * 
	 *  @param		pNetName				the network name
	 *  @return								the op_mksim code
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the network wasn't found.
	 */
	public Vector<String> getNetworkMKSIMCode(String pNetName) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to load the network op_mksim code: networks map not set");
		}
		
		//local attributes
		Vector<String>		mkSimCode	= null;
		Iterator<String>	itInner		= null;
		String				fileName	= ""; 
		
		//check the outer map
		if (this.networksMap.containsKey(pNetName)){
			//get the iterator for the network
			itInner						= this.networksMap.get(pNetName).keySet().iterator();
			//get the first op_mksim code (in theory all networks file names share the same code)
			if (itInner.hasNext()){
				fileName				= itInner.next();				
				mkSimCode				= this.networksMap.get(pNetName).get(fileName).getEfFileMKSIMCode();
			}
			
		} else {
			//network not found
			throw new OpnetHeavyException("Network name " + pNetName + " not found in the networks map");
		}		
		
		//return the op_mksim code
		return (mkSimCode);
		
	} // End Vector<String> getNetworkMKSIMCode
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the code for the op_mksim command for the specified network
	 * 
	 *  @param		pNetName				the network name
	 *  @param		pCode					the code to set  
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the network wasn't found.
	 */
	public void setNewNetworkMKSIMCode(String pNetName, Vector<String> pCode) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isNetworksMapSet) {
			throw new OpnetHeavyException("Unable to save the network op_mksim code: networks map not set");
		}
		
		//local attributes
		Iterator<String>	itInner		= null;
		String				fileName	= ""; 
		
		//check the outer map
		if (this.networksMap.containsKey(pNetName)){
			//get the iterator for the network
			itInner						= this.networksMap.get(pNetName).keySet().iterator();
			//get the first op_mksim code (in theory all networks file names share the same code)
			if (itInner.hasNext()){
				fileName				= itInner.next();				
				this.networksMap.get(pNetName).get(fileName).setEfFileMKSIMCode(pCode);
			}
			
		} else {
			//network not found
			throw new OpnetHeavyException("Network name " + pNetName + " not found in the networks map");
		}		
				
	} // End void setNewNetworkMKSIMCode
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** 
	 * Return the help of the op_mksim command
	 * 
	 * 	@return 							the op_mksim command help
 	 * 	@throws 	OpnetHeavyException		if it was not possible to get the command help
	 */
	public String getMKSIMHelp() throws OpnetHeavyException {
		
		//local attributes
		String			help	= "";
		ConsoleJob		result	= this.appUtils.getMKSimHelp();
		
		//check errors
		if (result.stderrActive()){
			//unable to get the help
			throw new OpnetHeavyException("Unable to get the op_mksim help");
		}
		
		//get the op_mksim command help
		if (result.stdoutActive()){
			help				= result.getStdout();
		}
		
		//return the help
		return(help);
		
	} // End String getMKSimHelp
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * 	Return the sim code of every ef file included and compiled correctly for each network name
	 * 
	 * @param		pFileName				the ef file name
	 * @return								the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public Vector<String> getSimFileCode(String pFileName) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isSimSetupDone) {
			throw new OpnetHeavyException("Unable to load the sim file code: sim setup not applied");
		}
		
		//local attributes
		Vector<String>		mkSimCode	= new Vector<String>();
		Vector<String>		itemCode	= null;
		Iterator<String>	itVec		= null;
		String				netName		= ""; 
		OpnetJob			item		= null;
		boolean				included	= false;
		boolean				compiled	= false;
		
		// get the network name from the ef file name
		netName							= this.appUtils.parseSingleFileName(pFileName);
		
		// check the outer map
		if (this.networksMap.containsKey(netName)){

			// get the correct item
			item						= this.networksMap.get(netName).get(pFileName);
			
			// check correctness:
			// 		the sim file exists (the compiled value == true)
			//		the ef file was selected (the included value == true)
			included				= item.isEfFileIncluded();
			compiled				= item.isEfFileMKSIMCompiled();
			
			if (included && compiled){
				//get the code
				itemCode			= item.getSimFileDTSIMCode();
				//parse it into a string
				itVec				= itemCode.iterator();
				while (itVec.hasNext()){
					mkSimCode.add(itVec.next());
				}
				//add an empty line
				mkSimCode.add(this.lineBreak);
			} else {
				//error
				throw new OpnetHeavyException("File " + pFileName + " included == " + Boolean.toString(included) + ", compiled == " + Boolean.toString(compiled));
			}
			
		} else {
			//network not found
			throw new OpnetHeavyException("Network name " + netName + " not found in the networks map");
		}		
		
		//return the op_mksim code
		return (mkSimCode);
		
	} // End Vector<String> getSimFileCode

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * 	Set the sim code of the ef file included and compiled correctly
	 * 
	 * @param		pFileName				the ef file name
	 * @param		pCode					the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public void setNewSimFileCode(String pFileName, Vector<String> pCode) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isSimSetupDone) {
			throw new OpnetHeavyException("Unable to load the new sim file code: sim setup not applied");
		}
		
		//local attributes
		String				netName		= "";
		String[]			splited		= null;
						
		//get the network name
		splited							= pFileName.split(IAppUtils.SPLIT_CHAR);
		if (splited != null && splited.length >= 3){
			netName						= splited[0] + IAppUtils.SPLIT_CHAR + splited[1];			
		} else{
			throw new OpnetHeavyException("Unable to get the network name from the sim file name '" + pFileName + "'");
		}
				
		//check the outer map
		if (this.networksMap.containsKey(netName)){
			
			if (this.networksMap.get(netName).containsKey(pFileName)){
			
				this.networksMap.get(netName).get(pFileName).setSimFileDTSIMCode(pCode);
				
			} else {
				//file name not found
				throw new OpnetHeavyException("File name " + pFileName + " not found in the networks map");
			}	 
			
		} else {
			//network not found
			throw new OpnetHeavyException("Network name " + netName + " not found in the networks map");
		}	
		
	} // End void setNewSimFileCode

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * 	Set the sim code of every ef file included and compiled correctly for each network name
	 * 
	 * @param		pSimFileName			the sim file name
	 * @param		pCode					the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public void setSimFileCode(String pSimFileName, Vector<String> pCode) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isSimSetupDone) {
			throw new OpnetHeavyException("Unable to load the sim file code: sim setup not applied");
		}
		
		//local attributes
		Iterator<String>	itInner		= null;
		String				fileName	= ""; 
		OpnetJob			item		= null;
		String				netName		= "";
		String[]			splited		= null;
		boolean				found		= false;
						
		//get the network name
		splited							= pSimFileName.split(".");
		if (splited != null && splited.length > 1){
			netName						= splited[0];
		} else{
			throw new OpnetHeavyException("Unable to get the network name from the sim file name '" + pSimFileName + "'");
		}
		
		//check the outer map
		if (this.networksMap.containsKey(netName)){
			//get the iterator for the network
			itInner						= this.networksMap.get(netName).keySet().iterator();
			//set the sim file code
			while(itInner.hasNext()){
				fileName				= itInner.next();
				item					= this.networksMap.get(netName).get(fileName);
				//check the sim file name
				if(item.getSimFileName().equals(pSimFileName)){
					this.networksMap.get(netName).get(fileName).setSimFileDTSIMCode(pCode);
					//set the flag
					found				= true;
					//exit
					break;
				} 
			}
			//check the correctness
			if (!found){
				throw new OpnetHeavyException("Unable to set the sim code for the sim file '" + pSimFileName + "'");
			}
			
		} else {
			//network not found
			throw new OpnetHeavyException("Network name " + netName + " not found in the networks map");
		}		
		
		
	} // End void setSimFileCode
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** 
	 * Return the help of the sim file (binary file)
	 * 
	 * 	@return 							the sim file help
 	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
 	 * 										if it was not possible to get the sim file help
	 */
	public String getSimsFileHelp() throws OpnetHeavyException {
		
		//check the previous operation status
		//--- op_mksim done
		if (!this.isRunMKSIMDone){
			throw new OpnetHeavyException("Unable to get the sim files help: op_mksim command not applied");
		}
		//--- sim help loaded
		if (!this.isSimHelpLoaded){
			throw new OpnetHeavyException("Unable to get the sim files help: sim help was not previously loaded");
		}
		
		//local attributes
		String			help		= "";
		
		//check the output
		if (this.simFileHelp.stderrActive()){
			throw new OpnetHeavyException("Unable to get the sim files help");
		}				
		//load the help
		if (this.simFileHelp.stdoutActive()){
			help					= this.simFileHelp.getStdout();
		}
		
		//return the help
		return(help);
		
	} // End String getSimsFileHelp
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Submit the corresponding jobs to the queue
	 * 
	 * @param		pQueueName				the queue to be used
	 * @param		pOpLicNum				the number of Opnet licenses needed 
	 * @param		pJobPriority			the Unix priority of the jobs
	 * @param		pOutDir					the output directory
	 * @param		pErrDir					the error directory
	 * @param		pScrDir					the script directory 
	 * @return								the operations info
	 * @throws		OpnetHeavyException		in case of a DrmaaException	
	 *  									in case of an InternalEception
	 *  									in case of an IllegalArgumentException
	 *  									in case of an OutOfMemory Error 
	 */
	public Vector<String> submitSimJobs(String pQueueName, int pOpLicNum, float pJobPriority, 
										String pOutDir, String pErrDir, String pScrDir) throws OpnetHeavyException {
		
		//check the previous operation status
		if (!this.isRunMKSIMDone) {
			throw new OpnetHeavyException("Unable to get the compiled sim job names: op_mksim command not applied");
		}
		
		if (!this.isSimSetupDone) {
			throw new OpnetHeavyException("Unable to get the compiled sim job names: sim jobs not set");
		}
		
		//avoid null pointer exception
		if (pQueueName 	== null){ throw new OpnetHeavyException("Unable to proceed: pQueueName == null"); }
		
		//clear flag
		this.isSimSubmitDone						= false;
		
		//general attributes 	
		Iterator<String>			itOuter			= null;
		Iterator<String>			itInner			= null;
		HashMap<String,OpnetJob>	itemOuter		= null;
		String						netName			= "";
		String						fileName		= "";
		OpnetJob					item			= null;
		boolean						compiled		= false;
		boolean						included		= false;
		//drmaa attributes
		Vector<String>				jobsInfo		= new Vector<String>();
		SessionFactory				factory			= SessionFactory.getFactory();
		JobTemplate 				jt				= null;
		String						uniqueShId		= "NO-SH-ID";
		String 						id				= "NO-ID";
		String						script_name		= "NO-NAME";
			
		try{
			
			//get the set of compiled networks
			itOuter									= this.networksMap.keySet().iterator();

			//get session
			this.queueSession						= factory.getSession();

			//init ids info container
			this.idsInfo							= new HashMap<String, String>();
			
			//initialize the DRMAA session
			this.queueSession.init(null);
			
			//init the job template
			jt										= this.queueSession.createJobTemplate();
			
			//check the network compiled status
			while (itOuter.hasNext()){
				
				//get the net name
				netName								= itOuter.next();
				//get the net hash map
				itemOuter							= this.networksMap.get(netName);
				//get the inner iterator
				itInner								= itemOuter.keySet().iterator();
			
				//check the status of the entire file names map for each list
				while (itInner.hasNext()){
					//get the file name
					fileName						= itInner.next();
					item							= this.networksMap.get(netName).get(fileName);
					compiled						= item.isEfFileMKSIMCompiled();
					included						= item.isEfFileIncluded();
				
					//ok, submit the job
					if (included && compiled){
												
						//get the unique id for the shell script name
						uniqueShId					= UUID.randomUUID().toString();

						//set job name
						jt.setJobName(fileName);
						
						//set the paths if necessary
						if (pScrDir != null){ 
							script_name				= this.appUtils.newGenericBashScript(pScrDir, item.getSimFileDTSIMCode(), uniqueShId);
						} else {
							script_name				= this.appUtils.newGenericBashScript(null, item.getSimFileDTSIMCode(), uniqueShId);
						}
						
						if (pOutDir != null){ jt.setOutputPath(pOutDir); }
						if (pErrDir != null){ jt.setErrorPath(pErrDir); }
						
						jt.setNativeSpecification("-l opnet_licenses=" + Integer.toString(pOpLicNum));
						jt.setNativeSpecification("-q " + pQueueName);
						jt.setNativeSpecification("-p " + Float.toString(pJobPriority));
												
						jt.setRemoteCommand("sh");
						jt.setArgs(new String[] {script_name});
						
						id = this.queueSession.runJob(jt);
										
						//save the id
						this.idsInfo.put(id, fileName);
												
						//save the job name
						jobsInfo.add("Job " + id + " (" + fileName + ") submited succesfully");			
												
					} //end if included and compiled
					
				} //end inner while
				
			} // end outer while
			
			//destroy the job template
			this.queueSession.deleteJobTemplate(jt);
			
			//finalize the DRMAA session (it does not affect the jobs)
			//TODO
//			this.queueSession.exit();

			
		} catch (OutOfMemoryError e){
			throw new OpnetHeavyException("OutOfMemory Error: " + e.getMessage());
		} catch (DrmaaException e){
			throw new OpnetHeavyException("DRMAA Exception: " + e.getMessage());
		} catch (InternalException e){
			throw new OpnetHeavyException("Internal Exception: " + e.getMessage());
		} catch (IllegalArgumentException e){
			throw new OpnetHeavyException("Illegal Argument Exception: " + e.getMessage());
		} 
		
		//update completion flag
		this.isSimSubmitDone						= true;
		
		//return the operations status
		return(jobsInfo);
		
	} // End Vector<String> submitSimJobs
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Remove the bash script files from the specified directory
	 * 
	 * @param		pScrPath				the path of the bash script files
	 * @return								the number of bash files removed
	 * @throws		OpnetHeavyException		If errors removing the scripts or null path
	 *
	 */
	public int removeOldScripts(String pScrPath) throws OpnetHeavyException {
		
		//avoid the null pointer exception
		if (pScrPath == null){
			throw new OpnetHeavyException("Unable to call the remove scripts: pScrPath == null");
		}
		
		//local attributes
		int		filesNum		= 0;
		
		//calls the remove method from the utilities class
		filesNum				= this.appUtils.removeScriptFiles(pScrPath);
		
		//return the number of files removed
		return(filesNum);
		
	} // End int removeOldScripts
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Remove the sim files from the specified directory
	 * 
	 * @param		pScrPath				the path of the sim files
	 * @return								the number of sim files removed
	 * @throws		OpnetHeavyException		If errors removing the sim or null path
	 *
	 */
	public int removeOldSims(String pScrPath) throws OpnetHeavyException {
		
		//avoid the null pointer exception
		if (pScrPath == null){
			throw new OpnetHeavyException("Unable to call the remove sims: pScrPath == null");
		}
		
		//local attributes
		int		filesNum		= 0;
		
		//calls the remove method from the utilities class
		filesNum				= this.appUtils.removeSimFiles(pScrPath);
		
		//return the number of files removed
		return(filesNum);
		
	} // End int removeOldSims
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* PRIVATE METHODS																								*/
	/* ------------------------------------------------------------------------------------------------------------ */
	/**
	 * Load the list of ef files related to the specified network name and the default set of parameters 
	 * for the op_mksim command
	 * 
	 * @return								operation status
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
	 * Check the existence of old sim files in the project directory
	 * 
	 * @return								the operation status
	 */
	private boolean oldSimFilesFound(){
		
		//local attributes
		boolean			found			= false;
		Vector<String>	simList			= null;
		
		//get the sim files list
		simList							= this.appUtils.getSimFilesList(this.projectPath);
		
		//check the correctness
		if (simList != null && simList.size() > 0){
			found						= true;
		}
		
		//return the operation status
		return(found);
		
	} // End boolean oldSimFilesFound

	/* ------------------------------------------------------------------------------------------------------------ */

	/** 
	 * Set the efFileMKSIMCompiled flag for the specified network name
	 * 
	 * @param		pNetName				the net name
	 * @param 		pFlag					the flag value to set
	 * @return								the operation status
	 * 
	 */
	private boolean setNetMKSIMCompiledFlag(String pNetName, boolean pFlag){
			
		//local attributes
		boolean				opStatus	= false;
		String				fileName	= null;
		Iterator<String>	it			= null;
		
		//avoid the null pointer exception
		if (pNetName == null){ return(opStatus); }
		
		//set the iterator 
		it								= this.networksMap.get(pNetName).keySet().iterator();
		
		//set the flags
		while (it.hasNext()){
			fileName					= it.next();
			this.networksMap.get(pNetName).get(fileName).setEfFileMKSIMCompiled(pFlag);
		}	
		
		//update the status flag
		opStatus						= true;
		
		//return the status
		return(opStatus);
		
	} // End boolean setNetMKSIMCompiledFlag
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Load the default list of sim files command parameters for each simulation file (.sim)   
	 * 
	 * @return								the operation status
	 * 
	 */
	private boolean getSimFilesDTSIMCode(){
		
		//status flag
		boolean						status		= false;
		//process flag
		this.isSimSetupDone						= false;
		
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
				item.setSimFileDTSIMCode(this.appUtils.getDefaultParamsDTSIM(this.projectPath, simName, fileName));
				//set the item
				itemMap.put(fileName, item);
			}	
			
			//set the item map
			this.networksMap.put(netName, itemMap);
			
		}
		
		//update the process flag
		this.isSimSetupDone				= true;
		
		//update the flag
		status							= true;
		
		//exit
		return (status);
		
	} // End boolean loadEFFilesMKSIMCode
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Set the sim file name for the pNetName element in the local networks map
	 * 
	 * @param		pNetName					the network name
	 * 
	 */
	private void loadSimFileName(String pNetName) throws OpnetHeavyException {
		
		//local attributes
		Vector<String>		simList		= null;
		Iterator<String>	simIt		= null;
		String				simFile		= "";
		String				splitStr	= ".";
		boolean				done		= false;
		boolean				okName		= false;
		boolean				okSuffix	= false;
		Iterator<String>	innerIt		= null;
		String				fileName	= "";
		
		//get the sim files list
		simList							= this.appUtils.getSimFilesList(this.projectPath);
		
		//check the file list
		if (simList == null || simList.size() == 0){
			//log the error
			this.sysUtils.printlnErr("No sim files found!", this.className + ", loadSimFileName");
			//throw and exception
			throw new OpnetHeavyException("Unable to set the sim file name (no sim files found)");
		}
		
		//set the iterator
		simIt							= simList.iterator();
		
		//get the correct file name
		while (simIt.hasNext()){
			//get the sim file name
			simFile						= simIt.next();
			
			//check the parts
			okName						= simFile.startsWith(pNetName + splitStr); 
			okSuffix					= simFile.endsWith(AppUtils.SUFFIX_SIM_FILE);
			//check the correctness 
			if (okName && okSuffix){
				//set the name for every object in the corresponding hash
				innerIt					= this.networksMap.get(pNetName).keySet().iterator();
				while (innerIt.hasNext()){
					fileName			= innerIt.next();
					this.networksMap.get(pNetName).get(fileName).setSimFileName(simFile);
				}				
				//update the flag
				done					= true;
				//exit
				break;
			}			 			
		}
		
		//check the correctness
		if (!done){
			throw new OpnetHeavyException("Unable to set the sim file name (no matching file found)");
		}
		
	} // End void loadSimFileName
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** 
	 * Load the sim file help of the sim file
	 * 
	 * @param		pSimFileName				the net name
	 */
	private void loadSimHelp(String pNetName) {

		//local attributes
		Iterator<String>	itInner			= this.networksMap.get(pNetName).keySet().iterator();
		String				fileName		= "";
		String				simFileName		= "";
		ConsoleJob			output			= null;

		//update the flag
		this.isSimHelpLoaded				= false;
		
		//try to load the sim file help
		while (itInner.hasNext()){
			//get the file name
			fileName						= itInner.next();
			//check the compiled status
			if (this.networksMap.get(pNetName).get(fileName).isEfFileMKSIMCompiled()){
				//get the sim file name
				simFileName					= this.networksMap.get(pNetName).get(fileName).getSimFileName();
				//get the sim file help
				output						= this.appUtils.getSimFileHelp(this.projectPath, simFileName);					
			}			
		}
		
		//save the help
		this.simFileHelp					= output;
		//update the flag
		this.isSimHelpLoaded				= true;
		
	} // End void getSimsFileHelp
		
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the projectName */
	public String getProjectName() { return projectName; }

	/** @return the isRunMKSIMDone */
	public boolean isRunMKSIMDone() { return isRunMKSIMDone; }

	/** @return the queueSession */
	public Session getQueueSession() { return queueSession; }

	/** @return the idsInfo */
	public HashMap<String, String> getIdsInfo() { return idsInfo; }

	/** @return the isSimSubmitDone */
	public boolean isSimSubmitDone() { return isSimSubmitDone; }
	
			
} // End class OpnetProject

