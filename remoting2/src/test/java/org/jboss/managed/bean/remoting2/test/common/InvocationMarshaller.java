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

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.serializable.SerializableMarshaller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InvocationMarshaller extends SerializableMarshaller
{
   private static class ReplacingObjectOutputStream extends ObjectOutputStream
   {
      private ReplacingObjectOutputStream(OutputStream out) throws IOException
      {
         super(out);

         enableReplaceObject(true);
      }

      @Override
      protected Object replaceObject(Object obj) throws IOException
      {
         if(obj instanceof Method)
            return MethodSerializer.replaceObject((Method) obj);
         return super.replaceObject(obj);
      }
   }

   @Override
   public Marshaller cloneMarshaller() throws CloneNotSupportedException
   {
      return new InvocationMarshaller();
   }

   @Override
   public OutputStream getMarshallingStream(OutputStream outputStream, Map config) throws IOException
   {
      return new ReplacingObjectOutputStream(outputStream);
   }
}
