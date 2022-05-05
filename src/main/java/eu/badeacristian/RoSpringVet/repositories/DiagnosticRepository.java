package eu.badeacristian.RoSpringVet.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import eu.badeacristian.RoSpringVet.models.Diagnostic;

@Repository
public interface DiagnosticRepository extends CrudRepository<Diagnostic, Long> {

}
