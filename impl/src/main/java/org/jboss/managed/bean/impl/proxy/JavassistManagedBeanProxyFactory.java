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
package org.jboss.managed.bean.impl.proxy;

import javassist.util.proxy.ProxyFactory;

import org.jboss.managed.bean.spi.proxy.ManagedBeanProxyCreationContext;
import org.jboss.managed.bean.spi.proxy.ManagedBeanProxyFactory;

/**
 * An implementation of {@link ManagedBeanProxyFactory} which creates Javassist based proxies
 * for managed beans 
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JavassistManagedBeanProxyFactory implements ManagedBeanProxyFactory
{

   /**
    * {@inheritDoc}
    */
   @Override
   public <T> T createProxy(Class<T> managedBeanClass, ClassLoader classLoader,
         ManagedBeanProxyCreationContext<?> context) throws IllegalArgumentException
   {
      if (managedBeanClass == null)
      {
         throw new IllegalArgumentException("Managed bean class cannot be null during creation of proxy");
      }
      if (classLoader == null)
      {
         throw new IllegalArgumentException("ClassLoader cannot be null during creation of managed bean proxy");
      }
      if (context == null || (context instanceof JavassistProxyCreationContext == false))
      {
         throw new IllegalArgumentException(JavassistManagedBeanProxyFactory.class.getSimpleName()
               + " can't handle proxy creation context: " + context);
      }

      ProxyFactory javassistProxyFactory = new ProxyFactory();
      // set the bean class for which we need a proxy 
      // Javassist internally uses the classloader of this "superclass" for
      // proxy creation
      javassistProxyFactory.setSuperclass(managedBeanClass);

      // Set the method handler which is responsible for handling the method invocations
      // on the proxy
      javassistProxyFactory.setHandler(((JavassistProxyCreationContext) context).getData());

      return managedBeanClass.cast(javassistProxyFactory.createClass());
      
   }

}
