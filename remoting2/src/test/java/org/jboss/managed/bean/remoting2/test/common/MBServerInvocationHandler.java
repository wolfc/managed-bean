/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.managed.bean.remoting2.test.common;

import org.jboss.logging.Logger;
import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.manager.ManagedBeanManagerRegistry;
import org.jboss.managed.bean.spi.ManagedBeanInstance;
import org.jboss.remoting.InvocationRequest;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class MBServerInvocationHandler extends AbstractServerInvocationHandler
{
   private static final Logger log = Logger.getLogger(MBServerInvocationHandler.class);

   private ManagedBeanManagerRegistry registry;
   
   @Override
   public Object invoke(InvocationRequest invocation) throws Throwable
   {
      Object params[] = (Object[]) invocation.getParameter();
      String managedBeanId = (String) params[0];
      ManagedBeanInstance<?> instance = (ManagedBeanInstance<?>) params[1];
      Method method = (Method) params[2];
      Object args[] = (Object[]) params[3];

      ManagedBeanManager manager = registry.get(managedBeanId);
      return manager.invoke(instance, method, args);
   }

   public void setManagedBeanManagerRegistry(ManagedBeanManagerRegistry registry)
   {
      this.registry = registry;
   }
}
