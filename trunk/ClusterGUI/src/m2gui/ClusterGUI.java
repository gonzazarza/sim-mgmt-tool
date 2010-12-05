package m2gui;

//swing classes
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
//awt classes
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//other classes
import m1kernel.SysUtils;
//interfaces
import m1kernel.interfaces.ISysUtils;

/**
 * GUI serving as the main interface between OPNET Modeler 14.0 and the IBM cluster at CAOS.
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1028
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
	//static attributes
	private static final long 		serialVersionUID 	= 1L;							//default serial version
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
        
		//initialize the application kernel
        this.appCluster				= new ClusterApp(this.sysUtils);
		
		//initialize components
	    this.appCluster.initComponents();
		
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
	/** Create the GUI and show it */
	private static void createAndShowGUI(){
				
		//frame for the main gui
		ClusterGUI	mainGUI			= new ClusterGUI();	
		JFrame		mainFrame		= new JFrame(ClusterGUI.APP_NAME);
				
		//set the window title
		mainFrame.setTitle(mainGUI.appCluster.getFrameTitle());
	
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
			//log the closing operation		
			this.sysUtils.printlnOut("Closing the " + ClusterGUI.APP_NAME + " application now...", this.className);
			//exit application
			System.exit(0);
		} 
		//else >> nothing to do				
					
	} // End void windowCosing

	
} // End class ClusterGUI
