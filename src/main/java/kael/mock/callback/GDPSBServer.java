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
import leap.lang.Charsets;
import leap.lang.Out;
import leap.lang.Strings;
import leap.lang.codec.Base64;
import leap.lang.codec.MD5;
import leap.lang.http.ContentTypes;
import leap.lang.http.Headers;
import leap.lang.json.JSON;
import leap.lang.logging.Log;
import leap.lang.logging.LogFactory;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kael.
 * 广东省公安厅的短信发送服务端模拟
 */
@Bean(type = CallBacker.class)
public class GDPSBServer implements CallBacker{

    private static final Log log = LogFactory.get(GDPSBServer.class);
    
    private static final String SALT = "gaSms12#654";
    private static final String UID = "jwt";
    private static final String UPW = MD5.hex("jwt@2017".getBytes(Charsets.UTF_8)).toLowerCase();

    private static final String EID = "警务通";
    private static final String TO = "15915920691";
    
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return HttpRequest.request().withPath("/WebApi.ashx")
                .withQueryStringParameter("r")
                .withQueryStringParameter("t")
                .withQueryStringParameter("v");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return response(check(request));
    }
    
    private boolean check(HttpRequest request){
        Out<String> r = new Out<>();
        Out<String> t = new Out<>();
        Out<String> v = new Out<>();
        request.getQueryStringParameters().forEach(parameter -> {
            if(parameter.getName().equals("r")){
                r.accept(parameter.getValues().get(0).getValue());
            }
            if(parameter.getName().equals("t")){
                t.accept(parameter.getValues().get(0).getValue());
            }
            if(parameter.getName().equals("v")){
                v.accept(parameter.getValues().get(0).getValue());
            }
        });
        if(!r.isPresent()||!t.isPresent()||!v.isPresent()){
            return false;
        }
        try {
            String json = URLDecoder.decode(Base64.decode(r.get()), Charsets.UTF_8_NAME);
            Map<String, Object> map = JSON.decodeMap(json);

            String v1 = MD5.hex((r.get()+t.get()+SALT).getBytes(Charsets.UTF_8_NAME)).toUpperCase();
            if(!Strings.equals(v1,v.get())){
                return false;
            }
            if(map.get("svc") == null || !map.get("svc").equals("SMS0000001")){
                return false;
            }
            Map<String, String > para = (Map<String, String >)map.get("para");
            if(!UID.equals(para.get("uId"))){
                return false;
            }
            if(!UPW.equals(para.get("uPw"))){
                return false;
            }
            if(!EID.equals(para.get("eId"))){
                return false;
            }
            log.info("省公安厅短信接口已经接收到短信请求，并且向以下号码推送短信：[{}],短信内容[{}]",para.get("mobiles"),para.get("content"));
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private HttpResponse response(boolean success){
        int status = 200;
        Map<String,Object> returns = new HashMap<>();
        Map<String, String> res = new HashMap<>();
        Map<String, String> inf = new HashMap<>();
        returns.put("res",res);
        returns.put("inf",inf);
        if(success){
            res.put("st","0");
            res.put("msg","ok");
            inf.put("retCode","0");
            inf.put("smsId","123");
            inf.put("successNum","2");
            inf.put("failureNum","0");
            inf.put("auditingNum","0");
            inf.put("lastRetMesg","");
            inf.put("realOrgaddr","1065734051107995");
        }else {
            status = 400;
        }
        return HttpResponse.response().withHeader(Headers.CONTENT_TYPE,ContentTypes.APPLICATION_JSON_UTF8)
                .withBody(JsonBody.json(returns)).withStatusCode(status);
    }
    
}
