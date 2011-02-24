package m1kernel;

//java.io 
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
//java.util
import java.util.Date;
//java.text
import java.text.SimpleDateFormat;
//exceptions
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
//interfaces
import m1kernel.interfaces.ISysUtils;

/** 
 * System oriented utilities class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public class SysUtils implements ISysUtils {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private File				logFile					= null;							//log file
	private PrintWriter 		logFileWriter			= null;							//writer for the log file
	private boolean				logFileOpen				= false;						//log file flag
	private boolean				existErrors				= false;						//errors flag
	private boolean				existWarnings			= false;						//warnings flag
	private SimpleDateFormat	dateFormat				= null;							//date format (output)
	private String				machineName				= "unknown";					//local machine name
	// logic checks
	private boolean				console_output			= true;							//console output flag
	private boolean				log_output				= true;							//log file output flag
	private String				log_file_dir			= ""; 							//log file path
	private boolean 			log_file_classdir		= false;						//log file execution path flag
	private String				className				= null;							//to store the name of the class
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Default constructor */
	public SysUtils(){
		
		//gets the machine name
		this.loadMachineName();
		
		//gets and stores the name of the class
		this.className				= this.getClass().getName();
		
		//tries to load the user home path
		if (System.getProperty("user.home") != null){
			this.log_file_dir		= System.getProperty("user.home");			
			//check the existence of the /op_admin/ directory
			
			if ((new File(log_file_dir + AppUtils.DIR_OPNET_ADMIN)).exists()){
				this.log_file_dir	= this.log_file_dir + AppUtils.DIR_OPNET_ADMIN;
			}
			
		} else {
			this.log_file_dir		= SysUtils.DIR_OPT_USER_HOME;
		}
		
		//creates the log file if necessary
		if (log_output){ 
			this.loadLogFile();
		}
		
		//inform the start of the initialization of the class
		this.printlnOut("... Init: start ...", this.className);
		
		//informs the correct initialization of the class
		this.printlnOut("... Init: DONE! ...", this.className);
		
	} // End constructor
	

	/*	
	================================================================================================================== 
	Finalize																										
	==================================================================================================================
	*/
	/** Finalize method */
	@Override
	public void finalize(){
		//closes the log file
		if (this.logFileOpen){
			this.printlnOut("Closing the log file now...", this.className + ", finalize");
			this.closeLogFile();
		}		
	} // End finalize
	
	
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
	public void printlnErr(String text, String className){	
		if (className != null){
			this.println("err", (text + "  [Class " + className + "] "));
		} else {
			this.println("err", (text));
		}
	} // End printlnErr
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** Prints the message string and the eol on the console and (or) the log file
	 * 
	 * @param text			Message text string
	 */
	public void printlnOut(String text, String className){
		if (className != null){
			this.println("out", (text + "  [Class " + className + "] "));
		} else {
			this.println("out", (text));
		}
	} // End printlnOut
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the warning string and the eol on the console and (or) the log file
	 * 
	 * @param text			Warning text string 
	 */
	public void printlnWar(String text, String className){
		if (className != null){
			this.println("war", (text + "  [Class " + className + "] "));
		} else {
			this.println("war", (text));
		}
	} // End printlnWar
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the error string on the console and (or) the log file
	 * 
	 * @param text			Error text string
	 */
	public void printErr(String text){
		this.print("err", (text));	
	} // End printErr
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the message string on the console and (or) the log file
	 * 
	 * @param text			Message text string
	 */
	public void printOut(String text){
		this.print("out", (text));
	} // End printOut
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the warning string on the console and (or) the log file
	 * 
	 * @param text			warning text string
	 */
	public void printWar(String text){
		this.print("war", (text));
	} // End printWar
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the string and the eol on the console and (or) the log file.
	 * 
	 * @param type			Text string type
	 * @param text			Text string
	 */
	private void println(String type, String text){
		//checks if the log file exists
		if (logFileOpen){
			//sets the console as default output
			if (console_output){
				if (type.equalsIgnoreCase("err")){
					//error
					System.err.println(text);
				} else if (type.equalsIgnoreCase("war")){
					//warning
					System.out.println("WARNING: " + text);
				} else {
					//message
					System.out.println(text);
				}
			}
			//sets the log file as default output
			if (log_output){
				this.writeLogFile(type,(text+"\n"));
			}			
		} else {
			//if there is no valid log file
			if (console_output){
				if (type.equalsIgnoreCase("err")){
					//error
					System.err.println(text);
				} else if (type.equalsIgnoreCase("war")){
					//warning
					System.out.println("WARNING: " + text);					
				} else {
					//message
					System.out.println(text);
				}
			}
		}
				
		//sets the errors flag
		if (!this.existErrors && type == "err"){ this.existErrors = true; }
		//sets the warnings flag
		if (!this.existWarnings && type == "war"){ this.existWarnings = true; }
		
	} // End println
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Prints the string on the console and (or) the log file.
	 * 
	 * @param type			Text string type
	 * @param text			Text string
	 */
	private void print(String type, String text){
		//checks if the log file exists
		if (logFileOpen){
			//sets the console as default output
			if (console_output){
				if (type.equalsIgnoreCase("err")){
					//error
					System.err.print(text);
				} else if (type.equalsIgnoreCase("war")){
					//warning
					System.out.print("WARNING: " + text);
				} else {
					//message
					System.out.print(text);
				}
			}
			//sets the log file as default output
			if (log_output){
				this.writeLogFile(type,(text));
			}			
		} else {
			//if there is no valid log file
			if (console_output){
				if (type.equalsIgnoreCase("err")){
					//error
					System.err.print(text);
				} else if (type.equalsIgnoreCase("war")){
					//warning
					System.out.print("WARNING: " + text);					
				} else {
					//message
					System.out.print(text);
				}
			}
		}
		
		//sets the errors flag
		if (!this.existErrors && type == "err"){ this.existErrors = true; }
		//sets the warnings flag
		if (!this.existWarnings && type == "war"){ this.existWarnings = true; }	
				
	} // End print
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Creates a new system log file (if there is a log file with the same name, it is overwritten)
	 * 
	 * If log_file_classdir = true, the log file is created on the execution directory
	 * If log_file_classdir = false, the log file is created on the log_file_dir directory 
	 */
	private void loadLogFile(){
		//sets the file open flag to false
		logFileOpen			= false;
		//gets the correct directory for the log file
		String fileDir;		
		if (log_file_classdir){
			//uses the execution directory
			fileDir 		= this.getClass().getResource("").toString();
		} else {
			//uses the log_file_dir directory
			fileDir 		= this.log_file_dir;
		}
		//gets the file
		logFile 			= new File(fileDir,SysUtils.LOG_FILE_NAME);		
		//if there is no file, tries to create it
		if (!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch(IOException e) {
				//cannot create the file
				this.println("err",("Could not create the log file: '" + SysUtils.LOG_FILE_NAME + "'" + " [Class Utils, loadLogFile] "));
				this.println("err",(e.toString()));
				//ends the method
				return;
			}
		}
		//verifies that it can write to the file
		if (!logFile.canWrite()){
			//cannot write the file
			this.println("err",("It is not possible to write to the log file: "	+ SysUtils.LOG_FILE_NAME) + " [Class Utils, loadLogFile] ");
			this.println("err",("Check directory permissions: "					+ fileDir) + " [Class Utils, loadLogFile] ");
			return;
		}
		//associates the PrintWriter to the log file
		try {
			logFileWriter		= new PrintWriter(new FileWriter(logFile.getPath()));
		} catch(IOException e) {
			//cannot associate the PrintWriter to the file
			this.println("err",("Could not asociate the PrintWriter to the log file: '"	+ SysUtils.LOG_FILE_NAME+"'") + " [Class Utils, loadLogFile] ");
			this.println("err",(e.toString()));
			//ends the method
			return;
		}
		//updates the file utilization flag
		logFileOpen				= true;
		//initializes the log file with the start time
		this.writeLogFile("nil",("----------------------------------------------------------------------------------------------------\n"));
		this.writeLogFile("nil",("Logfile: " + SysUtils.LOG_FILE_NAME + "\n"));
		this.writeLogFile("nil",("----------------------------------------------------------------------------------------------------\n"));
		this.writeLogFile("nil", "User: " + System.getProperty("user.name") + " @ " + this.machineName + "\n");
		this.writeLogFile("nil",("----------------------------------------------------------------------------------------------------\n"));
		dateFormat				= new SimpleDateFormat("yyyy/MM/dd");
		this.writeLogFile("nil",("Init date (yyyy/MM/dd): \t" 	+ dateFormat.format(new Date()) + "\n"));
		dateFormat				= new SimpleDateFormat("HH:mm:ss,SSS");
		this.writeLogFile("nil",("Init time (HH:mm:ss,SSS): \t" + dateFormat.format(new Date()) + "\n"));
		this.writeLogFile("nil",("----------------------------------------------------------------------------------------------------\n\n"));
		
	} // End newLogFile

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Gets the machine name */
	private void loadMachineName(){
		
		//get the machine name
		try{
			this.machineName = InetAddress.getLocalHost().getHostName();			
		} catch (UnknownHostException e){
			this.printlnErr(e.getMessage(), this.className + ", getMachineName");
		}
		
	} // End getMachineName
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Writes the text value to the log file
	 * 
	 * @param text			String to write 
	 * @param type			Message type
	 */
	private void writeLogFile(String type, String text){
		
		//cheks if the file is open
		if (!logFileOpen) { return; }		
		//adds the date/time information (avoiding the header)
		StringBuffer prefix = new StringBuffer("");
		if (!type.equalsIgnoreCase("") && !type.equalsIgnoreCase("nil")) {
			dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
			prefix.append(" " + dateFormat.format(new Date()));
		}
		//adds the message type
		if (type.equalsIgnoreCase("err")){
			prefix.append("\t" + "ERROR  " + "\t" + "[LOGERR]  ");
		} else if (type.equalsIgnoreCase("") || type.equalsIgnoreCase("nil")) {
			prefix.append(" ");
 		} else if (type.equalsIgnoreCase("war")){ 
 			prefix.append("\t" + "WARNING" + "\t" + "[LOGWAR]  ");
 		} else { 		
 			prefix.append("\t" + "INFO   " + "\t" + "[LOGOUT]  ");
 		}
		//writes the data to the file
		logFileWriter.write(prefix.toString() + text);
		logFileWriter.flush();
		
	} // End writeLog
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Closes the log file and updates its flag */
	public void closeLogFile(){
		//checks if the file is open
		if (!logFileOpen){ return; }		
		//writes the last info
		logFileWriter.flush();
		//closes the file
		logFileWriter.close();
		//updates the flag
		logFileOpen = false;
		
	} // End closeLogFile
	
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/
	/** @return the existErrors */
	public boolean existErrors() { return existErrors; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the existWarnings */
	public boolean existWarnings() { return existWarnings; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the machineName */
	public String getMachineName() { return machineName; }

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** @return the log_file_dir */
	public String getLog_file_dir() { return log_file_dir; }
	
	
		
} // End class Utils
