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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.spi.InvocationContext;

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
   
   public <I> Object invoke(InvocationContext<I> invocationCtx)
   {
      I targetIdentifier = invocationCtx.getTargetIdentifier();
      Object managedBeanInstance = this.getManagedBeanInstance(targetIdentifier);
      Method targetMethod = invocationCtx.getMethod();
      
      try
      {
         return targetMethod.invoke(managedBeanInstance, invocationCtx.getArgs());
      }
      catch (IllegalArgumentException iae)
      {
         // TODO: Throw a better exception (like ManagedBeanInvocationException), so that clients can handle it if necessary
         throw new RuntimeException(iae);
      }
      catch (IllegalAccessException iae)
      {
         // TODO: Throw a better exception (like ManagedBeanInvocationException), so that clients can handle it if necessary
         throw new RuntimeException(iae);
      }
      catch (InvocationTargetException ite)
      {
         // TODO: Throw a better exception (like ManagedBeanInvocationException), so that clients can handle it if necessary
         throw new RuntimeException(ite);
      }
   }
   
   private <I> Object getManagedBeanInstance(I targetIdentifier)
   {
      // TODO: See if we can (re)use the jboss-ejb3-bean-instantiator SPI (after moving it out as a non-EJB3 SPI)
      
      try
      {
         // for now just ignore the targetIdentifier and create a new instance
         return this.managedBeanClass.newInstance();
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
