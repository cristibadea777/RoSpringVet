package eu.badeacristian.RoSpringVet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.services.DiagnosticService;

@RestController
public class DiagnosticController {
	
	@Autowired
	private DiagnosticService diagnosticService;
	
	@RequestMapping("/diagnostice")
	public Iterable<Diagnostic> getDiagnostice(){
		return diagnosticService.getAllDiagnostice();
	}
	
}
