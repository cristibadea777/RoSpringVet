package eu.badeacristian.RoSpringVet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.badeacristian.RoSpringVet.models.Angajat;

@Repository
public interface AngajatRepository extends JpaRepository<Angajat, Long>{

	Angajat findByEmail(String email);
	
}
