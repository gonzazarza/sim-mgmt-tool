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

//awt classes
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
//swing classes
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//util classes
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
//other classes
import sim.mgmt.tool.SimMgmtTool.m1kernel.FilesTableModel;
import sim.mgmt.tool.SimMgmtTool.m1kernel.OpnetProject;
import sim.mgmt.tool.SimMgmtTool.m1kernel.PropsTableCellRenderer;
import sim.mgmt.tool.SimMgmtTool.m1kernel.PropsTableModel;
import sim.mgmt.tool.SimMgmtTool.m1kernel.ThreadForQstat;
//events and listeners
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
//exceptions
import java.awt.HeadlessException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import sim.mgmt.tool.SimMgmtTool.m1kernel.exceptions.OpnetLightException;
import sim.mgmt.tool.SimMgmtTool.m1kernel.exceptions.OpnetHeavyException;
//abstract classes
import sim.mgmt.tool.SimMgmtTool.m2gui.ClusterClass;
//interfaces
import sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces.IAppUtils;
import sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces.IOpnetProject;
import sim.mgmt.tool.SimMgmtTool.m1kernel.interfaces.ISysUtils;

/**
 * Main class of the ClusterGUI application
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
 * @version		2011.0313
 */
public class ClusterApp extends ClusterClass implements ChangeListener, ActionListener, MouseListener, KeyListener, DocumentListener {
 
	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	//class attributes
	private ISysUtils					sysUtils				= null;							//system-based utilities
	private String						className				= "unknown";					//current class name
	private String						machineName				= "unknown";					//machine name
	private	String						userName				= "unknown";					//user name
	private String						appTitle				= "";							//app frame title
	private String						lineBreak				= "\n";							//system line separator
	private IOpnetProject				opProject				= null;							//opnet project
	Thread								thQstat					= null;							//thread for qstat command
	//main panel attributes and components
	private JPanel						mainPanel				= null;							//main panel
	private JTabbedPane					tabbedPane				= null;							//main tabbed pane	
	//--- panel: project 
	private JPanel						pProject				= null;							//project panel
	private JButton						bBrowse					= null;							//browse button
	private JButton						bRunMKSIM				= null;							//run op_mksim command
	private JButton						bSubmitSims				= null;							//submit sim to queue
	private JButton						bAppOutputClear			= null;							//app info output clear button
	private JButton						bAppOutputSave			= null;							//app info output save button
	private JFileChooser				dFileChooser			= null;							//load file dialog
	private JTextArea					txAppOutput				= null;							//app info output
	private Document					deAppOutput				= null;							//app output document
	private String[][]					s1StatusData			= null;							//step 1 app status data container
	private JTable						s1StatusTable			= null;							//step 1 grid for the system props
	private PropsTableModel				s1StatusModel			= null;							//step 1 default table model
	private String[][]					s2StatusData			= null;							//step 2 app status data container
	private JTable						s2StatusTable			= null;							//step 2 grid for the system props
	private PropsTableModel				s2StatusModel			= null;							//step 2 default table model
	private String[][]					s3StatusData			= null;							//step 3 app status data container
	private JTable						s3StatusTable			= null;							//step 3 grid for the system props
	private PropsTableModel				s3StatusModel			= null;							//step 3 default table model
	//--- panel: load ef files
	private JPanel						pFileList				= null;							//ef file list panel
	private JCheckBox					ckSelectAll				= null;							//select all checkbox
	private JCheckBox					ckSelectNone			= null;							//unselect checkbox
	private JTextArea					txFileContent			= null;							//ef file context output
	private JTable						filesTable				= null;							//table for the ef files list
	private JLabel						filesLabel				= null;							//label for the ef files selected
	private FilesTableModel				filesModel				= null;							//custom table model
	private JScrollPane 				filesScroll				= null;							//scroll for the ef files table
	//--- panel: op_mksim
	private JPanel						pMKSIM					= null;							//op_mksim panel
	private JComboBox					cbNetsList				= null;							//net info combobox
	private JButton						bNetsReset				= null;							//net file reset button
	private JButton						bNetsSave				= null;							//net file save button
	private JTextArea					txMKSIMParams			= null;							//list of op_mksim command params
	private Document					deMKSIMParams			= null;							//op_mksim params document
	private JTextArea					txMKSIMHelp				= null;							//help of the op_mksim command
	private final String				netsListHeader			= " * Select the network *";	//network names cb header
	private final String				netsListEmpty			= " * No networks selected *";	//network names cb empty item	
	//--- panel: sim file
	private	JPanel						pDTSIM					= null;							//sim file panel
	private JComboBox					cbSimsList				= null;							//sim file info combobox
	private JButton						bSimsReset				= null;							//sim file reset button
	private JButton						bSimsSave				= null;							//sim file save button
	private JTextArea					txDTSIMParams			= null;							//sim file params
	private Document					deDTSIMParams			= null;							//sun file params documment
	private JTextArea					txDTSIMHelp				= null;							//sim file help
	private final String				simsListHeader			= " * Select the sim job *";	//sim files cb header
	private final String				simsListEmpty			= " * No sim job selected *";	//sim files cb empty item
	//--- panel: prefs/help
	private JPanel						pPrefs					= null;							//help panel
	private JComboBox					cbOpnetVersion			= null;							//opnet version combobox
	private JTextField					tfSimLicNumber			= null;							//opnet licenses number field
	private JTextField					tfSimPriority			= null;							//simulation priority value field
	private JTextField					tfSimQueue				= null;							//simulation queue name field
	private JButton						bSimOutDir				= null;							//simulation output dir
	private JButton						bSimErrDir				= null;							//simulation error dir
	private JButton						bSimScrDir				= null;							//simulation scripts dir
	private JButton						bKillJobs				= null;							//utilities kill jobs
	private JButton						bRemoveSimFiles			= null;							//utilities remove sims
	private JButton						bRemoveScrFiles			= null;							//utilities remove scripts
	private JFileChooser				dOutChooser				= null;							//output file chooser
	private JFileChooser				dErrChooser				= null;							//error file chooser
	private JFileChooser				dScrChooser				= null;							//scripts file chooser
	private JFileChooser				dOldScripts				= null;							//old scripts target dir
	private JFileChooser				dOldsims				= null;							//old sims target dir
	private String						outDir					= null;							//path for the outputs
	private String						errDir					= null;							//path for the error logs
	private String						scrDir					= null;							//path for the scripts
	private JLabel						lOutDir					= null;							//output dir label
	private JLabel						lErrDir					= null;							//error dir label
	private JLabel						lScrDir					= null;							//scripts dir label
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor
	 *
	 * @param		pUtils				the system utilities class 
	 */
	public ClusterApp (ISysUtils pUtils){
		
		//set the utilities class
		this.sysUtils				= pUtils;
		
		//get and store the name of the class
		this.className				= this.getClass().getName();
		
        //inform the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
		
		//initialize the opnet project
		this.opProject				= new OpnetProject(this.sysUtils);
				
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);		
		
	} // End constructor
	
	
	/*	
	================================================================================================================== 
	Methods
	==================================================================================================================
	*/
	/** 
	 * Initialize the components of the JPanel 
	 */
	public void initComponents(){
				
		//get the user and machine names for the title and other system properties
		this.userName				= System.getProperty("user.name");
		this.machineName			= this.sysUtils.getMachineName();		
		
		//save the title
		this.appTitle				= "Cluster GUI for OPNET Modeler - " + this.userName + "@" + this.machineName;  
		
		//initialize main jpanel
		this.mainPanel				= new JPanel();		
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS));
		
		//set the tabbed pane
		this.setTabbedPane();
		
		//add the tabbed pane
		this.mainPanel.add(this.tabbedPane);

		//initial diasble/enable gui components
		this.initGUILook();
		
	} // End void initComponents

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the main tabbed pane 
	 */
	private void setTabbedPane(){
		
		//initialize the tabbed pane
		this.tabbedPane				= new JTabbedPane();		
		//set pane options
		this.tabbedPane.setTabPlacement(JTabbedPane.TOP);
		//set pane listeners
		this.tabbedPane.addChangeListener(this);
		
		//add project panel
		this.setPanelProject();
		this.tabbedPane.add(ClusterApp.TAB_1_PRJ,	this.pProject);
		
		//add ef files panel
		this.setPanelFileList();
		this.tabbedPane.add(ClusterApp.TAB_2_EF, 	this.pFileList);			
		
		//add op_mksim panel
		this.setPanelMKSim();
		this.tabbedPane.add(ClusterApp.TAB_3_MKSIM, this.pMKSIM);				// TO BE FINISHED!
		
		//add sim file panel
		this.setPanelDTSim();
		this.tabbedPane.add(ClusterApp.TAB_4_SIM, 	this.pDTSIM);				// TO BE FINISHED!
				
		//add prefs/help panel
		this.setPanelPrefs();
		this.tabbedPane.add(ClusterApp.TAB_5_ABOUT,	this.pPrefs);				//TO BE DEFINED!
		
	} // End void setTabbedPane
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Exits the DRMAA session
	 */
	public void exitDRMAASession(){
		
		if (this.opProject.getQueueSession() != null){
			
			try {
				//try to close the drmaa session 
				this.opProject.getQueueSession().exit();
				
			} catch (DrmaaException e) {
				//log the error
				this.sysUtils.printlnErr("Unable to exit the DRMAA session", this.className + ", exitDRMAASession");
				//show an error message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Unable to exit the DRMAA session.",
						"Fatal Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	} // End void exitDRMAASession
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 1st-LEVEL METHODS: PANELS OPERATIONS																			*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the project panel 
	 */
	private void setPanelProject(){		
			
		//initialize panel
		this.pProject						= new JPanel();
		
		//local attributes
		int					buttonWidth		= 300;
		int					buttonHeight	= 25;
		int					strutVer		= 10;
		int					strutHor		= 50;
		
		//set border and layout		
		this.pProject.setLayout(new BoxLayout(this.pProject, BoxLayout.PAGE_AXIS));
		this.pProject.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(this.setPanelExtra(" Commands"));
		this.pProject.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: step 1, select project file
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: components and status
		//-----------------------------------------------------------------------------------------------
		JPanel				pp1Row1			= new JPanel();	
		this.bBrowse						= new JButton("Step 1:  Select project file");
		this.dFileChooser					= new JFileChooser();
		this.dFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dFileChooser.setFileFilter(new FileNameExtensionFilter("Opnet project file (*.prj)","prj"));
		this.dFileChooser.setMultiSelectionEnabled(false);
		this.bBrowse.addActionListener(this);	
		//------ configure components
		this.bBrowse.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		//------ set border and layout
		pp1Row1.setLayout(new BoxLayout(pp1Row1, BoxLayout.X_AXIS));
		pp1Row1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		//--- set background color
		pp1Row1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add components
		pp1Row1.add(this.bBrowse);		
		pp1Row1.add(Box.createHorizontalStrut(strutHor));
		pp1Row1.add(Box.createHorizontalGlue());
		//------ set status data	
		this.s1StatusData					= new String[2][2];
		//------ 0
		this.s1StatusData[0][0]				= new String(ClusterApp.LABEL_LOAD_PRJ);	
		this.s1StatusData[0][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 1
		this.s1StatusData[1][0]				= new String(ClusterApp.LABEL_LOAD_EF);
		this.s1StatusData[1][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ initialize status table
		boolean status1						= this.initTable(ClusterApp.TABLE_PROPS_S1, this.s1StatusData);
		//------ add status table
		if (status1 == true){
			pp1Row1.add(this.s1StatusTable);
		} else {
			//show an error message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Unable to initialize the system properties status table (step 1).",
					"App status error",
					JOptionPane.ERROR_MESSAGE);
		}
		//------ add panel
		this.pProject.add(pp1Row1);	
		//-----------------------------------------------------------------------------------------------
		//--- set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(Box.createVerticalStrut(strutVer));
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel: step 2, run op_mksim
		//-----------------------------------------------------------------------------------------------		
		//--- set row 1: components and status
		//-----------------------------------------------------------------------------------------------
		JPanel				pp2Row1			= new JPanel();	
		this.bRunMKSIM						= new JButton("Step 2:  op_mksim command");
		//------ configure components
		this.bRunMKSIM.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		//------ set border and layout
		pp2Row1.setLayout(new BoxLayout(pp2Row1, BoxLayout.X_AXIS));
		pp2Row1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		//--- set background color
		pp2Row1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add listeners
		this.bRunMKSIM.addActionListener(this);
		//------ add components
		pp2Row1.add(this.bRunMKSIM);
		pp2Row1.add(Box.createHorizontalStrut(strutHor));
		pp2Row1.add(Box.createHorizontalGlue());
		//------ set status data	
		this.s2StatusData					= new String[2][2];
		//------ 0
		this.s2StatusData[0][0]				= new String(ClusterApp.LABEL_CHECK_OP_VER);	
		this.s2StatusData[0][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 1
		this.s2StatusData[1][0]				= new String(ClusterApp.LABEL_RUN_MKSIM);
		this.s2StatusData[1][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ initialize status table
		boolean status2						= this.initTable(ClusterApp.TABLE_PROPS_S2, this.s2StatusData);
		//------ add status table
		if (status2 == true){
			pp2Row1.add(this.s2StatusTable);
		} else {
			//show an error message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Unable to initialize the system properties status table (step 2).",
					"App status error",
					JOptionPane.ERROR_MESSAGE);
		}
		//------ add panel
		this.pProject.add(pp2Row1);		
		//-----------------------------------------------------------------------------------------------
		//------ set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(Box.createVerticalStrut(strutVer));
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel: step 3, submit simulations to queue
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: components and status
		//-----------------------------------------------------------------------------------------------
		JPanel				pp3Row2			= new JPanel();	
		this.bSubmitSims					= new JButton("Step 3:  Submit simulations");
		//------ configure components
		this.bSubmitSims.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		//------ set border and layout
		pp3Row2.setLayout(new BoxLayout(pp3Row2, BoxLayout.X_AXIS));
		pp3Row2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		//--- set background color
		pp3Row2.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add listeners
		this.bSubmitSims.addActionListener(this);
		//------ add components
		pp3Row2.add(this.bSubmitSims);		
		pp3Row2.add(Box.createHorizontalStrut(strutVer));
		pp3Row2.add(Box.createHorizontalGlue());
		//------ set status data	
		this.s3StatusData					= new String[2][2];
		//------ 0
		this.s3StatusData[0][0]				= new String(ClusterApp.LABEL_SUBMIT_SIM);	
		this.s3StatusData[0][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 1
		this.s3StatusData[1][0]				= new String(ClusterApp.LABEL_QSTAT);
		this.s3StatusData[1][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ initialize status table
		boolean status3						= this.initTable(ClusterApp.TABLE_PROPS_S3, this.s3StatusData);
		//------ add status table
		if (status3 == true){
			pp3Row2.add(this.s3StatusTable);
		} else {
			//show an error message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Unable to initialize the system properties status table (step 3).",
					"App status error",
					JOptionPane.ERROR_MESSAGE);
		}
		//------ add panel
		this.pProject.add(pp3Row2);	
		//-----------------------------------------------------------------------------------------------
		//------ set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(Box.createVerticalStrut(strutVer*3));						
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(this.setPanelExtra(" Output"));
		this.pProject.add(Box.createVerticalStrut(5));		
		//-----------------------------------------------------------------------------------------------
		//--- set panel: output
		//-----------------------------------------------------------------------------------------------
		JPanel				ppoRow1			= new JPanel();
		String				userPrompt		= " No output";
		this.txAppOutput					= new JTextArea(userPrompt, 25, 70);
		JScrollPane			outputScroll	= new JScrollPane(this.txAppOutput);
		this.deAppOutput					= this.txAppOutput.getDocument();
		//------- configure components 
		this.txAppOutput.setBackground(IAppUtils.COLOR_COMPONENTS);
		this.txAppOutput.setFont(new Font(this.txAppOutput.getFont().getFamily(), Font.ITALIC, 12));
		this.deAppOutput.addDocumentListener(this);
		//------ set border and layout
		ppoRow1.setLayout(new BoxLayout(ppoRow1, BoxLayout.X_AXIS));
		ppoRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ add components
		ppoRow1.add(outputScroll);
		//------ add panel
		this.pProject.add(ppoRow1);
		//-----------------------------------------------------------------------------------------------
		//--- set clear and save buttons
		//-----------------------------------------------------------------------------------------------
		JPanel				ppoRow2			= new JPanel();
		this.bAppOutputClear				= new JButton("Clear output");
		this.bAppOutputSave					= new JButton("Save to file");
		//------ add listeners
		this.bAppOutputClear.addActionListener(this);
		this.bAppOutputSave.addActionListener(this);
		//------ set border and layout
		ppoRow2.setLayout(new BoxLayout(ppoRow2, BoxLayout.X_AXIS));
		ppoRow2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ set background color
		ppoRow2.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add components
		ppoRow2.add(Box.createHorizontalGlue());
		ppoRow2.add(this.bAppOutputSave);
		ppoRow2.add(Box.createHorizontalStrut(5));
		ppoRow2.add(this.bAppOutputClear);
		ppoRow2.add(Box.createHorizontalStrut(5));
		//------ add panel
		this.pProject.add(ppoRow2);

		//-----------------------------------------------------------------------------------------------
		//------ set bottom glue
		//-----------------------------------------------------------------------------------------------
		this.pProject.add(Box.createVerticalGlue());
		this.pProject.add(Box.createVerticalStrut(5));						
		
		
	} // End void setProjectPanel	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the supplementary environmental files options panel 
	 */
	private void setPanelFileList(){
				
		//initialize the panel
		this.pFileList						= new JPanel();
		
		//local attributes
		int					strutHor		= 50;
		
		//set border and layout
		this.pFileList.setLayout(new BoxLayout(this.pFileList, BoxLayout.PAGE_AXIS));
		this.pFileList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pFileList.add(this.setPanelExtra(" Supplementary environment files"));
		this.pFileList.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: files list
		//-----------------------------------------------------------------------------------------------
		JPanel			pfList				= new JPanel();
		//--- set border and layout
		pfList.setLayout(new BoxLayout(pfList, BoxLayout.Y_AXIS));
		pfList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- set background color		
		pfList.setAlignmentY(JPanel.TOP_ALIGNMENT);		
		pfList.setBackground(IAppUtils.COLOR_COMPONENTS);
		//--- add panel
		this.pFileList.add(pfList);
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: file grid
		//-----------------------------------------------------------------------------------------------
		JPanel			pfiRow1				= new JPanel();
		//------ set border and layout
		pfiRow1.setLayout(new BoxLayout(pfiRow1, BoxLayout.X_AXIS));
		pfiRow1.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));		
		//------ create components		
		boolean			status				= this.initTable(ClusterApp.TABLE_FILES, null);
		//------ configure components
		this.filesTable.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add components 
		if (status == true){
			pfiRow1.add(this.filesScroll);
		} else {
			//show an error message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Unable to initialize the ef files table.",
					"Files table error",
					JOptionPane.ERROR_MESSAGE);
		}				
		//------ add panel
		pfList.add(pfiRow1);		
		//-----------------------------------------------------------------------------------------------
		//--- set row 2: files info + checkboxes
		//-----------------------------------------------------------------------------------------------
		JPanel			pfiRow2				= new JPanel();
		//------ set border and layout
		pfiRow2.setLayout(new BoxLayout(pfiRow2, BoxLayout.X_AXIS));
		pfiRow2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		//------ set background color
		pfiRow2.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ create components
		this.ckSelectAll					= new JCheckBox("Select all");
		this.ckSelectNone					= new JCheckBox("Clear selection");
		this.filesLabel						= new JLabel("");
		this.initFilesSelectedInfo();
		//------ configure components
		this.ckSelectAll.setBackground(IAppUtils.COLOR_COMPONENTS);
		this.ckSelectNone.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ set action listeners
		this.ckSelectAll.addActionListener(this);
		this.ckSelectNone.addActionListener(this);
		//------ add components
		pfiRow2.add(this.ckSelectNone);
		pfiRow2.add(Box.createHorizontalStrut(10));
		pfiRow2.add(this.ckSelectAll);
		pfiRow2.add(Box.createHorizontalStrut(strutHor));
		pfiRow2.add(Box.createHorizontalGlue());
		pfiRow2.add(this.filesLabel);				
		pfiRow2.add(Box.createHorizontalStrut(5));
		//------ add panel
		pfList.add(pfiRow2);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pFileList.add(Box.createVerticalStrut(30));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pFileList.add(this.setPanelExtra(" File content"));
		this.pFileList.add(Box.createVerticalStrut(10));
			
		//-----------------------------------------------------------------------------------------------
		//set panel: file content
		//-----------------------------------------------------------------------------------------------
		JPanel			pfOutput			= new JPanel();
		//--- set border and layout
		pfOutput.setLayout(new BoxLayout(pfOutput, BoxLayout.Y_AXIS));
		pfOutput.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pFileList.add(pfOutput);
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: content
		//-----------------------------------------------------------------------------------------------
		//------ create components
		this.txFileContent					= new JTextArea(" Double click a file name to load the content ", 12, 60);
		JScrollPane		contentScroll		= new JScrollPane(this.txFileContent);
		Font 			contentFont			= this.txFileContent.getFont();		
		//------ tune components
		this.txFileContent.setBackground(IAppUtils.COLOR_COMPONENTS);
		this.txFileContent.setEditable(false);
		this.txFileContent.setLineWrap(true);
		this.txFileContent.setFont(new Font(contentFont.getFamily(), Font.ITALIC, 12));
		//------ add components
		pfOutput.add(contentScroll);		
						
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pFileList.add(Box.createVerticalGlue());
		this.pFileList.add(Box.createRigidArea(new Dimension(5,5)));
		
		
	} // End void setFileLIstPanel 
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the op_mksim panel 
	 */
	private void setPanelMKSim(){
				
		//initialize panel
		this.pMKSIM							= new JPanel();
		
		//local attributes
		int					comboWidth		= 500;
		int					comboHeight		= 25;
		int					buttonWidth		= 100;
		int					buttonHeight	= 25;
		int					strutVer		= 10;
		int					strutHor		= 50;
		
		//set border and layout
		this.pMKSIM.setLayout(new BoxLayout(this.pMKSIM, BoxLayout.PAGE_AXIS));
		this.pMKSIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pMKSIM.add(this.setPanelExtra(" Selected networks (environmental files)"));
		this.pMKSIM.add(Box.createVerticalStrut(10));
				
		//-----------------------------------------------------------------------------------------------
		//set panel: net names info
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: title and space
		//-----------------------------------------------------------------------------------------------
		JPanel			pmpRow1				= new JPanel();	
		//------ set border and layout
		pmpRow1.setLayout(new BoxLayout(pmpRow1, BoxLayout.X_AXIS));
		pmpRow1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//------ set background color
		pmpRow1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ create components
		this.cbNetsList						= new JComboBox();
		this.bNetsSave						= new JButton("Save");
		this.bNetsReset						= new JButton("Discard");
		//------ setup components
		this.bNetsReset.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bNetsSave.setPreferredSize(new Dimension (buttonWidth, buttonHeight));
		this.cbNetsList.setPreferredSize(new Dimension(comboWidth, comboHeight));
		//------ add listeners
		this.cbNetsList.addActionListener(this);
		this.bNetsSave.addActionListener(this);
		this.bNetsReset.addActionListener(this);
		//------ add components
		pmpRow1.add(this.cbNetsList);
		pmpRow1.add(Box.createHorizontalStrut(strutHor));
		pmpRow1.add(Box.createHorizontalGlue());
		pmpRow1.add(this.bNetsSave);
		pmpRow1.add(Box.createHorizontalStrut(5));
		pmpRow1.add(this.bNetsReset);
		//------ add panel
		this.pMKSIM.add(pmpRow1);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pMKSIM.add(Box.createVerticalStrut(strutVer*3));
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pMKSIM.add(this.setPanelExtra(" Params op_mksim command"));
		this.pMKSIM.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: info text area
		//-----------------------------------------------------------------------------------------------
		JPanel			pmpRow3				= new JPanel();
		//------ set border and layout 
		pmpRow3.setLayout(new BoxLayout(pmpRow3, BoxLayout.X_AXIS));
		pmpRow3.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txMKSIMParams					= new JTextArea("", 15, 60);
		this.deMKSIMParams					= this.txMKSIMParams.getDocument();
		JScrollPane		paramsScroll		= new JScrollPane(this.txMKSIMParams);
		this.txMKSIMParams.setEditable(true);
		this.txMKSIMParams.setLineWrap(true);
		//------ set background color
		this.txMKSIMParams.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add listeners
		this.deMKSIMParams.addDocumentListener(this);
		//------ add components
		pmpRow3.add(paramsScroll);
		//------ add panel
		this.pMKSIM.add(pmpRow3);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pMKSIM.add(Box.createVerticalStrut(strutVer*3));
				
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pMKSIM.add(this.setPanelExtra(" Help op_mksim command)"));
		this.pMKSIM.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: op_mksim help pane
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: help text area
		//-----------------------------------------------------------------------------------------------
		JPanel			pmhRow1				= new JPanel();
		//------ set border and layout
		pmhRow1.setLayout(new BoxLayout(pmhRow1, BoxLayout.X_AXIS));
		pmhRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txMKSIMHelp					= new JTextArea("", 15, 60);
		JScrollPane		helpScroll			= new JScrollPane(this.txMKSIMHelp);
		Font			helpFont			= this.txMKSIMHelp.getFont();
		this.txMKSIMHelp.setEditable(false);
		this.txMKSIMHelp.setFont(new Font(helpFont.getFamily(), Font.PLAIN, 10));
		//------ load the op_mksim command help
		try {
			this.txMKSIMHelp.setText(this.opProject.getMKSIMHelp());
		} catch (OpnetHeavyException e) {
			//show the error messages in the text area
			this.txMKSIMHelp.setText("");
			this.txMKSIMHelp.append("Unable to get the op_mksim command help (see the log file)");
			this.txMKSIMHelp.setForeground(ClusterApp.TX_COLOR_STDERR);
			//log the error
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", setMK_SIMPanel");
		}
		//------ set background color
		this.txMKSIMHelp.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add components
		pmhRow1.add(helpScroll);	
		//------ add panel
		this.pMKSIM.add(pmhRow1);
		
		
	} // End void setMK_SIMPanel
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the sim file panel 
	 */
	private void setPanelDTSim(){
		
		//initialize panel
		this.pDTSIM						= new JPanel();
		
		//local attributes
		int					comboWidth		= 500;
		int					comboHeight		= 25;
		int					buttonWidth		= 100;
		int					buttonHeight	= 25;
		int					strutVer		= 10;
		int					strutHor		= 50;
		
		//set border and layout
		this.pDTSIM.setLayout(new BoxLayout(this.pDTSIM, BoxLayout.PAGE_AXIS));
		this.pDTSIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pDTSIM.add(this.setPanelExtra(" Simulations to enqueue"));
		this.pDTSIM.add(Box.createVerticalStrut(10));

		//-----------------------------------------------------------------------------------------------
		//set panel: sim list
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: sim combo box
		//-----------------------------------------------------------------------------------------------
		JPanel			pslCombo			= new JPanel();
		//------ set border and layout
		pslCombo.setLayout(new BoxLayout(pslCombo, BoxLayout.X_AXIS));
		pslCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//------ create components
		this.cbSimsList						= new JComboBox();
		this.bSimsSave						= new JButton("Save");
		this.bSimsReset						= new JButton("Discard");
		//------ setup components
		this.bSimsReset.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bSimsSave.setPreferredSize(new Dimension (buttonWidth, buttonHeight));
		this.cbSimsList.setPreferredSize(new Dimension(comboWidth, comboHeight));
		this.cbSimsList.setMinimumSize(new Dimension(comboWidth, comboHeight));
		this.cbSimsList.setMaximumSize(new Dimension(comboWidth, comboHeight));
		//------ set background color
		pslCombo.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add listeners
		this.cbSimsList.addActionListener(this);
		this.bSimsSave.addActionListener(this);
		this.bSimsReset.addActionListener(this);
		//------ add components
		pslCombo.add(this.cbSimsList);
		pslCombo.add(Box.createHorizontalStrut(strutHor));
		pslCombo.add(Box.createHorizontalGlue());
		pslCombo.add(this.bSimsSave);
		pslCombo.add(Box.createHorizontalStrut(5));
		pslCombo.add(this.bSimsReset);
		//------ add panel
		this.pDTSIM.add(pslCombo);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pDTSIM.add(Box.createVerticalStrut(strutVer*3));
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pDTSIM.add(this.setPanelExtra(" Params simulations"));
		this.pDTSIM.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim file string
		//-----------------------------------------------------------------------------------------------
		JPanel			pslCode				= new JPanel();
		//------ set border and layout
		pslCode.setLayout(new BoxLayout(pslCode, BoxLayout.X_AXIS));
		pslCode.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txDTSIMParams					= new JTextArea("", 15, 60);
		this.deDTSIMParams					= this.txDTSIMParams.getDocument();
		JScrollPane		paramsScroll		= new JScrollPane(this.txDTSIMParams);
		this.txDTSIMParams.setEditable(true);
		this.txDTSIMParams.setLineWrap(true);
		//------ set background color
		this.txDTSIMParams.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add listeners
		this.deDTSIMParams.addDocumentListener(this);
		//------ add components
		pslCode.add(paramsScroll);
		//------ add panel
		this.pDTSIM.add(pslCode);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pDTSIM.add(Box.createVerticalStrut(strutVer*3));
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pDTSIM.add(this.setPanelExtra(" Help simulations"));
		this.pDTSIM.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim file help
		//-----------------------------------------------------------------------------------------------
		JPanel			pslHelp				= new JPanel();
		//------ set border and layout
		pslHelp.setLayout(new BoxLayout(pslHelp, BoxLayout.X_AXIS));
		pslHelp.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txDTSIMHelp					= new JTextArea("", 15, 60);
		JScrollPane		helpScroll			= new JScrollPane(this.txDTSIMHelp);
		Font			helpFont			= this.txDTSIMHelp.getFont();
		this.txDTSIMHelp.setEditable(false);
		this.txDTSIMHelp.setFont(new Font(helpFont.getFamily(), Font.PLAIN, 10));
		//------ set background color
		this.txDTSIMHelp.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ add components
		pslHelp.add(helpScroll);
		//------ add panel
		this.pDTSIM.add(pslHelp);

		
	} //End void setDT_SIMPanel
		
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the help panel
	 */
	@SuppressWarnings("serial")
	private void setPanelPrefs(){
				
		//initialize panel
		this.pPrefs							= new JPanel();
		
		//local attributes
		int					comboWidth		= 200;
		int					comboHeight		= 25;
		int					buttonWidth		= 200;
		int					buttonHeight	= 20;
		int					labelWidth		= 150;
		int					labelHeight		= 25;
		int					txSize			= 15;
		int					strutVer		= 10;
		int					strutHor		= 20;
		
		//set border and layout
		this.pPrefs.setLayout(new BoxLayout(this.pPrefs, BoxLayout.PAGE_AXIS));
		this.pPrefs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(this.setPanelExtra(" Preferences"));
		this.pPrefs.add(Box.createVerticalStrut(10));
		
		//-----------------------------------------------------------------------------------------------
		//set panel prefs step 1
		//-----------------------------------------------------------------------------------------------
		JPanel				pp1Row1			= new JPanel();
		JPanel				lp1Title		= new JPanel();
		JLabel				lp1Label		= new JLabel(" Step 1: select project file");
		//------ set border and layout
		pp1Row1.setLayout(new BoxLayout(pp1Row1, BoxLayout.Y_AXIS));
		pp1Row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ set background color
		pp1Row1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ configure components
		//--------- step title
		lp1Title.setLayout(new BoxLayout(lp1Title, BoxLayout.X_AXIS));
		lp1Title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lp1Title.setBackground(IAppUtils.COLOR_NOT_APPLIED);
		lp1Title.add(lp1Label);
		lp1Title.add(Box.createHorizontalGlue());
		//------ add components
		pp1Row1.add(lp1Title);
		pp1Row1.add(Box.createRigidArea(new Dimension(strutHor, strutVer*2)));
		//------ add panel
		this.pPrefs.add(pp1Row1);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(Box.createVerticalStrut(strutVer));
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel prefs step 2
		//-----------------------------------------------------------------------------------------------
		JPanel				pp2Row1			= new JPanel();
		JPanel				lp2Title		= new JPanel();
		JPanel				lp2Opts			= new JPanel();
		JLabel				lp2Label		= new JLabel(" Step 2: op_mksim command");
		JLabel				lOpVer			= new JLabel("OPNET Version:");
		this.cbOpnetVersion					= new JComboBox();
		//------ set border and layout
		pp2Row1.setLayout(new BoxLayout(pp2Row1, BoxLayout.Y_AXIS));
		pp2Row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ set background color
		pp2Row1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ configure components
		//--------- step title
		lp2Title.setLayout(new BoxLayout(lp2Title, BoxLayout.X_AXIS));
		lp2Title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lp2Title.setBackground(IAppUtils.COLOR_NOT_APPLIED);
		lp2Title.add(lp2Label);
		lp2Title.add(Box.createHorizontalGlue());
		//--------- step options
		lp2Opts.setLayout(new BoxLayout(lp2Opts, BoxLayout.X_AXIS));
		lp2Opts.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp2Opts.setBackground(IAppUtils.COLOR_COMPONENTS);
		//--------- combobox
		this.cbOpnetVersion.addItem(IAppUtils.OPNET_14_0_A);
		this.cbOpnetVersion.addItem(IAppUtils.OPNET_14_5);
		this.cbOpnetVersion.addItem(IAppUtils.OPNET_16_0);
		this.cbOpnetVersion.setEditable(false);
		this.cbOpnetVersion.setEnabled(false);
		this.cbOpnetVersion.setPreferredSize(new Dimension(comboWidth, comboHeight));
		//--------- label
		lOpVer.setFont(new Font(lOpVer.getFont().getFamily(), Font.PLAIN, 12));
		lOpVer.setPreferredSize(new Dimension(labelWidth, labelHeight));
		//row0
		lp2Opts.add(lOpVer);
		lp2Opts.add(Box.createHorizontalStrut(strutHor));
		lp2Opts.add(this.cbOpnetVersion);
		lp2Opts.add(Box.createHorizontalGlue());
		lp2Opts.add(Box.createHorizontalStrut(strutHor*20));
		//------ add components
		pp2Row1.add(lp2Title);
		pp2Row1.add(lp2Opts);
		//------ add panel
		this.pPrefs.add(pp2Row1);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(Box.createVerticalStrut(strutVer));

	
		//-----------------------------------------------------------------------------------------------
		//set panel prefs step 3
		//-----------------------------------------------------------------------------------------------
		JPanel				pp3Row1			= new JPanel();
		JPanel				lp3Title		= new JPanel();
		JPanel				lp3Opts1		= new JPanel();
		JPanel				lp3Opts2		= new JPanel();
		JPanel				lp3Opts3		= new JPanel();
		JPanel				lp3Opts4		= new JPanel();
		JPanel				lp3Opts5		= new JPanel();
		JPanel				lp3Opts6		= new JPanel();
		JLabel				lp3Label		= new JLabel(" Step 3: submit simulations");
		JLabel				lSimQueue		= new JLabel("Queue:");
		JLabel				lSimLicNumber	= new JLabel("Licences:");
		JLabel				lSimPriority	= new JLabel("Priority:");
		JLabel				lSimOutDir		= new JLabel("Output dir:");
		JLabel				lSimErrDir		= new JLabel("Error dir:");
		JLabel				lSimScrDir		= new JLabel("Scripts dir:");
		this.lOutDir						= new JLabel("(default dir)");
		this.lErrDir						= new JLabel("(default dir)");
		this.lScrDir						= new JLabel("(default dir)");		
		this.tfSimQueue						= new JTextField("cluster.q", 	txSize);
		this.tfSimPriority					= new JTextField("0", 			txSize);
		this.tfSimLicNumber					= new JTextField("1", 			txSize);
		this.bSimOutDir						= new JButton("Select out dir");
		this.bSimErrDir						= new JButton("Select error dir");
		this.bSimScrDir						= new JButton("Select scripts dir");
		this.dOutChooser					= new JFileChooser(){
			@Override
			public void approveSelection(){ 
				if (getSelectedFile().isFile()){ return; }
				super.approveSelection();
			}
		};	
		this.dErrChooser					= new JFileChooser(){
			@Override
			public void approveSelection(){ 
				if (getSelectedFile().isFile()){ return; }
				super.approveSelection();
			}
		};	
		this.dScrChooser					= new JFileChooser(){
			@Override
			public void approveSelection(){ 
				if (getSelectedFile().isFile()){ return; }
				super.approveSelection();
			}
		};	
//		this.dOutChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dOutChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.dOutChooser.setAcceptAllFileFilterUsed(false);
		this.dOutChooser.setMultiSelectionEnabled(false);
//		this.dErrChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dErrChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.dErrChooser.setAcceptAllFileFilterUsed(false);
		this.dErrChooser.setMultiSelectionEnabled(false);
//		this.dScrChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dScrChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.dScrChooser.setAcceptAllFileFilterUsed(false);
		this.dScrChooser.setMultiSelectionEnabled(false);
		//------ add listeners
		this.bSimOutDir.addActionListener(this);
		this.bSimErrDir.addActionListener(this);
		this.bSimScrDir.addActionListener(this);
		//------ set border and layout
		pp3Row1.setLayout(new BoxLayout(pp3Row1, BoxLayout.PAGE_AXIS));
		pp3Row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ set background color
		pp3Row1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ configure components
		//--------- buttons
		this.bSimOutDir.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bSimErrDir.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bSimScrDir.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		//--------- queue 
		this.tfSimQueue.setEnabled(true);
		this.tfSimQueue.setEditable(false);
		this.tfSimQueue.setHorizontalAlignment(JTextField.RIGHT);
		this.tfSimQueue.setFont(new Font(this.tfSimQueue.getFont().getFamily(), Font.PLAIN, 12));
		//--------- licenses number
		this.tfSimLicNumber.setEnabled(true);
		this.tfSimLicNumber.setEditable(false);
		this.tfSimLicNumber.setHorizontalAlignment(JTextField.RIGHT);
		this.tfSimLicNumber.setFont(new Font(this.tfSimLicNumber.getFont().getFamily(), Font.PLAIN, 12));
		//--------- job priority
		this.tfSimPriority.setEnabled(true);
		this.tfSimPriority.setEditable(true);
		this.tfSimPriority.setHorizontalAlignment(JTextField.RIGHT);
		this.tfSimPriority.setFont(new Font(this.tfSimPriority.getFont().getFamily(), Font.PLAIN, 12));
		//--------- labels
		lSimQueue.setFont(new Font(lSimQueue.getFont().getFamily(), Font.PLAIN, 12));
		lSimLicNumber.setFont(new Font(lSimLicNumber.getFont().getFamily(), Font.PLAIN, 12));
		lSimPriority.setFont(new Font(lSimPriority.getFont().getFamily(), Font.PLAIN, 12));
		lSimOutDir.setFont(new Font(lSimOutDir.getFont().getFamily(), Font.PLAIN, 12));
		lSimErrDir.setFont(new Font(lSimErrDir.getFont().getFamily(), Font.PLAIN, 12));
		lSimScrDir.setFont(new Font(lSimScrDir.getFont().getFamily(), Font.PLAIN, 12));
		lSimQueue.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lSimLicNumber.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lSimPriority.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lSimOutDir.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lSimErrDir.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lSimScrDir.setPreferredSize(new Dimension(labelWidth, labelHeight));		
		//--------- step title
		lp3Title.setLayout(new BoxLayout(lp3Title, BoxLayout.X_AXIS));
		lp3Title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lp3Title.setBackground(IAppUtils.COLOR_NOT_APPLIED);
		lp3Title.add(lp3Label);
		lp3Title.add(Box.createHorizontalGlue());
		//--------- step options
		//row1
		lp3Opts1.setLayout(new BoxLayout(lp3Opts1, BoxLayout.X_AXIS));
		lp3Opts1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts1.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts1.add(lSimQueue);
		lp3Opts1.add(Box.createHorizontalStrut(strutHor));
		lp3Opts1.add(this.tfSimQueue);
		lp3Opts1.add(Box.createHorizontalGlue());
		lp3Opts1.add(Box.createHorizontalStrut(strutHor*20));
		//row2
		lp3Opts2.setLayout(new BoxLayout(lp3Opts2, BoxLayout.X_AXIS));
		lp3Opts2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts2.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts2.add(lSimLicNumber);
		lp3Opts2.add(Box.createHorizontalStrut(strutHor));
		lp3Opts2.add(this.tfSimLicNumber);
		lp3Opts2.add(Box.createHorizontalGlue());
		lp3Opts2.add(Box.createHorizontalStrut(strutHor*20));
		//row3
		lp3Opts3.setLayout(new BoxLayout(lp3Opts3, BoxLayout.X_AXIS));
		lp3Opts3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts3.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts3.add(lSimPriority);
		lp3Opts3.add(Box.createHorizontalStrut(strutHor));
		lp3Opts3.add(this.tfSimPriority);
		lp3Opts3.add(Box.createHorizontalGlue());
		lp3Opts3.add(Box.createHorizontalStrut(strutHor*20));
		//row4
		lp3Opts4.setLayout(new BoxLayout(lp3Opts4, BoxLayout.X_AXIS));
		lp3Opts4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts4.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts4.add(lSimOutDir);
		lp3Opts4.add(Box.createHorizontalStrut(strutHor));
		lp3Opts4.add(this.bSimOutDir);
		lp3Opts4.add(Box.createHorizontalStrut(20));
		lp3Opts4.add(this.lOutDir);
		lp3Opts4.add(Box.createHorizontalGlue());
		//row5
		lp3Opts5.setLayout(new BoxLayout(lp3Opts5, BoxLayout.X_AXIS));
		lp3Opts5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts5.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts5.add(lSimErrDir);
		lp3Opts5.add(Box.createHorizontalStrut(strutHor));
		lp3Opts5.add(this.bSimErrDir);
		lp3Opts5.add(Box.createHorizontalStrut(20));
		lp3Opts5.add(this.lErrDir);
		lp3Opts5.add(Box.createHorizontalGlue());
		//row6
		lp3Opts6.setLayout(new BoxLayout(lp3Opts6, BoxLayout.X_AXIS));
		lp3Opts6.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lp3Opts6.setBackground(IAppUtils.COLOR_COMPONENTS);
		lp3Opts6.add(lSimScrDir);
		lp3Opts6.add(Box.createHorizontalStrut(strutHor));
		lp3Opts6.add(this.bSimScrDir);
		lp3Opts6.add(Box.createHorizontalStrut(20));
		lp3Opts6.add(this.lScrDir);
		lp3Opts6.add(Box.createHorizontalGlue());		
		//------ add components
		pp3Row1.add(lp3Title);
		pp3Row1.add(lp3Opts1);
		pp3Row1.add(lp3Opts2);
		pp3Row1.add(lp3Opts3);
		pp3Row1.add(lp3Opts4);
		pp3Row1.add(lp3Opts5);
		pp3Row1.add(lp3Opts6);
		//------ add panel
		this.pPrefs.add(pp3Row1);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(Box.createVerticalStrut(strutVer*2));
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(this.setPanelExtra(" Utilities"));
		this.pPrefs.add(Box.createVerticalStrut(10));

		//-----------------------------------------------------------------------------------------------
		//set panel utilities
		//-----------------------------------------------------------------------------------------------
		JPanel				putRow1			= new JPanel();
		JPanel				putRow0			= new JPanel();
		JPanel				putRow2			= new JPanel();
		JLabel				lUt1Label		= new JLabel(" Remove script files: ");
		JLabel				lUt2Label		= new JLabel(" Kill simulations: ");
		JLabel				lUt3Label		= new JLabel(" Remove .sim files: ");
		JLabel				lUt1Warning		= new JLabel(" Be careful! Don't remove old scripts!");
		JLabel				lUt2Warning		= new JLabel(" Be careful! Don't kill healty sims!");
		JLabel				lUt3Warning		= new JLabel(" Be careful! Don't remove non-started sims!");
		this.bKillJobs						= new JButton("Kill all sims");
		this.bRemoveSimFiles				= new JButton("Select target dir");
		this.bRemoveScrFiles				= new JButton("Select target dir");
		this.dOldsims						= new JFileChooser(){
			@Override
			public void approveSelection(){ 
				if (getSelectedFile().isFile()){ return; }
				super.approveSelection();
			}
		};
		this.dOldScripts					= new JFileChooser(){
			@Override
			public void approveSelection(){ 
				if (getSelectedFile().isFile()){ return; }
				super.approveSelection();
			}
		};		 
//		this.dOldScripts.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dOldScripts.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.dOldScripts.setFileFilter(new FileNameExtensionFilter("Bash script files (*.sh)","sh"));
		this.dOldScripts.setAcceptAllFileFilterUsed(false);
		this.dOldScripts.setMultiSelectionEnabled(false);
//		this.dOldsims.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dOldsims.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.dOldsims.setFileFilter(new FileNameExtensionFilter("Simulation files (*.sim)","sim"));
		this.dOldsims.setAcceptAllFileFilterUsed(false);
		this.dOldsims.setMultiSelectionEnabled(false);
		//------ set border and layout
		putRow1.setLayout(new BoxLayout(putRow1, BoxLayout.X_AXIS));
		putRow1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		putRow0.setLayout(new BoxLayout(putRow0, BoxLayout.X_AXIS));
		putRow0.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		putRow2.setLayout(new BoxLayout(putRow2, BoxLayout.X_AXIS));
		putRow2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//------ set background color
		putRow1.setBackground(IAppUtils.COLOR_COMPONENTS);
		putRow0.setBackground(IAppUtils.COLOR_COMPONENTS);
		putRow2.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ configure components
		this.bRemoveScrFiles.addActionListener(this);
		this.bRemoveScrFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bRemoveSimFiles.addActionListener(this);
		this.bRemoveSimFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		this.bKillJobs.addActionListener(this);
		this.bKillJobs.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		lUt1Label.setFont(new Font(lUt1Label.getFont().getFamily(), Font.PLAIN, 12));
		lUt1Label.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lUt2Label.setFont(new Font(lUt2Label.getFont().getFamily(), Font.PLAIN, 12));
		lUt2Label.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lUt3Label.setFont(new Font(lUt3Label.getFont().getFamily(), Font.PLAIN, 12));
		lUt3Label.setPreferredSize(new Dimension(labelWidth, labelHeight));
		lUt1Warning.setForeground(Color.RED);
		lUt2Warning.setForeground(Color.RED);
		lUt3Warning.setForeground(Color.RED);
		//------ add components
		//row1
		putRow1.add(lUt1Label);
		putRow1.add(Box.createHorizontalStrut(strutHor));
		putRow1.add(this.bRemoveScrFiles);
		putRow1.add(Box.createHorizontalStrut(20));
		putRow1.add(lUt1Warning);
		putRow1.add(Box.createHorizontalGlue());
		//row2
		putRow2.add(lUt3Label);
		putRow2.add(Box.createHorizontalStrut(strutHor));
		putRow2.add(this.bRemoveSimFiles);
		putRow2.add(Box.createHorizontalStrut(20));
		putRow2.add(lUt3Warning);
		putRow2.add(Box.createHorizontalGlue());
		//row0
		putRow0.add(lUt2Label);
		putRow0.add(Box.createHorizontalStrut(strutHor));
		putRow0.add(this.bKillJobs);
		putRow0.add(Box.createHorizontalStrut(20));
		putRow0.add(lUt2Warning);
		putRow0.add(Box.createHorizontalGlue());
		//------ add panel
		this.pPrefs.add(putRow0);
		this.pPrefs.add(putRow2);
		this.pPrefs.add(putRow1);
		//-----------------------------------------------------------------------------------------------
		//set bottom space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(Box.createVerticalStrut(strutVer*2));
		
		
		//-----------------------------------------------------------------------------------------------
		//set title and space
		//-----------------------------------------------------------------------------------------------
		this.pPrefs.add(this.setPanelExtra(" About"));
		this.pPrefs.add(Box.createVerticalStrut(10));

		//-----------------------------------------------------------------------------------------------
		//set panel about
		//-----------------------------------------------------------------------------------------------
		JPanel				pabRow1			= new JPanel();
		JPanel				pabInner		= new JPanel();
		JLabel				lLine1			= new JLabel();
		JLabel				lLine2			= new JLabel();
		JLabel				lLine3			= new JLabel();
		Date 				lDate			= new Date();
		DateFormat			lFormatCopy		= new SimpleDateFormat("yyyy");
		//------ set border and layout
		pabRow1.setLayout(new FlowLayout());
		pabRow1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//------ set background color
		pabRow1.setBackground(IAppUtils.COLOR_COMPONENTS);
		//------ configure components
		lLine1.setFont(new Font(lLine1.getFont().getFamily(), Font.BOLD, 14));
		lLine2.setFont(new Font(lLine2.getFont().getFamily(), Font.PLAIN, 12));
		lLine3.setFont(new Font(lLine3.getFont().getFamily(), Font.BOLD, 10));
		lLine1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lLine2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lLine3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lLine1.setText("Cluster GUI for OPNET Modeler\u00ae");
		lLine2.setText("Version 2011.0310");
		lLine3.setText("Copyright \u00a9" + " 2010-" + lFormatCopy.format(lDate) + " CAOS-UAB (Gonzalo Zarza).");
		pabInner.setLayout(new BoxLayout(pabInner, BoxLayout.Y_AXIS));
		pabInner.setBackground(IAppUtils.COLOR_COMPONENTS);
		pabInner.setAlignmentX(Component.CENTER_ALIGNMENT);
		//------ add components
		pabInner.add(lLine1);
		pabInner.add(Box.createVerticalStrut(5));
		pabInner.add(lLine2);
		pabInner.add(Box.createVerticalStrut(15));
		pabInner.add(lLine3);
		pabRow1.add(pabInner);
		//------ add panel
		this.pPrefs.add(pabRow1);
		
		
	} // End setPanelHelp
	
	/* ------------------------------------------------------------------------------------------------------------ */
		
	/** Set an extra panel to be used as section title 
	 * 
	 * @param	pTitle			the panel title
	 * @return					the resulting panel
	 * 
	 */
	private JPanel setPanelExtra(String pTitle){
		
		//initializes the panel
		JPanel		panel			= new JPanel();	
		JLabel		text			= new JLabel(pTitle);
		Font		font			= text.getFont();
		
		//set border and layout
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//set background color
		panel.setBackground(Color.GRAY);
		//tune title
		text.setForeground(Color.WHITE);
		text.setFont(new Font(font.getFamily(), Font.BOLD, 14));
		//add title
		panel.add(text);
		panel.add(Box.createHorizontalGlue());
				
		//return the panel
		return (panel);
				
	} // End setPanelExtra
		
	/* ------------------------------------------------------------------------------------------------------------ */
	/**
	 * Initialize the GUI components for the first time
	 */
	private void initGUILook(){
		
		//project pane
		//--- step 2
		this.bRunMKSIM.setEnabled(false);
		//--- step 3
		this.bSubmitSims.setEnabled(false);
		//--- app output
		this.txAppOutput.setEnabled(true);
		this.bAppOutputClear.setEnabled(true);
		this.bAppOutputSave.setEnabled(true);
		
		//file list pane
		this.filesTable.setEnabled(false);
		this.ckSelectNone.setEnabled(false);
		this.ckSelectAll.setEnabled(false);
		this.txFileContent.setEnabled(true);
		
		//setup op_mksim pane
		this.cbNetsList.setEnabled(false);
		this.bNetsSave.setEnabled(false);
		this.bNetsReset.setEnabled(false);
		this.txMKSIMParams.setEnabled(false);
		this.txMKSIMHelp.setEnabled(true);
		
		//setup sim jobs pane
		this.cbSimsList.setEnabled(false);
		this.bSimsSave.setEnabled(false);
		this.bSimsReset.setEnabled(false);
		this.txDTSIMParams.setEnabled(false);
		this.txDTSIMHelp.setEnabled(true);
		
		//prefs/help pane
		this.cbOpnetVersion.setEnabled(false);
		this.tfSimQueue.setEnabled(false);
		this.tfSimLicNumber.setEnabled(false);
		this.tfSimPriority.setEnabled(false);
		this.bSimOutDir.setEnabled(false);
		this.bSimErrDir.setEnabled(false);
		this.bSimScrDir.setEnabled(false);
		this.bKillJobs.setEnabled(false);		
		
	} // End void initGUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 2nd-LEVEL METHODS: PRIMARY FUNCTIONALITIES																	*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Trigger the corresponding action according to the pPhase
	 * 
	 *  @param		pPhase			the phase triggering the actions
	 *  @param 		pState			the phase state
	 *  
	 */
	private void actionTrigger(int pPhase, boolean pState){
		
		/*
		 * -----------------------------------------------------------------------------------------------
		 * System behavior resume
		 * -----------------------------------------------------------------------------------------------
		 * 
		 * phase 1: load project file
		 * --- init:		file load dialog
		 * --- rely on: 	none
		 * --- triggers: 	phase 2
		 *
		 * phase 2: load ef files
		 * --- init: 		correct finalization of step 1
		 * --- rely on: 	phase 1
		 * --- triggers:	none
		 *
		 * phase 3: run command op_mksim
		 * --- init: 		button run
		 * --- rely on: 	phase 2
		 * --- triggers: 	none
		 *  
		 * phase 4: submit simulations to queue
		 * --- init: 		button submit
		 * --- rely on: 	phase 3
		 * --- triggers: 	phase 5 (queue status listener)
		 * 
		 * phase 5: monitor simulation jobs
		 * --- init:		phase 4
		 * --- rely on:		phase 4
		 * --- triggers:	none
		 * 
		 * -----------------------------------------------------------------------------------------------
		 */
		
		//triggers the corresponding actions and configurations
		switch (pPhase){		
		// -----------------------------------------------------------------------------------------------
		// phase 1: load project file
		// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_1_LOAD_PRJ:				
				if (pState == true){
					//enable gui components
					//--- project pane
					//>> nothing to do
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//>> nothing to to
					//--- help pane
					//>> nothing to do
					
					//set corresponding variables
					//>> nothing to do
					
					//apply actions
					//--- interrupts the qstat thread (if necessary)
					if (this.thQstat != null){ this.thQstat.interrupt(); }
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, IAppUtils.STAT_DONE);									
					
					//trigger steps
					this.startPhase2();
					
				} else { // pState == FALSE
					//disable gui components
					//--- project pane
					this.bRunMKSIM.setEnabled(false);
					this.cbOpnetVersion.setEnabled(false);		
					this.bSubmitSims.setEnabled(false);
					//--- file list pane
					this.filesTable.setEnabled(false);
					this.ckSelectNone.setEnabled(false);
					this.ckSelectAll.setEnabled(false);
					this.txFileContent.setEnabled(true);
					//--- setup op_mksim pane
					this.cbNetsList.setEnabled(false);
					this.bNetsSave.setEnabled(false);
					this.bNetsReset.setEnabled(false);
					this.txMKSIMParams.setEnabled(false);
					this.txMKSIMHelp.setEnabled(true);
					//--- setup sim jobs pane
					this.cbSimsList.setEnabled(false);
					this.bSimsSave.setEnabled(false);
					this.bSimsReset.setEnabled(false);
					this.txDTSIMParams.setEnabled(false);
					this.txDTSIMHelp.setEnabled(true);
					//--- help pane
					this.tfSimQueue.setEnabled(false);
					this.tfSimLicNumber.setEnabled(false);
					this.tfSimPriority.setEnabled(false);	
					this.bSimOutDir.setEnabled(false);
					this.bSimErrDir.setEnabled(false);
					this.bSimScrDir.setEnabled(false);
					this.bKillJobs.setEnabled(false);
					
					//reset corresponding variables
					//>> nothing to do
					
					//apply actions
					//--- clear the ef file list
					this.filesModel.resetModel(null, null);
					this.filesModel.fireTableDataChanged();
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, IAppUtils.STAT_FAIL);		
					
					//show error messages
					//>> nothing to do					
				}		
				
				//notify the properties table changes
				this.s1StatusModel.fireTableDataChanged();
				
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 2: load ef file list
			// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_2_LOAD_EF:
				if (pState == true){
					//enable gui components
					//--- project pane
					this.bRunMKSIM.setEnabled(true);
					//this.cbOpnetVersion.setEnabled(true);
					//--- file list pane
					this.filesTable.setEnabled(true);
					this.ckSelectNone.setEnabled(true);
					this.ckSelectAll.setEnabled(true);
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//>> nothing to to
					//--- help pane
					//>> nothing to do
					
					//set corresponding variables
					//>> nothing to do
									
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, IAppUtils.STAT_DONE);
														
					//trigger steps
					//>> nothing to do
					
				} else {	// pState == FALSE
					//disable gui components
					//--- project pane
					this.bRunMKSIM.setEnabled(false);
					this.cbOpnetVersion.setEnabled(false);		
					this.bSubmitSims.setEnabled(false);
					//--- file list pane
					this.filesTable.setEnabled(false);
					this.ckSelectNone.setEnabled(false);
					this.ckSelectAll.setEnabled(false);
					this.txFileContent.setEnabled(true);
					//--- setup op_mksim pane
					this.cbNetsList.setEnabled(false);
					this.bNetsSave.setEnabled(false);
					this.bNetsReset.setEnabled(false);
					this.txMKSIMParams.setEnabled(false);
					this.txMKSIMHelp.setEnabled(true);
					//--- setup sim jobs pane
					this.cbSimsList.setEnabled(false);
					this.bSimsSave.setEnabled(false);
					this.bSimsReset.setEnabled(false);
					this.txDTSIMParams.setEnabled(false);
					this.txDTSIMHelp.setEnabled(true);
					//--- help pane
					this.tfSimQueue.setEnabled(false);
					this.tfSimLicNumber.setEnabled(false);
					this.tfSimPriority.setEnabled(false);	
					this.bSimOutDir.setEnabled(false);
					this.bSimErrDir.setEnabled(false);
					this.bSimScrDir.setEnabled(false);
					this.bKillJobs.setEnabled(false);
					
					//reset corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, IAppUtils.STAT_FAIL);
					//show error messages
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Unable to load the supplementary environment files list.\nPlease select another directory.",
							"Load error",
							JOptionPane.ERROR_MESSAGE);					
				}
				
				//notify the properties table changes
				this.s1StatusModel.fireTableDataChanged();
				
				//exit
				break;
			// -----------------------------------------------------------------------------------------------
			// phase 3: run op_mksim
			// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_3_RUN_MKSIM:
				if (pState == true){
					//enable gui components
					//--- project pane
					this.bSubmitSims.setEnabled(true);
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					this.cbSimsList.setEnabled(true);
					this.txDTSIMParams.setEnabled(true);
					//--- help pane
					this.tfSimQueue.setEnabled(true);
					this.tfSimLicNumber.setEnabled(true);
					this.tfSimPriority.setEnabled(true);
					this.bSimOutDir.setEnabled(true);
					this.bSimErrDir.setEnabled(true);
					this.bSimScrDir.setEnabled(true);
					this.bKillJobs.setEnabled(true);			
					
					//set corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_DONE);
					
					//trigger steps
					//>> nothing to do
					
					//apply actions 
					//--- update the list of generated sim files
					this.updateSimsListContent();
					//--- load the sim file help
					this.getSimsFileHelp();
					//--- interrupts the qstat thread (if necessary)
					if (this.thQstat != null){ this.thQstat.interrupt(); }
					
					
				} else {	// pState == FALSE
					//disable gui components
					//--- project pane
					this.bSubmitSims.setEnabled(false);
					//--- file list pane
					this.filesTable.setEnabled(false);
					this.ckSelectNone.setEnabled(false);
					this.ckSelectAll.setEnabled(false);
					this.txFileContent.setEnabled(true);
					//--- setup op_mksim pane
					this.cbNetsList.setEnabled(false);
					this.bNetsSave.setEnabled(false);
					this.bNetsReset.setEnabled(false);
					this.txMKSIMParams.setEnabled(false);
					this.txMKSIMHelp.setEnabled(true);
					//--- setup sim jobs pane
					this.cbSimsList.setEnabled(false);
					this.bSimsSave.setEnabled(false);
					this.bSimsReset.setEnabled(false);
					this.txDTSIMParams.setEnabled(false);
					this.txDTSIMHelp.setEnabled(true);
					//--- help pane
					this.tfSimQueue.setEnabled(false);
					this.tfSimLicNumber.setEnabled(false);
					this.tfSimPriority.setEnabled(false);	
					this.bSimOutDir.setEnabled(false);
					this.bSimErrDir.setEnabled(false);
					this.bSimScrDir.setEnabled(false);
					this.bKillJobs.setEnabled(false);
					
					//reset corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_FAIL);
					
					//apply actions
					//>> nothing to do
					
					//show error messages
					//>> nothing to do
										
				}
				
				//notify the properties table changes
				this.s2StatusModel.fireTableDataChanged();
				
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 4: submit simulation jobs
			// -----------------------------------------------------------------------------------------------				
			case ClusterApp.STEP_4_SUBMIT_SIM:
				if (pState == true){
					//enable gui components
					//--- project pane
					//>> nothing to do
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//--- help pane
					//>> nothing to do				
					
					//set corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, IAppUtils.STAT_DONE);
					
					//apply actions
					//>> nothing to do
					
					//trigger steps
					this.startPhase5();					
					
				} else {	// pState == FALSE
					//disable gui components
					//--- project pane
					//>> nothing to do
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//--- help pane
					//>> nothing to do
					
					//reset corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, IAppUtils.STAT_FAIL);
					
					//apply actions
					//>> nothing to do
					
					//show error messages
					//>> nothing to do
				}
				
				//notify the properties table changes
				this.s3StatusModel.fireTableDataChanged();
				
				//exit
				break;
				
			// -----------------------------------------------------------------------------------------------
			// phase 5: monitor simulation jobs
			// -----------------------------------------------------------------------------------------------				
			case ClusterApp.STEP_5_MONITOR_JOBS:
				if (pState == true){
					//enable gui components
					//--- project pane
					//>> nothing to do
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//--- help pane
					//>> nothing to do
					
					//set corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_QSTAT, IAppUtils.STAT_RUNNING);
					
					//trigger steps
					//>> nothing to do					
					
				} else {	// pState == FALSE
					//disable gui components
					//--- project pane
					//>> nothing to do
					//--- file list pane
					//>> nothing to do
					//--- setup op_mksim pane
					//>> nothing to do
					//--- setup sim jobs pane
					//--- help pane
					//>> nothing to do
					
					//reset corresponding variables
					//>> nothing to do
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_QSTAT, IAppUtils.STAT_FAIL);
					
					//apply actions
					//>> nothing to do
					
					//show error messages
					//>> nothing to do
				}
				
				//notify the properties table changes
				this.s3StatusModel.fireTableDataChanged();
				
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase not recognized error
			// -----------------------------------------------------------------------------------------------
			default:
				this.sysUtils.printlnErr("Ilegal step: " + Integer.toString(pPhase), this.className + ", actionTrigger");
		}
		
		//set the not applied status to the corresponding properties
		if (pState == false){
			this.setNotAppliedStatus(pPhase);
		}
		//notify the files table changes
		this.filesModel.fireTableDataChanged();		
		
	} // End void actionTrigger	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the not applied status to the corresponding properties 
	 * 
	 * @param	pPhase				the phase to start
	 */
	private void setNotAppliedStatus(int pPhase){
		
		//set the not applied status to the corresponding table
		switch (pPhase + 1){
		case ClusterApp.STEP_1_LOAD_PRJ:
			//nothing to do
		case ClusterApp.STEP_2_LOAD_EF:
			this.s1StatusData[1][1]		= IAppUtils.STAT_NOT_APPLIED;
			//$FALL-THROUGH$
		case ClusterApp.STEP_3_RUN_MKSIM:
			this.s2StatusData[0][1]		= IAppUtils.STAT_NOT_APPLIED;
			this.s2StatusData[1][1]		= IAppUtils.STAT_NOT_APPLIED;
			//$FALL-THROUGH$
		case ClusterApp.STEP_4_SUBMIT_SIM:
			this.s3StatusData[0][1]		= IAppUtils.STAT_NOT_APPLIED;
			//$FALL-THROUGH$
		case ClusterApp.STEP_5_MONITOR_JOBS:
			this.s3StatusData[1][1]		= IAppUtils.STAT_NOT_APPLIED;
			//$FALL-THROUGH$
			break;
		case ClusterApp.STEP_5_MONITOR_JOBS + 1:
			//nothing to do
			break;
		default:
			//avoid the index out of range exception
			this.sysUtils.printlnErr(	"Index out of range (pPhase + 1 == " 			+ 
					Integer.toString(pPhase)						+
					", status tables lenght == 2"					+
					")", this.className + ", setNotAppliedStatus"
				);
		}			
		
//		//clear the output
//		this.printAppOutputText(" ", ClusterApp.TX_STDOUT, true);
		
	} // End void setNotAppliedStatus
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the JTable parameters 
	 *
	 * @param	pType			the table type
	 * @param	pData			the table data 
	 * @return					the operation status
	 */
	private boolean initTable(int pType, Object[][] pData){
		
		//operation status flag
		boolean		status				= true;
		//local attributes
		String[]	propsHeader			= new String[]{"Property", "Status"};
		
		switch (pType){
		//-----------------------------------------------------------------------------------------------
		//table: application properties status 
		//-----------------------------------------------------------------------------------------------
		case ClusterApp.TABLE_PROPS_S1:						
			//init table
			this.s1StatusModel			= new PropsTableModel(this.sysUtils, pData, propsHeader);
			this.s1StatusTable			= new JTable(this.s1StatusModel);
			TableCellRenderer render_1	= new PropsTableCellRenderer(this.sysUtils);
			//set the custom renderer
			this.s1StatusTable.getColumnModel().getColumn(0).setCellRenderer(render_1);
			this.s1StatusTable.getColumnModel().getColumn(1).setCellRenderer(render_1);
			this.s1StatusTable.setEnabled(true);
			//set the bg and grid color
			this.s1StatusTable.setBackground(this.mainPanel.getBackground());
			this.s1StatusTable.setGridColor(IAppUtils.COLOR_COMPONENTS);			
			//set cols width
			int 	wsCol0_1			= 120;
			int		wsCol1_1			= 80;
			//--- col 0
			this.s1StatusTable.getColumnModel().getColumn(0).setMinWidth(wsCol0_1);
			this.s1StatusTable.getColumnModel().getColumn(0).setMaxWidth(wsCol0_1);
			this.s1StatusTable.getColumnModel().getColumn(0).setPreferredWidth(wsCol0_1);
			//--- col 1
			this.s1StatusTable.getColumnModel().getColumn(1).setMinWidth(wsCol1_1);
			this.s1StatusTable.getColumnModel().getColumn(1).setMaxWidth(wsCol1_1);
			this.s1StatusTable.getColumnModel().getColumn(1).setPreferredWidth(wsCol1_1);
			//set rows height
			int		wsRows_1			= 20;
			this.s1StatusTable.setRowHeight(wsRows_1);
			//exit
			break;
		//-----------------------------------------------------------------------------------------------
		//table: application properties status 
		//-----------------------------------------------------------------------------------------------
		case ClusterApp.TABLE_PROPS_S2:						
			//init table
			this.s2StatusModel			= new PropsTableModel(this.sysUtils, pData, propsHeader);
			this.s2StatusTable			= new JTable(this.s2StatusModel);
			TableCellRenderer render_2	= new PropsTableCellRenderer(this.sysUtils);
			//set the custom renderer
			this.s2StatusTable.getColumnModel().getColumn(0).setCellRenderer(render_2);
			this.s2StatusTable.getColumnModel().getColumn(1).setCellRenderer(render_2);
			this.s2StatusTable.setEnabled(true);
			//set the bg and grid color
			this.s2StatusTable.setBackground(this.mainPanel.getBackground());
			this.s2StatusTable.setGridColor(IAppUtils.COLOR_COMPONENTS);			
			//set cols width
			int 	wsCol0_2			= 120;
			int		wsCol1_2			= 80;
			//--- col 0
			this.s2StatusTable.getColumnModel().getColumn(0).setMinWidth(wsCol0_2);
			this.s2StatusTable.getColumnModel().getColumn(0).setMaxWidth(wsCol0_2);
			this.s2StatusTable.getColumnModel().getColumn(0).setPreferredWidth(wsCol0_2);
			//--- col 1
			this.s2StatusTable.getColumnModel().getColumn(1).setMinWidth(wsCol1_2);
			this.s2StatusTable.getColumnModel().getColumn(1).setMaxWidth(wsCol1_2);
			this.s2StatusTable.getColumnModel().getColumn(1).setPreferredWidth(wsCol1_2);
			//set rows height
			int		wsRows_2			= 20;
			this.s2StatusTable.setRowHeight(wsRows_2);
			//exit
			break;
		//-----------------------------------------------------------------------------------------------
		//table: application properties status 
		//-----------------------------------------------------------------------------------------------
		case ClusterApp.TABLE_PROPS_S3:						
			//init table
			this.s3StatusModel			= new PropsTableModel(this.sysUtils, pData, propsHeader);
			this.s3StatusTable			= new JTable(this.s3StatusModel);
			TableCellRenderer render_3	= new PropsTableCellRenderer(this.sysUtils);
			//set the custom renderer
			this.s3StatusTable.getColumnModel().getColumn(0).setCellRenderer(render_3);
			this.s3StatusTable.getColumnModel().getColumn(1).setCellRenderer(render_3);
			this.s3StatusTable.setEnabled(true);
			//set the bg and grid color
			this.s3StatusTable.setBackground(this.mainPanel.getBackground());
			this.s3StatusTable.setGridColor(IAppUtils.COLOR_COMPONENTS);			
			//set cols width
			int 	wsCol0_3			= 120;
			int		wsCol1_3			= 80;
			//--- col 0
			this.s3StatusTable.getColumnModel().getColumn(0).setMinWidth(wsCol0_3);
			this.s3StatusTable.getColumnModel().getColumn(0).setMaxWidth(wsCol0_3);
			this.s3StatusTable.getColumnModel().getColumn(0).setPreferredWidth(wsCol0_3);
			//--- col 1
			this.s3StatusTable.getColumnModel().getColumn(1).setMinWidth(wsCol1_3);
			this.s3StatusTable.getColumnModel().getColumn(1).setMaxWidth(wsCol1_3);
			this.s3StatusTable.getColumnModel().getColumn(1).setPreferredWidth(wsCol1_3);
			//set rows height
			int		wsRows_3			= 20;
			this.s3StatusTable.setRowHeight(wsRows_3);
			//exit
			break;
		//-----------------------------------------------------------------------------------------------
		//table: list of ef files
		//-----------------------------------------------------------------------------------------------
		case ClusterApp.TABLE_FILES:
			//init table
			this.filesModel				= new FilesTableModel(this.sysUtils, pData, null);
			this.filesTable				= new JTable(filesModel);
			this.filesScroll			= new JScrollPane(this.filesTable);
			//add sorter
			this.filesTable.setAutoCreateRowSorter(true);
			//add event listeners
			this.filesTable.addMouseListener(this);
			this.filesTable.addKeyListener(this);
			//set prefered size
			this.filesScroll.setPreferredSize(new Dimension(this.getMainPanel().getWidth()-10, 250));
			//set checkbox col width
			int 	wfCol1				= 80;
			//--- col 1
			this.filesTable.getColumnModel().getColumn(1).setMinWidth(wfCol1);
			this.filesTable.getColumnModel().getColumn(1).setMaxWidth(wfCol1);
			this.filesTable.getColumnModel().getColumn(1).setPreferredWidth(wfCol1);			
			//exit
			break;
		//-----------------------------------------------------------------------------------------------
		// table not recognized error
		//-----------------------------------------------------------------------------------------------
		default:
			this.sysUtils.printlnErr("Unable to initialize the table: unknown table type", this.className + ", initJTable");
			status					= false;
		}
					
		return(status);
		
	} //End boolean initTable
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the first phase of the system in order to load the prj file 
	 */
	private void startPhase1(){
		
		//local attributes
		int 		returnVal			= 0;
		boolean 	opStatus			= true;
		String		projectName			= "";
		String		projectPath			= "";
					
		//try to load the project file
		try{
			returnVal					= this.dFileChooser.showOpenDialog(this.pProject);
		} catch (HeadlessException err){
			this.sysUtils.printlnErr(err.getMessage(), this.className + ", startPhase1 (showOpenDialog)");
			opStatus					= false;
		}
		
		//get the project file directory
		if (returnVal == JFileChooser.APPROVE_OPTION){
			
			//get the project name
			projectName					= this.dFileChooser.getSelectedFile().getName();
			//get the project path
			projectPath					= this.dFileChooser.getSelectedFile().getParent();
			
			//one last check
			if (projectName.endsWith(".prj")){
						
				//load the project
				try {
					this.opProject.loadProject(projectPath, projectName);
					
				} catch (OpnetLightException e) {
					//this warning should be shown in the text area
					this.printAppOutputText(" WARNING:  " + e.getMessage(), ClusterApp.TX_STDERR, true);
					//log the warning
					this.sysUtils.printlnWar(e.getMessage(), this.className + ", startPhase1 (loadProject)");
					//show a popup warning message
					JOptionPane.showMessageDialog(
							this.mainPanel,
							e.getMessage() + ".",
							"Warning",
							JOptionPane.WARNING_MESSAGE);	
			
				} catch (OpnetHeavyException e) {
					this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase1 (loadProject)");
					opStatus			= false;
				}			
			
			} else {
				//this error should be shown in the text area
				this.printAppOutputText(" ERROR: Project not found", ClusterApp.TX_STDERR, true);
				//log the error
				this.sysUtils.printlnErr("Project not found", this.className + ", startPhase1 (loadProject)");
				//update the flag
				opStatus				= false;
			}
				
		} else if (returnVal == JFileChooser.ERROR_OPTION){
			//handle the possible error
			this.sysUtils.printlnErr("JFileChooser dialog dismissed", this.className + ", startPhase1 (JFileChooser.ERROR_OPTION)");
			opStatus					= false;
		} else if (returnVal == JFileChooser.CANCEL_OPTION){
			//update the status flag
			opStatus					= false;
		} else {
			//update the status flag
			opStatus					= false;
		}
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_1_LOAD_PRJ, opStatus);

		
	} // End startPhase1
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the second phase of the system in order to load the list of ef files 
	 */
	private void startPhase2(){
		
		//local attributes
		boolean				opStatus	= true;
				
		//load the file list
		try {
			
			//set the running status
			this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, IAppUtils.STAT_RUNNING);
			this.s1StatusModel.fireTableDataChanged();
			
			this.opProject.setNetworksMap();
			
			//load the data in the files table
			this.filesModel.resetModel(null, this.opProject.getFilesData());
						
			//update the ef selected label
			this.updateFilesSelectedInfo();
			
			//print project info 
			this.printProjectInfo();
			
		} catch (OpnetHeavyException e) {
			//log the error
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase2 (setNetworksMap)");
			opStatus					= false;
		} 
				
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_2_LOAD_EF, opStatus);		
		
	} // End startPhase2
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the third phase of the system in order to run the op_mksim command 
	 */
	private void startPhase3(){
		
		/*
		 * Right now this method assumes that all the sim files were correctly generated. 
		 * If there is an error running the op_mksim command, the runMKSIMCmd() method in the
		 * OpnetProject class throws an OpnetHeavyException thus aborting the operation. 
		 * 
		 * This could be changed in a future version, the runMKSIMCmd() method in the OpnetProject 
		 * class should be modified, and also the startPhase3() method in this class (ClusterApp).
		 * 
		 * NOTE: The updateSimsListContent() method in this class (ClusterApp) should be
		 * revisited in that case.
		 * 
		 */
		
		//local attributes
		boolean 			opStatus	= false;
		Iterator<String>	outIt		= null;
		Vector<String>		outVec		= null;
		String				output		= null;
		Set<String>			selNets		= null;
		int 				returnVal	= 0;	
		
		//check if ef files selected
		try {
			selNets							= this.opProject.getSelectedNetworksNames();
			
			if (selNets == null || selNets.size() == 0){
				//show the error in the output text area
				this.printAppOutputText("No environmental file(s) selected!!!", ClusterApp.TX_STDERR, true);
				//show a popup error message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"No environmental file(s) selected!!!",
						"Error",
						JOptionPane.ERROR_MESSAGE);	
				//get out!
				return;
			}
			
		} catch (OpnetHeavyException e) {
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase3");
			//show the error in the output text area
			this.printAppOutputText(e.getMessage(), ClusterApp.TX_STDERR, true);
			return;
		}
		
		//set the running status
		this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_RUNNING);

		//confirms the operation		
		returnVal	= JOptionPane.showOptionDialog(
							this.mainPanel,
							"This operation may take some time... continue?",
							"Confirm operation",
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null,
							null,
							null);
		
		//if NO cancel the operation
		if (returnVal == JOptionPane.NO_OPTION){
			//update the status
			this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_NOT_APPLIED);
			//show the message in the output text area
			this.printAppOutputText("Run op_mksim operation canceled by user.", ClusterApp.TX_STDERR, true);
			//get out
			return; 
		}
		
		//run the op_mksim command for the selected net names
		try {
					
			//disable the run button temporarily
			this.bRunMKSIM.setEnabled(false);
			
			//run the command			
			outVec						= this.opProject.runMKSIMCmd();
			
			this.printAppOutputText(" ", ClusterApp.TX_STDOUT, true);
			
			//load the output into the corresponding text area
			//--- get the stdout and stderr data
			outIt						= outVec.iterator();
			while(outIt.hasNext()){
				output					= outIt.next();
				this.printAppOutputText(output, ClusterApp.TX_STDOUT, false);
			}			
			
			//set the status flag
			opStatus					= true; 
			
			//show a popup notification message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Command op_mksim finished successfully",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);			
			
			//re-enable the run button temporarily
			this.bRunMKSIM.setEnabled(true);
			
		} catch (OpnetHeavyException e) {
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase3");
			//show the error in the output text area
			this.printAppOutputText(e.getMessage(), ClusterApp.TX_STDERR, true);
			//set the status flag
			opStatus					= false;	
		}
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_3_RUN_MKSIM, opStatus);
		
	} // End startPhase3

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the fourth phase of the system in order to submit simulations to queue
	 */
	private void startPhase4(){
		
		//local attributes
		Vector<String>		jobsInfo	= null;
		Iterator<String>	it			= null;
		boolean 			opStatus	= false;
		String				queueName	= null;
		int					opLicNum	= 1;
		float				jobPriority	= 0;
		
		try {
				
			//get the submit jobs params
			queueName					= this.tfSimQueue.getText();
			opLicNum					= Integer.valueOf(this.tfSimLicNumber.getText());
			jobPriority					= Float.valueOf(this.tfSimPriority.getText());
			
			//check priority value
			//from man qsub: 
			//	The  qsub  utility shall accept a value for the priority option-argument 
			//	that conforms to the syntax for signed decimal integers, and which is not
		    //	less than -1024 and not greater than 1023.
			if ((jobPriority < -1024) || (jobPriority > 1023)){
				
				//show the error message
				this.sysUtils.printlnErr(	"Priority value " 				+  
											this.tfSimPriority.getText() 	+ 
											" not valid! Should be [-1024 ; 1023]", 
											this.className + ", startPhase4");
				//show the error in the output text area
				this.printAppOutputText(	"Priority value " 				+  
											this.tfSimPriority.getText() 	+ 
											" not valid! Should be [-1024 ; 1023]", 
											ClusterApp.TX_STDERR, true);
				//set the status flag
				opStatus				= false;
				
			} else {			
								
				//submit jobs
				jobsInfo				= this.opProject.submitSimJobs(	queueName, opLicNum, jobPriority, 
																		this.outDir, this.errDir, this.scrDir);
				//write the output
				if (jobsInfo != null){						
					//print header
					this.printAppOutputText("------------------------------------------------------------", ClusterApp.TX_STDOUT, true);
					this.printAppOutputText(" Enqueued jobs", ClusterApp.TX_STDOUT, false);
					this.printAppOutputText("------------------------------------------------------------", ClusterApp.TX_STDOUT, false);
					//print info
					it					= jobsInfo.iterator();				
					while (it.hasNext()){					
						this.printAppOutputText(it.next(), ClusterApp.TX_STDOUT, false);					
					}				
				}				
				
				//update the completion flag
				opStatus				= true;
				
			}
			
		} catch (OpnetHeavyException e) {
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase4");
			//show the error in the output text area
			this.printAppOutputText(e.getMessage(), ClusterApp.TX_STDERR, true);
			//set the status flag
			opStatus					= false;				
		} catch (NumberFormatException e){
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase4");
			//show the error in the output text area
			this.printAppOutputText(e.getMessage(), ClusterApp.TX_STDERR, true);
			//set the status flag
			opStatus					= false;
		}
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_4_SUBMIT_SIM, opStatus);
		
	} // End startPhase4
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the fifth phase of the system in order to monitor the simulation jobs on the queue (by threads)
	 */
	private void startPhase5(){
		
		//local attributes
		boolean 				opStatus	= false;
		HashMap<String,String>	idsInfo		= null;
		Session					qSession	= null;
				
		//check the previous condition
		if (!this.opProject.isSimSubmitDone()){
			//show the error message
			this.sysUtils.printlnErr("Unable to monitor queued jobs: submit not applied", this.className + ", startPhase5");
			//show the error in the output text area
			this.printAppOutputText("Unable to monitor queued jobs: submit not applied", ClusterApp.TX_STDERR, true);
			//update the status
			this.statusDataSetValue(ClusterApp.LABEL_QSTAT, IAppUtils.STAT_FAIL);
			//get out
			return; 
		}
		//get the sims info
		idsInfo							= this.opProject.getIdsInfo();
		
		//go over the sims list
		if (idsInfo != null){
					
			//get session
			qSession					= this.opProject.getQueueSession();
			
			//init the thread for the qstat
			thQstat						= new Thread(new ThreadForQstat(this.sysUtils, this.txAppOutput, idsInfo, qSession));
			
			//start the qstat
			thQstat.start();
			
			//update flag
			opStatus					= true;
			
		} else {
			//update flag
			opStatus					= false;
		}
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_5_MONITOR_JOBS, opStatus);
		
	} // End startPhase5
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the pValue in the corresponding pField of the statusData
	 *  
	 * @param	pField		the field to update
	 * @param	pValue		the value to set
	 */
	private void statusDataSetValue(String pField, String pValue){

		//avoid null pointer exception
		if (pField == null || pValue == null){ return; }		
		
		//look for the correct field
		if (pField.equals(ClusterApp.LABEL_LOAD_PRJ)){
			//avoid null pointer exception
			if (this.s1StatusData == null){ return; }
			
			this.s1StatusData[0][1]		= pValue;
			this.s1StatusModel.fireTableDataChanged();

			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_1_LOAD_PRJ);
			}
			
		} else if (pField.equals(ClusterApp.LABEL_LOAD_EF)){
			//avoid null pointer exception
			if (this.s1StatusData == null){ return; }
			
			this.s1StatusData[1][1]		= pValue;
			this.s1StatusModel.fireTableDataChanged();
			
			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_2_LOAD_EF);
			}
			
		} else if (pField.equals(ClusterApp.LABEL_CHECK_OP_VER)){
			//avoid null pointer exception
			if (this.s2StatusData == null){ return; }
			
			this.s2StatusData[0][1]		= pValue;
			this.s2StatusModel.fireTableDataChanged();
			
			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_3_RUN_MKSIM);
			}
			
		} else if (pField.equals(ClusterApp.LABEL_RUN_MKSIM)){
			//avoid null pointer exception
			if (this.s2StatusData == null){ return; }
			
			this.s2StatusData[1][1]		= pValue;
			this.s2StatusModel.fireTableDataChanged();
			
			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_3_RUN_MKSIM);
			}
			
		} else if (pField.equals(ClusterApp.LABEL_SUBMIT_SIM)){
			//avoid null pointer exception
			if (this.s3StatusData == null){ return; }
			
			this.s3StatusData[0][1]		= pValue;
			this.s3StatusModel.fireTableDataChanged();
			
			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_4_SUBMIT_SIM);
			}
			
		} else if (pField.equals(ClusterApp.LABEL_QSTAT)){
			//avoid null pointer exception
			if (this.s3StatusData == null){ return; }
			
			this.s3StatusData[1][1]		= pValue;
			this.s3StatusModel.fireTableDataChanged();
			
			if (pValue == IAppUtils.STAT_FAIL || pValue == IAppUtils.STAT_NOT_APPLIED){
				this.setNotAppliedStatus(ClusterApp.STEP_5_MONITOR_JOBS);
			}
			
		} else {
			//error
			this.sysUtils.printlnErr("Unknwon field '"	+ pField + "'", this.className + ", setStatusDataValue");
		}
		
	} // End void setStatusDataValue	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 3rd-LEVEL METHODS: ATTRIBUTES SETUP FUNCTIONALITIES															*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Update the list of unique networks selected */
	private void updateNetsListContent(){
		
		//local attributes
		Set<String>			netNames	= null;
		Iterator<String>	it			= null;
		
		//clear the network names list
		this.cbNetsList.removeAllItems();
		this.cbNetsList.setEnabled(false);
		
		//check the number of ef files
		if (this.opProject.getFilesDataLength() == 0){
			this.cbNetsList.addItem(this.netsListEmpty);
			return;
		}
			
		//load the list
		try{
			//get the included net names list
			netNames					= this.opProject.getSelectedNetworksNames();
			
			if (netNames != null){
				//load the header
				this.cbNetsList.addItem(this.netsListHeader);
				//get the iterator
				it						= netNames.iterator();
				//load the network list
				while (it.hasNext()){
					this.cbNetsList.addItem(it.next());				
				}				
				//enable the list
				this.cbNetsList.setEnabled(true);
			} else {
				//nothing to load
				this.cbNetsList.addItem(this.netsListEmpty);
			}
			
		} catch (OpnetHeavyException e){
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", updateNetsListContent");
			return;
		}
		
		
	} // End void updateNetsListContent
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/** Update the list of sim jobs generated */
	private void updateSimsListContent(){
		
		/*
		 * Right now this method assumes that all the sim files were correctly generated.
		 * This could be changed in a future version modifying the runMKSIMCmd() method in
		 * the OpnetProject class, and the startPhase3() method in this class (ClusterApp).
		 */
		
		//local attributes
		Set<String>			compiledNames	= null;
		Iterator<String>	it				= null;
		
		//clear the sims names list
		this.cbSimsList.removeAllItems();
		this.cbSimsList.setEnabled(false);
		
		//check the number of ef files
		if (!this.opProject.isRunMKSIMDone()){
			this.cbSimsList.addItem(this.simsListEmpty);
			return;
		}
		
		//get the jobs list
		try {
			//get the names
			compiledNames					= this.opProject.getCompiledSimJobsNames();
			
			if (compiledNames != null && compiledNames.size() > 0){
				//load the header
				this.cbSimsList.addItem(this.simsListHeader);
				//get the iterator
				it							= compiledNames.iterator();
				//load the list
				while (it.hasNext()){
					this.cbSimsList.addItem(it.next());
				}				
				//enable the list
				this.cbSimsList.setEnabled(true);
				
			} else {
				//nothing to load 
				this.cbSimsList.addItem(this.simsListEmpty);
			}
			
		} catch (OpnetHeavyException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", updateSimsListContent");
			return;
		}
		
		
	} // End void updateSimsListContent
	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 4th-LEVEL METHODS: OUTPUT FUNCTIONALITIES																	*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Print the pText in the application output text area
	 * 
	 * @param		pText		the text to print
	 * @param		pOutType	the printing mode
	 * @param		pClear		the clear output flag 
	 */
	private void printAppOutputText(String pText, int pOutType, boolean pClear){
		
		//clear the output if necessary
		if (pClear){
			this.txAppOutput.setText("");
		}
		
		//restorte the default font
		this.txAppOutput.setFont(new Font(	this.txAppOutput.getFont().getFamily(), 
											Font.PLAIN, 
											this.txAppOutput.getFont().getSize()));	
		
		//set the fg color according to the printing mode
		switch (pOutType){
		case ClusterApp.TX_STDOUT:
			this.txAppOutput.setForeground(ClusterApp.TX_COLOR_STDOUT);
			break;
		case ClusterApp.TX_STDERR:
			this.txAppOutput.setForeground(ClusterApp.TX_COLOR_STDERR);
			break;
		default:
			this.txAppOutput.setForeground(ClusterApp.TX_COLOR_STDOUT);
			this.sysUtils.printlnErr("Unknown printing mode", this.className + ", printAppOutputText");
		}
		
		//print the text
		if (pText != null){
			this.txAppOutput.append(pText + this.lineBreak);		
		}
		
	} // End void printAppOutputText
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Load the project info in the corresponding text area
	 */
	private void printProjectInfo(){
		
		//local attributes
		StringBuffer		text	= new StringBuffer("");
		
		//add text
		text.append("------------------------------------------------------------" 	+ this.lineBreak);
		text.append(" Project name " 												+ this.lineBreak);
		text.append("------------------------------------------------------------" 	+ this.lineBreak);
		text.append(this.opProject.getProjectName()									+ this.lineBreak);
		text.append(this.lineBreak);
		text.append("------------------------------------------------------------" 	+ this.lineBreak);
		text.append(" Networks found "												+ this.lineBreak);
		text.append("------------------------------------------------------------" 	+ this.lineBreak);
		text.append(this.opProject.getOutputNetworkNames());		
		
		//print the text
		this.printAppOutputText(text.toString(), ClusterApp.TX_STDOUT, true);
		
	} // End void printProjectInfo
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Initialize the ef files info label 
	 */
	private void initFilesSelectedInfo(){
		
		//info strings		
		int				tableSize			= 0;
		int				cellSelected		= 0;
		String			filesNumber			= "0";
		String			filesSelected		= "0";
		
		filesNumber							= "Files found: "		+ Integer.toString(tableSize);
		filesSelected						= "Files selected: " 	+ Integer.toString(cellSelected);
		
		//set the label
		this.filesLabel.setText("[ " + filesNumber + " ]   [ " + filesSelected + " ]");
		
	} // End void initFilesSelectedInfo
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Update the ef files info label 
	 */
	private void updateFilesSelectedInfo(){
		
		//info strings		
		int				tableSize			= 0;
		int				cellSelected		= 0;
		String			filesNumber			= "0";
		String			filesSelected		= "0";
		boolean			cellValue			= false;
		Object[][]		filesData			= null;
		
		//try to load the files data
		try{
			filesData						= this.opProject.getFilesData();
		} catch (OpnetHeavyException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", updateFilesSelectedInfo");
			return;
		}			
		
		//load the real data
		if (filesData != null){
			tableSize						= filesData.length;
		} else {
			return;
		}
				
		//count the number of selected cells
		for (int i = 0; i < tableSize; i++){
			cellValue						= (Boolean) filesData[i][1];
			if (cellValue == true){ 
				cellSelected++;
			}
		}					
		
		filesNumber							= "Files found: "		+ Integer.toString(tableSize);
		filesSelected						= "Files selected: " 	+ Integer.toString(cellSelected);
		
		//set the label
		this.filesLabel.setText("[ " + filesNumber + " ]   [ " + filesSelected + " ]");
		
	} // End void updateFilesSelectedInfo
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/**
	 * Get the help for the sims files
	 */
	private void getSimsFileHelp(){
				
		//get the sim file help
		try{
		
			//load the help in the text area
			this.txDTSIMHelp.setText(this.opProject.getSimsFileHelp());
		
		} catch (OpnetHeavyException e){
			//show the error messages in the text area
			this.txDTSIMHelp.setText("");
			this.txDTSIMHelp.append("Unable to get the sim file help (see the log file)");
			this.txDTSIMHelp.setForeground(ClusterApp.TX_COLOR_STDERR);
			//log the error
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", getSimsFileHelp");
		}
		
	} // End void getSimsFileHelp 
	
	/*
	================================================================================================================== 
	Change Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		// tabbed pane changed
		//---------------------------------------------------------------------------------------------
		if (e.getSource() == this.tabbedPane){
			
			//get the pane title
			JTabbedPane		pane			= (JTabbedPane) e.getSource();
			String			title			= pane.getTitleAt(pane.getSelectedIndex());			
						
			//trigger the op_mksim pane update function
			if (title == ClusterApp.TAB_3_MKSIM){
				this.updateNetsListContent();
			}
				
			//trigger the sim jobs pane update function
			if (title == ClusterApp.TAB_4_SIM){
				this.updateSimsListContent();
			}
			
		}				
		
	} // End void stateChanged
	
	
	/*
	================================================================================================================== 
	Action Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//phase 1: load project file
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bBrowse){ 
			this.startPhase1(); 
		}		
		
		//-----------------------------------------------------------------------------------------------
		//phase 3: run command op_mksim 
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bRunMKSIM){ 
			//show the time info message in the text area
			this.printAppOutputText("This operation may take a few minutes to complete." + this.lineBreak + "Please wait...", ClusterApp.TX_STDOUT, true);
			
			//set the Opnet version status
			this.statusDataSetValue(ClusterApp.LABEL_CHECK_OP_VER, IAppUtils.STAT_DONE);
			
			//start the phase 3
			this.startPhase3(); 
		}
			
		//-----------------------------------------------------------------------------------------------
		//phase 4: submit simulation to queue 
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bSubmitSims){ 
			this.startPhase4(); 
		}
		
		//-----------------------------------------------------------------------------------------------
		// clear console button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bAppOutputClear){
			//clear the console
			this.printAppOutputText(null, ClusterApp.TX_STDOUT, true);
			//disables the save button
			this.bAppOutputSave.setEnabled(false);
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// save console to file button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bAppOutputSave && this.txAppOutput.getText() != null){
			
			//local attributes
			JFileChooser 		saveDialog 	= new JFileChooser();
			int					returnVal	= 0;
			File				outFile		= null;
			FileOutputStream	outFileStr	= null;
			DataOutputStream	outDataStr	= null; 
			
			//show the dialog
			returnVal						= saveDialog.showSaveDialog(this.mainPanel);
			
			if (returnVal == JFileChooser.APPROVE_OPTION){
			
				try {
					outFile						= saveDialog.getSelectedFile();
					outFileStr					= new FileOutputStream(outFile);
					outDataStr					= new DataOutputStream(outFileStr);
					
					outDataStr.writeUTF(this.txAppOutput.getText() + this.lineBreak);
					
					outDataStr.flush();
					outDataStr.close();
					
					outFileStr.flush();
					outFileStr.close();
					
				} catch (FileNotFoundException ex) {
					//log the error
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (bAppOutputSave)");
					//show a popup message
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Unable to save the output file (see the log file)",
							"ERROR",
							JOptionPane.ERROR_MESSAGE);
					//get out
					return;
				} catch (IOException ex){
					//log the error
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (bAppOutputSave)");
					//show a popup message
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Unable to write the output file (see the log file)",
							"ERROR",
							JOptionPane.ERROR_MESSAGE);
					//get out
					return;
				}
				
			} else {
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Save operation cancelled by user: output not saved",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				return; 
			}			
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select output path button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bSimOutDir){
			
			//local attributes
			int				returnVal		= 0;
			
			//try to load the output path
			try{
				returnVal					= this.dOutChooser.showOpenDialog(this.pProject);
			} catch (HeadlessException err){
				this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed (bSimOutDir)");
				return;
			}
			
			//get the project file directory
			if (returnVal == JFileChooser.APPROVE_OPTION){				
				//get the output path
				this.outDir					= this.dOutChooser.getSelectedFile().getPath();
				//update the output path label
				this.lOutDir.setText("New dir: " + this.outDir);
			} else {
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Scripts directory not set",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				return; 
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select error path button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bSimErrDir){
			
			//local attributes
			int				returnVal		= 0;
			
			//try to load the output path
			try{
				returnVal					= this.dErrChooser.showOpenDialog(this.pProject);
			} catch (HeadlessException err){
				this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed (bSimErrDir)");
				return;
			}
			
			//get the project file directory
			if (returnVal == JFileChooser.APPROVE_OPTION){				
				//get the output path
				this.errDir					= this.dErrChooser.getSelectedFile().getPath();		
				//update the error path label
				this.lErrDir.setText("New dir: " + this.errDir);
			} else {
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Scripts directory not set",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				return; 
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select scripts path button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bSimScrDir){
			
			//local attributes
			int				returnVal		= 0;
			
			//try to load the output path
			try{
				returnVal					= this.dScrChooser.showOpenDialog(this.pProject);
			} catch (HeadlessException err){
				this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed (bSimScrDir)");
				return;
			}
			
			//get the project file directory
			if (returnVal == JFileChooser.APPROVE_OPTION){				
				//get the output path
				this.scrDir					= this.dScrChooser.getSelectedFile().getPath();		
				//update the script path label
				this.lScrDir.setText("New dir: " + this.scrDir);
			} else {
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Scripts directory not set",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				return; 
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// kill jobs button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bKillJobs){
			
			Session					qSession	= this.opProject.getQueueSession();
			HashMap<String, String>	idsInfo		= this.opProject.getIdsInfo();
			Iterator<String>		idsIter		= null;
			String					id			= null;
			int						killed		= 0;
			
			//check if proceed
			if (this.thQstat != null && qSession != null && idsInfo != null){				
				idsIter							= idsInfo.keySet().iterator();
				//kill simulations one by one
				while (idsIter.hasNext()){					
					id							= idsIter.next();					
					try {						
						qSession.control(id, Session.TERMINATE);
						killed++;						
					} catch (DrmaaException ex) {
						//log the error
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (bKillJobs)");
						//show a popup message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to kill the job " + id + " (see the log file)",
								"ERROR",
								JOptionPane.ERROR_MESSAGE);
						//get out
						return;
					}									
				}
				
				//show message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						Integer.toString(killed) + " job(s) killed",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);				
			}
		
		}
				
		//-----------------------------------------------------------------------------------------------
		// remove script files button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bRemoveScrFiles){
			
			//local attributes
			String			oldScrsDir		= null;
			int				returnVal		= 0;
			int				filesRemNum		= 0;
			
			//try to load the output path
			try{
				returnVal					= this.dOldScripts.showOpenDialog(this.pProject);
			} catch (HeadlessException err){
				this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed (bRemoveScrFiles)");
				return;
			}
			
			//get the project file directory
			if (returnVal == JFileChooser.APPROVE_OPTION){				
				//get the output path
				oldScrsDir					= this.dOldScripts.getSelectedFile().getPath();			
			} else { 
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Unable to get the bash scripts directory",
						"",
						JOptionPane.INFORMATION_MESSAGE);
				//get out
				return;
			}
			
			try {
				
				filesRemNum					= this.opProject.removeOldScripts(oldScrsDir);
				
				//show a messsage
				JOptionPane.showMessageDialog(
						this.mainPanel,
						Integer.toString(filesRemNum) + " script file(s) removed",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				
			} catch (OpnetHeavyException ex) {
				//log the error
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (bRemoveScrFiles)");
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Unable to remove the bash script files (see the log file)",
						"ERROR",
						JOptionPane.ERROR_MESSAGE);
				//get out
				return;
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// remove sim files button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bRemoveSimFiles){
			
			//local attributes
			String			oldSims			= null;
			int				returnVal		= 0;
			int				filesRemNum		= 0;
			
			//try to load the output path
			try{
				returnVal					= this.dOldsims.showOpenDialog(this.pProject);
			} catch (HeadlessException err){
				this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed (bRemoveSimFiles)");
				return;
			}
			
			//get the project file directory
			if (returnVal == JFileChooser.APPROVE_OPTION){				
				//get the output path
				oldSims						= this.dOldsims.getSelectedFile().getPath();			
			} else { 
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Unable to get the sims directory",
						"",
						JOptionPane.INFORMATION_MESSAGE);
				//get out
				return;
			}
			
			try {
				
				filesRemNum					= this.opProject.removeOldSims(oldSims);
				
				//show a messsage
				JOptionPane.showMessageDialog(
						this.mainPanel,
						Integer.toString(filesRemNum) + " simulation file(s) removed",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				
			} catch (OpnetHeavyException ex) {
				//log the error
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (bRemoveSimFiles)");
				//show a popup message
				JOptionPane.showMessageDialog(
						this.mainPanel,
						"Unable to remove the simulation files (see the log file)",
						"ERROR",
						JOptionPane.ERROR_MESSAGE);
				//get out
				return;
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select all checkbox
		//-----------------------------------------------------------------------------------------------
		if ((this.filesTable.isEnabled()) && (e.getSource() == this.ckSelectAll)){			
			
			//get the files table data
			Object[][]		localData		= null;
			try {
				localData					= this.opProject.getFilesData();
			} catch (OpnetHeavyException ex) {
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (select all checkbox - get)");
				return;
			}
			
			//disable the select none checkbox
			this.ckSelectNone.setSelected(false);
			
			//select files
			for (int i = 0; i < this.opProject.getFilesDataLength(); i++){
				localData[i][1]				= true;
			}
			
			//set the data
			try {
				this.opProject.setFilesData(localData);
			} catch (OpnetHeavyException ex) {
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (select all checkbox - set)");
				return;
			}
			
			//update table			
			this.filesModel.resetModel(null, localData);
			this.filesModel.fireTableDataChanged();
			//update info label
			this.updateFilesSelectedInfo();			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select none checkbox
		//-----------------------------------------------------------------------------------------------
		if ((this.filesTable.isEnabled()) && (e.getSource() == this.ckSelectNone)){
			
			//get the files table data
			Object[][]		localData		= null;
	
			try {
				localData					= this.opProject.getFilesData();
			} catch (OpnetHeavyException ex) {
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (select all checkbox - get)");
				return;
			}
			
			//disable the select all checkbox
			this.ckSelectAll.setSelected(false);
			
			//unselect files
			for (int i = 0; i < localData.length; i++){
				localData[i][1]				= false;
			}
			
			//set the data
			try {
				this.opProject.setFilesData(localData);
			} catch (OpnetHeavyException ex) {
				this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (select all checkbox - set)");
				return;
			}
			
			//update table			
			this.filesModel.resetModel(null, localData);
			this.filesModel.fireTableDataChanged();
			//update info label
			this.updateFilesSelectedInfo();	
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// netlist combo box select
		//-----------------------------------------------------------------------------------------------
		if ((this.cbNetsList.isEnabled()) && (e.getSource() == this.cbNetsList)){
			
			JComboBox		combo				= (JComboBox) e.getSource();
			String			netName				= (String) combo.getSelectedItem();
			Vector<String>	params				= null;
			
			//avoid the selection of the header
			if ((netName != null) && (netName != this.netsListHeader)){

				//get the op_mksim command params
				try{
					//get the param
					params						= this.opProject.getNetworkMKSIMCode(netName);
					//clean the params area
					this.txMKSIMParams.setText("");
					//load the params
					for (int i = 0; i < params.size(); i++){
						this.txMKSIMParams.append(params.elementAt(i));
						this.txMKSIMParams.append(this.lineBreak);
					}
					
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (netlist select)");
					return;					
				}
				
				//enable the params text area
				this.txMKSIMParams.setEnabled(true);
				
			} else {
				//clear the output
				this.txMKSIMParams.setText("");
				//disable the params text area
				this.txMKSIMParams.setEnabled(false);
				//diable buttons
				this.bNetsReset.setEnabled(false);
				this.bNetsSave.setEnabled(false);
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// netlist save button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbNetsList.isEnabled()) && (e.getSource() == this.bNetsSave)){
			String			netName				= (String) this.cbNetsList.getSelectedItem();
			Vector<String>	vecCode				= new Vector<String>();
			String			code				= "";
			int				lines				= 0;
			int				start				= 0;
			int				end					= 0;
			
			//avoid the selection of the header
			if ((netName != null) && (netName != this.netsListHeader)){
				//get the code and line number
				code							= this.txMKSIMParams.getText();
				lines							= this.txMKSIMParams.getLineCount();
				
				//put it into a vector
				try {					
					for (int i = 0; i < lines; i++){					
						start					= this.txMKSIMParams.getLineStartOffset(i);
						end						= this.txMKSIMParams.getLineEndOffset(i);						
						vecCode.add((code.substring(start, end)).replace(this.lineBreak, ""));
					}					
				} catch (BadLocationException ex) {
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (netlist save button)");
					return;					
				}
				
				//set the new code
				try{
					if (code != null){
						this.opProject.setNewNetworkMKSIMCode(netName, vecCode);
					}
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (netlist save button)");
					return;					
				}
				
				//after save, disable both buttons
				this.bNetsReset.setEnabled(false);
				this.bNetsSave.setEnabled(false);
				
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// netlist discard button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbNetsList.isEnabled()) && (e.getSource() == this.bNetsReset)){
			
			String			netName				= (String) this.cbNetsList.getSelectedItem();
			Vector<String>	params				= null;
			
			//avoid the selection of the header
			if ((netName != null) && (netName != this.netsListHeader)){
				//get the op_mksim command params
				try{
					//get the param
					params						= this.opProject.getNetworkMKSIMCode(netName);
					//clean the params area
					this.txMKSIMParams.setText("");
					//load the params
					for (int i = 0; i < params.size(); i++){
						this.txMKSIMParams.append(params.elementAt(i));
						this.txMKSIMParams.append(this.lineBreak);
					}	

					//disable the save button
					this.bNetsSave.setEnabled(false);
					
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (netlist discard button)");
					return;					
				}
				
				//after reset, disable both buttons
				this.bNetsReset.setEnabled(false);
				this.bNetsSave.setEnabled(false);
								
			} 
		}
		
		//-----------------------------------------------------------------------------------------------
		// simslist combo box select
		//-----------------------------------------------------------------------------------------------
		if ((this.cbSimsList.isEnabled()) && (e.getSource() == this.cbSimsList)){
			
			JComboBox		combo				= (JComboBox) e.getSource();
			String			fileName			= (String) combo.getSelectedItem();
			Vector<String>	params				= null;
			
			//avoid the selection of the header
			if ((fileName != null) && (fileName != this.simsListHeader)){

				//get the op_mksim command params
				try{
					//get the param
					params						= this.opProject.getSimFileCode(fileName);
					//clean the params area
					this.txDTSIMParams.setText("");
					//load the params
					for (int i = 0; i < params.size(); i++){
						this.txDTSIMParams.append(params.elementAt(i));
						this.txDTSIMParams.append(this.lineBreak);
					}
					
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (simlist select)");
					return;					
				}
				
				//enable the params text area
				this.txDTSIMParams.setEnabled(true);
				
			} else {
				//clear the output
				this.txDTSIMParams.setText("");
				//disable the params text area
				this.txDTSIMParams.setEnabled(false);
				//diable buttons
				this.bSimsReset.setEnabled(false);
				this.bSimsSave.setEnabled(false);
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// simslist save button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbSimsList.isEnabled()) && (e.getSource() == this.bSimsSave)){
			String			simFileName			= (String) this.cbSimsList.getSelectedItem();
			Vector<String>	vecCode				= new Vector<String>();
			String			code				= "";
			int				lines				= 0;
			int				start				= 0;
			int				end					= 0;
			
			//avoid the selection of the header
			if ((simFileName != null) && (simFileName != this.simsListHeader)){
				//get the code and line number
				code							= this.txDTSIMParams.getText();
				lines							= this.txDTSIMParams.getLineCount();
				
				//put it into a vector
				try {					
					for (int i = 0; i < lines; i++){					
						start					= this.txDTSIMParams.getLineStartOffset(i);
						end						= this.txDTSIMParams.getLineEndOffset(i);						
						vecCode.add((code.substring(start, end)).replace(this.lineBreak, ""));
					}					
				} catch (BadLocationException ex) {
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (simlist save button)");
					return;					
				}
				
				//set the new code
				try{
					if (code != null){
						this.opProject.setNewSimFileCode(simFileName, vecCode);
					}
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (simlist save button)");
					return;					
				}	
				
				//after save, disable both buttons
				this.bSimsReset.setEnabled(false);
				this.bSimsSave.setEnabled(false);
				
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// simslist discard button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbSimsList.isEnabled()) && (e.getSource() == this.bSimsReset)){
			
			String			netName				= (String) this.cbSimsList.getSelectedItem();
			Vector<String>	params				= null;
			
			//avoid the selection of the header
			if ((netName != null) && (netName != this.simsListHeader)){
				//get the sim file params
				try{
					//get the param
					params						= this.opProject.getSimFileCode(netName);
					//clean the params area
					this.txDTSIMParams.setText("");
					//load the params
					for (int i = 0; i < params.size(); i++){
						this.txDTSIMParams.append(params.elementAt(i));
						this.txDTSIMParams.append(this.lineBreak);
					}	

					//disable the save button
					this.bSimsSave.setEnabled(false);
					
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (simslist discard button");
					return;					
				}		
				
				//after reset, disable both buttons
				this.bNetsReset.setEnabled(false);
				this.bNetsSave.setEnabled(false);
				
			} 
		}

		
	} // End actionPerformed
	

	/*
	================================================================================================================== 
	Mouse Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//object: files list table
		//-----------------------------------------------------------------------------------------------		
		if (e.getSource() == this.filesTable && this.filesTable.isEnabled()){
			
			//-------------------------------------------------------------------------------------------
			//event single-click: update the label value
			//-------------------------------------------------------------------------------------------
			if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)){
				//get the cell info
				JTable		grid				= (JTable) e.getSource();
				int			row					= grid.getSelectedRow();
				int 		col					= grid.getSelectedColumn();
			
				//check the column
				if (col == ClusterApp.TF_COL_INCLUDE){					
					String		fileName			= (String)	grid.getValueAt(row, ClusterApp.TF_COL_FILE_NAME);
					boolean		isIncluded			= (Boolean) grid.getValueAt(row, ClusterApp.TF_COL_INCLUDE);
					//update the corresponding table data	
					try{
						this.opProject.setIncluded(fileName, isIncluded);
					} catch (OpnetHeavyException ex) {
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (single-click - setIncluded)");
						return;
					}
					//update table
					try {
						this.filesModel.resetModel(null, this.opProject.getFilesData());
						this.filesModel.fireTableDataChanged();
						
					} catch (OpnetHeavyException ex) {
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (single-click - get data)");
						return;
					}
					//update the label value
					this.updateFilesSelectedInfo();
				}
				
			} //end single-click
			
			//-------------------------------------------------------------------------------------------
			//event double-click: show the file content
			//-------------------------------------------------------------------------------------------
			if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)){
				
				//get the row and column
				JTable			list		= (JTable) e.getSource();
				int				row			= list.getSelectedRow();
				int				col			= list.getSelectedColumn();
				
				//check the column
				if (col == ClusterApp.TF_COL_FILE_NAME){
					String		fileName	= (String) list.getValueAt(row, ClusterApp.TF_COL_FILE_NAME);				
					//avoid the null pointer exception
					if (fileName == null){ 
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the file.",
								"Wrong file name",
								JOptionPane.ERROR_MESSAGE);
						return; 
					}
				
					//load the file content
					try{
						//try to load the content
						String	content		= this.opProject.getEfFileContent(fileName);
						//show the content
						Font	filesFont		= this.txFileContent.getFont();
						//prepare the text area
						this.txFileContent.setEnabled(true);
						this.txFileContent.setFont(new Font(filesFont.getFamily(), Font.PLAIN, 12));
						this.txFileContent.setText("");
						//--- title
						this.txFileContent.append("File name: " + fileName);
						this.txFileContent.append(this.lineBreak);
						this.txFileContent.append(this.lineBreak);
						//--- output
						this.txFileContent.append(content);
						
					} catch (OpnetHeavyException ex){
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the ef file (see log)",
								"File load error",
								JOptionPane.ERROR_MESSAGE);
						//log error
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (double-click)");
						return;
					}
				}
			} // end double-click
				
		} // end object file list 
		
	} // End mouseClicked

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) { /* nothing to do */ }
	
	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) { /* nothing to do */ }

	
	/*
	================================================================================================================== 
	Key Listener																										
	==================================================================================================================	
	*/

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//object: files list table
		//-----------------------------------------------------------------------------------------------	
		if ((e.getSource() == this.filesTable) && (this.filesTable.isEnabled())){
			
			//-------------------------------------------------------------------------------------------
			//event space bar key pressed: update the label value
			//-------------------------------------------------------------------------------------------			
			if (e.getKeyCode() == KeyEvent.VK_SPACE){
				
				//get the cell info
				JTable		grid				= (JTable) e.getSource();
				int			row					= grid.getSelectedRow();
				int 		col					= grid.getSelectedColumn();
			
				//check the column
				if (col == ClusterApp.TF_COL_INCLUDE){					
					String		fileName			= (String)	grid.getValueAt(row, ClusterApp.TF_COL_FILE_NAME);
					boolean		isIncluded			= (Boolean) grid.getValueAt(row, ClusterApp.TF_COL_INCLUDE);
					//update the corresponding table data	
					try{
						this.opProject.setIncluded(fileName, isIncluded);
					} catch (OpnetHeavyException ex) {
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (single-click - setIncluded)");
						return;
					}
					//update table
					try {
						this.filesModel.resetModel(null, this.opProject.getFilesData());
						this.filesModel.fireTableDataChanged();
						
					} catch (OpnetHeavyException ex) {
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (single-click - get data)");
						return;
					}
					//update the label value
					this.updateFilesSelectedInfo();
				}
				
			} // end space key pressed
			
			//-------------------------------------------------------------------------------------------
			//event enter key pressed: show the file content
			//-------------------------------------------------------------------------------------------
			if (e.getKeyCode() == KeyEvent.VK_ENTER){
				
				//get the row and column
				JTable			list		= (JTable) e.getSource();
				int				row			= list.getSelectedRow();
				int				col			= list.getSelectedColumn();
				
				//check the column
				if (col == ClusterApp.TF_COL_FILE_NAME){
					String		fileName	= (String) list.getValueAt(row, ClusterApp.TF_COL_FILE_NAME);				
					//avoid the null pointer exception
					if (fileName == null){ 
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the file.",
								"Wrong file name",
								JOptionPane.ERROR_MESSAGE);
						return; 
					}
				
					//load the file content
					try{
						//try to load the content
						String	content		= this.opProject.getEfFileContent(fileName);
						//show the content
						Font	filesFont		= this.txFileContent.getFont();
						//prepare the text area
						this.txFileContent.setEnabled(true);
						this.txFileContent.setFont(new Font(filesFont.getFamily(), Font.PLAIN, 12));
						this.txFileContent.setText("");
						//--- title
						this.txFileContent.append("File name: " + fileName);
						this.txFileContent.append(this.lineBreak);
						this.txFileContent.append(this.lineBreak);
						//--- output
						this.txFileContent.append(content);
						
					} catch (OpnetHeavyException ex){
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the ef file (see log)",
								"File load error",
								JOptionPane.ERROR_MESSAGE);
						//log error
						this.sysUtils.printlnErr(ex.getMessage(), this.className + ", mouseClicked (double-click)");
						return;
					}
				}
				
			} // end enter key pressed
			
		} // end object file list table
		
	} // End void keyPressed

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) { /* nothing to do */ }
	
	/*
	================================================================================================================== 
	Document Listener																										
	==================================================================================================================	
	*/
	public void insertUpdate(DocumentEvent e) {
		
		//-------------------------------------------------------------------------------------------
		// object: op_mksim params text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deMKSIMParams){
			//enable the save button
			this.bNetsSave.setEnabled(true);
			//enable the discard button
			this.bNetsReset.setEnabled(true);
		}
		
		//-------------------------------------------------------------------------------------------
		// object: sim files params text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deDTSIMParams){
			//enable the save button
			this.bSimsSave.setEnabled(true);
			//enable the discard button
			this.bSimsReset.setEnabled(true);
		}
		
		//-------------------------------------------------------------------------------------------
		// object: application output text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deAppOutput){
			//enable the save button
			this.bAppOutputSave.setEnabled(true);
			
			//check if qstat finished
			if (this.thQstat != null){				
				//get the output text
				String 	text		= this.txAppOutput.getText();				
				//check if correct thread ending 
				if (text != null && text.contains(IAppUtils.THREAD_EXIT_MSG)){
					//start the stat update				
					this.statusDataSetValue(ClusterApp.LABEL_QSTAT, IAppUtils.STAT_DONE);
				}				
			}
			
		}
		
	} // End void insertUpdate

	/* ------------------------------------------------------------------------------------------------------------ */

	public void removeUpdate(DocumentEvent e) {

		//-------------------------------------------------------------------------------------------
		// object: op_mksim params text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deMKSIMParams){
			//enable the save button
			this.bNetsSave.setEnabled(true);
			//enable the discard button
			this.bNetsReset.setEnabled(true);
		}

		//-------------------------------------------------------------------------------------------
		// object: sim files params text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deDTSIMParams){
			//enable the save button
			this.bSimsSave.setEnabled(true);
			//enable the discard button
			this.bSimsReset.setEnabled(true);
		}

		//-------------------------------------------------------------------------------------------
		// object: application output text area document
		//-------------------------------------------------------------------------------------------
		if (e.getDocument() == this.deAppOutput){
			//enable the save button
			this.bAppOutputSave.setEnabled(true);
		}
		
	} // End void removeUpdate

	/* ------------------------------------------------------------------------------------------------------------ */

	public void changedUpdate(DocumentEvent e) { /* nothing to do */ }

	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/
	/** @return the appTitle */
	public String getFrameTitle() { return appTitle; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the mainPanel */
	public JPanel getMainPanel() { return mainPanel; }

	
} // End class ClusterApp
