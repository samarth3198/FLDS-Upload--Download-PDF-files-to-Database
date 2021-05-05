package gov.fl.dms.PdfStore;


//import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;


@Named(value = "pdfbean")
@RequestScoped
public class PdfStoreBean {
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    private Part uploadedFile; //File gets stored temporarily in server's memory
    private String fileName;
    private byte[] fileContents;


    PdfEntity singlePdf;
    List<PdfEntity> files;

    @Inject
    private PdfController pdfController;

    @PostConstruct
    public void init(){
        files = pdfController.getAll();

    }

    //Upload Method, file uploaded through Web browser gets passed here and from here it saves file to the database
    public void upload() {
        fileName = Paths.get(uploadedFile.getSubmittedFileName()).getFileName().toString();       //Getting the filename

        try(InputStream input = uploadedFile.getInputStream()) {            //Creating input stream

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte [] data = new byte[input.available()];                     //input.available() calculates total length of the input stream

            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            fileContents =  buffer.toByteArray();                           //Converted to Byte array

            // make new pdf object
            singlePdf = new PdfEntity(fileName, fileContents);

            input.close();
            pdfController.savePdf(singlePdf);                               //Controller method call to save file into the database

            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("viewFiles.xhtml");                                //Redirects to viewFiles page after uploading a new file
        }

        catch (IOException e){
            System.out.println("IOException Caught: " + e.getMessage());
        }
    }

    //Download Pdf Method is called when user clicks on download button from web browser
    public void downloadPDF(PdfEntity pdfEntity) throws IOException {

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

            BufferedOutputStream output = null;             //Setting up the Buffered Output Stream for writing bytes content to file

            try {
                byte[] rawFile = pdfEntity.getDoc();        //Getting the binary contents of a file stored in the database into byte array
                // Init servlet response.
                response.reset();
                response.setHeader("Content-Type", "application/text/plain");       //Setting header to text/plain for all text files like pdf, docx, txt
                response.setHeader("Content-Length", String.valueOf(rawFile.length));
                response.setHeader("Content-Disposition", "inline; filename=\"" + pdfEntity.getFileName() + "\"");  //Downloads file as a original filename

                output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
                output.write(rawFile, 0, rawFile.length);

                // Finalize task.
                output.flush();
            } finally {
                // Gently close streams.
                close(output);
            }

            // Inform JSF that it doesn't need to handle response.
            // This is very important, otherwise you will get the following exception in the logs:
            // java.lang.IllegalStateException: Cannot forward after response has been committed.
            facesContext.responseComplete();
        }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it. It may be useful to
                // know that this will generally only be thrown when the client aborted the download.
                e.printStackTrace();
            }
        }
    }

    public List<PdfEntity> getAllPdfFiles(){            //Get All method for reading files saved in the database
        return pdfController.getAll();
    }

    public String delete(PdfEntity pdfEntity){            // Delete method for deleting a file from a database
        pdfController.deletePdf(pdfEntity);
        return "viewFiles?faces-redirect=true";
    }


    //Getter and Setters
    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContents() {
        return fileContents;
    }

    public void setFileContents(byte[] fileContents) {
        this.fileContents = fileContents;
    }

    public List<PdfEntity> getFiles() {
        return files;
    }

    public void setFiles(List<PdfEntity> files) {
        this.files = files;
    }

}
