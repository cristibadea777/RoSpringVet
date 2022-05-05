package eu.badeacristian.RoSpringVet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.TratamentService;

@Controller
public class TratamentController {

	@Autowired
	private TratamentService tratamentService;
	
	@Autowired
	private AnimalService animalService;
	
	@GetMapping("/tratamente")
	private String actiuniTratamente(Model model) {
		
		return findPaginatedStapan(1, "datasfarsit", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziTratamente/page/{pageNo}")
	public String findPaginatedStapan(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir,
			Model model) {
		
		int pageSize = 5;
		
		
		
		Page<Tratament> page = tratamentService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Tratament> listaTratamente = page.getContent();
		

		// daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		// atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		// altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if (listaTratamente.size() == 0) {
			pageNo = page.getTotalPages(); // ultima pagina
			page = tratamentService.findPaginated(pageNo, pageSize, sortField, sortDir);				
			listaTratamente = page.getContent();
		}
			
		
		

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaTratamente", listaTratamente);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		
		return "tratamente";

	}

	
	
	//Toate tratamente active pentru animalul cu ID-ul...
	@RequestMapping("/tratamenteActive/{id}")
	public String getTratamenteAnimal(Model model, @PathVariable(value = "id") long id){
		return findPaginatedTratamenteAnimal(1, id, "datainceput", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziTratamenteAnimal/{id}/page/{pageNo}")
	public String findPaginatedTratamenteAnimal(
			@PathVariable(value = "pageNo") int pageNo, 
			@PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		
		Animal animal = animalService.getAnimal(id).get();
		model.addAttribute("animal", animal);
		
		Page<Tratament> page = tratamentService.findPaginatedAnimal(animal.getAnimalId(), pageNo, pageSize, sortField, sortDir);
		List<Tratament> listaTratamente = page.getContent();
		

		// daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		// atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		// altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if (listaTratamente.size() == 0) {
			pageNo = page.getTotalPages(); // ultima pagina
			//daca pagina e 0 adica nu exista inregistrari atunci mergem inapoi la profilu animalului
			if(pageNo==0)
				return "redirect:/profilAnimal/"+id;
			page = tratamentService.findPaginatedAnimal(animal.getAnimalId(), pageNo, pageSize, sortField, sortDir);				
			listaTratamente = page.getContent();
		}
			
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaTratamente", listaTratamente);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		return "animal-tratamente-active";

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
