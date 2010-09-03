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
package org.jboss.managed.bean.metadata.jbmeta.test;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

import javax.annotation.ManagedBean;

import junit.framework.Assert;

import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.metadata.jbmeta.annotation.processor.ManagedBeanMetaDataCreator;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests annotation processing of classes annotated with {@link ManagedBean}  
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanAnnotationProcessorTestCase
{

   /**
    * Metadata creator
    */
   private ManagedBeanMetaDataCreator metadataCreator;

   /**
    * Do the necessary setup
    */
   @Before
   public void before()
   {
      AnnotationFinder<AnnotatedElement> annotationFinder = new DefaultAnnotationFinder<AnnotatedElement>();
      metadataCreator = new ManagedBeanMetaDataCreator(annotationFinder);
   }

   /**
    * Tests that the {@link ManagedBeanMetaDataCreator} processes the names of managed beans as expected 
    */
   @Test
   public void testManagedBeanNameProcessing()
   {
      Class<?>[] classes = new Class<?>[]
      {ManagedBeanWithoutExplicitName.class, NotAManagedBean.class, ManagedBeanWithName.class};
      ManagedBeanDeploymentMetaData managedBeanDeployment = metadataCreator.create(Arrays.asList(classes));

      Assert.assertNotNull(ManagedBeanMetaDataCreator.class.getSimpleName()
            + " did not created a managed bean deployment", managedBeanDeployment);
      Assert.assertEquals("Unexpected number of managed beans created", 2, managedBeanDeployment.getManagedBeans()
            .size());

      // make sure managed bean without an explicit name was processed and metadata created
      String nameOfMBWithoutExplicitName = ManagedBeanWithoutExplicitName.class.getSimpleName();
      ManagedBeanMetaData managedBeanWithoutExplicitName = managedBeanDeployment
            .getManagedBean(nameOfMBWithoutExplicitName);
      Assert.assertNotNull("Metadata for " + ManagedBeanWithoutExplicitName.class.getName() + " was not created",
            managedBeanWithoutExplicitName);
      Assert.assertEquals("Unexpected name found on managed bean " + ManagedBeanWithoutExplicitName.class.getName(),
            nameOfMBWithoutExplicitName, managedBeanWithoutExplicitName.getName());
      Assert.assertEquals("Unexpected bean class found on managed bean "
            + ManagedBeanWithoutExplicitName.class.getName(), ManagedBeanWithoutExplicitName.class.getName(),
            managedBeanWithoutExplicitName.getManagedBeanClass());

      // make sure managed bean with an explicit name was processed and metadata created
      ManagedBeanMetaData managedBeanWithExplicitName = managedBeanDeployment.getManagedBean(ManagedBeanWithName.NAME);
      Assert.assertNotNull("Metadata for " + ManagedBeanWithName.class.getName() + " was not created",
            managedBeanWithExplicitName);
      Assert.assertEquals("Unexpected name found on managed bean " + ManagedBeanWithName.class.getName(),
            ManagedBeanWithName.NAME, managedBeanWithExplicitName.getName());
      Assert.assertEquals("Unexpected bean class found on managed bean " + ManagedBeanWithName.class.getName(),
            ManagedBeanWithName.class.getName(), managedBeanWithExplicitName.getManagedBeanClass());

      // make sure a class which is *not* managed bean, isn't processed
      String nameOfUnManagedBean = NotAManagedBean.class.getSimpleName();
      ManagedBeanMetaData notAManagedBean = managedBeanDeployment.getManagedBean(nameOfUnManagedBean);
      Assert.assertNull("Metadata for " + NotAManagedBean.class.getName() + " was unexpectedly created",
            notAManagedBean);

   }
}
