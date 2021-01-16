package com.andrei1058.discord.filterimagebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BotListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // download and check image attachments
        if (!event.getMessage().getAttachments().isEmpty()) {
            for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                if (attachment.isImage()) {
                    ImageFilterBot.getExecutorService().submit(new ImageJob(attachment, event));
                }
            }
        }
        //
        if (!event.getMessage().getEmbeds().isEmpty()) {
            System.out.println("embed");
            for (MessageEmbed embed : event.getMessage().getEmbeds()) {
                System.out.println("embed aaaa");
                if (embed.isEmpty()) continue;
                if (embed.getThumbnail() != null && embed.getThumbnail().getProxyUrl() != null) {
                    InputStream in;
                    File file = null;
                    try {
                        in = new URL(embed.getThumbnail().getProxyUrl()).openStream();
                        Files.copy(in, Paths.get(event.getMessageId() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                        file = new File(event.getMessageId() + ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null && ImageChecker.getInstance().check(file)) {
                        System.out.println("Thumbnail sus");
                    }
                }
                if (embed.getImage() != null && embed.getImage().getProxyUrl() != null) {
                    InputStream in;
                    File file = null;
                    try {
                        in = new URL(embed.getImage().getProxyUrl()).openStream();
                        Files.copy(in, Paths.get(event.getMessageId() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                        file = new File(event.getMessageId() + ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null && ImageChecker.getInstance().check(file)) {
                        System.out.println("Image sus");
                    }
                }
            }
        }
    }
}
