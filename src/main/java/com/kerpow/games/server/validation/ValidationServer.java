package com.kerpow.games.server.validation;

import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.PacketProcessor;
import com.kerpow.games.server.Player;
import com.kerpow.games.server.Server;
import com.kerpow.games.server.ServerInfo;
import io.netty.util.AttributeKey;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public abstract class ValidationServer<TInfo extends ValidationServerInfo> extends Server<TInfo> {

    public static final AttributeKey<Boolean> VALIDATED_ATTR_KEY = AttributeKey.newInstance("Channel.validated");

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final LinkedList<Validator> validators;

    public ValidationServer(TInfo serverInfo, PacketProcessor packetProcessor) {
        super(serverInfo, packetProcessor);
        this.validators = new LinkedList<>();
    }

    public void addValidator(Validator validator) {
        validators.add(validator);
        validator.init();
    }

    @Override
    public final Object transformSender(Object sender, Packet packet) {
        return super.transformSender(sender, packet);
    }

    @Override
    public final boolean checkMessage(Object sender, Packet packet, Object message) {
        Player player = (Player) sender;
        Boolean validated = player.channel.attr(VALIDATED_ATTR_KEY).get();
        if (validated == null || !validated) {
            //trigger validators
            tryValidate(player, packet, message);
            return false;
        }
        return true;
    }

    private void tryValidate(Player player, Packet packet, Object message) {
        for (Validator validator : validators) {
            if (!validator.matches(packet.opcode)) {
                continue;
            }
            validator.validate(player, packet.opcode, message).thenAccept(validated -> {
                if (!validated) {
                    return;
                }
                onValidated(player);
            });
            return;
        }
    }

    public final void onValidated(Player player) {
        player.channel.attr(VALIDATED_ATTR_KEY).set(true);
        validated(player);
    }

    @Override
    public final void connected(Player player) {
        executorService.schedule(() -> {
            Boolean validated = player.channel.attr(VALIDATED_ATTR_KEY).get();
            if (validated == null || !validated) {
                player.channel.attr(Server.PLAYER_ATTRIBUTE_KEY).remove();
                player.channel.close();
            }
        }, getServerInfo().validationTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Called when a player is validated and ready to play
     *
     * @param player The validated player
     */
    protected abstract void validated(Player player);
}
