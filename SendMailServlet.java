package com.graphapi.aem.core.servlets;

import com.graphapi.aem.core.services.MailService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.*;
import org.osgi.service.component.annotations.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/graph/sendmail",
                "sling.servlet.methods=GET"
        }
)
public class SendMailServlet extends SlingSafeMethodsServlet {

    @Reference
    private MailService mailService;

    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {

        String to = req.getParameter("to");
        String subject = req.getParameter("subject");
        String message = req.getParameter("message");

        String result = mailService.sendMail(to, subject, message);

        resp.setContentType("text/plain");
        resp.getWriter().write(result);
    }
}
