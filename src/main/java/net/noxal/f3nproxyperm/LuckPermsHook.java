package net.noxal.f3nproxyperm;

import com.velocitypowered.api.proxy.Player;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

import java.util.Optional;

public class LuckPermsHook {
    private static final LuckPerms api = LuckPermsProvider.get();
    private final EventSubscription<?> subscription;

    public LuckPermsHook() {
        subscription = api.getEventBus().subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculate);
    }

    private void onUserDataRecalculate(UserDataRecalculateEvent event) {
        Optional<Player> player = F3NProxyPerm.proxy.getPlayer(event.getUser().getUniqueId());
        player.ifPresent(value -> F3NProxyPerm.instance.updateOpLevel(value));
    }

    public void unregister() {
        if (subscription != null) {
            subscription.close();
        }
    }
}