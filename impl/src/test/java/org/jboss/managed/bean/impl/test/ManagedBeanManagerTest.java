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
package org.jboss.managed.bean.impl.test;

import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.metadata.jbmeta.annotation.processor.ManagedBeanMetaDataCreator;
import org.jboss.managed.bean.spi.ManagedBeanInstance;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;
import org.junit.Test;

/**
 * ManagedBeanManagerTest
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanManagerTest
{

   @Test
   public void testInstanceCreation()
   {
      ManagedBeanMetaData metadata = this.createMetaData(SimpleManagedBean.class);
      ManagedBeanManager<SimpleManagedBean> manager = new ManagedBeanManager<SimpleManagedBean>("Test", SimpleManagedBean.class, metadata);
      
      ManagedBeanInstance<SimpleManagedBean> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
   }
   
   private ManagedBeanMetaData createMetaData(Class<?> klass)
   {
      AnnotationFinder<AnnotatedElement> finder = new DefaultAnnotationFinder<AnnotatedElement>();
      ManagedBeanMetaDataCreator creator = new ManagedBeanMetaDataCreator(finder);
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(klass);
      ManagedBeanDeploymentMetaData managedBeanDeployment = creator.create(classes);
      return managedBeanDeployment.getManagedBeans().iterator().next();
   }
}
