package com.andrei1058.discord.filterimagebot;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ImageJob implements Runnable {

    private final File file;
    private final GuildMessageReceivedEvent event;
    private final String imageUrl;

    public ImageJob(Message.Attachment attachment, GuildMessageReceivedEvent event) {
        CompletableFuture<File> downloadJob = attachment.downloadToFile(file = new File(ImageFilterBot.DESTINATION_FOLDER, event.getMessageId() + "." + attachment.getFileExtension()));
        downloadJob.thenRunAsync(this);
        this.event = event;
        this.imageUrl = attachment.getUrl();
    }

    public ImageJob(File file, GuildMessageReceivedEvent event, String imageUrl) {
        this.file = file;
        this.event = event;
        this.imageUrl = imageUrl;
        ImageFilterBot.getExecutor().submit(this);

    }

    @Override
    public void run() {

        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(file.getPath());
        for (String image : ImageFilterBot.getImages()) {

            // load the images to be compared
            BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(image);

            //Create ImageComparison object for it.
            ImageComparison imageComparison = new ImageComparison(expectedImage, actualImage);

            //Also can be configured BEFORE comparing next properties:

            //Threshold - it's the max distance between non-equal pixels. By default it's 5.
            imageComparison.setThreshold(30);

            //RectangleListWidth - Width of the line that is drawn in the rectangle. By default it's 1.
            imageComparison.setRectangleLineWidth(5);

            //DifferenceRectangleFilling - Fill the inside the difference rectangles with a transparent fill. By default it's false and 20.0% opacity.
            imageComparison.setDifferenceRectangleFilling(true, 30.0);

            //ExcludedRectangleFilling - Fill the inside the excluded rectangles with a transparent fill. By default it's false and 20.0% opacity.
            imageComparison.setExcludedRectangleFilling(true, 30.0);


            //MaximalRectangleCount - It means that would get first x biggest rectangles for drawing.
            // by default all the rectangles would be drawn.
            imageComparison.setMaximalRectangleCount(5);

            //MinimalRectangleSize - The number of the minimal rectangle size. Count as (width x height).
            // by default it's 1.
            imageComparison.setMinimalRectangleSize(100);

            //Change the level of the pixel tolerance:
            imageComparison.setPixelToleranceLevel(0.1);

            //After configuring the ImageComparison object, can be executed compare() method:
            ImageComparisonResult imageComparisonResult = imageComparison.compareImages();

            if (imageComparisonResult.getDifferencePercent() < ImageFilterBot.getBanPercentage()) {
                User user = ImageFilterBot.getJda().retrieveUserById(ImageFilterBot.getOwnerId()).complete();
                if (user != null) {
                    user.openPrivateChannel().queue(dm -> dm.sendMessage(new MessageBuilder().append(event.getAuthor().getAsMention()).append(" | ID:").appendCodeLine(event.getAuthor().getId())
                            .append(" on ").appendCodeLine(event.getGuild().getName()).append(" | Difference: ").append(imageComparisonResult.getDifferencePercent())
                            .append("%").append("\n ").append(imageUrl).build()).queue());

                    if (event.getMember() != null) {
                        event.getMember().ban(1, "Filtered image difference: " + imageComparisonResult.getDifferencePercent() + "%").timeout(10, TimeUnit.SECONDS).queue(
                                c -> user.openPrivateChannel().queue(dm -> dm.sendMessage("Banned!").queue())
                        );
                    }

                    event.getMessage().delete().queue();
                }
            } else if (imageComparisonResult.getDifferencePercent() < ImageFilterBot.getWarnPercentage()) {
                TextChannel channel = ImageFilterBot.getJda().getTextChannelById(ImageFilterBot.getChannelId());
                if (channel != null) {
                    channel.sendMessage(new MessageBuilder().append(event.getAuthor().getAsMention()).append(" | ID:").appendCodeLine(event.getAuthor().getId())
                            .append(" on ").appendCodeLine(event.getGuild().getName()).append(" | Difference: ").append(imageComparisonResult.getDifferencePercent())
                            .append("%").append("\n ").append(imageUrl).build()).queue();
                }
            }

            // delete downloaded file
            if (!file.delete()) {
                System.out.println("Could not delete: " + file.getAbsolutePath());
            }
        }
    }
}
