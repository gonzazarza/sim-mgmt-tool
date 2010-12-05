package m1kernel.interfaces;

/** 
 * Interface to access to the system utilities class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public interface ISysUtils {
	
	
	/*	
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================
	*/
	static final String 		LOG_FILE_NAME				= "ClusterGUI.log";				//log file name
	static final String			DIR_OPT_USER_HOME			= "/home/gzarza/";				//optional user home dir
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/	
	/** Prints the error string and the eol on the console and (or) the log file 
	 * 
	 * @param text			Error text string
	 * @param className		Name of the invoking class
	 */
	void printlnErr(String text, String className);
	
	
	/** Prints the message string and the eol on the console and (or) the log file
	 * 
	 * @param text			Message text string
	 * @param className		Name of the invoking class
	 */
	void printlnOut(String text, String className);
	
	
	/** Prints the warning string and the eol on the console and (or) the log file
	 * 
	 * @param text			Warning text string
	 * @param className		Name of the invoking class 
	 */
	void printlnWar(String text, String className);
	
	
	/** Prints the error string on the console and (or) the log file
	 * 
	 * @param text			Error text string
	 * @param className		Name of the invoking class
	 */
	void printErr(String text, String className);
	
	
	/** Prints the message string on the console and (or) the log file
	 * 
	 * @param text			Message text string
	 * @param className		Name of the invoking class
	 */
	void printOut(String text, String className);
	
	
	/** Prints the warning string on the console and (or) the log file
	 * 
	 * @param text			warning text string
	 * @param className		Name of the invoking class
	 */
	void printWar(String text, String className);
	
	
	/** Closes the log file and updates its flag */
	void closeLogFile();
	
	
	/*
	================================================================================================================== 
	Getters y Setters																										
	==================================================================================================================	
	*/
	/** @return the existErrors */
	boolean existErrors();

	/** @return the existWarnings */
	boolean existWarnings();
	
	/** @return the machineName */
	String getMachineName();

} // End interface IUtils
