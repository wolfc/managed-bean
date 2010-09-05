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

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.manager.ManagedBeanManagerRegistry;
import org.jboss.managed.bean.spi.ManagedBeanInstance;

/**
 * ManagedBeanProxyMethodHandler
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanProxyMethodHandler<T> implements MethodHandler
{

   private ManagedBeanManagerRegistry registry;
   
   private String managedBeanManagerIdentifier;

   private ManagedBeanInstance<T> managedBeanInstance;
   
   public ManagedBeanProxyMethodHandler(ManagedBeanManagerRegistry registry, String managedBeanManagerIdentifier, ManagedBeanInstance<T> managedBeanInstance)
   {
      this.registry = registry;
      this.managedBeanManagerIdentifier = managedBeanManagerIdentifier;
      this.managedBeanInstance = managedBeanInstance;
   }

   @Override
   public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable
   {
      ManagedBeanManager<T> managedBeanManager = this.getManagedBeanManager();
      return managedBeanManager.invoke(managedBeanInstance, thisMethod, args);
   }

   private ManagedBeanManager<T> getManagedBeanManager()
   {
      if (!this.registry.isRegistered(this.managedBeanManagerIdentifier))
      {
         throw new IllegalStateException("Cannot find manager bean manager with id: "
               + this.managedBeanManagerIdentifier);
      }
      return (ManagedBeanManager<T>) this.registry.get(this.managedBeanManagerIdentifier);
   }

}
