package kz.kazimpex.sed.barcode.controllers;

import com.mongodb.gridfs.GridFSDBFile;
import kz.kazimpex.common.dto.RestResponseDto;
import kz.kazimpex.sed.barcode.dtos.FileDetailDto;
import kz.kazimpex.sed.barcode.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping(value = "/list/{barcode}")
    public List<FileDetailDto> getNotAttachedByBarcode(@PathVariable String barcode) {
        return fileService.getNotAttachedByBarcode(barcode);
    }


    @GetMapping(value = "/{fileId}")
    public ResponseEntity<InputStreamResource> getById(@PathVariable String fileId) {
        System.out.println("getById is called ==========================''''''''''''''");
        return fileService.findById(fileId);
    }


    @GetMapping(value = "/list")
    public List<FileDetailDto> getFileDetailList() {
        return fileService.getFilesDatesHirarhially();
    }

    @PutMapping(value = "/{fileId}")
    public RestResponseDto deleteFileDetail(@PathVariable String fileId) {
        return fileService.deleteFileDetail(fileId);
    }


}
