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
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.repositories.StapanRepository;

@Service
public class StapanService {

	@Autowired
	private StapanRepository stapanRepository;
	
	//selecteaza cu Email
	public Stapan getStapanByEmail(String email) {
		return stapanRepository.findByEmail(email);
	}
	
	//selecteaza toti stapanii
	public List<Stapan> getAllStapani(){
		List<Stapan> stapani = new ArrayList<>();
		stapanRepository.findAll()
		.forEach(stapani::add);
		return stapani;
	}
	//selecteaza un stapan in functie de id
	public Optional<Stapan> getStapan(long stapanId){
		return stapanRepository.findById(stapanId);
	}
	//adauga Stapan
	public void addStapan(Stapan stapan) {
		stapanRepository.save(stapan);
	}
	//update Stapan
	public void updateStapan(Stapan stapan) {
		stapanRepository.save(stapan);
	}
	//sterge Stapan
	public void deleteStapan(long stapanId) {
		stapanRepository.deleteById(stapanId);
	}
	
	public Page<Stapan> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.stapanRepository.findAll(pageable);
	}
	
	public Page<Stapan> findPaginatedSearch(int pageNo, int pageSize, String sortField, String sortDirection, String field, String text) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		
		if(field.equals("email"))
			return this.stapanRepository.findByEmailContaining(text, pageable);
		if(field.equals("lastname"))
			return this.stapanRepository.findByLastnameContaining(text, pageable);
		if(field.equals("firstname"))
			return this.stapanRepository.findByFirstnameContaining(text, pageable);
		if(field.equals("nrtelefon"))
			return this.stapanRepository.findByNrtelefonContaining(text, pageable);
		
		return null;
	}
	
}
