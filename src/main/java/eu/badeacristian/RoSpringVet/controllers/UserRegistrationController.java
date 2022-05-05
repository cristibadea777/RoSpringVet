package eu.badeacristian.RoSpringVet.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.badeacristian.RoSpringVet.models.User;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.repositories.UserRepository;
import eu.badeacristian.RoSpringVet.services.EmailService;
import eu.badeacristian.RoSpringVet.services.UserService;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

	private UserService userService;

	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@Autowired
	private EmailService emailService;
	
	

	@Autowired
	private UserRepository userRepository;

	@ModelAttribute("user")
	public UserRegistrationDTO userRegistrationDto() {
		return new UserRegistrationDTO();
	}

	@GetMapping
	public String showRegistrationForm() {
		return "registration";
	}

	@PostMapping
	public String registerUserAccountStapan(@ModelAttribute("user") @Valid UserRegistrationDTO registrationDto,
			Errors errors, Model model) {
		// registrationDto este obiectul pe care il "POSTeaza" utilizatorul cand se
		// inregistreaza, dupa ce da submit la formular, de asta il validam
		if (errors.hasErrors()) { // daca nu e format valid, daca este gol, etc
			return "registration";
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
			
			return "redirect:/registration?success";
		} else {
			return "redirect:/registration?errmail";
		}

	}

	@PostMapping("/angajat")
	public String registerUserAccountAngajat(@ModelAttribute("angajat") UserRegistrationDTO registrationDto,
			@ModelAttribute("functie") String functie) {
		userService.saveUser(registrationDto, "ROL_ANGAJAT");
		userService.saveAngajat(registrationDto, functie, null);
		return "redirect:/registration?success";
	}

	@ModelAttribute("user")
	public UserRegistrationDTO userRegistrationDtoAngajat() {
		return new UserRegistrationDTO();
	}

	@GetMapping("/angajat")
	public String showRegistrationFormAngajat() {
		return "angajat-adauga";
	}

}
