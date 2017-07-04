package kz.kazimpex.sed.barcode.controllers;

import com.mongodb.gridfs.GridFSDBFile;
import kz.kazimpex.common.dto.RestResponseDto;
import kz.kazimpex.sed.barcode.dtos.FileDetailDto;
import kz.kazimpex.sed.barcode.repositories.FileRepository;
import kz.kazimpex.sed.barcode.services.FileService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */

@RestController
@RequestMapping(value = "/a/file")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepository;

    @PostMapping(value = "/upload/list/{fileType}/{elementId}")
    public List<FileDetailDto> uploadFile(@PathVariable String fileType,
                                          @PathVariable String elementId,
                                          MultipartHttpServletRequest request) throws Exception {
        Iterator<String> iterator = request.getFileNames();
        List<MultipartFile> fileList = new ArrayList<>();
        while (iterator.hasNext()) {
            MultipartFile file = request.getFile(iterator.next());
            //do something with the file.....
            if (file != null) {
                fileList.add(file);
            }
        }
        fileService.uploadFileList(fileList);
        return fileService.getFileDetailList();
    }

    @GetMapping(value = "/list/{barcode}")
    public List<FileDetailDto> getNotAttachedByBarcode(@PathVariable String barcode) {
        return fileService.getNotAttachedByBarcode(barcode);
    }


    @GetMapping(value = "/{fileId}")
    public ResponseEntity<InputStreamResource> getById(@PathVariable String fileId) {
        return fileService.findById(fileId);
    }


    @GetMapping(value = "/list/{fileType}/{elementId}")
    public List<FileDetailDto> getFileDetailList() {
        return fileService.getFileDetailList();
    }

    @PutMapping(value = "/{fileId}")
    public RestResponseDto deleteFileDetail(@PathVariable String fileId) {
        return fileService.deleteFileDetail(fileId);
    }

    @GetMapping(value = "/imageById/{fileId}")
    public HttpEntity<byte[]> downloadFileById(@PathVariable String fileId) {
        return fileService.downloadFileById(fileId);
    }


}
