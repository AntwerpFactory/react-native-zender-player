#import "RNZenderPlayerView.h"
#import <Zender/ZenderPlayer.h>
#import <Zender/ZenderPlayerConfig.h>
#import <Zender/ZenderAVPlayerController.h>
#import <Zender/ZenderAuthentication.h>
#import <Zender/ZenderPhenixPlayerController.h>
#import <Zender/ZenderApiClient.h>

#import <Zender/ZenderLogger.h>

// Note: We prefix our object with RN here to avoid clashes
@implementation RNZenderPlayerView

- (instancetype)init
{
    
    if (self = [super init]) {
        
        NSString *targetId = _targetId;
        NSString *channelId =_channelId;
        
        // Override the endpoint for now
        NSString *playerEndpoint=@"https://player2-native.zender.tv";
        //  playerEndpoint=@"https://player2-native.staging.zender.tv";
        
        ZenderPlayerConfig *playerConfig = [ZenderPlayerConfig configWithTargetId:targetId channelId:channelId];
        
        // Register Videoplayers - Note: Order Matters
        ZenderPhenixPlayerController *zenderPhenixPlayer = [ZenderPhenixPlayerController new];
        [playerConfig registerVideoController:zenderPhenixPlayer];
        
        ZenderAVPlayerController *zenderAVPlayer = [ZenderAVPlayerController new];
        [playerConfig registerVideoController:zenderAVPlayer];
        
        _player = [ZenderPlayer new];
        _player.config = playerConfig;
        
        [playerConfig overridePlayerEndpointPrefix:playerEndpoint];
        _player.delegate = self;
        
        
        [self addSubview:self.player.view];
        
        // NOTE: we can't start the player during init
        // This is because TargetId & ChannelId are only initialized in the setters
        //  [_player start];
        
    }
    
    return self;
}


// This makes sure the view gets resized correctly
-(void)layoutSubviews
{
    [super layoutSubviews];
    self.player.view.frame = self.frame;
}

#pragma marker Zender Setters

-(void)setTargetId:(NSString*) targetId {
    _targetId = targetId;
    _player.config.targetId = targetId;
    
    [self startZenderPlayerWhenSettersComplete];
}

-(void)setChannelId:(NSString*) channelId {
    _channelId = channelId;
    _player.config.channelId = channelId;
    
    [self startZenderPlayerWhenSettersComplete];
}

-(void)setAuthentication:(NSDictionary *)authentication {
    _authentication = authentication;
    
    /*
     ZenderAuthentication *deviceAuthentication = [ZenderAuthentication authenticationWith:@{
     @"token": [[[UIDevice currentDevice] identifierForVendor] UUIDString],
     @"name": username,
     @"avatar": @"https://example.com/myavatar.png"
     } provider:@"device"];
     */
    
    NSString *authenticationProvider = [authentication objectForKey:@"provider"];
    NSDictionary *authenticationPayload = [authentication objectForKey:@"payload"];
    
    // Check if we got both payload & provider
    if ((authenticationProvider!=nil) && (authenticationPayload!=nil)) {
        _player.authentication = [ZenderAuthentication authenticationWith:authenticationPayload provider:authenticationProvider];
    }
    
    [self startZenderPlayerWhenSettersComplete];
}

-(void)setConfig:(NSDictionary *)config {
    
    _config = config;
    
    // If config is nil, skip
    if (config == nil) {
        return;
    }
    
    NSNumber *checkDebugEnabled = [config objectForKey:@"debugEnabled"];
    NSNumber *checkDeviceToken = [config objectForKey:@"deviceToken"];
    
    if (checkDebugEnabled!=nil) {
        BOOL debugEnabled = [config objectForKey:@"debugEnabled"];
        //     NSLog(@"BOOL DebugEnabled : %@", debugEnabled ? @"Yes" : @"No");
        
        if (debugEnabled) {
            [[ZenderLogger shared] setLevel:ZenderLogger_LEVEL_DEBUG];
            [_player.config enableDebug:debugEnabled];
        }
    }
    
    if (checkDeviceToken!=nil) {
        _deviceToken = [config objectForKey:@"deviceToken"];
        
        ZenderUserDevice *userDevice = [ZenderUserDevice new];
        userDevice.token = _deviceToken;
        [_player.config setUserDevice:userDevice];
        
    }
    
    [self startZenderPlayerWhenSettersComplete];
}

// Currently we check to see if we got all setters before starting the player
- (void) startZenderPlayerWhenSettersComplete {
    if (_targetId == nil) { return; }
    if (_channelId == nil) { return; }
    if (_authentication == nil) { return ; }
    if (_config == nil) { return ; }
    
    self.player.view.frame = self.frame;
    
    [self setupApiSubscribeDevice];
    [_player start];
}

- (void)dealloc
{
    //NSLog(@"alloc dealloc");
    [_player stop];
    _player = nil;
}



#pragma marker Zender Events

- (void)zenderPlayer:(ZenderPlayer *)zenderPlayer onZenderPlayerClose:(NSDictionary *)payload {
    
    if(self.onIosZenderPlayerClose){
        self.onIosZenderPlayerClose(@{});
    }
}

- (void)zenderPlayer:(ZenderPlayer *)zenderPlayer onZenderQuizShareCode:(NSDictionary *)payload {
    
    NSString *shareCode = [payload valueForKey:@"shareCode"];
    NSString *shareText = [payload valueForKey:@"shareText"];
    NSString *text = [payload valueForKey:@"text"];
    
    NSMutableDictionary *shareDict = [NSMutableDictionary new];
    
    if(self.onIosZenderPlayerQuizShareCode){
        if (shareText != nil) {
            [shareDict setValue:shareText forKey:@"shareText"];
        }
        if (text != nil) {
            [shareDict setValue:text forKey:@"shareText"];
        }
        if (shareCode != nil) {
            [shareDict setValue:shareCode forKey:@"shareCode"];
        }
        self.onIosZenderPlayerQuizShareCode([NSDictionary dictionaryWithDictionary:shareDict]);
    }
}



- (void)setupApiSubscribeDevice {
    
    if (_deviceToken == nil) {
        return;
    }
    
    ZenderApiClient *apiClient = [[ZenderApiClient alloc] initWithTargetId:_targetId channelId:_channelId ];
    
    apiClient.authentication = self.player.authentication;
    
    ZenderUserDevice *userDevice = [ZenderUserDevice new];
    userDevice.token = _deviceToken;
    
    [apiClient login:^(NSError *error, ZenderSession *session) {
        ZLog_Debug(@"RNZenderPlayer", @"api login callback completed");
        
        if (error == nil) {
            [apiClient registerDevice:userDevice andCompletionHandler:^(NSError *error) {
                if (error == nil) {
                    ZLog_Debug(@"RNZenderPlayer", @"api registerDevice callback completed");
                } else {
                    ZLog_Error(@"RNZenderPlayer", @"api registerDevice callback failed");
                }
            }];
        } else {
            ZLog_Error(@"RNZenderPlayer", @"failed in example %@",error);
        }
    }];
    
}


@end

