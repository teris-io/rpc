/**
 * Copyright (c) Profidata AG 2017
 */
package io.teris.rpc;

import java.util.Map;
import javax.annotation.Nonnull;


public interface ServiceDispatcher extends ServiceInvoker {

	@Nonnull
	static Builder builder() {
		return new ServiceDispatcherImpl.BuilderImpl();
	}

	interface Builder {

		/**
		 * Binds a serializer used to serialize service method arguments for the remote caller.
		 */
		@Nonnull
		Builder serializer(@Nonnull Serializer serializer);

		/**
		 * Binds a content type specific deserializer used to deserialize data received in
		 * response from the server based on the content type of the response.
		 */
		@Nonnull
		Builder deserializer(@Nonnull String contentType, @Nonnull Deserializer deserializer);

		/**
		 * Binds a collection of deserializers used to deserialize data received in response
		 * from the server based on the content type of the response.
		 */
		@Nonnull
		Builder deserializers(@Nonnull Map<String, Deserializer> deserializerMap);

		@Nonnull
		<S> Builder bind(@Nonnull Class<S> serviceClass, @Nonnull S service) throws ServiceException;

		@Nonnull
		ServiceDispatcher build();
	}
}
