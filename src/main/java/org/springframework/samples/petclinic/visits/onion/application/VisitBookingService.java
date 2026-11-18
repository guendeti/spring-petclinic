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
package org.springframework.samples.petclinic.visits.onion.application;

import java.time.LocalDate;

import org.springframework.samples.petclinic.visits.onion.domain.NewVisit;
import org.springframework.samples.petclinic.visits.onion.domain.PetOwner;
import org.springframework.samples.petclinic.visits.onion.domain.PetOwnerRepository;

public class VisitBookingService {

	private final PetOwnerRepository owners;

	public VisitBookingService(PetOwnerRepository owners) {
		this.owners = owners;
	}

	public void bookVisit(int ownerId, int petId, LocalDate date, String description) {
		PetOwner owner = owners.findOwner(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));
		NewVisit visit = owner.bookVisit(petId, date, description, LocalDate.now());
		owners.add(visit);
	}

}
