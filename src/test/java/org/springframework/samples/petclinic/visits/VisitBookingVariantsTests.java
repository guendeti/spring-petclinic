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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.visits.clean.core.BookVisitUseCase;
import org.springframework.samples.petclinic.visits.layered.VisitService;
import org.springframework.samples.petclinic.visits.onion.application.VisitBookingService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The same cases run against all three variants. In the sample data, Leo is pet 1 and
 * belongs to owner 1. Pet 2 belongs to owner 2.
 */
@SpringBootTest
@Transactional
class VisitBookingVariantsTests {

	@FunctionalInterface
	interface Booking {

		void book(int ownerId, int petId, LocalDate date, String description);

	}

	@Autowired
	private VisitService layered;

	@Autowired
	private BookVisitUseCase clean;

	@Autowired
	private VisitBookingService onion;

	@Autowired
	private OwnerRepository owners;

	private Booking variant(String name) {
		return switch (name) {
			case "layered" -> (ownerId, petId, date, description) -> {
				Visit visit = new Visit();
				visit.setDate(date);
				visit.setDescription(description);
				layered.bookVisit(ownerId, petId, visit);
			};
			case "clean" -> clean::bookVisit;
			case "onion" -> onion::bookVisit;
			default -> throw new IllegalArgumentException(name);
		};
	}

	private int visitsOfLeo() {
		return owners.findById(1).orElseThrow().getPet(1).getVisits().size();
	}

	private long allVisits() {
		return owners.findAll()
			.stream()
			.flatMap(owner -> owner.getPets().stream())
			.mapToLong(pet -> pet.getVisits().size())
			.sum();
	}

	private void assertRejected(String name, int ownerId, int petId, LocalDate date) {
		long before = allVisits();
		assertThatThrownBy(() -> variant(name).book(ownerId, petId, date, "Check-up"))
			.isInstanceOf(RuntimeException.class);
		assertThat(allVisits()).isEqualTo(before);
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void booksVisitForTomorrow(String name) {
		int before = visitsOfLeo();
		variant(name).book(1, 1, LocalDate.now().plusDays(1), "Check-up");
		assertThat(visitsOfLeo()).isEqualTo(before + 1);
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void rejectsVisitForToday(String name) {
		assertRejected(name, 1, 1, LocalDate.now());
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void rejectsVisitInThePast(String name) {
		assertRejected(name, 1, 1, LocalDate.now().minusDays(3));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void rejectsVisitWithoutDate(String name) {
		assertRejected(name, 1, 1, null);
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void rejectsPetOfAnotherOwner(String name) {
		assertRejected(name, 1, 2, LocalDate.now().plusDays(1));
	}

	@ParameterizedTest
	@ValueSource(strings = { "layered", "clean", "onion" })
	void rejectsUnknownOwner(String name) {
		assertRejected(name, 999, 1, LocalDate.now().plusDays(1));
	}

}
