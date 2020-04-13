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
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.GroupRouter;
import akka.actor.typed.javadsl.Routers;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;

import com.phyzicsz.akka.typed.receptionist.behaviors.x.BehaviorA;
import com.phyzicsz.akka.typed.receptionist.behaviors.x.EventX;
import com.phyzicsz.akka.typed.receptionist.behaviors.y.BehaviorB;
import com.phyzicsz.akka.typed.receptionist.behaviors.y.EventY;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class ServiceManager {

    public static final ServiceKey<EventX> eventXServiceKey = ServiceKey.create(EventX.class, "eventXServiceKey");
    public static final ServiceKey<EventY> eventYServiceKey = ServiceKey.create(EventY.class, "eventYServiceKey");

    private static final AtomicReference<ActorRef<EventX>> xrouter = new AtomicReference<>();
    private static final AtomicReference<ActorRef<EventY>> yrouter = new AtomicReference<>();

    private final ActorContext<Receptionist.Listing> context;

    private ServiceManager(ActorContext<Receptionist.Listing> context) {
        this.context = context;
    }

    public static AtomicReference<ActorRef<EventX>> xrouter() {
        return xrouter;
    }

    public static AtomicReference<ActorRef<EventY>> yrouter() {
        return yrouter;
    }

    public static Behavior<Void> create() {
        return Behaviors.setup(
                (ActorContext<Receptionist.Listing> context) -> {
                    ActorRef<Receptionist.Command> receptionist = context.getSystem().receptionist();

                    //create behaviors for EventX processing
                    receptionist.tell(Receptionist.subscribe(eventXServiceKey, context.getSelf().narrow()));
                    context.spawnAnonymous(BehaviorA.create());
                    context.spawnAnonymous(BehaviorA.create());
                    GroupRouter<EventX> xgroup = Routers.group(eventXServiceKey);
                    xrouter.set(context.spawn(xgroup, "event-x-processors"));

                    //create behaviors for EventY processing
                    receptionist.tell(Receptionist.subscribe(eventYServiceKey, context.getSelf().narrow()));
                    context.spawnAnonymous(BehaviorB.create());
                    context.spawnAnonymous(BehaviorB.create());
                    GroupRouter<EventY> ygroup = Routers.group(eventYServiceKey);
                    yrouter.set(context.spawn(ygroup, "event-y-processors"));

                    return new ServiceManager(context).behavior();
                })
                .unsafeCast(); // Void

    }

    private Behavior<Receptionist.Listing> behavior() {
        return Behaviors.receive(Receptionist.Listing.class)
                .onMessage(Receptionist.Listing.class, this::onListing)
                .build();
    }

    private Behavior<Receptionist.Listing> onListing(Receptionist.Listing listing) {
        context.getLog().info("onListing");
        ServiceKey key = listing.getKey();
        Set<ActorRef<?>> refs = listing.getServiceInstances(key);
        refs.forEach((ref) -> {
            context.getLog().info("adding service: {}:{}", key.id(), ref.path().toString());

        });

        return Behaviors.same();
    }

}
