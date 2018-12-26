
#import "RNZenderPlayer2.h"

@implementation RNZenderPlayer2

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(init: (NSDictionary *)options) {
    NSLog(@"this works fine");
    
}

@end

 





