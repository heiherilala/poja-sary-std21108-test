package school.hei.sary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PicturesUTL {
  private String blackAndWhiteImageURL;
  private String originalImageURL;
}
