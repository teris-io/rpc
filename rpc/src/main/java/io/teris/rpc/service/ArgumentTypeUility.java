/*
 * Copyright (c) teris.io & Oleg Sklyar, 2017. All rights reserved
 */

package io.teris.rpc.service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;

import io.teris.rpc.ExportName;


class ArgumentTypeUility {

	LinkedHashMap<String, Serializable> extractArguments(Method method, Object[] args) {
		LinkedHashMap<String, Serializable> payload = new LinkedHashMap<>();
		for (int i = 0; i < method.getParameterCount(); i++) {
			Parameter param = method.getParameters()[i];
			validate(method, param.getParameterizedType());
			String name = param.getName();
			if (!param.isNamePresent()) {
				ExportName exportName = param.getAnnotation(ExportName.class);
				if (exportName == null) {
					throw new IllegalArgumentException(String.format("Arguments of the service method '%s' must be annotated " +
						"with ExportName or compiler must preserve parameter names (-parameter)", method.getName()));
				}
				name = exportName.value();
			}
			if (name == null || "".equals(name.trim())) {
				throw new IllegalArgumentException("Service method argument names must not be empty");
			}
			payload.put(name, (Serializable) args[i]);
		}
		return payload;
	}

	private void validate(Method method, Type type) throws IllegalArgumentException {
		if (type instanceof WildcardType) {
			throw new IllegalArgumentException(String.format("Argument types of the service method '%s' must contain no " +
				"wildcards", method.getName()));
		}
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (!Serializable.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format("Argument types of the service method '%s' must implement " +
					"Serializable or be void", method.getName()));
			}
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parametrizedType = (ParameterizedType) type;
			validate(method, parametrizedType.getRawType());
			for (Type subtype: parametrizedType.getActualTypeArguments()) {
				validate(method, subtype);
			}
		}
	}
}
