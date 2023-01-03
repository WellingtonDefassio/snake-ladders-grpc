package com.grpc.game.client;

import com.grpc.game.Die;
import com.grpc.game.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameClientTest {


    private GameServiceGrpc.GameServiceStub stub;


    @BeforeAll
    public void setup() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();

        this.stub = GameServiceGrpc.newStub(channel);

    }

    @Test
    public void clientGame() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        GameStateStreamingResponse gameStateStreamingResponse = new GameStateStreamingResponse(countDownLatch);
        StreamObserver<Die> diceStreamObserver = this.stub.roll(gameStateStreamingResponse);
        gameStateStreamingResponse.setDiceStreamObserver(diceStreamObserver);
        gameStateStreamingResponse.roll();
        countDownLatch.await();
    }

}
