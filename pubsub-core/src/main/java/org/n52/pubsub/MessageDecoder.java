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

/**
 * @author matthes rieke
 *
 * @param <I>
 * @param <O>
 */
public interface MessageDecoder <I, O> {

	public O decode(I input) throws DecodingException;

	public class DecodingException extends Exception {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DecodingException(String m) {
			super(m);
		}

		public DecodingException(Throwable e) {
			super(e);
		}
		
	}
}
