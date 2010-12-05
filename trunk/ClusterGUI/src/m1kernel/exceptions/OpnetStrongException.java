package m1kernel.exceptions;

//abstract classes

/** 
 * Opnet project exception
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public class OpnetStrongException extends OpnetExceptionClass {

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
	public OpnetStrongException(String pException){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//set the dedault message type
		this.type			= OpnetExceptionClass.MSG_TYPE_ERROR;
		
		
	} // End constructor

	/**
	 * Class constructor
	 */
	public OpnetStrongException(String pException, String pTitle){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//save exception title
		this.title			= pTitle;
		
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
	
		
} // End OpnetException
