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
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.url.component.DubboServiceAddressURL;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.apache.dubbo.registry.client.migration.MigrationInvoker;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.filter.ClusterFilter;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ConsumerModel;

import java.util.List;

/**
 * EventFilter
 */
@Activate(group = CommonConstants.CONSUMER)
public class EnvFilter implements ClusterFilter {

    protected static final Logger logger = LoggerFactory.getLogger(EnvFilter.class);

    @Override
    public Result invoke(final Invoker<?> invoker, final Invocation invocation) throws RpcException {
        // need to configure if there's return value before the invocation in order to help invoker to judge if it's
        // necessary to return future.
        final ApplicationModel applicationModel = invoker.getUrl().getOrDefaultApplicationModel();
        final RegistryFactory registryFactory = invoker.getUrl().getOrDefaultApplicationModel().getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
        final List<RegistryConfig> registries = applicationModel.getDefaultModule().getConfigManager().getDefaultRegistries();
        final RegistryConfig registryConfig = registries.get(0);

        final Registry registryFactoryRegistry = registryFactory.getRegistry(URL.valueOf(registryConfig.getAddress()));
        final List<URL> lookup = registryFactoryRegistry.lookup(invoker.getUrl());
        final DubboServiceAddressURL url = (DubboServiceAddressURL) lookup.get(0);

        return invoker.invoke(invocation);
    }


}
