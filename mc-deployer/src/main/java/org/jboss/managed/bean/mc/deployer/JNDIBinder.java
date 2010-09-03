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

import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Stop;
import org.jboss.naming.Util;
import org.jboss.reloaded.naming.spi.JavaEEModule;

/**
 * JNDIBinder
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JNDIBinder
{

   private Context context;

   private String jndiName;

   private Object jndiObject;

   public JNDIBinder(Context ctx, String jndiName, Object obj)
   {
      this.context = ctx;
      this.jndiName = jndiName;
      this.jndiObject = obj;
   }

   public JNDIBinder(String jndiName, Object obj)
   {
      this.jndiName = jndiName;
      this.jndiObject = obj;
   }

   @Start
   void bind() throws NamingException
   {
      if (this.context == null)
      {
         throw new IllegalArgumentException("JNDI context is null, cannot bind to jndi name " + this.jndiName);
      }
      
      Util.bind(this.context, this.jndiName, this.jndiObject);
   }

   @Stop
   void unbind() throws NamingException
   {
      if (this.context == null)
      {
         throw new IllegalArgumentException("JNDI context is null, cannot unbind from jndi name " + this.jndiName);
      }

      Util.unbind(this.context, this.jndiName);
   }

   public void setJndiContext(Context ctx)
   {
      if (this.context != null)
      {
         throw new IllegalStateException("JNDI context is already set on " + this.getClass().getSimpleName());
      }
      if (ctx == null)
      {
         throw new IllegalArgumentException("Cannot set context to null");
      }
      this.context = ctx;
   }
   
   public void setJndiContext(JavaEEModule javaeeModule)
   {
      if (this.context != null)
      {
         throw new IllegalStateException("JNDI context is already set on " + this.getClass().getSimpleName());
      }
      if (javaeeModule == null)
      {
         throw new IllegalArgumentException("Cannot use null JavaEEModule");
      }
      this.context = javaeeModule.getContext();
   }
}
