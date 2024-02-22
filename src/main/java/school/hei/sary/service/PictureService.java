package school.hei.sary.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.hei.sary.file.BucketComponent;
import school.hei.sary.model.PicturesUTL;
import school.hei.sary.repository.PictureInformationRepository;
import school.hei.sary.repository.model.PictureInformation;

@AllArgsConstructor
@Service
public class PictureService {
  private final PictureInformationRepository repository;
  private final BucketComponent bucketComponent;
  private final Path IMAGE_BUCKET_DIRECTORY = Path.of("image/");

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
  public String uploadAndConvertImageToBlackAndWhite(String id, byte[] file) throws IOException {
    if (file == null) {
      throw new RemoteException("Image file is mandatory");
    }
    String fileEndSuffix = ".jpeg";
    String newFileName = id + fileEndSuffix;
    String blackFileName = id + "-black" + fileEndSuffix;

    File originalFile = File.createTempFile(newFileName, fileEndSuffix);
    File blackTempImageFile = File.createTempFile(blackFileName, fileEndSuffix);
    writeFileFromByteArray(file, originalFile);
    File blackImageFile = convertImageToBlackAndWhite(originalFile, blackTempImageFile);

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

  private File writeFileFromByteArray(byte[] bytes, File file) {
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(bytes);
      return file;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private File convertImageToBlackAndWhite(File originalFile, File outputFile) {
    ImagePlus image = IJ.openImage(originalFile.getPath());
    try {
      ImageConverter converter = new ImageConverter(image);
      converter.convertToGray8();
    } catch (Exception e) {
      throw new RuntimeException("Image file invalid");
    }
    ij.io.FileSaver fileSaver = new ij.io.FileSaver(image);
    fileSaver.saveAsJpeg(outputFile.getPath());
    return outputFile;
  }
}
