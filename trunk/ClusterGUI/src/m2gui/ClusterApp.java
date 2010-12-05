package m2gui;

//awt classes
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//swing classes
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
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
//other classes
import m1kernel.FilesTableModel;
import m1kernel.OpnetProject;
import m1kernel.PropsTableCellRenderer;
import m1kernel.PropsTableModel;
//events and listeners
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//exceptions
import java.awt.HeadlessException;

import m1kernel.exceptions.OpnetLightException;
import m1kernel.exceptions.OpnetStrongException;
//abstract classes
import m2gui.ClusterClass;
//interfaces
import m1kernel.interfaces.IOpnetProject;
import m1kernel.interfaces.ISysUtils;

/**
 * Main class of the ClusterGUI application
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1028
 */
public class ClusterApp extends ClusterClass implements ChangeListener, ActionListener, MouseListener, KeyListener {
 
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
	//main panel attributes and components
	private JPanel						mainPanel				= null;							//main panel
	private JTabbedPane					tabbedPane				= null;							//main tabbed pane	
	//--- panel: project 
	private JPanel						pProject				= null;							//project panel
	private JButton						bBrowse					= null;							//browse button
	private JButton						bRunMKSIM				= null;							//run op_mksim command
	private JButton						bSetupSims				= null;							//setup sim strings command
	private JButton						bSubmitSims				= null;							//submit sim to queue
	private JButton						bAppOutputSave			= null;							//app info save button
	private JButton						bAppOutputClear			= null;							//app info output clear button
	private JFileChooser				dFileChooser			= null;							//load file dialog
	private JTextArea					txAppOutput				= null;							//app info output
	private String[][]					statusData				= null;							//app status data container
	private JTable						statusTable				= null;							//grid for the system props
	private PropsTableModel				statusModel				= null;							//default table model
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
	private JTextArea					txMKSIMHelp				= null;							//help of the op_mksim command
	private final String				netsListHeader			= " * Select the network *";	//network names cb header
	private final String				netsListEmpty			= " * No network selected *";	//network names cb empty item	
	//--- panel: sim file
	private	JPanel						pDTSIM					= null;							//sim file panel
	private JComboBox					cbSimsList				= null;							//sim file info combobox
	private JButton						bSimsReset				= null;							//sim file reset button
	private JButton						bSimsSave				= null;							//sim file save button
	private JTextArea					txDTSIMParams			= null;							//sim file params
	private JTextArea					txDTSIMHelp				= null;							//sim file help
	private final String				simsListHeader			= " * Select the sim file *";	//sim files cb header
	private final String				sismListEmpty			= " * No sim file selected *";	//sim files cb empty item
	//--- panel: queue status
	private JPanel						pQueue					= null;							//queue status panel
	private JTextArea					txQueueStatus			= null;							//queue status info
	//--- panel: help
	private JPanel						pHelp					= null;							//help panel
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor */
	public ClusterApp (ISysUtils pUtils){
		
		//set the utilities class
		this.sysUtils				= pUtils;
		
		//initialize the opnet project
		this.opProject				= new OpnetProject(this.sysUtils);
		
		//get and store the name of the class
		this.className				= this.getClass().getName();
		
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);		
		
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
		this.appTitle				= "Cluster GUI for OPNET 14.0 - " + this.userName + "@" + this.machineName;  
		
		//initialize main jpanel
		this.mainPanel				= new JPanel();		
		this.mainPanel.setLayout(new BorderLayout());
		
		//set the tabbed pane
		this.setTabbedPane();
		
		//add the tabbed pane
		this.mainPanel.add(this.tabbedPane);

		//initial diasble/enable gui components
		//this.initGUILook();		
		
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
		this.tabbedPane.add(ClusterApp.TAB_3_MKSIM, 	this.pMKSIM);				//TO BE FINISHED!
		
		//add sim file panel
		this.setPanelDTSim();
		this.tabbedPane.add(ClusterApp.TAB_4_SIM, 	this.pDTSIM);				// TO BE FINISHED!
				
		//add queue status panel
		this.setPanelQueue();
		this.tabbedPane.add(ClusterApp.TAB_5_QUEUE,	this.pQueue);				// TO BE DEFINED!
		
		//add about/help panel
		this.setPanelHelp();
		this.tabbedPane.add(ClusterApp.TAB_6_ABOUT,	this.pHelp);				//TO BE DEFINED!
		
	} // End void setTabbedPane
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 1st-LEVEL METHODS: PANELS OPERATIONS																			*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the project panel 
	 */
	private void setPanelProject(){		
		
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 5, 5, 5
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5
		//		* layout:		boxlayout (x_axis or y_axis)
		
		//initialize panel
		this.pProject						= new JPanel();
		
		//set border and layout
		this.pProject.setLayout(new GridBagLayout());
		this.pProject.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: info
		//-----------------------------------------------------------------------------------------------
		JPanel				ppInfo			= new JPanel();
		GridBagConstraints	cpInfo			= new GridBagConstraints();
		//--- set border and layout
		ppInfo.setLayout(new BoxLayout(ppInfo, BoxLayout.Y_AXIS));
		ppInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));		
		//--- set position
		cpInfo.gridx						= 0;
		cpInfo.gridy						= 0;
		cpInfo.gridwidth					= 1;
		cpInfo.gridheight					= 2;
		this.pProject.add(ppInfo, cpInfo);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		ppInfo.add(this.setPanelExtra(" App Commands"));
		ppInfo.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------		
		//--- set row 1: step 1, select project file
		//-----------------------------------------------------------------------------------------------		
		JPanel				ppiRow1			= new JPanel();	
		JLabel				lSelect			= new JLabel("Select project file");
		this.bBrowse						= new JButton("Browse");
		this.dFileChooser					= new JFileChooser();
		this.dFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.dFileChooser.setFileFilter(new FileNameExtensionFilter("Opnet project file (*.prj)","prj"));
		this.dFileChooser.setMultiSelectionEnabled(false);
		this.bBrowse.addActionListener(this);	
		//------ set border and layout
		ppiRow1.setLayout(new BoxLayout(ppiRow1, BoxLayout.X_AXIS));
		ppiRow1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 1"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow1.add(lSelect);
		ppiRow1.add(Box.createHorizontalGlue());
		ppiRow1.add(this.bBrowse);		
		//------ add panel
		ppInfo.add(ppiRow1);
		//-----------------------------------------------------------------------------------------------
		//--- set row 2: step 2, run op_mksim
		//-----------------------------------------------------------------------------------------------
		JPanel				ppiRow2			= new JPanel();
		JLabel				lRunMKSIM		= new JLabel("Run command op_mksim");
		this.bRunMKSIM						= new JButton("Run");
		//------ set border and layout
		ppiRow2.setLayout(new BoxLayout(ppiRow2, BoxLayout.X_AXIS));
		ppiRow2.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 2"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow2.add(lRunMKSIM);
		ppiRow2.add(Box.createHorizontalGlue());
		ppiRow2.add(this.bRunMKSIM);
		//------ add panel
		ppInfo.add(ppiRow2);		
		//-----------------------------------------------------------------------------------------------
		//--- set row 3: step 3, generate simulation strings
		//-----------------------------------------------------------------------------------------------
		JPanel				ppiRow3			= new JPanel();
		JLabel				lGenString		= new JLabel("Setup simulation jobs");
		this.bSetupSims						= new JButton("Setup Sims");
		//------ set border and layout
		ppiRow3.setLayout(new BoxLayout(ppiRow3, BoxLayout.X_AXIS));
		ppiRow3.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 3"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow3.add(lGenString);
		ppiRow3.add(Box.createHorizontalGlue());
		ppiRow3.add(this.bSetupSims);
		//------ add panel
		ppInfo.add(ppiRow3);
		//-----------------------------------------------------------------------------------------------
		//--- set row 4: step 4, submit simulations to queue
		//-----------------------------------------------------------------------------------------------
		JPanel				ppiRow4			= new JPanel();
		JLabel				lSubmitSim		= new JLabel("Submit simulation jobss");
		this.bSubmitSims					= new JButton("Submit Sims");
		//------ set border and layout
		ppiRow4.setLayout(new BoxLayout(ppiRow4, BoxLayout.X_AXIS));
		ppiRow4.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 4"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow4.add(lSubmitSim);
		ppiRow4.add(Box.createHorizontalGlue());
		ppiRow4.add(this.bSubmitSims);
		//------ add panel
		ppInfo.add(ppiRow4);
		//-----------------------------------------------------------------------------------------------
		//--- set bottom glue
		//-----------------------------------------------------------------------------------------------
		ppInfo.add(Box.createVerticalGlue());
		
		//-----------------------------------------------------------------------------------------------
		//set panel: status
		//-----------------------------------------------------------------------------------------------		
		JPanel				ppStatus		= new JPanel();
		GridBagConstraints	cpStatus		= new GridBagConstraints();
		//--- set border and layout 
		ppStatus.setLayout(new BoxLayout(ppStatus, BoxLayout.Y_AXIS));
		ppStatus.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- set position
		cpStatus.gridx						= 1;
		cpStatus.gridy						= 0;
		cpStatus.gridwidth					= 1;
		cpStatus.gridheight					= 1;
		cpStatus.fill						= GridBagConstraints.VERTICAL;
		this.pProject.add(ppStatus, cpStatus);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		ppStatus.add(this.setPanelExtra(" App status"));
		ppStatus.add(Box.createVerticalStrut(10));		
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: status objects
		//-----------------------------------------------------------------------------------------------
		JPanel				ppsRow1			= new JPanel();
		//------ set border and layout
		ppsRow1.setLayout(new BoxLayout(ppsRow1, BoxLayout.X_AXIS));
		ppsRow1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ set status data		
		this.statusData						= new String[6][2];
		//------ 0
		this.statusData[0][0]				= new String(ClusterApp.LABEL_LOAD_PRJ);	
		this.statusData[0][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ 1
		this.statusData[1][0]				= new String(ClusterApp.LABEL_LOAD_EF);
		this.statusData[1][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ 2
		this.statusData[2][0]				= new String(ClusterApp.LABEL_RUN_MKSIM);	
		this.statusData[2][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ 3
		this.statusData[3][0]				= new String(ClusterApp.LABEL_SETUP_SIM);	
		this.statusData[3][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ 4	
		this.statusData[4][0]				= new String(ClusterApp.LABEL_SUBMIT_SIM);	
		this.statusData[4][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ 5
		this.statusData[5][0]				= new String(ClusterApp.LABEL_CHECK_QUEUE);	
		this.statusData[5][1]				= new String(ClusterApp.STAT_NOT_APPLIED);
		//------ initialize status table
		boolean status						= this.initTable(ClusterApp.TABLE_PROPS, this.statusData);
		//------ add status table
		if (status == true){
			ppsRow1.add(this.statusTable);
		} else {
			//show an error message
			JOptionPane.showMessageDialog(
					this.mainPanel,
					"Unable to initialize the system properties status table.",
					"App status error",
					JOptionPane.ERROR_MESSAGE);
		}
		//------ add panel
		ppStatus.add(ppsRow1);	
		//-----------------------------------------------------------------------------------------------
		//--- set bottom space
		//-----------------------------------------------------------------------------------------------
		ppStatus.add(Box.createVerticalStrut(20));	
		//-----------------------------------------------------------------------------------------------
		//--- set bottom glue
		//-----------------------------------------------------------------------------------------------
		ppStatus.add(Box.createVerticalGlue());
		
		//-----------------------------------------------------------------------------------------------
		//set panel: queue
		//-----------------------------------------------------------------------------------------------
		JPanel				ppQueue			= new JPanel();
		GridBagConstraints	cpQueue			= new GridBagConstraints();
		//--- set border and layout
		ppQueue.setLayout(new BoxLayout(ppQueue, BoxLayout.Y_AXIS));
		ppQueue.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- set position
		cpQueue.gridx						= 1;
		cpQueue.gridy						= 1;
		cpQueue.gridwidth					= 1;
		cpQueue.gridheight					= 1;
		cpQueue.fill						= GridBagConstraints.VERTICAL;		
		this.pProject.add(ppQueue, cpQueue);	
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		ppQueue.add(this.setPanelExtra(" Queue status"));
		ppQueue.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: queue status list (temp)
		//-----------------------------------------------------------------------------------------------
		JPanel				ppqRow1			= new JPanel();
		//------ set border and layout
		ppqRow1.setLayout(new BoxLayout(ppqRow1, BoxLayout.X_AXIS));
		ppqRow1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ set background color
		ppqRow1.setBackground(new Color(211, 211, 211));
		//------ add components
		ppqRow1.add(Box.createRigidArea(new Dimension(200, 150)));
		//------ add panel
		ppQueue.add(ppqRow1);
		//-----------------------------------------------------------------------------------------------
		//--- set bottom space
		//-----------------------------------------------------------------------------------------------
		ppQueue.add(Box.createVerticalStrut(20));						
		//-----------------------------------------------------------------------------------------------
		//--- set bottom glue
		//-----------------------------------------------------------------------------------------------
		ppQueue.add(Box.createVerticalGlue());
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel: output
		//-----------------------------------------------------------------------------------------------
		JPanel				ppOutput		= new JPanel();
		GridBagConstraints	cpOutput		= new GridBagConstraints();
		//--- set border and layout
		ppOutput.setLayout(new BoxLayout(ppOutput, BoxLayout.Y_AXIS));
		ppOutput.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- set position
		cpOutput.gridx						= 0;
		cpOutput.gridy						= 2;
		cpOutput.gridwidth					= 2;
		cpOutput.gridheight					= 1;
		cpOutput.fill						= GridBagConstraints.HORIZONTAL;		
		this.pProject.add(ppOutput, cpOutput);	
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		ppOutput.add(this.setPanelExtra(" App output"));
		ppOutput.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: output text area
		//-----------------------------------------------------------------------------------------------
		JPanel				ppoRow1			= new JPanel();
		String				userPrompt		= " No output";
		this.txAppOutput					= new JTextArea(userPrompt, 12, 60);
		JScrollPane			outputScroll	= new JScrollPane(this.txAppOutput);
		//------ set border and layout
		ppoRow1.setLayout(new BoxLayout(ppoRow1, BoxLayout.X_AXIS));
		ppoRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ add components
		ppoRow1.add(outputScroll);
		//------ add panel
		ppOutput.add(ppoRow1);
		//-----------------------------------------------------------------------------------------------
		//--- set row 2: output buttons
		//-----------------------------------------------------------------------------------------------
		JPanel				ppoRow2			= new JPanel();
		this.bAppOutputSave					= new JButton("Save to file");
		this.bAppOutputClear				= new JButton("Clear output");
		//------ add listeners
		this.bAppOutputClear.addActionListener(this);
		//------ set border and layout
		ppoRow2.setLayout(new BoxLayout(ppoRow2, BoxLayout.X_AXIS));
		ppoRow2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ add components
		ppoRow2.add(Box.createHorizontalGlue());
		ppoRow2.add(this.bAppOutputSave);
		ppoRow2.add(Box.createHorizontalStrut(5));
		ppoRow2.add(this.bAppOutputClear);
		//------ add panel
		ppOutput.add(ppoRow2);
		
		
	} // End void setProjectPanel	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the supplementary environmental files options panel 
	 */
	private void setPanelFileList(){
				
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5 
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		
		
		//initialize the panel
		this.pFileList						= new JPanel();
		
		//set border and layout
		this.pFileList.setLayout(new BoxLayout(this.pFileList, BoxLayout.Y_AXIS));
		this.pFileList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: files list
		//-----------------------------------------------------------------------------------------------
		JPanel			pfList				= new JPanel();
		//--- set border and layout
		pfList.setLayout(new BoxLayout(pfList, BoxLayout.Y_AXIS));
		pfList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pFileList.add(pfList);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		pfList.add(this.setPanelExtra(" Supplementary environment files"));
		pfList.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: file grid
		//-----------------------------------------------------------------------------------------------
		JPanel			pfiRow1				= new JPanel();
		//------ set border and layout
		pfiRow1.setLayout(new BoxLayout(pfiRow1, BoxLayout.X_AXIS));
		pfiRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));		
		//------ create components		
		boolean			status				= this.initTable(ClusterApp.TABLE_FILES, null);
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
		//--- set row 2: files info + buttons
		//-----------------------------------------------------------------------------------------------
		JPanel			pfiRow2				= new JPanel();
		//------ set border and layout
		pfiRow2.setLayout(new BoxLayout(pfiRow2, BoxLayout.X_AXIS));
		pfiRow2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//------ create components
		this.ckSelectAll					= new JCheckBox("Select all");
		this.ckSelectNone					= new JCheckBox("Clear selection");
		this.filesLabel						= new JLabel("");
		this.updateFilesSelectedInfo(true);
		//------ set action listeners
		this.ckSelectAll.addActionListener(this);
		this.ckSelectNone.addActionListener(this);
		//------ add components
		pfiRow2.add(this.ckSelectNone);
		pfiRow2.add(Box.createHorizontalStrut(5));
		pfiRow2.add(this.ckSelectAll);
		pfiRow2.add(Box.createRigidArea(new Dimension(5,0)));
		pfiRow2.add(Box.createHorizontalGlue());
		pfiRow2.add(this.filesLabel);				
		pfiRow2.add(Box.createVerticalStrut(30));
		//------ add panel
		pfList.add(pfiRow2);			
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		pfList.add(Box.createVerticalGlue());
				
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
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		pfOutput.add(this.setPanelExtra(" File content"));
		pfOutput.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: content
		//-----------------------------------------------------------------------------------------------
		//------ create components
		this.txFileContent					= new JTextArea(" Double click a file name to load the content ", 12, 60);
		JScrollPane		contentScroll		= new JScrollPane(this.txFileContent);
		Font 			contentFont			= this.txFileContent.getFont();		
		//------ tune components
		this.txFileContent.setEditable(false);
		this.txFileContent.setLineWrap(true);
		this.txFileContent.setFont(new Font(contentFont.getFamily(), Font.ITALIC, 12));
		//------ add components
		pfOutput.add(contentScroll);		
						
	} // End void setFileLIstPanel 
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the op_mksim panel 
	 */
	private void setPanelMKSim(){
		
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5 
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		
		//initialize panel
		this.pMKSIM							= new JPanel();
		
		//set border and layout
		this.pMKSIM.setLayout(new BoxLayout(this.pMKSIM, BoxLayout.Y_AXIS));
		this.pMKSIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: net names info
		//-----------------------------------------------------------------------------------------------
		JPanel			pmNetInfo			= new JPanel();
		//--- set border and layout
		pmNetInfo.setLayout(new BoxLayout(pmNetInfo, BoxLayout.Y_AXIS));
		pmNetInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pMKSIM.add(pmNetInfo);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		pmNetInfo.add(this.setPanelExtra(" Included network"));
		pmNetInfo.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: title and space
		//-----------------------------------------------------------------------------------------------
		JPanel			pmpRow1				= new JPanel();	
		//------ set border and layout
		pmpRow1.setLayout(new BoxLayout(pmpRow1, BoxLayout.X_AXIS));
		pmpRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.cbNetsList						= new JComboBox();
		this.bNetsSave						= new JButton("Save");
		this.bNetsReset						= new JButton("Reset");
		//------ add listeners
		this.cbNetsList.addActionListener(this);
		this.bNetsSave.addActionListener(this);
		this.bNetsReset.addActionListener(this);
		//------ add components
		pmpRow1.add(this.cbNetsList);
		pmpRow1.add(Box.createHorizontalGlue());
		pmpRow1.add(this.bNetsSave);
		pmpRow1.add(Box.createHorizontalStrut(5));
		pmpRow1.add(this.bNetsReset);
		//------ add panel
		pmNetInfo.add(pmpRow1);
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		pmNetInfo.add(Box.createVerticalStrut(30));
		pmNetInfo.add(Box.createVerticalGlue());		
		//-----------------------------------------------------------------------------------------------
		//--- set row 2: title and space
		//-----------------------------------------------------------------------------------------------
		pmNetInfo.add(this.setPanelExtra(" op_mksim command params"));
		pmNetInfo.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 3: info text area
		//-----------------------------------------------------------------------------------------------
		JPanel			pmpRow3				= new JPanel();
		//------ set border and layout 
		pmpRow3.setLayout(new BoxLayout(pmpRow3, BoxLayout.X_AXIS));
		pmpRow3.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txMKSIMParams					= new JTextArea("", 12, 60);
		JScrollPane		paramsScroll		= new JScrollPane(this.txMKSIMParams);
		this.txMKSIMParams.setEditable(true);
		this.txMKSIMParams.setLineWrap(true);
		//------ add components
		pmpRow3.add(paramsScroll);
		//------ add panel
		pmNetInfo.add(pmpRow3);
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		pmNetInfo.add(Box.createVerticalStrut(30));
		pmNetInfo.add(Box.createVerticalGlue());
		
		//-----------------------------------------------------------------------------------------------
		//set panel: op_mksim help pane
		//-----------------------------------------------------------------------------------------------
		JPanel			pmHelp			= new JPanel();
		//------ set border and layout
		pmHelp.setLayout(new BoxLayout(pmHelp, BoxLayout.Y_AXIS));
		pmHelp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//------ add panel
		pmNetInfo.add(pmHelp);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		pmHelp.add(this.setPanelExtra(" Help op_mksim command"));
		pmHelp.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: help text area
		//-----------------------------------------------------------------------------------------------
		JPanel			pmhRow1				= new JPanel();
		//------ set border and layout
		pmhRow1.setLayout(new BoxLayout(pmhRow1, BoxLayout.X_AXIS));
		pmhRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txMKSIMHelp					= new JTextArea("", 12, 60);
		JScrollPane		helpScroll			= new JScrollPane(this.txMKSIMHelp);
		Font			helpFont			= this.txMKSIMHelp.getFont();
		this.txMKSIMHelp.setEditable(false);
		this.txMKSIMHelp.setLineWrap(true);
		this.txMKSIMHelp.setFont(new Font(helpFont.getFamily(), Font.ITALIC, 12));
		//------ add components
		pmhRow1.add(helpScroll);	
		//------ add panel
		pmNetInfo.add(pmhRow1);
		
		
	} // End void setMK_SIMPanel
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the sim file panel 
	 */
	private void setPanelDTSim(){
	
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5 
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		
		//initialize panel
		this.pDTSIM						= new JPanel();
		
		//set border and layout
		this.pDTSIM.setLayout(new BoxLayout(this.pDTSIM, BoxLayout.Y_AXIS));
		this.pDTSIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim list
		//-----------------------------------------------------------------------------------------------
		JPanel			psList				= new JPanel();
		//--- set border and layout
		psList.setLayout(new BoxLayout(psList, BoxLayout.Y_AXIS));
		psList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pDTSIM.add(psList);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		psList.add(this.setPanelExtra(" Generated simulation files"));
		psList.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: sim combo box
		//-----------------------------------------------------------------------------------------------
		JPanel			pslCombo			= new JPanel();
		//------ set border and layout
		pslCombo.setLayout(new BoxLayout(pslCombo, BoxLayout.X_AXIS));
		pslCombo.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.cbSimsList						= new JComboBox();
		this.bSimsSave						= new JButton("Save");
		this.bSimsReset						= new JButton("Reset");
		//------ add listeners
		this.cbSimsList.addActionListener(this);
		this.bSimsSave.addActionListener(this);
		this.bSimsReset.addActionListener(this);
		//------ add components
		pslCombo.add(this.cbSimsList);
		pslCombo.add(Box.createHorizontalGlue());
		pslCombo.add(this.bSimsSave);
		pslCombo.add(Box.createHorizontalStrut(5));
		pslCombo.add(this.bSimsReset);
		//------ add panel
		psList.add(pslCombo);
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		psList.add(Box.createVerticalStrut(30));
		psList.add(Box.createVerticalGlue());
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim file string
		//-----------------------------------------------------------------------------------------------
		JPanel			psCode				= new JPanel();		
		//--- set border and layout
		psCode.setLayout(new BoxLayout(psCode, BoxLayout.Y_AXIS));
		psCode.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pDTSIM.add(psCode);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		psCode.add(this.setPanelExtra(" Simulation file params"));
		psCode.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: sim combo box
		//-----------------------------------------------------------------------------------------------
		JPanel			pslCode				= new JPanel();
		//------ set border and layout
		pslCode.setLayout(new BoxLayout(pslCode, BoxLayout.X_AXIS));
		pslCode.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txDTSIMParams					= new JTextArea("", 10, 60);
		JScrollPane		paramsScroll		= new JScrollPane(this.txDTSIMParams);
		this.txDTSIMParams.setEditable(true);
		this.txDTSIMParams.setLineWrap(true);		
		//------ add components
		pslCode.add(paramsScroll);
		//------ add panel
		psCode.add(pslCode);
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		psCode.add(Box.createVerticalStrut(30));
		psCode.add(Box.createVerticalGlue());
		
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim file help
		//-----------------------------------------------------------------------------------------------
		JPanel			psHelp				= new JPanel();		
		//--- set border and layout
		psHelp.setLayout(new BoxLayout(psHelp, BoxLayout.Y_AXIS));
		psHelp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pDTSIM.add(psHelp);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		psHelp.add(this.setPanelExtra(" Help sim files"));
		psHelp.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: sim help
		//-----------------------------------------------------------------------------------------------
		JPanel			pslHelp			= new JPanel();
		//------ set border and layout
		pslHelp.setLayout(new BoxLayout(pslHelp, BoxLayout.X_AXIS));
		pslHelp.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txDTSIMHelp				= new JTextArea("", 12, 60);
		JScrollPane		helpScroll		= new JScrollPane(this.txDTSIMHelp);
		this.txDTSIMHelp.setEditable(false);
		this.txDTSIMHelp.setLineWrap(true);		
		//------ add components
		pslHelp.add(helpScroll);
		//------ add panel
		psHelp.add(pslHelp);
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		psHelp.add(Box.createVerticalGlue());
		
	} //End void setDT_SIMPanel
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the jobs queue panel 
	 */
	private void setPanelQueue(){
				
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5 
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)

		
		//initialize panel
		this.pQueue						= new JPanel();
		
		//set border and layout
		this.pQueue.setLayout(new BoxLayout(this.pQueue, BoxLayout.Y_AXIS));
		this.pQueue.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: jobs list
		//-----------------------------------------------------------------------------------------------
		JPanel			pjList				= new JPanel();
		//--- set border and layout
		pjList.setLayout(new BoxLayout(pjList, BoxLayout.Y_AXIS));
		pjList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pQueue.add(pjList);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		pjList.add(this.setPanelExtra(" Jobs queue status"));
		pjList.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: job list
		//-----------------------------------------------------------------------------------------------
		JPanel			pjlRow1				= new JPanel();
		//------ set border and layout
		pjlRow1.setLayout(new BoxLayout(pjlRow1, BoxLayout.X_AXIS));
		pjlRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.txQueueStatus					= new JTextArea("", 30, 60);
		JScrollPane		queueScroll			= new JScrollPane(this.txQueueStatus);
		this.txQueueStatus.setEditable(false);
		this.txQueueStatus.setLineWrap(true);
		//------ add components
		pjlRow1.add(queueScroll);
		//------ add panel
		pjList.add(pjlRow1);		
		//-----------------------------------------------------------------------------------------------
		//set bottom glue
		//-----------------------------------------------------------------------------------------------
		pjList.add(Box.createVerticalGlue());
		
	} // End setPanelQueue
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Set the help panel
	 */
	private void setPanelHelp(){
		
		// look and feel info
		// * 1st-level panels:
		//		* borders:		empty, 5, 5, 5, 5 
		//		* layout:		not-defined
		// * 2nd-level panels:	
		//		* borders: 		empty, 0, 0, 0, 0 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		// * 3rd-level panels:
		//		* borders:		titled, 5, 5, 5, 5 or 5, 0, 5, 0
		//		* layout:		boxlayout (x_axis or y_axis)
		
		
		//initialize panel
		this.pHelp						= new JPanel();
		
		
		
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
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		//set background color
		panel.setBackground(Color.GRAY);
		//tune title
		text.setForeground(Color.WHITE);
		text.setFont(new Font(font.getFamily(), Font.BOLD, 12));
		//add title
		panel.add(text);
		panel.add(Box.createHorizontalGlue());
				
		//return the panel
		return (panel);
				
	} // End setPanelExtra
		
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
		
		/* -----------------------------------------------------------------------------------------------
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
		 * phase 4: generate simulation string (setup simulation)
		 * --- init: 		button generate
		 * --- rely on: 	phase 3
		 * --- triggers: 	none
		 * 
		 * phase 5: submit simulations to queue
		 * --- init: 		button submit
		 * --- rely on: 	phase 4
		 * --- triggers: 	queue status listener
		 * 
		 * phase 6: check queue status periodically
		 * --- init:		phase 5
		 * --- rely on:		phase 5
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
					//--- phase 2: load ef files
											
					//set corresponding variables
					//--- nothing to do					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, ClusterApp.STAT_DONE);									
					//trigger steps
					this.startPhase2();					
				} else {
					//disable gui components
					//--- phase 2: load ef files
					
					//--- phase 3: run op_mksim
					
					//--- phase 4: setup simulation
					
					//--- phase 5: submit simulations
					
					//--- phase 6: check queue
					
					//reset corresponding variables
					//--- phase 2: load ef files
					
					//file output title???
					//--- phase 3: run op_mksim
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, ClusterApp.STAT_FAIL);		
					//show error messages
					//--- nothing to do					
				}			
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 2: load ef file list
			// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_2_LOAD_EF:
				if (pState == true){
					//enable gui components
					//--- phase 2: load ef files
					
					//--- phase 3: run op_mksim
					
					//set corresponding variables
					//--- nothing to do
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, ClusterApp.STAT_DONE);
					//apply actions
					//--- print output
					this.printProjectInfo();					
					//trigger steps
					//--- nothing to do
				} else {
					//disable gui components
					//--- phase 2: load ef files
					
					//--- phase 3: run op_mksim
					
					//--- phase 4: setup simulation
					
					//--- phase 5: submit simulations
					
					//--- phase 6: check queue
					// ???
					
					//reset corresponding variables
					//--- phase 2: load ef files
					
					//--- phase 3: run op_mksim
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, ClusterApp.STAT_FAIL);
					
					//show error messages
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Unable to load the supplementary environment files list.\nPlease select another directory.",
							"Load error",
							JOptionPane.ERROR_MESSAGE);					
				}
				//exit
				break;
			// -----------------------------------------------------------------------------------------------
			// phase 3: run op_mksim
			// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_3_RUN_MKSIM:
				if (pState == true){
					//enable gui components
					//--- phase 4: setup simulation
					
										
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, ClusterApp.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 4: setup simulation
										
					//--- phase 5: submit simulations
					
					//--- phase 6: check queue
					
					
					//reset corresponding variables
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, ClusterApp.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 4: setup simulation
			// -----------------------------------------------------------------------------------------------
			case ClusterApp.STEP_4_SETUP_SIM:
				if (pState == true){
					//enable gui components
					//--- phase 5: submit simulations
										
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SETUP_SIM, ClusterApp.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 5: submit simulations
					
					//--- phase 6: check queue
					
					
					//reset corresponding variables
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SETUP_SIM, ClusterApp.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 5: submit simulation jobs
			// -----------------------------------------------------------------------------------------------				
			case ClusterApp.STEP_5_SUBMIT_SIM:
				if (pState == true){
					//enable gui components
					//--- phase 6: check queue
										
					//set corresponding variables
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, ClusterApp.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 6: check queue
										
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, ClusterApp.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 6: check queue status
			// -----------------------------------------------------------------------------------------------				
			case ClusterApp.STEP_6_CHECK_QUEUE:
				if (pState == true){
					//enable gui components
					
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_CHECK_QUEUE, ClusterApp.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_CHECK_QUEUE, ClusterApp.STAT_FAIL);
					
					//show error messages
					
				}				
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
		//notify the properties table changes
		this.statusModel.fireTableDataChanged();
		
	} // End void actionTrigger	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the not applied status to the corresponding properties 
	 * 
	 * @param	pPhase				the phase to start
	 */
	private void setNotAppliedStatus(int pPhase){
		
		//avoid the index out of range exception
		if ((pPhase + 1) > this.statusData.length){
			this.sysUtils.printlnErr(	"Index out of range (pPhase + 1 == " 		+ 
										Integer.toString(pPhase)					+
										", statusData.length == "					+
										Integer.toString(this.statusData.length)	+
										")", this.className + ", setNotAppliedStatus"
									);
			return;
		}
		
		//set the not applied status
		for (int i = pPhase + 1; i < this.statusData.length; i++){
			this.statusData[i][1]		= ClusterApp.STAT_NOT_APPLIED;
		}			
		
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
		case ClusterApp.TABLE_PROPS:						
			//init table
			this.statusModel			= new PropsTableModel(this.sysUtils, pData, propsHeader);
			this.statusTable			= new JTable(this.statusModel);
			TableCellRenderer render	= new PropsTableCellRenderer(this.sysUtils);
			//set the custom renderer
			this.statusTable.getColumnModel().getColumn(0).setCellRenderer(render);
			this.statusTable.getColumnModel().getColumn(1).setCellRenderer(render);
			this.statusTable.setEnabled(true);
			//set the bg and grid color
			this.statusTable.setBackground(this.mainPanel.getBackground());
			this.statusTable.setGridColor(this.mainPanel.getBackground());			
			//set cols width
			int 	wsCol0				= 120;
			int		wsCol1				= 80;
			//--- col 0
			this.statusTable.getColumnModel().getColumn(0).setMinWidth(wsCol0);
			this.statusTable.getColumnModel().getColumn(0).setMaxWidth(wsCol0);
			this.statusTable.getColumnModel().getColumn(0).setPreferredWidth(wsCol0);
			//--- col 1
			this.statusTable.getColumnModel().getColumn(1).setMinWidth(wsCol1);
			this.statusTable.getColumnModel().getColumn(1).setMaxWidth(wsCol1);
			this.statusTable.getColumnModel().getColumn(1).setPreferredWidth(wsCol1);
			//set rows height
			int		wsRows				= 20;
			this.statusTable.setRowHeight(wsRows);
			//exit
			break;		
		//-----------------------------------------------------------------------------------------------
		//table: list of ef files
		//-----------------------------------------------------------------------------------------------
		case ClusterApp.TABLE_FILES:
			//init table
			this.filesModel			= new FilesTableModel(this.sysUtils, pData, null);
			this.filesTable				= new JTable(filesModel);
			this.filesScroll			= new JScrollPane(this.filesTable);
			//add sorter
			this.filesTable.setAutoCreateRowSorter(true);
			//add event listeners
			this.filesTable.addMouseListener(this);
			this.filesTable.addKeyListener(this);	
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
			
			//load the project
			try {
				this.opProject.loadProject(projectPath, projectName);
				
			} catch (OpnetStrongException e) {
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase1 (loadProject)");
				opStatus				= false;
			}			
			
		} else if (returnVal == JFileChooser.ERROR_OPTION){
			//handle the possible error
			this.sysUtils.printlnErr("JFileChooser dialog dismissed", this.className + ", startPhase1 (JFileChooser.ERROR_OPTION)");
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
			
			this.opProject.setNetworksMap();
			
		} catch (OpnetLightException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase2 (setNetworksMap)");
		} catch (OpnetStrongException e) {
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase2 (setNetworksMap)");
			opStatus					= false;
		} 
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_2_LOAD_EF, opStatus);		
		
	} // End startPhase2
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the pValue in the corresponding pField of the statusData
	 *  
	 * @param	pField		the field to update
	 * @param	pValue		the value to set
	 */
	private void statusDataSetValue(String pField, String pValue){
		
		//avoid null pointer exception
		if (pField == null || pValue == null || this.statusData == null){ return; }		
		
		//look for the correct field
		for (int i = 0; i < this.statusData.length; i++){
			if (this.statusData[i][0].equals(pField)){
				this.statusData[i][1]	= pValue;
				break;
			}
		}
		
	} // End void setStatusDataValue	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 3rd-LEVEL METHODS: ATTRIBUTES SETUP FUNCTIONALITIES															*/
	/* ------------------------------------------------------------------------------------------------------------ */
	
	
		
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 4th-LEVEL METHODS: OUTPUT FUNCTIONALITIES																	*/
	/* ------------------------------------------------------------------------------------------------------------ */
		
	/** 
	 * Load the project info in the corresponding text area
	 */
	private void printProjectInfo(){
		
		//clean the console
		this.txAppOutput.setText("");
		
		//add text
		this.txAppOutput.append("------------------------------------------------------------" 	+ this.lineBreak);
		this.txAppOutput.append(" Project name " 												+ this.lineBreak);
		this.txAppOutput.append("------------------------------------------------------------" 	+ this.lineBreak);
		this.txAppOutput.append(this.opProject.getProjectName()									+ this.lineBreak);
		this.txAppOutput.append(this.lineBreak);
		this.txAppOutput.append("------------------------------------------------------------" 	+ this.lineBreak);
		this.txAppOutput.append(" Networks found "												+ this.lineBreak);
		this.txAppOutput.append("------------------------------------------------------------" 	+ this.lineBreak);
		this.txAppOutput.append(this.opProject.getOutputNetworkNames());		
		
	} // End void printProjectInfo
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Update the ef files info label 
	 */
	private void updateFilesSelectedInfo(boolean pInitUpdate){
		
		//info strings		
		int				tableSize			= 0;
		int				cellSelected		= 0;
		String			filesNumber			= "0";
		String			filesSelected		= "0";
		boolean			cellValue			= false;
		Object[][]		filesData			= null;
		
		//for the initial update there are no files loaded
		if (!pInitUpdate){
			//try to load the files data
			try{
				filesData						= this.opProject.getFilesData();
			} catch (OpnetStrongException e) {
				this.sysUtils.printlnErr(e.getMessage(), this.className + ", updateFilesSelectedInfo");
				return;
			}			
			
			//load the real data
			if (filesData != null){
				tableSize						= filesData.length;
			}
					
			//count the number of selected cells
			for (int i = 0; i < tableSize; i++){
				cellValue						= (Boolean) filesData[i][1];
				if (cellValue == true){ 
					cellSelected++;
				}
			}
		}				
		
		filesNumber							= "Files found: "		+ Integer.toString(tableSize);
		filesSelected						= "Files selected: " 	+ Integer.toString(cellSelected);
		
		//set the label
		this.filesLabel.setText("[ " + filesNumber + " ]   [ " + filesSelected + " ]");
		
	} // End void updateFilesSelectedInfo
	
	
	/*
	================================================================================================================== 
	Change Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	/*
	================================================================================================================== 
	Action Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//phase 1: load project file
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bBrowse){ 
			this.startPhase1(); 
		}		
		
		//-----------------------------------------------------------------------------------------------
		// clear console button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bAppOutputClear){ 
			this.txAppOutput.setText("");
		}
		
		
		
		
		// TODO Auto-generated method stub		
	}
	

	/*
	================================================================================================================== 
	Mouse Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	/*
	================================================================================================================== 
	Key Listener																										
	==================================================================================================================	
	*/

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
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
