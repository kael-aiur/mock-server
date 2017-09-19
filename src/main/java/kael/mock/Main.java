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

package kael.mock;

import kael.mock.callback.CallBacker;
import leap.core.AppContext;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * @author kael.
 */
public class Main {
    private static ClientAndProxy proxy;
    private static ClientAndServer mockServer;
    private static AppContext app;
    public static void main(String[] args) {
        try {
            app = AppContext.initStandalone();

            mockServer = startClientAndServer(8099);
            proxy = startClientAndProxy(8199);

            app.getBeanFactory().getBeans(CallBacker.class)
                    .forEach(cb -> mockServer
                            .when(cb.request(app,mockServer,proxy))
                            .callback(req -> cb.callback(req,app,mockServer,proxy)));
        }catch (Exception e){
            if (null != mockServer){
                mockServer.stop();
            }
            if(null != proxy){
                proxy.stop();
            }
        }
        
    }
}
