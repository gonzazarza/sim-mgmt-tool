package m1kernel.exceptions;

/* 
 * Copyright (c) 2010-2013 Gonzalo Zarza. All rights reserved.
 * Copyright (c) 2010-2011 Departament d'Arquitectura de Computadors i Sistemes Operatius, Universitat Autonoma de Barcelona. All rights reserved. 
 *
 * This file is part of the Simulation Management Tool (sim-mgmt-tool).
 *
 * Simulation Management Tool (sim-mgmt-tool) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Simulation Management Tool (sim-mgmt-tool) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Simulation Management Tool (sim-mgmt-tool). If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/** 
 * Opnet project warning exception
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
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
	@Override
	public String getMessage() { return message; }

	/** @return the type */ 
	@Override
	public int getType() { return type; }	
	
		
} // End OpnetLightException

