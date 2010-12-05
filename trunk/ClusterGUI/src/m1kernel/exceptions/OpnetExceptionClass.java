package m1kernel.exceptions;

/** 
 * Opnet project exception abstract class
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1029
 */
public abstract class OpnetExceptionClass extends Exception {
	
	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/	
	private static final long 				serialVersionUID 		= 1L;					//default serial version
	private String							message					= "unknown";			//exception message
	private int								type					= 0;					//exception type 
	//static attributes
	public static final int					MSG_TYPE_ERROR			= 0;					//error exception
	public static final int					MSG_TYPE_WARNING		= 1;					//warning exception
	public static final int					MSG_TYPE_INFO			= 2;					//info exception
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor
	 */
	public OpnetExceptionClass(String pException){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		
	} // End constructor
	
	
	/**
	 * Class constructor
	 */
	public OpnetExceptionClass(String pException, int pType){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
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

	
	/** @return the type */ 
	public int getType() { return type; }	
	

} // End OpnetExceptionClass
