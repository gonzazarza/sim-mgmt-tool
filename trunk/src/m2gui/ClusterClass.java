package m2gui;

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
import java.awt.Color;

/** 
 * Abstract class including the static values for the main class of the ClusterGUI application
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0306
 */
public abstract class ClusterClass {
	
	/*	
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================
	*/
	//table identifiers
	static final int		TABLE_FILES			= 0;							//file table type
	static final int		TABLE_PROPS_S1		= 1;							//properties table type step 1
	static final int		TABLE_PROPS_S2		= 2;							//properties table type step 2
	static final int		TABLE_PROPS_S3		= 3;							//properties table type step 3
	//app properties identifiers
	static final int		PROP_LOAD_PRJ		= 0;							//load proj app property
	static final int		PROP_LOAD_EF		= 1;							//load ef files app property
	static final int		PROP_RUN_MKSIM		= 2;							//run op_mksim app property
	static final int		PROP_SUBMIT_SIM		= 3;							//submit sim app property
	static final int		PROP_CHECK_QUEUE	= 4; 							//check queue app property
	//files grid column ids
	static final int		TF_COL_FILE_NAME	= 0;							//file table column file name id
	static final int		TF_COL_INCLUDE		= 1;							//file table column include id
	//static labels
	static final String		LABEL_LOAD_PRJ		= "Load project";				//load project label
	static final String		LABEL_LOAD_EF		= "Load file list";				//load ef file list label
	static final String		LABEL_CHECK_OP_VER	= "Check version";				//check opnet version
	static final String		LABEL_RUN_MKSIM		= "Run op_mksim";				//run op_mksim label
	static final String		LABEL_SUBMIT_SIM	= "Submit sim jobs";			//submit jobs label
	static final String		LABEL_QSTAT			= "Monitor queue";				//qstat label
	//step identifier
	static final int		STEP_1_LOAD_PRJ		= 0;							//step 1: load prj file
	static final int		STEP_2_LOAD_EF		= 1;							//step 2: load ef file list
	static final int		STEP_3_RUN_MKSIM	= 2;							//step 3: run op_mksim command
	static final int		STEP_4_SUBMIT_SIM	= 3;							//step 4: submit simulation to queue
	static final int 		STEP_5_MONITOR_JOBS	= 4;							//step 5: monitor simulation on queue
	//tabbed pane titles
	static final String		TAB_1_PRJ			= "OPNET Project";				//tab 1: opnet project
	static final String		TAB_2_EF			= "Environmental files";		//tab 2: ef file list
	static final String		TAB_3_MKSIM			= "Config op_mksim";			//tab 3: params op_mksim
	static final String		TAB_4_SIM			= "Config sim jobs";			//tab 4: params sim files
	static final String		TAB_5_ABOUT			= "Preferences ";				//tab 5: prefs/help
	//system output text area identifier
	static final int		TX_STDOUT			= 0;							//normal output
	static final int		TX_STDERR			= 1;							//error output
	//system output text area colors
	static final Color		TX_COLOR_STDOUT		= new Color(000, 000, 000);		//normal output color
	static final Color		TX_COLOR_STDERR		= new Color(255, 000, 000);		//error output color
	
	
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
		
			


} // End abstract class ClsusterClass
