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
package org.n52.pubsub;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.HttpClientException;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.n52.pubsub.subscribe.SubscriptionIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.n52.pubsub.binding.SoapBindingHelper.*;

/**
 * Class for managing Subscription endpoints and their IDs.
 * 
 * @author matthes rieke
 *
 */
public class SubscriptionManager {

	private static SubscriptionManager instance;
	private static final Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);
	
	public static synchronized SubscriptionManager getInstance() {
		if (instance == null) {
			instance = new SubscriptionManager();
		}
		return instance;
	}
	
	private Map<SubscriptionIdentifier, URL> subscriptions = new HashMap<SubscriptionIdentifier, URL>();

	private SubscriptionManager() {
		
	}
	
	/**
	 * this methods shall whenever a matching
	 * 
	 * @param subscriptionIdentifier
	 * @param message
	 */
	public void onMatchedSubscription(SubscriptionIdentifier subscriptionIdentifier, XmlObject message) {
		URL targetURL;
		synchronized (this) {
			targetURL = subscriptions.get(subscriptionIdentifier);	
		}
		
		if (targetURL == null) {
			logger.warn("The deliveryLocation for Subscription '{}' is not available.",
					subscriptionIdentifier);
			return;
		}
		
		sendNotify(targetURL, wrapWithSoapEnvelope(wrapWithNotify(message)));
	}
	
	/**
	 * Register a {@link SubscriptionIdentifier} to {@link URL} mapping.
	 * 
	 * @param subscriptionIdentifier
	 * @param targetURL
	 */
	public synchronized void registerSubscription(SubscriptionIdentifier subscriptionIdentifier, URL targetURL) {
		this.subscriptions.put(subscriptionIdentifier, targetURL);
	}

	/**
	 * Remove the mapping.
	 * 
	 * @param subscriptionIdentifier the key of the mapping to be removed
	 */
	public synchronized void removeSubscription(SubscriptionIdentifier subscriptionIdentifier) {
		this.subscriptions.remove(subscriptionIdentifier);
	}

	private void sendNotify(URL targetURL, XmlObject payloadToSend) {
		HttpClient client = new ProxyAwareHttpClient(new SimpleHttpClient(5000));
		try {
			HttpResponse response = client.executePost(targetURL.toExternalForm(), payloadToSend);
			if (response != null && response.getStatusLine().getStatusCode() >= 300) {
				logger.warn("Failed to send NotificationMessage to {}. HTTP Status: {}",
						targetURL, response.getStatusLine().getStatusCode());
			}
		} catch (HttpClientException e) {
			logger.warn("Failed to send NotificationMessage to {}. Exception was: {}", targetURL, e.getMessage());
		}
	}

}
