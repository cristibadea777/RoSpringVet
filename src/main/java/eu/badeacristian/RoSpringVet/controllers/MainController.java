package eu.badeacristian.RoSpringVet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.repositories.UserRepository;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.ProgramareService;
import eu.badeacristian.RoSpringVet.services.StapanService;

@Controller
public class MainController {
	
	@Autowired
	StapanService stapanService;
	
	@Autowired
	AngajatService angajatService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProgramareService programareService;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	
	@GetMapping("/")
	public ModelAndView home(Model model) {
		String username = "wut";
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}		
		
		//model and view
		ModelAndView modelAndView = new ModelAndView();
		
		Stapan stapan = stapanService.getStapanByEmail(username);

		if(stapan != null) {
			modelAndView.addObject("stapan", stapan);
			modelAndView.addObject("nume_stapan", stapan.getFirstname() + ' ' + stapan.getLastname());
			//numarul programarilor neconfirmate
			Page<Programare> page = programareService.findPaginatedStapanNeconfirmata(stapan.getStapanId(), 1, 5, "date", "asc");
			long programari_neconfirmate = page.getTotalElements();
			model.addAttribute("programari_neconfirmate", programari_neconfirmate);
			
			//numarul programarilor confirmate
			Page<Programare> page_conf = programareService.findPaginatedStapan(stapan.getStapanId(), 1, 5, "date", "asc");
			long programari_confirmate = page_conf.getTotalElements();
			model.addAttribute("programari_confirmate", programari_confirmate);
			
			modelAndView.setViewName("Acasa-Stapani");
		}
		else {
			Angajat angajat = angajatService.getAngajatByEmail(username);
			if(angajat != null) {
				modelAndView.addObject("angajat", angajat);
				modelAndView.addObject("nume_angajat", angajat.getFirstname() + ' ' + angajat.getLastname());
				modelAndView.setViewName("Acasa-Angajati");
			}
			else {
				modelAndView.setViewName("/login");
			}
		}
		
		
		return modelAndView;
		
	}
	
	
	
	
	
	
	
	
}
