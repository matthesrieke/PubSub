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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.pubsub.soap.AbstractSoapOperationResponse;
import org.oasisOpen.docs.wsn.b2.SubscribeResponseDocument;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 * @author matthes rieke
 *
 */
public class SubscribeResponse extends AbstractSoapOperationResponse {

	private Calendar terminationTime;
	private SubscriptionReference subscriptionReference;
	private Calendar responseTime;

	public SubscribeResponse(InputStream response) throws Exception {
		super(response);
	}

	@Override
	protected void parseResponse(InputStream response) throws Exception {
		XmlObject object = XmlObject.Factory.parse(response);

		if (object instanceof EnvelopeDocument) {
			parseEnvelope(((EnvelopeDocument) object).getEnvelope());
		}
		else if (object instanceof SubscribeResponseDocument) {
			parseSubscribeResponse(((SubscribeResponseDocument) object).getSubscribeResponse());
		}
	}
	
	@Override
	protected void validate() throws Exception {
		if (this.subscriptionReference == null)
			throw new IllegalStateException("SubscriptionReference not available.");
		
		if (this.subscriptionReference.getAddress() != null) {
			try {
				new URL(this.subscriptionReference.getAddress());
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		}
		else {
			throw new IllegalStateException("No Address for the Subscription defined.");
		}
		
		if (this.subscriptionReference.getResourceId() == null)
			throw new IllegalStateException("No ResourceId given!");
			
	}

	private void parseSubscribeResponse(org.oasisOpen.docs.wsn.b2.SubscribeResponseDocument.SubscribeResponse subscribeResponse) {
		this.terminationTime = subscribeResponse.getTerminationTime();
		this.subscriptionReference = new SubscriptionReference(subscribeResponse.getSubscriptionReference());
		this.responseTime = subscribeResponse.getCurrentTime();
	}

	private void parseEnvelope(Envelope envelope) {
		Body body = envelope.getBody();
		
		XmlCursor bodyCursor = body.newCursor();
		if (bodyCursor.toFirstChild() && bodyCursor.getObject() instanceof org.oasisOpen.docs.wsn.b2.SubscribeResponseDocument.SubscribeResponse) {
			parseSubscribeResponse((org.oasisOpen.docs.wsn.b2.SubscribeResponseDocument.SubscribeResponse) bodyCursor.getObject());
		}
	}
	
	public Calendar getTerminationTime() {
		return terminationTime;
	}

	public SubscriptionReference getSubscriptionReference() {
		return subscriptionReference;
	}

	public Calendar getResponseTime() {
		return responseTime;
	}

	@Override
	public XmlObject toXML() {
		SubscribeResponseDocument doc = SubscribeResponseDocument.Factory.newInstance();
		org.oasisOpen.docs.wsn.b2.SubscribeResponseDocument.SubscribeResponse subscribeResponse = doc.addNewSubscribeResponse();
		if (this.responseTime != null)
			subscribeResponse.setCurrentTime(this.responseTime);
		
		if (this.terminationTime != null)
			subscribeResponse.setTerminationTime(this.terminationTime);
		
		subscribeResponse.setSubscriptionReference(this.subscriptionReference.toXML());
		
		return doc;
	}

}