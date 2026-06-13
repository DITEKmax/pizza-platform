package edu.rutmiit.grpckitchenanalytics.config;

import edu.rutmiit.grpckitchenanalytics.service.OrderAnalyticsServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;

@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server server;
    private boolean running = false;

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(grpcPort)
                    .addService(new OrderAnalyticsServiceImpl())
                    .build()
                    .start();

            running = true;
            log.info("gRPC-сервер кухонной аналитики запущен на порту {}", grpcPort);
            log.info("Сервис: OrderAnalytics.AnalyzeOrder()");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить gRPC-сервер на порту " + grpcPort, e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            log.info("Остановка gRPC-сервера кухонной аналитики...");
            server.shutdown();
            running = false;
            log.info("gRPC-сервер кухонной аналитики остановлен");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
