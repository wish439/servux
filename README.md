Servux
==============
Servux is a server-side mod that provides extra support/features for some client-side mods when playing on a server.

**License**: LGPLv3 (see LICENSE file).  
This fork adds LAN support and is not affiliated with the original authors.
See the [LICENSE](LICENSE.txt) file for details.

~~**Servux itself is never needed on the clients or in single player**~~,
~~it's only needed/useful on the dedicated server side in multiplayer.~~
**Now Servux can work on Lan in my modified version**.

This branch is based [sakura-ryoko/masa's version](https://github.com/sakura-ryoko/servux) with some changes.

In version 0.1.x it only has one thing, which is sending structure bounding boxes for MiniHUD so that it can render those also in multiplayer.

For compiled builds (= downloads), see [github Releases](https://github.com/wish439/servux/releases)

Compiling
=========
* Clone the repository
* Open a command prompt/terminal to the repository directory
* run 'gradlew build'
* The built jar file will be in build/libs/
