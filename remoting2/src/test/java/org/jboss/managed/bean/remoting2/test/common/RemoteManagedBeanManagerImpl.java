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

import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.spi.ManagedBeanInstance;
import org.jboss.remoting.transporter.TransporterClient;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class RemoteManagedBeanManagerImpl<T> implements RemoteManagedBeanManager
{
   private ConcurrentHashMap<Serializable, ManagedBeanInstance<T>> sessions = new ConcurrentHashMap<Serializable, ManagedBeanInstance<T>>();

   private ManagedBeanManager<T> delegate;

   @Override
   public Serializable createInstance() throws Exception
   {
      ManagedBeanInstance<T> instance = delegate.createManagedBeanInstance();
      UUID id = UUID.randomUUID();
      sessions.put(id, instance);
      return id;
   }

   @Override
   public Object invoke(Serializable instance, Method method, Object[] args) throws Exception
   {
      return delegate.invoke(sessions.get(instance), method, args);
   }

   public void setManagedBeanManager(ManagedBeanManager<T> delegate)
   {
      this.delegate = delegate;
   }

   public void start() throws Exception
   {
      // FIXME: proper JNDI name
      InitialContext ctx = new InitialContext();
      // FIXME: this cannot be, because there are more than 1 RemoteManagedBeanManagers
      ctx.bind("RemoteManagedBeanManager", TransporterClient.createTransporterClient("socket://0.0.0.0:3874", RemoteManagedBeanManager.class));
   }

   public void stop() throws Exception
   {
      InitialContext ctx = new InitialContext();
      ctx.unbind("RemoteManagedBeanManager");
   }
}
