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
import org.apache.xmlbeans.XmlOptions;
import org.w3.x2003.x05.soapEnvelope.Fault;

/**
 * Soap extension of {@link AbstractOperationResponse}.
 * 
 * @author matthes rieke
 *
 */
public abstract class AbstractSoapOperationResponse extends AbstractOperationResponse {

	private XmlObject soapFault;
	
	public AbstractSoapOperationResponse(InputStream response) throws Exception {
		super(response);
		
		if (getSoapFault() != null) {
			throw new Exception(getSoapFault().xmlText(new XmlOptions().setSavePrettyPrint()));
		}
	}

	public boolean isSoapFault() {
		return this.soapFault != null;
	}
	
	public XmlObject getSoapFault() {
		return soapFault;
	}

	/**
	 * Process and store a possible fault for later retrieval.
	 * 
	 * @param object the Fault's XML representation
	 */
	protected void processFault(XmlObject object) {
		if (object instanceof Fault) {
			this.soapFault = object;
		}
	}
	
}
