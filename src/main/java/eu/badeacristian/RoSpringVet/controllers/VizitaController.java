package eu.badeacristian.RoSpringVet.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.models.VizitaWrapper;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.DiagnosticService;
import eu.badeacristian.RoSpringVet.services.StapanService;
import eu.badeacristian.RoSpringVet.services.TratamentService;
import eu.badeacristian.RoSpringVet.services.VizitaService;

@Controller
public class VizitaController {

	@Autowired
	private VizitaService vizitaService;
	
	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private StapanService stapanService;
	
	@Autowired
	private DiagnosticService diagnosticService;
	
	@Autowired
	private TratamentService tratamentService;
	
	@Autowired
	private AngajatService angajatService;
	
	
	@RequestMapping("/vizite")
	public String getVizite(Model model){
		return findPaginatedVizita(1, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziVizite/page/{pageNo}")
	public String findPaginatedVizita(@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "vizite";

	}

	
	@PostMapping("/salveazaVizita")
	public String saveVizita(
			@ModelAttribute("vizitawrapper") VizitaWrapper vizitawrapper, 
			@ModelAttribute("dropAngajat") Angajat angajatId, 
			@RequestParam("currentPage") int currentPage,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			@RequestParam("numeView") String numeView,
			Model model) {
		
		long idModal = vizitawrapper.getVizita().getAnimalId().getAnimalId();
		
		if(numeView.equals("veziAnimaleStapan"))
			numeView = numeView + "/" + vizitawrapper.getVizita().getStapanId().getStapanId(); 
		if(numeView.equals("veziViziteAnimal"))
			numeView = numeView + "/" + vizitawrapper.getVizita().getAnimalId().getAnimalId();
		
		//daca veterinar e null
		if(angajatId.getAngajatId() == 0) //"selecteaza angajat" din combo-box are valoarea 0
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errviz&idModal=" + idModal + "&errangajat";
		//daca data vizitei e null
		if(vizitawrapper.getVizita().getDate() == null )
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errviz&idModal=" + idModal + "&errvizdata";
		//daca diagnostic e null
		if(vizitawrapper.getDiagnostic().getDiagnostic().isEmpty())
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errviz&idModal=" + idModal + "&errdiagnostic";
		//daca data incepere tratament e null
		if(vizitawrapper.getTratament().getDatainceput() == null)
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errviz&idModal=" + idModal + "&errtratinceput";
		//daca tratament e null
		if(vizitawrapper.getTratament().getMetodatratament().isEmpty())
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&errviz&idModal=" + idModal + "&errtratament";
		//altfel:
		
		try {
			//Pentru ca obiectele nu erau inca salvate, cheile lor straine o sa fie nulle, de aia le setez aici dupa ce salvez pe fiecare 
			
			//salvare diagnostic in db
			vizitawrapper.getDiagnostic().setTratamentId(vizitawrapper.getTratament());
			diagnosticService.addDiagnostic(vizitawrapper.getDiagnostic());
			
			//salvare tratament in db
			vizitawrapper.getTratament().setDiagnosticId(vizitawrapper.getDiagnostic());
			tratamentService.addTratament(vizitawrapper.getTratament());
			
			//angajatul preluat din combobox
			Angajat angajat = angajatService.getAngajat(angajatId.getAngajatId()).get();
			String nume_angajat = angajat.getLastname() + " " + angajat.getFirstname();
			
			// salvare vizita in db - setare tratament si diagnostic
			vizitawrapper.getVizita().setDiagnosticId(vizitawrapper.getDiagnostic());
			vizitawrapper.getVizita().setTratamentId(vizitawrapper.getTratament());
			vizitawrapper.getVizita().setAngajatId(angajat);
			vizitawrapper.getVizita().setNumeangajat(nume_angajat);
			vizitaService.addVizita(vizitawrapper.getVizita());

		}catch(Exception e) {
			System.out.println(e);
		}
		
		// redirect
		return "redirect:/" + numeView + "/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
	}
	
	
	@RequestMapping("/viziteAnimal/{id}")
	public String getViziteAnimal(Model model, @PathVariable(value = "id") long id){
		return findPaginatedViziteAnimal(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziViziteAnimal/{id}/page/{pageNo}")
	public String findPaginatedViziteAnimal(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginatedAnimal(id, pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		Animal animal = animalService.getAnimal(id).get();
		model.addAttribute("animal", animal);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		// Atribute pentru adaugarea unei vizite noi cu modalul
		// Vizita
		Vizita vizita = new Vizita();
		// Diagnostic
		Diagnostic diagnostic = new Diagnostic();
		model.addAttribute("diagnostic", diagnostic);
		long diagnostic_id = diagnostic.getDiagnosticId();
		model.addAttribute("id_diagnostic", diagnostic_id);
		// Tratament
		Tratament tratament = new Tratament();
		model.addAttribute("tratament", tratament);
		long tratament_id = tratament.getTratamentId();
		model.addAttribute("id_tratament", tratament_id);
		// Angajat
		Angajat angajat = new Angajat();
		List<Angajat> lista_angajati = angajatService.getAllAngajati();
		model.addAttribute("lista_angajati", lista_angajati);
		model.addAttribute("angajat", angajat);
		VizitaWrapper vizitawrapper = new VizitaWrapper();
		// impachetam toate obiectele pe care vrem sa le salvam in obiectul
		// vizitawrapper, care e trimis de formular, acolo luam obiectele cu get() si le salvam
		vizitawrapper.setVizita(vizita);
		vizitawrapper.setDiagnostic(diagnostic);
		vizitawrapper.setTratament(tratament);
		model.addAttribute("vizitawrapper", vizitawrapper);		
		
		//Atribute pt redirectionare
		//Nume view
		model.addAttribute("numeView", "veziViziteAnimal");
		
		return "animal-vizite";

	}
	
	
	@RequestMapping("/viziteStapan/{id}")
	public String getViziteStapan(Model model, @PathVariable(value = "id") long id){
		return findPaginatedViziteStapan(1, id, "date", "asc", model); // returneaza prima pagina
	}
	
	@GetMapping("/veziViziteStapan/{id}/page/{pageNo}")
	public String findPaginatedViziteStapan(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginatedStapan(id, pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		Stapan stapan = stapanService.getStapan(id).get();
		model.addAttribute("stapan", stapan);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "stapan-vizite";

	}
	
	
	@RequestMapping("/viziteAngajat/{id}")
	public String getViziteAngajat(Model model, @PathVariable(value = "id") long id){
		return findPaginatedViziteAngajat(1, id, "date", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziViziteAngajat/{id}/page/{pageNo}")
	public String findPaginatedViziteAngajat(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "id") long id,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

		int pageSize = 5;
		Page<Vizita> page = vizitaService.findPaginatedAngajati(id, pageNo, pageSize, sortField, sortDir);
		List<Vizita> listaVizite = page.getContent();

		Angajat angajat = angajatService.getAngajat(id).get();
		model.addAttribute("angajat", angajat);
		
		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaVizite", listaVizite);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "angajat-vizite";

	}
	
}
