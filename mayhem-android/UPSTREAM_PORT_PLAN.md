# Mayhem Android: upstream integration

Source archive: cardze-mayhem-port-main.zip (user supplied).

Original Linux port: https://github.com/CARDZE-ROSTRUM-git/cardze-mayhem-port
Firmware: https://github.com/portapack-mayhem/mayhem-firmware
Pinned upstream commit: 77798cb09f227de5c58dea48899ddb7448b44a07

The Linux port's CMakeLists.txt requires third_party/mayhem-firmware/firmware/application/ui_navigation.cpp and compiles the original UI plus host SDL2 layer. Its optional PlutoSDR backend is platform/src/sdr_pluto.cpp via libiio.

## Android implementation stages
1. Retrieve exact pinned firmware and Linux port and verify source paths.
2. Cross-compile real Mayhem C++ UI and host platform via Android NDK; replace Linux-only entrypoints with SDL AndroidActivity integration.
3. Implement touchscreen coordinates, density scaling, fullscreen and Android app-scoped storage.
4. Build and validate offline mode with no USB radio attached.
5. Integrate libiio on Android, USB permission handling and PlutoSDR RX.
6. Verify TX separately on physical hardware and obey radio regulations.

Current repository Android scaffold is NOT the real UI. The source checkout workflow below only validates upstream source availability; it does not compile the Android port.

Upstream projects carry their respective open-source licenses; preserve copyright and license notices.
