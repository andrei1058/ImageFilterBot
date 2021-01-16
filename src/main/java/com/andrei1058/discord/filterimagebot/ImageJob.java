package com.andrei1058.discord.filterimagebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ImageJob implements Runnable {

    private final CompletableFuture<File> downloadJob;
    private final File file;

    public ImageJob(Message.Attachment attachment, GuildMessageReceivedEvent event) {
        this.downloadJob = attachment.downloadToFile(file = new File(ImageFilterBot.DESTINATION_FOLDER, event.getMessageId() + "." + attachment.getFileExtension()));
        System.out.println("Downloading new image...");
    }

    @Override
    public void run() {

    }
}
