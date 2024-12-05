package com.dhaval.personal.playground;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dhaval.personal.playground.controller.FileController;
import com.dhaval.personal.playground.services.FileConversionService;
import com.dhaval.personal.playground.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private FileConversionService fileConversionService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFile_ShouldReturnBadRequest_WhenFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<String> response = fileController.uploadFile(file, "PDF", Optional.empty(), Optional.empty());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    void uploadFile_ShouldReturnBadRequest_WhenFormatIsInvalid() {
        when(file.isEmpty()).thenReturn(false);

        ResponseEntity<String> response = fileController.uploadFile(file, "TXT", Optional.empty(), Optional.empty());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    void uploadFile_ShouldReturnOk_WhenFileIsConvertedToPdfSuccessfully() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(fileConversionService.convertToPdf(file)).thenReturn(new File("output.pdf"));

        ResponseEntity<String> response = fileController.uploadFile(file, "PDF", Optional.empty(), Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File converted successfully.", response.getBody());
    }

    @Test
    void uploadFile_ShouldReturnOk_WhenFileIsConvertedToTiffSuccessfully() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(fileConversionService.convertToTiff(file, 75, Optional.empty())).thenReturn(new File("output.tiff"));

        ResponseEntity<String> response = fileController.uploadFile(file, "TIFF", Optional.empty(), Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File converted successfully.", response.getBody());
    }

    @Test
    void uploadFile_ShouldReturnInternalServerError_WhenFileConversionFails() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(fileConversionService.convertToPdf(file)).thenThrow(new IOException());

        ResponseEntity<String> response = fileController.uploadFile(file, "PDF", Optional.empty(), Optional.empty());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("File conversion failed.", response.getBody());
    }
}