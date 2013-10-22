package org.ohmage.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.ohmage.domain.exception.InvalidArgumentException;
import org.ohmage.domain.exception.OhmageException;
import org.ohmage.domain.jackson.OhmageObjectMapper;
import org.ohmage.domain.jackson.OhmageObjectMapper.JsonFilterField;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * <p>
 * A user in the system.
 * </p>
 * 
 * @author John Jenkins
 */
@JsonFilter(User.JACKSON_FILTER_GROUP_ID)
public class User extends OhmageDomainObject {
	/**
	 * <p>
	 * A builder for constructing {@link User} objects.
	 * </p>
	 *
	 * @author John Jenkins
	 */
	public static class Builder extends OhmageDomainObject.Builder<User> {
		/**
		 * The user's user-name.
		 */
		protected String username;
		/**
		 * The user's password, plain-text or hashed.
		 */
		protected String password;
		/**
		 * The user's email address.
		 */
		protected String email;
		/**
		 * The user's full name.
		 */
		protected String fullName;
		/**
		 * The authenticated information from a provider about this user.
		 */
		protected Map<String, ProviderUserInformation> providers;
		
		/**
		 * Creates a new builder with the outward-facing allowed parameters.
		 * 
		 * @param username
		 *        The user-name of the user.
		 * 
		 * @param password
		 *        The hashed password of the user.
		 * 
		 * @param email
		 *        The email address of the user.
		 * 
		 * @param fullName
		 *        The full name of the user, which may be null.
		 */
		@JsonCreator
		public Builder(
			@JsonProperty(JSON_KEY_USERNAME) final String username,
			@JsonProperty(JSON_KEY_EMAIL) final String email,
			@JsonProperty(JSON_KEY_FULL_NAME) final String fullName) {
			
			super(null);
			
			this.username = username;
			this.email = email;
			this.fullName = fullName;
		}

		/**
		 * Creates a new builder based on an existing User object.
		 * 
		 * @param user
		 *        The existing User object on which this Builder should be
		 *        based.
		 */
		public Builder(final User user) {
			super(user);
			
			this.username = user.username;
			this.password = user.password;
			this.email = user.email;
			this.fullName = user.fullName;
			this.providers = user.providers;
		}
		
		/**
		 * Returns the currently set user-name of the user.
		 * 
		 * @return The currently set user-name of the user.
		 */
		public String getUsername() {
			return username;
		}
		
		/**
		 * Returns the currently set password or null if no password is set.
		 * 
		 * @return The currently set password or null if no password is set.
		 */
		public String getPassword() {
			return password;
		}
		
		/**
		 * Sets the password for the user. If the password is in plain-text,
		 * the 'hash' parameter should always be set to true to avoid
		 * attempting to create an account with a plain-text password. To
		 * remove the password, pass null for the password and "false" for the
		 * 'hash' parameter.
		 * 
		 * @param password
		 *        The password. This may already be hashed or it may be
		 *        plain-text.
		 * 
		 * @param hash
		 *        Whether or not the password should be hashed. For plain-text
		 *        passwords, this should always be true.
		 * 
		 * @return This builder to facilitate chaining.
		 */
		public Builder setPassword(final String password, final boolean hash) {
			if(hash) {
				this.password = hashPassword(password);
			}
			else {
				this.password = password;
			}
			
			return this;
		}
		
		/**
		 * Sets the email address of the user. User null to remove the value.
		 * 
		 * @param email
		 *        The email address of the user or null to remove the currently
		 *        set value.
		 * 
		 * @return This builder to facilitate chaining.
		 */
		public Builder setEmail(final String email) {
			this.email = email;
			
			return this;
		}
		
		/**
		 * Returns the currently set full name of the user, which may be null
		 * indicating that there is no full name set.
		 * 
		 * @return The currently set full name of the user or null if there is
		 *         no currently set full name.
		 */
		public String getFullName() {
			return fullName;
		}
		
		/**
		 * Sets the full name of the user. Use null to remove the value.
		 * 
		 * @param fullName
		 *        The full name of the user or null to remove the current full
		 *        name.
		 * 
		 * @return This builder to facilitate chaining.
		 */
		public Builder setFullName(final String fullName) {
			this.fullName = fullName;
			
			return this;
		}
		
		/**
		 * Directly sets the map of providers. This can be used to remove the
		 * entire map of providers by passing null. It is recommended to
		 * instead use {@link #addProvider(String, ProviderUserInformation)}
		 * and {@link #removeProvider(String)}.
		 * 
		 * @param providers
		 *        The new map of providers.
		 * 
		 * @return This builder to facilitate chaining.
		 * 
		 * @see #addProvider(String, ProviderUserInformation)
		 * @see #removeProvider(String)
		 */
		public Builder setProviders(
			final Map<String, ProviderUserInformation> providers) {
			this.providers = providers;
			
			return this;
		}
		
		/**
		 * Adds new or overwrites existing information as generated by a
		 * provider.
		 * 
		 * @param providerId
		 *        The provider's unique identifier.
		 * 
		 * @param information
		 *        The information generated by the provider.
		 * 
		 * @return This builder to facilitate chaining.
		 */
		public Builder addProvider(
			final String providerId,
			final ProviderUserInformation information) {
			
			this.providers.put(providerId, information);
			
			return this;
		}
		
		/**
		 * Removes a provider's information about this user.
		 * 
		 * @param providerId
		 *        The provider's unique identifier.
		 * 
		 * @return This builder to facilitate chaining.
		 */
		public Builder removeProvider(final String providerId) {
			this.providers.remove(providerId);
			
			return this;
		}
		
		/**
		 * Returns the currently-set information about a user from a provider.
		 * 
		 * @param provider
		 *        The provider's unique identifier.
		 * 
		 * @return The currently set information about a user from a provider.
		 */
		public ProviderUserInformation getProvider(final String provider) {
			return providers.get(provider);
		}
		
		/**
		 * Creates a {@link User} object based on the state of this builder.
		 * 
		 * @return A {@link User} object based on the state of this builder.
		 * 
		 * @throws OhmageException
		 *         The state of the builder contained invalid fields.
		 */
		public User build() {
			return
				new User(
					username, 
					password, 
					email, 
					fullName,
					providers.values(),
					internalReadVersion, 
					internalWriteVersion);
		}
	}
	
	/**
	 * The minimum allowed length for a user-name.
	 * 
	 * @see #validateUsername(String)
	 */
	public static final int USERNAME_LENGTH_MIN = 3;
	/**
	 * The maximum allowed length for a user-name.
	 * 
	 * @see #validateUsername(String)
	 */
	public static final int USERNAME_LENGTH_MAX = 25;
	
	/**
	 * The group ID for the Jackson filter. This must be unique to our class,
	 * whatever the value is.
	 */
	protected static final String JACKSON_FILTER_GROUP_ID =
		"org.ohmage.domain.User";
	// Register this class with the ohmage object mapper.
	static {
		OhmageObjectMapper.register(User.class);
	}
	
	/**
	 * The number of rounds for BCrypt to use when generating a salt.
	 */
	private static final int BCRYPT_SALT_ROUNDS = 12;
	
	/**
	 * The JSON key for the user-name.
	 */
	public static final String JSON_KEY_USERNAME = "username";
	/**
	 * The JSON key for the password.
	 */
	public static final String JSON_KEY_PASSWORD = "password";
	/**
	 * The JSON key for the email.
	 */
	public static final String JSON_KEY_EMAIL = "email";
	/**
	 * The JSON key for the full name.
	 */
	public static final String JSON_KEY_FULL_NAME = "full_name";
	/**
	 * The JSON key for the list of providers.
	 */
	public static final String JSON_KEY_PROVIDERS = "providers";

	/**
	 * The user's user-name.
	 */
	@JsonProperty(JSON_KEY_USERNAME)
	private final String username;
	/**
	 * The user's hashed password.
	 */
	@JsonProperty(JSON_KEY_PASSWORD)
	@JsonFilterField
	private final String password;
	/**
	 * The user's email address.
	 */
	@JsonProperty(JSON_KEY_EMAIL)
	private final String email;
	/**
	 * The user's full name.
	 */
	@JsonProperty(JSON_KEY_FULL_NAME)
	private final String fullName;
	/**
	 * The list of providers that have been linked to this account.
	 */
	@JsonProperty(JSON_KEY_PROVIDERS)
	@JsonSerialize(contentAs = List.class)
	private final Map<String, ProviderUserInformation> providers;

	/**
	 * Creates a new User object.
	 * 
	 * @param username
	 *        The user-name of the user.
	 * 
	 * @param password
	 *        The hashed password of the user.
	 * 
	 * @param email
	 *        The email address of the user.
	 * 
	 * @param fullName
	 *        The full name of the user, which may be null.
	 * 
	 * @param providers
	 *        The collection of information about providers that have
	 *        authenticated this user.
	 * 
	 * @throws InvalidArgumentException
	 *         A required parameter is null or invalid.
	 */
	public User(
		final String username,
		final String password,
		final String email,
		final String fullName,
		final List<ProviderUserInformation> providers)
		throws InvalidArgumentException {
		
		// Pass through to the builder constructor.
		this(
			username,
			password,
			email,
			fullName,
			providers,
			null);
	}

	/**
	 * Rebuilds an existing user.
	 * 
	 * @param username
	 *        The user-name of the user.
	 * 
	 * @param password
	 *        The hashed password of the user.
	 * 
	 * @param email
	 *        The email address of the user.
	 * 
	 * @param fullName
	 *        The full name of the user, which may be null.
	 * 
	 * @param providers
	 *        The collection of information about providers that have
	 *        authenticated this user.
	 * 
	 * @param internalVersion
	 *        The internal version of this entity.
	 * 
	 * @throws InvalidArgumentException
	 *         A required parameter is null or invalid.
	 */
	@JsonCreator
	protected User(
		@JsonProperty(JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_PASSWORD) final String password,
		@JsonProperty(JSON_KEY_EMAIL) final String email,
		@JsonProperty(JSON_KEY_FULL_NAME) final String fullName,
		@JsonProperty(JSON_KEY_PROVIDERS)
			final List<ProviderUserInformation> providers,
		@JsonProperty(JSON_KEY_INTERNAL_VERSION) final Long internalVersion)
		throws InvalidArgumentException {
		
		// Pass through to the builder constructor.
		this(
			username,
			password,
			email,
			fullName,
			providers,
			internalVersion,
			null);
	}

	/**
	 * Builds the User object.
	 * 
	 * @param username
	 *        The user-name of the user.
	 * 
	 * @param password
	 *        The hashed password of the user.
	 * 
	 * @param email
	 *        The email address of the user.
	 * 
	 * @param fullName
	 *        The full name of the user, which may be null.
	 * 
	 * @param providers
	 *        The collection of information about providers that have
	 *        authenticated this user.
	 * 
	 * @param internalReadVersion
	 *        The version of this entity when it was read from the database.
	 * 
	 * @param internalWriteVeresion
	 *        The version of this entity when it will be written to the
	 *        database.
	 * 
	 * @throws InvalidArgumentException
	 *         A required parameter is null or invalid.
	 */
	private User(
		final String username,
		final String password,
		final String email,
		final String fullName,
		final Collection<ProviderUserInformation> providers,
		final Long internalReadVersion,
		final Long internalWriteVersion)
		throws InvalidArgumentException {

		// Initialize the parent.
		super(internalReadVersion, internalWriteVersion);

		// Validate the parameters.
		if(username == null) {
			throw new InvalidArgumentException("The username is null.");
		}
		if(email == null) {
			throw new InvalidArgumentException("The email address is null.");
		}

		// Save the state.
		this.username = validateUsername(username);
		this.password = password;
		this.email = validateEmail(email);
		this.fullName = validateName(fullName);
		
		this.providers = new HashMap<String, ProviderUserInformation>();
		if(providers != null) {
			for(ProviderUserInformation information : providers) {
				this.providers.put(information.getProviderId(), information);
			}
		}
	}

	/**
	 * Returns the user-name of this user.
	 * 
	 * @return The user-name of this user.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the password of this user.
	 * 
	 * @return The password of this user.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the email address of this user.
	 * 
	 * @return The email address of this user.
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Returns the information from the provider for this user.
	 * 
	 * @param providerId
	 *        The provider's unique identifier.
	 * 
	 * @return The information from the provider for this user or null if no
	 *         such provider has been associated with this user.
	 */
	public ProviderUserInformation getProvider(final String providerId) {
		return providers.get(providerId);
	}
	
	/**
	 * Updates a provider's information for this user.
	 * 
	 * @param information
	 *        The information to add/update.
	 * 
	 * @return The new User object that reflects this change.
	 * 
	 * @throws IllegalArgumentException
	 *         The information is null.
	 */
	public User updateProvider(
		final ProviderUserInformation information)
		throws IllegalArgumentException {
		
		// Verify the input.
		if(information == null) {
			throw new IllegalArgumentException("The information is null.");
		}
		
		// Build a new User object with the new provider information.
		return
			(new Builder(this))
				.addProvider(information.getProviderId(), information)
				.build();
	}

	/**
	 * Verifies that a given password matches this user's password. This should
	 * only be used if the user's account actually has a password.
	 * 
	 * @param plaintextPassword
	 *        The plain-text password to check against this user's password.
	 * 
	 * @return True if the passwords match, false otherwise.
	 */
	public boolean verifyPassword(final String plaintextPassword) {
		if(password == null) {
			throw
				new IllegalStateException(
					"The user account does not have a password.");
		}
		return BCrypt.checkpw(plaintextPassword, password);
	}
	
	/**
	 * Updates this user's password by creating a new User object with all of
	 * the same fields as this object except the password, which is set as the
	 * given value.
	 * 
	 * @param password
	 *        The user's new password.
	 * 
	 * @return The new User object that represents the password change.
	 * 
	 * @throws IllegalArgumentException
	 *         The password is null.
	 */
	public User updatePassword(
		final String password)
		throws IllegalArgumentException {
		
		// Validate the input.
		if(password == null) {
			throw new IllegalArgumentException("The password is null.");
		}
		
		// Build a new User object with the new password.
		return (new Builder(this)).setPassword(password, false).build();
	}
	
	/**
	 * Validates that a user-name is a valid user-name.
	 * 
	 * @param username
	 *        The user-name to validate.
	 * 
	 * @return The trimmed and validated user-name.
	 * 
	 * @throws IllegalArgumentException
	 *         The user-name is not valid.
	 */
	public static String validateUsername(
		final String username)
		throws IllegalArgumentException {
		
		// Verify that it is not null.
		if(username == null) {
			throw new IllegalArgumentException("The username is null.");
		}
		
		// Trim it and continue validation.
		String trimmedUsername = username.trim();
		
		// Verify that the user-name is not empty. 
		if(trimmedUsername.length() == 0) {
			throw new IllegalArgumentException("The username is empty.");
		}
		
		// Verify that the user-name has at least as long as the minimum.
		if(trimmedUsername.length() < USERNAME_LENGTH_MIN) {
			throw
				new IllegalArgumentException(
					"The username is too short. It must be at least " +
						USERNAME_LENGTH_MIN +
						" characters.");
		}
		
		// Verify that the user-name has at least as long as the minimum.
		if(trimmedUsername.length() > USERNAME_LENGTH_MAX) {
			throw
				new IllegalArgumentException(
					"The username is too long. It must be less than " +
						USERNAME_LENGTH_MAX +
						" characters.");
		}
		
		// Return the trimmed, validated user-name.
		return trimmedUsername;
	}
	
	/**
	 * Hashes a user's plain-text password.
	 * 
	 * @param plaintextPassword
	 *        The plain-text password to hash.
	 * 
	 * @return The hashed password.
	 */
	public static String hashPassword(
		final String plaintextPassword)
		throws IllegalArgumentException {
		
		// Verify that it is not null.
		if(plaintextPassword == null) {
			throw
				new IllegalArgumentException(
					"The plain-text password is null.");
		}
		
		// Verify that it is not empty.
		if(plaintextPassword.length() == 0) {
			throw
				new IllegalArgumentException(
					"The plain-text password is empty.");
		}
		
		return
			BCrypt
				.hashpw(plaintextPassword, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
	}
	
	/**
	 * Validates that a user's email address is a valid email address. Note
	 * that there is no verification that the email address actually exists,
	 * only that it is a valid email address.
	 * 
	 * @param email
	 *        The email to validate.
	 * 
	 * @return The trimmed and validated email address.
	 * 
	 * @throws IllegalArgumentException
	 *         The email is not valid.
	 */
	public static String validateEmail(
		final String email)
		throws IllegalArgumentException {
		
		// Verify that it is not null.
		if(email == null) {
			throw new IllegalArgumentException("The email address is null.");
		}
		
		// Trim it and continue validation.
		String trimmedEmail = email.trim();
		
		// Verify that the email is not empty. 
		if(trimmedEmail.length() == 0) {
			throw new IllegalArgumentException("The email address is empty.");
		}
		
		// Verify that the email address is a valid email address even if the
		// email address doesn't actually exist.
		EmailValidator.getInstance().isValid(email);
		
		// Return the trimmed, validated email address.
		return trimmedEmail;
	}
	
	/**
	 * Validates that a name is valid.
	 * 
	 * @param name
	 *        The name to validate.
	 * 
	 * @return The trimmed and validated name or null if the parameter was null
	 *         only whitespace.
	 * 
	 * @throws IllegalArgumentException
	 *         The name is not valid.
	 */
	public static String validateName(
		final String name)
		throws IllegalArgumentException {
		
		// It is acceptable to be null.
		if(name == null) {
			return null;
		}
		
		// Trim it and continue validation.
		String trimmedName = name.trim();
		
		// If it is empty, that is the same as being non-existent.
		if(trimmedName.length() == 0) {
			return null;
		}
		
		// Return the trimmed, validated name.
		return trimmedName;
	}
}