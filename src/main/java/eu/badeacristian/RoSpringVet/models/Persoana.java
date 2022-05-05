package eu.badeacristian.RoSpringVet.models;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//nu este entitate

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class Persoana {
	
	@NotNull
	@Size(min=2, message="Introdu un prenume")
	private String firstname;
	@NotNull
	@Size(min=2, message="Introdu un nume")
	private String lastname;
	private String nrtelefon;
	@Email(message = "Format email nevalid !")
	@Column(unique = true, length = 32)
	@NotNull
	@Size(min=7, message="Introdu un email")
	private String email;
	private String parola;
	
	public Persoana(String firstname, String lastname, String nrtelefon, String email, String parola) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.nrtelefon = nrtelefon;
		this.email = email;
		this.parola = parola;
	}
	

	
	
}
