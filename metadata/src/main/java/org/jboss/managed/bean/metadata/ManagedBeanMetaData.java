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
package org.jboss.managed.bean.metadata;

import java.util.Collection;

import org.jboss.interceptor.spi.metadata.InterceptorMetadata;

/**
 * Represents the metadata for a Managed Bean
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface ManagedBeanMetaData
{

   /**
    * Returns the name of the managed bean
    * @return
    */
   String getName();
   
   
   /**
    * Returns the fully qualified class name of the managed bean
    * @return
    */
   String getManagedBeanClass();
   
   /**
    * Returns the post-construct method(s) metadata, of this managed bean.
    * <p>
    *   If there are no post-construct method(s) for this managed bean, then 
    *   it's upto the implementations of {@link ManagedBeanMetaData} to either return null
    *   or an empty {@link Collection}
    * </p>
    * @return
    */
   Collection<NamedMethodMetaData> getPostConstructs();
   
   /**
    * 
    * Returns the pre-destroy method(s) metadata, of this managed bean.
    * <p>
    *   If there are no pre-destroy method(s) for this managed bean, then 
    *   it's upto the implementations of {@link ManagedBeanMetaData} to either return null
    *   or an empty {@link Collection}
    * </p>
    * @return
    */
   Collection<NamedMethodMetaData> getPreDestroys();
   
   
   /**
    * Returns all the interceptors that are applicable for this managed bean.
    * If there are no applicable interceptors for this managed bean, then this
    * method returns an empty {@link Collection}
    * @return
    */
   Collection<InterceptorMetadata> getInterceptors();
   
   /**
    * Returns the around-invoke interceptors that are applicable for this managed bean.
    * If there are no around-invoke interceptors applicable for this managed bean, then
    * this method returns an empty {@link Collection}
    * @return
    */
   Collection<InterceptorMetadata> getAroundInvokeInterceptors();
}
