// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorGnssRtkNtripX5mDevice",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorGnssRtkNtripX5mDevice",
            targets: ["GnssServicePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "GnssServicePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/GnssServicePlugin"),
        .testTarget(
            name: "GnssServicePluginTests",
            dependencies: ["GnssServicePlugin"],
            path: "ios/Tests/GnssServicePluginTests")
    ]
)