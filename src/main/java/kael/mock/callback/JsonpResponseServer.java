package kael.mock.callback;

import leap.core.AppContext;
import leap.core.annotation.Bean;
import leap.lang.New;
import leap.lang.http.ContentTypes;
import leap.lang.http.Headers;
import leap.lang.json.JSON;
import leap.lang.json.JsonWriter;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务接口，返回一个jsonp的结果
 */
@Bean
public class JsonpResponseServer implements CallBacker{
    @Override
    public HttpRequest request(AppContext ctx, ClientAndServer mockServer, ClientAndProxy proxy) {
        return HttpRequest.request().withPath("/jsonp");
    }

    @Override
    public HttpResponse callback(HttpRequest request, AppContext ctx, ClientAndServer mockServer,
                                 ClientAndProxy proxy) {
        String callback = request.getQueryStringParameters().stream()
                .filter(parameter -> parameter.getName().equals("callback"))
                .map(parameter -> parameter.getValues().get(0).getValue())
                .findAny().orElse("func");
        List<Map<String, Object>> returns = new ArrayList<>();
        returns.add(New.hashMap("id","1","name","api1"));
        returns.add(New.hashMap("id","2","name","api2"));
        
        Map<String, Object> headers = new HashMap<>();
        request.getHeaders().forEach(header -> {
            headers.put(header.getName().getValue(),header.getValues().get(0).getValue());
        });
        
        StringWriter sw = new StringWriter();
        JsonWriter jw = JSON.createWriter(sw);
        sw.append(callback);
        sw.append('(');
        jw.map(New.hashMap("data",returns,"headers",headers));
        sw.append(')');
        return HttpResponse.response().
                withHeader(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JAVASCRIPT_UTF8)
                .withBody(StringBody.exact(sw.toString()))
                .withStatusCode(200);
    }
}
