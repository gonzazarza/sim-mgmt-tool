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
 * Opnet project exception abstract class
 * 
 * @author 		<a href = "mailto:gazarza@gmail.com"> Gonzalo Zarza </a>
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
	/** exception type error */
	public static final int					MSG_TYPE_ERROR			= 0;					
	/** exception type warning */
	public static final int					MSG_TYPE_WARNING		= 1;					
	/** exception type information */
	public static final int					MSG_TYPE_INFO			= 2;					
	
	/*	
	================================================================================================================== 
	Constructor																										
	==================================================================================================================
	*/
	/**
	 * Class constructor
	 * 
	 * @param		pException			the exception message
	 * 
	 */
	public OpnetExceptionClass(String pException){
		
		//call superclass constructor
		super(pException);
		//save exception message
		this.message		= pException;
		
	} // End constructor
	
	
	/**
	 * Class constructor
	 * 
	 * @param		pException			the exception message
	 * @param		pType				the exception type
	 * 
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
	@Override
	public String getMessage() { return message; }

	
	/** @return the type */ 
	public int getType() { return type; }	
	

} // End OpnetExceptionClass
