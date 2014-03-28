/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.ui.client.shared.exceptions;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;

// TODO: Auto-generated Javadoc
/**
 * The Class DtgovFormValidationException.
 * 
 * @author David Virgil Naranjo
 */

@Portable
public class DtgovFormValidationException extends DtgovUiException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6434832250991091319L;
	
	/** The errors. */
	private List<ValidationError> errors;

	/**
     * Gets the errors.
     * 
     * @return the errors
     */
	public List<ValidationError> getErrors() {
		return errors;
	}


	/**
     * Instantiates a new dtgov form validation exception.
     */
	public DtgovFormValidationException() {
		super();
	}
	
	/**
     * Instantiates a new dtgov form validation exception.
     * 
     * @param errors
     *            the errors
     */
	public DtgovFormValidationException(List<ValidationError> errors) {
		super();
		this.errors=errors;
	}

	/**
     * Instantiates a new dtgov form validation exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     * @param errors
     *            the errors
     */
	public DtgovFormValidationException(String message, Throwable cause,List<ValidationError> errors) {
		super(message, cause);
		this.errors=errors;
	}

	/**
     * Instantiates a new dtgov form validation exception.
     * 
     * @param message
     *            the message
     * @param errors
     *            the errors
     */
	public DtgovFormValidationException(String message,List<ValidationError> errors) {
		super(message);
		this.errors=errors;
	}

	/**
     * Instantiates a new dtgov form validation exception.
     * 
     * @param cause
     *            the cause
     * @param errors
     *            the errors
     */
	public DtgovFormValidationException(Throwable cause,List<ValidationError> errors) {
		super(cause);
		this.errors=errors;
	}


	
	

}
