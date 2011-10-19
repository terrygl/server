package org.ohmage.domain.campaign.prompt;

import org.ohmage.domain.campaign.Response.NoResponse;
import org.ohmage.domain.campaign.response.HoursBeforeNowPromptResponse;

/**
 * This class represents hours-before-now prompts.
 * 
 * @author John Jenkins
 */
public class HoursBeforeNowPrompt extends BoundedPrompt {
	/**
	 * Creates a hours-before-now prompt.
	 * 
	 * @param id The unique identifier for the prompt within its survey item
	 * 			 group.
	 * 
	 * @param condition The condition determining if this prompt should be
	 * 					displayed.
	 * 
	 * @param unit The unit value for this prompt.
	 * 
	 * @param text The text to be displayed to the user for this prompt.
	 * 
	 * @param abbreviatedText An abbreviated version of the text to be 
	 * 						  displayed to the user for this prompt.
	 * 
	 * @param explanationText A more-verbose version of the text to be 
	 * 						  displayed to the user for this prompt.
	 * 
	 * @param skippable Whether or not this prompt may be skipped.
	 * 
	 * @param skipLabel The text to show to the user indicating that the prompt
	 * 					may be skipped.
	 * 
	 * @param displayType This prompt's
	 * 					 {@link org.ohmage.domain.campaign.Prompt.DisplayType}.
	 * 
	 * @param displayLabel The display label for this prompt.
	 * 
	 * @param min The lower bound for a response to this prompt.
	 * 
	 * @param max The upper bound for a response to this prompt.
	 * 
	 * @param defaultValue The default value for this prompt. This is optional
	 * 					   and may be null if one doesn't exist.
	 * 
	 * @param index This prompt's index in its container's list of survey 
	 * 				items.
	 * 
	 * @throws IllegalArgumentException Thrown if any of the required 
	 * 									parameters are missing or invalid. 
	 */
	public HoursBeforeNowPrompt(final String id, final String condition, 
			final String unit, final String text, 
			final String abbreviatedText, final String explanationText,
			final boolean skippable, final String skipLabel,
			final DisplayType displayType, final String displayLabel,
			final long min, final long max, final Long defaultValue, 
			final int index) {
		
		super(id, condition, unit, text, abbreviatedText, explanationText,
				skippable, skipLabel, displayType, displayLabel, 
				min, max, defaultValue, Type.HOURS_BEFORE_NOW, index);
	}
	
	/**
	 * Creates a response to this prompt based on a response value.
	 * 
	 * @param response The response from the user as an Object.
	 * 
	 * @param repeatableSetIteration If this prompt belongs to a repeatable 
	 * 								 set, this is the iteration of that 
	 * 								 repeatable set on which the response to
	 * 								 this prompt was made.
	 * 
	 * @throws IllegalArgumentException Thrown if this prompt is part of a
	 * 									repeatable set but the repeatable set
	 * 									iteration value is null, if the
	 * 									repeatable set iteration value is 
	 * 									negative, or if the value is not a 
	 * 									valid response value for this prompt.
	 */
	@Override
	public HoursBeforeNowPromptResponse createResponse(final Object response, 
			final Integer repeatableSetIteration) {
		
		Object validatedResponse = validateValue(response);
		if(validatedResponse instanceof NoResponse) {
			return new HoursBeforeNowPromptResponse(
					this, 
					(NoResponse) validatedResponse, 
					repeatableSetIteration, 
					null,
					false
				);
		}
		else if(validatedResponse instanceof Long) {
			return new HoursBeforeNowPromptResponse(
					this, 
					null, 
					repeatableSetIteration, 
					(Long) validatedResponse,
					false
				);
		}
			
		throw new IllegalArgumentException("The response was not a valid response.");
	}
}