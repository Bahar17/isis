/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.command;

import java.lang.reflect.Method;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.background.ActionInvocationMemento;

/**
 * Submit actions to be invoked in the background.
 * 
 * <p>
 * Example usage:
 * <pre>
 * public void submitInvoices() {
 *     for(Customer customer: customerRepository.findCustomersToInvoice()) {
 *         backgroundService.execute(customer).submitInvoice();
 *     }
 * }
 * 
 * &#64;javax.inject.Inject
 * private BackgroundService backgroundService;
 * </pre>
 */
public interface CommandMementoService {

    @Programmatic
    ActionInvocationMemento asActionInvocationMemento(Method m, Object domainObject, Object[] args);

}
