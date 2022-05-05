package eu.badeacristian.RoSpringVet.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import eu.badeacristian.RoSpringVet.models.Stapan;

@Repository
public interface StapanRepository extends JpaRepository<Stapan, Long> {
	
	Stapan findByEmail(String email);
	
	Page<Stapan> findByEmailContaining(String email, Pageable pageable);
	Page<Stapan> findByFirstnameContaining(String email, Pageable pageable);
	Page<Stapan> findByLastnameContaining(String email, Pageable pageable);
	Page<Stapan> findByNrtelefonContaining(String email, Pageable pageable);
	
	
}
