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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;

/**
 * ManagedBeanDeploymentMetaDataImpl
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanDeploymentMetaDataImpl implements ManagedBeanDeploymentMetaData
{

   private Map<String, ManagedBeanMetaData> managedBeans = new HashMap<String, ManagedBeanMetaData>();

   @Override
   public Collection<ManagedBeanMetaData> getManagedBeans()
   {
      return this.managedBeans.values();
   }

   @Override
   public void addManagedBeans(ManagedBeanMetaData... managedBeans)
   {
      for (ManagedBeanMetaData managedBean : managedBeans)
      {
         this.managedBeans.put(managedBean.getName(), managedBean);
      }
   }

   @Override
   public ManagedBeanMetaData getManagedBean(String name)
   {
      return this.managedBeans.get(name);
   }

}
