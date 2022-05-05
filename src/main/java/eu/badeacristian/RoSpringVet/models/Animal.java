package eu.badeacristian.RoSpringVet.models;


import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Animal {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long animalId;
	
	@NotNull
	@Size(min = 2, message = "Nu poți lăsa un animal fără nume !")
	private String nume;
	
	@NotNull
	@Size(min = 3, message = "Nu poți lăsa un animal fără specie !")
	private String specie;

	private String rasa;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stapanId")
	@JsonIgnore
	private Stapan stapanId;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "animalId")
	@JsonIgnore
	private List<Vizita> vizite;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "animalId")
	@JsonIgnore
	private List<Programare> programari;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "animalId")
	@JsonIgnore
	private List<Tratament> tratamente;

	private String imagine;
	

	public Animal(@Size(min = 2, message = "Nu poți lăsa un animal fără nume !") String nume,
			@Size(min = 3, message = "Nu poți lăsa un animal fără specie !") String specie, String rasa,
			Stapan stapanId, List<Vizita> vizite, List<Programare> programari, List<Tratament> tratamente,
			String imagine) {
		super();
		this.nume = nume;
		this.specie = specie;
		this.rasa = rasa;
		this.stapanId = stapanId;
		this.vizite = vizite;
		this.programari = programari;
		this.tratamente = tratamente;
		this.imagine = imagine;
	}
	
	

	
	
	

}
