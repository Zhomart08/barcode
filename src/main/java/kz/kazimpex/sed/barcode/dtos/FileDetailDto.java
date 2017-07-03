package kz.kazimpex.sed.barcode.dtos;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;

import java.util.Date;
import java.util.List;

/**
 * Created by Жолдасбеков Жомарт on 29.06.2017.
 */
public class FileDetailDto {
    private String id;
    private String name;
    private Long size;
    private String type;
    private Date createdDate;
    private boolean attached;
    private String barcode;
    private String parentId;
    private List<FileDetailDto> children;

    public FileDetailDto() {
    }


    public FileDetailDto(GridFSDBFile file) {
        this.id = file.getId().toString();
        this.name = file.getFilename();
        this.size = file.getLength();
        this.type = file.getContentType();

        BasicDBObject metaData = (BasicDBObject) file.get("metadata");
        if (metaData == null) return;

        if (metaData.containsKey("parent_id")) {
            this.parentId = metaData.get("parent_id").toString();
        }
        if (metaData.containsKey("barcode")) {
            this.barcode = (String) metaData.get("barcode");
        }
        if (metaData.containsKey("created_date")) {
            this.createdDate = (Date) metaData.get("created_date");
        }
        if (metaData.containsKey("attached")) {
            this.attached = (boolean) metaData.get("attached");
        }


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<FileDetailDto> getChildren() {
        return children;
    }

    public void setChildren(List<FileDetailDto> children) {
        this.children = children;
    }
}
