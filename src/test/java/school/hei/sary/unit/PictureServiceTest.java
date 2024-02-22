package school.hei.sary.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.hei.sary.file.BucketComponent;
import school.hei.sary.repository.PictureInformationRepository;
import school.hei.sary.service.PictureService;

@ExtendWith(MockitoExtension.class)
class PictureServiceTest {

  @Mock private PictureInformationRepository repository;

  @Mock private BucketComponent bucketComponent;

  @InjectMocks private PictureService pictureService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testUploadImageFile() {
    File imageFile = mock(File.class);
    String imageName = "testImage.jpg";

    // Test
    assertThrows(
        RuntimeException.class, () -> pictureService.uploadImageFile(imageFile, imageName));

    // Verify
    //        verify(bucketComponent).upload(eq(imageFile), eq("image/" + imageName));
    //        verify(imageFile).delete();
  }

  //    @Test
  //    void testUploadAndConvertImageToBlackAndWhite() {
  //        // Mocking
  //        String id = "123";
  //        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test
  // data".getBytes());
  //
  //        // Mock directory creation
  //        File tempDir = mock(File.class);
  //        when(tempDir.exists()).thenReturn(false);
  //        when(tempDir.mkdirs()).thenReturn(true);
  //
  //        // Mock repository save
  //        when(repository.save(any())).thenReturn(null);
  //
  //        // Mock bucketComponent.presign
  //        when(bucketComponent.presign(any(), any())).thenReturn(null);
  //
  //        // Test
  //        assertThrows(RuntimeException.class,() -> {
  //            pictureService.uploadAndConvertImageToBlackAndWhite(id, file);
  //        });
  //
  ////        // Verify
  ////        verify(bucketComponent, times(2)).upload(any(), any());
  ////        verify(repository).save(any());
  ////        verify(bucketComponent, times(2)).presign(any(), any());
  //    }
  //
  //    @Test
  //    void testGetPicturesURL() {
  //        // Mocking
  //        String pictureInformationId = "456";
  //        PictureInformation pictureInformation = mock(PictureInformation.class);
  //
  //        when(repository.findById(any())).thenReturn(Optional.of(pictureInformation));
  //        when(bucketComponent.presign(any(), any())).thenReturn(null);
  //
  //        // Test
  //        assertThrows(RuntimeException.class,() -> {
  //            PicturesUTL picturesUTL = pictureService.getPicturesURL(pictureInformationId);
  //            assertNotNull(picturesUTL);
  //        });
  //
  //        // Verify
  ////        verify(repository).findById(eq(pictureInformationId));
  ////        verify(bucketComponent, times(2)).presign(any(), any());
  //    }
}
