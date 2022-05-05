package eu.badeacristian.RoSpringVet.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.UserService;

@Controller
public class AngajatController {

	@Autowired
	private UserService userService;

	@Autowired
	private AngajatService angajatService;

	@GetMapping("/angajati")
	private String actiuniAngajati(Model model) {
		return findPaginatedAngajat(1, "firstname", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziAngajati/page/{pageNo}")
	public String findPaginatedAngajat(@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		
		int pageSize = 5;
		Page<Angajat> page = angajatService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Angajat> listaAngajati = page.getContent();

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaAngajati", listaAngajati);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		UserRegistrationDTO registrationDto = new UserRegistrationDTO();
		model.addAttribute("user", registrationDto);
		
		return "angajati";

	}

	@PostMapping("/salveazaAsistent")
	public String saveAsistent(@ModelAttribute("user") @Valid UserRegistrationDTO registrationDto) {
		// salvare asistent in db
		userService.saveUser(registrationDto, "ROL_ANGAJAT");
		userService.saveAngajat(registrationDto, "asistent", null);
		// redirect
		return "redirect:/angajati";
	}

	@PostMapping("/salveazaAngajat")
	public String saveAngajat(@ModelAttribute("user") @Valid UserRegistrationDTO registrationDto) {
		// salvare veterinar in db
		userService.saveUser(registrationDto, "ROL_ANGAJAT");
		userService.saveAngajat(registrationDto, "asistent", null);
		// redirect
		return "redirect:/angajati";
	}

	@GetMapping("/deleteAngajat/{id}")
	public String deleteAngajat(@PathVariable(value = "id") long id) {
		// delete angajat
		angajatService.deleteAngajat(id);
		return "redirect:/angajati";
	}
	
	
	
	
}
