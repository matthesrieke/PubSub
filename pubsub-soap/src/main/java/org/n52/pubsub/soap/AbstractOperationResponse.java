/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.pubsub.soap;

import java.io.InputStream;

import org.apache.xmlbeans.XmlObject;

/**
 * Abstract class for processing service responses.
 * 
 * @author matthes rieke
 *
 */
public abstract class AbstractOperationResponse {


	public AbstractOperationResponse(InputStream response) throws Exception {
		parseResponse(response);
		validate();
	}
	
	/**
	 * operation-specific parsing of service operation response.
	 * 
	 * @param response the complete service response as {@link InputStream}
	 * @throws Exception if parsing fails for whatever reason
	 */
	protected abstract void parseResponse(InputStream response) throws Exception;
	
	
	/**
	 * an implementation should rise an exception if the state
	 * of the response is not valid (e.g. a ExceptionReport or
	 * invalid XML)
	 * 
	 * @throws Exception if the response is not valid
	 */
	protected abstract void validate() throws Exception;
	
	/**
	 * @return the marshaled XML representation
	 */
	public abstract XmlObject toXML();
	
}
