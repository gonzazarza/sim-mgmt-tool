package m1kernel.interfaces;

//classes
import javax.swing.JPanel;

/** 
 * Interface to access to the application kernel class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1116
 */
public interface IAppKernel {
	
	/*
	================================================================================================================== 
	Static Attributes																										
	==================================================================================================================	
	*/

	//table identifiers
	static final int		TABLE_PROPS			= 0;							//properties table type
	static final int		TABLE_FILES			= 1;							//file table type
	//app properties identifiers
	static final int		PROP_LOAD_PRJ		= 0;							//load proj app property
	static final int		PROP_LOAD_EF		= 1;							//load ef files app property
	static final int		PROP_RUN_MKSIM		= 2;							//run op_mksim app property
	static final int		PROP_SETUP_SIM		= 3;							//generate sim string app property
	static final int		PROP_SUBMIT_SIM		= 4;							//submit sim app property
	static final int		PROP_CHECK_QUEUE	= 5; 							//check queue app property
	//files grid column ids
	static final int		TF_COL_FILE_NAME	= 0;							//file table column file name id
	static final int		TF_COL_INCLUDE		= 1;							//file table column include id
	//app properties states
	static final String		STAT_NOT_APPLIED	= "....";						//not applied status
	static final String		STAT_DONE			= "DONE";						//done status
	static final String		STAT_FAIL			= "FAILED";						//fail status
	static final String		STAT_RUNNING		= "Running";					//running status
	//static labels
	static final String		LABEL_LOAD_PRJ		= "Load project";				//load project label
	static final String		LABEL_LOAD_EF		= "Load file list";				//load ef file list label
	static final String		LABEL_RUN_MKSIM		= "Run op_mksim";				//run op_mksim label
	static final String		LABEL_SETUP_SIM		= "Setup sim jobs";				//sim jobs label
	static final String		LABEL_SUBMIT_SIM	= "Submit sim jobs";			//submit jobs label
	static final String		LABEL_CHECK_QUEUE	= "Check jobs queue";			//check jobs queue label
	//step identifier
	static final int		STEP_1_LOAD_PRJ		= 0;							//step 1: load prj file
	static final int		STEP_2_LOAD_EF		= 1;							//step 2: load ef file list
	static final int		STEP_3_RUN_MKSIM	= 2;							//step 3: run op_mksim command
	static final int		STEP_4_SETUP_SIM	= 3;							//step 4: generate simulation string
	static final int		STEP_5_SUBMIT_SIM	= 4;							//step 5: submit simulation to queue
	static final int		STEP_6_CHECK_QUEUE	= 5;							//step 6: check queue status
	//tabbed pane titles
	static final String		TAB_1_PRJ			= "OPNET Project";				//tab 1: opnet project
	static final String		TAB_2_EF			= "File list (*.ef)";			//tab 2: ef file list
	static final String		TAB_3_MKSIM			= "Options op_mksim";			//tab 3: params op_mksim
	static final String		TAB_4_SIM			= "Options sim files";			//tab 4: params sim files
	static final String		TAB_5_QUEUE			= "Jobs queue";					//tab 5: jobs queue
	static final String		TAB_6_ABOUT			= "?";							//tab 6: about/help
	
	
	/*
	================================================================================================================== 
	Methods																										
	==================================================================================================================	
	*/
	/** 
	 * Initialize the components of the JPanel 
	 */
	public void initComponents();

	
	
	/*
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================	
	*/
	/** @return the frameTitle */
	public String getFrameTitle();

	/* ------------------------------------------------------------------------------------------------------------ */

	/** @return the mainPanel */
	public JPanel getMainPanel();

} // End interface IAppKernel
