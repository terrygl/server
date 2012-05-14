package org.ohmage.domain;

import java.io.IOException;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DecoderFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.ohmage.domain.Observer.Stream;
import org.ohmage.exception.DomainException;

/**
 * This class represents a set of data generated by a stream. It retains the
 * reference to the stream that was used to decode the data.
 *
 * @author John Jenkins
 */
public class DataStream {
	private static final DecoderFactory DECODER_FACTORY = new DecoderFactory();
	
	/**
	 * This class represents the meta-data for a data stream. All fields are 
	 * optional. This class is immutable and, therefore, thread-safe.
	 *
	 * @author John Jenkins
	 */
	public static class MetaData {
		/**
		 * This class is responsible for building new MetaData objects. This
		 * class is mutable and, therefore, not thread-safe.
		 *
		 * @author John Jenkins
		 */
		public static class Builder {
			private DateTime timestamp = null;
			private Location location = null;
			
			/**
			 * Creates an empty builder.
			 */
			public Builder() {};
			
			/**
			 * Analyzes the meta-data JSON object and attempts to retrieve the
			 * time information. If it is missing, it will simply be ignored, 
			 * but if it exists, then it will attempt to interpret it. If it
			 * cannot be interpreted, then an exception is raised.
			 * 
			 * @param metaData The meta-data JSON.
			 * 
			 * @throws DomainException A valid key was found, but its value was
			 * 						   not valid.
			 */
			public void setTimestamp(
					final JSONObject metaData) 
					throws DomainException {
				
				if(metaData.has("timestamp")) {
					try {
						timestamp =
							ISODateTimeFormat.basicDateTime().parseDateTime(
								metaData.getString("timestamp"));
					}
					catch(JSONException e) {
						throw new DomainException(
							"The timestamp isn't a string.", 
							e);
					}
				}
				else if(metaData.has("time")) {
					long time;
					try {
						time = metaData.getLong("time");
					}
					catch(JSONException e) {
						throw new DomainException("The time isn't a long.", e);
					}
						
					DateTimeZone timeZone = DateTimeZone.UTC;
					if(metaData.has("timezone")) {
						try {
							timeZone = 
								DateTimeZone.forID(
									metaData.getString("timezone"));
						}
						catch(JSONException e) {
							throw new DomainException(
								"The time zone isn't a string.", 
								e);
						}
						catch(IllegalArgumentException e) {
							throw new DomainException(
								"The time zone is not known.",
								e);
						}
					}
					
					timestamp = new DateTime(time, timeZone);
				}
			}
			
			/**
			 * Analyzes the meta-data JSON object and attempts to retrieve the
			 * location information. If it is missing it will simply be 
			 * ignored, but if it exists, then it will attempt to interpret it.
			 * If it cannot be interpreted, then an exception will be raised.
			 * 
			 * @param metaData The meta-data JSON.
			 * 
			 * @throws DomainException The location keys exist, but their 
			 * 						   values were invalid.
			 */
			public void setLocation(
					final JSONObject metaData) 
					throws DomainException {
				
				if(metaData.has("location")) {
					try {
						location = 
							new Location(metaData.getJSONObject("location"));
					}
					catch(JSONException e) {
						throw new DomainException(
							"The location was not a JSON object.", 
							e);
					}
					catch(DomainException e) {
						throw new DomainException(
							"The location JSON object was invalid.",
							e);
					}
				}
			}
			
			/**
			 * Builds the MetaData object.
			 * 
			 * @return The MetaData object.
			 */
			public MetaData build() {
				return new MetaData(timestamp, location);
			}
		}
		
		private final DateTime timestamp;
		private final Location location;
		
		/**
		 * Creates a new MetaData object.
		 * 
		 * @param timestamp The time stamp for this meta-data.
		 * 
		 * @param location The location for this meta-data.
		 */
		public MetaData(
				final DateTime timestamp, 
				final Location location) {
			
			this.timestamp = timestamp;
			this.location = location;
		}

		/**
		 * Returns timestamp.
		 *
		 * @return The timestamp.
		 */
		public DateTime getTimestamp() {
			return timestamp;
		}

		/**
		 * Returns location.
		 *
		 * @return The location.
		 */
		public Location getLocation() {
			return location;
		}
	}
	private final MetaData metaData;
	
	private final Stream stream;
	private final byte[] data;

	/**
	 * Creates a new DataStream from binary data generated by Avro.
	 * 
	 * @param stream The stream that contains the definition on how to decode
	 *				 the data.
	 *
	 * @param metaData The meta-data.
	 * 
	 * @param data The data.
	 * 
	 * @throws DomainException One of the parameters is invalid or null.
	 */
	public DataStream(
			final Stream stream,
			final MetaData metaData,
			final byte[] data) 
			throws DomainException {

		if(stream == null) {
			throw new DomainException("The stream is null.");
		}
		else if(data == null) {
			throw new DomainException("The data is null.");
		}
		else if(data.length == 0) {
			throw new DomainException("The data is empty.");
		}
		
		// Save the reference to the stream.
		this.stream = stream;
		
		// Save the meta-data.
		this.metaData = metaData;
		
		// Decode the data from the stream.
		GenericDatumReader<GenericContainer> genericReader =
			new GenericDatumReader<GenericContainer>(stream.getSchema());
		try {
			genericReader.read(
				null, 
				DECODER_FACTORY.binaryDecoder(data, null));
		}
		catch(IOException e) {
			throw new DomainException(e);
		}
		catch(AvroRuntimeException e) {
			throw new DomainException("The data is invalid.", e);
		}
		this.data = data;
	}

	/**
	 * Returns the stream.
	 *
	 * @return The stream.
	 */
	public Stream getStream() {
		return stream;
	}

	/**
	 * Returns the meta-data.
	 *
	 * @return The meta-data.
	 */
	public MetaData getMetaData() {
		return metaData;
	}
	
	/**
	 * Returns the binary data as a byte array.
	 * 
	 * @return The binary data as a byte array.
	 */
	public byte[] getBinaryData() {
		// FIXME: Deep copy.
		return data;
	}
}