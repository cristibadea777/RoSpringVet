package eu.badeacristian.RoSpringVet.controllers;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Data;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.User;
import eu.badeacristian.RoSpringVet.repositories.UserRepository;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.EmailService;
import eu.badeacristian.RoSpringVet.services.ProgramareService;
import eu.badeacristian.RoSpringVet.services.StapanService;

@Controller
public class ProgramareController {

	@Autowired
	private ProgramareService programareService;
	
	@Autowired
	private StapanService stapanService;
	
	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;

	
	@PostMapping("/adaugaProgramare")
	public String adaugaProgramare(
			@ModelAttribute("programare") Programare programare,
			@RequestParam("currentPage") int currentPage,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			@ModelAttribute("numeView") String numeView,
			Model model) {
		
		long idModal = programare.getAnimalId().getAnimalId();
		
		//nume view = veziAnimaleStapan/idStapan
		if(numeView.equals("veziAnimaleStapan"))
			numeView = numeView + "/" + programare.getStapanId().getStapanId(); 
		
		if(numeView.equals("veziProgramariAnimal"))
			numeView = numeView + "/" + programare.getAnimalId().getAnimalId();

		if(programare.getMotiv().isEmpty()) {
			if(numeView.equals("euProgramariAnimal"))
				return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&erradd&errmotiv";
			else
				return "redirect:/" + numeView +  "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errprog&idModal=" + idModal + "&errmotiv";
		}
		
		if(programare.getDate() == null) {
			if(numeView.equals("euProgramariAnimal"))
				return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&erradd&errdataprog";
			else
				return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errprog&idModal=" + idModal + "&errdataprog";
		}
		
		
		// salvare programare in db
		
		//Daca o adauga un angajat, atunci e CONFIRMATA 
		//Daca o adauga un stapan, atunci e NECONFIRMATA
		String username = "wut";
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}		
		
		Stapan stapan = stapanService.getStapanByEmail(username);
		
		if(stapan == null)  
			programare.setStare("confirmata");//daca mail-ul nu se gaseste in repozitoriul stapani atunci e clar ca angajatul face programarea, atunci o setam ca fiind confirmata
		else 
			programare.setStare("neconfirmata");

		
		programareService.addProgramare(programare);
				
		
		//email catre stapan programare schimbata (programare confirmata)
		User user = userRepository.findByEmail(programare.getStapanId().getEmail());
		String nume = programare.getAnimalId().getNume();
		
		//email Confirmata daca o face angajatul
		//email Neconfirmata daca o face stapanul
		//if(programare.getStare().equals("confirmata"))
			//emailService.sendEmailProgramareConfirmata(user, programare.getDate(), nume);
		//else
		//	emailService.sendEmailProgramareNeconfirmata(user, programare.getDate(), nume);
		
		
		
		
		
		// redirect
		
		if(numeView.equals("euProgramariAnimal")) {
			return "redirect:/" + numeView +  "/" + programare.getAnimalId().getAnimalId() +"/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir; 
		}
		
		return "redirect:/" + numeView + "/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
	}
	
	@PostMapping("/editareProgramare")
	public String editeazaProgramare(
			@ModelAttribute("programare") Programare programare,
			@RequestParam("currentPage") int currentPage,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			@RequestParam("data") String data,
			@ModelAttribute("numeView") String numeView,
			Model model) {
		
		
		
		Optional<Programare> programare_veche = programareService.getProgramare(programare.getProgramareId());
		long idModal = programare_veche.get().getProgramareId();
		
		
		//nume view = veziAnimaleStapan/idStapan
		if(numeView.equals("veziAnimaleStapan"))
			numeView = numeView + "/" + programare.getStapanId().getStapanId(); 
		
		if(numeView.equals("veziProgramariAnimal"))
			numeView = numeView + "/" + programare.getAnimalId().getAnimalId();
		

		if(programare.getMotiv().isEmpty()) {
			//daca e veziProgramari, mai avem si parametrul data de adaugat
			if(numeView.equals("veziProgramari")) {
				return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data + "&errprog&idModal=" + idModal + "&errmotiv";
			}
			//pt stapan
			if(numeView.equals("euProgramariAnimal"))
				return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&erradd&errmotiv" + "&idModal=" + idModal;
			else
				return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errprog&idModal=" + idModal + "&errmotiv";
		}
		
		if(programare.getDate() == null) {
			//daca e veziProgramari, mai avem si parametrul data de adaugat
			if(numeView.equals("veziProgramari")) {
				return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data + "&errprog&idModal=" + idModal + "&errdataprog";
			}
			//pt stapan
			if(numeView.equals("euProgramariAnimal"))
				return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&erradd&errdataprog" + "&idModal=" + idModal;
			else
				return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errprog&idModal=" + idModal + "&errdataprog";
		}
		
		// salvare programare in db
		programareService.addProgramare(programare);
		
		//email catre stapan programare schimbata (programare confirmata)
		User user = userRepository.findByEmail(programare.getStapanId().getEmail());				
		String nume = programare.getAnimalId().getNume();
		emailService.sendEmailProgramareConfirmata(user, programare.getDate(), nume);
		
		
			
		// redirect
		//daca e veziProgramari, mai avem si parametrul data de adaugat
		if(numeView.equals("veziProgramari")) {
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data;
		}
		if(numeView.equals("euProgramariAnimal")) {
			return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;
		}
		return "redirect:/" + numeView + "/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
	}
	
	
	@GetMapping("/programariAzi")
	private String arataProgramariAzi(Model model) {
		
		String data = LocalDate.now().toString();

		return findPaginatedProgramari(1, "date", "asc", data, model); // returneaza prima pagina
	}
	
	
	@GetMapping("/programari")
	private String arataProgramari(Model model) {
		
		return findPaginatedProgramari(1, "date", "asc", "", model); // returneaza prima pagina
	
	}
	
	
	@GetMapping("/veziProgramari/page/{pageNo}")
	public String findPaginatedProgramari(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("data") String data,
			Model model) {

		// atribute pentru a edita o programare
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		// atribute pentru numele vederii
		model.addAttribute("numeView", "veziProgramari");
		
		int pageSize = 5;
		Page<Programare> page = null;
		
		if(data != null) {
			page = programareService.findPaginatedData(pageSize, pageNo, pageSize, sortField, sortDir, data);
			//daca cautarea nu are rezultate, atunci ne intoarcem la programari pe prima pagina
			if(page.isEmpty())
				return "programari";
		}
		else
			page = programareService.findPaginatedProgramare(pageNo, pageSize, sortField, sortDir);
		
		
		//page = programareService.findPaginatedProgramare(pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();
		
		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			//daca nu exista inregistrari deloc, atunci ultima pagina va fi 0
			//nu putem sa o lasam asa,altfel ne va da eroarea "Page index must not be less than zero!"
			//asa ca daca pageNo este 0, atunci returnam numele vederii, pe prima pagina, care e goala.
			if(pageNo==0)
				return "programari";
			page = programareService.findPaginatedProgramare(pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent(); //altfel umple-o cu ultima pagina
		}
		
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
		model.addAttribute("numeView", "veziProgramari");
		
		//data pentru cautare 
		model.addAttribute("data", data);
		
		return "programari";

	}
	
	@GetMapping("/stergereProgramare")
	public String stergeProgramare(
			@ModelAttribute("programareId") long programareId,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("data") String data,
			@ModelAttribute("numeView") String numeView) {
		
		if(numeView.equals("veziProgramariAnimal"))
			numeView = numeView + "/" + programareService.getProgramare(programareId).get().getAnimalId().getAnimalId();

		if(numeView.equals("veziProgramariStapan"))
			numeView = numeView + "/" + programareService.getProgramare(programareId).get().getStapanId().getStapanId();
		if(numeView.equals("veziProgramariStapanNeconfirmate"))
			numeView = numeView + "/" + programareService.getProgramare(programareId).get().getStapanId().getStapanId();
		//if(numeView.equals("euProgramari")) //pt stapan
			//ramane asa
		
		
		Programare programare = programareService.getProgramare(programareId).get();
		User user = userRepository.findByEmail(programare.getStapanId().getEmail());
		String nume = programare.getAnimalId().getNume();
		
		//daca era confirmata si o sterge angajatul, atunci mail Programare Anulata
		//altfel, dara era neconfirmata, atunci e Refuzata
		//if(programare.getStare().equals("confirmata"))
			//emailService.sendEmailProgramareAnulata(user, programare.getDate(), nume);
		//else
		//	emailService.sendEmailProgramareRefuzata(user, programare.getDate(), nume);
		//email programare amanata
		
		
		programareService.deleteProgramare(programareId);
		
		
		// redirect
		//daca e veziProgramari, mai avem si parametrul data de adaugat
		if(numeView.equals("veziProgramari")) 
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data;
		//pt veziProgramariStapanNeconfirmate
		if(numeView.equals("veziProgramariStapanNeconfirmate"))
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data;
		if(numeView.equals("euProgramariAnimal")) {
			return "redirect:/" + numeView + "/" + programare.getAnimalId().getAnimalId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;
		}
		return "redirect:/" + numeView + "/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
	}
	
	
	
	
	
	@GetMapping("/formStergeProgramariVechi")
	public String showStergeProgramariVechi(Model model) {
		// cream o programare goala ca sa legam de o data anume
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		return "programari-vechi";
	}

	@PostMapping("/stergeProgramariVechi")
	public String stergeProgramariVechi(@ModelAttribute("programare") Programare programare) {
		
		Data data_stabilita = new Data();
		data_stabilita.setDate(programare.getDate());
		
		List<Programare> programari = programareService.getAllProgramari();
		for(Programare pr:programari) {
			if(pr.getDate().compareTo(data_stabilita.getDate()) < 0) //daca programarea este mai veche decat data aleasa, atunci e stearsa
				programareService.deleteProgramare(pr);
		}
		
		return "redirect:/programari";
	}
	
	
	
	
	
	@RequestMapping("/programariStapan/{id}")
	public String getProgramariStapan(Model model, @PathVariable(value = "id") long id){
		return findPaginatedProgramariStapan(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziProgramariStapan/{id}/page/{pageNo}")
	public String findPaginatedProgramariStapan(
			@PathVariable(value = "pageNo") int pageNo, 
			@PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			Model model) {

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
				return "redirect:/profilStapan/"+id;
			page = programareService.findPaginatedStapan(id, pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent();
		}
		
		Stapan stapan = stapanService.getStapan(id).get();
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
		model.addAttribute("numeView", "veziProgramariStapan");
		
		//data pentru cautare 
		//model.addAttribute("data", data);
		
		return "stapan-programari";

	}
	
	
	@RequestMapping("/programariAnimal/{id}")
	public String getProgramariAnimal(Model model, @PathVariable(value = "id") long id){
		return findPaginatedProgramariAnimal(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziProgramariAnimal/{id}/page/{pageNo}")
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
				return "redirect:/profilAnimal/"+id;
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
		model.addAttribute("numeView", "veziProgramariAnimal");
		
		
		return "animal-programari";

	}
	
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Pt programari neconfirmate ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	//Cand confirmam programarea, doar ii schimbam starea din neconfirmata in confirmata.
	
	@GetMapping("/confirmareProgramare")
	private String confirmaProgramarea(
			@ModelAttribute("programareId") long programareId,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("data") String data,
			@ModelAttribute("numeView") String numeView) {
		
		System.out.println(numeView);
		
		if(numeView.equals("veziProgramariNeconfirmateAnimal"))
			numeView = numeView + "/" + programareService.getProgramare(programareId).get().getAnimalId().getAnimalId();
		
		if(numeView.equals("veziProgramariNeconfirmateStapan"))
			numeView = numeView + "/" + programareService.getProgramare(programareId).get().getStapanId().getStapanId();
		
		Programare programare = programareService.getProgramare(programareId).get();
		programare.setStare("confirmata");
		programareService.addProgramare(programare);
		
		
		// redirect
		//daca e veziProgramariNeconfirmate, mai avem si parametrul data de adaugat
		if(numeView.equals("veziProgramariNeconfirmate")) {
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&data="+ data;
		}
		if(numeView.equals("veziProgramariStapanNeconfirmate")) {
			return "redirect:/" + numeView + "/" + programare.getStapanId().getStapanId() + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;
		}
		
		return "redirect:/" + numeView + "/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
		
	}
	
	
	@GetMapping("/programariNeconfirmate")
	private String arataProgramariNeconfirmate(Model model) {
		
		return findPaginatedProgramariNeconfirmate(1, "date", "asc", "", model); // returneaza prima pagina
	}
	
	@GetMapping("/programariNeconfirmateAzi")
	private String arataProgramariNeconfirmateAzi(Model model) {
		
		String data = LocalDate.now().toString();

		return findPaginatedProgramariNeconfirmate(1, "date", "asc", data, model); // returneaza prima pagina
	}
	
	@GetMapping("/veziProgramariNeconfirmate/page/{pageNo}")
	public String findPaginatedProgramariNeconfirmate(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("data") String data,
			Model model) {

		// atribute pentru a edita o programare
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		// atribute pentru numele vederii
		model.addAttribute("numeView", "veziProgramari");
		
		int pageSize = 5;
		Page<Programare> page = null;
		
		if(data != null) {
			page = programareService.findPaginatedDataNeconfirmata(pageSize, pageNo, pageSize, sortField, sortDir, data);
			//daca cautarea nu are rezultate, atunci ne intoarcem la programari pe prima pagina
			if(page.isEmpty())
				return "redirect:/programari?fararezultat&data="+data;
		}
		else
			page = programareService.findPaginatedDataNeconfirmata(pageSize, pageNo, pageSize, sortField, sortDir, data);
		
		
		//page = programareService.findPaginatedProgramare(pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();
		
		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			//daca nu exista inregistrari deloc, atunci ultima pagina va fi 0
			//nu putem sa o lasam asa,altfel ne va da eroarea "Page index must not be less than zero!"
			//asa ca daca pageNo este 0, atunci returnam numele vederii, pe prima pagina, care e goala.
			if(pageNo==0)
				return "programari";
			page = programareService.findPaginatedDataNeconfirmata(pageSize, pageNo, pageSize, sortField, sortDir, data);
			listaProgramari = page.getContent(); //altfel umple-o cu ultima pagina
		}
		
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
		model.addAttribute("numeView", "veziProgramariNeconfirmate");
		
		//data pentru cautare 
		model.addAttribute("data", data);
		
		return "programari-neconfirmate";

	}
	
	@RequestMapping("/programariStapanNeconfirmate/{id}")
	public String getProgramariStapanNeconfirmate(
			@PathVariable(value = "id") long id,
			Model model){
		return findPaginatedProgramariStapanNeconfirmate(id, 1, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziProgramariStapanNeconfirmate/{id}/page/{pageNo}")
	public String findPaginatedProgramariStapanNeconfirmate(
			@PathVariable(value = "id") long id,
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {

		int pageSize = 5;
		Page<Programare> page = programareService.findPaginatedStapanNeconfirmata(id, pageNo, pageSize, sortField, sortDir);
		List<Programare> listaProgramari = page.getContent();

		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaProgramari.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			if(pageNo == 0)
				return "redirect:/profilStapan/"+id;
			page = programareService.findPaginatedStapanNeconfirmata(id, pageNo, pageSize, sortField, sortDir);
			listaProgramari = page.getContent();
		}
		
		Stapan stapan = stapanService.getStapan(id).get();
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
		model.addAttribute("numeView", "veziProgramariStapanNeconfirmate");
		
		return "stapan-programari-neconfirmate";

	}
	
	
	
	
	
}
