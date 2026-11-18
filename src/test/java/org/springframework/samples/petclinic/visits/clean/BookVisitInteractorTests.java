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
package org.springframework.samples.petclinic.visits.clean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.visits.clean.core.BookVisitInteractor;
import org.springframework.samples.petclinic.visits.clean.core.BookVisitUseCase;
import org.springframework.samples.petclinic.visits.clean.core.InvalidVisitDateException;

/**
 * Uses an in-memory store so the booking rules can be tested without a database.
 */
class BookVisitInteractorTests {

	private InMemoryVisitStore store;

	private BookVisitUseCase bookVisit;

	@BeforeEach
	void setUp() {
		store = new InMemoryVisitStore();
		store.addPet(1, 7);
		bookVisit = new BookVisitInteractor(store);
	}

	@Test
	void booksVisitForTomorrow() {
		bookVisit.bookVisit(1, 7, LocalDate.now().plusDays(1), "Vaccination");
		assertThat(store.visits).hasSize(1);
	}

	@Test
	void rejectsVisitForToday() {
		assertThatThrownBy(() -> bookVisit.bookVisit(1, 7, LocalDate.now(), "Vaccination"))
			.isInstanceOf(InvalidVisitDateException.class);
		assertThat(store.visits).isEmpty();
	}

	@Test
	void rejectsPetOfAnotherOwner() {
		assertThatThrownBy(() -> bookVisit.bookVisit(2, 7, LocalDate.now().plusDays(1), "Vaccination"))
			.isInstanceOf(IllegalArgumentException.class);
		assertThat(store.visits).isEmpty();
	}

}
