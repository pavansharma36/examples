package org.one.spring.core.security.jwt.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

public class JwtSecurityContextRepository implements SecurityContextRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecurityContextRepository.class);
	private final UserService userService;

	public JwtSecurityContextRepository(final UserService userService) {
		this.userService = userService;
	}

	@Override
	public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
		final HttpServletRequest request = requestResponseHolder.getRequest();

		final String authToken = request.getHeader(JwtService.X_AUTH_TOKEN);

		try {
			if (authToken != null) {
				final String username = new JwtService().parseToken(authToken);
				final UserDetails user = userService.loadUserByUsername(username);

				final Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(),
						user.getPassword(), user.getAuthorities());
				return new SecurityContextImpl(authentication);
			}
		} catch (final TokenExpiredException e) {
			LOGGER.debug("Token expired : {}", e.getMessage());
		} catch (final Exception e) {
			LOGGER.warn("Exception while parsing auth token : {}", e.getMessage(), e);
		}
		return SecurityContextHolder.createEmptyContext();
	}

	@Override
	public void saveContext(final SecurityContext context, final HttpServletRequest request,
			final HttpServletResponse response) {
		// not required
	}

	@Override
	public boolean containsContext(final HttpServletRequest request) {
		return request.getHeader(JwtService.X_AUTH_TOKEN) != null;
	}

}
