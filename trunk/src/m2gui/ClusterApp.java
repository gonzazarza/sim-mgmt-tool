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
//util classes
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
//exceptions
import java.awt.HeadlessException;
import m1kernel.exceptions.OpnetLightException;
import m1kernel.exceptions.OpnetHeavyException;
//abstract classes
import m2gui.ClusterClass;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.IOpnetProject;
import m1kernel.interfaces.ISysUtils;

/**
 * Main class of the ClusterGUI application
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1119
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
	//main panel attributes and components
	private JPanel						mainPanel				= null;							//main panel
	private JTabbedPane					tabbedPane				= null;							//main tabbed pane	
	//--- panel: project 
	private JPanel						pProject				= null;							//project panel
	private JButton						bBrowse					= null;							//browse button
	private JButton						bRunMKSIM				= null;							//run op_mksim command
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
	private final String				sismListEmpty			= " * No sim job selected *";	//sim files cb empty item
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
		this.appTitle				= "Cluster GUI for OPNET 14.0.A - " + this.userName + "@" + this.machineName;  
		
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
		this.tabbedPane.add(ClusterApp.TAB_3_MKSIM, this.pMKSIM);				// TO BE FINISHED!
		
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
		//------ add listeners
		this.bRunMKSIM.addActionListener(this);
		//------ add components
		ppiRow2.add(lRunMKSIM);
		ppiRow2.add(Box.createHorizontalGlue());
		ppiRow2.add(this.bRunMKSIM);
		//------ add panel
		ppInfo.add(ppiRow2);		
		//-----------------------------------------------------------------------------------------------
		//--- set row 4: step 4, submit simulations to queue
		//-----------------------------------------------------------------------------------------------
		JPanel				ppiRow4			= new JPanel();
		JLabel				lSubmitSim		= new JLabel("Submit simulation jobs");
		this.bSubmitSims					= new JButton("Submit");
		//------ set border and layout
		ppiRow4.setLayout(new BoxLayout(ppiRow4, BoxLayout.X_AXIS));
		ppiRow4.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, "Step 4"), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		//------ add listeners
		this.bSubmitSims.addActionListener(this);
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
		this.statusData						= new String[4][2];
		//------ 0
		this.statusData[0][0]				= new String(ClusterApp.LABEL_LOAD_PRJ);	
		this.statusData[0][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 1
		this.statusData[1][0]				= new String(ClusterApp.LABEL_LOAD_EF);
		this.statusData[1][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 2
		this.statusData[2][0]				= new String(ClusterApp.LABEL_RUN_MKSIM);	
		this.statusData[2][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
		//------ 3	
		this.statusData[3][0]				= new String(ClusterApp.LABEL_SUBMIT_SIM);	
		this.statusData[3][1]				= new String(IAppUtils.STAT_NOT_APPLIED);
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
		this.txAppOutput					= new JTextArea(userPrompt, 20, 60);
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
		this.initFilesSelectedInfo();
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
		pmNetInfo.add(this.setPanelExtra(" Included networks"));
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
		this.bNetsReset						= new JButton("Discard");
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
		this.deMKSIMParams					= this.txMKSIMParams.getDocument();
		JScrollPane		paramsScroll		= new JScrollPane(this.txMKSIMParams);
		this.txMKSIMParams.setEditable(true);
		this.txMKSIMParams.setLineWrap(true);
		//------ add listeners
		this.deMKSIMParams.addDocumentListener(this);
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
		psList.add(this.setPanelExtra(" Simulation jobs to send"));
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
		this.bSimsReset						= new JButton("Discard");
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
		psCode.add(this.setPanelExtra(" Simulation job params"));
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
		this.deDTSIMParams					= this.txDTSIMParams.getDocument();
		JScrollPane		paramsScroll		= new JScrollPane(this.txDTSIMParams);
		this.txDTSIMParams.setEditable(true);
		this.txDTSIMParams.setLineWrap(true);
		//------ add listeners
		this.deDTSIMParams.addDocumentListener(this);
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
		psHelp.add(this.setPanelExtra(" Help sim files (general)"));
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
		Font			helpFont		= this.txDTSIMHelp.getFont();
		this.txDTSIMHelp.setEditable(false);
		this.txDTSIMHelp.setFont(new Font(helpFont.getFamily(), Font.PLAIN, 10));
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
		 * phase 5: submit simulations to queue
		 * --- init: 		button submit
		 * --- rely on: 	phase 3
		 * --- triggers: 	queue status listener
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
					
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, IAppUtils.STAT_DONE);									
					//trigger steps
					this.startPhase2();					
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_PRJ, IAppUtils.STAT_FAIL);		
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
				
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, IAppUtils.STAT_DONE);
														
					//trigger steps
					//--- nothing to do
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_LOAD_EF, IAppUtils.STAT_FAIL);
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
										
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_DONE);
					//trigger steps
					//--- nothing to do
					//apply actions 
					//--- update the list of generated sim files
					this.updateSimsListContent();
					//--- load the sim file help
					this.getSimsFileHelp();
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_FAIL);
					
					//apply actions
					
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
										
					//set corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, IAppUtils.STAT_DONE);
					//trigger steps
					
				} else {
					//disable gui components
					
					//reset corresponding variables
					
					//update phase status in the properties table
					this.statusDataSetValue(ClusterApp.LABEL_SUBMIT_SIM, IAppUtils.STAT_FAIL);
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
			this.sysUtils.printlnErr(	"Index out of range (pPhase + 1 == " 			+ 
										Integer.toString(pPhase)						+
										", statusData.length == "						+
										Integer.toString(this.statusData.length)		+
										")", this.className + ", setNotAppliedStatus"
									);
			return;
		}
		
		//set the not applied status
		for (int i = pPhase + 1; i < this.statusData.length; i++){
			this.statusData[i][1]		= IAppUtils.STAT_NOT_APPLIED;
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
				opStatus				= false;
			}
				
		} else if (returnVal == JFileChooser.ERROR_OPTION){
			//handle the possible error
			this.sysUtils.printlnErr("JFileChooser dialog dismissed", this.className + ", startPhase1 (JFileChooser.ERROR_OPTION)");
			opStatus					= false;
		} else if (returnVal == JFileChooser.CANCEL_OPTION){
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
			this.statusModel.fireTableDataChanged();
			
			this.opProject.setNetworksMap();
			
			//load the data in the files table
			this.filesModel.resetModel(null, this.opProject.getFilesData());
			//update the files table
			this.filesModel.fireTableDataChanged();
			
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
		
		//set the running status
		this.statusDataSetValue(ClusterApp.LABEL_RUN_MKSIM, IAppUtils.STAT_RUNNING);
		this.statusModel.fireTableDataChanged();			
			
		//run the op_mksim command for the selected net names
		try {
		
			//run the command 
			outVec						= this.opProject.runMKSIMCmd();
			
			//load the output into the corresponding text area
			//--- get the stdout and stderr data
			outIt						= outVec.iterator();
			while(outIt.hasNext()){
				output					= outIt.next();
				this.printAppOutputText(output, ClusterApp.TX_STDOUT, true);
			}			
			
			//set the status flag
			opStatus					= true;
			
		} catch (OpnetHeavyException e) {
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", startPhase3");
			//set the status flag
			opStatus					= false;	
		}
		
		//clean the application output text area
//		this.printAppOutputText(null, ClusterApp.TX_STDOUT, true);
//		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_3_RUN_MKSIM, opStatus);
		
	} // End startPhase3

	/* ------------------------------------------------------------------------------------------------------------ */
	
	/** 
	 * Start the fifth phase of the system in order to submit simulations to queue
	 */
	private void startPhase5(){
		
		//local attributes
		boolean 	opStatus			= false;
		
		//TODO
		
		//triggers the corresponding phase and actions
		this.actionTrigger(ClusterApp.STEP_5_SUBMIT_SIM, opStatus);
		
	} // End startPhase5
	
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
				this.cbSimsList.addItem(this.sismListEmpty);
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
			this.txAppOutput.append(pText);		
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
			if (title == ClusterApp.TAB_3_MKSIM){
				this.updateNetsListContent();
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
	@Override
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
			//start the phase 3
			this.startPhase3(); 
		}
			
		//-----------------------------------------------------------------------------------------------
		//phase 5: submit simulation to queue 
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bSubmitSims){ 
			this.startPhase5(); 
		}
		
		//-----------------------------------------------------------------------------------------------
		// clear console button
		//-----------------------------------------------------------------------------------------------
		if (e.getSource() == this.bAppOutputClear){
			this.printAppOutputText(null, ClusterApp.TX_STDOUT, true);
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
				
			} else {
				//clear the output
				this.txMKSIMParams.setText("");
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// netlist save button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbNetsList.isEnabled()) && (e.getSource() == this.bNetsSave)){
			String			netName				= (String) this.cbNetsList.getSelectedItem();
			Vector<String>	params				= null;
			String			code				= "";
			
			//avoid the selection of the header
			if ((netName != null) && (netName != this.netsListHeader)){
				//get the code
				code							= this.txMKSIMParams.getText();
				//set the new code
/*
				try{
					if (code != null){
						//TODO
						//this.opProject.setNetworkMKSIMCode(netName, code);
					}
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (netlist save button)");
					return;					
				}	
*/
			}
			
		}
		
		//TODO
		
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
				
			} else {
				//clear the output
				this.txDTSIMParams.setText("");
			}
			
		}
		
		//-----------------------------------------------------------------------------------------------
		// simslist save button
		//-----------------------------------------------------------------------------------------------
		if ((this.cbSimsList.isEnabled()) && (e.getSource() == this.bSimsSave)){
			String			simFileName			= (String) this.cbSimsList.getSelectedItem();
			Vector<String>	params				= null;
			String			code				= "";
			
			//avoid the selection of the header
			if ((simFileName != null) && (simFileName != this.simsListHeader)){
				//get the code
				code							= this.txDTSIMParams.getText();
				//set the new code
/*
				try{
					if (code != null){
						//TODO
						//this.opProject.setSimFileCode(simFileName, code);
					}
				} catch (OpnetHeavyException ex){
					this.sysUtils.printlnErr(ex.getMessage(), this.className + ", actionPerformed (simlist save button)");
					return;					
				}	
*/	
			}
			
		}
		
		//TODO
		
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
	@Override
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
	Key Listener																										
	==================================================================================================================	
	*/

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
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
	Document Listener																										
	==================================================================================================================	
	*/
	@Override
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
		
	} // End void insertUpdate

	/* ------------------------------------------------------------------------------------------------------------ */

	@Override
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

		
	} // End void removeUpdate

	/* ------------------------------------------------------------------------------------------------------------ */

	@Override
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
