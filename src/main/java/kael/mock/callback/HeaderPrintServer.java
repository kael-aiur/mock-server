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
import leap.lang.http.ContentTypes;
import leap.lang.http.Headers;
import leap.lang.json.JSON;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kael.
 */
@Bean(type = CallBacker.class)
public class HeaderPrintServer implements CallBacker {
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return HttpRequest.request().withPath("/header_print");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer,
                                 ClientAndProxy proxy) {
        Map<String,Object> returns = new HashMap<>();
        request.getHeaders().forEach(header -> returns.put(header.getName().getValue(), JSON.encode(header.getValues().stream().map(s -> s.getValue()).toArray())));
        return HttpResponse.response().withHeader(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON_UTF8)
                .withBody(JsonBody.json(returns)).withStatusCode(200);
    }
}
