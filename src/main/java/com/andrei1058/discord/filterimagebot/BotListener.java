package com.andrei1058.discord.filterimagebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class BotListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().equals(ImageFilterBot.getJda().getSelfUser())) return;
        // download and check image attachments
        if (!event.getMessage().getAttachments().isEmpty()) {
            for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                if (attachment.isImage()) {
                    new ImageJob(attachment, event);
                }
            }
        }

        //
        if (!event.getMessage().getEmbeds().isEmpty()) {
            for (MessageEmbed embed : event.getMessage().getEmbeds()) {
                if (embed.isEmpty()) continue;
                if (embed.getThumbnail() != null && embed.getThumbnail().getUrl() != null) {
                    File file = new File(ImageFilterBot.DESTINATION_FOLDER, event.getMessageId() + ".jpg");
                    try {
                        TheKing_DownloadFileFromURL(embed.getThumbnail().getUrl(), file.getPath());
                        new ImageJob(file, event, embed.getThumbnail().getUrl());
                    } catch (Exception ignored) {
                        //stfu
                    }
                }
                if (embed.getImage() != null && embed.getImage().getUrl() != null) {
                    File file = new File(ImageFilterBot.DESTINATION_FOLDER, event.getMessageId() + ".jpg");
                    try {
                        TheKing_DownloadFileFromURL(embed.getThumbnail().getUrl(), file.getPath());
                        new ImageJob(file, event, embed.getThumbnail().getUrl());
                    } catch (Exception ignored) {
                        //stfu
                    }
                }
            }
        }
    }


    // But are u denied access?
// well here is the solution.
    public static void TheKing_DownloadFileFromURL(String search, String path) throws IOException {

        // This will get input data from the server
        InputStream inputStream = null;

        // This will read the data from the server;
        OutputStream outputStream = null;

        try {
            // This will open a socket from client to server
            URL url = new URL(search);

            // This user agent is for if the server wants real humans to visit
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

            // This socket type will allow to set user_agent
            URLConnection con = url.openConnection();

            // Setting the user agent
            con.setRequestProperty("User-Agent", USER_AGENT);

            //Getting content Length
            //int contentLength = con.getContentLength();
            //System.out.println("File contentLength = " + contentLength + " bytes");


            // Requesting input data from server
            inputStream = con.getInputStream();

            // Open local file writer
            outputStream = new FileOutputStream(path);

            // Limiting byte written to file per loop
            byte[] buffer = new byte[2048];

            // Increments file size
            int length;
            @SuppressWarnings("unused") int downloaded = 0;

            // Looping until server finishes
            while ((length = inputStream.read(buffer)) != -1) {
                // Writing data
                outputStream.write(buffer, 0, length);
                downloaded += length;
                //System.out.println("Downlad Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");


            }
        } catch (Exception ex) {
            //Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // closing used resources
        // The computer will not be able to use the image
        // This is a must
        if (outputStream != null)
            outputStream.close();
        if (inputStream != null)
            inputStream.close();
    }
}
