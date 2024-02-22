package school.hei.sary.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PictureInformation {
  @Id private String id;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant creationDatetime;

  @UpdateTimestamp private Instant updateDatetime;
  private String blackAndWhiteBucketKey;
  private String originalBucketKey;

  public Boolean HaveBlackAndWhiteImage() {
    return !blackAndWhiteBucketKey.isEmpty();
  }
  ;
}
