package com.oracle.weblogic.wls12c.restdeployer;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service wrapper for weblogic.Deployer utility
 *
 * @author bruno.borges@oracle.com
 */
@Path("deployer")
public class WebLogicDeployer {

    @Inject
    private WebLogicProperties wlsProps;

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
        File tempFile;
        try {
            File tempDir = createTemporaryDirectory();
            tempFile = new File(tempDir, fileDetail.getFileName());
            writeTempFile(inputStream, tempFile);

            Logger.getLogger(WebLogicDeployer.class.getName()).log(Level.INFO, "File received at: {0}", tempFile);

            // if args is null, use default operation: '-deploy'
            if (args == null || args.trim().length() == 0) {
                String user = wlsProps.getUser();
                String pass = wlsProps.getPassword();
                args = String.format("-username {0} -password {0} -deploy", user, pass);
            }
            
            // invokes WebLogic Deployer class
            exitValue = Runtime.getRuntime().exec("java weblogic.Deployer " + args + " " + tempFile.getAbsolutePath()).waitFor();

            final File finalTempFile = tempFile;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                        finalTempFile.delete();
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

        return Response.status(exitValue == 0 ? 200 : 500).build();
    }

    private void writeTempFile(InputStream uploadedInputStream,
            File tempUploadedFile) throws IOException {

        try (OutputStream out = new FileOutputStream(tempUploadedFile)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
    }

    private File createTemporaryDirectory() throws IOException {
        final File tempDir = File.createTempFile("wls_deploy_tmp", Long.toString(System.nanoTime()));

        if (!tempDir.delete()) {
            throw new IOException("Unable to delete temp file: " + tempDir.getAbsolutePath());
        }

        if (!tempDir.mkdir()) {
            throw new IOException("Unable to create temp directory: " + tempDir.getAbsolutePath());
        }

        return tempDir;
    }
}
