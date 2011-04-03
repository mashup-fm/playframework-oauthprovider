/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.Model;
import play.mvc.Controller;

/**
 * The Class Debug.
 */
public class Debug extends Controller {

	/**
	 * Messages.
	 */
	public static void messages() {
		ApplicationClasses appClasses = Play.classes;
		Set<String> list = new HashSet<String>();
		List<ApplicationClass> classes = appClasses
				.getAssignableClasses(Model.class);
		for (ApplicationClass clazz : classes) {
			// list.add("# Class: " + clazz.name);
			List<Field> fields = getAllFields(clazz.javaClass);
			if (fields != null) {
				for (Field f : fields) {
					list.add(f.getName() + "=" + getFieldLabel(f.getName()));
				}
			}
			list.add("");
			list.add("");
		}
		response.contentType = "plain/text";
		String content = "";
		for (String s : list) {
			content = content + s + "\n";
		}
		render(content);
	}

	/**
	 * Gets the field label.
	 *
	 * @param name the name
	 * @return the field label
	 */
	private static String getFieldLabel(String name) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (char c : name.toCharArray()) {
			count++;
			if (count == 1) {
				sb.append(String.valueOf(c).toUpperCase());

			} else {
				if (Character.isUpperCase(c)) {
					sb.append(" ");
				}
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Gets the all fields.
	 *
	 * @param originalClass the original class
	 * @return the all fields
	 */
	private static List<Field> getAllFields(final Class<?> originalClass) {
		Class<?> clazz = originalClass;

		// Init Counter
		int count = 0;

		// Init List
		final List<Field> fields = new ArrayList<Field>();

		// Get all the fields including superclasses
		while (clazz != null) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
			count++;
		}

		// Check Count
		if (count > 10) {
			Logger.warn("Too many iterations on ReflectionUtil.getAllFields() - class: "
					+ originalClass);
		}

		// make the list unmodifiable
		List<Field> unmodifiableFields = Collections
				.<Field> unmodifiableList(fields);

		// Return List
		return unmodifiableFields;
	}

}
