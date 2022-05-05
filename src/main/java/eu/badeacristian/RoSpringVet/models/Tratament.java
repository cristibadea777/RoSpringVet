package eu.badeacristian.RoSpringVet.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Tratament{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long tratamentId;
	
	private String metodatratament;
	@DateTimeFormat(pattern = "MM/dd/yyyy h:mm a")
	private Date datainceput;
	@DateTimeFormat(pattern = "MM/dd/yyyy h:mm a")
	private Date datasfarsit;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tratamentId")
	private Vizita vizitaId;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "diagnosticId")
	private Diagnostic diagnosticId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "animalId")
	private Animal animalId;

	public Tratament(String metodatratament, Date datainceput, Date datasfarsit, Vizita vizitaId,
			Diagnostic diagnosticId, Animal animalId) {
		super();
		this.metodatratament = metodatratament;
		this.datainceput = datainceput;
		this.datasfarsit = datasfarsit;
		this.vizitaId = vizitaId;
		this.diagnosticId = diagnosticId;
		this.animalId = animalId;
	}

	
	
	
	
	
	
}
