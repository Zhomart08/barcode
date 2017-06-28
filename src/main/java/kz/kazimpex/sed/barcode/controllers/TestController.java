package kz.kazimpex.sed.barcode.controllers;

import kz.kazimpex.sed.barcode.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */
@RestController
@RequestMapping(value = "test")
public class TestController {

    @Autowired
    FileService fileService;

    @GetMapping(value = "/test")
    public void testMethod() {
        System.out.println("testMethod is called.......");

    }

    @PostMapping(value = "/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println("uploadFile ByType");
        fileService.uploadFile(file);
    }


}
