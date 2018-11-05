package upb_z2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadHandler2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String UPLOAD_DIRECTORY = "C:/uploads";
	  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {   
        //process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
              
                File temp = null;
                File encrypted = null;
                String key = null;
                
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
                        temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                        item.write(temp);
                        encrypted = new File(UPLOAD_DIRECTORY + File.separator + name + ".enc");
                    }
                    else {
                    	if(item.getFieldName().equals("fname")) {
                    		key = item.getString();
                    	}
                    }
                }
//                String key = "";
                CryptoUtils.encrypt(key, temp, encrypted);
           
               //File uploaded successfully
                response.setContentType("text/plain");
//                
                OutputStream out = response.getOutputStream();
                FileInputStream in = new FileInputStream(encrypted);
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0) {
                	out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                
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
