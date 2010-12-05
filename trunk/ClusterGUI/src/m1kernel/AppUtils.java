package m1kernel;

//classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
 * @version		2010.1025
 */
public class AppUtils implements IAppUtils {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private String				className					= "";						//to store the name of the class
	private String 				lineSeparator				= "";						//system line separator
	private String				userHome						= "";						//user dir property
	private ISysUtils			sysUtils					= null;						//system utilities class
	private Vector<String>		fileList					= null;						//list of ef files
	private Vector<String>		badFileList					= null;						//list of not valid ef files
	private Vector<String>		networkNames				= null;						//list of network names
	private String[][]			parsedNetworkNames			= null;						//list of parsed network names	
	private File				efFile						= null;						//ef file
	private BufferedReader		efReader					= null;						//ef file reader
	private StringBuffer		efContents					= new StringBuffer("");		//ef file content
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor */
	public AppUtils(ISysUtils pSysUtils){
		
		//get the system line separator
		this.lineSeparator				= System.getProperty("line.separator");
		
		//get the user dir property
		this.userHome					= System.getProperty("user.home");
		
		//get and stores the name of the class
		this.className					= this.getClass().getName();	
		
		//set the system utilities class
		this.sysUtils					= pSysUtils;
		
		//initializes the file list and network names vectors
		this.fileList				= new Vector<String>();
		this.networkNames			= new Vector<String>();
		this.badFileList			= new Vector<String>();
		
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);		
		
	} // End constructor
	

	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/** Load the list of ef files from the path
	 * 
	 * @param 	pPath			path to the ef files 
	 */
	public boolean loadFileList(String pPath){
		
		File		dir				= null; 
		
		//avoid the null pointer exception
		if (pPath == null){ return(false); }
		
		//try to open the file
		try{
			dir						= new File(pPath);
		} catch (NullPointerException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFileList");
			return(false);
		}
		
		//reset the file list
		this.fileList				= new Vector<String>();
		
		//try to load the list
		String[]	localList		= dir.list();
		
		//avoid the null pointer exception
		if (localList == null){
			this.sysUtils.printlnErr("Wrong path or project file name: " + pPath, this.className + ", loadFileList");
			return(false);
		}
		
		if (localList.length == 0){ return(false); }
		
		//load the complete list of correct ef files
		for (int i = 0; i < localList.length; i++){			
			if (localList[i].endsWith(AppUtils.SUFFIX_EF_FILE)){
				
				if (this.checkFileName(localList[i].toString())){
					//correct file name
					this.fileList.add(localList[i].toString());
				} else {
					//wrong file name
					this.badFileList.add(localList[i].toString());
				}
				
			}			
		}		
		
		//load the parsed file names list
		this.parseFileNames();
		
		//exit
		return(true);
		
	} // End load FilesList

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Check if the ef file name is correct according to the OPNET 14.0 file name rules */	
	private boolean checkFileName(String pFileName){
		
		//operation flag
		boolean		correct		= false;
		//local attributes
		//OPNET info: "Scenario name may not contain '-' characters."
		String		splitChar	= "-";
		String[]	stringVec	= null;
		
		stringVec				= pFileName.split(splitChar);
		
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
	
	/** Load the content of the ef file	 
	 *
	 * @param	pFilePath		input file path
	 * @param	pFileName		input file name
	 * @return					operation status
	 */
	public boolean loadFile(String pFilePath, String pFileName){
		
		boolean	extractStatus	= true;
		boolean validPath		= !(pFilePath.equals(null));
		boolean validName		= !(pFileName.equals(null));
		int 	lineNum			= 0;
		
		//check the input file path and name
		if (!validPath || !validName){ return (false); }
		
		//check if the file name is correct
		if (!this.checkFileName(pFileName)){ return(false); }
		
		//try to open the input file
		try{
			this.efFile	= new File(pFilePath,pFileName);
		} catch (NullPointerException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (false);
		}
	
		//check if the file exists
		if (!this.efFile.exists()){
			//the file does not exist
			this.sysUtils.printlnErr("Could not find the file: " + pFilePath + pFileName, this.className + ", loadFile");
			return (false);
		}
		
		//check if the file can be read
		if (!this.efFile.canRead()){
			//the file cannot be read
			this.sysUtils.printlnErr("Could not read the file: " + pFilePath + pFileName, this.className + ", loadFile");			
			return (false);
		}
	
		//try to read the file
		try{
			//create the file buffered reader
			this.efContents			= new StringBuffer("");
			this.efReader			= new BufferedReader(new FileReader(this.efFile)); 
			String	text			= this.efReader.readLine();
		
			while(text != null){				
				//append the line number
				this.efContents.append(" [Line " + ++lineNum + "]  ");
				//append the data
				this.efContents.append(text);
				this.efContents.append(this.lineSeparator);
				//read the next line
				text				= this.efReader.readLine();
			}
		
			//close the reader
			this.efReader.close();
			
		} catch (FileNotFoundException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (false);
		} catch (IOException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", loadFile");
			return (false);	
		}
								
		//return the status of the data processing operation
		return (extractStatus);
		
	} // End loadFile
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the list of ef file names
	 * 
	 *  @return		the operation status
	 */
	private boolean parseFileNames(){
		
		//operation flag
		boolean				status		= false;
		
		//avoid the null pointer exception
		if (this.fileList == null){ return (status); }
		
		//get the list of network names
		//OPNET 14.0 info: "Scenario name may not contain '-' characters."
		String[]			stringVec	= null;
		Iterator<String>	it			= this.fileList.iterator();
		String				item		= "";
		int					filesNum	= this.fileList.size();
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
					//updates counter
					count++;				
				}				
					
			} catch (PatternSyntaxException e){
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", parseFileNames");
			}
		}		
		
		return (status);
		
	} // End parseFileNames
	
/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the list of file names in order to obtain the network names
	 * 
	 * 	@param	pFileList		the input list of file names
	 * 	@param	pUniqueList		the unique list option flag
	 *  @return					the parsed list of network names
	 */
	public Vector<String> parseNetworkNames(Vector<String> pFileList, boolean pUniqueList){
		
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
		
	} // End parseNetworkNames
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** Return the list of network names found */
	public String getConsoleNetworkNames(){
		
		//OPNET info: "Scenario name may not contain '-' characters."
		StringBuffer		netNames	= new StringBuffer("");
		String				names		= new String("");
		int					count		= 0;
		
		//avoid the null pointer exception
		if (this.parsedNetworkNames == null){ return (netNames.toString()); }
		
		//generates the console-style list of file names
		for (int i = 0; i < this.parsedNetworkNames.length; i++){
			// parsedNetworkNames list structure
			// [0]: Project name
			// [1]: Scenario name
			// [2]: Simulation id
						
			// [0] + '-' + [1] = network name
			names						= this.parsedNetworkNames[i][0] + AppUtils.SPLIT_CHAR + this.parsedNetworkNames[i][1];
			
			//check if the network name exists
			if (!this.networkNames.contains(names)){
				//save the network names list
				this.networkNames.add(names);
				//output
				netNames.append(Integer.toString(++count) + ") " + names + this.lineSeparator);
			}
			
		}
	
		//return the list of network names
		return (netNames.toString());	
		
	} // End String getConsoleNetworkNames

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the illegal file names flag */ 
	public boolean existIllegalEFFileNames(){
		
		return(this.badFileList.size() > 0);
		
	} // End existIlLegalEFFileNames
	
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
		defParams.add("-env_db "		+ "'" + this.userHome + AppUtils.DIR_OPNET_ADMIN + AppUtils.FILE_NAME_ENV_DB + "'" + bashEOF);		
		//--- param: net_name	
		defParams.add("-net_name " 	+ pNetName + bashEOF);		
		//--- param: opnet_dir	
		defParams.add("-opnet_dir " 	+ AppUtils.DIR_OPNET_BINS);					
		
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
		arch					= this.runCommand(AppUtils.CMD_ID_UNAME, "-m");
		
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
		
		return(path);
		
	} // End String getCorrectArchPath
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Run the specified command
	 * 
	 * @param		pCmd		the command identifier
	 * @param		pParams		the parameters for the command
	 * @return					the operation output
	 */
	private String runCommand(int pCmd, String pParams){
		
		//output
		String		output		= "";
		
		
		return(output);		
		
	} // End boolean runCommand
	
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/	
	/** @return the fileList */
	public Vector<String> getFileList() { return fileList; }
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the efContents */
	public StringBuffer getEfContents() { return efContents; }


	/** @return the networkNames */
	public Vector<String> getNetworkNames() { return networkNames; }


	/** @return the parsedNetworkNames */
	public String[][] getParsedNetworkNames() { return parsedNetworkNames; }
	
		
} // End class AppUtils
