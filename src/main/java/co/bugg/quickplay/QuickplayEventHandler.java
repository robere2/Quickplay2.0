package co.bugg.quickplay;

import co.bugg.quickplay.client.gui.InstanceDisplay;
import co.bugg.quickplay.client.gui.config.QuickplayGuiUsageStats;
import co.bugg.quickplay.util.ServerChecker;
import co.bugg.quickplay.util.TickDelay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * Main event handler for Quickplay
 */
public class QuickplayEventHandler {

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new ServerChecker((onHypixel, ip, method) -> {
            Quickplay.INSTANCE.onHypixel = onHypixel;
            Quickplay.INSTANCE.verificationMethod = method;
        });
    }

    @SubscribeEvent
    public void onLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Quickplay.INSTANCE.onHypixel = false;
        Quickplay.INSTANCE.verificationMethod = null;
    }


    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if(Quickplay.INSTANCE.onHypixel && event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            // Only render overlay if there is no other GUI open at the moment or if the GUI is chat (assuming proper settings)
            if(Quickplay.INSTANCE.settings.displayInstance && (Minecraft.getMinecraft().currentScreen == null ||
                    (Quickplay.INSTANCE.settings.displayInstanceWithChatOpen && (Minecraft.getMinecraft().currentScreen instanceof GuiChat)))) {
                InstanceDisplay instanceDisplay = Quickplay.INSTANCE.instanceDisplay;
                instanceDisplay.render(instanceDisplay.getxRatio(), instanceDisplay.getyRatio(), Quickplay.INSTANCE.settings.instanceOpacity);
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        // Prompt the user for usage stats setting every time they join a world until they select an
        // option (at which point promptUserForUsageStats is set to false & ConfigUsageStats is created)
        if(Quickplay.INSTANCE.promptUserForUsageStats)
            new TickDelay(() -> Minecraft.getMinecraft().displayGuiScreen(new QuickplayGuiUsageStats()), 20);
    }
}
