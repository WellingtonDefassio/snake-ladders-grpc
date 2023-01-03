package com.grpc.game.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.grpc.game.Die;
import com.grpc.game.GameState;
import com.grpc.game.Player;
import io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GameStateStreamingResponse implements StreamObserver<GameState> {
    private StreamObserver<Die> diceStreamObserver;
    private CountDownLatch latch;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GameState gameState) {
        List<Player> playerList = gameState.getPlayerList();
        playerList.forEach(p -> System.out.println(p.getName() + ": " + p.getPosition()));
        boolean winner = playerList.stream()
                .anyMatch(p -> p.getPosition() == 100);

        if(winner){
            System.out.println("Game is over");
            this.diceStreamObserver.onCompleted();
        }else {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }
        System.out.println("--------------");
    }

    @Override
    public void onError(Throwable throwable) {
    this.latch.countDown();
    }

    @Override
    public void onCompleted() {
    this.latch.countDown();
    }
    public void setDiceStreamObserver(StreamObserver<Die> streamObserver) {
        this.diceStreamObserver = streamObserver;
    }
    public void roll() {
        int diceValue = ThreadLocalRandom.current().nextInt(1, 7);
        Die dice = Die.newBuilder().setValue(diceValue).build();
        this.diceStreamObserver.onNext(dice);
    }
}
