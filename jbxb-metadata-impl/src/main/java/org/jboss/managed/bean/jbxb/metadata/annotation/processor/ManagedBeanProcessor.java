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
package org.jboss.managed.bean.jbxb.metadata.annotation.processor;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.ManagedBean;

import org.jboss.managed.bean.jbxb.metadata.ManagedBeanMetaDataImpl;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.metadata.annotation.creator.AbstractComponentProcessor;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.finder.AnnotationFinder;

/**
 * ManagedBeanProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanProcessor extends AbstractComponentProcessor<ManagedBeanMetaData>
      implements
         Processor<ManagedBeanDeploymentMetaData, Class<?>>
{

   public ManagedBeanProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
      
      // add any method/field processors here
   }

   /**
    * Processes the <code>managedBeanClass</code> to find any {@link ManagedBean} annotation. 
    * <p>
    *   If a {@link ManagedBean} annotation is found on the <code>managedBeanClass</code>, then
    *   this method creates a {@link ManagedBeanMetaData} for that class and adds it to the <code>managedBeanDeploymentMetaData</code>
    * </p>
    */
   @Override
   public void process(ManagedBeanDeploymentMetaData managedBeanDeploymentMetaData, Class<?> managedBeanClass)
   {
      ManagedBean managedBeanAnnotation = this.finder.getAnnotation(managedBeanClass, ManagedBean.class);

      if (managedBeanAnnotation == null)
      {
         return;
      }
      ManagedBeanMetaDataImpl managedBeanMetaData = new ManagedBeanMetaDataImpl();
      managedBeanMetaData.setManagedBeanClass(managedBeanClass.getName());
      // set the name in the metadata, if it has been set on the annotation
      String name = managedBeanAnnotation.value();
      if (name != null && !name.isEmpty())
      {
         managedBeanMetaData.setName(name);
      }
      
      // add this managed bean to the managed bean deployment metadata
      managedBeanDeploymentMetaData.addManagedBeans(managedBeanMetaData);
   }

}
