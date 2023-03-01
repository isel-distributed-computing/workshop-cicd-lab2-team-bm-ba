package todolist.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import todolist.notificationservice.model.EventModel;
import todolist.notificationservice.repository.EventRepository;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class NotificationService //{
        implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    @Value("${spring.rabbitmq.queue}")
    String QUEUE_NAME;
    @Value("${spring.rabbitmq.host}")
    private String HOST;
    @Value("${spring.rabbitmq.username}")
    private String USERNAME;
    @Value("${spring.rabbitmq.password}")
    private String PASSWORD;


    @Autowired
    private EventRepository eventRepository;

    /*
    Autowired used for demonstrations purposes.
    It can be a static solution, but we can have an API
     supporting registering to populate the list.
     */
    @Autowired(required = false)
    private List<INotificationStrategy> notificationStrategyList;

    private void logDB(EventModel evt) {
        // Save event in database
        //TODO
        logger.debug("Storing evt {}-{} in database", evt.getUsername(), evt.getAction());

        eventRepository.save(evt);
    }

    private void notify(EventModel evt) {
        logger.info(String.format("Received '%s'", evt));

        logDB(evt);
        for (INotificationStrategy s : notificationStrategyList) {
            s.sendNotification(evt);
        }

    }

    @Override
    public void run(String... args) throws Exception {

        RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handle(IOException.class)
                .handle(TimeoutException.class)
                .handle(ConnectException.class)
                .withDelay(Duration.ofSeconds(10))
                .withMaxRetries(3)
                .build();

        // connect with failsafe policy
        Connection connection = Failsafe.with(retryPolicy).get(this::connectAmqp);

        //create channel and queue
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        logger.info(String.format("Waiting for messages on queue %s", QUEUE_NAME));

        //Callback to handle messages
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            try {
                String message = new String(delivery.getBody(), "UTF-8");

                //convert json to java object
                ObjectMapper objectMapper = new ObjectMapper();
                EventModel evt = objectMapper.readValue(message, EventModel.class);
                //Notify all
                this.notify(evt);
            } catch (Exception ex) {
                logger.error("handling messages: ", ex);
            }
            logger.info(String.format("Waiting for messages on queue %s", QUEUE_NAME));
        };

        //Set up handler
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    private Connection connectAmqp() throws IOException, TimeoutException {
        logger.info(String.format("Trying to connect to %s", HOST));
        //creating connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        return factory.newConnection();
    }

}
