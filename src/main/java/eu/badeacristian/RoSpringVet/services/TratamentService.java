package eu.badeacristian.RoSpringVet.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Tratament;
import eu.badeacristian.RoSpringVet.repositories.TratamentRepository;

@Service
public class TratamentService {
	
	@Autowired
	private TratamentRepository tratamentRepository;
	
	@Autowired
	private AnimalService animalService;

	//selecteaza un tratament in functie de id
	public Optional<Tratament> getTratament(long tratamentId){
		return tratamentRepository.findById(tratamentId);
	}
	//adauga tratament
	public void addTratament(Tratament tratament) {
		tratamentRepository.save(tratament);
	}
	//update tratament
	public void updateTratament(Tratament tratament) {
		tratamentRepository.save(tratament);
	}
	//sterge tratament
	public void deleteTratament(Tratament tratament) {
		tratamentRepository.delete(tratament);
	}
	
	public Page<Tratament> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.tratamentRepository.findAll(pageable);
	}
	
	
	public Page<Tratament> findPaginatedAnimal(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Animal> animal = animalService.getAnimal(id);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.tratamentRepository.findAllByAnimalId(animal, pageable);
	}
	
	
	
}
