package m1kernel;

/** 
 * Console job datatype
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1117
 */
public class ConsoleJob {
	
	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private String						stdout			= "";							//console command output
	private String						stderr			= "";							//console command errors
	
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
	/** @return the cOutput enabled value */
	public boolean stdoutActive(){
		
		return (this.stdout != null && this.stdout != "");
		
	} // End boolean stdoutActive
	
	/** @return the cErrors enabled value */
	public boolean stderrActive(){
		
		return (this.stderr != null && this.stderr != "");
		
	} // End boolean stderrActive
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the output */
	public String getStdout() { return stdout; }

	/** @param pOutput the output to set */
	public void setStdout(String pOutput) { 
		
		if (pOutput != null){
			this.stdout = pOutput;
		}
	}

	/** @return the errors */
	public String getStderr() { return stderr; }

	/** @param pErrors the errors to set */
	public void setStderr(String pErrors) { 
		
		if (pErrors != null){
			this.stderr = pErrors; 
		}
	}		

} // End class ConsoleJob