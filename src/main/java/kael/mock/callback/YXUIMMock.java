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
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 * @author kael.
 */
@Bean(type = CallBacker.class)
public class YXUIMMock implements CallBacker {
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        System.out.println("YXUIMMock.request");
        return HttpRequest.request().withPath("/cas/v1/tickets").withQueryStringParameter("username").withQueryStringParameter("password");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        System.out.println("YXUIMMock.response");
        return HttpResponse.response().withBody("success: action=\"/TGT-abc\"");
    }
}
