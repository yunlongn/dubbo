/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.springboot.demo.consumer;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProtocolServer;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import java.util.List;


/**
 * dubbo protocol support.
 */
public class DubboProtocolWrapper implements Protocol {

    private Protocol protocol;

    public DubboProtocolWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == null");
        }
        this.protocol = protocol;
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return protocol.export(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        if (url.getProtocol().equals("dubbo")) {
            final Invoker<T> refer = protocol.refer(type, url);
            return new Invoker<T>() {
                @Override
                public Class<T> getInterface() {
                    return refer.getInterface();
                }

                @Override
                public Result invoke(Invocation invocation) throws RpcException {
                    System.out.println("方法调用前 调用服务接口为： " + refer.getUrl().getAddress());
                    final Result invoke = refer.invoke(invocation);
                    System.out.println("方法调用后");
                    return invoke;
                }

                @Override
                public URL getUrl() {
                    return refer.getUrl();
                }

                @Override
                public boolean isAvailable() {
                    return refer.isAvailable();
                }

                @Override
                public void destroy() {
                    refer.destroy();
                }
            };
        }
        return protocol.refer(type, url);
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }

    @Override
    public List<ProtocolServer> getServers() {
        return protocol.getServers();
    }
}
