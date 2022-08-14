![Chokistream](banner.svg)

Nintendo 3DS wireless video capture software, compatible with [BootNTR](https://github.com/44670/BootNTR) and [HzMod](https://chainswordcs.com/horizon-by-sono.html) and the only cross-platform video capture software to support HzMod.

A from-scratch re-implementation of [Snickerstream](https://github.com/RattletraPM/Snickerstream), with planned TARGA support from [TGAHz](https://github.com/ChainSwordCS/TGAHz-Parsing) and [HorizonScreen](hps://github.com/gamingaddictionz03/HorizonM)

Notable features:
 * Portable: it's just a standalone jar file!
 * Various display output options to get it looking just right on your monitor
 * No monitor? No problem! Chokistream can run entirely headless and stream straight to a video file. (currently very buggy)
 * Color correction modes to fix whatever weirdness HzMod may throw your way
 * Dual-Screen HzMod support for version 2017-05-05 (one of two dual-screen versions)

Plus even more planned:
 * TGA (lossless compression) support for HzMod, which will be the only application other than HorizonScreen to do so!
 * Update HzMod settings mid-stream
 * NFC patching for NTR using various patch types (a single patch is currently implemented)
 * Various other minor improvements coming nearly daily during active development

Currently under active development by [Eiim](https://github.com/Eiim), [herronjo](https://github.com/herronjo), and [ChainSwordCS](https://github.com/ChainSwordCS).

# Build Instructions

Written in Java 17 with Gradle 7.5, requires Java 17 to be installed :)

No other requirements. Run `gradlew build` in Windows Command Prompt or `./gradlew build` in a Unix terminal.
