import PropTypes from 'prop-types';
import React from 'react';
import {requireNativeComponent} from 'react-native';

class ZenderPlayerView extends React.Component {
  _onZenderPlayerClose = (event) => {
    if (!this.props.onZenderPlayerClose) {
      return;
    }

    // process raw event
    this.props.onZenderPlayerClose(event.nativeEvent);
  }

  _onZenderPlayerQuizShareCode = (event) => {
    if (!this.props.onZenderPlayerQuizShareCode) {
      return;
    }

    // process raw event
    this.props.onZenderPlayerQuizShareCode(event.nativeEvent);
  }

  render() {
    return <RNZenderPlayer 
         {...this.props} 
         onIosZenderPlayerClose={this._onZenderPlayerClose} 
         onIosZenderPlayerQuizShareCode={this._onZenderPlayerQuizShareCode} 
         // ^^ In the ZenderPlayerView we use onIosZenderPlayerClose to avoid conflict with the zender callbacks
    />
  }

}

ZenderPlayerView.propTypes = {
  targetId: PropTypes.string,
  channelId: PropTypes.string,
  onZenderPlayerClose: PropTypes.func, // This is how we expose it to the client
  onZenderPlayerQuizShareCode: PropTypes.func // This is how we expose it to the client
}

ZenderPlayerView.defaultProps = {
  targetId: "unknown",
  channelId: "unknown"
}

// RNZenderPlayer is automagically mapped to RNZenderPlayerManager by requireNativeComponent
var RNZenderPlayer = requireNativeComponent('RNZenderPlayer', ZenderPlayerView);

module.exports = { ZenderPlayerView };
