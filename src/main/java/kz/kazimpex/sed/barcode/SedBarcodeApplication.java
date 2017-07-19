package kz.kazimpex.sed.barcode;

import kz.kazimpex.sed.barcode.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SedBarcodeApplication {


    public static void main(String[] args) {
        SpringApplication.run(SedBarcodeApplication.class, args);
        System.out.println("============================ Barcode project is started ===============================");
    }
}
