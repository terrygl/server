package org.ohmage.request.observer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ohmage.annotator.Annotator.ErrorCode;
import org.ohmage.exception.ValidationException;
import org.ohmage.request.InputKeys;
import org.ohmage.request.UserRequest;
import org.ohmage.validator.ObserverValidators;

public class StreamUploadRequest extends UserRequest {
	private static final Logger LOGGER = 
		Logger.getLogger(StreamUploadRequest.class);

	private final String observerId;
	private final Long observerVersion;
	private final String streamId;
	private final Long streamVersion;
	private final byte[] data;
	
	
	public StreamUploadRequest(final HttpServletRequest httpRequest) {
		super(httpRequest, TokenLocation.PARAMETER, false);
		
		String tObserverId = null;
		Long tObserverVersion = null;
		String tStreamId = null;
		Long tStreamVersion = null;
		byte[] tData = null;
		
		if(! isFailed()) {
			LOGGER.info("Creating a stream upload request.");
			String[] t;
			
			try {
				String[] uriParts = httpRequest.getRequestURI().split("/");
				
				tObserverId = 
					ObserverValidators.validateObserverId(
						uriParts[uriParts.length - 2]);
				if(tObserverId == null) {
					throw new ValidationException(
						ErrorCode.OBSERVER_INVALID_ID,
						"The observer's ID is missing.");
				}
				
				tObserverVersion = 
					ObserverValidators.validateObserverVersion(
						uriParts[uriParts.length - 1]);
				if(tObserverVersion == null) {
					throw new ValidationException(
						ErrorCode.OBSERVER_INVALID_VERSION,
						"The observer's version is missing.");
				}
				
				t = getParameterValues(InputKeys.STREAM_ID);
				if(t.length > 1) {
					throw new ValidationException(
						ErrorCode.OBSERVER_INVALID_STREAM_ID,
						"Multiple stream IDs were given: " + 
							InputKeys.STREAM_ID);
				}
				else if(t.length == 1) {
					tStreamId = ObserverValidators.validateStreamId(t[0]);
				}
				if(tStreamId == null) {
					throw new ValidationException(
						ErrorCode.OBSERVER_INVALID_STREAM_ID,
						"The stream ID is missing: " + InputKeys.STREAM_ID);
				}
				
				t = getParameterValues(InputKeys.STREAM_VERSION);
				if(t.length > 1) {
					throw new ValidationException(
						ErrorCode.OBSERVER_INVALID_STREAM_VERSION,
						"Multiple stream versions were given: " +
							InputKeys.STREAM_VERSION);
				}
				else if(t.length == 1) {
					tStreamVersion = 
				}
			}
			catch(ValidationException e) {
				e.failRequest(this);
				e.logException(LOGGER);
			}
		}
		
		observerId = tObserverId;
		observerVersion = tObserverVersion;
		streamId = tStreamId;
		streamVersion = tStreamVersion;
		data = tData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ohmage.request.Request#service()
	 */
	@Override
	public void service() {
		LOGGER.info("Servicing a stream upload request.");
		
		if(! authenticate(AllowNewAccount.NEW_ACCOUNT_DISALLOWED)) {
			return;
		}
		
		try {
			
		}
		catch(ServiceException e) {
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.ohmage.request.Request#respond(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void respond(
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		
		super.respond(httpRequest, httpResponse, null);
	}
}