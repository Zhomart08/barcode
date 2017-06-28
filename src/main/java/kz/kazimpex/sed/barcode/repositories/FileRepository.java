package kz.kazimpex.sed.barcode.repositories;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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


}
