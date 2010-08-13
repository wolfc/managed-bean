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
import java.util.Collection;

import org.jboss.managed.bean.jbxb.metadata.ManagedBeanDeploymentMetaDataImpl;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.metadata.annotation.creator.AbstractCreator;
import org.jboss.metadata.annotation.creator.Creator;
import org.jboss.metadata.annotation.finder.AnnotationFinder;

/**
 * Responsible for creating {@link ManagedBeanDeploymentMetaData} out of annotated classes.
 *
 * @see Creator
 * 
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanMetaDataCreator extends AbstractCreator<ManagedBeanDeploymentMetaData>
      implements
         Creator<Collection<Class<?>>, ManagedBeanDeploymentMetaData>
{

   /**
    * Creates a {@link ManagedBeanDeploymentMetaData}
    * 
    * @param finder The {@link AnnotationFinder} that will be used for processing the annotation(s) on the 
    *               classes
    */
   public ManagedBeanMetaDataCreator(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
      
      // add the managed bean processor
      this.addProcessor(new ManagedBeanProcessor(finder));
      
   }

   /**
    * Creates a {@link ManagedBeanDeploymentMetaData} for the passed <code>classes</code>
    * 
    * @throws IllegalArgumentException If the passed <code>classes</code> is null or empty
    */
   @Override
   public ManagedBeanDeploymentMetaData create(Collection<Class<?>> classes)
   {
      if (classes == null || classes.isEmpty())
      {
         throw new IllegalArgumentException("Cannot create managed bean deployment metadata out of null or empty classes"); 
      }
      
      ManagedBeanDeploymentMetaData managedBeanDeploymentMetaData = new ManagedBeanDeploymentMetaDataImpl();
      
      // pass it through the processor(s)
      this.processMetaData(classes, managedBeanDeploymentMetaData);
      
      // return the processed metadata
      return managedBeanDeploymentMetaData;
   }

   @Override
   protected boolean validateClass(Class<?> klass)
   {
      // I haven't so far digged deeper to figure out why we really need this. But let's 
      // say all the classes are valid
      return true;
   }

}
