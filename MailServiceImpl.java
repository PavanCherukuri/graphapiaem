package com.graphapi.aem.core.services.impl;

import com.graphapi.aem.core.services.MailService;
import com.graphapi.aem.core.services.TokenService;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.json.JSONObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;

@Component(service = MailService.class)
@Designate(ocd = MailServiceImpl.Config.class)
public class MailServiceImpl implements MailService {

    @ObjectClassDefinition(name = "Graph Mail Service Config")
    public @interface Config {
        @AttributeDefinition(name = "Sender mailbox", description = "Example: aem-forms@yourcompany.com")
        String mailbox();
    }

    @Reference
    private TokenService tokenService;

    private String mailbox;

    @Activate
    @Modified
    protected void activate(Config cfg) {
        this.mailbox = cfg.mailbox();
    }

    @Override
    public String sendMail(String to, String subject, String message) {

        String url = "https://graph.microsoft.com/v1.0/users/" + mailbox + "/sendMail";

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            String token = tokenService.getAccessToken();

            JSONObject json = new JSONObject();
            JSONObject msg = new JSONObject();
            JSONObject body = new JSONObject();
            JSONObject recipient = new JSONObject();
            JSONObject emailAddr = new JSONObject();

            body.put("contentType", "Text");
            body.put("content", message);
            emailAddr.put("address", to);
            recipient.put("emailAddress", emailAddr);

            msg.put("subject", subject);
            msg.put("body", body);
            msg.put("toRecipients", new org.json.JSONArray().put(recipient));

            json.put("message", msg);
            json.put("saveToSentItems", false);

            HttpPost post = new HttpPost(url);
            post.setHeader("Authorization", "Bearer " + token);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));

            CloseableHttpResponse response = client.execute(post);

            int code = response.getStatusLine().getStatusCode();
            return "Status: " + code;

        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
