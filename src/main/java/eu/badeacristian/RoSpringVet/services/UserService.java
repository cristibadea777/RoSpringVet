package eu.badeacristian.RoSpringVet.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.User;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.models.Vizita;



public interface UserService extends UserDetailsService{
	User saveUser(UserRegistrationDTO registrationDto, String rol);
	User updateUser(User user);
	Stapan saveStapan(UserRegistrationDTO registrationDto, List<Animal> animale, List<Programare> programari, List<Vizita> vizite);
	Angajat saveAngajat(UserRegistrationDTO registrationDto, String functie, List<Vizita> vizite);
	
}
