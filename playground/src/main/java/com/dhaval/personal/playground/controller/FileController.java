package com.dhaval.personal.playground.controller;

import com.dhaval.personal.playground.entity.Job;
import com.dhaval.personal.playground.services.FileConversionService;
import com.dhaval.personal.playground.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private JobService jobService;

    @Autowired
    private FileConversionService fileConversionService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format,
            @RequestParam("quality") Optional<Integer> quality,
            @RequestParam(value = "metadata", required = false) Optional<String> metadata
    ) {
        if (file.isEmpty() || !(format.equalsIgnoreCase("PDF") || format.equalsIgnoreCase("TIFF"))) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }

        Job job = new Job();
        job.setStatus("ACCEPTED");
        jobService.saveJob(job);

        try {
            File outputFile;
            if (format.equalsIgnoreCase("PDF")) {
                outputFile = fileConversionService.convertToPdf(file);
            } else {
                int tiffQuality = quality.orElse(75);
                outputFile = fileConversionService.convertToTiff(file, tiffQuality, metadata);
            }

            job.setStatus("COMPLETED");
            jobService.saveJob(job);

            return new ResponseEntity<>("File converted successfully.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File conversion failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}