package com.graphapi.aem.core.services.impl;

import com.graphapi.aem.core.services.TokenService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component(service = TokenService.class)
@Designate(ocd = TokenServiceImpl.Config.class)
public class TokenServiceImpl implements TokenService {

    @ObjectClassDefinition(name = "Graph Token Service Config")
    public @interface Config {
        @AttributeDefinition(name = "Client ID") String client_id();
        @AttributeDefinition(name = "Client Secret") String client_secret();
        @AttributeDefinition(name = "Tenant ID") String tenant_id();
    }

    private String clientId;
    private String clientSecret;
    private String tenantId;

    @Activate
    @Modified
    protected void activate(Config cfg) {
        this.clientId = cfg.client_id();
        this.clientSecret = cfg.client_secret();
        this.tenantId = cfg.tenant_id();
    }

    @Override
    public String getAccessToken() {
        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost post = new HttpPost(tokenUrl);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=client_credentials"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default";

            post.setEntity(new StringEntity(body));

            CloseableHttpResponse response = client.execute(post);

            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) sb.append(line);

            JSONObject json = new JSONObject(sb.toString());
            return json.getString("access_token");

        } catch (Exception e) {
            return null;
        }
    }
}
