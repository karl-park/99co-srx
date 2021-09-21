//
//  NotificationService.m
//  NotificationService
//
//  Created by Wei Liang Tan on 13/3/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "NotificationService.h"

@interface NotificationService ()

@property (nonatomic, strong) void (^contentHandler)(UNNotificationContent *contentToDeliver);
@property (nonatomic, strong) UNMutableNotificationContent *bestAttemptContent;

@end

@implementation NotificationService

- (void)didReceiveNotificationRequest:(UNNotificationRequest *)request withContentHandler:(void (^)(UNNotificationContent * _Nonnull))contentHandler {
    self.contentHandler = contentHandler;
    self.bestAttemptContent = [request.content mutableCopy];
    
    // Modify the notification content here...
//    self.bestAttemptContent.title = [NSString stringWithFormat:@"%@ [modified]", self.bestAttemptContent.title];
    
    NSDictionary *userInfo = request.content.userInfo;
    
    NSString *imageUrl = [self getImageURLInUserInfo:userInfo];
    
    //if there is image
    if (imageUrl && [imageUrl isKindOfClass:[NSString class]] && imageUrl.length > 0) {
        NSURL *url = [NSURL URLWithString:imageUrl];
        [self downloadImageFrom:url withCompletionHandler:^(UNNotificationAttachment * _Nullable attachment) {
            if (attachment) {
                self.bestAttemptContent.attachments = [NSArray arrayWithObject:attachment];
                self.contentHandler(self.bestAttemptContent);
                return;
            } else {
                self.contentHandler(self.bestAttemptContent);
            }
        }];
    } else {
        self.contentHandler(self.bestAttemptContent);
    }
    
    
    
}

- (void)serviceExtensionTimeWillExpire {
    // Called just before the extension will be terminated by the system.
    // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
    self.contentHandler(self.bestAttemptContent);
}

// get image url fro notification
-(nullable NSString *)getImageURLInUserInfo: (NSDictionary *)userInfo {
    //image from userInfo
    NSString *imageUrl = [userInfo objectForKey:@"url"];

    //if don't have image url, check if there is from fcm_options
    if (!imageUrl || imageUrl.length == 0) {
        NSDictionary *fcm_options = [userInfo objectForKey:@"fcm_options"];
        if (fcm_options && [fcm_options isKindOfClass:[NSDictionary class]]) {
            imageUrl = [fcm_options objectForKey:@"image"];
        }
    }
    
    return imageUrl;
}

-(void)downloadImageFrom: (NSURL *)url withCompletionHandler: (void (^)(UNNotificationAttachment * _Nullable))completionHandler {
    NSURLSessionDownloadTask *task = [[NSURLSession sharedSession] downloadTaskWithURL:url completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        // 1. Test URL and escape if URL not OK
        if (!location) {
            completionHandler(nil);
            return;
        }
        
        // 2. Get current's user temporary directory path
        NSString *directory =  NSTemporaryDirectory();
        
        // 3. Add proper ending to url path, in the case .jpg (The system validates the content of attached files before scheduling the corresponding notification request. If an attached file is corrupted, invalid, or of an unsupported file type, the notification request is not scheduled for delivery.)
        NSString *uniqueURLending = [NSString stringWithFormat:@"%@.jpg", [[NSProcessInfo processInfo] globallyUniqueString]];
        directory = [directory stringByAppendingPathComponent:uniqueURLending];
        
        NSURL *urlPath = [NSURL fileURLWithPath:directory];
        
        @try {
            NSError *error;
            [[NSFileManager defaultManager] moveItemAtURL:location toURL:urlPath error:&error];
            UNNotificationAttachment *attachment = [UNNotificationAttachment attachmentWithIdentifier:@"picture" URL:urlPath options:nil error:&error];
            
            completionHandler(attachment);
        } @catch (NSException *exception) {
            completionHandler(nil);
        }
    }];
    [task resume];
}

@end
