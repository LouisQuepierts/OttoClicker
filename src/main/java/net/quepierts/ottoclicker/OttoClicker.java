package net.quepierts.ottoclicker;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = OttoClicker.MODID)
@Mod(OttoClicker.MODID)
public class OttoClicker {
    public static final String MODID = "ottoclicker";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static int delay = 20;
    private static int tick = 0;
    private static boolean enabled = false;

    public static final String CATEGORY = "key.categories.ottoclicker";

    public static final Lazy<KeyMapping> KEY_AUTO_CLICK;
    public static final Lazy<KeyMapping> KEY_DELAY;

    public OttoClicker() {
        LOGGER.info("OttoClick Loaded!");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterKeyMapping);
    }

    public void onRegisterKeyMapping(final RegisterKeyMappingsEvent event) {
        event.register(KEY_AUTO_CLICK.get());
        event.register(KEY_DELAY.get());
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (!enabled) {
            return;
        }

        tick ++;

        if (tick % delay == 0) {
            Minecraft.getInstance().startUseItem();
        }
    }

    @SubscribeEvent
    public static void onKey(final InputEvent.Key event) {
        if (KEY_AUTO_CLICK.get().isDown()) {
            enabled = !enabled;
            if (enabled) {
                MutableComponent component = Component.translatable("ottoclicker.message.on");
                Minecraft.getInstance().gui.setOverlayMessage(component, false);
            } else {
                tick = 0;
                MutableComponent component = Component.translatable("ottoclicker.message.off");
                Minecraft.getInstance().gui.setOverlayMessage(component, false);
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (KEY_DELAY.get().isDown()) {
            int last = delay;

            if (event.getScrollDelta() > 0 && delay < 100) {
                delay++;
            } else if (delay > 1) {
                delay --;
            }

            if (last != delay) {
                MutableComponent component = Component.translatable("ottoclicker.message.speed", delay);
                Minecraft.getInstance().gui.setOverlayMessage(component, false);
            }

            event.setCanceled(true);
        }
    }

    static {
        KEY_AUTO_CLICK = Lazy.of(
                () -> new KeyMapping(
                        "key.ottoclicker.toggle",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_O,
                        CATEGORY
                )
        );

        KEY_DELAY = Lazy.of(
                () -> new KeyMapping(
                        "key.ottoclicker.delay",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_I,
                        CATEGORY
                )
        );
    }
}
