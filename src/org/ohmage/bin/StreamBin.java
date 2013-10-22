package org.ohmage.bin;

import java.util.List;

import org.ohmage.domain.exception.InvalidArgumentException;
import org.ohmage.domain.stream.Stream;

/**
 * <p>
 * The interface to the database-backed stream repository.
 * </p>
 * 
 * @author John Jenkins
 */
public abstract class StreamBin {
	/**
	 * The singular instance of this class.
	 */
	private static StreamBin instance;

	/**
	 * Initializes the singleton instance to this.
	 */
	protected StreamBin() {
		instance = this;
	}

	/**
	 * Retrieves the singleton instance of this class.
	 * 
	 * @return The singleton instance of this class.
	 */
	public static final StreamBin getInstance() {
		return instance;
	}

	/**
	 * Adds a new stream to the repository.
	 * 
	 * @param stream
	 *        The stream to add.
	 * 
	 * @throws IllegalArgumentException
	 *         The stream is null.
	 * 
	 * @throws InvalidArgumentException
	 *         A stream with the same ID-version pair already exists.
	 */
	public abstract void addStream(
		final Stream stream)
		throws IllegalArgumentException, InvalidArgumentException;

	/**
	 * Returns a list of the visible stream IDs.
	 * 
	 * @return A list of the visible stream IDs.
	 */
	public abstract List<String> getStreamIds();

	/**
	 * Returns a list of the versions for a given stream.
	 * 
	 * @param streamId
	 *        The unique identifier for the stream.
	 * 
	 * @return A list of the versions of the stream.
	 * 
	 * @throws IllegalArgumentException
	 *         The stream ID is null.
	 */
	public abstract List<Long> getStreamVersions(
		final String streamId)
		throws IllegalArgumentException;

	/**
	 * Returns a Stream object for the desired stream.
	 * 
	 * @param streamId
	 *        The unique identifier for the stream.
	 * 
	 * @param streamVersion
	 *        The version of the stream.
	 * 
	 * @return A Stream object that represents this stream.
	 * 
	 * @throws IllegalArgumentException
	 *         The stream ID and/or version are null.
	 */
	public abstract Stream getStream(
		final String streamId,
		final Long streamVersion)
		throws IllegalArgumentException;
	
	/**
	 * Returns whether or not a stream exists.
	 * 
	 * @param streamId
	 *        The stream's unique identifier. Required.
	 * 
	 * @param streamVersion
	 *        A specific version of a stream. Optional.
	 * 
	 * @return Whether or not the stream exists.
	 * 
	 * @throws IllegalArgumentException
	 *         The stream ID is null.
	 */
	public abstract boolean exists(
		final String streamId,
		final Long streamVersion)
		throws IllegalArgumentException;

	/**
	 * Returns a Stream object that represents the stream with the greatest
	 * version number or null if no streams exist with the given ID.
	 * 
	 * @param streamId
	 *        The unique identifier for the stream.
	 * 
	 * @return A Stream object that represents the stream with the greatest
	 *         version number or null if no streams exist with the given ID.
	 * 
	 * @throws IllegalArgumentException
	 *         The stream ID is null.
	 */
	public abstract Stream getLatestStream(
		final String streamId)
		throws IllegalArgumentException;
}