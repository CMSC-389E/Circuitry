package cmsc389e.circuitry.networking;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;

public enum CommonKey {
    DECREASE_TAG("Decrease Tag Modifier", Keyboard.KEY_LMENU) {
	@Override
	public boolean test(EntityPlayer player, boolean pressed, boolean down) {
	    return pressed || CircuitryPacketHandler.isPlayerHoldingModifier(player) != down;
	}
    },
    TOGGLE_NODE("Toggle Node", Keyboard.KEY_R) {
	@Override
	public boolean test(EntityPlayer player, boolean pressed, boolean down) {
	    return pressed;
	}
    };

    private final String description;
    private final int keyCode;

    private CommonKey(String description, int keyCode) {
	this.description = description;
	this.keyCode = keyCode;
    }

    public int getKeyCode() {
	return keyCode;
    }

    public abstract boolean test(EntityPlayer player, boolean pressed, boolean down);

    @Override
    public String toString() {
	return description;
    }
}