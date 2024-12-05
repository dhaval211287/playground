package com.dhaval.personal.playground.services;

import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.itextpdf.text.Image;


import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
@Service
public class FileConversionService {

    public File convertToPdf(MultipartFile file) throws IOException {
        System.out.println("Starting PDF conversion");
        File pdfFile = new File("output.pdf");

        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            // Check if the file is an image and add it to the PDF accordingly
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                // If the file is an image, add it to the PDF as an image
                Image image = Image.getInstance(file.getBytes());
                image.scaleToFit(500, 700); // Scale image to fit the page
                document.add(image);
            } else {
                // If the file is not an image, treat it as text
                Path tempFile = Files.createTempFile("input", file.getOriginalFilename());
                file.transferTo(tempFile);

                try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        String content = new String(buffer, 0, bytesRead);
                        document.add(new Paragraph(content));
                    }
                }
            }

            document.close();
            System.out.println("PDF conversion completed");
        } catch (DocumentException e) {
            System.err.println("Error during PDF conversion: " + e.getMessage());
            throw new IOException("PDF conversion failed", e);
        }

        return pdfFile;
    }


    public File convertToTiff(MultipartFile file, int quality, Optional<String> metadata) throws IOException {
        System.out.println("Starting TIFF conversion with quality: " + quality);
        File tiffFile = new File("output.tiff");

        try {
            // Write the content of the MultipartFile to a BufferedImage
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            // Create a TiffOutputSet to add metadata
            TiffOutputSet tiffOutputSet = new TiffOutputSet();

            if (metadata.isPresent()) {
                // Add metadata if provided
                TiffOutputDirectory exifDirectory = tiffOutputSet.getOrCreateExifDirectory();
                exifDirectory.add(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION, metadata.get());
            }

            // Save the image as TIFF with specified quality (compression)
            Imaging.writeImage(bufferedImage, tiffFile, ImageFormats.TIFF, null);

            System.out.println("TIFF conversion completed with quality: " + quality);
        } catch (Exception e) {
            System.err.println("Error during TIFF conversion: " + e.getMessage());
            throw new IOException("TIFF conversion failed", e);
        }
        return tiffFile;
    }

}