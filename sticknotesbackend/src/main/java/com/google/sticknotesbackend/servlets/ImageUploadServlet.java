/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.EnvironmentVariable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 * Gets a base64 JSON array
 */
@WebServlet("api/file-upload/")
@MultipartConfig
public class ImageUploadServlet extends AppAbstractServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String projectId = System.getenv(EnvironmentVariable.PROJECT_ID.name);
    String bucketName = System.getenv(EnvironmentVariable.GCS_BUCKET_NAME.name);
    if (projectId == null || bucketName == null) {
      badRequest("PROJECT_ID and GCS_BUCKET_NAME env vars must be set", response);
      return;
    }
    // get the uploaded file
    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
    InputStream fileContent = filePart.getInputStream();
    // generate filename for the uploaded file by prepending UUID to the filename
    fileName = UUID.randomUUID().toString() + fileName;
    // get bytes
    byte[] bytes = IOUtils.toByteArray(fileContent);
    // get storage service
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    // upload file
    storage.create(blobInfo, bytes);
    // generate a link to the file
    String linkToFile = "https://storage.cloud.google.com/" + bucketName + "/" + fileName;
    JsonObject responseJson = new JsonObject();
    responseJson.addProperty("fileUrl", linkToFile);
    response.getWriter().print(responseJson.toString());
  }
}
