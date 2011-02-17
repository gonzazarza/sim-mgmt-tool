package m1kernel;

/** 
 * Console job datatype
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2011.0216
 */
public class ConsoleJob {
	
	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private int							exitValue		= 0;							//subprocess exit value
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
		
		return (this.exitValue != 0 && this.stderr != null && this.stderr != "");
		
	} // End boolean stderrActive
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the exitValue */
	public int getExitValue() { return exitValue; }

	/** @param exitValue the exitValue to set */
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	
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