package nl.dykam.dev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

class ServiceListener implements Listener {
    private NestedKitManager manager;

    public ServiceListener(NestedKitManager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onServiceRegister(ServiceRegisterEvent event) {
        if(event.getProvider().getService() == KitManager.class) {
            manager.add((KitManager) event.getProvider().getProvider());
        }
    }
    @EventHandler
    private void onServiceUnregister(ServiceUnregisterEvent event) {
        if(event.getProvider().getService() == KitManager.class) {
            manager.remove((KitManager) event.getProvider().getProvider());
        }
    }
}
