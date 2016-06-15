package fr.galaxyoyo.spigot.twitterapi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;

public class TwitterAPI extends JavaPlugin
{
	private static TwitterAPI instance;
	private Twitter twitter;

	@Override
	public void onEnable()
	{
		instance = this;

		try
		{
			Metrics metrics = new Metrics(this);
			metrics.start();
		}
		catch (IOException e)
		{
			getLogger().warning("Unable to start metrics :(");
			e.printStackTrace();
		}
		getLogger().info("You're running the TwitterAPI, made by galaxyoyo. Thanks for downloading!");

		getCommand("tweet").setExecutor(new TweetExecutor());

		if (!getDataFolder().isDirectory())
			getDataFolder().mkdir();
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists())
		{
			try
			{
				IOUtils.copy(getClass().getResourceAsStream("/config.yml"), FileUtils.openOutputStream(configFile));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		YamlConfiguration config = (YamlConfiguration) getConfig();
		AuthenticationType type;
		try
		{
			type = AuthenticationType.valueOf(config.getString("preferred-connection-mode", "oauth").toUpperCase());
		}
		catch (IllegalArgumentException ex)
		{
			getLogger().warning("Unknown authentication method: " + config.getString("preferred-connection-mode") + ", considering OAuth.");
			type = AuthenticationType.OAUTH;
		}

		ConfigurationBuilder builder = new ConfigurationBuilder();

		switch (type)
		{
			case OAUTH2:
				builder.setOAuth2AccessToken(config.getString("oauth2-access-token"));
			case OAUTH:
				builder.setOAuthConsumerKey(config.getString("oauth-consumer-key"))
						.setOAuthConsumerSecret(config.getString("oauth-consumer-key-secret"))
						.setOAuthAccessToken(config.getString("oauth-access-token"))
						.setOAuthAccessTokenSecret(config.getString("oauth-access-token-secret"));
				break;
			case XAUTH:
				builder.setUser(config.getString("xauth-username"))
						.setPassword(config.getString("xauth-password"));
				break;
		}

		twitter = new TwitterFactory(builder.build()).getInstance();

		try
		{
			twitter.help().getAPIConfiguration();
			getLogger().info("Logged in successfully!");
		}
		catch (TwitterException e)
		{
			if (e.getStatusCode() == 400)
			{
				getLogger().warning("There was an authentication error while logging in TwitterAPI. If it is the first time you run this API, don't forget to edit the config by " +
						"adding your tokens :) To get your tokens, please visit https://apps.twitter.com/");
				getServer().getPluginManager().disablePlugin(this);
			}
			else
			{
				getLogger().warning("An unknown error ocurred while logging in TwitterAPI.");
				getLogger().warning("Plugin will be disabled now.");
				e.printStackTrace();
				getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public static TwitterAPI instance()
	{
		return instance;
	}

	public Twitter getTwitter()
	{
		return twitter;
	}

	public void tweet(String tweet)
	{
		tweet(tweet, null);
	}

	public void tweet(String tweet, CommandSender sender)
	{
		try
		{
			Status status = twitter.updateStatus(tweet);
			if (sender != null)
			{
				String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
				sender.sendMessage("[TwitterAPI] Succefully tweeted: '" + tweet + "' on the account: @" + twitter.getScreenName() + "\n\nTweet Link: " + url);
			}
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
	}

	private enum AuthenticationType
	{
		OAUTH, OAUTH2, XAUTH
	}
}
