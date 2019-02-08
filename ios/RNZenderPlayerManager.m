#import "RNZenderPlayerManager.h"
#import "RNZenderPlayerView.h"

#import <React/RCTBridgeModule.h>

@implementation RNZenderPlayerManager

RCT_EXPORT_MODULE()
RCT_EXPORT_VIEW_PROPERTY(targetId, NSString)
RCT_EXPORT_VIEW_PROPERTY(channelId, NSString)
RCT_EXPORT_VIEW_PROPERTY(authentication, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(config, NSDictionary)
//RCT_EXPORT_VIEW_PROPERTY(autoplay, NSBoolean)


RCT_EXPORT_VIEW_PROPERTY(onIosZenderPlayerClose, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onIosZenderPlayerQuizShareCode, RCTBubblingEventBlock);

// This gets executed everytime an element is rendered
// The view dealloc function is called whenever it is not rendered
- (UIView *)view
{
   // NSLog(@"alloc new");
    return [[ RNZenderPlayerView alloc] init] ;
    
}

@end
