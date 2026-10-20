# Ball Sort Puzzle (Android + AdMob)

A complete color-sorting puzzle game: endless auto-generated levels (every one checked solvable),
undo, restart, hints, extra tube, coins, and AdMob ads with GDPR consent.

## Ads included

| Ad | Where | Rule |
|---|---|---|
| Banner (adaptive) | Bottom of screen | Always |
| Interstitial | Between levels | From level 4, every 3 levels, max once per 60 s |
| Rewarded | Hint, Extra tube, 3x coins after a level | Player chooses to watch |

Frequency rules are at the top of `app/src/main/java/com/ballsort/game/AdsManager.java`.
Coin prices are at the top of `MainActivity.java`.

## 1. Build on GitHub

1. Create a new GitHub repository.
2. Upload **everything** in this folder, including the hidden `.github` folder
   (the build file is `.github/workflows/build.yml`).
3. Open the **Actions** tab. The "Build APK" run starts on every push (or press **Run workflow**).
4. When it finishes (about 5 minutes), open the run and download from **Artifacts**:
   - `ballsort-debug-apk` – install on your phone for testing (shows **test ads**)
   - `ballsort-release-apk` – uses your real ad IDs
   - `ballsort-release-aab` – the file you upload to Google Play

## 2. Put in your own settings (`gradle.properties`)

- `APPLICATION_ID` – change `com.yourstudio.ballsort` to your own unique name,
  e.g. `com.yourname.ballsort`. **It cannot be changed after publishing.**
- `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID`, `ADMOB_REWARDED_ID` –
  create the app and 3 ad units (Banner, Interstitial, Rewarded) at https://apps.admob.com
  and paste the IDs.

Debug builds always use Google's test ads, so you can't accidentally click your own real ads
(that gets AdMob accounts banned). Never tap real ads in your own release build.

## 3. Signing key for Google Play (do this once, keep it forever)

Without a key, the release is signed with a debug key: fine for testing, rejected by Google Play.

Create a key (on PC with Java, or on Android with Termux: `pkg install openjdk-17`):

```
keytool -genkeypair -v -keystore release.jks -alias ballsort -keyalg RSA -keysize 2048 -validity 10000
base64 -w 0 release.jks > release.b64
```

In GitHub: **Settings → Secrets and variables → Actions → New repository secret**, add:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | full contents of `release.b64` |
| `KEYSTORE_PASSWORD` | the keystore password you typed |
| `KEY_ALIAS` | `ballsort` |
| `KEY_PASSWORD` | the key password (often the same) |

Back up `release.jks` and the passwords somewhere safe. **Never** commit the `.jks` file.
(If you use Play App Signing, this becomes your upload key.)

## 4. Before publishing

- Host an `app-ads.txt` on your developer website (AdMob shows the line to add).
- Privacy policy URL is required in Play Console because the app shows ads.
- In Play Console: Content rating, "Contains ads" = Yes, Data safety (AdMob collects device IDs).
- Target audience: choose **13+ or 18+**, not children. Kids' apps have strict ad rules and earn less.
- Store graphics are in the `store/` folder (512 icon + 1024x500 feature graphic).
  Take phone screenshots from the debug APK.

## Project layout

```
app/src/main/java/com/ballsort/game/
  MainActivity.java    screens, coins, boosters, win popup
  GameView.java        drawing, tapping, animation, undo
  GameState.java       game rules
  LevelGenerator.java  level difficulty + creation (3 to 12 colors)
  Solver.java          checks levels are solvable, powers hints
  AdsManager.java      AdMob + consent
```

Built with Android Gradle Plugin 8.10, compileSdk/targetSdk 36, minSdk 23, Java 17.
