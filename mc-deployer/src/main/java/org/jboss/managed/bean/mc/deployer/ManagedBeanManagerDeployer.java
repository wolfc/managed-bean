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

import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.managed.bean.metadata.ManagedBeanMetaData;

/**
 * Responsible for picking up {@link DeploymentUnit}s which have {@link ManagedBeanDeploymentMetaData}
 * and create a {@link ManagedBeanManager} for each of the {@link ManagedBeanMetaData} contained in
 * that {@link ManagedBeanDeploymentMetaData}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanManagerDeployer extends AbstractDeployer
{

   public ManagedBeanManagerDeployer()
   {
      this.setStage(DeploymentStages.REAL);

      this.setInput(ManagedBeanDeploymentMetaData.class);

      // we output MC bean metadata
      this.setOutput(BeanMetaData.class);
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

      String managedBeanManagerMCBeanName = this.getManagedBeanManagerMCBeanName(unit, managedBean);
      ManagedBeanManager<?> managedBeanManager = new ManagedBeanManager(managedBeanManagerMCBeanName, beanClass, managedBean);
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(managedBeanManagerMCBeanName,
            ManagedBeanManager.class.getName());

      builder.setConstructorValue(managedBeanManager);

      unit.addAttachment(BeanMetaData.class + ":" + managedBeanManagerMCBeanName, builder.getBeanMetaData(),
            BeanMetaData.class);

   }

   private Class<?> getBeanClass(DeploymentUnit unit, ManagedBeanMetaData managedBean) throws ClassNotFoundException
   {
      ClassLoader cl = unit.getClassLoader();

      return cl.loadClass(managedBean.getManagedBeanClass());
   }

   private String getManagedBeanManagerMCBeanName(DeploymentUnit unit, ManagedBeanMetaData managedBean)
   {
      StringBuilder sb = new StringBuilder("org.jboss.managedbean:");
      DeploymentUnit parentUnit = unit.getParent();
      if (parentUnit != null)
      {
         sb.append("parent=");
         sb.append(parentUnit.getName());
      }
      sb.append("unit=");
      sb.append(unit.getName());

      sb.append("name=");
      sb.append(managedBean.getName());

      return sb.toString();
   }

}
