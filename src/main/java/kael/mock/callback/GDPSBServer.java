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
import org.mockserver.model.JsonBody;

import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpResponse.response;

/**
 * @author kael.
 * 广东省公安厅的短信发送服务端模拟
 */
@Bean(type = CallBacker.class)
public class GDPSBServer implements CallBacker{
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return HttpRequest.request().withPath("/WebApi.ashx")
                .withQueryStringParameter("r")
                .withQueryStringParameter("t")
                .withQueryStringParameter("v");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        Map<String,Object> returns = new HashMap<>();
        Map<String, String> res = new HashMap<>();
        Map<String, String> inf = new HashMap<>();
        returns.put("res",res);
        returns.put("inf",inf);
        
        res.put("st","0");
        res.put("msg","ok");
        inf.put("retCode","0");
        inf.put("smsId","123");
        inf.put("successNum","2");
        inf.put("failureNum","0");
        inf.put("auditingNum","0");
        inf.put("lastRetMesg","");
        inf.put("realOrgaddr","1065734051107995");
        
        return response().withBody(JsonBody.json(returns)).withStatusCode(200);
    }
}
