/*
  Copyright © 2019 Pasqual K. | All rights reserved
 */

package systems.reformcloud.commands;

import systems.reformcloud.ReformCloudController;
import systems.reformcloud.commands.utility.Command;
import systems.reformcloud.commands.utility.CommandSender;
import systems.reformcloud.meta.enums.TemplateBackend;
import systems.reformcloud.meta.proxy.ProxyGroup;
import systems.reformcloud.meta.server.ServerGroup;
import systems.reformcloud.network.out.PacketOutDeployServer;

import java.io.Serializable;

/**
 * @author _Klaro | Pasqual K. / created on 10.04.2019
 */

public final class CommandDeploy extends Command implements Serializable {
    public CommandDeploy() {
        super("deploy", "Deploys a template from one client to another", "reformcloud.commands.deploy", new String[0]);
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] args) {
        if (ReformCloudController.getInstance().getCloudConfiguration().getWebAddress() == null) {
            commandSender.sendMessage("Please enable the web server to use template deploy");
            return;
        }

        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("proxy")) {
                ProxyGroup proxyGroup = ReformCloudController.getInstance().getProxyGroup(args[1]);
                if (proxyGroup == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "The proxy group doesn't exists"));
                    return;
                }

                if (proxyGroup.getTemplateOrElseNull(args[2]) == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "The template doesn't exists"));
                    return;
                }

                if (!proxyGroup.getTemplate(args[2]).getTemplateBackend().equals(TemplateBackend.CLIENT)) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Template backend must be a client"));
                    return;
                }

                if (ReformCloudController.getInstance().getConnectedClient(args[3]) == null
                        || ReformCloudController.getInstance().getConnectedClient(args[4]) == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Both clients must be started"));
                    return;
                }

                if (args[3].equalsIgnoreCase(args[4])) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Cannot deploy template to the same client"));
                    return;
                }

                ReformCloudController.getInstance().getChannelHandler().sendPacketSynchronized(
                        args[3], new PacketOutDeployServer(args[1], args[2], true, args[4])
                );
                commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_deploy_trying()
                        .replace("%template%", args[2])
                        .replace("%group%", args[1])
                        .replace("%client1%", args[3])
                        .replace("%client2%", args[4]));
                return;
            } else if (args[0].equalsIgnoreCase("server")) {
                ServerGroup serverGroup = ReformCloudController.getInstance().getServerGroup(args[1]);
                if (serverGroup == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "The server group doesn't exists"));
                    return;
                }

                if (serverGroup.getTemplateOrElseNull(args[2]) == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "The template doesn't exists"));
                    return;
                }

                if (!serverGroup.getTemplate(args[2]).getTemplateBackend().equals(TemplateBackend.CLIENT)) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Template backend must be a client"));
                    return;
                }

                if (ReformCloudController.getInstance().getConnectedClient(args[3]) == null
                        || ReformCloudController.getInstance().getConnectedClient(args[4]) == null) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Both clients must be started"));
                    return;
                }

                if (args[3].equalsIgnoreCase(args[4])) {
                    commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_error_occurred()
                            .replace("%message%", "Cannot deploy template to the same client"));
                    return;
                }

                ReformCloudController.getInstance().getChannelHandler().sendPacketSynchronized(
                        args[3], new PacketOutDeployServer(args[1], args[2], false, args[4])
                );
                commandSender.sendMessage(ReformCloudController.getInstance().getLoadedLanguage().getCommand_deploy_trying()
                        .replace("%template%", args[2])
                        .replace("%group%", args[1])
                        .replace("%client1%", args[3])
                        .replace("%client2%", args[4]));
                return;
            }
        }

        commandSender.sendMessage("deploy <proxy/server> <name> <template> <client1> <client2>");
    }
}