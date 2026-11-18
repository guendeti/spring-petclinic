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
package org.springframework.samples.petclinic.visits.layered;

import java.time.LocalDate;

import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.visits.VisitFormModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/layered/owners/{ownerId}/pets/{petId}/visits/new")
class LayeredVisitController {

	private final VisitService visitService;

	private final OwnerRepository owners;

	LayeredVisitController(VisitService visitService, OwnerRepository owners) {
		this.visitService = visitService;
		this.owners = owners;
	}

	@InitBinder
	void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setAllowedFields("date", "description");
	}

	@ModelAttribute("visit")
	Visit newVisit() {
		Visit visit = new Visit();
		visit.setDate(null);
		return visit;
	}

	@GetMapping
	String initNewVisitForm(@PathVariable int ownerId, @PathVariable int petId, @ModelAttribute("visit") Visit visit,
			Model model) {
		visit.setDate(LocalDate.now().plusDays(1));
		VisitFormModel.addOwnerAndPet(owners, ownerId, petId, model);
		return VisitFormModel.VIEW;
	}

	@PostMapping
	String processNewVisitForm(@PathVariable int ownerId, @PathVariable int petId,
			@Valid @ModelAttribute("visit") Visit visit, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		if (!result.hasErrors()) {
			try {
				visitService.bookVisit(ownerId, petId, visit);
				redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
				return "redirect:/owners/{ownerId}";
			}
			catch (InvalidVisitDateException ex) {
				result.rejectValue("date", "typeMismatch.visitDate");
			}
		}
		VisitFormModel.addOwnerAndPet(owners, ownerId, petId, model);
		return VisitFormModel.VIEW;
	}

}
