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

import org.jboss.interceptor.proxy.DefaultInvocationContextFactory;
import org.jboss.interceptor.proxy.InterceptorInvocation;
import org.jboss.interceptor.proxy.SimpleInterceptionChain;
import org.jboss.interceptor.spi.context.InterceptionChain;
import org.jboss.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.interceptor.spi.model.InterceptionType;
import org.jboss.managed.bean.impl.ManagedBeanInstanceImpl;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.metadata.MethodMetadata;
import org.jboss.managed.bean.spi.ManagedBeanInstance;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
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
 * FIXME: After an initial working version, move this {@link ManagedBeanManager} as an SPI
 * after deciding the contracts 
 *  
 * @author Jaikiran Pai
 * @version $Revision: $
 */
// Implementation note: Do NOT include any MC (or any other kernel) specific implicit/explicit semantics (like start() stop() etc...) in
// this manager 
public class ManagedBeanManager<T>
{

   /**
    * Managed bean metadata
    */
   private ManagedBeanMetaData mbMetaData;

   /**
    * The managed bean class
    */
   private Class<T> managedBeanClass;
   

   private Method postConstructMethod;
   
   /**
    * Constructs a {@link ManagedBeanManager} for the passed managed bean class and metadata.
    * 
    * @param beanClass The managed bean class
    * @param beanMetaData The metadata of the managed bean
    * 
    */
   public ManagedBeanManager(Class<T> beanClass, ManagedBeanMetaData beanMetaData)
   {
      this.managedBeanClass = beanClass;
      this.mbMetaData = beanMetaData;
   }

   public Object invoke(ManagedBeanInstance<T> managedBeanInstance, Method method, Object[] args) throws Exception
   {
      Collection<InterceptorMetadata> aroundInvokeInterceptors = this.mbMetaData.getAroundInvokeInterceptors();
      Collection<InterceptorInvocation<?>> interceptorInvocations = new ArrayList<InterceptorInvocation<?>>(
            aroundInvokeInterceptors.size());
      for (InterceptorMetadata aroundInvokeInterceptor : aroundInvokeInterceptors)
      {
         String interceptorClassName = aroundInvokeInterceptor.getInterceptorClass().getClassName();
         Object interceptorInstance = managedBeanInstance.getInterceptor(interceptorClassName);
         // TODO: Interceptor invocation creation will be simplified in jboss-interceptors project.
         // Currently being discussed with Marius
         InterceptorInvocation<?> interceptorInvocation = new InterceptorInvocation(interceptorInstance,
               aroundInvokeInterceptor, InterceptionType.AROUND_INVOKE);
         interceptorInvocations.add(interceptorInvocation);
      }
      InterceptionChain interceptorChain = new SimpleInterceptionChain(interceptorInvocations,
            InterceptionType.AROUND_INVOKE, managedBeanInstance.getInstance(), method);
      DefaultInvocationContextFactory invocationCtxFactory = new DefaultInvocationContextFactory();
      InvocationContext invocationCtx = invocationCtxFactory.newInvocationContext(interceptorChain, managedBeanInstance
            .getInstance(), method, args);
      // invoke
      return invocationCtx.proceed();
   }


   public ManagedBeanInstance<T> createManagedBeanInstance() throws Exception
   {
      // TODO: See if we can (re)use the jboss-ejb3-bean-instantiator SPI (after moving it out as a non-EJB3 SPI)
      T instance = this.createInstance(this.managedBeanClass);
      Collection<Object> interceptorInstances = this.createInterceptors();
      ManagedBeanInstance<T> mbInstance = new ManagedBeanInstanceImpl<T>(instance, interceptorInstances);
      // invoke post-construct
      this.handlePostConstruct(mbInstance);
      return mbInstance;
   }

   private Method getPostConstructMethod()
   {
      if (this.postConstructMethod == null && this.mbMetaData.getPostConstructMethod() != null)
      {
         this.initPostConstructMethod();
      }
      return this.postConstructMethod;
   }
   
   private void initPostConstructMethod()
   {
      MethodMetadata postConstructMetadata = this.mbMetaData.getPostConstructMethod();
      String methodName = postConstructMetadata.getMethodName();
      String[] params = postConstructMetadata.getMethodParams();
      if (params == null)
      {
         params = new String[0];
      }
      ClassLoader cl = this.managedBeanClass.getClassLoader();
      Class<?>[] paramTypes = new Class<?>[params.length];
      int i = 0;
      for (String param : params)
      {
         try
         {
            paramTypes[i++] = Class.forName(param, false, cl);
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new RuntimeException("Could not load post-construct method param type: " + param, cnfe);
         }
      }
      Class<?> klass = this.managedBeanClass;
      while (klass != null)
      {
         try
         {
            this.postConstructMethod = klass.getDeclaredMethod(methodName, paramTypes);
            return;
         }
         catch (SecurityException se)
         {
            throw new RuntimeException("Could not find post-construct method named: " + methodName + " on managed bean: " + this.managedBeanClass);
         }
         catch (NoSuchMethodException nsme)
         {
            // ignore
         }
         klass = klass.getSuperclass();
      }
   }
   
   private void handlePostConstruct(ManagedBeanInstance<T> managedBeanInstance) throws Exception
   {
      // TODO: Invoke post construct on MB and interceptors
      Collection<InterceptorMetadata> postConstructInterceptors = this.mbMetaData.getPostConstructInterceptors();
      Collection<InterceptorInvocation<?>> interceptorInvocations = new ArrayList<InterceptorInvocation<?>>(
            postConstructInterceptors.size());
      for (InterceptorMetadata postConstructInterceptor : postConstructInterceptors)
      {
         String interceptorClassName = postConstructInterceptor.getInterceptorClass().getClassName();
         Object interceptorInstance = managedBeanInstance.getInterceptor(interceptorClassName);
         // TODO: Interceptor invocation creation will be simplified in jboss-interceptors project.
         // Currently being discussed with Marius
         InterceptorInvocation<?> interceptorInvocation = new InterceptorInvocation(interceptorInstance, postConstructInterceptor, InterceptionType.POST_CONSTRUCT);
         interceptorInvocations.add(interceptorInvocation);
      }
      InterceptionChain interceptorChain = new SimpleInterceptionChain(interceptorInvocations,
            InterceptionType.POST_CONSTRUCT, managedBeanInstance.getInstance(), this.getPostConstructMethod());
      DefaultInvocationContextFactory invocationCtxFactory = new DefaultInvocationContextFactory();
      InvocationContext invocationCtx = invocationCtxFactory.newInvocationContext(interceptorChain, managedBeanInstance.getInstance(), this.postConstructMethod, null);
      // invoke post-construct
      invocationCtx.proceed();
   }
   
   private Collection<Object> createInterceptors()
   {
      Collection<Object> interceptorInstances = new ArrayList<Object>(this.mbMetaData.getInterceptors().size());
      for (InterceptorMetadata interceptor : this.mbMetaData.getInterceptors())
      {
         String interceptorClassName = interceptor.getInterceptorClass().getClassName();
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
