package org.ohmage.bin;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.ohmage.domain.ColumnList;
import org.ohmage.domain.survey.SurveyResponse;

/**
 * <p>
 * The interface to the database-backed survey response repositoy.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class SurveyResponseBin {
    /**
     * The singular instance of this class.
     */
    private static SurveyResponseBin instance;

    /**
     * Initializes the singleton instance to this.
     */
    protected SurveyResponseBin() {
        instance = this;
    }

    /**
     * Retrieves the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    public static final SurveyResponseBin getInstance() {
        return instance;
    }

    /**
     * Adds new survey responses to the repository.
     *
     * @param surveyResponses
     *        The survey responses to add.
     *
     * @throws IllegalArgumentException
     *         The survey responses are null.
     *
     * @throws IllegalStateException
     *         A survey response or media file has a duplicate ID, which should
     *         have alrady been accounted for.
     */
    public abstract void addSurveyResponses(
        final List<SurveyResponse> surveyResponses)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Determines which survey response IDs already exist for the given owner
     * and survey.
     *
     * @param owner
     *        The owner's unique identifier.
     *
     * @param surveyId
     *        the survey response's unique identifier.
     *
     * @param surveyVersion
     *        The survey's version.
     *
     * @param candidateIds
     *        The survey response IDs to check against.
     *
     * @return The duplicate survey response IDs.
     *
     * @throws IllegalArgumentException
     *         A required parameter was null.
     */
    public abstract MultiValueResult<String> getDuplicateIds(
        final String owner,
        final String surveyId,
        final long surveyVersion,
        final Set<String> candidateIds)
        throws IllegalArgumentException;

    /**
     * Retrieves survey responses specific to a user and a survey.
     *
     * @param surveyId
     *        The survey's unique identifier.
     *
     * @param surveyVersion
     *        The version of the survey.
     *
     * @param userIds
     *        The unique identifiers of the users whose survey responses are
     *        desired.
     *
     * @param surveyResponseIds
     *        A specific set of survey response IDs that should be returned.
     *        Null indicates that this parameter should be ignored, while an
     *        empty collection indicates that no survey responses will be
     *        returned.
     *
     * @param startDate
     *        The earliest time for any given point. Null indicates that there
     *        is no earliest time.
     *
     * @param endDate
     *        The latest time for any given point. Null indicates that there is
     *        no latest time.
     *
     * @param columnList
     *        The projection of columns that should be returned. Null indicates
     *        that all columns should be returned.
     *
     * @param chronological
     *        Whether or not the data should be sorted in chronological order
     *        (as opposed to reverse-chronological order).
     *
     * @param numToSkip
     *        The number of survey responses to skip.
     *
     * @param numToReturn
     *        The number of survey responses to return.
     *
     * @return The survey responses that matches the parameters.
     *
     * @throws IllegalArgumentException
     *         A required parameter was null.
     */
    public abstract MultiValueResult<? extends SurveyResponse> getSurveyResponses(
        final String surveyId,
        final long surveyVersion,
        final Set<String> userIds,
        final Collection<String> surveyResponseIds,
        final DateTime startDate,
        final DateTime endDate,
        final ColumnList columnList,
        final boolean chronological,
        final long numToSkip,
        final long numToReturn)
        throws IllegalArgumentException;

    /**
     * Retrieves a specific survey response.
     *
     * @param owner
     *        The user's unique identifier.
     *
     * @param surveyId
     *        The survey's unique identifier.
     *
     * @param surveyVersion
     *        The version of the survey.
     *
     * @param pointId
     *        The unique identifier of the specific point.
     *
     * @return The survey responses that matches the parameters.
     *
     * @throws IllegalArgumentException
     *         A required parameter was null.
     */
    public abstract SurveyResponse getSurveyResponse(
        final String owner,
        final String surveyId,
        final long surveyVersion,
        final String pointId)
        throws IllegalArgumentException;

    /**
     * Returns the survey response that references the given media file.
     *
     * @param mediaId
     *        The media's unique identifier.
     *
     * @return The survey response that references the given media file or null
     *         if no such media file exists.
     *
     * @throws IllegalArgumentException
     *         A required parameter was null.
     */
    public abstract SurveyResponse getSurveyResponseForMedia(
        final String mediaId)
        throws IllegalArgumentException;

    /**
     * Deletes a specific survey response.
     *
     * @param owner
     *        The user's unique identifier.
     *
     * @param surveyId
     *        The survey's unique identifier.
     *
     * @param surveyVersion
     *        The version of the survey.
     *
     * @param pointId
     *        The unique identifier of the specific point.
     *
     * @throws IllegalArgumentException
     *         A required parameter was null.
     */
    public abstract void deleteSurveyResponse(
        final String owner,
        final String surveyId,
        final long surveyVersion,
        final String pointId)
        throws IllegalArgumentException;
}