/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Translates a text using Translate API
 */
@WebServlet("api/translate/")
public class TranslateTextServlet extends AppAbstractServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserService userService = UserServiceFactory.getUserService();
    // if user is not authenticated, deny access to the resource
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }
    JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"texts", "targetLanguage"};
      validateRequestData(body, response, requiredFields);
    } catch (PayloadValidationException ex) {
      badRequest(ex.getMessage(), response);
      return;
    }
    // get list of texts that has to be translated
    JsonArray textsJsonArray = body.get("texts").getAsJsonArray();
    ArrayList<String> texts = new ArrayList<>();
    for (JsonElement element: textsJsonArray) {
      texts.add(element.getAsString());
    }
    String targetLanguage = body.get("targetLanguage").getAsString();
    // translate text
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    // translate a list of strings because it is faster
    List<Translation> translation = translate.translate(texts, Translate.TranslateOption.targetLanguage(targetLanguage));
    // create a JSON array which stores the result
    JsonArray responseJsonArray = new JsonArray();
    for (Translation t: translation) {
      // add each translation to the array
      responseJsonArray.add(t.getTranslatedText());
    }
    // construct a JSON response
    JsonObject responseJson = new JsonObject();
    responseJson.add("result", responseJsonArray);
    // set character encoding to UTF-8 to allow non latin characters
    response.setCharacterEncoding("UTF-8");
    response.getWriter().print(responseJson.toString());
  }
}
