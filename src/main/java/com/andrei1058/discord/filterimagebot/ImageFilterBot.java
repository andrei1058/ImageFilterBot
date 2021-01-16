package com.andrei1058.discord.filterimagebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ImageFilterBot {

    public static final String DESTINATION_FOLDER = "downloads";

    private static long ownerId = 0;
    private static long channelId = 0;
    private static JDA jda;
    private static final Set<String> images = new LinkedHashSet<>();
    private static int warnPercentage = -1;
    private static int banPercentage = -1;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {

        Pattern tokenPattern = Pattern.compile("^token:");
        Pattern imagePattern = Pattern.compile("^image:");
        Pattern ownerPattern = Pattern.compile("^ownerId:");
        Pattern channelPattern = Pattern.compile("^channelId:");
        Pattern warnPercentagePattern = Pattern.compile("^warn:");
        Pattern banPercentagePattern = Pattern.compile("^ban:");

        String botToken = null;

        for (String arg : args) {
            if (imagePattern.matcher(arg).find()) {
                String img = arg.replace("image:", "");
                images.add(img);
                System.out.println("Loaded image: " + img);
            } else if (ownerPattern.matcher(arg).find()) {
                try {
                    ImageFilterBot.ownerId = Long.parseLong(arg.replace("ownerId:", ""));
                    System.out.println("Loaded owner id: " + ownerId);
                } catch (Exception exception) {
                    System.out.println("Bad owner id!");
                    return;
                }
            } else if (tokenPattern.matcher(arg).find()) {
                botToken = arg.replace("token:", "");
            } else if (warnPercentagePattern.matcher(arg).find()){
                warnPercentage = Integer.parseInt(arg.replace("warn:", ""));
                System.out.println("Warn: " + warnPercentage);
            } else if (banPercentagePattern.matcher(arg).find()){
                banPercentage = Integer.parseInt(arg.replace("ban:", ""));
                System.out.println("Ban: " + banPercentage);
            }else if (channelPattern.matcher(arg).find()) {
                try {
                    ImageFilterBot.channelId = Long.parseLong(arg.replace("channelId:", ""));
                    System.out.println("Loaded channel id: " + channelId);
                } catch (Exception exception) {
                    System.out.println("Bad channel id!");
                    return;
                }
            }
        }

        if (botToken == null) {
            System.out.println("You need to provide a token!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(botToken).build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        jda.addEventListener(new BotListener());

        File destinationFolder = new File(DESTINATION_FOLDER);
        if (!destinationFolder.exists()) {
            if (!destinationFolder.mkdir()) {
                System.out.println("Could not create folder: " + DESTINATION_FOLDER);
            }
        }
    }

    public static Set<String> getImages() {
        return images;
    }

    public static JDA getJda() {
        return jda;
    }

    public static long getOwnerId() {
        return ownerId;
    }

    public static int getWarnPercentage() {
        return warnPercentage;
    }

    public static int getBanPercentage() {
        return banPercentage;
    }

    public static long getChannelId() {
        return channelId;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
}
