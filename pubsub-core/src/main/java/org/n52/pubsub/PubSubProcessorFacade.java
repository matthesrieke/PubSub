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

import org.n52.pubsub.ses.EventServiceProcessor;

/**
 * @author matthes rieke
 *
 */
public class PubSubProcessorFacade {
	
	private static PubSubProcessorFacade instance = null;
	private EventServiceProcessor processorInstance;

	private PubSubProcessorFacade() {
		//TODO Reflections or ServiceLoader
		this.processorInstance = new EventServiceProcessor(); 
	}
	
	public static synchronized PubSubProcessorFacade getInstance() {
		if (instance == null) {
			instance = new PubSubProcessorFacade();
		}
		return instance;
	}

	public PubSubProcesser getProcessorInstance() {
		return processorInstance;
	}

}