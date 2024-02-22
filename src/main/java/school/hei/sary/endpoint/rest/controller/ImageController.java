package school.hei.sary.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.model.PicturesUTL;
import school.hei.sary.service.PictureService;

@RestController
@AllArgsConstructor
public class ImageController {
  private final PictureService service;

  @PutMapping(value = "/blacks/{id}")
  public String getBlackAndWhiteImage(
      @PathVariable(name = "id") String id,
      @RequestParam(value = "file", required = false) MultipartFile file) {
    return service.uploadAndConvertImageToBlackAndWhite(id, file);
  }

  @GetMapping(value = "/blacks/{id}")
  public PicturesUTL getImageURL(@PathVariable(name = "id") String id) {
    return service.getPicturesURL(id);
  }
}
