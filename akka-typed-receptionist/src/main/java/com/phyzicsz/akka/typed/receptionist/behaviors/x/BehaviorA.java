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
package com.phyzicsz.akka.typed.receptionist.behaviors.x;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import com.phyzicsz.akka.typed.receptionist.ServiceManager;

/**
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class BehaviorA {
    private final ActorContext<EventX> context;

    private BehaviorA(ActorContext<EventX> context) {
        this.context = context;
    }

    public static Behavior<EventX> create() {
        return Behaviors.setup(context -> {
                    context
                            .getSystem()
                            .receptionist()
                            .tell(Receptionist.register(ServiceManager.eventXServiceKey, context.getSelf()));

                    return new BehaviorA(context).behavior();
                });
    }

    public Behavior<EventX> behavior() {
        return Behaviors.receive(EventX.class)
                .onMessage(EventA.class, this::onEventA)
                .onMessage(EventB.class, this::onEventB)
                .build();
    }

    private Behavior<EventX> onEventA(EventA event) {
        context.getLog().info("onEventA!");
        return Behaviors.same();
    }
    
     private Behavior<EventX> onEventB(EventB event) {
        context.getLog().info("onEventB!");
        return Behaviors.same();
    }

}
