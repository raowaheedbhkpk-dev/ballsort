# <paste android-build.yml contents here>
YMLEOF

# ---------------- app ----------------
# All Flutter files: paste each one
cat > app/pubspec.yaml <<'EOF'
# <paste pubspec.yaml>
EOF

# ... repeat for every file listed in the previous message ...
echo "Files created. Now paste the actual contents into each file."
echo "Then run: cd .. && zip -r rainax-portal.zip rainax-portal"
clear
cd
ls
rm -rf rainax-portal
rm -rf keystore.bs4
clear
ls
nano build.sh
bash build.sh
ls
cd rainax-portal
ls
cd app
ls
clear
ls
cd ..
clear
ls
rm -rf app.py kestote.b64 templates package-lock.json result.html
clear
ls
rm -rf keystore.b64
ear
keytool -genkey -v -keystore upload-keystore.jks   -keyalg RSA -keysize 2048 -validity 10000 -alias upload
base64 -w 0 upload-keystore.jks > keystore.b64
clear
pkg install openjdk-25
clear
#!/usr/bin/env bash
set -e
ROOT=rainax-portal
rm -rf "$ROOT" && mkdir -p "$ROOT"
cd "$ROOT"
mkdir -p .github/workflows server/templates
mkdir -p app/lib/{api,models,screens,widgets}
mkdir -p app/android/app/src/main/kotlin/com/rainax/portal
mkdir -p app/android/app/src/main/res/{values,values-night,drawable,drawable-v21}
mkdir -p app/android/gradle/wrapper
# ---------------- root ----------------
cat > README.md <<'EOF'
# Rainax Portal
Android app + Flask API for SIM tracking.
EOF

cat > .gitignore <<'EOF'
__pycache__/
*.pyc
.venv/
venv/
app/.dart_tool/
app/.flutter-plugins
app/.flutter-plugins-dependencies
app/build/
app/.packages
app/pubspec.lock
app/android/key.properties
app/android/app/upload-keystore.jks
app/android/.gradle/
app/android/local.properties
.idea/
.vscode/
*.iml
.DS_Store
Thumbs.db
EOF

cat > SETUP.md <<'EOF'
See chat instructions.
EOF

# ---------------- server ----------------
cat > server/requirements.txt <<'EOF'
flask==3.0.3
flask-cors==4.0.1
gunicorn==22.0.0
requests==2.32.3
beautifulsoup4==4.12.3
itsdangerous==2.2.0
cryptography==43.0.1
werkzeug==3.0.4
EOF

cat > server/Procfile <<'EOF'
web: gunicorn server:app --bind 0.0.0.0:$PORT --workers 2 --threads 4 --timeout 60
EOF

# server.py: paste the full server.py from the previous message here
cat > server/server.py <<'PYEOF'
# <paste server.py contents here>
PYEOF

# admin.html: paste from previous message
cat > server/templates/admin.html <<'HTMLEOF'
<!-- <paste admin.html contents here> -->
HTMLEOF

# ---------------- workflows ----------------
# android-build.yml: paste from previous message
cat > .github/workflows/android-build.yml <<'YMLEOF'
# <paste android-build.yml contents here>
YMLEOF

# ---------------- app ----------------
# All Flutter files: paste each one
cat > app/pubspec.yaml <<'EOF'
# <paste pubspec.yaml>
EOF

# ... repeat for every file listed in the previous message ...
echo "Files created. Now paste the actual contents into each file."
echo "Then run: cd .. && zip -r rainax-portal.zip rainax-portal"
clear
cd
ls
rm -rf rainax-portal
rm -rf keystore.bs4
clear
ls
nano build.sh
bash build.sh
ls
cd rainax-portal
ls
cd app
ls
clear
ls
cd ..
clear
ls
rm -rf app.py kestote.b64 templates package-lock.json result.html
clear
ls
rm -rf keystore.b64
ear
pkg update && pkg upgrade -y
pkg install python ffmpeg -y
pip install yt-dlp flask
pkg update && pkg upgrade -y
clear
ls
nano app.py
python app.py
exit
cd ~/storage/downloads/ytdl-app
sed -i 's/val host = Uri.parse(url).host ?: return null/val host = Uri.parse(url).host ?: throw IllegalStateException("no host")/' app/src/main/java/com/example/ytdl/CookieHelper.kt
git add -A
git commit -m "Fix return in expression-body function"
git push
gh run watch
clear
cd ~/storage/downloads/ytdl-app
gh run watch
git tag v5.0.0
git push origin v5.0.0
gh run watch
clear
cd
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.zip
rm -f app/src/main/java/com/example/ytdl/SiteAdapter.kt       app/src/main/java/com/example/ytdl/QualityAdapter.kt       app/src/main/res/layout/item_site.xml       app/src/main/res/layout/item_quality.xml       app/src/main/res/layout/page_downloads.xml
git add -A
git commit -m "v6: SnapTube-style UI, YouTube-only browser, format sheet"
git push
gh run watch
cd ~/storage/downloads/ytdl-app
gh run list --limit 3
git tag v6.0.0
git push origin v6.0.0
gh run watch
clear
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.1.zip
git add -A
git commit -m "v6.1: built-in Download button under player, hide Open app"
git push
gh run watch
git tag v6.1.0
git push origin v6.1.0
clear
cd
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.2.zip
git add -A
git commit -m "v6.2: fix in-page Download button, add fallback bar"
git push
gh run watch
git tag v6.2.0
git push origin v6.2.0
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.3.zip
git add -A
git commit -m "v6.3: fix YouTube/Music tab mix-up, hardening"
git push
gh run watch
git tag v6.3.0
git push origin v6.3.0
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.7.zip
git add -A
git commit -m "v6.7: multi-select, global actions, ad blocking"
git push
gh run watch
git tag v6.7.0
git push origin v6.7.0
gh run watch
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.8.zip
git add -A
git commit -m "v6.8: select + bulk actions for all downloads"
git push
gh run watch
git tag v6.8.0
git push origin v6.8.0
clear
exit
cd ~/storage/downloads/ytdl-app
unzip -o ~/storage/downloads/ytdl-app-v6.9.zip
git add -A
git commit -m "v6.9: use in-app YouTube sign-in, remove cookies.txt import"
git push
gh run watch
git tag v6.9.0
git push origin v6.9.0
gh run watch
clear
cd ~/storage/downloads/ytdl-app
git rm -rq . 
unzip -o ~/storage/downloads/rainax-yt-downloader-v7.0.0.zip -d ~/rainax-tmp
cp -r ~/rainax-tmp/rainax-yt-downloader/. .
rm -rf ~/rainax-tmp
git add -A
git commit -m "v7.0.0: RAINAX YT DOWNLOADER"
git push
git tag v7.0.0
git push origin v7.0.0
gh run watch
clear
cd ~/storage/downloads/ytdl-app
gh run list --limit 10
for id in $(gh run list --status queued --json databaseId,workflowName -q '.[] | select(.workflowName | test("Dependabot")) | .databaseId'); do gh run cancel $id; done
cd
clear
cd ~/storage/downloads
rm -rf rainax-yt-downloader
unzip -o rainax-yt-downloader-v7.0.0.zip
cd rainax-yt-downloader
git config --global --add safe.directory "$(pwd)"
git init -b main
git add -A
git commit -m "v7.0.0: RAINAX YT DOWNLOADER"
gh repo create rainax-yt-downloader --private --source=. --push
git tag v7.0.0
git push origin v7.0.0
gh run watch
clear
cd
clear
ls
gh auth logout
clear
gh auth login
clear
git config --global user.name "raowaheedbhkpk-dev"
git config --global user.email "rao.waheed.bhk.pk@gmail.com"
rm -f ~/.git-credentials
gh auth setup-git
gh auth status
cd ~/storage/downloads
rm -rf rainax-yt-downloader
unzip -o rainax-yt-downloader-v7.0.0.zip
cd rainax-yt-downloader
git config --global --add safe.directory "$(pwd)"
git init -b main
git add -A
git commit -m "v7.0.0: RAINAX YT DOWNLOADER"
gh repo create rainax-yt-downloader --private --source=. --push
git tag v7.0.0
git push origin v7.0.0
gh run watch
clear
git tag v7.0.0 && git push origin v7.0.0
cd ~/storage/downloads
rm -rf ytdl-app-v69
unzip -o ytdl-app-v6.9-FULL.zip
mv ytdl-app ytdl-app-v69
cd ytdl-app-v69
git config --global --add safe.directory "$(pwd)"
git init -b main
git add -A
git commit -m "v6.9: use in-app YouTube sign-in, remove cookies.txt import"
gh repo create ytdl-app --private --source=. --push
git tag v6.9.0
git push origin v6.9.0
gh run watch
clear
exit
cd ~/storage/downloads
rm -rf rainax-yt-downloader
unzip -o rainax-yt-downloader-v7.0.0.zip
cd rainax-yt-downloader
git config --global --add safe.directory "$(pwd)"
git init -b main
git add -A
git commit -m "v7.0.0: RAINAX YT DOWNLOADER"
gh repo create rainax-yt-downloader --private --source=. --push
git tag v7.0.0
git push origin v7.0.0
gh run watch
clear
cd ~/storage/downloads/rainax-yt-downloader
gh auth status
gh repo create rainax-yt-downloader --private
git remote remove origin 2>/dev/null
git remote add origin https://github.com/raowaheedbhkpk-dev/rainax-yt-downloader.git
git branch -M main
git push -u origin main
git tag v7.0.0
git push origin v7.0.0
gh run watch
cd ~/storage/downloads/rainax-yt-downloader
git push -u origin main --force
git tag -f v7.0.0
git push origin v7.0.0 --force
gh run watch
clear
exit
cd ~/storage/downloads/rainax-yt-downloader
git rm -rq .
unzip -o ~/storage/downloads/rainax-yt-downloader-v7.4.0.zip -d ~/rx-tmp
cp -r ~/rx-tmp/rainax-yt-downloader/. .
rm -rf ~/rx-tmp
git add -A
git commit -m "v7.4.0: native background audio with prefetch and seamless handoff"
git pull --rebase origin main
git push
git tag v7.4.0
git push origin v7.4.0
gh run watch
clear
bcd ~/storage/downloads/rainax-yt-downloader
git rm -rq .
unzip -o ~/storage/downloads/rainax-yt-downloader-v7.5.0.zip -d ~/rx-tmp
cp -r ~/rx-tmp/rainax-yt-downloader/. .
rm -rf ~/rx-tmp
git add -A
git commit -m "v7.5.0: remove background audio, faster downloads, stability fixes"
git pull --rebase origin main
git push
git tag v7.5.0
git push origin v7.5.0
gh run watch
cd ~/storage/downloads/rainax-yt-downloader
git rm -rq .
unzip -o ~/storage/downloads/rainax-yt-downloader-v7.5.1.zip -d ~/rx-tmp
cp -r ~/rx-tmp/rainax-yt-downloader/. .
rm -rf ~/rx-tmp
git add -A
git commit -m "v7.5.1: keep the open video after theme change"
git pull --rebase origin main
git push
git tag v7.5.1
git push origin v7.5.1
gh run watch
cd ~/storage/downloads/rainax-yt-downloader
git rm -rq .
unzip -o ~/storage/downloads/rainax-yt-downloader-v7.5.1.zip -d ~/rx-tmp
cp -r ~/rx-tmp/rainax-yt-downloader/. .
rm -rf ~/rx-tmp
git add -A
git commit -m "v7.5.1: keep the open video after theme change"
git pull --rebase origin main
git push
git tag v7.5.1
git push origin v7.5.1
gh run watch
git auth logout
gh auth logout
gh auth login
clear
cd ~/storage/downloads/redline-pdf-editor
git rm -rq .
unzip -o ~/storage/downloads/redline-pdf-editor-github.zip -d ~/rd-tmp
cp -r ~/rd-tmp/redline-pdf-editor/. .
rm -rf ~/rd-tmp
git add -A
git commit -m "v1.0.1: describe the change here"
git pull --rebase origin main
git push
git tag v1.0.1
git push origin v1.0.1
mkdir -p ~/storage/downloads/redline-pdf-editor
cd ~/storage/downloads/redline-pdf-editor
git init -b main
unzip -o ~/storage/downloads/redline-pdf-editor-github.zip -d ~/rd-tmp
cp -r ~/rd-tmp/redline-pdf-editor/. .
rm -rf ~/rd-tmp
git add -A
git commit -m "v1.0.0: Redline PDF Editor with font detection"
gh repo create redline-pdf-editor --public --source=. --push
git tag v1.0.0
git push origin v1.0.0
clear
cd ~/storage/downloads/redline-pdf-editor
git rm -rq .
unzip -o ~/storage/downloads/redline-pdf-editor-github.zip -d ~/rd-tmp
cp -r ~/rd-tmp/redline-pdf-editor/. .
rm -rf ~/rd-tmp
git add -A
git commit -m "v1.1.0: 1,900+ Google Fonts, computer fonts, font upload"
git pull --rebase origin main
git push
git tag v1.1.0
git push origin v1.1.0
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.6.0.zip
git add -A
git commit -m "v8.6.0: YouTube sign-in, mini player, skeleton loading"
git push
git tag v8.6.0
git push origin v8.6.0
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.7.0.zip
git add -A
git commit -m "v8.7.0: subscribe, like, watch later, channel pages"
git push
git tag v8.7.0
git push origin v8.7.0
gh runner watch
gh run watch
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.8.0.zip
git add -A
git commit -m "v8.8.0: social downloads, auto quality, full comments, description"
git push
git tag v8.8.0
git push origin v8.8.0
gh run watch
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.8.1.zip
git add -A
git commit -m "v8.8.1: popular videos for new accounts, empty list messages"
git push
git tag v8.8.1
git push origin v8.8.1
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.9.61.zip -d .
rm -f app/src/main/java/com/rainax/ytdownloader/SocialBrowser.kt app/src/main/res/layout/layout_browser.xml app/src/main/res/drawable/bg_browser_download.xml app/src/main/res/layout/item_platform.xml
git add -A
git commit -m "v8.9.61: Rate us on APKPure and Share app"
git push
git tag v8.9.61 && git push origin v8.9.61
gh run watch
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.9.61.zip -d .
rm -f app/src/main/java/com/rainax/ytdownloader/SocialBrowser.kt app/src/main/res/layout/layout_browser.xml app/src/main/res/drawable/bg_browser_download.xml app/src/main/res/layout/item_platform.xml
git add -A
git commit -m "v8.9.61: Rate us on APKPure and Share app"
git push
git tag v8.9.61 && git push origin v8.9.61
cd ~/rainax-yt-downloader
unzip -o ~/storage/downloads/rainax-yt-downloader-v8.9.61.zip -d .
rm -f app/src/main/java/com/rainax/ytdownloader/SocialBrowser.kt app/src/main/res/layout/layout_browser.xml app/src/main/res/drawable/bg_browser_download.xml app/src/main/res/layout/item_platform.xml
git add -A
git commit -m "v8.9.62: Rate us on APKPure and Share app"
git push
git tag v8.9.62 && git push origin v8.9.62
cd ~/rainax-yt-downloader
git rm -r -q .
unzip -o ~/storage/downloads/rainax-yt-downloader-v9.0.zip -d .
git add -A
git commit -m "v9.0"
git push
git tag v9.0 && git push origin v9.0
gh run watch
cd ~/rainax-yt-downloader
git rm -r -q .
unzip -o ~/storage/downloads/rainax-yt-downloader-v10.1.zip -d .
git add -A
git commit -m "v10.1"
git push
cd ~/rainax-yt-downloader
git rm -r -q .
unzip -o ~/storage/downloads/rainax-yt-downloader-v10.2.zip -d .
git add -A
git commit -m "v10.2"
git push
