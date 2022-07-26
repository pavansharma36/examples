package org.one.spring.core.security.jwt.service;

import java.util.Collections;
import org.one.spring.core.security.jwt.bean.User;
import org.one.spring.core.security.jwt.bean.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if("pavan".equals(username)) {
			User user = new User();
			user.setUsername("pavan");
			user.setPassword("password");
			user.setAuthorities(Collections.singleton(UserRole.USER));
			return user;
		}
		throw new UsernameNotFoundException(username);
	}

}
