package org.one.spring.core.security.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	public static final String X_AUTH_TOKEN = "x-auth-token";

	private final String secret = "kajsnd83dakjnsd";

	private final Algorithm algorithm = Algorithm.HMAC256(secret);

	/**
	 * Tries to parse specified String as a JWT token. If successful, returns User
	 * object with username, id and role prefilled (extracted from token). If
	 * unsuccessful (token is invalid or not containing all required user
	 * properties), simply returns null.
	 *
	 * @param token the JWT token to parse
	 * @return the User object extracted from specified token or null if a token is
	 *         invalid.
	 */
	public String parseToken(final String token) {
		final JWTVerifier verifier = JWT.require(algorithm).withIssuer("pavan").build(); // Reusable verifier instance
		final DecodedJWT jwt = verifier.verify(token);
		return jwt.getSubject();
	}

	/**
	 * Generates a JWT token containing username as subject, and userId and role as
	 * additional claims. These properties are taken from the specified User object.
	 * Tokens validity is infinite.
	 *
	 * @param u the user for which the token will be generated
	 * @return the JWT token
	 */
	public String generateToken(final String username) {
		return JWT.create().withSubject(username).withIssuer("pavan")
				.withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))).sign(algorithm);
	}

}
