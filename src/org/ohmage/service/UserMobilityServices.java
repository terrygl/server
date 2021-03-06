/*******************************************************************************
 * Copyright 2012 The Regents of the University of California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohmage.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.ohmage.annotator.Annotator.ErrorCode;
import org.ohmage.domain.campaign.Campaign;
import org.ohmage.exception.DataAccessException;
import org.ohmage.exception.ServiceException;
import org.ohmage.query.ICampaignQueries;
import org.ohmage.query.IUserCampaignQueries;
import org.ohmage.query.IUserMobilityQueries;

/**
 * This class contains all of the services pertaining to reading and writing
 * user-Mobility information.
 * 
 * @author John Jenkins
 */
public class UserMobilityServices {
	private static UserMobilityServices instance;
	
	private static final long MILLIS_IN_A_HOUR = 60 * 60 * 1000;
	private static final int HOURS_IN_A_DAY = 24;
	
	private ICampaignQueries campaignQueries;
	private IUserCampaignQueries userCampaignQueries;
	private IUserMobilityQueries userMobilityQueries;
	
	/**
	 * Default constructor. Privately instantiated via dependency injection
	 * (reflection).
	 * 
	 * @throws IllegalStateException if an instance of this class already
	 * exists
	 * 
	 * @throws IllegalArgumentException if iCampaignQueries or 
	 * iUserCampaignQueriesor or iUserMobilityQueries is null
	 */
	private UserMobilityServices(ICampaignQueries iCampaignQueries, 
			IUserCampaignQueries iUserCampaignQueries, IUserMobilityQueries iUserMobilityQueries) {
		
		if(instance != null) {
			throw new IllegalStateException("An instance of this class already exists.");
		}
		
		if(iCampaignQueries == null) {
			throw new IllegalArgumentException("An instance of ICampaignQueries is required.");
		}
		if(iUserCampaignQueries == null) {
			throw new IllegalArgumentException("An instance of IUserCampaignQueries is required.");
		}
		if(iUserMobilityQueries == null) {
			throw new IllegalArgumentException("An instance of IUserMobilityQueries is required.");
		}

		
		campaignQueries = iCampaignQueries;
		userCampaignQueries = iUserCampaignQueries;
		userMobilityQueries = iUserMobilityQueries;
		
		instance = this;
	}
	
	/**
	 * @return  Returns the singleton instance of this class.
	 */
	public static UserMobilityServices instance() {
		return instance;
	}
	
	/**
	 * Checks if some "requesting" user can view Mobility data of another data.
	 * It may be that there is no data or that all data is private; therefore,
	 * while it is acceptable for a requester to view the data about another
	 * user, there is no data to view. For this to return true, one of the 
	 * following rules must be true:<br />
	 * <ul>
	 * <li>The requesting user is attempting to view the own Mobility data.
	 * </li>
	 * <li>The requesting user is a supervisor in any campaign to which the user
	 *   belongs.</li>
	 * <li>The requesting user is an analyst in any campaign to which the user
	 *   belongs and the campaign is shared.</li>
	 * </ul>
	 * 
	 * @param requestersUsername The username of the user that is attempting to
	 * 							 view data about another user.
	 * 
	 * @param usersUsername The username of the user whose information is being
	 * 						queried.
	 * 
	 * @throws ServiceException Thrown if the requesting user doesn't have
	 * 							sufficient permissions to read Mobility 
	 * 							information about another user or if there is
	 * 							an error.
	 */
	public void requesterCanViewUsersMobilityData(
			final String requestersUsername, final String usersUsername) 
			throws ServiceException {
		
		try {
			if(requestersUsername.equals(usersUsername)) {
				return;
			}
			
			Set<String> campaignIds = userCampaignQueries.getCampaignIdsAndNamesForUser(usersUsername).keySet();
			for(String campaignId : campaignIds) {
				List<Campaign.Role> requestersCampaignRoles = userCampaignQueries.getUserCampaignRoles(requestersUsername, campaignId);
				
				if(requestersCampaignRoles.contains(Campaign.Role.SUPERVISOR)) {
					return;
				}
				else if(requestersCampaignRoles.contains(Campaign.Role.ANALYST) && 
						Campaign.PrivacyState.SHARED.equals(campaignQueries.getCampaignPrivacyState(campaignId))) {
					return;
				}
			}
			
			throw new ServiceException(
					ErrorCode.MOBILITY_INSUFFICIENT_PERMISSIONS, 
					"Insufficient permissions to read Mobility information about another user.");
		}
		catch(DataAccessException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Retrieves the number of hours since the last Mobility upload from a 
	 * user.
	 * 
	 * @param username The username of the user in question.
	 * 
	 * @return Returns a double value representing the number of hours since 
	 * 		   the last time that some user uploaded Mobility points or null if
	 * 		   there are none.
	 * 
	 * @throws ServiceException Thrown if there is an error.
	 */
	public Double getHoursSinceLastMobilityUpload(final String username)
			throws ServiceException {
		
		try {
			Date lastMobilityUpload = userMobilityQueries.getLastUploadForUser(username);
			if(lastMobilityUpload == null) {
				return null;
			}
			else {
				long differenceInMillis = Calendar.getInstance().getTimeInMillis() - lastMobilityUpload.getTime();
				
				return new Double(differenceInMillis) / new Double(MILLIS_IN_A_HOUR);
			}
		}
		catch(DataAccessException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Retrieves the percentage of non-null location values in all of the 
	 * updates in the last 24 hours.
	 * 
	 * @param username The username of the user in question.
	 * 
	 * @return Returns a double value representing the percentage of non-null
	 * 		   location values from all of the Mobility uploads in the last 24
	 * 		   hours or null if there are none.
	 * 
	 * @throws ServiceException Thrown if there is an error.
	 */
	public Double getPercentageOfNonNullLocationsOverPastDay(
			final String username) throws ServiceException {
		
		try {
			return userMobilityQueries.getPercentageOfNonNullLocations(username, HOURS_IN_A_DAY);
		}
		catch(DataAccessException e) {
			throw new ServiceException(e);
		}
	}
}
