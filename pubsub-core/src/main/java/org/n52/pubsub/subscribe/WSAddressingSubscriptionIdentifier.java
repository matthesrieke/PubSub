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
package org.n52.pubsub.subscribe;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;

/**
 * WSA representation of a Subscription Identifier.
 * 
 * @author matthes rieke
 *
 */
public class WSAddressingSubscriptionIdentifier implements SubscriptionIdentifier {
	
	private URL serviceEndpoint;
	private Map<QName, XmlObject> referenceParameters;
	
	public WSAddressingSubscriptionIdentifier(URL serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.referenceParameters = new HashMap<QName, XmlObject>();
	}

	public URL getServiceEndpoint() {
		return serviceEndpoint;
	}
	
	public void addReferenceParameter(QName root, XmlObject xo) {
		this.referenceParameters.put(root, xo);
	}

	public Map<QName, XmlObject> getReferenceParameters() {
		return referenceParameters;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WSAddressingSubscriptionIdentifier) {
			WSAddressingSubscriptionIdentifier wsa = (WSAddressingSubscriptionIdentifier) obj;
			if (wsa.serviceEndpoint.equals(this.serviceEndpoint)) {
				//TODO is this test sufficient?
				return wsa.getReferenceParameters().equals(this.getReferenceParameters());
			}
		}
		return false;
	}
	
}