#import "React/RCTViewManager.h"
#import <UIKit/UIKit.h>

#import <Zender/ZenderPlayer.h>

@interface RNZenderPlayerView : UIView<ZenderPlayerDelegate>

@property(nonatomic, strong)ZenderPlayer *player;

@property(nonatomic, strong)NSString *targetId;
@property(nonatomic, strong)NSString *channelId;
@property(nonatomic, strong)NSDictionary *authentication;
@property(nonatomic, strong)NSDictionary *config;

@property(nonatomic, copy)RCTBubblingEventBlock onIosZenderPlayerClose;

@end
