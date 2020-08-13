package com.google.sticknotesbackend;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RedirectFilter implements Filter {
  private FilterConfig config = null;

  public void doFilter(final ServletRequest request, final ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    ServletContext context = config.getServletContext();
    HttpServletRequest httpRequest = (HttpServletRequest)request;
    String requestedUrl = httpRequest.getRequestURL().toString();
    // if request is not to API, redirect it to 'index.html', otherwise do nothing
    if (!requestedUrl.startsWith("api/")) {
      // request to angular, forward it to the index.html
      RequestDispatcher dispatcher = context.getRequestDispatcher("/index.html");
      dispatcher.forward(request, response);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = filterConfig;
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub
  }
}
