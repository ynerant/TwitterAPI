package fr.galaxyoyo.spigot.twitterapi;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.Arrays;

public class DMExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length < 2)
		{
			sender.sendMessage(ChatColor.RED + "Usage: /dm <user> <message>");
			return true;
		}

		TwitterAPI.instance().dm(args[0], Strings.join(Arrays.copyOfRange(args, 1, args.length), " "), sender);

		return true;
	}
}
