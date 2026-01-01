package ma.enset.documentservice.repositories;

import ma.enset.documentservice.entities.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {
    List<GeneratedDocument> findByUserId(Long userId);

    Optional<GeneratedDocument> findByReference(String reference);
}
