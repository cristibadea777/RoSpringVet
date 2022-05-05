package eu.badeacristian.RoSpringVet.models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diagnostic {
	 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long diagnosticId;
	private String diagnostic;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "diagnosticId")
	private Vizita vizitaId;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "tratamentId")
	private Tratament tratamentId;

	public Diagnostic(String diagnostic, Vizita vizitaId, Tratament tratamentId) {
		super();
		this.diagnostic = diagnostic;
		this.vizitaId = vizitaId;
		this.tratamentId = tratamentId;
	}
	
	
	
	
}
