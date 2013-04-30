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
package org.n52.pubsub.ses;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.ses.adapter.client.httplistener.HttpListener;
import org.n52.oxf.ses.adapter.client.httplistener.SimpleConsumerServlet;
import org.n52.pubsub.SubscriptionManager;
import org.n52.pubsub.subscribe.WSAddressingSubscriptionIdentifier;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 * The consumer servlet is responsible for retrieving
 * matched messages and populating these to the
 * {@link SubscriptionManager}.
 * 
 * @author matthes rieke
 *
 */
public class ConsumerServlet extends SimpleConsumerServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, WSAddressingSubscriptionIdentifier> uriToSubscription = new HashMap<String, WSAddressingSubscriptionIdentifier>();

	private String url;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	/**
	 * @param url the URL shall point to the root of the containing
	 * webapp (e.g. "http://my.server:8080/pubsub-sos") and shall
	 * omit a trailing "/".
	 */
	public void setPublicURL(String url) {
		this.url = url + getInitParameter("url");
	}
	
	public ConsumerServlet() {
		super();
		this.setListener(new HttpListener() {
			
			@Override
			public String processRequest(String request, String uri, String method,
					Properties header) {
				String uriTail = uri.substring(uri.lastIndexOf("/")+1);
				
				WSAddressingSubscriptionIdentifier subscriptionIdentifier;
				synchronized (ConsumerServlet.this) {
					subscriptionIdentifier = uriToSubscription.get(uriTail);
				}
				
				try {
					SubscriptionManager.getInstance().onMatchedSubscription(subscriptionIdentifier,
							getSoapBody(request));
				} catch (XmlException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	protected XmlObject getSoapBody(String request) throws XmlException {
		XmlObject doc = XmlObject.Factory.parse(request);
		if (doc instanceof EnvelopeDocument) {
			Envelope envelope = ((EnvelopeDocument) doc).getEnvelope();
			XmlCursor cur = envelope.getBody().newCursor();
			if (cur.toFirstChild()) {
				return cur.getObject();
			}
		}
		return null;
	}

	public String getUrl() {
		return url;
	}

	public String receiveUniqueConsumerId() {
		return UUID.randomUUID().toString();
	}

	public void registerSubscription(String internalConsumerUrl,
			WSAddressingSubscriptionIdentifier subscriptionIdentifier) {
		this.uriToSubscription.put(internalConsumerUrl, subscriptionIdentifier);
	}
	
}
