package eu.badeacristian.RoSpringVet.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Role;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.User;
import eu.badeacristian.RoSpringVet.models.UserRegistrationDTO;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.repositories.AngajatRepository;
import eu.badeacristian.RoSpringVet.repositories.StapanRepository;
import eu.badeacristian.RoSpringVet.repositories.UserRepository;



@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StapanRepository stapanRepository;
	
	@Autowired
	private AngajatRepository angajatRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(UserRegistrationDTO registrationDto, String rol) {
		User user = new User(registrationDto.getFirstname(), registrationDto.getLastname(), registrationDto.getEmail(), passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role(rol)));
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public Stapan saveStapan(UserRegistrationDTO registrationDto,  List<Animal> animale, List<Programare> programari, List<Vizita> vizite) {
		Stapan stapan = new Stapan(registrationDto.getFirstname(), registrationDto.getLastname(), registrationDto.getNrtelefon(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()), animale, programari, vizite);
		return stapanRepository.save(stapan);
	}
	@Override
	public Angajat saveAngajat(UserRegistrationDTO registrationDto, String functie, List<Vizita> vizite) {
		Angajat angajat = new Angajat(registrationDto.getFirstname(), registrationDto.getLastname(), registrationDto.getNrtelefon(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()), functie, vizite);
		System.out.println(registrationDto.getPassword());
		return angajatRepository.save(angajat);
	}
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRepository.findByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException("Email sau parola gresite");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));		
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
	
}
