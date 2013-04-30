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
package org.n52.pubsub.soap.subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.pubsub.Constants;
import org.w3.x2005.x08.addressing.EndpointReferenceDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import org.w3.x2005.x08.addressing.ReferenceParametersType;
import org.w3c.dom.Node;

/**
 * @author matthes rieke
 *
 */
public class SubscriptionReference {

	public static final QName RESOURCE_ID_QNAME = new QName(
			"http://ws.apache.org/muse/addressing", "ResourceId");

	private String address;
	private Map<QName, String> referenceParameters = new HashMap<QName, String>();
	private String resourceId;

	public SubscriptionReference(EndpointReferenceType subscriptionReference) {
		//TODO resolve Manager URL from extended service!
		this.address = Constants.getPubSubSuscriptionManagerUrl();
		XmlCursor paramCur = subscriptionReference.getReferenceParameters()
				.newCursor();
		storeReferenceParameters(paramCur);
	}

	private void storeReferenceParameters(XmlCursor paramCur) {
		if (paramCur.toFirstChild()) {
			XmlObject currentChild;
			Node dom;
			QName qn;
			do {
				currentChild = paramCur.getObject();
				dom = currentChild.getDomNode();
				qn = new QName(dom.getNamespaceURI(), dom.getLocalName());
				
				if  (qn.equals(RESOURCE_ID_QNAME)) {
					qn = Constants.PUBSUB_SUBSCRIPTION_IDENTIFIER_QNAME;
				}
				
				this.referenceParameters.put(qn, XmlUtil.stripText(currentChild));
				
				if (qn.equals(Constants.PUBSUB_SUBSCRIPTION_IDENTIFIER_QNAME)) {
					this.resourceId = this.referenceParameters.get(qn);
				}
			} while (paramCur.toNextSibling());
		}
	}

	public String getAddress() {
		return address;
	}

	public Map<QName, String> getReferenceParameters() {
		return referenceParameters;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public EndpointReferenceType toXML() {
		EndpointReferenceType result = EndpointReferenceType.Factory
				.newInstance();
		result.addNewAddress().setStringValue(this.address);

		if (!this.referenceParameters.isEmpty()) {
			ReferenceParametersType params = result.addNewReferenceParameters();
			XmlCursor cur = params.newCursor();
			cur.toFirstContentToken();
			for (QName qn : this.referenceParameters.keySet()) {
				cur.beginElement(qn);
				cur.insertAttributeWithValue(new QName(EndpointReferenceDocument.type.getDocumentElementName().getNamespaceURI(), "IsReferenceParameter"),
						"true");
				cur.insertChars(this.referenceParameters.get(qn));
			}
			cur.dispose();
		}
		return result;
	}

}
