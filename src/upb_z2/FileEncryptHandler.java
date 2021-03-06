package upb_z2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileEncryptHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	private final String UPLOAD_DIRECTORY = "C:/uploads";
	private final String UPLOAD_DIRECTORY = "/usr/local/apache-tomcat-9.0.12/uploads";
	  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {   
        //process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
              
                File temp = null;
                File encrypted = null;
                String key = CryptoUtils.generateKey();
                
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
                        temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                        item.write(temp);
                        encrypted = new File(UPLOAD_DIRECTORY + File.separator + "encrypted.txt");
                    }
//                    else {
//                    	if(item.getFieldName().equals("fname")) {
//                    		key = item.getString();
//                    	}
//                    }
                }
//                String key = "";
                CryptoUtils.encrypt(key, temp, encrypted);
                
                //add rsa key
                String encKey = Asymmetric_encryption.do_RSA(key);
                Files.write(Paths.get(UPLOAD_DIRECTORY + File.separator + "encrypted.txt"), encKey.getBytes(), StandardOpenOption.APPEND);
           
               //File uploaded successfully
                response.setContentType("text/plain");
                
                OutputStream out = response.getOutputStream();
                FileInputStream in = new FileInputStream(encrypted);
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0) {
                	out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                
                CryptoUtils.writeKeyFile(UPLOAD_DIRECTORY + File.separator + "key.txt", key);
                
               request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
               request.setAttribute("message", "File Upload Failed due to " + ex);
            }          
         
        }else{
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }
    
        request.getRequestDispatcher("/result.jsp").forward(request, response);
     
    }
}	
