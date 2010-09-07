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

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.kernel.Kernel;
import org.jboss.remoting.transport.Connector;
import org.jboss.remoting.transporter.TransporterClient;
import org.jboss.remoting.transporter.TransporterHandler;

import javax.naming.InitialContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A dangerous service which exposes all MC beans.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class RemoteServiceImpl implements RemoteService
{
   private Connector connector;
   private Kernel kernel;

   @Override
   public Object invoke(String id, Method method, Object[] args) throws Exception
   {
      Object target = kernel.getController().getInstalledContext(id).getTarget();
      try
      {
         return method.invoke(target, args);
      }
      catch(InvocationTargetException e)
      {
         Throwable t = e.getTargetException();
         if(t instanceof Error)
            throw (Error) t;
         if(t instanceof Exception)
            throw (Exception) t;
         throw new Error(t);
      }
   }

   public void setConnector(Connector connector)
   {
      this.connector = connector;
   }

   @Inject(bean="jboss.kernel:service=Kernel")
   public void setKernel(Kernel kernel)
   {
      this.kernel = kernel;
   }

   public void start() throws Exception
   {
      connector.addInvocationHandler(RemoteService.class.getName(), new TransporterHandler(this));
      InitialContext ctx = new InitialContext();
      ctx.bind("RemoteService", TransporterClient.createTransporterClient(connector.getLocator(), RemoteService.class));
   }

   public void stop() throws Exception
   {
      InitialContext ctx = new InitialContext();
      ctx.unbind("RemoteService");
      connector.removeInvocationHandler(RemoteService.class.getName());
   }
}
