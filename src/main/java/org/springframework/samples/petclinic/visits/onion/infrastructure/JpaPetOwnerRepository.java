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
package org.springframework.samples.petclinic.visits.onion.infrastructure;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.visits.onion.domain.NewVisit;
import org.springframework.samples.petclinic.visits.onion.domain.PetOwner;
import org.springframework.samples.petclinic.visits.onion.domain.PetOwnerRepository;

public class JpaPetOwnerRepository implements PetOwnerRepository {

	private final OwnerRepository owners;

	public JpaPetOwnerRepository(OwnerRepository owners) {
		this.owners = owners;
	}

	@Override
	public Optional<PetOwner> findOwner(int ownerId) {
		return owners.findById(ownerId)
			.map(owner -> new PetOwner(ownerId, owner.getPets().stream().map(Pet::getId).collect(Collectors.toSet())));
	}

	@Override
	public void add(NewVisit newVisit) {
		Owner owner = owners.findById(newVisit.ownerId()).orElseThrow();
		Visit visit = new Visit();
		visit.setDate(newVisit.date());
		visit.setDescription(newVisit.description());
		owner.addVisit(newVisit.petId(), visit);
		owners.save(owner);
	}

}
