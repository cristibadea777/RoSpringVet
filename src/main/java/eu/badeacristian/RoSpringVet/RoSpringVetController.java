package eu.badeacristian.RoSpringVet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.StapanService;
import eu.badeacristian.RoSpringVet.services.UserService;

@Controller
public class RoSpringVetController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private StapanService stapanService;
	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private AngajatService angajatService;
	

	
	@GetMapping("/start")
	public String pornesteAplicatia(Model model) {
		//Prima oara cand se acceseaza aplicatia vreodata, adminu trebuie sa stie ca trebuie sa navigheaze catre /start...
		//Acum se populeaza cu un angajat (mai departe poate adauga alti angajati acum) si cu un stapan, si cu un animal 
		//- ca sa poata adauga stapani si animale fara stapan daca pana acum nu s-au inregistrat stapani si sa-si inscrie animale
		if(angajatService.getAllAngajati().isEmpty())
			adaugaInregistrari();
		
		//pentru a adauga un stapan (USER) nou folosind modal-ul
		//punem un nou USER (Un Data Transfer Objec care sa tina datele pentru USER-ul nostru. Folosim clasa noastra UserRegistrationDTO) in model
		//cream un USER gol ca sa ne legam de datele din formularul modalului

		UserRegistrationDTO user = new UserRegistrationDTO();
		model.addAttribute("user", user);
		
		return "login";
	}
	
	public void adaugaInregistrari() {
		UserRegistrationDTO userRegDto = new UserRegistrationDTO();
		userRegDto.setEmail("RoSpringVetAdmin@gmail.com");
		userRegDto.setFirstname("RoSpringVet");
		userRegDto.setLastname("Admin");
		userRegDto.setPassword("fulger");
		userService.saveUser(userRegDto, "ROL_ANGAJAT");
		userService.saveAngajat(userRegDto, "Veterinar RoSpringVet", null);
		
	    userRegDto = new UserRegistrationDTO();
		userRegDto.setEmail("RoSpringVetStapan@gmail.com");
		userRegDto.setFirstname("RoSpringVet");
		userRegDto.setLastname("Stapan");
		userRegDto.setPassword("fulger");
		userService.saveUser(userRegDto, "ROL_STAPAN");
		userService.saveStapan(userRegDto, null, null, null);
		
		Animal animalSpringVet = new Animal();
		animalSpringVet.setNume("Țițușa");
		animalSpringVet.setSpecie("Pisică");
		animalSpringVet.setRasa("Europeana");
		animalSpringVet.setStapanId(stapanService.getStapanByEmail("RoSpringVetStapan@gmail.com"));
		animalService.addAnimal(animalSpringVet);
		
	}
	
	
	
	
}