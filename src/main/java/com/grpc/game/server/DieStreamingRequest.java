package com.grpc.game.server;

import com.grpc.game.Die;
import com.grpc.game.GameState;
import com.grpc.game.Player;
import io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import io.grpc.stub.StreamObserver;

public class DieStreamingRequest implements StreamObserver<Die> {

    private StreamObserver<GameState> gameStateStreamObserver;
    private Player client;
    private Player server;

    public DieStreamingRequest(StreamObserver<GameState> gameStateStreamObserver, Player client, Player server) {
        this.gameStateStreamObserver = gameStateStreamObserver;
        this.client = client;
        this.server = server;
    }

    @Override
    public void onNext(Die die) {
        this.client = this.getNewPlayerPosition(this.client, die.getValue());
        if (this.client.getPosition() != 100) {
            this.server = this.getNewPlayerPosition(this.server, ThreadLocalRandom.current().nextInt(1, 7));
        }
        this.gameStateStreamObserver.onNext(this.getGameState());
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private GameState getGameState() {
        return GameState.newBuilder()
                .addPlayer(this.client)
                .addPlayer(this.server)
                .build();
    }

    private Player getNewPlayerPosition(Player player, int diceValue) {
        int position = player.getPosition() + diceValue;
        position = SnakesAndLaddersMap.getPosition(position);
        if (position <= 100) {
            player = player.toBuilder()
                    .setPosition(position)
                    .build();
        }
        return player;
    }


}
