# Fictional Octo System
***Uses [liplum/MultiCrafterLib](https://github.com/liplum/MultiCrafterLib), though you don't need to install it into your client in order for this mod to work.***

Update 1.1 brings you a _new_ asteroid group and a planet for you to explore with brand-new blocks and mechanics such as underground ores, gameplay unique from Serpulo/Erekir, etc.
## Credits
- Team founder: [@Polar4ik](https://github.com/Polar4ik) (root$Polar4ik#6727)
- Programmer: [@Slotterleet](https://github.com/Slotterleet) (Slotterleet#7897)
- Artist: [@NotTheGrawx](https://github.com/NotTheGrawx) (MyatiyMotilyok#8455)
- Composer: Saigo no-nozomi#1206
- Translator: [@Slotterleet](https://github.com/Slotterleet) (Slotterleet#7897)

## Special Thanks
- [@kapzduke](https://github.com/kapzduke) - Contributor & Pre-alpha Tester

## Building for Desktop Testing

1. Install JDK **17**.
2. Run `gradlew jar` [1].
3. Your mod jar will be in the `build/libs` directory. **Only use this version for testing on desktop. It will not work with Android.**
To build an Android-compatible version, you need the Android SDK. You can either let GitHub Actions handle this, or set it up yourself.

## Building Locally

Building locally takes more time to set up, but shouldn't be a problem if you've done Android development before.
1. Download the Android SDK, unzip it and set the `ANDROID_HOME` environment variable to its location.
2. Make sure you have API level 30 installed, as well as any recent version of build tools (e.g. 30.0.1)
3. Add a build-tools folder to your PATH. For example, if you have `30.0.1` installed, that would be `$ANDROID_HOME/build-tools/30.0.1`.
4. Run `gradlew deploy`. If you did everything correctly, this will create a jar file in the `build/libs` directory that can be run on both Android and desktop.