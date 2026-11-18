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

import java.time.LocalDate;
import java.util.Set;

public class PetOwner {

	private final int ownerId;

	private final Set<Integer> petIds;

	public PetOwner(int ownerId, Set<Integer> petIds) {
		this.ownerId = ownerId;
		this.petIds = Set.copyOf(petIds);
	}

	public NewVisit bookVisit(int petId, LocalDate date, String description, LocalDate today) {
		if (date == null || !date.isAfter(today)) {
			throw new InvalidVisitDateException(date);
		}
		if (!petIds.contains(petId)) {
			throw new IllegalArgumentException("Pet with id " + petId + " not found for owner with id " + ownerId);
		}
		return new NewVisit(ownerId, petId, date, description);
	}

}
