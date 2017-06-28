package kz.kazimpex.sed.barcode.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 28.06.2017.
 */
public class PdDocumentBarcodeScanner {

    PDDocument pdDocument;
    int maximumBlankPixelDelimiterCount = 20;
    List<PdPageBarcodeScanner> pageScannerList;

    public PdDocumentBarcodeScanner(InputStream inputStream) throws IOException {
        //FileInputStream pdfInputStream = new FileInputStream(file);
        this.pdDocument = PDDocument.load(inputStream);
        inputStream.close();

        pageScannerList = new ArrayList<PdPageBarcodeScanner>();
    }


    public void scan() throws IOException {
        List<PDPage> pages = pdDocument.getDocumentCatalog().getAllPages();
        int pageNumber = 0;


        for (PDPage page : pages) {


            PdPageBarcodeScanner pageScanner
                    = new PdPageBarcodeScanner(
                    page,
                    pageNumber,
                    this.maximumBlankPixelDelimiterCount);

            pageScannerList.add(pageScanner);
            pageScanner.scan();
            pageNumber++;
        }

    }


    public void displayResults() {

        for (PdPageBarcodeScanner pageScanner : pageScannerList) {
            pageScanner.displayResults();
        }
    }

}
