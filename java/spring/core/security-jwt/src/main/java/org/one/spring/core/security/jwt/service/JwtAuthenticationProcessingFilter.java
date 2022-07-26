package org.one.spring.core.security.jwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.one.spring.core.security.jwt.bean.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JwtAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected JwtAuthenticationProcessingFilter(final AuthenticationManager authenticationManager,
			final AuthenticationSuccessHandler authenticationSuccessHandler) {
		super(new AntPathRequestMatcher("/login", "POST"));
		setAuthenticationManager(authenticationManager);
		setAuthenticationSuccessHandler(authenticationSuccessHandler);
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		final User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword());

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return getAuthenticationManager().authenticate(authRequest);
	}

	/**
	 * Provided so that subclasses may configure what is put into the authentication
	 * request's details property.
	 *
	 * @param request     that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its
	 *                    details set
	 */
	protected void setDetails(final HttpServletRequest request, final UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

}
