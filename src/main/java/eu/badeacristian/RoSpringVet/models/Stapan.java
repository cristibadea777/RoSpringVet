package eu.badeacristian.RoSpringVet.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Stapan extends Persoana{

	//mosteneste Persoana
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long stapanId;
			
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stapanId")
	@JsonIgnore
	private List<Animal> animale;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stapanId")
	@JsonIgnore
	private List<Programare> programari;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stapanId")
	@JsonIgnore
	private List<Vizita> vizite;

	public Stapan(String firstname, String lastname, String nrtelefon, String email, String parola,
			List<Animal> animale, List<Programare> programari, List<Vizita> vizite) {
		super(firstname, lastname, nrtelefon, email, parola);
		this.animale = animale;
		this.programari = programari;
		this.vizite = vizite;
	}




	


}
