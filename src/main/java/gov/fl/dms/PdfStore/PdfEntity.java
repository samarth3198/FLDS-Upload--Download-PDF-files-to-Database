package gov.fl.dms.PdfStore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "pdf")
public class PdfEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String fileName;
    @NotNull
    //@Type(type="org.hibernate.type.BinaryType")
    @Column(columnDefinition = "BYTEA")
    private byte[] doc;

    public PdfEntity() {
    }

    public PdfEntity(@NotNull String fileName, @NotNull byte[] doc) {
        this.fileName = fileName;
        this.doc = doc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getDoc() {
        return doc;
    }

    public void setDoc(byte[] doc) {
        this.doc = doc;
    }
}
