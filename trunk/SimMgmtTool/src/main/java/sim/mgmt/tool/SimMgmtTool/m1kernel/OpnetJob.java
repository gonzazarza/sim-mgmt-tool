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

//classes
import java.util.Vector;

/** 
 * Opnet job datatype
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0313
 */
public class OpnetJob {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private boolean						efFileIncluded			= true;					//ef file status
	private Vector<String>				efFileMKSIMCode			= new Vector<String>();		//ef file op_mksim code
	private boolean						efFileMKSIMCompiled		= false;					//ef file op_mksim status
	private String						simFileName				= "";						//sim file name
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
