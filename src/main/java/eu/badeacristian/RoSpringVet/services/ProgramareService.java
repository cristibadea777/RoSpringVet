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
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.repositories.ProgramareRepository;

@Service
public class ProgramareService {

	@Autowired
	private ProgramareRepository programareRepository;
	
	@Autowired
	private StapanService stapanService;
	
	@Autowired
	private AnimalService animalService;
	
	//selecteaza toate programarile
	public List<Programare> getAllProgramari(){
		List<Programare> programari = new ArrayList<>();
		programareRepository.findAll()
		.forEach(programari::add);
		return programari;
	}
	//selecteaza o programare in functie de id
	public Optional<Programare> getProgramare(long programareId){
		return programareRepository.findById(programareId);
	}
	//adauga programare
	public void addProgramare(Programare programare) {
		programareRepository.save(programare);
	}
	//update programare
	public void updateProgramare(Programare programare) {
		programareRepository.save(programare);
	}
	//sterge programare
	public void deleteProgramare(Programare programare) {
		programareRepository.delete(programare);
	}
	
	
	public Page<Programare> findPaginatedProgramare(int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAll(pageable);
	}
	
	
	public void deleteProgramare(long id) {
		programareRepository.deleteById(id);
	}
	
	public Page<Programare> findPaginatedStapan(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Stapan> stapan = stapanService.getStapan(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAllByStapanId(stapan, pageable);
	}
	
	public Page<Programare> findPaginatedAnimal(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Animal> animal = animalService.getAnimal(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAllByAnimalId(animal, pageable);
	}
	
	public Page<Programare> findPaginatedData(long id, int pageNo, int pageSize, String sortField, String sortDir, String data) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAllByDate(data, pageable);
	}
	
	public Page<Programare> findPaginatedDataNeconfirmata(long id, int pageNo, int pageSize, String sortField, String sortDir, String data) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAllByDateNeconfirmata(data, pageable);
	}
	
	public Page<Programare> findPaginatedStapanNeconfirmata(long id, int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Optional<Stapan> stapan = stapanService.getStapan(id);
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.programareRepository.findAllByStapanIdNeconfirmata(stapan, pageable);
	}
	
}
