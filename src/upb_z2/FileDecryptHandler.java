package upb_z2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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

public class FileDecryptHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String UPLOAD_DIRECTORY = "C:/uploads";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// process only if its multipart content
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				File encrypted = null;
				File decrypted = null;
				String key = null;

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						String name = new File(item.getName()).getName();

						encrypted = new File(UPLOAD_DIRECTORY + File.separator + name);
						decrypted = new File(UPLOAD_DIRECTORY + File.separator + "decrypted.txt");
					}
//                    else {
//                    	if(item.getFieldName().equals("fname")) {
//                    		key = item.getString();
//                    	}
//                    }
				}

				key = CryptoUtils.readKeyFile(UPLOAD_DIRECTORY + File.separator + "key.txt");

				String encKey = Asymmetric_encryption.do_RSA(key);

				int size_of_key = encKey.length();
				Path path = Paths.get(UPLOAD_DIRECTORY + File.separator + "encrypted.txt");
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				try {
					FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
					channel.read(buffer, channel.size() - size_of_key);
					System.out.println("Key in encrypted file = " + new String(buffer.array()));
				} catch (IOException e) {
					e.printStackTrace();
				}

				String s = CryptoUtils.readKeyFile(UPLOAD_DIRECTORY + File.separator + "encrypted.txt");
				int size = s.length() - size_of_key;
				String text_to_encrypt = s.substring(0, size);
				System.out.println("Text to encrypt: " + text_to_encrypt);
				
				CryptoUtils.writeKeyFile(UPLOAD_DIRECTORY + File.separator + "tmp.txt", text_to_encrypt);
				
				CryptoUtils.decrypt(key, new File(UPLOAD_DIRECTORY + File.separator + "tmp.txt"), decrypted);

//               File uploaded successfully
				response.setContentType("text/plain");

				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(UPLOAD_DIRECTORY + File.separator + "decrypted.txt");
				byte[] buffer2 = new byte[4096];
				int length;
				while ((length = in.read(buffer2)) > 0) {
					out.write(buffer2, 0, length);
				}
				in.close();
				out.flush();

				request.setAttribute("message", "File Uploaded Successfully");
			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to " + ex);
			}

		} else {
			request.setAttribute("message", "Sorry this Servlet only handles file upload request");
		}

		request.getRequestDispatcher("/result.jsp").forward(request, response);

	}
}
