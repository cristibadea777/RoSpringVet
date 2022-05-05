package eu.badeacristian.RoSpringVet.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Vizita;
import eu.badeacristian.RoSpringVet.repositories.VizitaRepository;

@Service
public class VizitaService {

	@Autowired
	private VizitaRepository vizitaRepository;
	
	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private AngajatService angajatService;
	
	@Autowired
	private StapanService stapanService;
	
	//selecteaza toate vizitele
	public List<Vizita> getAllVizita(){
		List<Vizita> vizite = new ArrayList<>();
		vizitaRepository.findAll()
		.forEach(vizite::add);
		return vizite;
	}
	//selecteaza o vizita in functie de id
	public Optional<Vizita> getVizita(long vizitaId){
		return vizitaRepository.findById(vizitaId);
	}
	//adauga vizita
	public void addVizita(Vizita vizita) {
		vizitaRepository.save(vizita);
	}
	//update vizita
	public void updateVizita(Vizita vizita) {
		vizitaRepository.save(vizita);
	}
	//sterge vizita
	public void deleteVizita(Vizita vizita) {
		vizitaRepository.delete(vizita);
	}
	public Page<Vizita> findPaginated(int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.vizitaRepository.findAll(pageable);
	}
	
	public Page<Vizita> findPaginatedAnimal(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Animal> animal = animalService.getAnimal(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.vizitaRepository.findAllByAnimalId(animal, pageable);
		
	}
	
	public Page<Vizita> findPaginatedAngajati(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Angajat> angajat = angajatService.getAngajat(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.vizitaRepository.findAllByAngajatId(angajat, pageable);
		
	}
	
	public Page<Vizita> findPaginatedStapan(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Stapan> stapan = stapanService.getStapan(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.vizitaRepository.findAllByStapanId(stapan, pageable);
	}
	
	
	
	
	
	
	
}
