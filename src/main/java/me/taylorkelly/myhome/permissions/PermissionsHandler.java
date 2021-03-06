package me.taylorkelly.myhome.permissions;

import me.taylorkelly.myhome.utils.HomeLogger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsHandler implements IPermissionsHandler {
	private enum PermHandler {
		PERMISSIONSEX, PERMISSIONS3, PERMISSIONS2, GROUPMANAGER, BPERMISSIONS, SUPERPERMS, NONE
	}
	private static PermHandler permplugin = PermHandler.NONE;
	private transient IPermissionsHandler handler = new NullHandler();
	private final transient Plugin plugin;

	public PermissionsHandler(final Plugin plugin) {
		this.plugin = plugin;
		checkPermissions();
	}

	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		return handler.hasPermission(player, node, defaultPerm);
	}

	@Override
	public int getInteger(final Player player, final String node, int defaultInt) {
		return handler.getInteger(player, node, defaultInt);
	}
	
	public void checkPermissions() {
		final PluginManager pluginManager = plugin.getServer().getPluginManager();

		final Plugin permExPlugin = pluginManager.getPlugin("PermissionsEx");
		if (permExPlugin != null && permExPlugin.isEnabled()) {
			if (!(handler instanceof PermissionsExHandler)) {
				permplugin = PermHandler.PERMISSIONSEX;
				String version = permExPlugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using PermissionsEx v"+ version);
				handler = new PermissionsExHandler();
			}
			return;
		}

		final Plugin bPermPlugin = pluginManager.getPlugin("bPermissions");
		if (bPermPlugin != null && bPermPlugin.isEnabled()) {
			if (!(handler instanceof BPermissionsHandler)) {
				permplugin = PermHandler.BPERMISSIONS;
				String version = bPermPlugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using bPermissions"+ version);
				handler = new BPermissionsHandler();
			}
			return;
		}

		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
		if (GMplugin != null && GMplugin.isEnabled()) {
			if (!(handler instanceof GroupManagerHandler)) {
				permplugin = PermHandler.GROUPMANAGER;
				String version = GMplugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using GroupManager v"+ version);
				handler = new GroupManagerHandler(GMplugin);
			}
			return;
		}

		final Plugin permPlugin = pluginManager.getPlugin("Permissions");
		if (permPlugin != null && permPlugin.isEnabled()) {
			if (permPlugin.getDescription().getVersion().charAt(0) == '3') {
				if (!(handler instanceof Permissions3Handler)) {
					permplugin = PermHandler.PERMISSIONS3;
					String version = permPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using Permissions v"+ version);
					handler = new Permissions3Handler(permPlugin);
				}
			} else {
				if (!(handler instanceof Permissions2Handler)) {
					permplugin = PermHandler.PERMISSIONS2;
					String version = permPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using Permissions v"+ version);
					handler = new Permissions2Handler(permPlugin);
				}
			}
			return;
		}

		if (permplugin == PermHandler.NONE) {
			if (!(handler instanceof SuperpermsHandler)) {
				HomeLogger.info("Access Control: Using SuperPerms");
				handler = new SuperpermsHandler(this.plugin);
			}
		}
	}
}
