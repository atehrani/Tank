/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;

/**
 * RestExceptionHandler
 * 
 * @author dangleton
 * 
 */
public class ExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);

    @Inject
    private Messages messages;

    public void handle(Throwable t) {
        Throwable root = getRoot(t);
        if (root instanceof ConstraintViolationException) {
            ConstraintViolationException c = (ConstraintViolationException) root;
            for (@SuppressWarnings("rawtypes") ConstraintViolation v : c.getConstraintViolations()) {
                StringBuilder sb = new StringBuilder();
                sb.append(WordUtils.capitalize(v.getPropertyPath().iterator().next().getName()));
                sb.append(' ').append(v.getMessage()).append('.');
                messages.error(sb.toString());
            }
        } else if (!StringUtils.isEmpty(root.getMessage())) {
            messages.error(root.getMessage());
        } else {
            messages.error(root.toString());
        }
    }

    /**
     * @param status
     * @return
     */
    private Throwable getRoot(Throwable t) {
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }
}
