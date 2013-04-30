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

import javax.xml.namespace.QName;

public class Constants {

	public static final String NS_PUBSUB = "http://www.opengis.net/pubsub/1.0/core";

    public static final String NS_PUBSUB_PREFIX = "psc";

    public static final String RESOURCE_ID_NS = "http://52north.org/pubsub";

	public static final String PUBSUB_SUBSCRIPTION_IDENTIFIER = "SubscriptionIdentifier";

	public static final QName PUBSUB_SUBSCRIPTION_IDENTIFIER_QNAME = new QName(RESOURCE_ID_NS, PUBSUB_SUBSCRIPTION_IDENTIFIER);

	private static String subscriptionManagerUrl;

	public static String getPubSubSuscriptionManagerUrl() {
		return subscriptionManagerUrl;
	}
	
	public static void setPubSubSuscriptionManagerUrl(String url) {
		// XXX CALL!
		subscriptionManagerUrl = url;
	}
      

}
