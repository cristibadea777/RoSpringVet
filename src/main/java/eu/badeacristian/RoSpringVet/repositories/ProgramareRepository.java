package eu.badeacristian.RoSpringVet.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.badeacristian.RoSpringVet.models.Animal;
import eu.badeacristian.RoSpringVet.models.Programare;
import eu.badeacristian.RoSpringVet.models.Stapan;

@Repository
public interface ProgramareRepository extends JpaRepository<Programare, Long> {

	

	//toti stapanii, cautare dupa data
	@Query(value = "SELECT * FROM Programare WHERE date LIKE %?1% AND stare='confirmata'", nativeQuery = true)
	Page<Programare> findAllByDate(@Param ("date") String date, Pageable pageable);

	//stapan neconfirmate cautare data
	@Query(value = "SELECT * FROM Programare WHERE date LIKE %?1% AND stare='neconfirmata'", nativeQuery = true)
	Page<Programare> findAllByDateNeconfirmata(@Param ("date") String date, Pageable pageable);
	
	//stapan neconfirmate
	@Query(value = "SELECT * FROM Programare WHERE stapan_id = ?1 AND stare='neconfirmata'", nativeQuery = true)
	Page<Programare> findAllByStapanIdNeconfirmata(@Param("stapanId") Optional<Stapan> stapan, Pageable pageable);
	
	//stapan confirmate
	@Query(value = "SELECT * FROM Programare WHERE stapan_id = ?1 AND stare='confirmata'", nativeQuery = true)
	Page<Programare> findAllByStapanId(@Param("stapanId") Optional<Stapan> stapan, Pageable pageable);
	
	//animal confirmate
	@Query(value = "SELECT * FROM Programare WHERE animal_id = ?1 AND stare='confirmata'", nativeQuery = true)
	Page<Programare> findAllByAnimalId(@Param("animalId") Optional<Animal> animal, Pageable pageable);
	
	
	//SELECT DISTINCT date FROM appvet1.programare 
	//WHERE Month(date) = Month('2021-08-01')
	//AND Day(date) = dayofmonth('2021-08-24')
	//ORDER BY date;
	
}
