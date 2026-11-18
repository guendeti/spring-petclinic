/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visits;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VisitVariantControllersTests {

	@Autowired
	private MockMvc mockMvc;

	private static String formUrl(String variant) {
		return "/" + variant + "/owners/1/pets/1/visits/new";
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void showsVisitForm(String variant) throws Exception {
		mockMvc.perform(get(formUrl(variant)))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void booksVisitAndRedirectsToOwner(String variant) throws Exception {
		mockMvc
			.perform(post(formUrl(variant)).param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Check-up"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void showsDateErrorForToday(String variant) throws Exception {
		mockMvc
			.perform(post(formUrl(variant)).param("date", LocalDate.now().toString()).param("description", "Check-up"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void showsDateErrorWhenFieldIsOmitted(String variant) throws Exception {
		mockMvc.perform(post(formUrl(variant)).param("description", "Check-up"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void showsDescriptionErrorWhenBlank(String variant) throws Exception {
		mockMvc
			.perform(post(formUrl(variant)).param("date", LocalDate.now().plusDays(1).toString())
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "description"));
	}

}
