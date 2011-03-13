package m1kernel;

//classes
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JTextArea;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
//interfaces
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.ISysUtils;


/** 
 * Qstat by thread class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2011.0311
 */
public class ThreadForQstat implements Runnable {

	/*	
	================================================================================================================== 
	Attributes
	==================================================================================================================
	*/
	private ISysUtils						sysUtils				= null;					//system utilities class
	private String							className				= "unknown";			//to store the name of the class
	private JTextArea						txOutput				= null;					//the program output text area
	HashMap<String,String>					idsInfo					= null;					//the ids information
	Session									qSession				= null;					//queue session
	private String							lineBreak				= "\n";					//system line separator
	//qstat update interval
	static final int						QSTAT_INTERVAL			= 10000;				//update interval in ms
	
	
	/*	
	================================================================================================================== 
	Constructor
	==================================================================================================================
	*/
	/**
	 * Default constructor
	 * 
	 * @param		pSysUtils					the system utilities class
	 * @param		pOutput						output text area
	 * @param		pIdsInfo					id information  
	 * @param		pQSession					queue session
	 * 
	 */
	public ThreadForQstat(ISysUtils pSysUtils, JTextArea pOutput, HashMap<String,String>	pIdsInfo, Session pQSession){
		
		//get and stores the name of the class
		this.className						= this.getClass().getName();
	
		//set the system utilities class
		this.sysUtils						= pSysUtils;
			
		//informs the start of the initialization of the class
		this.sysUtils.printlnOut("... Init: start ...", this.className);
		
		//save the output text area
		this.txOutput						= pOutput;
		
		//save the id info
		this.idsInfo						= pIdsInfo;
		
		//save the session
		this.qSession						= pQSession;
		
		//informs the correct initialization of the class
		this.sysUtils.printlnOut("... Init: DONE! ...", this.className);
		
	} // End constructor


	/*	
	================================================================================================================== 
	Methods
	==================================================================================================================
	*/
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		//local attributes
		int 					notEnded	= 0;
		int						jobCounter	= 0;
		int						qsStatus	= 0;
		String					id			= null;
		String					fileName	= null;
		String					status		= null;
		Iterator<String>		idsIter		= null;
		
		//go over the sims list
		if (idsInfo != null){
		
			//get the number of jobs sent to the queue
			jobCounter						= idsInfo.size();		
			notEnded						= jobCounter;
		
			//keep monitoring the queue until end...
			while (notEnded > 0){
		
				//get iterator
				idsIter							= idsInfo.keySet().iterator();
							
				//clear the output
				this.txOutput.setText("");				
				this.txOutput.setForeground(Color.BLACK);
				
				//print header
				this.txOutput.append("------------------------------------------------------------" + this.lineBreak);
				this.txOutput.append(" Enqueued jobs status" + this.lineBreak);
				this.txOutput.append("------------------------------------------------------------" + this.lineBreak);
				this.txOutput.append(this.lineBreak);
				
				//now go over the sims 
				while (idsIter.hasNext()){
					
					id					= idsIter.next();
					fileName			= idsInfo.get(id);

					try{
					
						qsStatus		= this.qSession.getJobProgramStatus(id);
						
						switch (qsStatus){					
						case Session.UNDETERMINED: 			status = "status cannot be determined          "; 				break;						
						case Session.QUEUED_ACTIVE:			status = "is queued and active                 "; 				break;
						case Session.USER_ON_HOLD:			status = "is queued and in user hold           "; 				break;
						case Session.SYSTEM_ON_HOLD:		status = "is queued and in system hold         "; 				break;
						case Session.USER_SYSTEM_ON_HOLD:	status = "is queued and in user and system hold";				break;
						case Session.RUNNING:				status = "is running                           "; 				break;
						case Session.SYSTEM_SUSPENDED:		status = "is system suspended                  "; 				break;
						case Session.USER_SUSPENDED:		status = "is user suspended                    "; 				break;
						case Session.USER_SYSTEM_SUSPENDED:	status = "is user and system suspended         "; 				break;
						case Session.DONE:					status = "finished normally                    "; notEnded--;	break;
						case Session.FAILED:				status = "finished, but failed                 "; notEnded--;	break;
						}
				
						this.txOutput.append("Job " + id  + "  >>  " + status + " [" + fileName + "]" + this.lineBreak);
								
					} catch (DrmaaException e){
						//show the error message
						this.sysUtils.printlnErr(e.getMessage(), this.className + ", run");
						//show the error in the output text area
						this.txOutput.setForeground(Color.RED);
						this.txOutput.setText(e.getMessage());
					}					
					
				} // End inner while
			
				//wait a predefined time
				try {
					
					if (notEnded > 0){ Thread.sleep(ThreadForQstat.QSTAT_INTERVAL); }
					
				} catch (InterruptedException e) {
					//show the error message
					this.sysUtils.printlnErr(e.getMessage(), this.className + ", run");
					//show the error in the output text area
					this.txOutput.setForeground(Color.RED);
					this.txOutput.setText(e.getMessage());
				}
				
			} // End forever while
			
		} // End if id null
		
		//write the qstat final statement
		this.txOutput.append(this.lineBreak);
		this.txOutput.append("------------------------------------------------------------" + this.lineBreak);
		this.txOutput.append(IAppUtils.THREAD_EXIT_MSG + this.lineBreak);
		this.txOutput.append("------------------------------------------------------------" + this.lineBreak);
		
		//close session
		try {
			this.qSession.exit();
		} catch (DrmaaException e) {
			//show the error message
			this.sysUtils.printlnErr(e.getMessage(), this.className + ", run");
			//show the error in the output text area
			this.txOutput.setForeground(Color.RED);
			this.txOutput.setText(e.getMessage());
		}
		
	} // End void run
		
	
	/*	
	================================================================================================================== 
	Getters and Setters
	==================================================================================================================
	*/
	
	
	
} // End class ThreadForQstat
