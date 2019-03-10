/*
  Copyright © 2019 Pasqual K. | All rights reserved
 */

package systems.reformcloud.commands;

import systems.reformcloud.PermissionsAddon;
import systems.reformcloud.ReformCloudController;
import systems.reformcloud.commands.interfaces.Command;
import systems.reformcloud.commands.interfaces.CommandSender;
import systems.reformcloud.player.permissions.group.PermissionGroup;
import systems.reformcloud.player.permissions.player.PermissionHolder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author _Klaro | Pasqual K. / created on 10.03.2019
 */

public final class CommandPermissions extends Command implements Serializable {
    public CommandPermissions() {
        super("permissions", "Manage the permissions", "reformcloud.commands.permissions", new String[]{"perms"});
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            List<PermissionGroup> registered = PermissionsAddon.getInstance().getPermissionDatabase()
                    .getPermissionCache().getAllRegisteredGroups();
            registered.sort((os, as) -> {
                int id1 = os.getGroupID();
                int id2 = as.getGroupID();
                return Integer.compare(id1, id2);
            });
            commandSender.sendMessage("The following permissiongroups are registered:");
            registered.forEach(permissionGroup -> commandSender.sendMessage("   - " + permissionGroup.getName() +
                    "/ID=" + permissionGroup.getGroupID()));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            if (PermissionsAddon.getInstance().getPermissionDatabase()
                    .getPermissionCache().getAllRegisteredGroups().stream().filter(e -> e.getName().equals(args[1]))
                    .findFirst().orElse(null) != null) {
                commandSender.sendMessage("PermissionGroup already exists");
                return;
            }

            PermissionGroup permissionGroup = new PermissionGroup(args[1], "", "", "", 1, new HashMap<>());
            PermissionsAddon.getInstance().getPermissionDatabase().createPermissionGroup(permissionGroup);
            PermissionsAddon.getInstance().getPermissionDatabase().update();

            commandSender.sendMessage("PermissionGroup " + args[1] + " was created successfully");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            PermissionGroup permissionGroup = PermissionsAddon.getInstance().getPermissionDatabase()
                    .getPermissionCache().getAllRegisteredGroups().stream().filter(e -> e.getName().equals(args[1]))
                    .findFirst().orElse(null);
            if (permissionGroup == null) {
                commandSender.sendMessage("The PermissionGroup doesn't exists");
                return;
            }

            PermissionsAddon.getInstance().getPermissionDatabase().deletePermissionGroup(permissionGroup);
            PermissionsAddon.getInstance().getPermissionDatabase().update();

            commandSender.sendMessage("The PermissionGroup was deleted successfully");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
            UUID uuid = ReformCloudController.getInstance().getPlayerDatabase().getFromName(args[0]);
            if (uuid == null) {
                commandSender.sendMessage("Could not found uuid of player in database");
                return;
            }

            PermissionHolder permissionHolder = PermissionsAddon.getInstance().getPermissionDatabase().getPermissionHolder(uuid);
            if (permissionHolder == null) {
                commandSender.sendMessage("Could not find PermissionHolder");
                return;
            }

            commandSender.sendMessage("  - " + args[0] + "/Groups=" + permissionHolder.getPermissionGroups() +
                    "/Permissions=" + permissionHolder.getPlayerPermissions());
        } else if (args.length == 3 && args[1].equalsIgnoreCase("addperm")) {
            UUID uuid = ReformCloudController.getInstance().getPlayerDatabase().getFromName(args[0]);
            if (uuid == null) {
                commandSender.sendMessage("Could not found uuid of player in database");
                return;
            }

            PermissionHolder permissionHolder = PermissionsAddon.getInstance().getPermissionDatabase().getPermissionHolder(uuid);
            if (permissionHolder == null) {
                commandSender.sendMessage("Could not find PermissionHolder");
                return;
            }

            permissionHolder.getPlayerPermissions().replace(args[2], !args[2].startsWith("-"));
            PermissionsAddon.getInstance().getPermissionDatabase().updatePermissionHolder(permissionHolder);

            commandSender.sendMessage("The permission " + args[2] + " was added to the user " + args[0]);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("removeperm")) {
            UUID uuid = ReformCloudController.getInstance().getPlayerDatabase().getFromName(args[0]);
            if (uuid == null) {
                commandSender.sendMessage("Could not found uuid of player in database");
                return;
            }

            PermissionHolder permissionHolder = PermissionsAddon.getInstance().getPermissionDatabase().getPermissionHolder(uuid);
            if (permissionHolder == null) {
                commandSender.sendMessage("Could not find PermissionHolder");
                return;
            }

            permissionHolder.getPlayerPermissions().remove(args[2]);
            PermissionsAddon.getInstance().getPermissionDatabase().updatePermissionHolder(permissionHolder);

            commandSender.sendMessage("The permission " + args[2] + " was removed from the user " + args[0]);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("add")) {
            PermissionGroup permissionGroup = PermissionsAddon.getInstance().getPermissionDatabase()
                    .getPermissionCache().getAllRegisteredGroups().stream().filter(e -> e.getName().equals(args[0]))
                    .findFirst().orElse(null);
            if (permissionGroup == null) {
                commandSender.sendMessage("Could not find PermissionGroup " + args[0]);
                return;
            }

            permissionGroup.getPermissions().put(args[2], !args[2].startsWith("-"));
            PermissionsAddon.getInstance().getPermissionDatabase().updatePermissionGroup(permissionGroup);
            PermissionsAddon.getInstance().getPermissionDatabase().update();
        } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
            PermissionGroup permissionGroup = PermissionsAddon.getInstance().getPermissionDatabase()
                    .getPermissionCache().getAllRegisteredGroups().stream().filter(e -> e.getName().equals(args[0]))
                    .findFirst().orElse(null);
            if (permissionGroup == null) {
                commandSender.sendMessage("Could not find PermissionGroup " + args[0]);
                return;
            }

            permissionGroup.getPermissions().remove(args[2]);
            PermissionsAddon.getInstance().getPermissionDatabase().updatePermissionGroup(permissionGroup);
            PermissionsAddon.getInstance().getPermissionDatabase().update();
        }

        commandSender.sendMessage("perms list");
        commandSender.sendMessage("perms <USERNAME> list");
        commandSender.sendMessage("perms <CREATE/DELETE> <GROUPNAME>");
        commandSender.sendMessage("perms <USERNAME> <ADDPERM/REMOVEPERM> <PERMISSION>");
        commandSender.sendMessage("perms <GROUPNAME> <ADD/REMOVE> <PERMISSION>");
    }
}
