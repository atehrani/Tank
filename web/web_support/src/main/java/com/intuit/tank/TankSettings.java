package com.intuit.tank;

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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * 
 * TAnkSettings applicationScoped settings like is standalone etc
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
@Named
public class TankSettings implements Serializable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TankSettings.class);
    private static final long serialVersionUID = 1L;
    private TankConfig config = new TankConfig();
    private List<VMRegion> regions;

    @PostConstruct
    public void init() {
        regions = new ArrayList<VMRegion>(config.getVmManagerConfig().getRegions());
    }

    /**
     * 
     * @return
     */
    public boolean isStandalone() {
        return config.getStandalone();
    }

    /**
     * 
     * @param region
     * @return
     */
    public boolean hasRegionConfigured(String region) {
        boolean ret = false;
        if (config.needsReload()) {
            init();
        }
        try {
            for (VMRegion r : regions) {
                if (r.name().equalsIgnoreCase(region)) {
                    ret = true;
                    break;
                }
            }
        } catch (Exception e) {
            LOG.error("Error finding region: " + e, e);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public List<VMRegion> getVmRegions() {
        if (config.needsReload()) {
            init();
        }
        return regions;
    }

    /**
     * 
     * @return
     */
    public String getControllerUrl() {
        return config.getControllerBase();
    }

}
