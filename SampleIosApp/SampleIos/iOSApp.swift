import Sample
import SwiftUI

@main
struct iOSApp: App {

    init() {
        MainViewControllerKt.initializeMolApiEditor()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
