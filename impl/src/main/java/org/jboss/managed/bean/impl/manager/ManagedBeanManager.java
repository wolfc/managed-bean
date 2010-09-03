/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
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
package org.jboss.managed.bean.impl.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.interceptor.InvocationContext;

import org.jboss.interceptor.proxy.DefaultInvocationContextFactory;
import org.jboss.interceptor.proxy.InterceptorInvocation;
import org.jboss.interceptor.proxy.SimpleInterceptionChain;
import org.jboss.interceptor.spi.context.InterceptionChain;
import org.jboss.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.interceptor.spi.model.InterceptionType;
import org.jboss.managed.bean.impl.ManagedBeanInstanceImpl;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.spi.ManagedBeanInstance;

/**
 * Manages a managed bean.
 * 
 * <p>
 *  {@link ManagedBeanManager} is responsible for managing the runtime of a managed bean. This includes 
 *  (although not limited to):
 *  
 *   <ul>
 *      <li>Instance creation of the managed bean either directly or indirectly</li>
 *      <li>Invoking appropriate lifecycle callbacks on the managed bean</li>
 *      <li>Passing on the managed bean instance through a chain on injectors</li>
 *      <li>Passing on the invocations on a managed bean instance, through a chain of interceptors</li>  
 *   </ul>
 * Note that the {@link ManagedBeanManager} may utilize services from other SPIs for any/all of the above responsibilities.    
 * </p>
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanManager<T>
{

   /**
    * The identifier of this managed bean manager
    */
   private String id;

   /**
    * Managed bean metadata
    */
   private ManagedBeanMetaData mbMetaData;

   /**
    * The managed bean class
    */
   private Class<T> managedBeanClass;

   /**
    * Constructs a {@link ManagedBeanManager} for the passed managed bean class and metadata.
    * 
    * @param beanClass The managed bean class
    * @param beanMetaData The metadata of the managed bean
    */
   public ManagedBeanManager(String id, Class<T> beanClass, ManagedBeanMetaData beanMetaData)
   {
      this.id = id;
      this.managedBeanClass = beanClass;
      this.mbMetaData = beanMetaData;
   }

   public String getId()
   {
      return id;
   }

   public void start()
   {
      ManagedBeanManagerRegistry.register(this.id, this);
   }

   public void stop()
   {
      ManagedBeanManagerRegistry.unregister(id);
   }

   public Object invoke(ManagedBeanInstance<T> managedBeanInstance, Method method, Object[] args) throws Throwable
   {
      Collection<InterceptorMetadata> aroundInvokeInterceptors = this.mbMetaData.getAroundInvokeInterceptors();
      Collection<InterceptorInvocation<?>> interceptorInvocations = new ArrayList<InterceptorInvocation<?>>(
            aroundInvokeInterceptors.size());
      for (InterceptorMetadata aroundInvokeInterceptor : aroundInvokeInterceptors)
      {
         // FIXME: interceptor metadata doesn't have a API to get interceptor classname
         String interceptorClassName = null; //aroundInvokeInterceptor.getIntercetorClassName();
         Object interceptorInstance = managedBeanInstance.getInterceptor(interceptorClassName);
         InterceptorInvocation<?> interceptorInvocation = new InterceptorInvocation(interceptorInstance,
               aroundInvokeInterceptor, InterceptionType.AROUND_INVOKE);
         interceptorInvocations.add(interceptorInvocation);
      }
      InterceptionChain interceptorChain = new SimpleInterceptionChain(interceptorInvocations,
            InterceptionType.AROUND_INVOKE, managedBeanInstance.getInstance(), method);
      DefaultInvocationContextFactory invocationCtxFactory = new DefaultInvocationContextFactory();
      InvocationContext invocationCtx = invocationCtxFactory.newInvocationContext(interceptorChain, managedBeanInstance
            .getInstance(), method, args);
      return interceptorChain.invokeNextInterceptor(invocationCtx);
   }


   public ManagedBeanInstance<T> createManagedBeanInstance()
   {
      // TODO: See if we can (re)use the jboss-ejb3-bean-instantiator SPI (after moving it out as a non-EJB3 SPI)
      T managedBeanInstance = this.createInstance(this.managedBeanClass);
      Collection<Object> interceptorInstances = this.createInterceptors();
      return new ManagedBeanInstanceImpl<T>(managedBeanInstance, interceptorInstances);
   }

   private Collection<Object> createInterceptors()
   {
      Collection<Object> interceptorInstances = new ArrayList<Object>(this.mbMetaData.getInterceptors());
      for (InterceptorMetadata interceptor : this.mbMetaData.getInterceptors())
      {
         // FIXME: The interceptor metadata doesn't have a API to get the
         // class name of the interceptor
         String interceptorClassName = null;//interceptor.getInterceptorClass();
         try
         {
            Class<?> interceptorClass = Class.forName(interceptorClassName, false, this.managedBeanClass
                  .getClassLoader());
            Object interceptorInstance = this.createInstance(interceptorClass);
            interceptorInstances.add(interceptorInstance);
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new RuntimeException("Could not load interceptor class: " + interceptorClassName, cnfe);
         }

      }
      return interceptorInstances;
   }

   private <I> I createInstance(Class<I> klass)
   {
      try
      {
         // for now just ignore the targetIdentifier and create a new instance
         return klass.newInstance();
      }
      catch (InstantiationException ie)
      {
         // TODO: I guess a BeanInstantionException SPI would be better.
         // But let's wait for the decision on the jboss-ejb3-bean-instantiator usage
         throw new RuntimeException(ie);
      }
      catch (IllegalAccessException iae)
      {
         // TODO: I guess a BeanInstantionException SPI would be better.
         // But let's wait for the decision on the jboss-ejb3-bean-instantiator usage
         throw new RuntimeException(iae);
      }
   }

}
