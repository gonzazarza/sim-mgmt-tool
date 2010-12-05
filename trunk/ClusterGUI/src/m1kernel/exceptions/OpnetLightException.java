package m1kernel.exceptions;


/** 
 * Opnet project warning exception
 * 
 * @author 		<a href = "mailto:gonzalo.zarza@caos.uab.es"> Gonzalo Zarza </a>
 * @version		2010.1118
 */
public class OpnetLightException extends OpnetExceptionClass {

	/*	
	================================================================================================================== 
	Attributes																										
	==================================================================================================================
	*/
	private static final long 				serialVersionUID 		= 1L;					//default serial version
	private String							message					= "unknown";			//exception message
	private int								type					= 0;					//exception type 
	
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor
	 * 
	 * @param		pException				the exception message
	 * @param		pType					the exception type
	 * @throws 		OpnetHeavyException		if the exception type is not correct.
	 * 
	 */
	public OpnetLightException(String pException, int pType) throws OpnetHeavyException {
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		//save exception type
		if (pType == OpnetExceptionClass.MSG_TYPE_WARNING || pType == OpnetExceptionClass.MSG_TYPE_INFO){
			this.type			= pType;
		} else {
			throw new OpnetHeavyException("Message type cannot be MSG_TYPE_ERROR in the OpnetLightException"); 
		}
		
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
	
		
} // End OpnetLightException

