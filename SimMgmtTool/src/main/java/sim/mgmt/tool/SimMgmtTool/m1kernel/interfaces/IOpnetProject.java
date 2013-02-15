package sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces;

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
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.ggf.drmaa.Session;
//exceptions
import sim.mgmt.tool.SimMgmtTool.m1kernel.exceptions.OpnetLightException;
import sim.mgmt.tool.SimMgmtTool.m1kernel.exceptions.OpnetHeavyException;

/** 
 * Interface to acces the Opnet project class
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0311
 */
public interface IOpnetProject {

	/*	
	================================================================================================================== 
	Static Attributes
	==================================================================================================================
	*/
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/**
	 * Load the project file
	 * 
	 *  @param		pPath					project path
	 *  @param 		pName					project name  
	 * 	@throws 	OpnetHeavyException		if one of the parameters is null
	 *  @throws		OpnetLightException		if there are old sim files in the project directory
	 */
	public void loadProject(String pPath, String pName) throws OpnetHeavyException, OpnetLightException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 * 
	 * @throws 		OpnetHeavyException		if the previous operation is not applied 
	 * 										or if it is not possible to load the ef files list 
	 */
	public void setNetworksMap() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Run the op_mksim command
	 * 
	 * @return								the command output streams as a vector of strings
	 * @throws 		OpnetHeavyException		if the previous operation is not applied
	 * 										or if it is not possible to run the op_mksim command
	 */
	public Vector<String> runMKSIMCmd() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the complete list of supplementary environmental files and their included status
	 * 
	 * 	@return 							the complete list of files and their included status  
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the environmental files list was not loaded.
	 * 										if there are errors in the files table data array bounds.
	 */
	public Object[][] getFilesData() throws OpnetHeavyException;	
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** 
	 * Set the data for the list of supplementary environmental files and their included status
	 * 
	 * @param 	pData						the data for the list of supplementary environmental files and their included status to set 
	 * @throws 	OpnetHeavyException			if the previous operation was not applied.
	 * 										if the pData parameter is null.
	 * 										if it was not possible to set the data.
	 */
	public void setFilesData(Object[][] pData) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Set the isIncluded status for the fileName
	 * 
	 * @param		pFileName				the item identifier
	 * @param		pIsIncluded				the item status
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 										if the operation fails for some reason.
	 * 
	 */	
	public void setIncluded(String pFileName, boolean pIsIncluded) throws OpnetHeavyException;

	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the number of supplementary environmental files
	 * 
	 * @return 								the length of the files data  (a.k.a. the number of ef files) 
	 */
	public int getFilesDataLength();

	/* ------------------------------------------------------------------------------------------------------------ */	
		
	/**
	 * Set and return the network names for the output text area
	 * 
	 * @return 								the set of network names prepared for the output text area  
	 * 
	 */
	public String getOutputNetworkNames() ;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the list of network names
	 * 
	 * @return 								the set of network names
	 * @throws		 OpnetHeavyException	if the previous operation was not applied.
	 */
	public Set<String> getNetworksNames() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the list of selected networks
	 * 
	 * @return								the set of selected network names
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 				
	 */
	public Set<String> getSelectedNetworksNames() throws OpnetHeavyException;

	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Return the names of the network names successfully compiled
	 * 
	 * @return 							the set of compiled network names
	 * @throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 
	 */
	public Set<String> getCompiledNetworksNames() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the names of the sim files successfully compiled
	 * 
	 * @return 							the set of compiled sim files
	 * @throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 									if there is an error finding the sim file.
	 */
	public Set<String> getCompiledSimJobsNames() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * Return the content of the specified file
	 * 
	 * @param		pFileName				the name of the file to load
	 * @return								the file content
	 * @throws 		OpnetHeavyException		if the previous operation was not applied.
	 * 										if it was not possible to read the ef file content.
	 * 
	 */
	public String getEfFileContent(String pFileName) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/**
	 * Return the code for the op_mksim command for the specified network
	 * 
	 *  @param		pNetName				the network name
	 *  @return								the op_mksim code
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the network wasn't found.
	 */
	public Vector<String> getNetworkMKSIMCode(String pNetName) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the code for the op_mksim command for the specified network
	 * 
	 *  @param		pNetName				the network name
	 *  @param		pCode					the code to set  
	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
	 * 										if the network wasn't found.
	 */
	public void setNewNetworkMKSIMCode(String pNetName, Vector<String> pCode) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** 
	 * Return the help of the op_mksim command
	 * 
	 * 	@return 							the op_mksim command help
 	 * 	@throws 	OpnetHeavyException		if it was not possible to get the command help
	 */
	public String getMKSIMHelp() throws OpnetHeavyException;

	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/**
	 * 	Return the sim code of every ef file included and compiled correctly for each network name
	 * 
	 * @param		pNetName				the network name
	 * @return								the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public Vector<String> getSimFileCode(String pFileName) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * 	Set the sim code of the ef file included and compiled correctly
	 * 
	 * @param		pFileName				the ef file name
	 * @param		pCode					the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public void setNewSimFileCode(String pFileName, Vector<String> pCode) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * 	Set the sim code of every ef file included and compiled correctly for each network name
	 * 
	 * @param		pSimFileName			the sim file name
	 * @param		pCode					the sim code
	 * @throws		OpnetHeavyException		if the previous action was not applied.
	 * 										if there is a logic error in the file.
	 */
	public void setSimFileCode(String pSimFileName, Vector<String> pCode) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/** 
	 * Return the help of the sim file (binary file)
	 * 
	 * 	@return 							the sim file help
 	 * 	@throws 	OpnetHeavyException		if the previous operation was not applied.
 	 * 										if it was not possible to get the sim file help
	 */
	public String getSimsFileHelp() throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Submit the corresponding jobs to the queue
	 * 
	 * @param		pQueueName				the queue to be used
	 * @param		pOpLicNum				the number of Opnet licenses needed 
	 * @param		pJobPriority			the Unix priority of the jobs
	 * @param		pOutDir					the output directory
	 * @param		pErrDir					the error directory
	 * @param		pScrDir					the script directory 
	 * @return								the operations info
	 * @throws		OpnetHeavyException		in case of a DrmaaException	
	 *  									in case of an InternalEception
	 *  									in case of an IllegalArgumentException
	 *  									in case of an OutOfMemory Error 
	 */
	public Vector<String> submitSimJobs(String pQueueName, int pOpLicNum, float pJobPriority, 
										String pOutDir, String pErrDir, String pScrDir) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Remove the bash script files from the specified directory
	 * 
	 * @param		pScrPath				the path of the bash script files
	 * @return								the number of bash files removed
	 * @throws		OpnetHeavyException		If errors removing the scripts or null path
	 *
	 */
	public int removeOldScripts(String pScrPath) throws OpnetHeavyException;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Remove the sim files from the specified directory
	 * 
	 * @param		pScrPath				the path of the sim files
	 * @return								the number of sim files removed
	 * @throws		OpnetHeavyException		If errors removing the sim or null path
	 *
	 */
	public int removeOldSims(String pScrPath) throws OpnetHeavyException;
	
	
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the projectName */
	public String getProjectName();
	
	/** @return the isRunMKSIMDone */
	public boolean isRunMKSIMDone();
	
	/** @return the queueSession */
	public Session getQueueSession();
	
	/** @return the idsInfo */
	public HashMap<String, String> getIdsInfo();
	
	/** @return the isSimSubmitDone */
	public boolean isSimSubmitDone();
	
} // End interface IOpnetProject

