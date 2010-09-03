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
package org.jboss.managed.bean.metadata.jbmeta.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashSet;

import javax.interceptor.Interceptors;

import org.jboss.interceptor.reader.InterceptorMetadataUtils;
import org.jboss.interceptor.reader.ReflectiveClassMetadata;
import org.jboss.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.managed.bean.metadata.impl.ManagedBeanMetaDataImpl;
import org.jboss.metadata.annotation.creator.AbstractFinderUser;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.finder.AnnotationFinder;

/**
 * InterceptorsProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class InterceptorsProcessor<T extends AnnotatedElement> extends AbstractFinderUser
      implements
         Processor<ManagedBeanMetaDataImpl, T>
{

   private static final Collection<Class<? extends Annotation>> PROCESSABLE_ANNOTATION_TYPES;

   static
   {
      PROCESSABLE_ANNOTATION_TYPES = new HashSet<Class<? extends Annotation>>(1);
      PROCESSABLE_ANNOTATION_TYPES.add(Interceptors.class);
   }

   public InterceptorsProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
   }

   @Override
   public Collection<Class<? extends Annotation>> getAnnotationTypes()
   {
      return PROCESSABLE_ANNOTATION_TYPES;
   }

   @Override
   public void process(ManagedBeanMetaDataImpl managedBean, T annotatedElement)
   {
      Interceptors interceptors = annotatedElement.getAnnotation(Interceptors.class);
      if (interceptors == null)
      {
         return;
      }
      for (Class<?> interceptor : interceptors.value())
      {
         InterceptorMetadata interceptorMetaData = InterceptorMetadataUtils
               .readMetadataForInterceptorClass(ReflectiveClassMetadata.of(interceptor));
         
         managedBean.addInterceptor(interceptorMetaData);
      }

   }

}
