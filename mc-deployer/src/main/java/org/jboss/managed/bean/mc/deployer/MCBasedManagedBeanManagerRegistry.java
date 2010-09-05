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

import org.jboss.dependency.spi.ControllerContext;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.managed.bean.impl.manager.ManagedBeanManager;
import org.jboss.managed.bean.impl.manager.ManagedBeanManagerRegistry;

/**
 * 
 *
 * MC based registry for maintaining {@link ManagedBeanManager}s
 * 
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class MCBasedManagedBeanManagerRegistry implements ManagedBeanManagerRegistry
{

   private KernelController kernelController;
   
   public MCBasedManagedBeanManagerRegistry(KernelController kernelController)
   {
      this.kernelController = kernelController;
   }
   
   @Override
   public ManagedBeanManager<?> get(String name) throws IllegalArgumentException
   {
      ControllerContext ctx = this.kernelController.getInstalledContext(name);
      if (ctx == null)
      {
         throw new IllegalArgumentException("No managed bean manager registered with id " + name);
      }
      
      Object target = ctx.getTarget();
      if (target instanceof ManagedBeanManager<?> == false)
      {
         throw new IllegalArgumentException("Object registered with " + name + " is not of type " + ManagedBeanManager.class);
      }
      return (ManagedBeanManager<?>) target;
   }
   
   @Override
   public boolean isRegistered(String name)
   {
      ControllerContext ctx = this.kernelController.getInstalledContext(name);
      return ctx != null;
   }
}
