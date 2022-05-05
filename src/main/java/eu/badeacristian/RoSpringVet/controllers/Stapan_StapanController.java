package eu.badeacristian.RoSpringVet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.models.VizitaWrapper;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.DiagnosticService;
import eu.badeacristian.RoSpringVet.services.ProgramareService;
import eu.badeacristian.RoSpringVet.services.StapanService;
import eu.badeacristian.RoSpringVet.services.TratamentService;
import eu.badeacristian.RoSpringVet.services.VizitaService;

@Controller
public class Stapan_StapanController {
	
	@Autowired
	StapanService stapanService;
	
	@Autowired
	private AngajatService angajatService;
	
	@Autowired
	private TratamentService tratamentService;
	
	@Autowired
	private ProgramareService programareService;

	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private VizitaService vizitaService;
	
	@Autowired
	private DiagnosticService diagnosticService;
	
	
	private Stapan getStapan() {
		String username = "wut";
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}		
		Stapan stapan = stapanService.getStapanByEmail(username);
		return stapan;
	}
	
	
	@GetMapping("/veziAnimaleleTale")
	private String actiuniAnimalStapani(Model model) {
		return findPaginatedAnimaleStapan(1, "nume", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziAnimaleleTale/page/{pageNo}")
	public String findPaginatedAnimaleStapan(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {

		Stapan stapan = getStapan();
		
		long id_stapan = stapan.getStapanId();
		int pageSize = 5;
		Page<Animal> page = animalService.findPaginatedAnimalStapan(id_stapan, pageNo, pageSize, sortField, sortDir);
		List<Animal> listaAnimale = page.getContent();
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaAnimale", listaAnimale);

		// adaugam numele stapanului in model
		String nume_stapan = stapanService.getStapan(id_stapan).get().getFirstname() + " " + stapanService.getStapan(id_stapan).get().getLastname();
		model.addAttribute("nume_stapan", nume_stapan);
		
		//id stapan
		model.addAttribute("id_stapan", id_stapan);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
				
		//nume view - pt redirectionare fie la veziAnimale fie la veziAnimaleStapan (in cazu asta e veziAnimale)
		model.addAttribute("numeView", "veziAnimaleleTale");
		
		//Atribute pentru adaugarea unei programari
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		//Atribute pentru adaugarea unui animal nou
		Animal animal = new Animal();
		model.addAttribute("animal", animal);
		
		return "templates-stapan/vezi-animale.html";

	}
	
	@GetMapping("/euProfilAnimal/{id}")
	public String veziAnimal(@PathVariable(value = "id") long id, Model model) {
		
		// luam animalul cu id-ul
		Animal animal = animalService.getAnimal(id).get();

		//luam stapanul CONECTAT
		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
		
		//acum DACA id-ul stapanului animalului nu coincide cu al stapanului conectat 
		//atunci nu o sa mearga, redirectionare catre /veziAnimaleleTale
		if(animal.getStapanId().getStapanId() != stapan.getStapanId())
			return "redirect:/veziAnimaleleTale";
		
		
		// pre-populam pagina cu informatiile animalului
		model.addAttribute("animal", animal);
		// pre-populam pagina cu informatiile stapanului
		model.addAttribute("stapan", stapan);
		
		//numarul tratamentelor active
		Page<Tratament> page = tratamentService.findPaginatedAnimal(animal.getAnimalId(), 1, 5, "datainceput", "asc");
		long numarTratamente = page.getTotalElements();
		model.addAttribute("numarTratamente", numarTratamente);
		
		//numarul programarilor confirmate
		Page<Programare> page_prog = programareService.findPaginatedAnimal(id, 1, 5, "date", "asc");
		long numarProgConfirmate = page_prog.getTotalElements();
		model.addAttribute("numarProgConfirmate", numarProgConfirmate);
		
		
		return "templates-stapan/eu-profil-animal";
	}
	
	@GetMapping("/euProfilStapan")
	public String veziStapan(Model model) {
		// apare o noua pagina cu profilu stapanului
	
		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
		long id = stapan.getStapanId();
		
		//numarul programarilor neconfirmate
		Page<Programare> page = programareService.findPaginatedStapanNeconfirmata(id, 1, 5, "date", "asc");
		long programari_neconfirmate = page.getTotalElements();
		model.addAttribute("programari_neconfirmate", programari_neconfirmate);
		
		//numarul programarilor confirmate
		Page<Programare> page_conf = programareService.findPaginatedStapan(id, 1, 5, "date", "asc");
		long programari_confirmate = page_conf.getTotalElements();
		model.addAttribute("programari_confirmate", programari_confirmate);

		// pre-populam pagina cu informatiile stapanului
		model.addAttribute("stapan", stapan);
		return "Acasa-Stapani";
	}
	
	
	
	@RequestMapping("/euVizite")
	public String getViziteStapan(Model model){
		return findPaginatedViziteStapan(1, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/euVizite/page/{pageNo}")
	public String findPaginatedViziteStapan(@PathVariable(value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
		long id = stapan.getStapanId();
		
		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginatedStapan(id, pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		model.addAttribute("stapan", stapan);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "templates-stapan/eu-vizite";

	}
	
	
	@RequestMapping("/euProgramari")
	public String getProgramariStapan(Model model){
		return findPaginatedProgramariStapan(1, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/euProgramari/page/{pageNo}")
	public String findPaginatedProgramariStapan(
			@PathVariable(value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			Model model) {

		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
		long id = stapan.getStapanId();
		
		int pageSize = 5;
		Page<Programare> page = programareService.findPaginatedStapan(id, pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();

		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			//daca pagina e 0 adica nu exista inregistrari atunci mergem inapoi la profilu stapanului
			if(pageNo==0)
				return "redirect:/euProfilStapan?0prog";
			page = programareService.findPaginatedStapan(id, pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent();
		}
		
		model.addAttribute("stapan", stapan);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaProgramari", listaProgramari);
		
		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//nume view pt redirectionare
		model.addAttribute("numeView", "euProgramari");
		
		//data pentru cautare 
		//model.addAttribute("data", data);
		
		return "templates-stapan/eu-programari";

	}
	

	@RequestMapping("/euProgramariNeconfirmate")
	public String getProgramariStapanNeconfirmate(
			Model model){
		return findPaginatedProgramariStapanNeconfirmate(1, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/euProgramariNeconfirmate/page/{pageNo}")
	public String findPaginatedProgramariStapanNeconfirmate(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		
		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
		long id = stapan.getStapanId();

		int pageSize = 5;
		Page<Programare> page = programareService.findPaginatedStapanNeconfirmata(id, pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();

		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			if(pageNo == 0)
				return "redirect:/euProfilStapan?0progneconf";
			page = programareService.findPaginatedStapanNeconfirmata(id, pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent();
		}
		
		model.addAttribute("stapan", stapan);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaProgramari", listaProgramari);
		
		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//nume view pt redirectionare
		model.addAttribute("numeView", "euProgramariNeconfirmate");
		
		return "templates-stapan/eu-programari-neconfirmate";

	}
	
	
	
	@RequestMapping("/euViziteAnimal/{id}")
	public String getViziteAnimal(Model model, @PathVariable(value = "id") long id){
		return findPaginatedViziteAnimal(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/euViziteAnimal/{id}/page/{pageNo}")
	public String findPaginatedViziteAnimal(
			@PathVariable(value = "pageNo") int pageNo, 
			@PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			Model model) {

		
		//animalul cu id-ul...din url
		Animal animal = animalService.getAnimal(id).get();
		
		//luam stapanul CONECTAT
		//luam stapanu utilizand spring security. dupa ii luam id-ul
		Stapan stapan = getStapan();
				
		//acum DACA id-ul stapanului animalului nu coincide cu al stapanului conectat 
		//atunci nu o sa mearga, redirectionare catre /veziAnimaleleTale
		if(animal.getStapanId().getStapanId() != stapan.getStapanId())
			return "redirect:/veziAnimaleleTale";
		
		
		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginatedAnimal(id, pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		model.addAttribute("animal", animal);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		// Atribute pentru adaugarea unei vizite noi cu modalul
		// Vizita
		Vizita vizita = new Vizita();
		// Diagnostic
		Diagnostic diagnostic = new Diagnostic();
		model.addAttribute("diagnostic", diagnostic);
		long diagnostic_id = diagnostic.getDiagnosticId();
		model.addAttribute("id_diagnostic", diagnostic_id);
		// Tratament
		Tratament tratament = new Tratament();
		model.addAttribute("tratament", tratament);
		long tratament_id = tratament.getTratamentId();
		model.addAttribute("id_tratament", tratament_id);
		// Angajat
		Angajat angajat = new Angajat();
		List<Angajat> lista_angajati = angajatService.getAllAngajati();
		model.addAttribute("lista_angajati", lista_angajati);
		model.addAttribute("angajat", angajat);
		VizitaWrapper vizitawrapper = new VizitaWrapper();
		// impachetam toate obiectele pe care vrem sa le salvam in obiectul
		// vizitawrapper, care e trimis de formular, acolo luam obiectele cu get() si le salvam
		vizitawrapper.setVizita(vizita);
		vizitawrapper.setDiagnostic(diagnostic);
		vizitawrapper.setTratament(tratament);
		model.addAttribute("vizitawrapper", vizitawrapper);		
		
		//Atribute pt redirectionare
		//Nume view
		model.addAttribute("numeView", "veziViziteAnimal");
		
		return "templates-stapan/eu-animal-vizite";

	}
	
	
	
	@RequestMapping("/euProgramariAnimal/{id}")
	public String getProgramariAnimal(Model model, @PathVariable(value = "id") long id){
		return findPaginatedProgramariAnimal(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/euProgramariAnimal/{id}/page/{pageNo}")
	public String findPaginatedProgramariAnimal(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		Page<Programare> page = programareService.findPaginatedAnimal(id, pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();
		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			//daca pagina e 0 adica nu exista inregistrari atunci mergem inapoi la profilu animalului
			if(pageNo==0)
				return "redirect:/euProfilAnimal/"+id+"?0prog";
			page = programareService.findPaginatedAnimal(id, pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent();
		}

		Animal animal = animalService.getAnimal(id).get();
		model.addAttribute("animal", animal);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaProgramari", listaProgramari);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//atribute editare si adaugare programare
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		//nume view pt redirectionare
		model.addAttribute("numeView", "euProgramariAnimal");
		
		
		return "templates-stapan/eu-programari-animal";

	}
	
	
	
	//Toate tratamente active pentru animalul cu ID-ul...
		@RequestMapping("/euTratamenteActiveAnimal/{id}")
		public String getTratamenteAnimal(Model model, @PathVariable(value = "id") long id){
			return findPaginatedTratamenteAnimal(1, id, "datainceput", "asc", model); // returneaza prima pagina
		}
		
		@GetMapping("/euTratamenteActiveAnimal/{id}/page/{pageNo}")
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
			
			return "templates-stapan/eu-tratamente-active-animal";

		}
	
	
	
	
	

	
}
