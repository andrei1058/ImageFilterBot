# ImageFilterBot
A Discord Bot made in Java that filters images (upload/ embed). It will send warnings when similar images are uploaded or will even ban users based on start up arguments.

(Made ASAP for a friend)

# How to use
Start command line:
```
java -jar botJar.jar token:botTokenHere ownerId:userIdToReceiveBanNotifications channelId:channelWhereToSendWarning image:imageFilterPath image:image2FilterPath warn:warnWhenDifferenceIsLowerThanThis ban:whenDifferenceIsLowerThanThis
```
Example:
```
java -jar filterImageBot.jar token:fn49fu3b9f34fb39bf ownerId:2312312312312 channelId:21312412342121412 image:images/filter1.jar warn:30 ban:10
```