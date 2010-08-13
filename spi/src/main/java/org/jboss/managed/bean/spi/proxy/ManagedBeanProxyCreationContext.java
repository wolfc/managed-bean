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
 * Holds an contextual data required during proxy creation of managed beans.
 * <p>
 *  This {@link ManagedBeanProxyCreationContext} will be passed to the {@link ManagedBeanProxyFactory}
 *  implementation, during the proxy creation through {@link ManagedBeanProxyFactory#createProxy(Class, ClassLoader, ManagedBeanProxyCreationContext)}
 * </p> 
 *
 * @param <E> The type of the data that is being passed through the context
 * 
 * @see ManagedBeanProxyFactory
 *  
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface ManagedBeanProxyCreationContext<E>
{
   /**
    * Returns the data associated with this context.
    * <p>
    *   This data can be used during proxy creation in {@link ManagedBeanProxyFactory#createProxy(Class, ClassLoader, ManagedBeanProxyCreationContext)}
    * </p>
    * @return
    */
   E getData();
}
