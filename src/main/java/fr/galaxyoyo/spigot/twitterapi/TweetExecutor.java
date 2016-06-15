package fr.galaxyoyo.spigot.twitterapi;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

public class TweetExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Usage: /tweet <message>");
			return true;
		}

		TwitterAPI.instance().tweet(Strings.join(args, " "), sender);

		return true;
	}
}
