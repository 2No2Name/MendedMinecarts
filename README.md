# Mended Minecarts

Mended Minecarts makes minecarts a lot more understandable and predictable for redstoners and other players. Being able
to see where a minecart actually is allows creating precisely timed contraptions more easily. Furthermore Mended
Minecarts makes the speed limit of minecarts customizable and can prevent minecarts from derailing when going fast
around corners.

## Download & Installation

Mended Minecarts requires the Fabric mod loader. The `mendedminecarts-<version>.jar` should be placed in
the `.minecraft/mods` folder.

## Features

/mendedminecarts setting value

Available settings:

- AccurateClientMinecarts: Simulate Minecarts on the client like on the server
- NoClientCartInterpolation: Avoid vanilla's clientside minecart position smoothing
- AlwaysSyncCartPosition: Server always sends the cart position to the client for accuracy
- CartSpeed: Customizable minecart speed limit
- DerailingCartFix: Minecarts do not derail
- RotateCartToRail: Rotates minecarts to avoid carts on neighboring straight rails pushing each other
- DisplayCartData:
    - Position
    - Velocity (vector)
    - Speed (absolute value)
    - FillLevel (storage carts only)
    - OnGround
    - InWater
    - SlowdownRate (on rail)
    - EstimatedDistance (on rail)
    - BoundingBox
    - HopperLocked (hopper carts only)
    - Wobble
    - DataPrecision (number of floating point digits in the display)

## Setup for Programmers

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the
IDE that you are using.

## License

MIT License