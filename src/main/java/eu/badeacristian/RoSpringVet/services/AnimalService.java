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
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.repositories.AnimalRepository;

@Service
public class AnimalService {
	
	@Autowired
	private AnimalRepository animalRepository;
	
	@Autowired
	private StapanService stapanService;
	
	//selecteaza toate animalele
	public List<Animal> getAllAnimale(){
		List<Animal> animale = new ArrayList<Animal>();
		animalRepository.findAll()
		.forEach(animale::add);
		return animale;
	}
	//selecteaza un animal in functie de id
	public Optional<Animal> getAnimal(long animalId){
		return animalRepository.findById(animalId);
	}
	//adauga animal
	public void addAnimal(Animal animal) {
		animalRepository.save(animal);
	}
	//update animal
	public void updateAnimal(Animal animal) {
		animalRepository.save(animal);
	}
	//sterge animal
	public void deleteAnimal(long animalId) {
		animalRepository.deleteById(animalId);
	}
	
	public Page<Animal> findPaginatedAnimal(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.animalRepository.findAll(pageable);
	}
	
	public Page<Animal> findPaginatedAnimalStapan(long stapanId, int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		//tipu de date a stapanId a Animal este Stapan
		Optional<Stapan> stapan = stapanService.getStapan(stapanId);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return animalRepository.findAllByStapanId(stapan, pageable);
	}
	
	
	
	
}
