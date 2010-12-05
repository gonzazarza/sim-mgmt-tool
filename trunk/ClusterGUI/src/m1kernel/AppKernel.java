package m1kernel;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
//other classes
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
//events and listeners
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//exceptions
import java.awt.HeadlessException;
//interfaces
import m1kernel.interfaces.IAppKernel;
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.ISysUtils;


/**
 * Main class of the ClusterGUI application
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1025
 */
public class AppKernel implements IAppKernel, ActionListener, MouseListener, KeyListener, ChangeListener {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private ISysUtils					sysUtils				= null;							//system-based utilities
	private IAppUtils					appUtils				= null;							//app utilities class
	private String						className				= "unknown";					//current class name
	private String						machineName				= "unknown";					//machine name
	private	String						userName				= "unknown";					//user name
	private String						projectName				= "";							//project name
	private String						networkNames			= "";							//network names
	private String						projectFullName			= "";							//project path + name		
	private String						projectPath				= "";							//project path
	private String						frameTitle				= "";							//frame title	
	private String						fileSeparator			= "/";							//file separator
	private String						lineSeparator			= "\n";							//system line separator
	private Object[][]					filesGridData			= null;							//ef files grid data container
	private HashMap<String,Object[]>	mapNetsCommands			= null;							//network names params
	private HashMap<String,Object[]>	maptSimsCommands		= null;							//sim files params
	private Vector<String>				selectedNets			= null;							//list of networks selected
	private Vector<String>				allNets					= null;							//list of all networks
	private final String				listNetsHeader			= " * Select the network *";	//network names cb header
	private final String				listNetsEmpty			= " * No network selected *";	//network names cb empty item
	private final String				listSimsHeader			= " * Select the sim file *";	//sim files cb header
	private final String				listSimsEmpty			= " * No sim file selected *";	//sim files cb empty item
	//gui components
	private JPanel						mainPanel				= null;							//main jpanel
	private JTabbedPane					tabbedPane				= null;							//main tabbed pane
	//--- panel: project 
	private JPanel						pProject				= null;							//project panel
	private JButton						bBrowseFiles			= null;							//browse button
	private JButton						bSaveFile				= null;							//save file button
	private JButton						bClearConsole			= null;							//clear console button
	private JButton						bRunMKSIM				= null;							//run op_mksim command
	private JButton						bGenSimString			= null;							//generate the sim command string
	private JButton						bSubmitSim				= null;							//submit sim to queue
	private Object[][]					statusData				= null;							//app status data container
	private JFileChooser				filesBrowser			= null;							//file dialog
	private JTextArea					consoleOut				= null;							//console output
	private PropsTableModel				statusModel				= null;							//default table model			
	private JTable						statusGrid				= null;							//grid for the system props
	//--- panel: load ef files
	private JPanel						pFileList				= null;							//ef file list panel
	private JTable						filesGrid				= null;							//grid for the ef files
	private FilesTableModel				filesGridModel			= null;							//custom table model
	private JCheckBox					cbSelectAll				= null;							//select all checkbox
	private JCheckBox					cbSelectNone			= null;							//unselect checkbox
	private JLabel						filesSelectedInfo		= null;							//ef files selected label
	private JScrollPane 				filesScroll				= null;							//scroll for the ef files panel
	private JTextArea					filesOutput				= null;							//ef files output
	//--- panel: op_mksim
	private JPanel						pMK_SIM					= null;							//op_mksim panel
	private JComboBox					cbListNets				= null;							//net info combobox
	private JButton						bNetReset				= null;							//net file reset button
	private JButton						bNetSave				= null;							//net file save button
	private JTextArea					mksimParams				= null;							//list of op_mksim command params 
	private JTextArea					mksimHelp				= null;							//help of the op_mksim command
	//--- panel: sim file
	private	JPanel						pDT_SIM					= null;							//sim file panel
	private JComboBox					cbListSims				= null;							//sim file info combobox
	private JButton						bSimReset				= null;							//sim file reset button
	private JButton						bSimSave				= null;							//sim file save button
	private JTextArea					dtsimParams				= null;							//sim file params
	private JTextArea					dtsimHelp				= null;							//sim file help
	//--- panel: queue stat
	private JPanel						pQueue					= null;							//queue stat panel
	private JTextArea					queueInfo				= null;							//queue status info	
	//--- panel: help
	private JPanel						pHelp					= null;							//help panel
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/** Class constructor */
	public AppKernel (ISysUtils pUtils){
		
		//set the utilities class
		this.sysUtils				= pUtils;
		
		//initializes the application utilities class
		this.appUtils				= new AppUtils(this.sysUtils);
		
		//get and store the name of the class
		this.className				= this.getClass().getName();
		
		//get the system file separator
		this.fileSeparator			= System.getProperty("file.separator");
		
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
		this.lineSeparator			= System.getProperty("line.separator");
		this.userName				= System.getProperty("user.name");
		this.machineName			= this.sysUtils.getMachineName();		
		
		//save the title
		this.frameTitle				= "Cluster GUI for OPNET 14.0 - " + this.userName + "@" + this.machineName;  
		
		//initialize main jpanel
		this.mainPanel				= new JPanel();		
		this.mainPanel.setLayout(new BorderLayout());
		
		//set the tabbed pane
		this.setTabbedPane();
		
		//add an info bar
		//this.mainPanel.add(new JLabel("status var?"), BorderLayout.PAGE_START);
		
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
		this.tabbedPane.add(AppKernel.TAB_1_PRJ,	this.pProject);
		
		//add ef files panel
		this.setPanelFileList();
		this.tabbedPane.add(AppKernel.TAB_2_EF, 	this.pFileList);			
		
		//add op_mksim panel
		this.setPanelMKSim();
		this.tabbedPane.add(AppKernel.TAB_3_MKSIM, 	this.pMK_SIM);				//TO BE FINISHED!
		
		//add sim file panel
		this.setPanelDTSim();
		this.tabbedPane.add(AppKernel.TAB_4_SIM, 	this.pDT_SIM);				// TO BE FINISHED!
				
		//add queue status panel
		this.setPanelQueue();
		this.tabbedPane.add(AppKernel.TAB_5_QUEUE,	this.pQueue);				// TO BE DEFINED!
		
		//add about/help panel
		this.setPanelHelp();
		this.tabbedPane.add(AppKernel.TAB_6_ABOUT,	this.pHelp);				//TO BE DEFINED!
		
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
		//cpInfo.fill							= GridBagConstraints.VERTICAL;
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
		this.bBrowseFiles					= new JButton("Browse");
		this.filesBrowser					= new JFileChooser();
		this.filesBrowser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.filesBrowser.setFileFilter(new FileNameExtensionFilter("Opnet project file (*.prj)","prj"));
		this.filesBrowser.setMultiSelectionEnabled(false);
		this.bBrowseFiles.addActionListener(this);	
		//------ set border and layout
		ppiRow1.setLayout(new BoxLayout(ppiRow1, BoxLayout.X_AXIS));
		ppiRow1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 1"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow1.add(lSelect);
		ppiRow1.add(Box.createHorizontalGlue());
		ppiRow1.add(this.bBrowseFiles);		
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
		JLabel				lGenString		= new JLabel("Generate simulation string");
		this.bGenSimString					= new JButton("Generate");
		//------ set border and layout
		ppiRow3.setLayout(new BoxLayout(ppiRow3, BoxLayout.X_AXIS));
		ppiRow3.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 3"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow3.add(lGenString);
		ppiRow3.add(Box.createHorizontalGlue());
		ppiRow3.add(this.bGenSimString);
		//------ add panel
		ppInfo.add(ppiRow3);
		//-----------------------------------------------------------------------------------------------
		//--- set row 4: step 4, submit simulations to queue
		//-----------------------------------------------------------------------------------------------
		JPanel				ppiRow4			= new JPanel();
		JLabel				lSubmitSim		= new JLabel("Submit simulations");
		this.bSubmitSim						= new JButton("Submit");
		//------ set border and layout
		ppiRow4.setLayout(new BoxLayout(ppiRow4, BoxLayout.X_AXIS));
		ppiRow4.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 4"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add components
		ppiRow4.add(lSubmitSim);
		ppiRow4.add(Box.createHorizontalGlue());
		ppiRow4.add(this.bSubmitSim);
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
		this.statusData[0][0]				= new String(AppKernel.LABEL_LOAD_PRJ);	
		this.statusData[0][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ 1
		this.statusData[1][0]				= new String(AppKernel.LABEL_LOAD_EF);
		this.statusData[1][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ 2
		this.statusData[2][0]				= new String(AppKernel.LABEL_RUN_MKSIM);	
		this.statusData[2][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ 3
		this.statusData[3][0]				= new String(AppKernel.LABEL_SETUP_SIM);	
		this.statusData[3][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ 4	
		this.statusData[4][0]				= new String(AppKernel.LABEL_SUBMIT_SIM);	
		this.statusData[4][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ 5
		this.statusData[5][0]				= new String(AppKernel.LABEL_CHECK_QUEUE);	
		this.statusData[5][1]				= new String(AppKernel.STAT_NOT_APPLIED);
		//------ initialize status grid
		boolean status						= this.initJTable(AppKernel.TABLE_PROPS, this.statusData);
		//------ add status grid
		if (status == true){
			//ppsRow1.add(this.statusGrid.getTableHeader());
			ppsRow1.add(this.statusGrid);
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
		//String				userPrompt		= this.userName + "@" + this.machineName + ":~$";
		String				userPrompt		= " No output";
		this.consoleOut						= new JTextArea(userPrompt, 12, 60);
		JScrollPane			consoleScroll	= new JScrollPane(this.consoleOut);
		//------ set border and layout
		ppoRow1.setLayout(new BoxLayout(ppoRow1, BoxLayout.X_AXIS));
		ppoRow1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ add components
		ppoRow1.add(consoleScroll);
		//------ add panel
		ppOutput.add(ppoRow1);
		//-----------------------------------------------------------------------------------------------
		//--- set row 2: output buttons
		//-----------------------------------------------------------------------------------------------
		JPanel				ppoRow2			= new JPanel();
		this.bSaveFile						= new JButton("Save to file");
		this.bClearConsole					= new JButton("Clear output");
		//------ add listeners
		this.bClearConsole.addActionListener(this);
		//------ set border and layout
		ppoRow2.setLayout(new BoxLayout(ppoRow2, BoxLayout.X_AXIS));
		ppoRow2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ add components
		ppoRow2.add(Box.createHorizontalGlue());
		ppoRow2.add(this.bSaveFile);
		ppoRow2.add(Box.createHorizontalStrut(5));
		ppoRow2.add(this.bClearConsole);
		//------ add panel
		ppOutput.add(ppoRow2);
		
		
	} // End void setProjectPanel	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the project panel 
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
		this.initJTable(AppKernel.TABLE_FILES, null);		
		//------ add components
		pfiRow1.add(this.filesScroll);		
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
		this.cbSelectAll					= new JCheckBox("Select all");
		this.cbSelectNone					= new JCheckBox("Clear selection");
		this.filesSelectedInfo				= new JLabel("");
		this.updateFilesSelectedInfo();
		//------ set action listeners
		this.cbSelectAll.addActionListener(this);
		this.cbSelectNone.addActionListener(this);
		//------ add components
		pfiRow2.add(this.cbSelectNone);
		pfiRow2.add(Box.createHorizontalStrut(5));
		pfiRow2.add(this.cbSelectAll);
		pfiRow2.add(Box.createRigidArea(new Dimension(5,0)));
		pfiRow2.add(Box.createHorizontalGlue());
		pfiRow2.add(this.filesSelectedInfo);				
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
		this.filesOutput					= new JTextArea(" Double click a file name to load the content ", 12, 60);
		JScrollPane		outputScroll		= new JScrollPane(this.filesOutput);
		Font 			outputFont			= this.filesOutput.getFont();		
		//------ tune components
		this.filesOutput.setEditable(false);
		this.filesOutput.setLineWrap(true);
		this.filesOutput.setFont(new Font(outputFont.getFamily(), Font.ITALIC, 12));
		//------ add components
		pfOutput.add(outputScroll);		
		
				
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
		this.pMK_SIM						= new JPanel();
		
		//set border and layout
		this.pMK_SIM.setLayout(new BoxLayout(this.pMK_SIM, BoxLayout.Y_AXIS));
		this.pMK_SIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: net names info
		//-----------------------------------------------------------------------------------------------
		JPanel			pmNetInfo			= new JPanel();
		//--- set border and layout
		pmNetInfo.setLayout(new BoxLayout(pmNetInfo, BoxLayout.Y_AXIS));
		pmNetInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pMK_SIM.add(pmNetInfo);
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
		this.cbListNets						= new JComboBox();
		this.bNetSave						= new JButton("Save");
		this.bNetReset						= new JButton("Reset");
		//------ add listeners
		this.cbListNets.addActionListener(this);
		this.bNetSave.addActionListener(this);
		this.bNetReset.addActionListener(this);
		//------ add components
		pmpRow1.add(this.cbListNets);
		pmpRow1.add(Box.createHorizontalGlue());
		pmpRow1.add(this.bNetSave);
		pmpRow1.add(Box.createHorizontalStrut(5));
		pmpRow1.add(this.bNetReset);
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
		this.mksimParams					= new JTextArea("", 12, 60);
		JScrollPane		paramsScroll		= new JScrollPane(this.mksimParams);
		this.mksimParams.setEditable(true);
		this.mksimParams.setLineWrap(true);
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
		this.mksimHelp						= new JTextArea("", 12, 60);
		JScrollPane		helpScroll			= new JScrollPane(this.mksimHelp);
		Font			helpFont			= this.mksimHelp.getFont();
		this.mksimHelp.setEditable(false);
		this.mksimHelp.setLineWrap(true);
		this.mksimHelp.setFont(new Font(helpFont.getFamily(), Font.ITALIC, 12));
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
		this.pDT_SIM						= new JPanel();
		
		//set border and layout
		this.pDT_SIM.setLayout(new BoxLayout(this.pDT_SIM, BoxLayout.Y_AXIS));
		this.pDT_SIM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		//-----------------------------------------------------------------------------------------------
		//set panel: sim list
		//-----------------------------------------------------------------------------------------------
		JPanel			psList				= new JPanel();
		//--- set border and layout
		psList.setLayout(new BoxLayout(psList, BoxLayout.Y_AXIS));
		psList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		//--- add panel
		this.pDT_SIM.add(psList);
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
		this.cbListSims						= new JComboBox();
		this.bSimSave						= new JButton("Save");
		this.bSimReset						= new JButton("Reset");
		//------ add listeners
		this.cbListSims.addActionListener(this);
		this.bSimSave.addActionListener(this);
		this.bSimReset.addActionListener(this);
		//------ add components
		pslCombo.add(this.cbListSims);
		pslCombo.add(Box.createHorizontalGlue());
		pslCombo.add(this.bSimSave);
		pslCombo.add(Box.createHorizontalStrut(5));
		pslCombo.add(this.bSimReset);
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
		this.pDT_SIM.add(psCode);
		//-----------------------------------------------------------------------------------------------
		//--- set row 0: title and space
		//-----------------------------------------------------------------------------------------------
		psCode.add(this.setPanelExtra(" Simulation file params"));
		psCode.add(Box.createVerticalStrut(10));
		//-----------------------------------------------------------------------------------------------
		//--- set row 1: sim combo box
		//-----------------------------------------------------------------------------------------------
		JPanel			pslCode			= new JPanel();
		//------ set border and layout
		pslCode.setLayout(new BoxLayout(pslCode, BoxLayout.X_AXIS));
		pslCode.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//------ create components
		this.dtsimParams				= new JTextArea("", 10, 60);
		JScrollPane		paramsScroll	= new JScrollPane(this.dtsimParams);
		this.dtsimParams.setEditable(true);
		this.dtsimParams.setLineWrap(true);		
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
		this.pDT_SIM.add(psHelp);
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
		this.dtsimHelp				= new JTextArea("", 12, 60);
		JScrollPane		helpScroll		= new JScrollPane(this.dtsimHelp);
		this.dtsimHelp.setEditable(false);
		this.dtsimHelp.setLineWrap(true);		
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
		this.queueInfo						= new JTextArea("", 30, 60);
		JScrollPane		jobsScroll			= new JScrollPane(this.queueInfo);
		this.queueInfo.setEditable(false);
		this.queueInfo.setLineWrap(true);
		//------ add components
		pjlRow1.add(jobsScroll);
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
	
	/** Initialize the GUI components look (enable/disable) */
	private void initGUILook(){
		
		//local attributes
		boolean		pState			= false;
		
		//disable the corresponding components in the application initialization
		//--- phase 2: load ef files
		this.filesGrid.setEnabled(pState);
		this.filesOutput.setEnabled(pState);
		this.cbSelectAll.setEnabled(pState);
		this.cbSelectNone.setEnabled(pState);
		//--- phase 3: run op_mksim
		this.bRunMKSIM.setEnabled(pState);
		//--- phase 4: setup simulation
		this.bGenSimString.setEnabled(pState);
		//--- phase 5: submit simulations
		this.bSubmitSim.setEnabled(pState);
		
	} // End initGUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 1 (enable/disable) */
	private void setPhase1GUILook(){
		
		
	} // End setPhase1GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 2 (enable/disable) */
	private void setPhase2GUILook(){
		
		
	} // End setPhase2GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 3 (enable/disable) */
	private void setPhase3GUILook(){
		
		
	} // End setPhase3GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 4 (enable/disable) */
	private void setPhase4GUILook(){
		
		
	} // End setPhase4GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 5 (enable/disable) */
	private void setPhase5GUILook(){
		
		
	} // End setPhase5GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Set the GUI components look for phase 6 (enable/disable) */
	private void setPhase6GUILook(){
		
		
	} // End setPhase6GUILook
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Set the JTable parameters 
	 *
	 * @param	pType			the table type
	 * @param	pData			the table data 
	 * @return					the operation status
	 */
	private boolean initJTable(int pType, Object[][] pData){
		
		//operation status flag
		boolean		status				= true;
		//local attributes
		String[]	propsHeader			= new String[]{"Property", "Status"};
		
		switch (pType){
		//-----------------------------------------------------------------------------------------------
		//table: application properties status 
		//-----------------------------------------------------------------------------------------------
		case AppKernel.TABLE_PROPS:						
			//init table
			this.statusModel			= new PropsTableModel(this.sysUtils, pData, propsHeader);
			this.statusGrid				= new JTable(this.statusModel);
			TableCellRenderer render	= new PropsTableCellRenderer(this.sysUtils);
			//set the custom renderer
			this.statusGrid.getColumnModel().getColumn(0).setCellRenderer(render);
			this.statusGrid.getColumnModel().getColumn(1).setCellRenderer(render);
			this.statusGrid.setEnabled(true);
			//set the bg and grid color
			this.statusGrid.setBackground(this.mainPanel.getBackground());
			this.statusGrid.setGridColor(this.mainPanel.getBackground());			
			//set cols width
			int 	wsCol0				= 120;
			int		wsCol1				= 80;
			//--- col 0
			this.statusGrid.getColumnModel().getColumn(0).setMinWidth(wsCol0);
			this.statusGrid.getColumnModel().getColumn(0).setMaxWidth(wsCol0);
			this.statusGrid.getColumnModel().getColumn(0).setPreferredWidth(wsCol0);
			//--- col 1
			this.statusGrid.getColumnModel().getColumn(1).setMinWidth(wsCol1);
			this.statusGrid.getColumnModel().getColumn(1).setMaxWidth(wsCol1);
			this.statusGrid.getColumnModel().getColumn(1).setPreferredWidth(wsCol1);
			//set rows height
			int		wsRows				= 20;
			this.statusGrid.setRowHeight(wsRows);
			//exit
			break;		
		//-----------------------------------------------------------------------------------------------
		//table: list of ef files
		//-----------------------------------------------------------------------------------------------
		case AppKernel.TABLE_FILES:
			//init table
			this.filesGridModel			= new FilesTableModel(this.sysUtils, pData, null);
			this.filesGrid				= new JTable(filesGridModel);
			this.filesScroll			= new JScrollPane(this.filesGrid);
			//add sorter
			this.filesGrid.setAutoCreateRowSorter(true);
			//add event listeners
			this.filesGrid.addMouseListener(this);
			this.filesGrid.addKeyListener(this);	
			//set checkbox col width
			int 	wfCol1				= 80;
			//--- col 1
			this.filesGrid.getColumnModel().getColumn(1).setMinWidth(wfCol1);
			this.filesGrid.getColumnModel().getColumn(1).setMaxWidth(wfCol1);
			this.filesGrid.getColumnModel().getColumn(1).setPreferredWidth(wfCol1);
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
		
	} //End boolean initJTable
	
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
			case AppKernel.STEP_1_LOAD_PRJ:				
				if (pState == true){
					//enable gui components
					//--- phase 2: load ef files
					this.filesGrid.setEnabled(pState);
					this.filesOutput.setEnabled(pState);
					this.cbSelectAll.setEnabled(pState);
					this.cbSelectNone.setEnabled(pState);						
					//set corresponding variables
					//--- nothing to do					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_LOAD_PRJ, AppKernel.STAT_DONE);									
					//trigger steps
					this.startPhase2();					
				} else {
					//disable gui components
					//--- phase 2: load ef files
					this.filesGrid.setEnabled(pState);
					this.filesOutput.setEnabled(pState);
					this.cbSelectAll.setEnabled(pState);
					this.cbSelectNone.setEnabled(pState);
					//--- phase 3: run op_mksim
					this.bRunMKSIM.setEnabled(pState);
					//--- phase 4: setup simulation
					this.bGenSimString.setEnabled(pState);
					//--- phase 5: submit simulations
					this.bSubmitSim.setEnabled(pState);
					//--- phase 6: check queue
					// ???					
					//reset corresponding variables
					//--- phase 2: load ef files
					this.filesGridModel.cleanModel();	
					this.filesOutput.setText("");
					this.consoleOut.setText("");
					//file output title???
					//--- phase 3: run op_mksim
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_LOAD_PRJ, AppKernel.STAT_FAIL);		
					//show error messages
					//--- nothing to do					
				}			
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 2: load ef file list
			// -----------------------------------------------------------------------------------------------
			case AppKernel.STEP_2_LOAD_EF:
				if (pState == true){
					//enable gui components
					//--- phase 2: load ef files
					this.filesGrid.setEnabled(pState);
					this.filesOutput.setEnabled(pState);
					this.cbSelectAll.setEnabled(pState);
					this.cbSelectNone.setEnabled(pState);
					//--- phase 3: run op_mksim
					this.bRunMKSIM.setEnabled(pState);
					//set corresponding variables
					//--- nothing to do
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_LOAD_EF, AppKernel.STAT_DONE);
					//apply actions
					//--- print output
					this.printProjectInfo();
					//--- initialize the network command array
					this.initListNetsCommands();
					//trigger steps
					//--- nothing to do
				} else {
					//disable gui components
					//--- phase 2: load ef files
					this.filesGrid.setEnabled(pState);
					this.filesOutput.setEnabled(pState);
					this.cbSelectAll.setEnabled(pState);
					this.cbSelectNone.setEnabled(pState);
					//--- phase 3: run op_mksim
					this.bRunMKSIM.setEnabled(pState);
					//--- phase 4: setup simulation
					this.bGenSimString.setEnabled(pState);
					//--- phase 5: submit simulations
					this.bSubmitSim.setEnabled(pState);
					//--- phase 6: check queue
					// ???
					
					//reset corresponding variables
					//--- phase 2: load ef files
					this.filesGridModel.cleanModel();	
					this.filesOutput.setText("");
					this.consoleOut.setText("");
					//--- phase 3: run op_mksim
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_LOAD_EF, AppKernel.STAT_FAIL);
					
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
			case AppKernel.STEP_3_RUN_MKSIM:
				if (pState == true){
					//enable gui components
					//--- phase 4: setup simulation
					this.bGenSimString.setEnabled(pState);
										
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_RUN_MKSIM, AppKernel.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 4: setup simulation
					this.bGenSimString.setEnabled(pState);					
					//--- phase 5: submit simulations
					this.bSubmitSim.setEnabled(pState);
					//--- phase 6: check queue
					// ???
					
					//reset corresponding variables
					//--- phase 4: setup simulation
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_RUN_MKSIM, AppKernel.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 4: setup simulation
			// -----------------------------------------------------------------------------------------------
			case AppKernel.STEP_4_SETUP_SIM:
				if (pState == true){
					//enable gui components
					//--- phase 5: submit simulations
					this.bSubmitSim.setEnabled(pState);
					
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_SETUP_SIM, AppKernel.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 5: submit simulations
					this.bSubmitSim.setEnabled(pState);
					//--- phase 6: check queue
					// ???
					
					//reset corresponding variables
					//--- phase 5: submit simulations
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_SETUP_SIM, AppKernel.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 5: submit simulation jobs
			// -----------------------------------------------------------------------------------------------				
			case AppKernel.STEP_5_SUBMIT_SIM:
				if (pState == true){
					//enable gui components
					//--- phase 6: check queue
					// ???
					
					
					//set corresponding variables
					//--- phase 6: check queue
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_SUBMIT_SIM, AppKernel.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					//--- phase 6: check queue
					// ???
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_SUBMIT_SIM, AppKernel.STAT_FAIL);
					
					//show error messages
					
				}
				//exit
				break;			
			// -----------------------------------------------------------------------------------------------
			// phase 6: check queue status
			// -----------------------------------------------------------------------------------------------				
			case AppKernel.STEP_6_CHECK_QUEUE:
				if (pState == true){
					//enable gui components
					
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_CHECK_QUEUE, AppKernel.STAT_DONE);
					
					//trigger steps
					
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(AppKernel.LABEL_CHECK_QUEUE, AppKernel.STAT_FAIL);
					
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
		this.filesGridModel.fireTableDataChanged();		
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
			this.statusData[i][1]		= AppKernel.STAT_NOT_APPLIED;
		}			
		
	} // End void setNotAppliedStatus
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the first phase of the system in order to load the prj file 
	 */
	private void startPhase1(){
		
		//local attributes
		int 	returnVal				= 0;
		boolean opStatus				= false;
					
		//try to load the project file
		try{
			returnVal					= this.filesBrowser.showOpenDialog(this.pProject);
		} catch (HeadlessException err){
			this.sysUtils.printlnErr(err.getMessage(), this.className + ", actionPerformed [Open project dir]");
			opStatus					= false;
		}
		
		//get the project file directory
		if (returnVal == JFileChooser.APPROVE_OPTION){
			
			//get the project full path (path + file name)
			this.projectFullName 		= this.filesBrowser.getSelectedFile().getPath();
			
			System.out.println(this.filesBrowser.getSelectedFile().getName());
			
			//get the project name
			this.projectName			= this.projectFullName.substring(this.projectFullName.lastIndexOf(this.fileSeparator)+1);
			// project path = projectFullName - projectName 
			this.projectPath			= this.projectFullName.substring(0, this.projectFullName.lastIndexOf(this.fileSeparator));				
			
			opStatus					= true;
			
		} else if (returnVal == JFileChooser.ERROR_OPTION){
			//handle the possible error
			this.sysUtils.printlnErr("JFileChooser dialog dismissed", this.className + ", actionPerformed [Open project dir]");
			opStatus					= false;
		} 
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_1_LOAD_PRJ, opStatus);
		
	} // End startPhase1
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Start the second phase of the system in order to load the list of ef files */
	private void startPhase2(){
		
		//local attributes
		boolean				opStatus	= false;
		Vector<String>		files		= new Vector<String>();
		int					numFiles	= 0;
		
		//load the file list
		opStatus						= this.appUtils.loadFileList(this.projectPath);
		
		//get the file list
		if (opStatus == true){
			files						= this.appUtils.getFileList();
			numFiles					= files.size();
			
			//load the file list to the local files grid
			if (numFiles > 0){
				opStatus				= this.loadFilesGrid(files);
				//load the list of parsed file names
				this.networkNames		= this.appUtils.getConsoleNetworkNames();
				//check the existence of illegal file names
				if (this.appUtils.existIllegalEFFileNames()){
					//show the warning
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Illegal ef file names found.\nSee the log file for more information.",
							"Warning",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				opStatus				= false;
			}			
		}		
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_2_LOAD_EF, opStatus);		
		
	} // End startPhase2
	
	/* ------------------------------------------------------------------------------------------------------------ */	

	/** Start the third phase of the system in order to run the op_mksim command */
	private void startPhase3(){
		
		//local attributes
		boolean 	opStatus			= false;
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_3_RUN_MKSIM, opStatus);
		
	} // End startPhase3
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Start the fourth phase of the system in order to setup the simulations */
	private void startPhase4(){
		
		//local attributes
		boolean 	opStatus			= false;
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_4_SETUP_SIM, opStatus);
		
	} // End startPhase4
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Start the fifth phase of the system in order to submit the simulation jobs */
	private void startPhase5(){
		
		//local attributes
		boolean 	opStatus			= false;
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_5_SUBMIT_SIM, opStatus);
		
	} // End startPhase5
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Start the sixth phase of the system in order to check the jobs queue status */
	private void startPhase6(){
		
		//local attributes
		boolean 	opStatus			= false;
		
		//triggers the corresponding phase and actions
		this.actionTrigger(AppKernel.STEP_6_CHECK_QUEUE, opStatus);
		
	} // End startPhase6
	
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
	
	/**
	 * Initialize the array of network commands
	 */
	private void initListNetsCommands(){
		
		//avoid the null pointer exception
		if (this.networkNames == null){ return; }
		
		//local attributes
		int				filesNum	= this.filesGridData.length;
		Object[]		item		= null;
		String			name		= "";
		Vector<String>	localVec	= new Vector<String>();
		
		//initialize the network commands hashmap
		this.mapNetsCommands		= new HashMap<String, Object[]>();
		
		//load the list of network names
		//--- load the list of ef file names
		for (int i = 0; i < filesNum; i++){
			localVec.add((String) this.filesGridData[i][0]);
		}
		//--- parse the file names
		this.allNets				= this.appUtils.parseNetworkNames(localVec, false);
						
		//load the default params for each network
		for (int i = 0; i < filesNum; i++){
			item					= new Object[2];
			//netname			
			name					= this.allNets.get(i);
			//state
			item[0]					= (Boolean) this.filesGridData[i][AppKernel.TF_COL_INCLUDE];
			//default params
			item[1]					= (Vector<String>)	this.appUtils.getDefaultParamsMKSIM(name);
			//add the network and its data
			this.mapNetsCommands.put(name, item);
		}		
		
	} // End initListNetsCommands
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Initialize the array of sim files commands
	 */
	private void initListSimsCommands(){
		
		
		//this.listSimsCommands			= new Object[][3];
		
	} // End initListSimsCommands
	
	/* ------------------------------------------------------------------------------------------------------------ */	
	
	/** Load the list of ef files into the local ef file grid
	 * 
	 * @param	pFiles		set of files to load
	 * 
	 * @return				the operation status 
	 */
	private boolean loadFilesGrid(Vector<String> pFiles){
		
		//operation status flag	
		boolean				status			= false;
		//other variables
		int					vecSize			= pFiles.size();
				
		//avoid the null pointer exception
		if (pFiles == null){ return(status); }
		
		//check the vecSize and add the checkboxs
		if (vecSize > 0){			
			//create the new data
			this.filesGridData				= new Object[vecSize][2];
			//load the data
			for (int i = 0; i < vecSize; i++){	
				this.filesGridData[i][0]	= new String(pFiles.elementAt(i)); 
				this.filesGridData[i][1]	= new Boolean(true);
			}
				
			//updates the table			
			this.filesGridModel.resetModel(null, this.filesGridData);
			this.filesGridModel.fireTableDataChanged();			
			
			//update the ef selected label
			this.updateFilesSelectedInfo();
			
			//update the status flag
			status							= true;
		}
		
		return(status);
		
	} // End boolean loadFilesGrid
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** Update the list of unique networks selected */
	private void updateNetworksSelectedList(){
		
		//local attributes
		Vector<String>		filesnames	= new Vector<String>();		
		Iterator<String>	it			= null;
		String				gridname	= "";
		boolean				gridbool	= false;
		
		//clear the network name list
		this.cbListNets.removeAllItems();
		this.cbListNets.setEnabled(false);
		
		//avoid the null pointer exception
		if (this.filesGridData == null){
			//nothing to load 
			this.cbListNets.addItem(this.listNetsEmpty);
			//abort
			return;
		}
		
		//get the list of file names		
		for (int i = 0; i < this.filesGridData.length; i++){
			gridname					= (String)	this.filesGridData[i][0];
			gridbool					= (Boolean) this.filesGridData[i][1];
			if (gridbool == true){
				filesnames.add(gridname);
			}
		}		
		
		//get the list of unique parsed network names
		this.selectedNets				= this.appUtils.parseNetworkNames(filesnames, true);
		
		//load the list
		if ((this.selectedNets != null) && (this.selectedNets.size() > 0)){
			//load the null element
			this.cbListNets.addItem(this.listNetsHeader);
			
			//load the list of network names
			it							= this.selectedNets.iterator();			
			while (it.hasNext()){
				this.cbListNets.addItem(it.next());
			}
			//enable the network name list
			this.cbListNets.setEnabled(true);
		} else {
			//nothing to load 
			this.cbListNets.addItem(this.listNetsEmpty);
		}		
		
	} // End void updateNetworksSelectedList
	
	/* ------------------------------------------------------------------------------------------------------------ */
	/* 4th-LEVEL METHODS: OUTPUT FUNCTIONALITIES																	*/
	/* ------------------------------------------------------------------------------------------------------------ */	

	/** 
	 * Load the project info in the corresponding text area
	 */
	private void printProjectInfo(){
		
		//clean the console
		this.consoleOut.setText("");
		
		//add text
		this.consoleOut.append("------------------------------------------------------------" 	+ this.lineSeparator);
		this.consoleOut.append(" Project name " 												+ this.lineSeparator);
		this.consoleOut.append("------------------------------------------------------------" 	+ this.lineSeparator);
		this.consoleOut.append(this.projectName													+ this.lineSeparator);
		this.consoleOut.append(this.lineSeparator);
		this.consoleOut.append("------------------------------------------------------------" 	+ this.lineSeparator);
		this.consoleOut.append(" Network name "													+ this.lineSeparator);
		this.consoleOut.append("------------------------------------------------------------" 	+ this.lineSeparator);
		this.consoleOut.append(this.networkNames);		
		
	} // End void printProjectInfo
	
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
		
		//load the real data
		if (this.filesGridData != null){
			tableSize						= this.filesGridData.length;
		}
				
		//count the number of selected cells
		for (int i = 0; i < tableSize; i++){
			cellValue						= (Boolean) this.filesGridData[i][1];
			if (cellValue == true){ 
				cellSelected++;
			}
		}		
				
		filesNumber							= "Files found: "		+ Integer.toString(tableSize);
		filesSelected						= "Files selected: " 	+ Integer.toString(cellSelected);
		
		//set the label
		this.filesSelectedInfo.setText("[ " + filesNumber + " ]   [ " + filesSelected + " ]");
		
	} // End void updateFilesSelectedInfo
	
	
	/*
	================================================================================================================== 
	Action Listener																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//phase 1: load project file
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bBrowseFiles){ 
			this.startPhase1(); 
		}		
		
		//-----------------------------------------------------------------------------------------------
		// clear console button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bClearConsole){ 
			this.consoleOut.setText("");
		}
		
		//-----------------------------------------------------------------------------------------------
		// select all checkbox
		//-----------------------------------------------------------------------------------------------
		if ((this.filesGrid.isEnabled()) && (e.getSource() == this.cbSelectAll)){
			//disable the select none checkbox
			this.cbSelectNone.setSelected(false);
			//select files
			for (int i = 0; i < this.filesGridData.length; i++){
				this.filesGridData[i][1]	= true;
			}			
			//update table
			this.filesGridModel.resetModel(null, this.filesGridData);
			this.filesGridModel.fireTableDataChanged();
			//update info label
			this.updateFilesSelectedInfo();			
		}
		
		//-----------------------------------------------------------------------------------------------
		// select none checkbox
		//-----------------------------------------------------------------------------------------------
		if ((this.filesGrid.isEnabled()) && (e.getSource() == this.cbSelectNone)){
			//disable the select all checkbox
			this.cbSelectAll.setSelected(false);
			//unselect files
			for (int i = 0; i < this.filesGridData.length; i++){
				this.filesGridData[i][1]	= false;
			}
			//update table
			this.filesGridModel.resetModel(null, this.filesGridData);
			this.filesGridModel.fireTableDataChanged();
			//update info label
			this.updateFilesSelectedInfo();			
		}		
		
		//-----------------------------------------------------------------------------------------------
		// network names combo box select
		//-----------------------------------------------------------------------------------------------
		if ((this.cbListNets.isEnabled()) && (e.getSource() == this.cbListNets)){
			
			JComboBox	combo					= (JComboBox) e.getSource();
			String		selValue				= (String) combo.getSelectedItem();
			Object[]	item					= new Object[2];
			
			//avoid the selection of the header
			if ((selValue != null) && (selValue != this.listNetsHeader)){
				//load the network params
				if (this.mapNetsCommands.containsKey(selValue)){
					//get the command
					item						= this.mapNetsCommands.get(selValue);
					boolean 		included	= (Boolean) item[0];
					Vector<String>	params		= (Vector<String>)  item[1];					
					//clean the params text area
					this.mksimParams.setText("");
					//load the params into the text area
					if (included){
						for (int i = 0; i < params.size(); i++){
							this.mksimParams.append(params.elementAt(i));
							this.mksimParams.append(this.lineSeparator);
						}
					} else {
						//write the error into the log file
						this.sysUtils.printlnErr("Network " + selValue + " not included!!!", this.className + ", actionPerformed [cbListNets]");						
					}					
				} else {
					//write the error into the log file
					this.sysUtils.printlnErr("Params not found (network " + selValue + ")", this.className + ", actionPerformed [cbListNets]");
					//show an error message
					JOptionPane.showMessageDialog(
							this.mainPanel,
							"Params not found (network " + selValue + ").",
							"Params not found",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				//clear the output
				this.mksimParams.setText("");
			}
			
		}
		
		
		
	} // End void actionPerformed
	
	
	/*
	================================================================================================================== 
	Change Listeners																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		// tabbed pane changed
		//---------------------------------------------------------------------------------------------
		if (e.getSource() == this.tabbedPane){
			
			//get the pane title
			JTabbedPane		pane			= (JTabbedPane) e.getSource();
			String			title			= pane.getTitleAt(pane.getSelectedIndex());			
						
			//trigger the op_mksim pane update function
			if (title == AppKernel.TAB_3_MKSIM){
				this.updateNetworksSelectedList();
			}
			
		}
		
		
	} // End void stateChanged
	
	
	/*
	================================================================================================================== 
	Mouse Events Listeners																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		//-----------------------------------------------------------------------------------------------
		//object: files list table
		//-----------------------------------------------------------------------------------------------		
		if (e.getSource() == this.filesGrid && this.filesGrid.isEnabled()){
			
			//-------------------------------------------------------------------------------------------
			//event single-click: update the label value
			//-------------------------------------------------------------------------------------------
			if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)){
				//get the cell info
				JTable		grid				= (JTable) e.getSource();
				int			row					= grid.getSelectedRow();
				int 		col					= grid.getSelectedColumn();
				
				//check the column
				if (col == AppKernel.TF_COL_INCLUDE){					
					boolean		value				= (Boolean) grid.getValueAt(row, col);
					//update the corresponding grid data
					this.filesGridData[row][col]	= value;
					//update table
					this.filesGridModel.resetModel(null, this.filesGridData);
					this.filesGridModel.fireTableDataChanged();
					//changes the label value
					this.updateFilesSelectedInfo();
				}
				
			} //end single-click
			
			//-------------------------------------------------------------------------------------------
			//event double-click: show the file content
			//-------------------------------------------------------------------------------------------
			if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)){
				
				//get the row and column
				JTable		list		= (JTable) e.getSource();
				int			row			= list.getSelectedRow();
				int			col			= list.getSelectedColumn();
				
				//check the column
				if (col == AppKernel.TF_COL_FILE_NAME){
					String		fileName	= (String) list.getValueAt(row, col);				
					//avoid the null pointer exception
					if (this.projectPath == null || fileName == null){ 
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the file.",
								"Wrong file name",
								JOptionPane.ERROR_MESSAGE);
						return; 
					}
				
					//load the file
					boolean 	opStatus		= this.appUtils.loadFile(this.projectPath, fileName);
				
					//show the content
					if (opStatus == true){
						//get the content
						String 	content			= this.appUtils.getEfContents().toString();
						Font	filesFont		= this.filesOutput.getFont();
						//prepare the text area
						this.filesOutput.setEnabled(true);
						this.filesOutput.setFont(new Font(filesFont.getFamily(), Font.PLAIN, 12));
						this.filesOutput.setText("");
						//--- title
						this.filesOutput.append("File name: " + fileName);
						this.filesOutput.append(this.lineSeparator);
						this.filesOutput.append(this.lineSeparator);
						//--- output
						this.filesOutput.append(content);					
					} else {
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the ef file. See the log file.",
								"File load error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} // end double-click
				
		} 
		
	} // End mouseClicked

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) { /* nothing to do */ }
	

	/*
	================================================================================================================== 
	Keyboard Events Listeners																										
	==================================================================================================================	
	*/
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		
		//if the table is disabled abort the operation
		//if (!this.filesGrid.isEnabled()){ return; }
		
		//-----------------------------------------------------------------------------------------------
		//object: files list table
		//-----------------------------------------------------------------------------------------------	
		if ((e.getSource() == this.filesGrid) && (this.filesGrid.isEnabled())){
			
			//-------------------------------------------------------------------------------------------
			//event space bar key pressed: update the label value
			//-------------------------------------------------------------------------------------------			
			if (e.getKeyCode() == KeyEvent.VK_SPACE){
				//get the cell info
				JTable		grid				= (JTable) e.getSource();
				int			row					= grid.getSelectedRow();
				int 		col					= grid.getSelectedColumn();
				
				//check the column
				if (col == AppKernel.TF_COL_INCLUDE){					
					boolean		value				= (Boolean) grid.getValueAt(row, col);
					//update the corresponding grid data
					this.filesGridData[row][col]	= value;
					//update table
					this.filesGridModel.resetModel(null, this.filesGridData);
					this.filesGridModel.fireTableDataChanged();
					//changes the label value
					this.updateFilesSelectedInfo();
				}

				
			} // end space key pressed
			
			//-------------------------------------------------------------------------------------------
			//event enter key pressed: show the file content
			//-------------------------------------------------------------------------------------------
			if (e.getKeyCode() == KeyEvent.VK_ENTER){
				
				//get the row and column
				JTable		list		= (JTable) e.getSource();
				int			row			= list.getSelectedRow();
				int			col			= list.getSelectedColumn();
				
				//check the column
				if (col == AppKernel.TF_COL_FILE_NAME){
					
					String		fileName	= (String) list.getValueAt(row, col);	
					
					//avoid the null pointer exception
					if (this.projectPath == null || fileName == null){ 
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the file.",
								"Wrong file name",
								JOptionPane.ERROR_MESSAGE);
						return; 
					}
				
					//load the file
					boolean 	opStatus	= this.appUtils.loadFile(this.projectPath, fileName);
				
					//show the content
					if (opStatus == true){
						//get the content
						String 	content			= this.appUtils.getEfContents().toString();
						Font	filesFont		= this.filesOutput.getFont();
						//prepare the text area
						this.filesOutput.setEnabled(true);
						this.filesOutput.setFont(new Font(filesFont.getFamily(), Font.PLAIN, 12));
						this.filesOutput.setText("");
						//--- title
						this.filesOutput.append("File name: " + fileName);
						this.filesOutput.append(this.lineSeparator);
						this.filesOutput.append(this.lineSeparator);
						//--- output
						this.filesOutput.append(content);					
					} else {
						//show error message
						JOptionPane.showMessageDialog(
								this.mainPanel,
								"Unable to load the content of the ef file. See the log file.",
								"File load error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				
			} // end enter key pressed
			
		}
		
	} // End void keyPressed

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) { /* nothing to do */ }

	/* ------------------------------------------------------------------------------------------------------------ */

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) { /* nothing to do */ }
		
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/
	/** @return the frameTitle */
	public String getFrameTitle() { return frameTitle; }

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the mainPanel */
	public JPanel getMainPanel() { return mainPanel; }

	
} // End class AppKernel
