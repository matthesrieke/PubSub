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

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.pubsub.soap.AbstractSoapOperationResponse;
import org.oasisOpen.docs.wsn.b2.UnsubscribeResponseDocument;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 * @author matthes rieke
 *
 */
public class UnsubscribeResponse extends AbstractSoapOperationResponse {

	private org.oasisOpen.docs.wsn.b2.UnsubscribeResponseDocument.UnsubscribeResponse content;

	public UnsubscribeResponse(InputStream response) throws Exception {
		super(response);
	}

	@Override
	protected void parseResponse(InputStream response) throws Exception {
		XmlObject obj = XmlObject.Factory.parse(response);
		
		if (obj instanceof EnvelopeDocument) {
			parseEnvelope(((EnvelopeDocument) obj).getEnvelope());
		}
	}

	private void parseEnvelope(Envelope envelope) {
		Body body = envelope.getBody();
		XmlCursor bodyCur = body.newCursor();
		if (bodyCur.toFirstChild() && bodyCur.getObject() instanceof org.oasisOpen.docs.wsn.b2.UnsubscribeResponseDocument.UnsubscribeResponse) {
			this.content = (org.oasisOpen.docs.wsn.b2.UnsubscribeResponseDocument.UnsubscribeResponse) bodyCur.getObject();
		}
		else {
			processFault(bodyCur.getObject());
		}
	}

	@Override
	protected void validate() throws Exception {
		if (this.content == null) {
			throw new IllegalStateException("No UnsubscribeResponse available!");
		}
	}

	@Override
	public XmlObject toXML() {
		UnsubscribeResponseDocument doc = UnsubscribeResponseDocument.Factory.newInstance();
		doc.addNewUnsubscribeResponse();
		return doc;
	}

}