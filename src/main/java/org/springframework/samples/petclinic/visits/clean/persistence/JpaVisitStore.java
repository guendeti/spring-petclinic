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
package org.springframework.samples.petclinic.visits.clean.persistence;

import java.time.LocalDate;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.visits.clean.core.VisitStore;

public class JpaVisitStore implements VisitStore {

	private final OwnerRepository owners;

	public JpaVisitStore(OwnerRepository owners) {
		this.owners = owners;
	}

	@Override
	public boolean petBelongsToOwner(int ownerId, int petId) {
		return owners.findById(ownerId).map(owner -> owner.getPet(petId) != null).orElse(false);
	}

	@Override
	public void addVisit(int ownerId, int petId, LocalDate date, String description) {
		Owner owner = owners.findById(ownerId).orElseThrow();
		Visit visit = new Visit();
		visit.setDate(date);
		visit.setDescription(description);
		owner.addVisit(petId, visit);
		owners.save(owner);
	}

}
