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
package org.jboss.managed.bean.spi.proxy;

/**
 * A proxy factory responsible for creating proxies for a managed bean class
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface ManagedBeanProxyFactory
{

   /**
    * Creates and returns a proxy for the passed <code>managedBeanClass</code>
    * 
    * @param <T> The class type of the managed bean
    * @param <E> The type of data that is being passed through the {@link ManagedBeanProxyCreationContext}
    * @param managedBeanClass The {@link Class} of the managed bean
    * @param classLoader The {@link ClassLoader} that has to be used to create the proxy
    * @param context The {@link ManagedBeanProxyCreationContext} that can be used to pass any additional data required
    *               during proxy creation. This <i>may</i> be null, but it's upto the individual implementation to enforce this.
    * @return
    * @throws IllegalArgumentException If either the <code>managedBeanClass</code> or the <code>classLoader</code> is null
    */
   <T> T createProxy(Class<T> managedBeanClass, ClassLoader classLoader, ManagedBeanProxyCreationContext<?> context) throws IllegalArgumentException;
}
