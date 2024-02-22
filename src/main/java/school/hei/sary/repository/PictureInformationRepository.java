package school.hei.sary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.sary.repository.model.PictureInformation;

@Repository
public interface PictureInformationRepository extends JpaRepository<PictureInformation, String> {}
