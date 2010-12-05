package m1kernel;

//classes
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
//interfaces
import m1kernel.exceptions.OpnetException;
import m1kernel.interfaces.IAppUtils;
import m1kernel.interfaces.ISysUtils;

/** 
 * Opnet project class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public class OpnetProject implements IOpnetProject {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private ISysUtils					sysUtils				= null;						//system-based utilities
	private IAppUtils					appUtils				= null;						//application-based utilities
	private String						className				= "unknown";				//class name
	private String						fileSeparator			= "/";						//system file separator
	private String						lineSeparator			= "\n";						//system line separator
	private String						projectName				= "";						//project name
	private String						projectPath				= "";						//project path	
	private HashMap<String,OpnetJob>	networksMap				= null;						//project networks map
	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor 
	 */
	public OpnetProject(ISysUtils pSysUtils, IAppUtils pAppUtils){
		
		//get the class name  
		this.className				= this.getClass().getName(); 
		
		//set the utilities classes
		this.sysUtils				= pSysUtils;
		this.appUtils				= pAppUtils;
		
		//load separators
		this.fileSeparator			= System.getProperty("file.separator");
		this.lineSeparator			= System.getProperty("line.separator");
		
		//initialize the networks map
		this.networksMap			= new HashMap<String,OpnetJob>();
		
		//inform the correct initialization of the class
		this.sysUtils.printlnOut("Successful initialization", this.className);		
		
	} // End constructor
	
	
	/*	
	================================================================================================================== 
	Methods																										
	==================================================================================================================
	*/
	/**
	 * Load the project file
	 * 
	 *  @param		pPath				project path
	 *  @param 		pName				project name
	 *  @return							operation status  
	 */
	public boolean loadProject(String pPath, String pName) throws OpnetException {
	
		//status flag
		boolean			status		= ((pPath != null) && (pName != null));
		
		//get the project name
		if (pPath != null){
			this.projectPath		= pPath;
		} else {
			//exception
			throw new OpnetException("Project path not valid", JOptionPane.ERROR_MESSAGE);
		}
		
		//get the project path
		if (pName != null){
			this.projectName		= pName;
		} else {			
			//exception
			throw new OpnetException("Project name not valid", JOptionPane.ERROR_MESSAGE);
		}
		
		return(status);		
		
	} // End boolean loadProject
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	/**
	 * Load the supplementary environmental files and set the networks map
	 * 
	 * @return							operation status
	 */
	public boolean setNetworksMap() throws OpnetException {
		
		//status flag
		boolean			status		= false;
		//local attributes
		boolean			completed	= false;
		Vector<String>	files		= new Vector<String>();
		Vector<String>	networks	= new Vector<String>();
		
		//load the file list
		completed					= this.appUtils.loadFileList(this.projectPath);
		
		//get the file list
		if (completed){
			//load the list of valid files names
			files					= this.appUtils.getFileList();
			
			//load the list of unique network names
			networks				= this.appUtils.parseNetworkNames(files, true);
			
			//set the networks map (with empty opnet jobs)
			for (int i = 0; i < networks.size(); i++){
				this.networksMap.put(networks.get(i), new OpnetJob());
			}
			
			//--- HABRIA QUE CARGAR LOS OPNET JOBS >> LLENARLOS CON LOS EFs CORRESPONDIENTES
			
			//check the existence of illegal file names
			if (this.appUtils.existIllegalEFFileNames()){
				throw new OpnetException("Illegal supplementary environmental files found (see log).", JOptionPane.WARNING_MESSAGE);
			}
			
		} else {
			//exception
			throw new OpnetException("Unable to load the file list", JOptionPane.ERROR_MESSAGE);
		}
		
		
		return(status);
		
	} // End boolean setNetworksMap	
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	
	
		
	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	
	
} // End class OpnetProject

