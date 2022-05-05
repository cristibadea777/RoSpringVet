package eu.badeacristian.RoSpringVet.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.repositories.DiagnosticRepository;

@Service
public class DiagnosticService {

	@Autowired
	private DiagnosticRepository diagnosticRepository;
	
	//selecteaza toate diagnosticele
	public List<Diagnostic> getAllDiagnostice(){
		List<Diagnostic> diagnostice = new ArrayList<Diagnostic>();
		diagnosticRepository.findAll()
		.forEach(diagnostice::add);
		return diagnostice;
	}
	//selecteaza un diagnostic in functie de id
	public Optional<Diagnostic> getDiagnostic(long diagnosticId){
		return diagnosticRepository.findById(diagnosticId);
	}
	//adauga diagnostic
	public void addDiagnostic(Diagnostic diagnostic) {
		diagnosticRepository.save(diagnostic);
	}
	//update diagnostic
	public void updateDiagnostic(Diagnostic diagnostic) {
		diagnosticRepository.save(diagnostic);
	}
	//sterge diagnostic
	public void deleteDiagnostic(Diagnostic diagnostic) {
		diagnosticRepository.delete(diagnostic);
	}
	
}
