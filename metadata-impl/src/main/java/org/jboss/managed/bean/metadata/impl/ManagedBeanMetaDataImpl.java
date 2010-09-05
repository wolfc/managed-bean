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
package org.jboss.managed.bean.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.interceptor.spi.model.InterceptionType;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.metadata.MethodMetadata;

/**
 * ManagedBeanMetaDataImpl
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanMetaDataImpl implements ManagedBeanMetaData
{

   /**
    * Fully qualified classname of the managed bean
    */
   private String managedBeanClass;

   /**
    * Name of the managed bean
    */
   private String managedBeanName;

   /**
    * Interceptors applicable to this managed bean
    */
   private List<InterceptorMetadata> interceptors;
   
   /**
    * Post-construct method metadata
    */
   private MethodMetadata postConstructMethod;
   
   @Override
   public String getManagedBeanClass()
   {
      return this.managedBeanClass;
   }

   @Override
   public String getName()
   {
      return this.managedBeanName;
   }

   @Override
   public MethodMetadata getPreDestroyMethod()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   public MethodMetadata getPostConstructMethod()
   {
      return this.postConstructMethod;
   }
   
   public void setPostConstructMethod(MethodMetadata method)
   {
      this.postConstructMethod = method;
   }
   
   public void setManagedBeanClass(String managedBeanClass)
   {
      this.managedBeanClass = managedBeanClass;
   }

   public void setName(String name)
   {
      this.managedBeanName = name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<InterceptorMetadata> getInterceptors()
   {
      if (this.interceptors == null)
      {
         return Collections.EMPTY_LIST;
      }
      return this.interceptors;
   }

   @Override
   public List<InterceptorMetadata> getAroundInvokeInterceptors()
   {
      return this.getInterceptors(InterceptionType.AROUND_INVOKE);
   }
   
   @Override
   public List<InterceptorMetadata> getPostConstructInterceptors()
   {
      return this.getInterceptors(InterceptionType.POST_CONSTRUCT);
   }
   
   public void setInterceptors(Collection<InterceptorMetadata> interceptors)
   {
      this.interceptors = new ArrayList<InterceptorMetadata>(interceptors); 
   }
   
   public void addInterceptor(InterceptorMetadata interceptor)
   {
      if (this.interceptors == null)
      {
         this.interceptors = new ArrayList<InterceptorMetadata>();
      }
      this.interceptors.add(interceptor);
   }
   
   private List<InterceptorMetadata> getInterceptors(InterceptionType interceptionType)
   {
      if (this.interceptors == null)
      {
         return Collections.EMPTY_LIST;
      }
      List<InterceptorMetadata> applicableInterceptors = new ArrayList<InterceptorMetadata>();
      for (InterceptorMetadata interceptor : this.interceptors)
      {
         if (interceptor.isEligible(interceptionType))
         {
            applicableInterceptors.add(interceptor);
         }
      }
      return applicableInterceptors;
   }
}
