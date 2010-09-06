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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ProcessManager
{
   private EventListenerAggregate<ProcessEventListener> listeners = new EventListenerAggregate<ProcessEventListener>();

   private Process process;
   private BufferedReader errReader;
   private BufferedReader inReader;
   private PrintWriter writer;

   public ProcessManager(Process process)
   {
      this.process = process;
      this.errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      this.inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      this.writer = new PrintWriter(process.getOutputStream(), true);

      Thread thread = new Thread() {
         @Override
         public void run()
         {
            try
            {
               String s;
               while((s = errReader.readLine()) != null)
               {
                  fireEvent(ProcessEvent.Type.ERR, s);
               }
            }
            catch(IOException e)
            {
               e.printStackTrace();
            }
         }
      };
      thread.setDaemon(true);
      thread.start();


      thread = new Thread() {
         @Override
         public void run()
         {
            try
            {
               String s;
               while((s = inReader.readLine()) != null)
               {
                  fireEvent(ProcessEvent.Type.OUT, s);
               }
            }
            catch(IOException e)
            {
               e.printStackTrace();
            }
         }
      };
      thread.setDaemon(true);
      thread.start();
   }

   public void addProcessEventListener(ProcessEventListener listener)
   {
      listeners.add(listener);
   }

   public void destroy()
   {
      process.destroy();
   }

   private void fireEvent(ProcessEvent.Type type, String value)
   {
      fireEvent(new ProcessEvent(this, type, value));
   }

   private void fireEvent(ProcessEvent event)
   {
      for(ProcessEventListener listener : listeners)
      {
         listener.event(event);
      }
   }
}
