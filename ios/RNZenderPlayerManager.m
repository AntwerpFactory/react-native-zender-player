#import "RNZenderPlayerManager.h"
#import "RNZenderPlayerView.h"

#import <React/RCTBridgeModule.h>

@implementation RNZenderPlayerManager

RCT_EXPORT_MODULE()
RCT_EXPORT_VIEW_PROPERTY(targetId, NSString)
RCT_EXPORT_VIEW_PROPERTY(channelId, NSString)
RCT_EXPORT_VIEW_PROPERTY(authentication, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(config, NSDictionary)


RCT_EXPORT_VIEW_PROPERTY(onIosZenderPlayerClose, RCTBubblingEventBlock);

- (UIView *)view
{
   
    return [[ RNZenderPlayerView alloc] init] ;
    
}

@end
