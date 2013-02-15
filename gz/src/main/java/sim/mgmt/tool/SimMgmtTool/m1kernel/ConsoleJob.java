package sim.mgmt.tool.SimMgmtTool.m1kernel;

/* 
 * Copyright (c) 2010-2013 Gonzalo Zarza. All rights reserved.
 * Copyright (c) 2010-2011 Departament d'Arquitectura de Computadors i Sistemes Operatius, Universitat Autonoma de Barcelona. All rights reserved. 
 *
 * This file is part of the Simulation Management Tool (sim-mgmt-tool).
 *
 * Simulation Management Tool (sim-mgmt-tool) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Simulation Management Tool (sim-mgmt-tool) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Simulation Management Tool (sim-mgmt-tool). If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/** 
 * Console job datatype
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
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