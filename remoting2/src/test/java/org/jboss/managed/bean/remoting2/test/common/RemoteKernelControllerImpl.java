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
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.kernel.Kernel;
import org.jboss.remoting.transporter.TransporterClient;

import javax.naming.InitialContext;

/**
 * Provides functions of MC which can be remotely called.
 * 
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class RemoteKernelControllerImpl implements RemoteKernelController
{
   private static final String OID = "jboss:RemoteKernelController";
   
   private Kernel kernel;

   public void install(BeanMetaData metaData) throws Throwable
   {
      kernel.getController().install(metaData);
      System.out.println(kernel.getController().getNotInstalled());
   }

   public void uninstall(String name) throws Throwable
   {
      kernel.getController().uninstall(name);
   }

   @Inject(bean="jboss.kernel:service=Kernel")
   public void setKernel(Kernel kernel)
   {
      this.kernel = kernel;
   }
   
   public void start() throws Exception
   {
      InitialContext ctx = new InitialContext();
//      Class<?> interfaces[] = { RemoteKernelController.class };
//      Object proxy = Remoting.createPojiProxy(OID, interfaces, "socket://0.0.0.0:3874");
//      ctx.rebind("RemoteKernelController", proxy);
//      Dispatcher.singleton.registerTarget(OID, this);
      ctx.rebind("RemoteKernelController", TransporterClient.createTransporterClient("socket://0.0.0.0:3874", RemoteKernelController.class));
   }
   
   public void stop() throws Exception
   {
//      Dispatcher.singleton.unregisterTarget(OID);
      InitialContext ctx = new InitialContext();
      ctx.unbind("RemoteKernelController");
   }
   
   // Must implement toString otherwise it won't get exposed via ClassProxyFactory.getMethodMap
   @Override
   public String toString()
   {
      return OID;
   }
}
