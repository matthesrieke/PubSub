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

import java.util.Map;

import org.apache.xmlbeans.XmlObject;

/**
 * Interface for the underlying event processing implementation.
 * 
 * @author matthes rieke
 *
 */
public interface PubSubProcesser {

	/**
	 * Process a subscribe request.
	 * 
	 * @param payload the subscription as XML
	 * @return the response as XML
	 * @throws PubSubProcessorExcecption
	 */
	XmlObject processSubscribe(XmlObject payload) throws PubSubProcessorExcecption;

	/**
	 * Process an unsubscribe request.
	 * 
	 * @param payload the unsubscribe as XML
	 * @param map SOAP headers to enable resource managing
	 * @return the response as XML
	 * @throws PubSubProcessorExcecption
	 */
	XmlObject processUnsubscribe(XmlObject payload, Map<String, SoapHeader> map) throws PubSubProcessorExcecption;

	/**
	 * Generic method for processing a message object.
	 * This method is used to push data to be filtered into the PubSub
	 * processor.
	 * 
	 * @param e the message as {@link Object}
	 */
	void processMessage(Object e);

}
