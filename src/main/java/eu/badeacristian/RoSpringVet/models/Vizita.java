package eu.badeacristian.RoSpringVet.models;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Vizita extends Data{

	//mosteneste Data
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long vizitaId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "animalId")
	private Animal animalId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stapanId")
	private Stapan stapanId;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "angajatId")
	private Angajat angajatId;
	
	private String numestapan;
	private String numeanimal;
	private String numeangajat;
	private String motiv;

	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "diagnosticId")
	private Diagnostic diagnosticId;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "tratamentId")
	private Tratament tratamentId;

	public Vizita(Animal animalId, Stapan stapanId, Angajat angajatId, String numestapan, String numeanimal,
			String numeangajat, String motiv, Diagnostic diagnosticId, Tratament tratamentId) {
		super();
		this.animalId = animalId;
		this.stapanId = stapanId;
		this.angajatId = angajatId;
		this.numestapan = numestapan;
		this.numeanimal = numeanimal;
		this.numeangajat = numeangajat;
		this.motiv = motiv;
		this.diagnosticId = diagnosticId;
		this.tratamentId = tratamentId;
	}

	

}
