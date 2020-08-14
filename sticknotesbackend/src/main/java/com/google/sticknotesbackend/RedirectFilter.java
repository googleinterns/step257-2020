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
    // if request is not to API, forward it to 'index.html', otherwise do nothing
    if (!isApiUrl(httpRequest.getRequestURL().toString())) {
      // request to angular, forward it to the index.html
      RequestDispatcher dispatcher = context.getRequestDispatcher("/index.html");
      dispatcher.forward(request, response);
    }
  }

  private boolean isApiUrl(String url) {
    return url.startsWith("api") || url.startsWith("/api");
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = filterConfig;
  }

  @Override
  public void destroy() {}
}
