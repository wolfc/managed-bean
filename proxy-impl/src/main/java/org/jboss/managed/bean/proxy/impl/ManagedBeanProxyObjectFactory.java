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
package org.jboss.managed.bean.proxy.impl;

import java.util.Hashtable;

import javassist.util.proxy.ProxyFactory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.manager.ManagedBeanManagerRegistry;
import org.jboss.managed.bean.spi.ManagedBeanInstance;

/**
 * ManagedBeanProxyObjectFactory
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanProxyObjectFactory implements ObjectFactory
{

   @Override
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
         throws Exception
   {
      // Cast Reference
      if (!Reference.class.isInstance(obj))
      {
         throw new IllegalArgumentException("Object bound at " + name.toString() + " was not of expected type "
               + Reference.class.getName());

      }

      Reference reference = (Reference) obj;
      // get the managed bean manager registry
      ManagedBeanManagerRegistryRefAddr registryRefAddr = this.getRefAddr(reference,
            ManagedBeanProxyRefAddrType.MANAGER_REGISTRY_REF_ADDR_TYPE, ManagedBeanManagerRegistryRefAddr.class);
      ManagedBeanManagerRegistry registry = (ManagedBeanManagerRegistry) registryRefAddr.getContent();
      // get the manager bean manager id
      StringRefAddr managerNameRefAddr = this.getRefAddr(reference,
            ManagedBeanProxyRefAddrType.MANAGER_NAME_REF_ADDR_TYPE, StringRefAddr.class);
      String managerName = (String) managerNameRefAddr.getContent();

      ManagedBeanInstance<?> managedBeanInstance = this.getManagedBeanManager(registry, managerName).createManagedBeanInstance();
      return this.createProxy(registry, managerName, managedBeanInstance);

   }

   private <R> R getRefAddr(Reference reference, String addrType, Class<R> expectedType)
   {
      RefAddr refAddr = reference.get(addrType);
      if (!expectedType.isInstance(refAddr))
      {
         throw new RuntimeException("Unexpected RefAddr: " + refAddr + " for addrType: " + addrType
               + " expected type  was " + expectedType);
      }
      return expectedType.cast(refAddr);
   }

   private <T> T createProxy(ManagedBeanManagerRegistry registry, String managerName,
         ManagedBeanInstance<T> managedBeanInstance) throws IllegalArgumentException
   {
      if (managedBeanInstance == null)
      {
         throw new IllegalArgumentException("Managed bean instance cannot be null during creation of proxy");
      }
      ProxyFactory javassistProxyFactory = new ProxyFactory();
      // set the bean class for which we need a proxy 
      // Javassist internally uses the classloader of this "superclass" for
      // proxy creation
      javassistProxyFactory.setSuperclass(managedBeanInstance.getInstance().getClass());

      // Set the method handler which is responsible for handling the method invocations
      // on the proxy
      javassistProxyFactory
            .setHandler(new ManagedBeanProxyMethodHandler<T>(registry, managerName, managedBeanInstance));

      try
      {
         return (T) javassistProxyFactory.create(new Class[0], new Object[0]);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not create proxy for managed bean class "
               + managedBeanInstance.getInstance().getClass());
      }

   }
   
   private ManagedBeanManager<?> getManagedBeanManager(ManagedBeanManagerRegistry registry, String managerName)
   {
      if (!registry.isRegistered(managerName))
      {
         throw new IllegalStateException("Cannot find managed bean manager with id: " + managerName);
      }
      return (ManagedBeanManager<?>) registry.get(managerName);
   }


}
