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
package org.jboss.managed.bean.impl.test.unit;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.test.InterceptorA;
import org.jboss.managed.bean.impl.test.InterceptorOneWithPostConstruct;
import org.jboss.managed.bean.impl.test.InterceptorTwoWithPostConstruct;
import org.jboss.managed.bean.impl.test.ManagedBeanWithInterceptors;
import org.jboss.managed.bean.impl.test.ManagedBeanWithPostConstruct;
import org.jboss.managed.bean.impl.test.ManagedBeanWithPostConstructInterceptors;
import org.jboss.managed.bean.impl.test.SimpleManagedBean;
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
   public void testInstanceCreation() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(SimpleManagedBean.class);
      ManagedBeanManager<SimpleManagedBean> manager = new ManagedBeanManager<SimpleManagedBean>(SimpleManagedBean.class, metadata);
      
      ManagedBeanInstance<SimpleManagedBean> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
   }
   
   @Test
   public void testInterceptorInManagedBeanInstance() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(ManagedBeanWithInterceptors.class);
      ManagedBeanManager<ManagedBeanWithInterceptors> manager = new ManagedBeanManager<ManagedBeanWithInterceptors>(ManagedBeanWithInterceptors.class, metadata);
      
      ManagedBeanInstance<ManagedBeanWithInterceptors> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
      Collection<Object> interceptors = mbInstance.getInterceptors();
      
      Assert.assertNotNull("No interceptors assoicated with managed bean instance", interceptors);
      Assert.assertEquals("Unexpected number of interceptors associated with managed bean instance", 1, interceptors.size());
      Assert.assertTrue("Unexpected interceptor type in managed bean instance",  InterceptorA.class.isInstance(interceptors.iterator().next()));
   }
   
   @Test
   public void testManagedBeanPostConstruct() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(ManagedBeanWithPostConstruct.class);
      ManagedBeanManager<ManagedBeanWithPostConstruct> manager = new ManagedBeanManager<ManagedBeanWithPostConstruct>(ManagedBeanWithPostConstruct.class, metadata);
      
      ManagedBeanInstance<ManagedBeanWithPostConstruct> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
      ManagedBeanWithPostConstruct bean = mbInstance.getInstance();
      Assert.assertEquals("@PostConstruct on managed bean was not invoked", ManagedBeanWithPostConstruct.POST_CONSTRUCT_CALLED, bean.getValue());
      
   }
   
   @Test
   public void testManagedBeanPostConstructWithInterceptors() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(ManagedBeanWithPostConstructInterceptors.class);
      ManagedBeanManager<ManagedBeanWithPostConstructInterceptors> manager = new ManagedBeanManager<ManagedBeanWithPostConstructInterceptors>(ManagedBeanWithPostConstructInterceptors.class, metadata);
      
      ManagedBeanInstance<ManagedBeanWithPostConstructInterceptors> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
      ManagedBeanWithPostConstructInterceptors bean = mbInstance.getInstance();
      List<String> invokedPostConstructInterceptors = bean.getInvokedPostConstructInterceptors();
      
      Assert.assertEquals("Unexpected number of post-construct interceptors invoked for managed bean: " + ManagedBeanWithPostConstructInterceptors.class, 2, invokedPostConstructInterceptors.size());
      Assert.assertTrue(InterceptorOneWithPostConstruct.class + " wasn't invoked on post-construct of managed bean: " + ManagedBeanWithPostConstructInterceptors.class, invokedPostConstructInterceptors.contains(InterceptorOneWithPostConstruct.class.getName()));
      Assert.assertTrue(InterceptorTwoWithPostConstruct.class + " wasn't invoked on post-construct of managed bean: " + ManagedBeanWithPostConstructInterceptors.class, invokedPostConstructInterceptors.contains(InterceptorTwoWithPostConstruct.class.getName()));
   }
   
   
   @Test
   public void testPostConstructOnManagedBeanAndInterceptors() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(ManagedBeanWithPostConstructInterceptors.class);
      ManagedBeanManager<ManagedBeanWithPostConstructInterceptors> manager = new ManagedBeanManager<ManagedBeanWithPostConstructInterceptors>(ManagedBeanWithPostConstructInterceptors.class, metadata);
      
      ManagedBeanInstance<ManagedBeanWithPostConstructInterceptors> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
      ManagedBeanWithPostConstructInterceptors bean = mbInstance.getInstance();
      List<String> invokedPostConstructInterceptors = bean.getInvokedPostConstructInterceptors();
      
      Assert.assertEquals("Unexpected number of post-construct interceptors invoked for managed bean: " + ManagedBeanWithPostConstructInterceptors.class, 2, invokedPostConstructInterceptors.size());
      Assert.assertEquals("Incorrect interceptor invocation ordering for managed bean: " + ManagedBeanWithPostConstructInterceptors.class, InterceptorTwoWithPostConstruct.class.getName(), invokedPostConstructInterceptors.get(0));
      Assert.assertEquals("Incorrect interceptor invocation ordering for managed bean: " + ManagedBeanWithPostConstructInterceptors.class, InterceptorOneWithPostConstruct.class.getName(), invokedPostConstructInterceptors.get(1));
   }
   
   @Test
   public void testManagedBeanInterceptorOrdering() throws Exception
   {
      ManagedBeanMetaData metadata = this.createMetaData(ManagedBeanWithPostConstructInterceptors.class);
      ManagedBeanManager<ManagedBeanWithPostConstructInterceptors> manager = new ManagedBeanManager<ManagedBeanWithPostConstructInterceptors>(ManagedBeanWithPostConstructInterceptors.class, metadata);
      
      ManagedBeanInstance<ManagedBeanWithPostConstructInterceptors> mbInstance = manager.createManagedBeanInstance();
      
      Assert.assertNotNull("Managed bean instance is null", mbInstance);
      Assert.assertNotNull("No managed bean instance was created", mbInstance.getInstance());
      
      ManagedBeanWithPostConstructInterceptors bean = mbInstance.getInstance();
      List<String> invokedPostConstructInterceptors = bean.getInvokedPostConstructInterceptors();
      
      Assert.assertEquals("Unexpected number of post-construct interceptors invoked for managed bean: " + ManagedBeanWithPostConstructInterceptors.class, 2, invokedPostConstructInterceptors.size());
      Assert.assertTrue(InterceptorOneWithPostConstruct.class + " wasn't invoked on post-construct of managed bean: " + ManagedBeanWithPostConstructInterceptors.class, invokedPostConstructInterceptors.contains(InterceptorOneWithPostConstruct.class.getName()));
      Assert.assertTrue(InterceptorTwoWithPostConstruct.class + " wasn't invoked on post-construct of managed bean: " + ManagedBeanWithPostConstructInterceptors.class, invokedPostConstructInterceptors.contains(InterceptorTwoWithPostConstruct.class.getName()));
   }

   
   /**
    * Creates managed bean metadata for the passed {@link Class}
    * @param klass
    * @return
    */
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
