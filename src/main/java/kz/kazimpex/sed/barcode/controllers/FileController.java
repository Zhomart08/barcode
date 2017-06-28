package kz.kazimpex.sed.barcode.controllers;

import kz.kazimpex.sed.barcode.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */

@RestController
@RequestMapping(value = "file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping(value = "/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println("Trying to upload file...");
        fileService.uploadFile(file);
    }


}
