package m1kernel.exceptions;


/** 
 * Opnet project warning exception
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public class OpnetLightException extends OpnetExceptionClass {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private static final long 				serialVersionUID 		= 1L;					//default serial version
	private String							message					= "unknown";			//exception message
	private String							title					= "";					//exception title
	private int								type					= 0;					//exception type 
	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor
	 */
	public OpnetLightException(String pException, int pType) throws OpnetStrongException {
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//save exception type
		if (pType == OpnetExceptionClass.MSG_TYPE_WARNING || pType == OpnetExceptionClass.MSG_TYPE_INFO){
			this.type			= pType;
		} else {
			throw new OpnetStrongException("Message type cannot be MSG_TYPE_ERROR in the OpnetLightException"); 
		}
		
	} // End constructor

	
	/**
	 * Class constructor
	 */
	public OpnetLightException(String pException, String pTitle, int pType){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//save exception title
		this.title			= pTitle;
		//save exception type
		this.type			= pType;		
		
	} // End constructor
	

	/*	
	================================================================================================================== 
	Getters and Setters																										
	==================================================================================================================
	*/
	/** @return the message */
	public String getMessage() { return message; }

	
	/** @return the title */
	public String getTitle(){ return title; }
	
	
	/** @return the type */ 
	public int getType() { return type; }	
	
		
} // End OpnetLightException

