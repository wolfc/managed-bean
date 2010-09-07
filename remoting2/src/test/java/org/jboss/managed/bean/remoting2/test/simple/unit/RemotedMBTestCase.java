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
package org.jboss.managed.bean.remoting2.test.simple.unit;

import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.managed.bean.remoting2.test.common.ManagedBeanInstaller;
import org.jboss.managed.bean.remoting2.test.common.RemoteKernelController;
import org.jboss.managed.bean.remoting2.test.common.RemoteManagedBeanManager;
import org.jboss.managed.bean.remoting2.test.common.RemoteManagedBeanManagerImpl;
import org.jboss.managed.bean.remoting2.test.common.RemoteService;
import org.jboss.managed.bean.remoting2.test.common.TinyServer;
import org.jboss.managed.bean.remoting2.test.process.JavaProcess;
import org.jboss.managed.bean.remoting2.test.process.ProcessEvent;
import org.jboss.managed.bean.remoting2.test.process.ProcessEventListener;
import org.jboss.managed.bean.remoting2.test.process.ProcessManager;
import org.jboss.managed.bean.remoting2.test.simple.GreeterRemote;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class RemotedMBTestCase
{
   private static ProcessManager manager;

   @AfterClass
   public static void afterClass()
   {
      if(manager != null)
         manager.destroy();
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      final CyclicBarrier barrier = new CyclicBarrier(2);

      manager = new ProcessManager(new JavaProcess(TinyServer.class));
      manager.addProcessEventListener(new ProcessEventListener() {
         @Override
         public void event(ProcessEvent event)
         {
            String v = event.getValue();
            try
            {
               switch(event.getType())
               {
                  case ERR:
                     System.err.println(v);
                     break;
                  case OUT:
                     System.out.println(v);
                     if(v.equals("Ready"))
                     {
                        barrier.await(10, SECONDS);
                     }
                     break;
               }
            }
            catch (InterruptedException e)
            {
               throw new RuntimeException(e);
            }
            catch (BrokenBarrierException e)
            {
               throw new RuntimeException(e);
            }
            catch (TimeoutException e)
            {
               throw new RuntimeException(e);
            }
         }
      });
      barrier.await(10, SECONDS);
   }

   private static <T> T createRemoteManagedBeanManagerProxy(final RemoteManagedBeanManager manager, final Serializable id, Class<T> remoteClass)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> interfaces[] = { remoteClass };
      InvocationHandler handler = new InvocationHandler() {
         @Override
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            return manager.invoke(id, method, args);
         }
      };
      return (T) Proxy.newProxyInstance(loader, interfaces, handler);
   }

   private static <T> T createRemoteServiceProxy(Context ctx, final String name, Class<T> iface) throws NamingException
   {
      final RemoteService remoteService = (RemoteService) ctx.lookup("RemoteService");
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> interfaces[] = { iface };
      InvocationHandler handler = new InvocationHandler() {
         @Override
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            return remoteService.invoke(name, method, args);
         }
      };
      return (T) Proxy.newProxyInstance(loader, interfaces, handler);
   }
   
   @Test
   public void test1() throws Throwable
   {
      Properties props = new Properties();
      props.put(Context.PROVIDER_URL, "jnp://localhost:1099");

      InitialContext ctx = new InitialContext(props);
//      RemoteKernelController remoteKernelController = (RemoteKernelController) ctx.lookup("RemoteKernelController");
      RemoteKernelController remoteKernelController = createRemoteServiceProxy(ctx, "RemoteKernelController", RemoteKernelController.class);

      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder("simple-mb-installer", ManagedBeanInstaller.class.getName());
      remoteKernelController.install(builder.getBeanMetaData());

      BeanMetaDataBuilder builder2 = BeanMetaDataBuilderFactory.createBuilder("simple/RemoteManagedBeanManager", RemoteManagedBeanManagerImpl.class.getName());
      builder2.addPropertyMetaData("managedBeanManager", builder2.createInject("org.jboss.managedbean:name=simple"));
      remoteKernelController.install(builder2.getBeanMetaData());

//      RemoteService remoteService = (RemoteService) ctx.lookup("RemoteService");
//      System.out.println(remoteService);

      RemoteManagedBeanManager manager = createRemoteServiceProxy(ctx, "simple/RemoteManagedBeanManager", RemoteManagedBeanManager.class);
      
      Serializable id = manager.createInstance();
      System.out.println("id = " + id);

      GreeterRemote greeter = createRemoteManagedBeanManagerProxy(manager, id, GreeterRemote.class);

      System.out.println(greeter.greet("your royal wickedness"));
   }

}
