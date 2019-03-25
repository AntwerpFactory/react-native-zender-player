# Description

This repository provides a react-native wrapper around the Zender Player. Current version is 1.0.0
The react-native packages has several native dependencies. As these dependencies are not publicly available, they need to be manually added/installed.

# Installation
## Add NPM Package
Normally one would add the package with:
`$ npm install react-native-zender-player --save`

Currently this module is not publicly published yet.

NOTE: 
- installing it from a local directory through `npm link` or install from local directory breaks react-native scripts as they don't handle correctly symlinks.
- install it from a github link `npm install git+ssh://git@github.com:smalltownheroes/react-native-zender-player#v1.0.0

## Link the native package inside your own project

`$ react-native link react-native-zender-player`


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

  onZenderPlayerQuizShareCode(event) {
    console.log('Zender Player Share code Event');
    console.log('Share text: '+event.shareText);
    console.log('Share code: '+event.shareCode);
  }

  render() {

    return <ZenderPlayerView
      targetId={ zenderTargetId }
      channelId={ zenderChannelId }
      authentication = { zenderAuthentication }
      config = { zenderConfig }
      onZenderPlayerClose={ this.onZenderPlayerClose }
      onZenderPlayerQuizShareCode={ this.onZenderPlayerQuizShareCode }
      style={{ flex: 1 }} />; // be sure to add flex:1 so the view appears full size
  }
}
```


#iOS native setup

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

Note: don't forget to install `git-lfs` (git large file system support) if installing phenix-sdk from github

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

#Android native setup
## Base setup
For android , all necessary files are included in the react-native library ; 

- RNZenderPlayer depends on both `zender_core, zender_logger, zender_phenix` and the phenix-sdk .aar files
- The buildToolsVersion is currently `28.0.3` , this can be changed in the android/build.gradle file of the module if necessary

For reference Zender also depends on:
```
     implementation 'com.google.code.gson:gson:2.7'
     implementation 'com.squareup.picasso:picasso:2.5.2'
```

Depending on your react-native version the config of your React Native project may require some tweaking.

## Allow backup flag
Depending on your React native version used, you may have to add the flag android:allowBackup to your app `AndroidManifest.xml`

```
 <manifest xmlns:android="http://schemas.android.com/apk/res/android"
+    xmlns:tools="http://schemas.android.com/tools"
     package="com.zenderrnsample">

     <uses-permission android:name="android.permission.INTERNET" />
     <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

     <application
+        tools:replace="android:allowBackup"

```

## Android 9+ - Apache HTTP client deprecation
Starting from Android 9 , android does not include the legacy org.http package anymore. This is currently required for the Zender Logger solution.
To make it work you need to add the following to your React Native Android Manifest. More info at <https://developer.android.com/about/versions/pie/android-9.0-changes-28>


`<uses-library android:name="org.apache.http.legacy" android:required="false"/>`

## Soft-input pan
Android has different ways of dealing with the focus when typing on the keyboard.
React-Native by default uses `android:windowSoftInputMode="adjustResize">`. This setting resizes the view to allow for the keyboard.

When using the keyboard in zender , we want a different behavior: scroll up the view instead of resizing . This is the equivalent of the `adjustPan` modus.
To have the expected behavior Zender forces the softInputModus `adjustPan`

## Orientation
The Zender player autorotates, if you don't want this behaviour you need to fix the app rotation

## Changelog
- 1.0.0: fixes background/foreground, connectionfeedback flex layout rendering, image fullwidth, allow auto-orientation
- 0.0.3: react-native android version
- 0.0.2: react-native ios version
