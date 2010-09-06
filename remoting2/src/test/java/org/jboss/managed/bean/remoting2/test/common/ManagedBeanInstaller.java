/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
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
package org.jboss.managed.bean.remoting2.test.common;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.kernel.Kernel;
import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.manager.ManagedBeanManagerRegistry;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.metadata.jbmeta.annotation.processor.ManagedBeanMetaDataCreator;
import org.jboss.managed.bean.proxy.impl.ManagedBeanManagerRegistryRefAddr;
import org.jboss.managed.bean.proxy.impl.ManagedBeanProxyObjectFactory;
import org.jboss.managed.bean.proxy.impl.ManagedBeanProxyRefAddrType;
import org.jboss.managed.bean.remoting2.test.simple.SimpleManagedBean;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;

import javax.naming.InitialContext;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ManagedBeanInstaller
{
   private Kernel kernel;
   private ManagedBeanManagerRegistry registry;

   private static ManagedBeanMetaData createBeanMetaData(Class<?> cls, String name)
   {
      return createDeploymentMetaData(cls).getManagedBean(name);
   }

   private static ManagedBeanDeploymentMetaData createDeploymentMetaData(Class<?>... cls)
   {
      AnnotationFinder<AnnotatedElement> finder = new DefaultAnnotationFinder<AnnotatedElement>();
      ManagedBeanMetaDataCreator creator = new ManagedBeanMetaDataCreator(finder);
      ManagedBeanDeploymentMetaData managedBeanDeployment = creator.create(Arrays.asList(cls));
      return managedBeanDeployment;
   }

   @Inject(bean="jboss.kernel:service=Kernel")
   public void setKernel(Kernel kernel)
   {
      this.kernel = kernel;
   }

   @Inject
   public void setRegistry(ManagedBeanManagerRegistry registry)
   {
      this.registry = registry;
   }

   public void start() throws Throwable
   {
      Class<SimpleManagedBean> beanClass = SimpleManagedBean.class;
      ManagedBeanMetaData beanMetaData = createBeanMetaData(beanClass, "simple");
      ManagedBeanManager<?> manager = new ManagedBeanManager<SimpleManagedBean>(beanClass, beanMetaData);

      String registryId = "org.jboss.managedbean:name=simple";

      BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(ManagedBeanManager.class.getName());
      builder.setName(registryId);
      builder.setConstructorValue(manager);
      
      kernel.getController().install(builder.getBeanMetaData());

      InitialContext ctx = new InitialContext();

      String className = SimpleManagedBean.class.getName();
      Reference reference = new Reference(className, ManagedBeanProxyObjectFactory.class.getName(), null);
      StringRefAddr addr = new StringRefAddr(ManagedBeanProxyRefAddrType.MANAGER_NAME_REF_ADDR_TYPE, registryId);
      reference.add(addr);
      ManagedBeanManagerRegistryRefAddr regAddr = new ManagedBeanManagerRegistryRefAddr(registry);
      reference.add(regAddr);
      
      ctx.bind("simple", reference);
   }

   public void stop() throws Exception
   {
      InitialContext ctx = new InitialContext();
      
      ctx.unbind("simple");
      
      kernel.getController().uninstall("org.jboss.managedbean:name=simple");
   }
}
