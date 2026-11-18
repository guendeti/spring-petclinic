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
package org.springframework.samples.petclinic.visits.onion.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PetOwnerTests {

	private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);

	private final PetOwner owner = new PetOwner(1, Set.of(7));

	@Test
	void acceptsVisitForTomorrow() {
		NewVisit visit = owner.bookVisit(7, TODAY.plusDays(1), "Vaccination", TODAY);
		assertThat(visit).isEqualTo(new NewVisit(1, 7, TODAY.plusDays(1), "Vaccination"));
	}

	@Test
	void rejectsVisitForToday() {
		assertThatThrownBy(() -> owner.bookVisit(7, TODAY, "Vaccination", TODAY))
			.isInstanceOf(InvalidVisitDateException.class);
	}

	@Test
	void rejectsPetOfAnotherOwner() {
		assertThatThrownBy(() -> owner.bookVisit(8, TODAY.plusDays(1), "Vaccination", TODAY))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void rejectsVisitWithoutDate() {
		assertThatThrownBy(() -> owner.bookVisit(7, null, "Vaccination", TODAY))
			.isInstanceOf(InvalidVisitDateException.class);
	}

}
