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
package org.n52.pubsub.binding;

import org.apache.xmlbeans.XmlObject;
import org.oasisOpen.docs.wsn.b2.NotificationMessageHolderType;
import org.oasisOpen.docs.wsn.b2.NotifyDocument;
import org.oasisOpen.docs.wsn.b2.NotifyDocument.Notify;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 * @author matthes rieke
 *
 */
public class SoapBindingHelper  {

	public static XmlObject wrapWithNotify(XmlObject payload) {
		if (payload instanceof NotifyDocument) {
			return payload;
		}
		
		NotifyDocument doc = NotifyDocument.Factory.newInstance();
		
		if (payload instanceof Notify) {
			doc.setNotify((Notify) payload);
			return doc;
		}
		
		Notify nm = doc.addNewNotify();

		if (payload instanceof NotificationMessageHolderType) {
			nm.addNewNotificationMessage().set(payload);
			return doc;
		}

		NotificationMessageHolderType message = nm.addNewNotificationMessage();
		message.addNewMessage().set(payload);
		return doc;
	}

	public static XmlObject wrapWithSoapEnvelope(XmlObject payload) {
		EnvelopeDocument doc = EnvelopeDocument.Factory.newInstance();
		Envelope envelope = doc.addNewEnvelope();
		Body body = envelope.addNewBody();
		body.set(payload);
		return doc;
	}
	
}
