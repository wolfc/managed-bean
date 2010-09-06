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
package org.jboss.managed.bean.remoting2.test.process;

import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class JavaProcess extends Process
{
   private static final Logger log = Logger.getLogger(JavaProcess.class);
   
   private Process p;
   
   public JavaProcess(Class<?> cls, String... args) throws IOException
   {
      this(cls.getName(), args);
   }

   public JavaProcess(String className, String... args) throws IOException
   {
      String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      // make sure we can run under surefire
      String classPath = System.getProperty("surefire.test.class.path");
      if(classPath == null)
         classPath = System.getProperty("java.class.path");
      log.debug("classPath = " + classPath);
      ArrayList<String> cmdarray = new ArrayList<String>();
      cmdarray.add(java);
      cmdarray.add("-cp");
      cmdarray.add(classPath);
      cmdarray.add(className);
      for(String arg : args)
         cmdarray.add(arg);
      p = Runtime.getRuntime().exec(cmdarray.toArray(new String[0]));      
   }

   @Override
   public OutputStream getOutputStream()
   {
      return p.getOutputStream();
   }

   @Override
   public InputStream getInputStream()
   {
      return p.getInputStream();
   }

   @Override
   public InputStream getErrorStream()
   {
      return p.getErrorStream();
   }

   @Override
   public int waitFor() throws InterruptedException
   {
      return p.waitFor();
   }

   @Override
   public int exitValue()
   {
      return p.exitValue();
   }

   @Override
   public void destroy()
   {
      p.destroy();
   }
}
