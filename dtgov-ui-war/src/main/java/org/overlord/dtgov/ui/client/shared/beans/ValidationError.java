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
package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @author David Virgil Naranjo
 *
 */
@Portable
public class ValidationError implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2473229547611141961L;

	private String errorLabel;
	
	public ValidationError(){}
	
	public ValidationError(String errorLabel){
		this.errorLabel=errorLabel;
	}

	public String getErrorLabel() {
		return errorLabel;
	}
	
	
	
}
