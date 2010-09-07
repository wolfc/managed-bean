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

import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.remoting.marshal.serializable.SerializableUnMarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InvocationUnMarshaller extends SerializableUnMarshaller
{
   private static class ReplacingObjectInputStream extends ObjectInputStream
   {
      private ReplacingObjectInputStream(InputStream in) throws IOException
      {
         super(in);

         enableResolveObject(true);
      }

      @Override
      protected Object resolveObject(Object obj) throws IOException
      {
         if(obj instanceof MethodSerializer.MethodRef)
            return MethodSerializer.resolveObject((MethodSerializer.MethodRef) obj);
         return super.resolveObject(obj);
      }
   }

   @Override
   public UnMarshaller cloneUnMarshaller() throws CloneNotSupportedException
   {
      return new InvocationUnMarshaller();
   }

   @Override
   public InputStream getMarshallingStream(InputStream inputStream, Map config) throws IOException
   {
      return new ReplacingObjectInputStream(inputStream);
   }
}
