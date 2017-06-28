package kz.kazimpex.sed.barcode.services;

import com.aspose.barcode.barcoderecognition.BarCodeReadType;
import com.aspose.barcode.barcoderecognition.BarCodeReader;
import com.aspose.pdf.facades.PdfExtractor;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;
import kz.kazimpex.sed.barcode.repositories.FileRepository;
import org.apache.pdfbox.io.IOUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.common.PDStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */
@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;

    public void uploadFile(MultipartFile file) throws Exception {
        readTextFromPage(file.getInputStream());
        // fileRepository.upload(file);
    }


    public void readTextFromPage(InputStream inputStream) throws Exception {

        try {

            PdfReader reader = new PdfReader(inputStream);
            int n = reader.getNumberOfPages();
            System.out.println("Number of pages : " + n);
            int i = 0;
            while (i < n) {
                String outFile = "src/main/resources/" + 1 + "blank.pdf";
                //System.out.println("Writing " + outFile);
                Document document = new Document(reader.getPageSizeWithRotation(1));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfCopy writer = new PdfCopy(document, baos);
                document.open();
                PdfImportedPage page = writer.getImportedPage(reader, ++i);
                writer.addPage(page);
                document.close();
                writer.close();

                byte[] documentBytes = baos.toByteArray();

                //  convertToFile(documentBytes, i);


                if (i == 2) {
                    parseBarcode(documentBytes);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void convertToFile(byte[] buffer, int i) throws Exception {

        File targetFile = new File("src/main/resources/" + i + "tarFile.pdf");
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }


    public void parseBarcode(byte[] bytes) throws NotFoundException, IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        PdDocumentBarcodeScanner scanner = new PdDocumentBarcodeScanner(bis);
        scanner.scan();
        long endTime = System.currentTimeMillis();



        scanner.displayResults();


    }


}
