# Description

This repository provides a react-native wrapper around the Zender Player`.
This is currently work in progress. Current version is 0.0.1

# Installation
## Add NPM Package
Normally one would add the package with:
`$ npm install react-native-zender-player --save`

Currently this module is not publicly published yet.

NOTE: 
- installing it from a local directory through `npm link` or install from local directory breaks react-native scripts as they don't handle correctly symlinks.
- install if from a github link `npm install gitgit+ssh://git@github.com:smalltownheroes/react-native-zender-player#v0.0.1`

## Link the native package inside your own project

`$ react-native link react-native-zender-player`

## Configure cocoapods on your project
The native dependencies of this project are provided as cocoapods. 
As this package depends on several non-public cocoapods, this requires a bit more work than usual.

### Install cocoapods (if needed)
- install a recent ruby version
- install the gem bundler `gem install bundler`
- create a `Gemfile` :
```
source "http://rubygems.org"

gem "cocoapods" , "~> 1.5.0"
```
- run `bundle install` to install cocoapods

### Download the necessary pods locally
`react-native-zender-player` depends on the Cocoapods `Zender` and `PhenixSdk`
These need to installed locally. See instructions of each package on how to install.

Note: don't forget to install `git-lfs` (git large file system support) if installing from github

### Create/Extend a Podfile
- in your `$PROJECTDIR/ios` create or extend a podfile
- to create a blank one run `pod init`

```
ENV['COCOAPODS_DISABLE_STATS'] = "true"
# Uncomment the next line to define a global platform for your project
platform :ios, '10.0'

target 'ZenderRNSample' do
  # Uncomment the next line if you're using Swift or would like to use dynamic frameworks
  #use_frameworks!

  # Pods for ZenderRNSample
  pod 'Zender/Core', :path => '/Users/patrick/dev/zender-player-sdk/zender-ios-player/sdk'
  pod 'Zender/Phenix', :path => '/Users/patrick/dev/zender-player-sdk/zender-ios-player/sdk'
  pod 'PhenixSdk' , :git => 'git@github.com:smalltownheroes/phenix-ios-sdk.git' , :branch => 'v2018-10-25'

  target 'ZenderRNSampleTests' do
    inherit! :search_paths
    # Pods for testing
  end

end
```

### Install the Pods
- in your `$PROJECTDIR/ios` do a `pod install`

Note: from now on open `YourProject.xcworkspace` instead of `YourProject.xcodeproj`

## Usage
```javascript
import { ZenderPlayerView } from 'react-native-zender-player';

const zenderTargetId  = "ttttt-ttttt-ttttt-tttt-ttttt"
const zenderChannelId = "ccccc-ccccc-ccccc-cccc-ccccc"

// Example Device Login provider
const zenderAuthentication = {
  provider: "device" ,
  payload: {
    "token": "something-unique-like-the-uuid-of-the-phone",
    "name": "patrick",
    "avatar": "https://example.com/myavatar.png"
  }
}

const zenderConfig = {
  debugEnabled: false,
  deviceToken: "<deviceToken used for push notification>"
}

type Props = {};
export default class App extends Component<Props> {

  onZenderPlayerClose(event) {
    console.log('Zender Player Close Event');
  }

  render() {

    return <ZenderPlayerView
      targetId={ zenderTargetId }
      channelId={ zenderChannelId }
      authentication = { zenderAuthentication }
      config = { zenderConfig }
      onZenderPlayerClose={ this.onZenderPlayerClose }
      style={{ flex: 1 }} />; // be sure to add flex:1 so the view appears full size
  }
}


```

# TODO
- handle correct dealloc of view
- add start/stop function to overcome the React Native View limitation
- ability to pass JSON authentication
- expose all zenderEvents required
- android module setup
