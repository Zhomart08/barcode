package kz.kazimpex.sed.barcode.repositories;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */
@Service
public class FileRepository {

    @Autowired
    GridFsTemplate gridFsTemplate;


    @Transactional
    public GridFSFile upload(MultipartFile multipartFile) {
        try {
            return gridFsTemplate.store(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType());
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    @Transactional
    public GridFSFile upload(InputStream inputStream) {
        try {
            return gridFsTemplate.store(inputStream, "fileName", "application/pdf");
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


}
