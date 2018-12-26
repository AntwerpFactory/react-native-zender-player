using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Zender.Player.RNZenderPlayer
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNZenderPlayerModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNZenderPlayerModule"/>.
        /// </summary>
        internal RNZenderPlayerModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNZenderPlayer";
            }
        }
    }
}
