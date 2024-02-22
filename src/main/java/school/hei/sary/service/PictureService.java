package school.hei.sary.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.file.BucketComponent;
import school.hei.sary.model.PicturesUTL;
import school.hei.sary.repository.PictureInformationRepository;
import school.hei.sary.repository.model.PictureInformation;

@AllArgsConstructor
@Service
public class PictureService {
  private final PictureInformationRepository repository;
  private final BucketComponent bucketComponent;
  private final Path TEMP_IMAGE_DIRECTORY = Path.of("/sary-temp");
  private final Path IMAGE_BUCKET_DIRECTORY = Path.of("image/");
  private final String lambdaWorkingDirectory = "/var/task/";

  @Transactional
  public void uploadImageFile(File imageFile, String imageName) {
    String bucketKey = IMAGE_BUCKET_DIRECTORY + imageName;
    bucketComponent.upload(imageFile, bucketKey);
    boolean isDelete = imageFile.delete();
    if (!isDelete) {
      throw new RuntimeException("file " + bucketKey + " is not deleted.");
    }
  }

  @Transactional
  public String uploadAndConvertImageToBlackAndWhite(String id, MultipartFile file) {
    if (file == null) {
      throw new RuntimeException("Image file is mandatory");
    }
    try {
      Path directoryPath =
          //          Paths.get(System.getProperty("user.dir"), TEMP_IMAGE_DIRECTORY.toString());
          Paths.get(lambdaWorkingDirectory, TEMP_IMAGE_DIRECTORY.toString());

      if (!Files.exists(directoryPath)) {
        Files.createDirectories(directoryPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create directory");
    }
    String newFileName = id + "." + getFileExtension(file);
    String blackFileName = id + "-black." + getFileExtension(file);

    Path filePath = getFilePathStartWithUser(TEMP_IMAGE_DIRECTORY, newFileName);
    writeFileFromMultipart(file, filePath);

    File blackImageFile = convertImageToBlackAndWhite(filePath.toString(), blackFileName);

    File originalFile = filePath.toFile();
    uploadImageFile(originalFile, newFileName);
    uploadImageFile(blackImageFile, blackFileName);
    PictureInformation toSave =
        PictureInformation.builder()
            .id(id)
            .originalBucketKey(IMAGE_BUCKET_DIRECTORY + newFileName)
            .blackAndWhiteBucketKey(IMAGE_BUCKET_DIRECTORY + blackFileName)
            .build();
    repository.save(toSave);
    return bucketComponent
        .presign(IMAGE_BUCKET_DIRECTORY + blackFileName, Duration.ofHours(1))
        .toString();
  }

  public PicturesUTL getPicturesURL(String pictureInformationId) {
    PictureInformation pictureInformation =
        repository
            .findById(pictureInformationId)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Picture information with id " + pictureInformationId + " does not exist"));

    String originalPictureURL =
        bucketComponent
            .presign(pictureInformation.getOriginalBucketKey(), Duration.ofHours(1))
            .toString();
    String backAndWhitePictureURL =
        bucketComponent
            .presign(pictureInformation.getBlackAndWhiteBucketKey(), Duration.ofHours(1))
            .toString();

    System.out.println(originalPictureURL);
    System.out.println(backAndWhitePictureURL);
    return PicturesUTL.builder()
        .originalImageURL(originalPictureURL)
        .blackAndWhiteImageURL(backAndWhitePictureURL)
        .build();
  }

  private File convertImageToBlackAndWhite(String imagePath, String blackFileName) {
    ImagePlus image = IJ.openImage(imagePath);
    try {
      ImageConverter converter = new ImageConverter(image);
      converter.convertToGray8();
    } catch (Exception e) {
      throw new RuntimeException("Image file invalid");
    }
    Path blackFilePath = getFilePathStartWithUser(TEMP_IMAGE_DIRECTORY, blackFileName);
    ij.io.FileSaver fileSaver = new ij.io.FileSaver(image);
    fileSaver.saveAsJpeg(blackFilePath.toString());
    return blackFilePath.toFile();
  }

  private void writeFileFromMultipart(MultipartFile file, Path dir) {
    try {
      file.transferTo(dir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Path getFilePathStartWithUser(Path directory, String filename) {
    //    Path userDirectory = Paths.get(System.getProperty("user.dir"), directory.toString());
    Path userDirectory = Paths.get(lambdaWorkingDirectory, directory.toString());

    System.out.println(userDirectory.toString());
    return Paths.get(userDirectory.toString(), filename);
  }

  private String getFileExtension(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null ? contentType.substring(contentType.lastIndexOf('/') + 1) : "";
  }
}
