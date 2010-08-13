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

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.managed.bean.jbxb.metadata.annotation.processor.ManagedBeanMetaDataCreator;
import org.jboss.managed.bean.metadata.ManagedBeanDeploymentMetaData;
import org.jboss.metadata.annotation.finder.DefaultAnnotationFinder;
import org.jboss.scanning.annotations.spi.AnnotationIndex;
import org.jboss.scanning.annotations.spi.Element;
import org.jboss.vfs.VirtualFile;

/**
 * A {@link DeploymentStages#POST_CLASSLOADER} deployer which creates {@link ManagedBeanDeploymentMetaData} 
 * out of annotated classes from a {@link DeploymentUnit}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ManagedBeanAnnotationMetaDataDeployer extends AbstractDeployer
{

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(ManagedBeanAnnotationMetaDataDeployer.class);
   
   /**
    * The attachment key that we add to the {@link DeploymentUnit} if a {@link ManagedBeanDeploymentMetaData}
    * is created out of it
    */
   public static String MANAGED_BEAN_DEPLOYMENT_METADATA_ATTACHMENT_KEY = "ManagedBeanDeploymentMetaData"; 

   /**
    * 
    */
   public ManagedBeanAnnotationMetaDataDeployer()
   {
      this.setStage(DeploymentStages.POST_CLASSLOADER);

      // we output ManagedBeanDeploymentMeaData
      this.setOutput(ManagedBeanDeploymentMetaData.class);
      this.addOutput(MANAGED_BEAN_DEPLOYMENT_METADATA_ATTACHMENT_KEY);
   }

   /**
    * 
    */
   @Override
   public void deploy(DeploymentUnit unit) throws DeploymentException
   {
      if (!(unit instanceof VFSDeploymentUnit))
      {
         return;
      }
      VFSDeploymentUnit vfsDeploymentUnit = (VFSDeploymentUnit) unit;

      List<VirtualFile> classpath = vfsDeploymentUnit.getClassPath();
      if (classpath == null || classpath.isEmpty())
      {
         logger.tracef("Skipping unit %s because classpath is empty", unit.getName());
         return;
      }
      AnnotationIndex annotationIndex = unit.getAttachment(AnnotationIndex.class);
      if (annotationIndex == null)
      {
         logger.tracef("Skipping unit %s because AnnotationIndex is absent", unit.getName());
         return;
      }
      Set<Element<ManagedBean, Class<?>>> annotatedElements = annotationIndex.classIsAnnotatedWith(ManagedBean.class);
      if (annotatedElements == null || annotatedElements.isEmpty())
      {
         logger.tracef("Skipping unit %s because no @ManagedBean annotated classes found", unit.getName());
         return;
      }
      Collection<Class<?>> annotatedClasses = new HashSet<Class<?>>(annotatedElements.size());
      for (Element<ManagedBean, Class<?>> element : annotatedElements)
      {
         annotatedClasses.add(element.getAnnotatedElement());
      }
      ManagedBeanMetaDataCreator managedBeanMetaDataCreator = new ManagedBeanMetaDataCreator(new DefaultAnnotationFinder<AnnotatedElement>());
      logger.debugf("Creating metadata for @ManagedBean annotated classes for unit %s", unit.getName());
      // create the metadata out of the annotated classes
      ManagedBeanDeploymentMetaData managedBeanDeploymentMetaData = managedBeanMetaDataCreator.create(annotatedClasses);
      int numManagedBeans = managedBeanDeploymentMetaData.getManagedBeans() == null ? 0 : managedBeanDeploymentMetaData.getManagedBeans().size();
      logger.debugf("%d managed beans found in unit %s", numManagedBeans, unit.getName());
      
      // add it as an attachment to the deployment unit
      unit.addAttachment(MANAGED_BEAN_DEPLOYMENT_METADATA_ATTACHMENT_KEY, managedBeanDeploymentMetaData, ManagedBeanDeploymentMetaData.class);

   }

}
