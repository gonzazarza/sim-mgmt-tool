package m1kernel;

/** 
 * Console job datatype
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1105
 */
public class ConsoleJob {
	
	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private StringBuffer				cOutput			= new StringBuffer("");			//console command output
	private StringBuffer				cErrors			= new StringBuffer("");			//console command errors
	//control attributes
	private boolean						isOutputEnabled	= false;						//output enable flag
	private boolean						isErrorsEnabled = false;						//errors enable flag
	private boolean						isOutputNull	= false;						//output null flag
	private boolean						isErrorsNull	= false;						//errors null flag
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/** Class constructor */
	public ConsoleJob(){ super(); }


	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/** */
	
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	

} // End class ConsoleJob
