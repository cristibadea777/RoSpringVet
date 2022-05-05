package eu.badeacristian.RoSpringVet.models;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
	@NotNull(message = "Completează prenumele")
	@Size(min = 2, message = "Completează prenumele")
	private String firstname;
	@NotNull(message = "Completeaza numele")
	@Size(min = 2, message = "Completează numele")
	private String lastname;
	@Email(message = "Format email nevalid")
	@NotNull(message = "Email-ul nu trebuie să fie gol")
	private String email;
	@NotNull(message = "Parola nu trebuie să fie goală")
	@Size(min = 5, message = "Parola este prea scurtă")
	private String password;
	private String nrtelefon;
	
	
}
