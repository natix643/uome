package cz.pikadorama.uome.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBroker {

    private static final Map<Topic, List<Subscriber>> subscribers = new HashMap<>();

    /**
     * Register subscriber implementation for the given topic. If the registration is successful
     * (e.i. the subscriber hasn't been registered before), the {@link Subscriber#receive(Topic)}}
     * method is called afterwards.
     * <p/>
     * Note that if you register your subscriber for {@link Topic#ALL}, you can also register it for other types too.
     * In such case, the subscriber can be notified twice. It is your responsibility to handle registration properly.
     *
     * @param subscriber subscriber to register
     * @param topic      topic
     * @throws IllegalStateException in case parameters are null or the subscriber has already been registered
     */
    public synchronized static void subscribe(Subscriber subscriber, Topic topic) {
        List<Subscriber> processorsForType = subscribers.get(topic);
        if (processorsForType == null) {
            List<Subscriber> newSubscribers = new ArrayList<>();
            newSubscribers.add(subscriber);
            subscribers.put(topic, newSubscribers);
        } else {
            if (processorsForType.contains(subscriber)) {
                throw new IllegalStateException("Subscriber " + subscriber + " is already registered.");
            } else {
                subscribers.get(topic).add(subscriber);
            }

        }
        subscriber.receive(topic);
    }

    /**
     * Unregister subscriber.
     *
     * @param subscriber subscriber to unregister
     * @param topic      topic
     * @throws IllegalStateException in case parameters are null or the subscriber has already been unregistered
     *                               or not registered at all
     */
    public synchronized static void unsubscribe(Subscriber subscriber, Topic topic) {
        if (subscribers.containsKey(topic)) {
            if (!subscribers.get(topic).contains(subscriber)) {
                throw new IllegalStateException("Subscriber" + subscriber + " is not registered.");
            }
            subscribers.get(topic).remove(subscriber);
        } else {
            throw new IllegalStateException("Subscriber" + subscriber + " is not registered.");
        }
    }

    /**
     * Notify all subscribers of the given {@link Topic}. Few rules to follow:
     * <ul>
     * <li>{@link Topic} == {@link Topic#ALL}: subscribers registered in as {@link Topic#ALL}
     * and also all subscribers for other topics are notified</li>
     * <li>{@link Topic} != {@link Topic#ALL}: only subscriber registered for the type are notified</li>
     * </ul>
     *
     * @param topic topic
     */
    public synchronized static void publish(Topic topic) {
        switch (topic) {
            case ALL:
                for (Map.Entry<Topic, List<Subscriber>> entry : subscribers.entrySet()) {
                    for (Subscriber subscriber : entry.getValue()) {
                        subscriber.receive(entry.getKey());
                    }
                }
                break;
            default:
                if (subscribers.containsKey(topic)) {
                    for (Subscriber subscriber : subscribers.get(topic)) {
                        subscriber.receive(topic);
                    }
                }
        }
    }

}
