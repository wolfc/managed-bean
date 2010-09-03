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
package org.jboss.managed.bean.mc.deployer;

import java.util.Collection;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;
import org.jboss.managed.bean.proxy.impl.ManagedBeanProxyObjectFactory;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;

/**
 * ManagedBeanNamingDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanNamingDeployer extends AbstractDeployer
{
   /**
    * component informer
    */
   private JavaEEComponentInformer javaeeComponentInformer;

   public ManagedBeanNamingDeployer()
   {
      this.setStage(DeploymentStages.REAL);

      this.setInput(ManagedBeanDeploymentMetaData.class);

   }

   @Inject
   public void setJavaEEComponentInformer(JavaEEComponentInformer componentInformer)
   {
      this.javaeeComponentInformer = componentInformer;
   }

   @Override
   public void deploy(DeploymentUnit unit) throws DeploymentException
   {
      ManagedBeanDeploymentMetaData managedBeanDeployment = unit.getAttachment(ManagedBeanDeploymentMetaData.class);

      Collection<ManagedBeanMetaData> managedBeans = managedBeanDeployment.getManagedBeans();
      if (managedBeans == null || managedBeans.isEmpty())
      {
         return;
      }

      for (ManagedBeanMetaData managedBean : managedBeans)
      {
         this.deploy(unit, managedBean);
      }

   }

   private void deploy(DeploymentUnit unit, ManagedBeanMetaData managedBean) throws DeploymentException
   {
      Class<?> beanClass;
      try
      {
         beanClass = this.getBeanClass(unit, managedBean);
      }
      catch (ClassNotFoundException cnfe)
      {
         throw new DeploymentException("Could not load managed bean class " + managedBean.getManagedBeanClass()
               + " in unit " + unit, cnfe);
      }

      String mbManagerIdentifier = ManagedBeanManagerIdentifierGenerator.generateIdentifier(
            this.javaeeComponentInformer, unit, managedBean);

      String moduleName = this.javaeeComponentInformer.getModulePath(unit);
      String jndiName = moduleName + "/" + managedBean.getName();
      JNDIBinder jndiBinder = new JNDIBinder(jndiName, new ManagedBeanProxyObjectFactory(mbManagerIdentifier));

      String jndiBinderName = "managed-bean-jndibinder:" + jndiName;
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory
            .createBuilder(jndiBinderName, JNDIBinder.class.getName());
      builder.setConstructorValue(jndiBinder);
      AbstractInjectionValueMetaData javaeeModuleInjectMetaData = new AbstractInjectionValueMetaData(this
            .getJavaEEModuleMCBeanName(unit));
      builder.addPropertyMetaData("jndiContext", javaeeModuleInjectMetaData);
      unit.addAttachment(BeanMetaData.class + ":" + jndiBinderName, builder.getBeanMetaData(), BeanMetaData.class);

   }

   private Class<?> getBeanClass(DeploymentUnit unit, ManagedBeanMetaData managedBean) throws ClassNotFoundException
   {
      ClassLoader cl = unit.getClassLoader();

      return Class.forName(managedBean.getManagedBeanClass(), false, cl);
   }

   private String getJavaEEModuleMCBeanName(DeploymentUnit deploymentUnit)
   {
      String applicationName = this.javaeeComponentInformer.getApplicationName(deploymentUnit);
      String moduleName = this.javaeeComponentInformer.getModulePath(deploymentUnit);

      final StringBuilder builder = new StringBuilder("jboss.naming:");
      if (applicationName != null)
      {
         builder.append("application=").append(applicationName).append(",");
      }
      builder.append("module=").append(moduleName);
      return builder.toString();
   }
}
