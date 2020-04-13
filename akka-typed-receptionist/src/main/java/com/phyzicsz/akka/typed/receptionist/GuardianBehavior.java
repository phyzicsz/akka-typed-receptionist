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

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

/**
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class GuardianBehavior {

    public static ActorRef<Void> serviceManager;
    
    public static ActorRef<Void> getServiceManager(){
        return serviceManager;
    }
    
    public static Behavior<Void> create() {

        return Behaviors.setup(
                context -> {
                    //spawn behaviors here...
                    serviceManager = context.spawn(ServiceManager.create(), "serviceManager");

                    return Behaviors.same();
                }).unsafeCast();
    }
}
