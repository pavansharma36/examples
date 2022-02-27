package org.one.spring.core.feign.client;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import feign.spring.SpringContract;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientBuilder {

	/**
	 * builder which will build client using jackson as encoder decoder library.
	 * uses spring contract for client implementation.
	 *
	 * @param <T>
	 * @param clazz
	 * @param host
	 * @return
	 */
	public static <T> T buildClient(final Class<T> clazz, final String host) {
		return Feign.builder().logger(new Slf4jLogger(clazz)).contract(new SpringContract())
				.encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).target(clazz, host);
	}

}
