package eu.badeacristian.RoSpringVet.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.badeacristian.RoSpringVet.models.Angajat;
import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Stapan;
import eu.badeacristian.RoSpringVet.models.Vizita;

@Repository
public interface VizitaRepository extends JpaRepository<Vizita, Long> {

	
	Page<Vizita> findAllByAnimalId(@Param("animalId") Optional<Animal> animal, Pageable pageable);
	Page<Vizita> findAllByAngajatId(@Param("angajatId") Optional<Angajat> angajat, Pageable pageable);
	Page<Vizita> findAllByStapanId(@Param("stapanId") Optional<Stapan> stapan, Pageable pageable);
	
}
