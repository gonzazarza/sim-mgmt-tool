package m1kernel;

//io classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//util classes
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
//exceptions
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.ISysUtils;

/** 
 * Application oriented utilities class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1103
 */
public class AppUtils implements IAppUtils {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private ISysUtils						sysUtils				= null;				//system utilities class
	private String							className				= "unknown";		//to store the name of the class
	private String 							lineBreak				= "";				//system line separator
	private String							userHome				= "";				//user dir property	
	private Vector<String>					efFileList				= null;				//list of ef files
	private Vector<String>					badFileList				= null;				//list of not valid ef files
	private Vector<String>					networkNames			= null;				//list of network names
	private Vector<String>					uniqueNetworkNames		= null;				//list of unique network names
	private String[][]						parsedNetworkNames		= null;				//list of parsed network names	
	private HashMap<String,Vector<String>>	efFilesByNetworkNames	= null;				//list of ef files by network names
	private StringBuffer					efContents				= null;				//ef file content
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor */
	public AppUtils(ISysUtils pSysUtils){
		
		//get the system line separator
		this.lineBreak					= System.getProperty("line.separator");
		
		//get the user dir property
		this.userHome					= System.getProperty("user.home");
		
		//get and stores the name of the class
		this.className					= this.getClass().getName();	
		
		//set the system utilities class
		this.sysUtils					= pSysUtils;
		
		//initializes the file list and network names vectors
		this.efFileList				= new Vector<String>();
		this.networkNames			= new Vector<String>();
		this.uniqueNetworkNames		= new Vector<String>();
		this.badFileList			= new Vector<String>();
		
		//other initializations
		this.efFilesByNetworkNames	= new HashMap<String,Vector<String>>();
		this.efContents				= new StringBuffer("");

		//export the correct LD_LD_LIBRARY_PATH
		this.exportLD_LIB_PATH();
		
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);		
		
	} // End constructor
	

	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* LOAD METHODS: LOAD THE SET OF EF FILE LISTS																	*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Load the list of ef files from the path
	 * 
	 * @param 	pPath			path to the ef files 
	 */
	public boolean loadFileList(String pPath){
		
		//status flag
		boolean		status			= false;
		
		//local attributes
		File		dir				= null; 
		
		
		//avoid the null pointer exception
		if (pPath == null){
			this.sysUtils.printlnWar("EF files path null", this.className + ", loadFileList");
			return(status); 
		}
		
		//try to open the file
		try{
			dir						= new File(pPath);
		} catch (NullPointerException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFileList");
			return(status);
		}
		
		//reset the file list
		this.efFileList				= new Vector<String>();
		
		//try to load the list
		String[]	localList		= dir.list();
		
		//avoid the null pointer exception
		if (localList == null){
			this.sysUtils.printlnErr("Wrong path or project file name: " + pPath, this.className + ", loadFileList");
			return(status);
		}
		
		if (localList.length == 0){
			this.sysUtils.printlnWar("EF Files list lenght == 0", this.className + ", loadFileList");
			return(status); 
		}
		
		//load the complete list of correct ef files
		for (int i = 0; i < localList.length; i++){			
			if (localList[i].endsWith(AppUtils.SUFFIX_EF_FILE)){
				
				if (this.checkFileName(localList[i].toString())){
					//correct file name
					this.efFileList.add(localList[i].toString());
				} else {
					//wrong file name
					this.badFileList.add(localList[i].toString());
				}
				
			}			
		}		
		
		//load the parsed file names list, the network names list and the unique network names list
		if (!this.loadNetworkNamesSetOfLists()){
			this.sysUtils.printlnWar("Unable to load the network names set of lists", this.className + ", loadFileList");
			return (status);
		}
		
		//load the map of ef files associated to the network names
		if (!this.loadNetworksMap()){
			this.sysUtils.printlnWar("Unable to load the networks map", this.className + ", loadFileList");
			return (status);
		}
		
		//update the flag
		status						= true;
		
		//exit
		return(status);
		
	} // End load FilesList

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Load the map of network names and their associated lists of ef files names  
	 * 
	 * @return				operation status
	 */
	private boolean loadNetworksMap(){
		
		//status flag
		boolean			status		= false;
		String			fileName	= "";
		String			netName		= "";
		Vector<String>	auxVec		= null;
		
		//avoid the null pointer exceptions
		if (this.uniqueNetworkNames == null){
			this.sysUtils.printlnWar("Unique network names == null", this.className + ", loadNetworksMap");
			return(status);
		}
		if (this.efFileList == null){
			this.sysUtils.printlnWar("EF files list == null", this.className + ", loadNetworksMap");
			return(status);
		}
		
		//step 1: load the list of unique network names
		for (int i = 0; i < this.uniqueNetworkNames.size(); i++){			
			this.efFilesByNetworkNames.put(this.uniqueNetworkNames.get(i), new Vector<String>());			
		}
		
		status						= true;
		
		//step 2: load the set of ef files names
		for (int i = 0; i < this.efFileList.size(); i++){
			
			fileName				= this.efFileList.get(i);
			netName					= this.parseSingleFileName(fileName);
					
			if (netName != null){
				//check if the file name is loaded
				if (this.efFilesByNetworkNames.containsKey(netName)){
					//get the list
					auxVec			= new Vector<String>();
					auxVec			= this.efFilesByNetworkNames.get(netName);
					//update the list
					auxVec.add(fileName);
					//set the new list
					this.efFilesByNetworkNames.put(netName, auxVec);
					//clear the aux list
					auxVec			= null;
				} else {
					//show a warning message
					this.sysUtils.printlnWar("Net name '" + netName + "' not found in the unique list", this.className + ", loadNetworksMap");
				}
			} else {
				//show an error message and abort the operation
				this.sysUtils.printlnErr("Illegal net name", this.className + ", loadNetworksMap");
				status				= false;	
			}
			
		}		
		
		//exit
		return(status);
		
	} // End loadNetworksMap
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the file name pFileName
	 * 
	 * @param		pFileName	the file name to parse
	 * @return					the parsed file name 
	 */
	public String parseSingleFileName(String pFileName){
		
		//local attributes
		String		parsedName		= null;		
		//OPNET info: "Scenario name may not contain '-' characters."
		String[]	stringVec		= null;
		
		//avoid the null pointer exception
		if (pFileName == null){	
			this.sysUtils.printlnWar("File name to parse == null", this.className + ", parseSingleFileName");
			return(parsedName); 
		}
		
		stringVec					= pFileName.split(AppUtils.SPLIT_CHAR);
		
		//check the file name correctness
		if (stringVec == null || stringVec.length < 3){
			//shows a warning message
			this.sysUtils.printlnWar("Ilegal ef file name '" + pFileName + "'", this.className + ", parseLocalFileName");
		} else {
			//parse the file name
			parsedName				= stringVec[0] + AppUtils.SPLIT_CHAR + stringVec[1]; 
		}		
		
		return (parsedName);		
		
	} // End parseSingleFileName
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	
	
	/** Check if the ef file name is correct according to the OPNET 14.0 file name rules
	 * 
	 * @param	pFileName	the file name to check
	 * @return				operation status  
	 */	
	private boolean checkFileName(String pFileName){
		
		//operation flag
		boolean		correct		= false;
		//local attributes
		//OPNET info: "Scenario name may not contain '-' characters."
		String[]	stringVec	= null;
		
		stringVec				= pFileName.split(AppUtils.SPLIT_CHAR);
		
		//check the file name correctness
		if (stringVec == null || stringVec.length < 3){
			//shows a warning message
			this.sysUtils.printlnWar("Ilegal ef file name '" + pFileName + "'", this.className + ", checkFileName");
			correct				= false;
		} else {
			correct				= true;
		}		
		
		return (correct);
		
	} // End boolean checkFileName
		
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the list of ef file names and load the networks names and the unique networks names lists 
	 * 
	 *  @return		the operation status
	 */
	private boolean loadNetworkNamesSetOfLists(){
		
		//operation flag
		boolean				status		= false;
		
		//avoid the null pointer exception
		if (this.efFileList == null){
			this.sysUtils.printlnWar("EF Files list == null", this.className + ", loadNetworkNamesSetOfLists");
			return (status); 
		}
		
		//get the list of network names
		//OPNET 14.0 info: "Scenario name may not contain '-' characters."
		String[]			stringVec	= null;
		Iterator<String>	it			= this.efFileList.iterator();
		String				item		= "";
		String				netName		= "";
		int					filesNum	= this.efFileList.size();
		int					count		= 0;
		//OPNET 14.0: file name structure (separated by '-')
		// [0]: Project name
		// [1]: Scenario name
		// [2]: Simulation id
		int					opnetSplit	= 3;
		
		//sets the parsed names array
		this.parsedNetworkNames			= new String[filesNum][opnetSplit];
		
		//get the file list
		while (it.hasNext()){
			item						= it.next();
			
			try{
				//split the network char
				// [0]: Project name
				// [1]: Scenario name
				// [2]: Simulation id
				stringVec				= item.split(AppUtils.SPLIT_CHAR);
				
				//check the correct network name
				if (stringVec.length < opnetSplit){
					this.sysUtils.printlnErr("Illegal ef file name: " + item , this.className + ", parseFileNames");					
				} else {
					//project name					
					this.parsedNetworkNames[count][0]	= stringVec[0];
					//scenario name
					this.parsedNetworkNames[count][1]	= stringVec[1];
					//simulation id
					this.parsedNetworkNames[count][2]	= stringVec[2];
					// [0] + '-' + [1] = network name
					netName								= stringVec[0] + AppUtils.SPLIT_CHAR + stringVec[1];
					//load the network names list
					this.networkNames.add(netName);
					//load the unique network names list
					if (!this.uniqueNetworkNames.contains(netName)){
						this.uniqueNetworkNames.add(netName);
					}					
					//updates counter
					count++;					
				}				
					
			} catch (PatternSyntaxException e){
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", parseFileNames");
				return (status);
			}
		}		
		
		status							= true;
		
		return (status);
		
	} // End loadNetworkNamesSetOfLists
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* OUTPUT METHODS: LOAD AND OUTPUT METHODS																		*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Load the content of the ef file	 
	 *
	 * @param	pFilePath		input file path
	 * @param	pFileName		input file name
	 * @return					operation status
	 */
	public boolean loadFileContent(String pFilePath, String pFileName){
		
		//local attributes
		File			efFile			= null;
		BufferedReader	efReader		= null;				
		boolean			extractStatus	= false;
		boolean 		validPath		= !(pFilePath.equals(null));
		boolean 		validName		= !(pFileName.equals(null));
		int 			lineNum			= 0;
		
		//check the input file path and name
		if (!validPath || !validName){ return (extractStatus); }
		
		//check if the file name is correct
		if (!this.checkFileName(pFileName)){ return(extractStatus); }
		
		//try to open the input file
		try{
			efFile						= new File(pFilePath,pFileName);
		} catch (NullPointerException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (extractStatus);
		}
	
		//check if the file exists
		if (!efFile.exists()){
			//the file does not exist
			this.sysUtils.printlnErr("Could not find the file: " + pFilePath + pFileName, this.className + ", loadFile");
			return (extractStatus);
		}
		
		//check if the file can be read
		if (!efFile.canRead()){
			//the file cannot be read
			this.sysUtils.printlnErr("Could not read the file: " + pFilePath + pFileName, this.className + ", loadFile");			
			return (extractStatus);
		}
	
		//try to read the file
		try{
			//create the file buffered reader
			this.efContents				= new StringBuffer("");
			efReader					= new BufferedReader(new FileReader(efFile)); 
			String	text				= efReader.readLine();
		
			while(text != null){				
				//append the line number
				this.efContents.append(" [Line " + ++lineNum + "]  ");
				//append the data
				this.efContents.append(text);
				this.efContents.append(this.lineBreak);
				//read the next line
				text					= efReader.readLine();
			}
		
			extractStatus				= true;
			
			//close the reader
			efReader.close();
			
		} catch (FileNotFoundException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (extractStatus);
		} catch (IOException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (extractStatus);	
		}
								
		//return the status of the data processing operation		
		return (extractStatus);
		
	} // End loadFileContent
		
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the list of file names in order to obtain the network names
	 * 
	 * 	@param	pFileList		the input list of file names
	 * 	@param	pUniqueList		the unique list option flag
	 *  @return					the parsed list of network names
	 */
	public Vector<String> parseFileNamesList(Vector<String> pFileList, boolean pUniqueList){
		
		//avoid the null pointer exception
		if (pFileList == null){
			return (null); 
		}
		
		//get the list of network names
		//OPNET 14.0 info: "Scenario name may not contain '-' characters."
		Vector<String> 		parsedList	= new Vector<String>();
		String[]			stringVec	= null;
		Iterator<String>	it			= pFileList.iterator();
		String				item		= "";
		String				name		= "";
		//OPNET 14.0: file name structure (separated by '-')
		// [0]: Project name
		// [1]: Scenario name
		// [2]: Simulation id
		int					opnetSplit	= 3;
		
		//get the file list
		while (it.hasNext()){
			item						= it.next();
			
			try{
				//split the network char
				// [0]: Project name
				// [1]: Scenario name
				// [2]: Simulation id
				stringVec				= item.split(AppUtils.SPLIT_CHAR);
				
				//check the correct network name
				if (stringVec.length < opnetSplit){
					this.sysUtils.printlnErr("Illegal ef file name: " + item , this.className + ", parseFileNames");					
				} else {
					// [0] + '-' + [1] = network name
					name				= stringVec[0] + AppUtils.SPLIT_CHAR + stringVec[1];
					//check the unique list option
					if (pUniqueList){
						//unique list
						if (!parsedList.contains(name)){
							parsedList.add(name);
						}
					} else {
						//complete list
						parsedList.add(name);
					}
				}				
					
			} catch (PatternSyntaxException e){
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", parseFileNames");
			}
		}	
		
		return(parsedList);
		
	} // End parseFileNamesList
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** Return the list of network names found */
	public String getConsoleNetworkNames(){
		
		//OPNET info: "Scenario name may not contain '-' characters."
		StringBuffer		netNames	= new StringBuffer("");
		int					count		= 0;
		
		//avoid the null pointer exception
		if (this.uniqueNetworkNames == null){ return (netNames.toString()); }
		
		//generates the console-style list of file names
		for (int i = 0; i < this.uniqueNetworkNames.size(); i++){			
			netNames.append(Integer.toString(++count) + ") " + this.uniqueNetworkNames.get(i)+ this.lineBreak);			
		}
	
		//return the list of network names
		return (netNames.toString());	
		
	} // End String getConsoleNetworkNames
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* COMMAND METHODS: RUN AND PARAMS SET METHODS																	*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the default set of parameters for the op_mksim command
	 * 
	 * @param		pNetName		the network name
	 * @return						the set of default params
	 * 
	 */
	public Vector<String> getDefaultParamsMKSIM(String pNetName){
		
		//local attributes
		Vector<String>	defParams		= new Vector<String>(); 
		String			bashEOF			= new String(" \\");
		String			path			= this.getCorrectArchPath();
				
		//------------------------------------------------------------------------------------------------------
		//op_mksim
		//------------------------------------------------------------------------------------------------------
		/* example:	
			/usr/opnet/14.0.A/sys/pc_intel_linux64/bin/op_mksim \
			-env_db '/home/gzarza/op_admin/env_db14.0' \
			-net_name Pattern_analysis-FT_DRB_16x16_torus_comm_patterns \
			-opnet_dir /usr/opnet/		
		*/		
		//--- command 	
		defParams.add(path + AppUtils.CMD_OP_MKSIM + bashEOF);
		//--- param: env_db	
		defParams.add("-env_db "	+ "'" + this.userHome + AppUtils.DIR_OPNET_ADMIN + AppUtils.FILE_NAME_ENV_DB + "'" + bashEOF);		
		//--- param: net_name	
		defParams.add("-net_name " 	+ pNetName 	+ bashEOF);		
		//--- param: c (force recompilation)
		defParams.add("-c " 		+ "true"	+ bashEOF);
		//--- param: opnet_dir	
		defParams.add("-opnet_dir "	+ AppUtils.DIR_OPNET_BINS);					
		
		//return the params list
		return (defParams);		
		
	} // End getDefaultParamsMKSIM
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the default set of parameters for the sim files
	 * 
	 * @param		pSimPath		the simulation file path
	 * @param		pSimName		the simulation file name
	 * @return						the set of default params
	 * 
	 */
	public Vector<String> getDefaultParamsDTSIM(String pSimPath, String pSimName){
		
		//local attributes
		Vector<String>	defParams		= new Vector<String>(); 
		String			bashEOF			= new String(" \\");
				
		//------------------------------------------------------------------------------------------------------
		//sim files
		//------------------------------------------------------------------------------------------------------
		/* example:	
		 *	/file/path/sim_file_name.sim
		 *	-env_db /home/gzarza/op_admin/env_db.14.0 
		 *	-opnet_dir /usr/opnet 
		 *	-noprompt true 
		 *	-ef ef_file_name.ef
		 */		
		 	
		//--- command
		defParams.add(pSimPath		+ pSimName	+ AppUtils.SUFFIX_SIM_FILE  + bashEOF);		
		//--- param: env_db
		defParams.add("-env_db "	+ "'" + this.userHome + AppUtils.DIR_OPNET_ADMIN + AppUtils.FILE_NAME_ENV_DB + "'" + bashEOF);		
		//--- param: opnet_dir
		defParams.add("-opnet_dir " + AppUtils.DIR_OPNET_BINS + bashEOF);		
		//--- param: noprompt
		defParams.add("-noprompt " 	+ "true" + bashEOF);		
		//--- param: ef
		defParams.add("-ef " 		+ pSimName + AppUtils.SUFFIX_EF_FILE);
		
		//return the params
		return(defParams);
		
	} // End getDefaultParamsDTSIM
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Get the correct path for the system architecture
	 * 
	 * @return			the system architecture opnet path
	 * 
	 */
	private String getCorrectArchPath(){
		
		//local attributes
		String			path	= "";
		String			arch	= "";
		//arch outputs
		Vector<String>	m32		= new Vector<String>();
		Vector<String>	m64		= new Vector<String>();
		//--- 32 bits arch
		m32.add("i386");
		m32.add("i686");
		//--- 64 bits arch
		m64.add("x86_64");
		m64.add("amd64");
		m64.add("ia64");		
		
		//run the uname -m command to get the machine time
		try {
			arch				= this.runCommand("uname", "-m");
		} catch (Exception e) {
			//show the error
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", getCorrectArchPath");
			//return the default path
			return(AppUtils.DIR_OPNET_BINS);
		}
		
		//check the bits number
		if (m32.contains(arch)){
			//32 architecture
			path				= AppUtils.DIR_OPNET32;
		} else if(m64.contains(arch)){
			//64 architecture
			path				= AppUtils.DIR_OPNET64;
		} else {
			//unknown architecture
			this.sysUtils.printlnErr("Unknown architecture '" + arch + "'", this.className + ", getCorrrectArchPath");
			//use default path
			path				= AppUtils.DIR_OPNET_BINS;
		}
		
		//return the path
		return(path);
		
	} // End String getCorrectArchPath
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Get the correct opnet lib for the system architecture
	 * 
	 * @return			the system architecture opnet lib dir
	 * 
	 */
	private String getCorrectArchLib(){
		
		//local attributes
		String			path	= "";
		String			arch	= "";
		//arch outputs
		Vector<String>	m32		= new Vector<String>();
		Vector<String>	m64		= new Vector<String>();
		//--- 32 bits arch
		m32.add("i386");
		m32.add("i686");
		//--- 64 bits arch
		m64.add("x86_64");
		m64.add("amd64");
		m64.add("ia64");		
		
		//run the uname -m command to get the machine time
		try {
			arch				= this.runCommand("uname", "-m");
		} catch (Exception e) {
			//show the error
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", getCorrectArchPath");
			//return the default path
			return(AppUtils.DIR_OPNET_BINS);
		}
		
		//check the bits number
		if (m32.contains(arch)){
			//32 architecture
			path				= AppUtils.DIR_OPNET32;
		} else if(m64.contains(arch)){
			//64 architecture
			path				= AppUtils.DIR_OPNET64;
		} else {
			//unknown architecture
			this.sysUtils.printlnErr("Unknown architecture '" + arch + "'", this.className + ", getCorrrectArchPath");
			//use default path
			path				= AppUtils.DIR_OPNET_BINS;
		}
		
		//return the path
		return(path);
		
	} // End String getCorrectArchLib
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Run the specified command
	 * 
	 * @param		pCmdAndParams	the command and its parameters for the command
	 * @return						the operation output
	 */
	private String runCommand(String... pCmdAndParams) throws Exception {
		
		//avoid the null pointer exception
		if (pCmdAndParams == null || pCmdAndParams.length == 0){
			this.sysUtils.printlnErr("The command to run cannot be null or an empty string", this.className + ", runCommand");
			return(null);
		}		
		
		//local attributes
		String			output		= null;				
		String			errors		= null;
		Process			proc		= null;
		
		//execute the command
		try {
			
			//try to execute the command
			proc					= Runtime.getRuntime().exec(pCmdAndParams);
			
			//get the errors
			errors					= this.streamHandler(proc.getErrorStream());
			
			//check the errors and get the output
			if (errors == null){
				//get the output
				output				= this.streamHandler(proc.getInputStream());
			} else {
				throw new Exception("Unable to run the command '" + pCmdAndParams[0] + "'" + this.lineBreak + " >> " + errors);		
			}
			
		} catch (IOException e) {
			//unable to run the command
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", runCommand");
		}
		
		//return the result
		return(output);		
		
	} // End String runCommand
		
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Parse the input stream into a string
	 * 
	 * @param	pStream			the input stream
	 * @return					the resulting string
	 */
	private String streamHandler(InputStream pStream){
		
		//local attributes
		StringBuffer		output		= new StringBuffer("");
		InputStreamReader	isReader	= null;
		BufferedReader		bReader		= null;
		String				line		= null;
		
		//parse the input stream into a string
	    try{
	    	isReader					= new InputStreamReader(pStream);
	    	bReader						= new BufferedReader(isReader);
	    	line						= bReader.readLine();
	    	
	    	if (line != null){
	    	
	    		output.append(line);
	    		line					= bReader.readLine();
	    		
	    		while (line != null){
	    			output.append(this.lineBreak);
		    		output.append(line);
		    		line				= bReader.readLine();
		    	}	    		
	    		
	    	} else {
	    		return(null);
	    	}
	    	
	    } catch (IOException e){
	    	this.sysUtils.printlnErr(e.getMessage(), this.className + ", commandStreamHandler");
	    }
		
		//return the value
		return(output.toString());
		
	} // End String streamHandler
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* OTHER METHODS: ADDITIONAL METHODS																			*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the illegal file names flag */ 
	public boolean existIllegalEFFileNames(){		
		return(this.badFileList.size() > 0);
		
	} // End existIlLegalEFFileNames
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**	@return the list of ef files for the specified network name */
	public Vector<String> getEFFiles(String pNetName){
		
		//local attributes
		Vector<String>	list	= null;
		
		//avoid the null pointer exception
		if (pNetName == null){ return(list); }
		
		//get the correct files list
		if (this.efFilesByNetworkNames.containsKey(pNetName)){
			list				= this.efFilesByNetworkNames.get(pNetName); 
		} else {
			//show a warning
			this.sysUtils.printlnWar("Unable to load the supplementary environmental files lists", this.className + ", getEFFiles");
		}		
		
		return(list);
		
	} // End Vector<String> getEFFiles 

	/* ------------------------------------------------------------------------------------------------------------ */

	/** 
	 * Run the op_mksim command for the pParamsList
	 * 
	 * @param		pParamsList			the list of parameters for the command
	 * @return							the console output
	 */
	public String runCommandMKSim(String pParamsList) throws Exception {
		
		//local attributes
		String		console		= "";
		String		output		= "";
				
		//try to run the command
		console					= this.runCommand(this.getCorrectArchPath() + "op_mksim", pParamsList);
		
		if (console != null){
			output				= console;
		}
		
		//return the status flag
		return(output);
		
	} // End runCommandMKSim

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the help for the op_mksim command */
	public String getMKSimHelp(){
		
		//local attributes
		String		help		= "";
		String		out			= null;
		
		//run op_mksim -help command and get the output
		try {
			out					= this.runCommand(this.getCorrectArchPath() + "op_mksim", "-help");
		} catch (Exception e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", getMKSimHelp");
		}
		
		//check the output
		if (out != null){
			help				= out;
		} else {
			this.sysUtils.printlnErr("Unable to run the 'op_mksim -help' command", this.className + ", getMKSimHelp");
		}
		
		//return the op_mksim help
		return(help);
		
	} // End String getMKSimHelp
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the help for the file pSimFileName */
	public String getSimFileHelp(String pSimFileNameAndPath){
		
		//local attributes
		String		help		= "";
		String		out			= null;
		
		//run simFile -help command and get the output
		try {
			out					= this.runCommand(pSimFileNameAndPath, "-help");
		} catch (Exception e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", getSimFileHelp");
		}
		
		//check the output
		if (out != null){
			help				= out;
		} else {
			this.sysUtils.printlnErr("Unable to run the sim file help command", this.className + ", getSimFileHelp");
		}
		
		//return the sim file help
		return(help);
		
		
	} // End String getSimFileHelp
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Export the LD_LIBRARY_PATH
	 */	
	private void exportLD_LIB_PATH(){
		
		//local attributes
		String 	console				= null;
		String	fileName			= null;
		boolean	deleted				= false;
		
		//try to run the command
		try {
			
			//create the script file
			fileName				= this.newBashScript("export LD_LIBRARY_PATH=" + this.getCorrectArchLib());
			
			//run the command
			console					= this.runCommand("bash", fileName);
			
			//delete the script file
			deleted					= this.deleteBashScript(fileName);
			
			//show the output (in this case correspond to errors)
			if (console != null){
				this.sysUtils.printlnErr(console, this.className + "exportLD_LIB_PATH (command run)");
			} 
			
			//show an error if not deleted
			if (!deleted){
				this.sysUtils.printlnErr("Bash script file '" + fileName + "' not deleted", this.className + ", exportLD_LIB_PATH (delete)");
			}
			
		} catch (Exception e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", exportLD_LIB_PATH (runCommand op)");
		}
		
	} // End exportLD_LIB_PATH
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Create a new bash script file
	 */
	private String newBashScript(String pCmd){
		
		//local attributes
		String 			fileDir			= null;
		File			bashFile		= null;
		PrintWriter		bashWriter		= null;
		StringBuffer	bashScript		= null;
		String			fullFileName	= null;
		
		//use the same file dir than the system log
		fileDir							= this.sysUtils.getLog_file_dir();		
				
		//create the new bash script file
		bashFile		 				= new File(fileDir, "ld_lib_path.sh");		
		
		//load the full file name
		fullFileName					= bashFile.getAbsolutePath();
		
		//if there is no file, try to create it
		if (!bashFile.exists()){
			try {
				bashFile.createNewFile();
			} catch(IOException e) {
				//cannot create the file
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", newBashScript (exists)");
				//abort
				return(fullFileName);
			}
		}
		
		//verify that it can write the file
		if (!bashFile.canWrite()){
			//cannot write the file
			this.sysUtils.printlnErr("Unable to write the bash script file", this.className + ", newBashScript (canWrite)");
			//abort
			return(fullFileName);
		}
		
		//associate the PrintWriter to the log file
		try {
			bashWriter				= new PrintWriter(new FileWriter(bashFile.getPath()));
		} catch(IOException e) {
			//cannot associate the PrintWriter to the file
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", newBashScript (printWriter failed)");
		}
		
		//load the script
		bashScript					= new StringBuffer("");
		bashScript.append("#!/bin/bash");
		bashScript.append(this.lineBreak);
		bashScript.append(this.lineBreak);
		bashScript.append(pCmd);
		bashScript.append(this.lineBreak);
		bashScript.append(this.lineBreak);
		
		//write the file
		bashWriter.write(bashScript.toString());
		bashWriter.flush();
		
		//close the writer
		bashWriter.close();
		
		//return the file name
		return(fullFileName);
		
	} // End newBashScript
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Delete the bash script file pFileName
	 */
	private boolean deleteBashScript(String pFileName){
		
		//local attributes
		boolean		status			= false;
		File		bashFile		= null;
		
		//try to delete the bash script file
		try{
		
			bashFile				= new File(pFileName);
			bashFile.delete();		
			
			status					= true;
			
		} catch (NullPointerException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", deleteBashScript");
			return(status);
		}	
				
		//return the operation status
		return (status);
		
	} // End deleteBashScript
	
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/	
	/** @return the fileList */
	public Vector<String> getFileList() { return efFileList; }
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the efContents */
	public StringBuffer getEfContents() { return efContents; }

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the networkNames */
	public Vector<String> getNetworkNames() { return networkNames; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the uniqueNetworkNames */
	public Vector<String> getUniqueNetworkNames() { return uniqueNetworkNames; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the parsedNetworkNames */
	public String[][] getParsedNetworkNames() { return parsedNetworkNames; }

	
		
} // End class AppUtils
