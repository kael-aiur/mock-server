/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kael.mock.callback;

import leap.core.AppContext;
import leap.core.annotation.Bean;
import leap.lang.security.RSA;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author kael.
 */
@Bean(type = CallBacker.class)
public class ClientRegistrationServer implements CallBacker{
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return HttpRequest.request().withMethod("POST").withPath("/ssov3/oauth2/register");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        Map<String, String> map = new HashMap<>();
        map.put("client_id",UUID.randomUUID().toString());
        map.put("client_secret",UUID.randomUUID().toString());
        map.put("client_id_issued_at",System.currentTimeMillis()/1000+"");
        map.put("client_secret_expires_at","0");
        map.put("e_code","mocktest");
        RSA.RsaKeyPair pair = RSA.generateKeyPair();
        map.put("public_key",pair.getBase64PublicKey());
        map.put("private_key",pair.getBase64PrivateKey());
        return HttpResponse.response().withBody(JsonBody.json(map));
    }
}
