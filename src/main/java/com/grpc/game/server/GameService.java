package com.grpc.game.server;

import com.grpc.game.Die;
import com.grpc.game.GameServiceGrpc;
import com.grpc.game.GameState;
import com.grpc.game.Player;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase {
    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player cliente = Player.newBuilder().setName("cliente").setPosition(0).build();
        Player server = Player.newBuilder().setName("server").setPosition(0).build();
        return new DieStreamingRequest(responseObserver, cliente, server);
    }
}
