package com.sparkle.demo.ibannamecheckblockingimpl.web.controller;

import com.sparkle.demo.ibannamecheckblockingimpl.service.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckRestController {

    private final FileMapper fileMapper;

    @PostMapping(value = "/upload-pain-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> initiatePaymentInterchange(HttpServletRequest request) {

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Not a multipart file upload");
        }

        ServletFileUpload upload = new ServletFileUpload();

        try {
            FileItemIterator fileIterator = upload.getItemIterator(request);

            if (!fileIterator.hasNext()) {
                throw new IllegalArgumentException("File upload request is incomplete");
            }

            while (fileIterator.hasNext()) {
                FileItemStream item = fileIterator.next();
                InputStream stream = item.openStream();

                if (!item.isFormField() && item.getFieldName().equals("file")) {
                    long fileSize = request.getContentLengthLong();
                    String fileName = item.getName();

                    log.info("File Size : {} and File Name : {}", fileSize, fileName);

                    stream = createByteArrayInputStream(stream);
                    String string = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
                    return new ResponseEntity<>(string, HttpStatus.CREATED);
                }
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream createByteArrayInputStream(InputStream stream) throws IOException {
        return new ByteArrayInputStream(stream.readAllBytes());
    }

}
