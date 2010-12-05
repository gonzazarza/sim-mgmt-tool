package m1kernel.exceptions;

/** 
 * Opnet project exception
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1026
 */
public class OpnetException extends Exception {

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
	public OpnetException(String pException, int pType){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//save exception type
		this.type			= pType;
		
	} // End constructor

	/**
	 * Class constructor
	 */
	public OpnetException(String pException, String pTitle, int pType){
		
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
	
		
} // End OpnetException
