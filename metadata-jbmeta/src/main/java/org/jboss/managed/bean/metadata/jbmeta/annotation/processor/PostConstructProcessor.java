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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.jboss.managed.bean.metadata.MethodMetadata;
import org.jboss.managed.bean.metadata.impl.ManagedBeanMetaDataImpl;
import org.jboss.managed.bean.metadata.impl.MethodMetadataImpl;
import org.jboss.metadata.annotation.creator.AbstractFinderUser;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.finder.AnnotationFinder;

/**
 * PostConstructProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class PostConstructProcessor extends AbstractFinderUser implements Processor<ManagedBeanMetaDataImpl, Method>
{

   private static final Collection<Class<? extends Annotation>> PROCESSABLE_ANNOTATION_TYPES;

   static
   {
      PROCESSABLE_ANNOTATION_TYPES = new HashSet<Class<? extends Annotation>>(1);
      PROCESSABLE_ANNOTATION_TYPES.add(PostConstruct.class);
   }


   public PostConstructProcessor(AnnotationFinder<AnnotatedElement> finder)
   {
      super(finder);
      
   }

   @Override
   public Collection<Class<? extends Annotation>> getAnnotationTypes()
   {
      return PROCESSABLE_ANNOTATION_TYPES;
   }

   @Override
   public void process(ManagedBeanMetaDataImpl metaData, Method method)
   {
      PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
      if (postConstruct == null)
      {
         return;
      }
      Class<?> paramTypes[] = method.getParameterTypes();
      if (paramTypes == null)
      {
         paramTypes = new Class<?>[0];
      }
      String[] params = new String[paramTypes.length];
      int i = 0;
      for (Class<?> paramType : paramTypes)
      {
         params[i++] = paramType.getName();
      }
      MethodMetadata methodMetadata = new MethodMetadataImpl(method.getName(), params);
      metaData.setPostConstructMethod(methodMetadata);

   }

}
