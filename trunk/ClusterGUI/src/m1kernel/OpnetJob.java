package m1kernel;

//classes
import java.util.Vector;

/** 
 * Opnet job datatype
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1027
 */
public class OpnetJob {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private boolean						efFileIncluded			= false;					//ef file status
	private Vector<String>				efFileMKSIMCode			= new Vector<String>();		//ef file op_mksim code
	private boolean						efFileMKSIMCompiled		= false;					//ef file op_mksim status
	private String						simFileName				= "";						//sim file name
	private boolean						simFileIncluded			= false;					//sim file status
	private Vector<String>				simFileDTSIMCode		= new Vector<String>();		//sim file run code
	private boolean						simFileQueueSent		= false;					//sim file queue sent status
	private boolean						simFileQueueDone		= false;					//sim file queue done status 
	private Vector<String>				simFileQueueError		= new Vector<String>();		//sim file queue error message
	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/** Class constructor */
	public OpnetJob(){ super(); }


	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	
			
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the efFileIncluded */ 
	public boolean isEfFileIncluded() { return efFileIncluded; }


	/** @param efFileIncluded the efFileIncluded to set */
	public void setEfFileIncluded(boolean efFileIncluded) { this.efFileIncluded = efFileIncluded; }


	/** @return the efFileMKSIMCode */
	public Vector<String> getEfFileMKSIMCode() { return efFileMKSIMCode; }


	/** @param efFileMKSIMCode the efFileMKSIMCode to set */
	public void setEfFileMKSIMCode(Vector<String> efFileMKSIMCode) { this.efFileMKSIMCode = efFileMKSIMCode; }


	/** @return the efFileMKSIMCompiled */
	public boolean isEfFileMKSIMCompiled() { return efFileMKSIMCompiled; }


	/** @param efFileMKSIMCompiled the efFileMKSIMCompiled to set */
	public void setEfFileMKSIMCompiled(boolean efFileMKSIMCompiled) { this.efFileMKSIMCompiled = efFileMKSIMCompiled; }


	/** @return the simFileName */
	public String getSimFileName() { return simFileName; }


	/** @param simFileName the simFileName to set */
	public void setSimFileName(String simFileName) { this.simFileName = simFileName; }


	/** @return the simFileIncluded */
	public boolean isSimFileIncluded() { return simFileIncluded; }


	/** @param simFileIncluded the simFileIncluded to set */
	public void setSimFileIncluded(boolean simFileIncluded) { this.simFileIncluded = simFileIncluded; }


	/** @return the simFileDTSIMCode */
	public Vector<String> getSimFileDTSIMCode() { return simFileDTSIMCode; }


	/** @param simFileDTSIMCode the simFileDTSIMCode to set */
	public void setSimFileDTSIMCode(Vector<String> simFileDTSIMCode) { this.simFileDTSIMCode = simFileDTSIMCode; }


	/** @return the simFileQueueSent */
	public boolean isSimFileQueueSent() { return simFileQueueSent; }


	/** @param simFileQueueSent the simFileQueueSent to set */
	public void setSimFileQueueSent(boolean simFileQueueSent) { this.simFileQueueSent = simFileQueueSent; }


 	/** @return the simFileQueueDone */
	public boolean isSimFileQueueDone() { return simFileQueueDone; }


	/** @param simFileQueueDone the simFileQueueDone to set */
	public void setSimFileQueueDone(boolean simFileQueueDone) { this.simFileQueueDone = simFileQueueDone; }


	/** @return the simFileQueueError */
	public Vector<String> getSimFileQueueError() { return simFileQueueError; }


	/** @param simFileQueueError the simFileQueueError to set */
	public void setSimFileQueueError(Vector<String> simFileQueueError) { this.simFileQueueError = simFileQueueError; } 
	
		
} // End class OpnetJob
