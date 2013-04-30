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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.FilterDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.oxf.ses.adapter.SESRequestBuilder_00;
import org.n52.oxf.ses.adapter.client.httplistener.SimpleConsumerServlet;
import org.n52.oxf.ses.adapter.client.httplistener.SimpleConsumerServlet.CallbackOnAvailableListener;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.pubsub.MessageDecoder;
import org.n52.pubsub.MessageDecoder.DecodingException;
import org.n52.pubsub.Constants;
import org.n52.pubsub.PubSubProcesser;
import org.n52.pubsub.PubSubProcessorExcecption;
import org.n52.pubsub.SoapHeader;
import org.n52.pubsub.SubscriptionManager;
import org.n52.pubsub.soap.PubSubSoapHeader;
import org.n52.pubsub.soap.subscribe.SubscribeResponse;
import org.n52.pubsub.soap.subscribe.UnsubscribeResponse;
import org.n52.pubsub.subscribe.SubscriptionIdentifier;
import org.n52.pubsub.subscribe.WSAddressingSubscriptionIdentifier;
import org.oasisOpen.docs.wsn.b2.FilterType;
import org.oasisOpen.docs.wsn.b2.QueryExpressionType;
import org.oasisOpen.docs.wsn.b2.SubscribeDocument;
import org.oasisOpen.docs.wsn.b2.SubscribeDocument.Subscribe;

/**
 * Implementation delegating to a Sensor Event Service
 * instance.
 * 
 * @author matthes rieke
 *
 */
public class EventServiceProcessor implements PubSubProcesser {
	
	private static final String SES_SERVICE_URL = "http://localhost:8080/pubsub-ses/services/Broker";
	private static final String SES_SUBSCRIPTION_MANAGER_URL = "http://localhost:8080/pubsub-ses/services/SubscriptionManager";
	private SESAdapter adapter;
	private ConsumerServlet consumerServlet;

	public EventServiceProcessor() {
		adapter = new SESAdapter();
		ConsumerServlet.registerCallbackOnAvailable(new CallbackOnAvailableListener() {
			@Override
			public void onConsumerServletAvailable(SimpleConsumerServlet servlet) {
				if (servlet instanceof ConsumerServlet) {
					consumerServlet = (ConsumerServlet) servlet;
				}
			}
		});
	}

	private String extractFilterXml(XmlObject obj) {
		if (obj instanceof net.opengis.fes.x20.FilterType) {
			net.opengis.fes.x20.FilterType fesFilter = (net.opengis.fes.x20.FilterType) obj;
			FilterDocument filterDoc = FilterDocument.Factory.newInstance();
			filterDoc.setFilter(fesFilter);
			return filterDoc.xmlText();
		}
		return XmlUtil.stripText(obj);
	}


	/**
	 * Prepare the parameters for a Subscribe request
	 */
	private void parseSubscribeParameters(Subscribe subscribe, ParameterContainer params) throws OXFException {
		FilterType filter = subscribe.getFilter();
		XmlCursor filterCur = filter.newCursor();
		
		if (filterCur.toFirstChild()) {
			XmlObject obj = filterCur.getObject();
			if (obj instanceof QueryExpressionType) {
				QueryExpressionType query = (QueryExpressionType) obj;
				params.addParameterShell(SESRequestBuilder_00.SUBSCRIBE_FILTER_MESSAGE_CONTENT_DIALECT,
						query.getDialect());
				filterCur.toFirstChild();
				
				String filterXml = extractFilterXml(filterCur.getObject());
				
				params.addParameterShell(SESRequestBuilder_00.SUBSCRIBE_FILTER_MESSAGE_CONTENT,
						filterXml);
			}
				
		} else throw new RuntimeException("No valid filter given in Subscribe request.");

		
	}

	@Override
	public void processMessage(Object e) {
		MessageDecoder<Object, XmlObject> decoder = resolveDecoder(e.getClass(), XmlObject.class);
		try {
			XmlObject xo = decoder.decode(e);
			sendNotify(xo);
		} catch (DecodingException e1) {
			e1.printStackTrace();
		} catch (OXFException e1) {
			e1.printStackTrace();
		} catch (ExceptionReport e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public XmlObject processSubscribe(XmlObject xo) throws PubSubProcessorExcecption {
		SubscribeDocument subscribe = (SubscribeDocument) xo;
		ParameterContainer params = new ParameterContainer();
		try {
			params.addParameterShell(SESRequestBuilder_00.SUBSCRIBE_SES_URL, SES_SERVICE_URL);
		} catch (OXFException e) {
			throw new RuntimeException(e);
		}
		
		String internalConsumerId = consumerServlet.receiveUniqueConsumerId();
		String internalConsumerUrl = consumerServlet.getUrl() + internalConsumerId;
		
		try {
			params.addParameterShell(SESRequestBuilder_00.SUBSCRIBE_CONSUMER_REFERENCE_ADDRESS,
					internalConsumerUrl);
		} catch (OXFException e) {
			throw new PubSubProcessorExcecption(e);
		}

		try {
			parseSubscribeParameters(subscribe.getSubscribe(), params);
		} catch (OXFException e) {
			throw new PubSubProcessorExcecption(e);
		}
		
		try {
			Operation op = new Operation(SESAdapter.SUBSCRIBE, null, SES_SERVICE_URL);
			OperationResult response = adapter.doOperation(op, params);
			SubscribeResponse result = new SubscribeResponse(response.getIncomingResultAsStream());
			
			WSAddressingSubscriptionIdentifier subscriptionIdentifier = new WSAddressingSubscriptionIdentifier(
					new URL(result.getSubscriptionReference().getAddress()));
			
			for (QName qn : result.getSubscriptionReference().getReferenceParameters().keySet()) {
				String value = result.getSubscriptionReference().getReferenceParameters().get(qn);
				XmlString valueObject = XmlString.Factory.newInstance();
				valueObject.setStringValue(value);
				subscriptionIdentifier.addReferenceParameter(qn, valueObject);
			}
			
			registerAtSubscriptionManager(result, subscribe, subscriptionIdentifier);
			
			registerAtConsumerServlet(internalConsumerId, subscriptionIdentifier);
			
			return result.toXML();
		} catch (Exception e) {
			throw new PubSubProcessorExcecption(e);
		}
	}
	

	@Override
	public XmlObject processUnsubscribe(XmlObject unsubscribe,
			Map<String, SoapHeader> map) throws PubSubProcessorExcecption {
		
		if (!map.containsKey(Constants.RESOURCE_ID_NS)) {
			throw new RuntimeException("Could not find Subscription Identifier!");
		}
		
		PubSubSoapHeader soapHeader = (PubSubSoapHeader) map.get(Constants.RESOURCE_ID_NS);
		
		ParameterContainer params = new ParameterContainer();
		try {
			params.addParameterShell(SESRequestBuilder_00.UNSUBSCRIBE_SES_URL, SES_SUBSCRIPTION_MANAGER_URL);
			params.addParameterShell(SESRequestBuilder_00.UNSUBSCRIBE_REFERENCE, soapHeader.getSubscriptionIdentifier());
		} catch (OXFException e) {
			throw new RuntimeException(e);
		}
		
		try {
			Operation op = new Operation(SESAdapter.UNSUBSCRIBE, null, SES_SUBSCRIPTION_MANAGER_URL);
			OperationResult response = adapter.doOperation(op, params);
			return new UnsubscribeResponse(response.getIncomingResultAsStream()).toXML();
		} catch (Exception e) {
			throw new PubSubProcessorExcecption(e);
		}
	}

	/**
	 * register a {@link SubscriptionIdentifier} at the
	 * internal consumer servlet.
	 */
	private void registerAtConsumerServlet(String internalConsumerUrl, WSAddressingSubscriptionIdentifier subscriptionIdentifier) {
		this.consumerServlet.registerSubscription(internalConsumerUrl, subscriptionIdentifier);
	}

	/**
	 * Register a {@link SubscriptionIdentifier} at the 
	 * PubSub SubscriptionManager instance.
	 */
	private void registerAtSubscriptionManager(SubscribeResponse result, SubscribeDocument subscribe, WSAddressingSubscriptionIdentifier subscriptionIdentifier) throws MalformedURLException {
		SubscriptionManager.getInstance().registerSubscription(subscriptionIdentifier,
				new URL(subscribe.getSubscribe().getConsumerReference().getAddress().getStringValue()));		
	}

	/**
	 * TODO implement a dynamic resolution of decoders:
	 * <ol>
	 * <li>Use the internal representation of events of the wrapping service</li>
	 * <li>Provide an output type (depending on the PubSub binding)</li>
	 * <li>do the conversion between IN and OUT</li>
	 * </ol>
	 */
	private MessageDecoder<Object, XmlObject> resolveDecoder(Class<?> classIn,
			Class<?> classOut) {
		//TODO resolve SOSDecoder.ObservationInsertionDecoder();
		return null;
	}

	private void sendNotify(XmlObject xo) throws OXFException, ExceptionReport {
		Operation op = new Operation(SESAdapter.NOTIFY, null, SES_SUBSCRIPTION_MANAGER_URL);
		ParameterContainer params = new ParameterContainer();
		params.addParameterShell(SESRequestBuilder_00.NOTIFY_SES_URL, SES_SERVICE_URL);
		params.addParameterShell(SESRequestBuilder_00.NOTIFY_XML_MESSAGE, xo.xmlText());
		OperationResult response = adapter.doOperation(op, params);
		if (response != null) {
			//TODO do something!
			response.getIncomingResultAsStream();
		}
	}


}
