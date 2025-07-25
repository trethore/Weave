package tytoo.weave;

import net.fabricmc.api.ClientModInitializer;

public class WeaveClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("Hello Fabric world from the client side!");
	}
}