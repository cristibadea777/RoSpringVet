package eu.badeacristian.RoSpringVet.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Diagnostic;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.models.VizitaWrapper;
import eu.badeacristian.RoSpringVet.services.AngajatService;
import eu.badeacristian.RoSpringVet.services.AnimalService;
import eu.badeacristian.RoSpringVet.services.ProgramareService;
import eu.badeacristian.RoSpringVet.services.TratamentService;

@Controller
public class AnimalController {

	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private AngajatService angajatService;
	
	@Autowired
	private TratamentService tratamentService;
	
	@Autowired
	private ProgramareService programareService;
	
	@GetMapping("/animale")
	private String actiuniAnimalStapani(Model model) {
		return findPaginatedAnimale(1, "nume", "asc", model); // returneaza prima pagina
	}

	@GetMapping("/veziAnimale/page/{pageNo}")
	public String findPaginatedAnimale(
			@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, 
			@RequestParam("sortDir") String sortDir, 
			Model model) {

		int pageSize = 5;
		
		if(pageNo == 0)
			pageNo = 1;
		
		Page<Animal> page = animalService.findPaginatedAnimal(pageNo, pageSize, sortField, sortDir);	
		
		if(page.isEmpty()) {
			return "redirect:/stapani";
		}
				
		List<Animal> listaAnimale = page.getContent();
		
		//daca pagina ramane fara inregistrari (toate sunt sterse) sau in URL se introduce o pagina inexistenta
		//atunci luam ultima pagina (page.getTotalPages()) si populam tabelul
		//altfel utilizatorul se impotmoleste, va vedea un tabel gol fara sa poata naviga inapoi
		if(listaAnimale.size() == 0) {
			pageNo = page.getTotalPages(); //ultima pagina
			page = animalService.findPaginatedAnimal(pageNo, pageSize, sortField, sortDir);	
			listaAnimale = page.getContent();
		}

		// atribute paginare
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		// tabel
		model.addAttribute("listaAnimale", listaAnimale);

		// atribute sortare
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//nume view - pt redirectionare fie la veziAnimale fie la veziAnimaleStapan (in cazu asta e veziAnimale)
		model.addAttribute("numeView", "veziAnimale");
		
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
		
		//Atribute pentru adaugarea unei programari
		Programare programare = new Programare();
		model.addAttribute("programare", programare);
		
		
		return "animale";
	

	}

	@GetMapping("/profilAnimal/{id}")
	public String veziAnimal(@PathVariable(value = "id") long id, Model model) {
		// luam animalul cu id-ul
		Animal animal = animalService.getAnimal(id).get();
		// pre-populam pagina cu informatiile animalului
		model.addAttribute("animal", animal);
		//luam stapanul animalului
		Stapan stapan = animal.getStapanId();
		// pre-populam pagina cu informatiile stapanului
		model.addAttribute("stapan", stapan);
		
		//numarul tratamentelor active
		Page<Tratament> page = tratamentService.findPaginatedAnimal(animal.getAnimalId(), 1, 5, "datainceput", "asc");
		long numarTratamente = page.getTotalElements();
		model.addAttribute("numarTratamente", numarTratamente);
		
		//numarul programarilor confirmate
		Page<Programare> page_prog = programareService.findPaginatedAnimal(id, 1, 5, "date", "asc");
		long numarProgConfirmate = page_prog.getTotalElements();
		model.addAttribute("numarProgConfirmate", numarProgConfirmate);
		
		
		return "profil-animal";
	}
	
	
	@PostMapping("/editareAnimal")
	public String editareAnimal(
			@ModelAttribute("animal") @Valid Animal animal,
			Errors errors, 
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@ModelAttribute("numeView") String numeView,
			@ModelAttribute("imageFile") MultipartFile imageFile,
			Model model){
		
		
		Animal animal_vechi = animalService.getAnimal(animal.getAnimalId()).get();
		long id_stapan = animal_vechi.getStapanId().getStapanId();
		long idModal = animal_vechi.getAnimalId();
	
		//fie veziAnimale fie veziAnimaleStapan/idStapan fie veziAnimaleleTale
		//daca e veziAnimaleStapan/idStapan, atunci sa stabilim idStapan
		if(numeView.equals("veziAnimaleStapan")) {
			numeView = "veziAnimaleStapan/" + id_stapan;
		}
		
		
		if(errors.hasErrors()) {
			
			//erori nume
			if(errors.getFieldError("nume") != null) { 
				//nume prea scurt
				return "redirect:" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&err&idModal=" + idModal + "&errnume"; 
			}
			
			//erori specie
			if(errors.getFieldError("specie") != null) { 
				//specie prea scurta ca sa fie reala
				return "redirect:" + numeView +  "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&err&idModal=" + idModal + "&errspecie";
			}			
			
			//erori imagine
			if(errors.getFieldError("imagine") != null) { 
				return "redirect:" + numeView +  "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&err&idModal=" + idModal + "&errpoza";
			}	
			
			return "redirect:" + numeView +  "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&err&idModal=" + idModal;	//vreo eroare nedepistata de mine
		}
		
		String nume_poza =  animal_vechi.getImagine();		
		//daca userul a selectat o imagine noua pentru animal - adica daca variabila imageFile nu este goala, atunci setam acea imagine
		if(! imageFile.isEmpty()) {
			try {	
				//setare nume poza cu numele si id-ul animalului si numele stapanului
				String nume_animal = animal.getNume();
				Stapan stapan = animal_vechi.getStapanId();//nu luam ID-ul stapanului in form asa ca il luam din animalul vechi, pe care il luam cu ajutorul serviciului
				String nume_stapan = stapan.getLastname() + stapan.getFirstname();
				String extensie = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
			    nume_poza = animal.getAnimalId() + "_" + nume_animal + "_" + nume_stapan + extensie ;
							
				//salvare poza in folderul images
				String folder = "src\\main\\resources\\static\\images\\";
				byte[] bytes = imageFile.getBytes();
				Path path = Paths.get(folder + nume_poza);
				Files.write(path, bytes);				
				
			}catch(Exception e) {
				System.out.println(e);
			    return "redirect:" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&err&idModal=" + idModal + "&errpoza";
			}
		}
		
		//setare nume poza pt animal in baza de date
		animal.setImagine(nume_poza);
		//nu luam id-ul stapanului in formular, asa ca il setam in controller utilizandu-ne de vechiul animal
		animal.setStapanId(animal_vechi.getStapanId());
		//salvare animal editat 
		animalService.addAnimal(animal); //daca obiectul animal nu are erori atunci il salvam
		
		
		return "redirect:" + numeView +  "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;	 //daca totu e bine ne intoarcem pe pagina tabelului cu aceeasi directie de sortare si camp dupa care sortam
		
	}
	
	@GetMapping("/stergereAnimal")
	public String stergereAnimal(
			@ModelAttribute("animalId") long animalId,
			@RequestParam("currentPage") String currentPage,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("numeView") String numeView) {
		
		long id = animalService.getAnimal(animalId).get().getStapanId().getStapanId(); //inainte de a sterge animalu, luam ID-ul stapanului pentru redirectionare
		//stergem animalul
		animalService.deleteAnimal(animalId);
		//redirect
		
		System.out.println(numeView);
		
		if(numeView.equals("veziAnimale"))
			return  "redirect:/" + "veziAnimale" + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;
		
		if(numeView.equals("veziAnimaleleTale"))
			return "redirect:/" + numeView + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir; //veziAnimaleleTale nu are ID, e al stapanului si e luat din controller
		
		return "redirect:" + numeView + "/" + id + "/page/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir;	 //daca totu e bine ne intoarcem pe pagina tabelului cu aceeasi directie de sortare si camp dupa care sortam
	}
	
	

	//salvare poza in folderul  imagini
	@PostMapping("/incarcareImagine")
	public String incarcaImagine(@RequestParam("animalId") long id, @RequestParam("imageFile") MultipartFile imageFile) {
		 //parametrul de tip MultipartFile este poza, iar parametrul animalId ne trebuie pentru a seta poza animalului corespunzator		
		try {
	
			Animal animal = animalService.getAnimal(id).get();
			//setare nume poza cu numele si id-ul animalului si numele stapanului
			String nume_animal = animal.getNume();
			Stapan stapan = animal.getStapanId();
			String nume_stapan = stapan.getLastname() + stapan.getFirstname();
			String extensie = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
			String nume_poza = id + "_" + nume_animal + "_" + nume_stapan + extensie ;
			
			//salvare poza in folderul images
			String folder = "src\\main\\resources\\static\\images\\";
			byte[] bytes = imageFile.getBytes();
			Path path = Paths.get(folder + nume_poza);
			Files.write(path, bytes);
			
			//setare nume poza pt animal in baza de date
			animal.setImagine(nume_poza);
			//salvare animal editat 
			animalService.addAnimal(animal);
			
			
		}catch(Exception e) {
			System.out.println("Eroare, nu am putut incarca imaginea\n" + e);
			return "redirect:/profilAnimal/"+id+"?errpoza";
		}
		
		
		return "redirect:/profilAnimal/"+id+"?succes";
	}
	
	//pt stapan incarca imagine
	//salvare poza in folderul  imagini
		@PostMapping("/stapanIncarcareImagine")
		public String stapanIncarcaImagine(@RequestParam("animalId") long id, @RequestParam("imageFile") MultipartFile imageFile) {
			 //parametrul de tip MultipartFile este poza, iar parametrul animalId ne trebuie pentru a seta poza animalului corespunzator		
			try {
		
				Animal animal = animalService.getAnimal(id).get();
				//setare nume poza cu numele si id-ul animalului si numele stapanului
				String nume_animal = animal.getNume();
				Stapan stapan = animal.getStapanId();
				String nume_stapan = stapan.getLastname() + stapan.getFirstname();
				String extensie = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
				String nume_poza = id + "_" + nume_animal + "_" + nume_stapan + extensie ;
				
				//salvare poza in folderul images
				String folder = "src\\main\\resources\\static\\images\\";
				byte[] bytes = imageFile.getBytes();
				Path path = Paths.get(folder + nume_poza);
				Files.write(path, bytes);
				
				//setare nume poza pt animal in baza de date
				animal.setImagine(nume_poza);
				//salvare animal editat 
				animalService.addAnimal(animal);
				
				
			}catch(Exception e) {
				System.out.println("Eroare, nu am putut incarca imaginea\n" + e);
				return "redirect:/euProfilAnimal/"+id+"?errpoza";
			}
			
			
			return "redirect:/euProfilAnimal/"+id+"?succes";
		}

}
