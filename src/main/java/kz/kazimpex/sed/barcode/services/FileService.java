package kz.kazimpex.sed.barcode.services;

import com.google.zxing.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import kz.kazimpex.common.dto.RestResponseDto;
import kz.kazimpex.sed.barcode.dtos.FileDetailDto;
import kz.kazimpex.sed.barcode.repositories.FileRepository;
import kz.kazimpex.sed.barcode.scanners.PdDocumentBarcodeScanner;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */
@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;


    public HttpEntity<byte[]> downloadFileById(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GridFSDBFile file = fileRepository.findById(fileId);
            IOUtils.copy(file.getInputStream(), out);
            byte[] photo = out.toByteArray();

            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(photo.length);
            return new HttpEntity<byte[]>(photo, headers);
        } catch (Exception e) {

        }
        return new HttpEntity<byte[]>(null, headers);
    }

    @Transactional
    public List<FileDetailDto> uploadFileList(List<MultipartFile> fileList) throws Exception {
        List<FileDetailDto> fileDetailList = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            uploadFile(fileList.get(i));
        }
        return fileDetailList;
    }


    public List<FileDetailDto> getFilesDatesHirarhially() {
        List<GridFSDBFile> parents = fileRepository.getAllParents();
        List<FileDetailDto> fileDetailList = new ArrayList<>();
        for (GridFSDBFile file : parents) {
            FileDetailDto dto = new FileDetailDto(file);
            dto.setChildren(convertToDtoList(fileRepository.getByParentId(file.getId().toString())));
            fileDetailList.add(dto);
        }
        return fileDetailList;
    }

    @Transactional
    public RestResponseDto deleteFileDetail(String fileID) {
        if (fileRepository.delete(fileID)) {
            return new RestResponseDto().createSuccessDeleted();
        }
        return new RestResponseDto().createErrorDeleted();
    }


    public List<FileDetailDto> getNotAttachedByBarcode(String barcode) {
        return convertToDtoList(fileRepository.getNotAttachedByBarcode(barcode));
    }

    public ResponseEntity<InputStreamResource> findById(String fileId) {
        GridFSDBFile file = fileRepository.findById(fileId);
        file.getMetaData().put("attached", true);
        file.save();
        return ResponseEntity.ok()
                .contentLength(file.getLength())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header("filename", file.getFilename())
                .body(new InputStreamResource(file.getInputStream()));
    }

    public List<FileDetailDto> getFileDetailList() {
        return convertToDtoList(fileRepository.getAllFiles());
    }

    @Transactional
    public void uploadFile(MultipartFile file) throws Exception {
        GridFSFile gridFSFile = fileRepository.upload(file);
        splitPdfByBarcode(file.getInputStream(), gridFSFile.getId().toString());
    }

    public void uploadSeperatedFile(byte[] source, DBObject dbObject, String fileName) {
        ByteArrayInputStream bis = new ByteArrayInputStream(source);
        fileRepository.upload(bis, dbObject, fileName);
    }


    public void splitPdfByBarcode(InputStream inputStream, String parentId) throws Exception {
        try {
            PdfReader reader = new PdfReader(inputStream);
            Document document = new Document(reader.getPageSizeWithRotation(1));

            List<InputStream> streamOfPDFFiles = null;

            String lastBarcode = null;

            for (int i = 0; i < reader.getNumberOfPages(); i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfCopy writer = new PdfCopy(document, baos);
                document.open();
                PdfImportedPage page = writer.getImportedPage(reader, i + 1);
                writer.addPage(page);
                document.close();
                writer.close();

                byte[] documentBytes = baos.toByteArray();
                String barcode = recognizeBarcode(documentBytes);
                System.out.println("=====barcode: " + barcode);

                if (barcode != null) {
                    if (streamOfPDFFiles != null) {
                        byte[] concatedPDFsBytes = concatPDFs(streamOfPDFFiles, false, i);
                        DBObject dbObject = generateDBObject(parentId, lastBarcode);
                        uploadSeperatedFile(concatedPDFsBytes, dbObject, lastBarcode);
                        convertToFile(concatedPDFsBytes, i);
                    }

                    lastBarcode = barcode;
                    streamOfPDFFiles = new ArrayList();
                    streamOfPDFFiles.add(new ByteArrayInputStream(documentBytes));

                } else {
                    streamOfPDFFiles.add(new ByteArrayInputStream(documentBytes));
                }

                if (i == reader.getNumberOfPages() - 1) {

                    byte[] concatedPDFsBytes = concatPDFs(streamOfPDFFiles, false, i);
                    DBObject dbObject = generateDBObject(parentId, lastBarcode);
                    uploadSeperatedFile(concatedPDFsBytes, dbObject, lastBarcode);
                    convertToFile(concatedPDFsBytes, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public DBObject generateDBObject(String parentId, String barcode) {
        DBObject extra = new BasicDBObject();
        extra.put("parent_id", parentId);
        extra.put("barcode", barcode);
        extra.put("created_date", new java.util.Date());
        extra.put("attached", false);
        return extra;


    }

    private FileDetailDto convertToDtoList(GridFSDBFile gridFSDBFile, List<FileDetailDto> children) {
        List<FileDetailDto> fileDetailList = new ArrayList<>();
        FileDetailDto dto = new FileDetailDto(gridFSDBFile);
        dto.setChildren(children);
        fileDetailList.add(dto);
        return dto;
    }

    private List<FileDetailDto> convertToDtoList(List<GridFSDBFile> gridFSDBFilesList) {
        List<FileDetailDto> fileDetailList = new ArrayList<>();
        for (GridFSDBFile file : gridFSDBFilesList) {
            fileDetailList.add(new FileDetailDto(file));
        }
        return fileDetailList;
    }


    public String recognizeBarcode(byte[] bytes) throws NotFoundException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        PdDocumentBarcodeScanner scanner = new PdDocumentBarcodeScanner(bis);
        scanner.scan();
        return scanner.getBarcode();
    }

    public void convertToFile(byte[] buffer, int i) throws Exception {
        File targetFile = new File("src/main/resources/" + i + "tarFile.pdf");
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
        outStream.close();
    }


    public byte[] concatPDFs(List<InputStream> streamOfPDFFiles,
                             boolean paginate, int i) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                        + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return outputStream.toByteArray();
    }
}



