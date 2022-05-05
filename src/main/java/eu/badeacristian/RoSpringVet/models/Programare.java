package eu.badeacristian.RoSpringVet.models;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Programare extends Data {

	//mosteneste Data
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long programareId;

	private String motiv;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stapanId")
	private Stapan stapanId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "animalId")
	private Animal animalId;

	private String numestapan;
	private String numeanimal;
	
	private String stare;

	public Programare(Date date, long programareId, String motiv, Stapan stapanId, Animal animalId, String numestapan,
			String numeanimal, String stare) {
		super(date);
		this.programareId = programareId;
		this.motiv = motiv;
		this.stapanId = stapanId;
		this.animalId = animalId;
		this.numestapan = numestapan;
		this.numeanimal = numeanimal;
		this.stare = stare;
	}
	
	

	
	
}
