package eu.badeacristian.RoSpringVet.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Tratament;

@Repository
public interface TratamentRepository extends JpaRepository<Tratament, Long> {

	Page<Tratament> findAll(Pageable pageable);
	
	

	//Cu aceasta metoda vom gasi toate tratamentele active ale unui anumit animal, dand ca parametru ID-ul animalului.
	//Tratamente active adica data lor de sfarsit este mai mare decat data curenta
	@Query(value = "SELECT * FROM Tratament WHERE tratament.animal_id = ?1 AND datasfarsit > curdate()", nativeQuery = true)
	Page<Tratament> findAllByAnimalId(@Param("animalId") Optional<Animal> animal, Pageable pageable);
	
}
