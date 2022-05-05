package eu.badeacristian.RoSpringVet.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VizitaWrapper {

	private Vizita vizita;
	private Tratament tratament;
	private Diagnostic diagnostic;
}
