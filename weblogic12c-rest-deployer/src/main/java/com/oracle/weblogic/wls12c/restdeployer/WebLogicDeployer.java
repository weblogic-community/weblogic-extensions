package com.oracle.weblogic.wls12c.restdeployer;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author bruno.borges@oracle.com
 */
@Path("deployer")
@RolesAllowed({"Administrators"})
public class WebLogicDeployer {

    /**
     * POST method for updating or creating an instance of WebLogicDeployer
     *
     * @param inputStream
     * @param fileDetail
     * @param args
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response deploy(@FormDataParam("args") String args, @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        Logger.getLogger(WebLogicDeployer.class.getName()).log(Level.INFO, "Arguments: {0}", args);

        int exitValue = -1;
        File tempFile = null;
        try {
            File tempDir = createTemporaryDirectory();
            tempFile = new File(tempDir, fileDetail.getFileName());
            writeTempFile(inputStream, tempFile);

            // invokes WebLogic Deployer class
            exitValue = Runtime.getRuntime().exec("java weblogic.Deployer " + args + " " + tempFile.getAbsolutePath()).waitFor();

            final File realTempFile = tempFile;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                        realTempFile.delete();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WebLogicDeployer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        } catch (IOException ex) {
            Logger.getLogger(WebLogicDeployer.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).build();
        } catch (InterruptedException ex) {
            Logger.getLogger(WebLogicDeployer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(exitValue == 0 ? 200 : 500).entity("File received at: " + tempFile).build();
    }

    private void writeTempFile(InputStream uploadedInputStream,
            File tempUploadedFile) throws IOException {

        OutputStream out;
        int read;
        byte[] bytes = new byte[1024];

        out = new FileOutputStream(tempUploadedFile);
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
    }

    private File createTemporaryDirectory() throws IOException {
        final File tempDir = File.createTempFile("wls_deploy_tmp", Long.toString(System.nanoTime()));

        if (!tempDir.delete()) {
            throw new IOException("Unable to delete temp file: " + tempDir.getAbsolutePath());
        } else if (!tempDir.mkdir()) {
            throw new IOException("Unable to create temp directory: " + tempDir.getAbsolutePath());
        }

        return tempDir;
    }
}
