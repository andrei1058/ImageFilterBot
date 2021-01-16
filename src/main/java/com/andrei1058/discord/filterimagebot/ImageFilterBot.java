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
    private static JDA jda;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {

        Pattern tokenPattern = Pattern.compile("^token:");
        Pattern imagePattern = Pattern.compile("^image:");
        Pattern ownerPattern = Pattern.compile("^ownerId:");

        Set<String> images = new LinkedHashSet<>();
        String botToken = null;

        for (String arg : args) {
            if (imagePattern.matcher(arg).find()) {
                String img = arg.replace("image:", "");
                images.add(img);
                System.out.println("Loaded image: " + img);
            } else if (ownerPattern.matcher(arg).find()) {
                try {
                    ImageFilterBot.ownerId = Long.parseLong(arg.replace("ownerId:", ""));
                } catch (Exception exception) {
                    System.out.println("Bad owner id!");
                    return;
                }
            } else if (tokenPattern.matcher(arg).find()) {
                botToken = arg.replace("token:", "");
            }
        }

        if (botToken == null) {
            System.out.println("You need to provide a token!");
            return;
        }

        ImageChecker.init(images);

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

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
