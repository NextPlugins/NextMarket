package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.NextMarket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtils {

    public static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean sendUpdateMessage(CommandSender sender) {
        val updateChecker = NextMarket.getInstance().getUpdateChecker();
        if (!updateChecker.canUpdate()) return false;

        val lastRelease = updateChecker.getLastRelease();
        if (lastRelease == null) return false;

        val newVersionComponent = new TextComponent(String.format(
                "  Uma nova versão do NextMarket está disponível (%s » %s)",
                updateChecker.getCurrentVersion(),
                lastRelease.getVersion()
        ));

        val downloadComponent = new TextComponent("  Clique aqui para ir até o local de download.");
        val channelComponent = new TextComponent(TextComponent.fromLegacyText(MessageUtils.colored(
                "  &7Canal de atualização: " + getChannel(lastRelease.isPreRelease() || lastRelease.isDraft()))
        ));

        val hoverText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(MessageUtils.colored("&7Este link irá levar até o github do plugin")));
        val clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, lastRelease.getDownloadURL());

        newVersionComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        downloadComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        newVersionComponent.setHoverEvent(hoverText);
        downloadComponent.setHoverEvent(hoverText);
        channelComponent.setHoverEvent(hoverText);

        newVersionComponent.setClickEvent(clickEvent);
        downloadComponent.setClickEvent(clickEvent);
        channelComponent.setClickEvent(clickEvent);

        val player = sender instanceof Player ? ((Player) sender) : null;
        val spigotPlayer = player == null ? null : player.spigot();

        sender.sendMessage("");

        if (spigotPlayer == null) {
            sender.sendMessage(newVersionComponent.getText());
            sender.sendMessage(downloadComponent.getText());
            sender.sendMessage(channelComponent.getText());
        } else {
            spigotPlayer.sendMessage(newVersionComponent);
            spigotPlayer.sendMessage(downloadComponent);
            spigotPlayer.sendMessage(channelComponent);
        }

        sender.sendMessage("");

        return true;
    }

    private static String getChannel(boolean betaChannel) {
        return betaChannel ? "&6Beta" : "&aEstável";
    }

}
