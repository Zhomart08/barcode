package kz.kazimpex.sed.barcode.repositories;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 27.06.2017.
 */
@Service
public class FileRepository {

    @Autowired
    GridFsTemplate gridFsTemplate;




    public List<GridFSDBFile> getAllFiles() {
        return gridFsTemplate.find(null);
    }


    public List<GridFSDBFile> getAllParents() {
        return gridFsTemplate.find(new Query(Criteria.where("metadata.parent_id").is(null)));

    }


    public List<GridFSDBFile> getByParentId(String parentId) {
        return gridFsTemplate.find(new Query(Criteria.where("metadata.parent_id").is(parentId)));

    }

    public List<GridFSDBFile> getNotAttachedByBarcode(String barcode) {
        return gridFsTemplate
                .find(new Query(Criteria.where("metadata.barcode").is(barcode)
                        .andOperator(Criteria.where("metadata.attached").is(false))));

    }

    public GridFSDBFile findById(String fileID) {
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileID)));
    }

    @Transactional
    public boolean delete(String fileID) {
        try {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileID))));
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }


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


    @Transactional
    public GridFSFile upload(InputStream inputStream, DBObject extra, String fileName) {
        try {
            return gridFsTemplate.store(inputStream, fileName, "application/pdf", extra);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


}
