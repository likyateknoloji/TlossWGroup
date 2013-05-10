package com.likya.tlossw.web.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizeFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// UserManager userManager = (UserManager) req.getSession().getAttribute("userManager");
		String userManager = (String) req.getSession().getAttribute(LoginBase.SESSION_KEY);

		// if (userManager == null || !userManager.isLoggedIn()) {
		// if (userManager == null && !req.getRequestURI().endsWith("/login.jsf")) {
		if (userManager == null) {
		    res.sendRedirect(req.getContextPath() + "/login.jsf");
		} else {
		    chain.doFilter(request, response);
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
