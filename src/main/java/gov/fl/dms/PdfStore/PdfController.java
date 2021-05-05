package gov.fl.dms.PdfStore;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PdfController {
    @PersistenceContext(unitName = "CRUD")
    EntityManager em;

    //Saving the pdf into the database
    public void savePdf(PdfEntity pdfEntity){
        System.out.println(pdfEntity);
        em.persist(pdfEntity);
    }

    //Getting all pdf files from the database
    public List<PdfEntity> getAll(){
        Query query =em.createQuery("select p from PdfEntity p");
        return query.getResultList();
    }

    //Delete files from the database
    public void deletePdf(PdfEntity pdfEntity){
        if(em.contains(pdfEntity)){
            em.remove(pdfEntity);
        }
        else{
            em.remove(em.merge(pdfEntity));
        }

    }
}
