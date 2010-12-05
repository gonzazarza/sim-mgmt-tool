package m1kernel.interfaces;

//classes
import java.awt.Color;
import java.util.Vector;


/** 
 * Interface to access to the application oriented utilities class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1102
 */
public interface IAppUtils {

	/*	
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================
	*/
	static final String		SUFFIX_PROJECT		= ".project";										//opnet project suffix
	static final String		SUFFIX_EF_FILE		= ".ef";											//opnet ef file suffix
	//colors used in the application
	static final Color		COLOR_DONE			= new Color(034, 139, 034);							//done status cell color
	static final Color		COLOR_FAIL			= new Color(178, 034, 034);							//fail status cell color
	static final Color		COLOR_NOT_APPLIED	= new Color(211, 211, 211);							//not applied status cell color
	//arch type identifiers
	static final int		MACHINE_UNKNOWN		= 0;												//machine arch unknown 
	static final int		MACHINE_32_BITS		= 1;												//machine arch 32 bits
	static final int		MACHINE_64_BITS		= 2;												//machine arch 64 bits
	//opnet 14.0 default settings
	static final String		SPLIT_CHAR			= "-";												//opnet 14.0 file name split char
	static final String		FILE_NAME_ENV_DB	= "env_db14.0";										//opnet 14.0 env_db file name
	static final String		CMD_OP_MKSIM		= "op_mksim";										//opnet 14.0 op_mksim command
	static final String		SUFFIX_SIM_FILE		= ".sim";											//opnet 14.0 sim file suffix
	static final String		DIR_OPNET_ADMIN		= "/op_admin/";										//opnet 14.0 admin dir
	static final String		DIR_OPNET			= "/usr/opnet/";									//opnet 14.0 dir
	static final String		DIR_OPNET_BINS		= "/usr/opnet/14.0.A/sys/unix/bin/";				//opnet 14.0 binaries dir
	static final String		DIR_OPNET_LIB		= "/usr/opnet/14.0.A/sys/unix/lib/";				//opnet 14.0 binaries lib
	static final String		DIR_OPNET64			= "/usr/opnet/14.0.A/sys/pc_intel_linux64/bin/";	//opnet 14.0 64 bits system path
	static final String		DIR_OPNET32			= "/usr/opnet/14.0.A/sys/unix/bin/";				//opnet 14.0 32 bits system path
	static final String		DIR_OPNET64_LIB		= "/usr/opnet/14.0.A/sys/pc_intel_linux64/lib/";	//opnet 14.0 64 bits system lib
	static final String		DIR_OPNET32_LIB		= "/usr/opnet/14.0.A/sys/unix/lib/";				//opnet 14.0 32 bits system lib
	
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/** Load the list of ef files from the path
	 * 
	 * @param 	pPath			path to the ef files
	 *  
	 * @return					the operation status
	 */
	public boolean loadFileList(String pPath);
	
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
	
	/** Return the list of network names found */
	public String getConsoleNetworkNames();
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the illegal file names flag */ 
	public boolean existIllegalEFFileNames();
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Return the default set of parameters for the op_mksim command
	 * 
	 * @param		pNetName		the network name
	 * @return						the set of default params
	 * 
	 */
	public Vector<String> getDefaultParamsMKSIM(String pNetName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Return the default set of parameters for the sim files
	 * 
	 * @param		pSimPath		the simulation file path
	 * @param		pSimName		the simulation file name
	 * @return						the set of default params
	 * 
	 */
	public Vector<String> getDefaultParamsDTSIM(String pSimPath, String pSimName);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**	@return the list of ef files for the specified network name */
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
	 * @return							the console output
	 */
	public String runCommandMKSim(String pPath, String pCmdAndParamsList);
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the help for the op_mksim command */
	public String getMKSimHelp();
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the help for the file pSimFileName */
	public String getSimFileHelp(String pSimFileNameAndPath);
	
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
