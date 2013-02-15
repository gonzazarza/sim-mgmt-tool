package sim.mgmt.tool.SimMgmtTool.m2gui;

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

//swing classes
import javax.swing.JFrame;
import javax.swing.JOptionPane;
//awt classes
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//other classes
import sim.mgmt.tool.SimMgmtTool.m1kernel.SysUtils;
//interfaces
import sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces.ISysUtils;

/**
 * GUI serving as the main interface between OPNET Modeler 14.0 and the IBM cluster at CAOS.
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0310
 */
public class ClusterGUI extends WindowAdapter {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private ISysUtils				sysUtils			= null;							//system utilities class
	private ClusterApp				appCluster			= null;							//application kernel
	private String					className			= "unknown";					//current class name	
	private static final String		APP_NAME			= "ClusterGUI";					//application name
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Default constructor */		
	public ClusterGUI(){
	
		//start the system-based utilities
        this.sysUtils				= new SysUtils();
        
		//get and store the name of the class
		this.className				= this.getClass().getName();
        
        //inform the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
        
		//initialize the application kernel
        this.appCluster				= new ClusterApp(this.sysUtils);
		
		//initialize components
	    this.appCluster.initComponents();
				
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);

	} // End constructor          
	
	
	/*	
	================================================================================================================== 
	Methods
	==================================================================================================================
	*/
	/** Create the GUI and show it */
	private static void createAndShowGUI(){
				
		//frame for the main gui
		ClusterGUI	mainGUI			= new ClusterGUI();	
		JFrame		mainFrame		= new JFrame(ClusterGUI.APP_NAME);
				
		//set the window title
		mainFrame.setTitle(mainGUI.appCluster.getFrameTitle());
	
		//prevent resizing
		mainFrame.setResizable(false);
		
		//set the exit option
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//add contents to the window
		mainFrame.add(mainGUI.appCluster.getMainPanel());
		
		//add window listener windowClosing
		mainFrame.addWindowListener(mainGUI);
		
		//display the window
		mainFrame.pack();
		mainFrame.setVisible(true);
				
	} // End void createAndShowGUI
	
	
	/** Main method
	 * 
	 * @param args		the command line arguments 
	 */
	public static void main(String args[]){
		//run the application
		java.awt.EventQueue.invokeLater(new Runnable(){ public void run() { createAndShowGUI(); } }	);
		
	} // End static void main

		
	/*
	================================================================================================================== 
	Action Listeners																										
	==================================================================================================================	
	*/		
	/* (non-Javadoc)
	 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e){
		
		//confirms the exit operation		
		int returnVal	= JOptionPane.showOptionDialog(
							this.appCluster.getMainPanel(),
							"Exit " + ClusterGUI.APP_NAME + "?",
							"Confirm exit",
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null,
							null,
							null);
		
		if (returnVal == JOptionPane.YES_OPTION){
			//exit the DRMAA session if necessary
			this.appCluster.exitDRMAASession();			
			//log the closing operation		
			this.sysUtils.printlnOut("... Closing the " + ClusterGUI.APP_NAME + " application now ...", this.className);
			//exit application
			System.exit(0);
		} 
		//else >> nothing to do				
					
	} // End void windowCosing

	
} // End class ClusterGUI
