package m1kernel.interfaces;

//classes
import java.awt.Color;
import java.util.Vector;
import m1kernel.ConsoleJob;
import m1kernel.exceptions.OpnetHeavyException;


/** 
 * Interface to access to the application oriented utilities class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2011.0310
 */
public interface IAppUtils {

	/*	
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================
	*/
	
	//--- app properties status
	/** app properties: status not applied */
	static final String		STAT_NOT_APPLIED	= "....";											
	/** app properties: status done */
	static final String		STAT_DONE			= "DONE";											
	/** app properties: status failed */
	static final String		STAT_FAIL			= "FAILED";										
	/** app properties: status running */
	static final String		STAT_RUNNING		= "Running";										

	//--- colors used in the application
	/** color:  not applied */
	static final Color		COLOR_NOT_APPLIED	= new Color(192, 192, 192);						
	/** color:  done */
	static final Color		COLOR_DONE			= new Color(034, 139, 034);						
	/** color:  fail */
	static final Color		COLOR_FAIL			= new Color(178, 034, 034);						
	/** color:  running */
	static final Color		COLOR_RUNNING		= new Color(070, 130, 180);
	/** color: 	components */
	static final Color		COLOR_COMPONENTS	= new Color(211, 211, 211);
	
	//--- arch type identifiers
	/** arch type: unknown */
	static final int		MACHINE_UNKNOWN		= 0;												
	/** arch type: 32 bits */
	static final int		MACHINE_32_BITS		= 1;												
	/** arch type: 64 bits */
	static final int		MACHINE_64_BITS		= 2;												

	//--- opnet versions
	/**	opnet 14.0.A */
	static final String		OPNET_14_0_A		= "OPNET 14.0.A";
	/**	opnet 14.5 */
	static final String		OPNET_14_5			= "OPNET 14.5";
	/**	opnet 16.0 */
	static final String		OPNET_16_0			= "OPNET 16.0";
	
	//--- opnet 14.0 default settings
	/** opnet 14.0 project suffix */
	static final String		SUFFIX_PROJECT		= ".project";										
	/** opnet 14.0 ef file suffix */
	static final String		SUFFIX_EF_FILE		= ".ef";											
	/** opnet 14.0 sim file suffix */
	static final String		SUFFIX_SIM_FILE		= ".sim";											
	/** opnet 14.0 file name split char */
	static final String		SPLIT_CHAR			= "-";												
	/** opnet 14.0 env_db file name */
	static final String		FILE_NAME_ENV_DB	= "env_db14.0";										
	/** opnet 14.0 op_mksim command */
	static final String		CMD_OP_MKSIM		= "op_mksim";										
	/** opnet 14.0 admin dir */
	static final String		DIR_OPNET_ADMIN		= "/op_admin/";										
	/** opnet 14.0 default dir */
	static final String		DIR_OPNET			= "/usr/opnet/";									
	/** opnet 14.0 binaries dir */
	static final String		DIR_OPNET_BINS		= "/usr/opnet/14.0.A/sys/unix/bin/";
	/** opnet 14.0 32 bits system path */
	static final String		DIR_OPNET32			= "/usr/opnet/14.0.A/sys/unix/bin/";			
	/** opnet 14.0 64 bits system path */
	static final String		DIR_OPNET64			= "/usr/opnet/14.0.A/sys/pc_intel_linux64/bin/";		
	/** opnet 14.0  binaries lib */
	static final String		DIR_OPNET_LIB		= "/usr/opnet/14.0.A/sys/pc_intel_linux/lib/";
	/** opnet 14.0 32 bits system lib */
	static final String		DIR_OPNET32_LIB		= "/usr/opnet/14.0.A/sys/pc_intel_linux/lib/";
	/** opnet 14.0 64 bits system lib */
	static final String		DIR_OPNET64_LIB		= "/usr/opnet/14.0.A/sys/pc_intel_linux64/lib/";					
		
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/** Load the list of ef files from the path
	 * 
	 * @param 	pPath			path to the ef files 
	 * @return					the operation status
	 * 
	 */
	public boolean loadFileList(String pPath);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the list of sim files in the specified path
	 * 
	 * @param		pPath		the path to search
	 * @return 					the list of sim files found in the pPath
	 */
	public Vector<String> getSimFilesList(String pPath);

	/* ------------------------------------------------------------------------------------------------------------ */

	/** Load the content of the ef file	 
	 *
	 * @param	filePath		input file path
	 * @param	fileName		input file name
	 * @return					operation status
	 */
	public boolean loadFileContent(String filePath, String fileName);
		
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the list of file names in order to obtain the network names
	 * 
	 * 	@param	pFileList		the input list of file names
	 * 	@param	pUniqueList		the unique list option flag
	 *  @return					the parsed list of network names
	 */
	public Vector<String> parseFileNamesList(Vector<String> pFileList, boolean pUniqueList);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Return the list of network names found
	 * 
	 * @return					the list of network names  
	 */
	public String getConsoleNetworkNames();
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the illegal file names flag */ 
	public boolean existIllegalEFFileNames();
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Return the default set of parameters for the op_mksim command
	 * 
	 * @param		pNetName		the network name
	 * @return						the set of default parameters 
	 */
	public Vector<String> getDefaultParamsMKSIM(String pNetName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the default set of parameters for the sim files
	 * 
	 * @param		pSimPath		the simulation file path
	 * @param		pSimName		the simulation file name
	 * @param		pFileName		the ef file name
	 * @return						the set of default params
	 */
	public Vector<String> getDefaultParamsDTSIM(String pSimPath, String pSimName, String pFileName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Remove the script files from the specified directory
	 * 
	 * @param		pPath					the directory where remove files
	 * @return								the number of script files removed 
	 * @throw		OpnetHeavyException		If errors during the rm command
	 * 
	 */
	public int removeScriptFiles(String pPath) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**	
	 * Return the list of ef files for the specified network name
	 * 
	 * @param 	pNetName			the network name
	 * @return 						the list of ef files 
	 */
	public Vector<String> getEFFiles(String pNetName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Parse the file name pFileName
	 * 
	 * @param		pFileName	the file name to parse
	 * @return					the parsed file name 
	 */
	public String parseSingleFileName(String pFileName);
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** 
	 * Run the op_mksim command for the pCmdAndParamsList
	 * 
	 * @param		pPath				the project path
	 * @param		pCmdAndParamsList	the command and the list of parameters for the command
	 * @return							the console output and error streams
	 */
	public ConsoleJob runCommandMKSim(String pPath, String pCmdAndParamsList);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the help for the op_mksim command */
	public ConsoleJob getMKSimHelp();
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Return the sim file help
	 * 
	 * @param		pPath				the project path
	 * @param		pSimFileName		the sim file name
	 * @return 							the help for the file pSimFileName 
	 */
	public ConsoleJob getSimFileHelp(String pPath, String pSimFileName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Create a new bash script file (generic)
	 * 
	 * @param	pScriptPath			the script path
	 * @param	pCmd				the command to run
	 * @param 	pId					the job id
	 * @return						the bash script file name
	 */
	public String newGenericBashScript(String pScriptPath, Vector<String> pCmd, String pId);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Delete the bash script file pFileName
	 */
	public boolean deleteBashScript(String pFileName);
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/	
	/** @return the fileList */
	public Vector<String> getFileList();
	
	/** @return the efContents */
	public StringBuffer getEfContents();
	
	/** @return the networkNames */
	public Vector<String> getNetworkNames();
	
	/** @return the uniqueNetworkNames */
	public Vector<String> getUniqueNetworkNames();
	
	/** @return the parsedNetworkNames */
	public String[][] getParsedNetworkNames();
	
		
} // End interface IAppUtils
