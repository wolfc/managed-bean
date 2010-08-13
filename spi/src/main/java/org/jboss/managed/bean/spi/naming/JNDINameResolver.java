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
package org.jboss.managed.bean.spi.naming;

import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.reloaded.naming.spi.JavaEEModule;

/**
 * Responsible for resolving the jndi-name of a managed bean
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface JNDINameResolver
{

   /**
    * Resolves and returns the jndi-name of a managed bean, based on the <code>managedBeanMetadata</code>
    * and the <code>javaeeModule</code> to which the managed bean belongs
    * 
    * @param managedBeanMetaData The metadata of the managed bean
    * @param javaeeModule The {@link JavaEEModule} to which the managed bean belongs
    * @return
    */
   String resolveJNDIName(ManagedBeanMetaData managedBeanMetaData, JavaEEModule javaeeModule);
}
