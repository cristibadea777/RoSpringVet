package eu.badeacristian.RoSpringVet.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.models.User;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.models.VizitaWrapper;
import eu.badeacristian.RoSpringVet.repositories.UserRepository;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.EmailService;
import eu.badeacristian.RoSpringVet.services.ProgramareService;
import eu.badeacristian.RoSpringVet.services.StapanService;
import eu.badeacristian.RoSpringVet.services.UserService;

@Controller
public class StapanController {

	@Autowired
	private StapanService stapanService;
	
	@Autowired
	private UserService userService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private ProgramareService programareService;

	
	@Autowired
	private AnimalService animalService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AngajatService angajatService;
	
	@GetMapping("/stapani")
	private String actiuniStapani(Model model) {
		
		return findPaginatedStapan(1, "firstname", "asc", "", "", model); // returneaza prima pagina
	}

	@GetMapping("/veziStapani/page/{pageNo}")
	public String findPaginatedStapan(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir,
			@RequestParam("txtSearch") String txtSearch,
			@RequestParam("fieldSearch") String fieldSearch,
			Model model) {
		
		int pageSize = 5;
		
		Page<Stapan> page = null;
		List<Stapan> listaStapani = null;
		
		//la lansarea aplicatiei prima oara, nu avem stapani
		//asa ca vom crea un stapan, RoSpringVetStapan, altfel ne da eroarea "Page index must not be less than zero!"
		
		if(txtSearch.isEmpty()) {
			//gaseste paginat toti stapanii
			page = stapanService.findPaginated(pageNo, pageSize, sortField, sortDir);
			listaStapani = page.getContent();

			//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
			//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
			//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
			if(listaStapani.size() == 0) {
				pageNo = page.getTotalPages(); //ultima pagina
				page = stapanService.findPaginated(pageNo, pageSize, sortField, sortDir);
				listaStapani = page.getContent();
			}
			
		}
		else {
			// gaseste paginat stapanii al carui <<fieldSearch>> incep cu <<txtSearch>>
			page = stapanService.findPaginatedSearch(pageNo, pageSize, sortField, sortDir, fieldSearch, txtSearch);
		
			//daca utilizatorul introduce in URL un nume de camp ce nu exista
			//atunci pagina o sa fie null, si o sa dea eroare.
			//deci il redirectionam inapoi pe prima pagina
			if(page == null) {
				return "redirect:/stapani?campinexistent";
			}
			
			//daca pagina este goala, adica nu am gasit nici un rezultat pentru cautarea noastra
			//atunci mergem inapoi pe pagina stapanilor si informam utilizatorul deschizand un modal
			if(page.isEmpty()) {
				
				//aici doar traduc in Română 
				if(fieldSearch.equals("lastname"))
					fieldSearch = "Nume";
				if(fieldSearch.equals("firstname"))
					fieldSearch = "Prenume";
				if(fieldSearch.equals("nrtelefon"))
					fieldSearch = "Nr.telefon";			
				if(fieldSearch.equals("email"))
					fieldSearch = "Email";		
				
				return "redirect:/stapani?fararezultat&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch;
			}
			
			listaStapani = page.getContent();

			// daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
			// atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
			// altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
			if (listaStapani.size() == 0) {
				pageNo = page.getTotalPages(); // ultima pagina
				page = stapanService.findPaginatedSearch(pageNo, pageSize, sortField, sortDir, fieldSearch, txtSearch);				
				listaStapani = page.getContent();
			}
			
			
		}

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		//atribute cautare
		model.addAttribute("txtSearch", txtSearch);
		model.addAttribute("fieldSearch", fieldSearch);

		// tabel
		model.addAttribute("listaStapani", listaStapani);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//pentru a adauga un stapan (USER) nou folosind modal-ul
		//punem un nou USER (Un Data Transfer Objec care sa tina datele pentru USER-ul nostru. Folosim clasa noastra UserRegistrationDTO) in model
		//cream un USER gol ca sa ne legam de datele din formularul modalului

		UserRegistrationDTO user = new UserRegistrationDTO();
		model.addAttribute("user", user);
		
		//pentru a adauga un animal  nou folosind modal-ul
		//cream un animal gol si il punem in model ca sa ne legam de datele din formularul modalului 
		Animal animal = new Animal();
		model.addAttribute("animal", animal);
		
		model.addAttribute("numeView", "veziStapani");
		
		return "stapani";

	}
	
	@GetMapping("/profilStapan/{id}")
	public String veziStapan(@PathVariable(value = "id") long id, Model model) {
		// apare o noua pagina cu profilu stapanului
		
		Stapan stapan = stapanService.getStapan(id).get();
		
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
		return "profil-stapan";
	}
	
	@PostMapping("/adaugaAnimalStapan/{id}")
	public String saveAnimalStapan(
			@ModelAttribute("animal") @Valid Animal animal, 
			Errors errors, 
			@ModelAttribute("imageFile") MultipartFile imageFile,
			@PathVariable(value = "id") long id, 
			@RequestParam("numeView") String numeView,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("txtSearch") String txtSearch,
			@RequestParam("fieldSearch") String fieldSearch,
			Model model) {
		
		if(numeView.equals("veziAnimaleleTale,veziAnimaleleTale")) //nu stiu pe unde in cod se face de doua ori da nu mai  caut
			numeView="veziAnimaleleTale";

		//daca nume sau specie sunt goale, mergem inapoi pe pagina animalelor stapanului, si deschidem modalul  cu mesaju de eroare potrivit 
		//mesajele de eroare il iau din clasa Animal, pentru ca validez un obiect
		if (errors.hasFieldErrors("nume")) {
			
			if(numeView.equals("veziStapani"))
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&errnume" + "&erranimal&idModal=" + id;
			if(numeView.equals("veziAnimaleleTale"))
				return "redirect:/" + numeView + "?erradd&errnume";
			else
				return "redirect:/" + numeView + "/" +id +"?erradd&errnume" + "&err&idModal=" + id;			
		}
		
		if (animal.getSpecie().isEmpty()) {
			if(numeView.equals("veziStapani"))
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&errspecie" + "&erranimal&idModal=" + id;
			if(numeView.equals("veziAnimaleleTale"))
				return "redirect:/" + numeView + "?erradd&errspecie";
			else
				return "redirect:/" + numeView + "/" +id +"?erradd&errspecie" + "&err&idModal=" + id;
		}
		
		// salvare animal in db
		animalService.addAnimal(animal);
		
		//salvare poza animal in folder
		//parametrul de tip MultipartFile este poza, iar parametrul animalId ne trebuie pentru a seta poza animalului corespunzator		
		
		//daca userul a selectat o imagine - adica daca variabila imageFile nu este goala, atunci setam acea imagine
		if(! imageFile.isEmpty()) {
			try {	
				//setare nume poza cu numele si id-ul animalului si numele stapanului
				String nume_animal = animal.getNume();
				Stapan stapan = animal.getStapanId();
				String nume_stapan = stapan.getLastname() + stapan.getFirstname();
				String extensie = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
				String nume_poza = animal.getAnimalId() + "_" + nume_animal + "_" + nume_stapan + extensie ;
							
				//salvare poza in folderul images
				String folder = "src\\main\\resources\\static\\images\\";
				byte[] bytes = imageFile.getBytes();
				Path path = Paths.get(folder + nume_poza);
				Files.write(path, bytes);
				
				//setare nume poza pt animal in baza de date
				animal.setImagine(nume_poza);

				//salvare animal editat 
				animalService.addAnimal(animal);
				
				
			}catch(Exception e) {
				System.out.println("EROARE:\n" + e);
				return "redirect:/" + numeView + "/" +id +"?erradd&errpoza" + "&err&idModal=" + id;
			}
		}
		
		System.out.println(numeView);
		if(numeView.equals("veziAnimaleleTale"))
			return "redirect:/veziAnimaleleTale";
		
		// redirect
		return "redirect:/veziAnimaleStapan/"+id;
	}
	
	
		
	@GetMapping("/veziAnimaleStapan/{id}")
	private String actiuniAnimalStapani(Model model, @PathVariable(value = "id") long id) {
		return findPaginatedAnimaleStapan(1, id, "nume", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziAnimaleStapan/{id}/page/{pageNo}")
	public String findPaginatedAnimaleStapan(
			@PathVariable(value = "pageNo") int pageNo,
			@PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir, Model model) {
				
		int pageSize = 5;
		Page<Animal> page = animalService.findPaginatedAnimalStapan(id, pageNo, pageSize, sortField, sortDir);
		
		if(page.isEmpty())
			return "redirect:/stapani";
		
		List<Animal> listaAnimale = page.getContent();
		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaAnimale.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			page = animalService.findPaginatedAnimalStapan(id, pageNo, pageSize, sortField, sortDir);
			listaAnimale = page.getContent();
		}

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaAnimale", listaAnimale);

		// adaugam numele stapanului in model
		String nume_stapan = stapanService.getStapan(id).get().getFirstname() + " " + stapanService.getStapan(id).get().getLastname();
		model.addAttribute("nume_stapan", nume_stapan);
		
		//id stapan
		model.addAttribute("id_stapan", id);
		
		//nume view - pt redirectionare fie la veziAnimale fie la veziAnimaleStapan (in cazu asta e veziAnimaleStapan)
		model.addAttribute("numeView", "veziAnimaleStapan");

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//pentru a adauga un animal nou folosind modal-ul
		//punem un nou animal in model
		//cream un animal gol ca sa ne legam de datele din formularul modalului

		Animal animal = new Animal();
		model.addAttribute("animal", animal);

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
		
		
		//Atribute pentru adaugarea unei programari
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		return "stapan-animale-lista";

	}	
	
	@PostMapping("/editareStapan")
	public String editStapan(
			@ModelAttribute("stapan") @Valid Stapan stapan_nou,
			Errors errors, 
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("txtSearch") String txtSearch,
			@RequestParam("fieldSearch") String fieldSearch,
			Model model)  {

		//ID-ul noului stapan nu se schimba, este acela cu al vechiului stapan. 
		//Luam informatiile vechiului stapan, ne folosim de ele si dupa le setam pe cele noi.
		Stapan stapan_vechi = stapanService.getStapan(stapan_nou.getStapanId()).get(); 
		String email_vechi = stapan_vechi.getEmail();
		
		//ID-ul modalului ce va aparea pe ecran, la fel cu al stapanului
		long idModal = stapan_nou.getStapanId();
		
		//Validam obiectul "stapan" primit, pentru nume, prenume, campul lui de "email" etc.
		//Vom transmite ca parametru pentru URL "email" daca exista erori pentru campul de "email". Daca acest parametru exista (verificam asta utilizand Thymeleaf),
		//in modal-ul nostru va aparea un mesaj de eroare corespunzator pentru a atrage atentia utilizatorului ca email-ul trimis nu este valid.			
		//Id-ul modalului, directia de sortare, campul dupa care se sorteaza, pagina,vor fi trimise ca parametru si citite utilizand Thymeleaf.
		if(errors.hasErrors()) {
			
			if(errors.getFieldError("firstname") != null) { 
				//prenume prea scurt ca sa fie real
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal + "&prenume"; 
			}
			// /veziStapani/page/2?sortField=firstname&sortDir=asc
			if(errors.getFieldError("lastname") != null) { 
				//nume prea scurt ca sa fie real
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal + "&nume";
			}
			
			if(errors.getFieldError("email") != null) { 
				//email prea scurt ca sa fie real, email format gresit
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal + "&email";
			}			
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal;	//vreo eroare nedepistata de mine
		}
		
		//Am obervat ca pentru constrangerea "unique" nu o sa se inregistreze erori pentru obiectul "stapan", deci acest if l-am scos din if-ul care verifica daca sunt erori in prima instanta 
		if(!stapan_nou.getEmail().equals(email_vechi) && stapanService.getStapanByEmail(stapan_nou.getEmail()) != null) { 
			//email deja existent (care sa fie diferit de al vechiului stapan)
			//daca nu e asa, o sa fie informat utilizatorul print-un mesaj de eroare "Email deja existent"
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal + "&emailExistent";
		}
		
		//Email-ul trebuie sa fie mai mic de 32 de caractere, ca asa am pus in constrangerea Unique
		if(stapan_nou.getEmail().length() > 32) {
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&err&idModal=" + idModal + "&emailLung";
		}
		
		else {

			//salvare stapan_nou daca nu are nicio eroare
			//setam parola vechiului stapan, ca asta nu s-a schimbat si nu am luat-o in form cu hidden field cum am luat ID-ul, altfel primeim un mesaj de eroare care ne spune ca parola nu poate fi nula.
			stapan_nou.setParola(stapan_vechi.getParola());
			stapanService.addStapan(stapan_nou);
			
			//si useru cu acelas email vechi sa se ia si sa se seteze cu nou mail, nume, prenume, parola
			User user = userRepository.findByEmail(email_vechi);
			user.setFirstname(stapan_nou.getFirstname());
			user.setLastname(stapan_nou.getLastname());
			user.setEmail(stapan_nou.getEmail());
			user.setPassword(stapan_nou.getParola());
			userService.updateUser(user);
		}
		
		return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch;
	}
	
	
	@GetMapping("/stergereStapan")
	public String stergereStapan(
			@RequestParam("stapanId") long stapanId,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("txtSearch") String txtSearch,
			@RequestParam("fieldSearch") String fieldSearch,
			Model model) {
		
		
		String email = stapanService.getStapan(stapanId).get().getEmail();
		User user = userRepository.findByEmail(email);
		stapanService.deleteStapan(stapanId);
		userRepository.delete(user);
		
		return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch;
	}

	
	@PostMapping("/adaugareStapan")
	public String adaugareStapan(
			@ModelAttribute("user") @Valid UserRegistrationDTO registrationDto,
			Errors errors,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("txtSearch") String txtSearch,
			@RequestParam("fieldSearch") String fieldSearch,
			Model model) {
				
		// registrationDto este obiectul pe care il "POSTeaza" utilizatorul cand se
		// inregistreaza, dupa ce da submit la formular, de asta il validam
		if (errors.hasErrors()) { // daca nu e format valid, daca este gol, etc
		
			if(errors.getFieldError("firstname") != null) { 
				//prenume prea scurt ca sa fie real
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd&errprenume"; 
			}
			// /veziStapani/page/2?sortField=firstname&sortDir=asc
			if(errors.getFieldError("lastname") != null) { 
				//nume prea scurt ca sa fie real
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd&errnume"; 
			}
			
			if(errors.getFieldError("email") != null) { 
				//email prea scurt ca sa fie real, email format gresit
				//System.out.println(errors.getFieldError("email").toString());
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd&errmail1"; 
			}	
			
			if(errors.getFieldError("password") != null) { 
				//email prea scurt ca sa fie real, email format gresit
				return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd&errparola"; 
			}
			
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd";
		}
		// pentru ca n-am cum sa verific in DTO daca exista deja, folosesc repozitoriul
		// (daca as fi vrut asta ar fi trebuit ca in loc de DTO sa iau User ca obiect al formularului)
		// pentru DTO se fac celelalte verificari pentru ca le pot face (@Email - sa fie format valid, @NotNull, @Size, etc) - si sunt capturate de .hasErrors,
		// cu Thymeleaf le pun in View
				
		User user = userRepository.findByEmail(registrationDto.getEmail()); 
		// daca este null inseamna ca nu exista deja, altfel da eroare ca deja exista.
		//Utilizam adnotarea @Unique pentru campul de "email" in entitatea noastra de User, pentru a nu putea introduce mai multe mail-uri la fel in baza de date. 
		if (user == null && registrationDto.getEmail().length() < 32) {	
			//salvam userul daca mail-ul nu exista deja si nu este mai mare de 32 de caractere 
			userService.saveUser(registrationDto, "ROL_STAPAN");
			userService.saveStapan(registrationDto, null, null, null);
			
			//trimitere notificare catre noul utilizator inregistrat
			User userDupaSalvare = userRepository.findByEmail(registrationDto.getEmail());
			try {
				emailService.sendEmailInregistrare(userDupaSalvare);
			}catch(MailException ex) {
				System.out.println(ex);
			} 
			
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&success";

		} else {
			return "redirect:/veziStapani/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir  + "&txtSearch=" + txtSearch + "&fieldSearch=" + fieldSearch + "&erradd" + "&errmail2";

		}

	}
	
	
}