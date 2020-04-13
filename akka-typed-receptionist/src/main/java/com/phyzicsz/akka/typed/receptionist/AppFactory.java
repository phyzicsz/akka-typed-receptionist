/*
 * Copyright 2020 phyzicsz <phyzics.z@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phyzicsz.akka.typed.receptionist;

import akka.actor.typed.ActorSystem;
import com.phyzicsz.akka.typed.receptionist.behaviors.x.EventA;
import com.phyzicsz.akka.typed.receptionist.behaviors.y.EventC;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class AppFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppFactory.class);
    private Config config;
    private ActorSystem<Void> actorSystem;
    
    public AppFactory init() {
        LOGGER.info("Initializing App Services");
        String confFile = System.getProperty("application.conf");
        if (confFile != null) {
            LOGGER.debug("Using configuration file {}", confFile);
            config = ConfigFactory.load(confFile);
        } else {
            LOGGER.debug("Using default configuration file");
            config = ConfigFactory.load();
        }
        
        return this;
    }

    /**
     * Create actors.
     *
     * @return fluent interface
     */
    public AppFactory startActors() {
        LOGGER.info("Starting Actors.");
        try {
            actorSystem = ActorSystem.create(GuardianBehavior.create(), "mainBehavior", config);
            
        } catch (NullPointerException ex) {
            LOGGER.error("Actor Failed to start: {}", ex);
        }
        return this;
    }
    
    public AppFactory sendData() throws InterruptedException {
        LOGGER.info("send data");
        Thread.sleep(1000);
        
        for(int i = 0; i < 100; i++) {
            ServiceManager.xrouter().get().tell(new EventA());
        }
        
        for(int i = 0; i < 100; i++) {
            ServiceManager.yrouter().get().tell(new EventC());
        }
        
       
        Thread.sleep(1000);
        actorSystem.terminate();
       
        return this;
    }
}
