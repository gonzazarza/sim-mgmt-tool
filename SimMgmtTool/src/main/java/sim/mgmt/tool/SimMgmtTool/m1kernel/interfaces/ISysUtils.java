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

/** 
 * Interface to access to the system utilities class
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public interface ISysUtils {
	
	
	/*	
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================
	*/
	/** log file name */
	static final String 		LOG_FILE_NAME				= "ClusterGUI.log";	
	/** optional user home dir */
	static final String			DIR_OPT_USER_HOME			= "/home/gzarza/";	
	
	
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
	 */
	void printErr(String text);
	
	
	/** Prints the message string on the console and (or) the log file
	 * 
	 * @param text			Message text string
	 */
	void printOut(String text);
	
	
	/** Prints the warning string on the console and (or) the log file
	 * 
	 * @param text			warning text string
	 */
	void printWar(String text);
	
	
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
	
	/** @return the log_file_dir */
	public String getLog_file_dir();

} // End interface IUtils
